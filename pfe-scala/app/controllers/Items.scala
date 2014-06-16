package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.Item
import play.api.data.Form
import play.api.data.Forms.{mapping, text, of}
import play.api.data.format.Formats.doubleFormat
import play.api.data.validation.Constraints

case class CreateItem(name: String, price: Double)

object CreateItem {
  val form = Form(mapping(
    "name" -> text.verifying(Constraints.nonEmpty),
    "price" -> of[Double].verifying(Constraints.min(0.0, strict = true))
  )(CreateItem.apply)(CreateItem.unapply))
}

object Items extends Controller {

  val shop = models.Shop

  val socialNetwork = models.SocialNetwork

  implicit val writesItem = Json.writes[Item]

  val list = DBAction { implicit request =>
    val items = shop.list()
    render {
      case Accepts.Html() => Ok(views.html.list(items))
      case Accepts.Json() => Ok(Json.toJson(items))
    }
  }

  val create = DBAction { implicit request =>
    CreateItem.form.bindFromRequest().fold(
      formWithErrors => render {
        case Accepts.Html() => BadRequest(views.html.createForm(formWithErrors))
        case Accepts.Json() => BadRequest(formWithErrors.errorsAsJson)
      },
      createItem => {
        shop.create(createItem.name, createItem.price) match {
          case Some(item) => render {
            case Accepts.Html() => Redirect(routes.Items.details(item.id))
            case Accepts.Json() => Ok(Json.toJson(item))
          }
          case None => InternalServerError
        }
      }
    )
  }

  val createForm = Action { implicit request =>
    Ok(views.html.createForm(CreateItem.form))
  }

  def details(id: Long) = DBAction { implicit request =>
    shop.get(id) match {
      case Some(item) => render {
        case Accepts.Html() => Ok(views.html.details(item))
        case Accepts.Json() => Ok(Json.toJson(item))
      }
      case None => NotFound
    }
  }

  def update(id: Long) = DBAction { implicit request =>
    CreateItem.form.bindFromRequest().fold(
      formWithErrors => BadRequest(formWithErrors.errorsAsJson),
      updateItem => shop.update(id, updateItem.name, updateItem.price) match {
        case Some(item) => Ok(Json.toJson(item))
        case None => InternalServerError
      }
    )
  }

  def delete(id: Long) = DBAction {
    if (shop.delete(id)) Ok else BadRequest
  }

  def share(id: Long) = Action { implicit request =>
    request.session.get(OAuth.tokenKey) match {
      case Some(token) =>
        socialNetwork.share(routes.Items.details(id).absoluteURL(), token)
        Ok
      case None =>
        Redirect(OAuth.authorizeUrl(routes.Items.details(id)))
    }
  }

}
