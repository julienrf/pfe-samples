package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.Item

case class CreateItem(name: String, price: Double)

object CreateItem {
  import play.api.libs.functional.syntax._
  implicit val readsCreateItem = (
    (__ \ "name").read(Reads.minLength[String](1)) and
    (__ \ "price").read(Reads.min[Double](0))
  )(CreateItem.apply _)
}

object Items extends Controller {

  val shop = models.Shop

  implicit val writesItem = Json.writes[Item]

  val list = Action {
    Ok(Json.toJson(shop.list))
  }

  val create = Action(parse.json) { implicit request =>
      request.body.validate[CreateItem] match {
        case JsSuccess(createItem, _) =>
          shop.create(createItem.name, createItem.price) match {
            case Some(item) => Ok(Json.toJson(item))
            case None => InternalServerError
          }
        case JsError(errors) =>
          BadRequest
      }
  }

  def details(id: Long) = Action {
    shop.get(id) match {
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound
    }
  }

  def update(id: Long) = Action(parse.json) { implicit request =>
    request.body.validate[CreateItem] match {
      case JsSuccess(updateItem, _) =>
        shop.update(id, updateItem.name, updateItem.price) match {
          case Some(item) => Ok(Json.toJson(item))
          case None => InternalServerError
        }
      case JsError(errors) =>
        BadRequest
    }
  }

  def delete(id: Long) = Action {
    if (shop.delete(id)) Ok else BadRequest
  }

}
