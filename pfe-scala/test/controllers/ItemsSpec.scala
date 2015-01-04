package controllers

import play.api.test.{PlaySpecification, FakeRequest}
import play.api.libs.json.{JsValue, Json}
import loaders.WithShopApplication
import scala.concurrent.Future
import play.api.mvc.{EssentialAction, AnyContentAsEmpty, Call, Result}
import play.api.http.{MimeTypes, Writeable}

class ItemsSpec extends PlaySpecification {

  "Items controller" should {

    val itemCreate = Json.obj("name" -> "Play Framework Essentials", "price" -> 42)
    val createdItem = Json.obj("id" -> 1, "name" -> "Play Framework Essentials", "price" -> 42)

    "add an item" in new WithShopApplication {
      route(jsonRequest(routes.Items.create(), itemCreate)) { response =>
        status(response) must equalTo(OK)
        contentAsJson(response) must equalTo(createdItem)
      }
    }

    "list items" in new WithShopApplication {
      route(jsonRequest(routes.Items.list())) { response =>
        status(response) must equalTo (OK)
        contentAsJson(response) must equalTo (Json.arr())
        route(jsonRequest(routes.Items.create(), itemCreate)) { created =>
          await(created)
          route(jsonRequest(routes.Items.list())) { response2 =>
            status(response2) must equalTo (OK)
            contentAsJson(response2) must equalTo (Json.arr(createdItem))
          }
        }
      }
    }

    "get an item" in new WithShopApplication {
      route(jsonRequest(routes.Items.create(), itemCreate)) { createdResponse =>
        val item = contentAsJson(createdResponse)
        route(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { response =>
          status(response) must equalTo (OK)
          contentAsJson(response) must equalTo (createdItem)
        }
      }
    }

    "update an item" in new WithShopApplication {
      route(jsonRequest(routes.Items.create(), itemCreate)) { createdResponse =>
        val item = contentAsJson(createdResponse)
        route(jsonRequest(routes.Items.update((item \ "id").as[Long]), Json.obj("name" -> "Play Framework Essentials", "price" -> 10))) { updatedResponse =>
          val updatedItem = contentAsJson(updatedResponse)
          route(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { detailsResponse =>
            val itemDetails = contentAsJson(detailsResponse)
            itemDetails must equalTo (updatedItem)
            itemDetails must equalTo (Json.obj("id" -> 1, "name" -> "Play Framework Essentials", "price" -> 10))
          }
        }
      }
    }

    "delete an item" in new WithShopApplication {
      route(jsonRequest(routes.Items.create(), itemCreate)) { createResponse =>
        val item = contentAsJson(createResponse)
        route(jsonRequest(routes.Items.delete((item \ "id").as[Long]))) { deleteResponse =>
          status(deleteResponse) must equalTo (OK)
          route(jsonRequest(routes.Items.details((item \ "id").as[Long]))) { detailsResponse =>
            status(detailsResponse) must equalTo (NOT_FOUND)
          }
        }
      }
    }

  }

  def route[A : Writeable, B](request: FakeRequest[A])(f: Future[Result] => B)(implicit application: play.api.Application) = {
    val (taggedHeader, handler) = application.requestHandler.handlerForRequest(request)
    handler match {
      case a: EssentialAction =>
        f(call(a, request, request.body))
    }
  }

  def jsonRequest(call: Call, body: JsValue): FakeRequest[JsValue] =
    FakeRequest(call).withBody(body).withHeaders(ACCEPT -> MimeTypes.JSON)

  def jsonRequest(call: Call): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(call).withHeaders(ACCEPT -> MimeTypes.JSON)

}
