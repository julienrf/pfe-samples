package controllers

import play.api.test.{PlaySpecification, FakeRequest}
import play.api.libs.json.Json
import shop.ShopApplication

class ItemsSpec extends PlaySpecification {

  "Items controller" should {

    val itemCreate = Json.obj("name" -> "Play! Framework Essentials", "price" -> 42)
    val createdItem = Json.obj("id" -> 1, "name" -> "Play! Framework Essentials", "price" -> 42)

    "add an item" in new ShopApplication {
      val response = call(Items.create, FakeRequest().withJsonBody(itemCreate))
      status(response) must equalTo (OK)
      contentAsJson(response) must equalTo (createdItem)
    }

    "list items" in new ShopApplication {
      val response = call(Items.list, FakeRequest())
      status(response) must equalTo (OK)
      contentAsJson(response) must equalTo (Json.arr())
      await(call(Items.create, FakeRequest().withJsonBody(itemCreate)))
      val response2 = call(Items.list, FakeRequest())
      status(response2) must equalTo (OK)
      contentAsJson(response2) must equalTo (Json.arr(createdItem))
    }

    "get an item" in new ShopApplication {
      val item = contentAsJson(call(Items.create, FakeRequest().withJsonBody(itemCreate)))
      val response = call(Items.details((item \ "id").as[Long]), FakeRequest())
      status(response) must equalTo (OK)
      contentAsJson(response) must equalTo (createdItem)
    }

    "update an item" in new ShopApplication {
      val item = contentAsJson(call(Items.create, FakeRequest().withJsonBody(itemCreate)))
      val updatedItem = contentAsJson(call(Items.update((item \ "id").as[Long]), FakeRequest().withJsonBody(Json.obj("name" -> "Play! Framework Essentials", "price" -> 10))))
      val itemDetails = contentAsJson(call(Items.details((item \ "id").as[Long]), FakeRequest()))
      itemDetails must equalTo (updatedItem)
      itemDetails must equalTo (Json.obj("id" -> 1, "name" -> "Play! Framework Essentials", "price" -> 10))
    }

    "delete an item" in new ShopApplication {
      val item = contentAsJson(call(Items.create, FakeRequest().withJsonBody(itemCreate)))
      status(call(Items.delete((item \ "id").as[Long]), FakeRequest())) must equalTo (OK)
      status(call(Items.details((item \ "id").as[Long]), FakeRequest())) must equalTo (NOT_FOUND)
    }

  }

}
