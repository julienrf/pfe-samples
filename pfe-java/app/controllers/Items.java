package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Item;
import models.Shop;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class Items extends Controller {

    static class CreateItem {
        public String name;
        public Double price;
    }

    static Shop shop = models.Shop.Shop;

    public static Result list() {
        return ok(Json.toJson(shop.list()));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        JsonNode json = request().body().asJson();
        CreateItem createItem;
        try {
            createItem = Json.fromJson(json, CreateItem.class);
        } catch (RuntimeException e) {
            return badRequest();
        }
        Item item = shop.create(createItem.name, createItem.price);
        if (item != null) {
            return ok(Json.toJson(item));
        } else {
            return internalServerError();
        }
    }

    public static Result details(Long id) {
        Item item = shop.get(id);
        if (item != null) {
            return ok(Json.toJson(item));
        } else {
            return notFound();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        JsonNode json = request().body().asJson();
        CreateItem updateItem;
        try {
            updateItem = Json.fromJson(json, CreateItem.class);
        } catch (RuntimeException e) {
            return badRequest();
        }
        Item updated = shop.update(id, updateItem.name, updateItem.price);
        if (updated != null) {
            return ok(Json.toJson(updated));
        } else {
            return internalServerError();
        }
    }

    public static Result delete(Long id) {
        if (shop.delete(id)) {
            return ok();
        } else {
            return badRequest();
        }
    }

}
