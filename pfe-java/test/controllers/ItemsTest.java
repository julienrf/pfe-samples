package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Item;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ItemsTest {

    JsonNode itemCreate;

    {
        Items.CreateItem item = new Items.CreateItem();
        item.name = "Play! Framework Essentials";
        item.price = 42.0;
        itemCreate = Json.toJson(item);
    }

    @Test
    public void createItem() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            Result response = callAction(routes.ref.Items.create(), fakeRequest().withJsonBody(itemCreate));
            assertThat(status(response)).isEqualTo(OK);
            Item item = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
            assertThat(item.name).isEqualTo("Play! Framework Essentials");
            assertThat(item.price).isEqualTo(42.0);
            assertThat(item.id).isNotNull();
        });
    }

    @Test
    public void listItems() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            Result response = callAction(routes.ref.Items.list());
            assertThat(status(response)).isEqualTo(OK);
            assertThat(contentAsString(response)).isEqualTo("[]");
        });
    }

    @Test
    public void getItem() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            Result response = callAction(routes.ref.Items.create(), fakeRequest().withJsonBody(itemCreate));
            Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
            Result response2 = callAction(controllers.routes.ref.Items.details(createdItem.id));
            assertThat(status(response)).isEqualTo(OK);
            Item item = Json.fromJson(Json.parse(contentAsString(response2)), Item.class);
            assertThat(item.name).isEqualTo("Play! Framework Essentials");
            assertThat(item.price).isEqualTo(42.0);
        });
    }

    @Test
    public void updateItem() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            Result response = callAction(routes.ref.Items.create(), fakeRequest().withJsonBody(itemCreate));
            Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
            Items.CreateItem update = new Items.CreateItem();
            update.name = createdItem.name;
            update.price = 10.0;
            Result response2 = callAction(controllers.routes.ref.Items.update(createdItem.id), fakeRequest().withJsonBody(Json.toJson(update)));
            assertThat(status(response)).isEqualTo(OK);
            Item item = Json.fromJson(Json.parse(contentAsString(response2)), Item.class);
            assertThat(item.name).isEqualTo("Play! Framework Essentials");
            assertThat(item.price).isEqualTo(10.0);
        });
    }

    @Test
    public void deleteItem() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            Result response = callAction(routes.ref.Items.create(), fakeRequest().withJsonBody(itemCreate));
            Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
            Result response2 = callAction(controllers.routes.ref.Items.delete(createdItem.id));
            assertThat(status(response2)).isEqualTo(OK);
            assertThat(status(callAction(routes.ref.Items.details(createdItem.id)))).isEqualTo(NOT_FOUND);
        });
    }
}
