package controllers

import play.api.test.{PlaySpecification, FakeRequest}
import play.api.libs.json.{JsValue, Json}
import shop.WithShopApplication
import scala.concurrent.Future
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import org.specs2.execute.AsResult
import play.api.http.{MimeTypes, Writeable}

class ItemsSpec extends PlaySpecification {

  "Items controller" should {

    val itemCreate = Json.obj("name" -> "Play Framework Essentials", "price" -> 42)
    val createdItem = Json.obj("id" -> 1, "name" -> "Play Framework Essentials", "price" -> 42)

    "add an item" in new WithShopApplication {
      successfullyRoute(jsonRequest(routes.Items.create(), itemCreate)) { response =>
        status(response) must equalTo(OK)
        contentAsJson(response) must equalTo(createdItem)
      }
    }

    "list items" in new WithShopApplication {
      successfullyRoute(jsonRequest(routes.Items.list())) { response =>
        status(response) must equalTo (OK)
        contentAsJson(response) must equalTo (Json.arr())
        successfullyRoute(jsonRequest(routes.Items.create(), itemCreate)) { created =>
          await(created)
          successfullyRoute(jsonRequest(routes.Items.list())) { response2 =>
            status(response2) must equalTo (OK)
            contentAsJson(response2) must equalTo (Json.arr(createdItem))
          }
        }
      }
    }

    "get an item" in new WithShopApplication {
      successfullyRoute(jsonRequest(routes.Items.create(), itemCreate)) { createdResponse =>
        val item = contentAsJson(createdResponse)
        successfullyRoute(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { response =>
          status(response) must equalTo (OK)
          contentAsJson(response) must equalTo (createdItem)
        }
      }
    }

    "update an item" in new WithShopApplication {
      successfullyRoute(jsonRequest(routes.Items.create(), itemCreate)) { createdResponse =>
        val item = contentAsJson(createdResponse)
        successfullyRoute(jsonRequest(routes.Items.update((item \ "id").as[Long]), Json.obj("name" -> "Play Framework Essentials", "price" -> 10))) { updatedResponse =>
          val updatedItem = contentAsJson(updatedResponse)
          successfullyRoute(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { detailsResponse =>
            val itemDetails = contentAsJson(detailsResponse)
            itemDetails must equalTo (updatedItem)
            itemDetails must equalTo (Json.obj("id" -> 1, "name" -> "Play Framework Essentials", "price" -> 10))
          }
        }
      }
    }

    "delete an item" in new WithShopApplication {
      successfullyRoute(jsonRequest(routes.Items.create(), itemCreate)) { createResponse =>
        val item = contentAsJson(createResponse)
        successfullyRoute(jsonRequest(routes.Items.delete((item \ "id").as[Long]))) { deleteResponse =>
          status(deleteResponse) must equalTo (OK)
          successfullyRoute(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { detailsResponse =>
            status(detailsResponse) must equalTo (NOT_FOUND)
          }
        }
      }
    }

  }

  def successfullyRoute[A : Writeable, B : AsResult](request: FakeRequest[A])(f: Future[Result] => B) =
    route(request) must beSome(f)

  def jsonRequest(call: Call, body: JsValue): FakeRequest[JsValue] =
    FakeRequest(call).withBody(body).withHeaders(ACCEPT -> MimeTypes.JSON)

  def jsonRequest(call: Call): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(call).withHeaders(ACCEPT -> MimeTypes.JSON)

}
