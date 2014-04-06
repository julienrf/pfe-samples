package controllers

import play.api.test.{PlaySpecification, FakeRequest}
import play.api.libs.json.Json

class ItemsSpec extends PlaySpecification {

  "Items controller" should {
    "list items" in {
      val response = call(Items.list, FakeRequest())
      status(response) must equalTo (OK)
      contentAsJson(response) must equalTo (Json.arr())
    }
  }

}
