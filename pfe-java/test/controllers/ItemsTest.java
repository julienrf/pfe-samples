package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Item;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.FakeRequest;
import play.test.WithApplication;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static shop.ShopApplication.shopApplication;

public class ItemsTest extends WithApplication {

    @Override
    protected FakeApplication provideFakeApplication() {
        return shopApplication();
    }

    JsonNode itemCreate;

    {
        Items.CreateItem item = new Items.CreateItem();
        item.name = "Play Framework Essentials";
        item.price = 42.0;
        itemCreate = Json.toJson(item);
    }

    @Test
    public void createItem() {
        Result response = route(jsonRequest(routes.Items.create(), itemCreate));
        assertThat(status(response)).isEqualTo(OK);
        Item item = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
        assertThat(item.name).isEqualTo("Play Framework Essentials");
        assertThat(item.price).isEqualTo(42.0);
        assertThat(item.id).isNotNull();
    }

    @Test
    public void listItems() {
        Result response = route(jsonRequest(routes.Items.list()));
        assertThat(status(response)).isEqualTo(OK);
        assertThat(contentAsString(response)).isEqualTo("[]");
    }

    @Test
    public void getItem() {
        Result response = route(jsonRequest(routes.Items.create(), itemCreate));
        Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
        Result response2 = route(jsonRequest(routes.Items.details(createdItem.id)));
        assertThat(status(response)).isEqualTo(OK);
        Item item = Json.fromJson(Json.parse(contentAsString(response2)), Item.class);
        assertThat(item.name).isEqualTo("Play Framework Essentials");
        assertThat(item.price).isEqualTo(42.0);
    }

    @Test
    public void updateItem() {
        Result response = route(jsonRequest(routes.Items.create(), itemCreate));
        Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
        Items.CreateItem update = new Items.CreateItem();
        update.name = createdItem.name;
        update.price = 10.0;
        Result response2 = route(jsonRequest(routes.Items.update(createdItem.id), Json.toJson(update)));
        assertThat(status(response)).isEqualTo(OK);
        Item item = Json.fromJson(Json.parse(contentAsString(response2)), Item.class);
        assertThat(item.name).isEqualTo("Play Framework Essentials");
        assertThat(item.price).isEqualTo(10.0);
    }

    @Test
    public void deleteItem() {
        Result response = route(jsonRequest(routes.Items.create(), itemCreate));
        Item createdItem = Json.fromJson(Json.parse(contentAsString(response)), Item.class);
        Result response2 = route(jsonRequest(routes.Items.delete(createdItem.id)));
        assertThat(status(response2)).isEqualTo(OK);
        assertThat(status(route(jsonRequest(routes.Items.details(createdItem.id))))).isEqualTo(NOT_FOUND);
    }

    FakeRequest jsonRequest(Call call, JsonNode body) {
        return fakeRequest(call).withJsonBody(body).withHeader(ACCEPT, Http.MimeTypes.JSON);
    }

    FakeRequest jsonRequest(Call call) {
        return fakeRequest(call).withHeader(ACCEPT, Http.MimeTypes.JSON);
    }

}
