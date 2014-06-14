package controllers

import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import models.Item
import play.api.data.Form
import play.api.data.Forms.{mapping, text, of}
import play.api.data.format.Formats.doubleFormat
import play.api.data.validation.Constraints
import play.api.libs.concurrent.Akka
import scala.concurrent.Future

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

  implicit val jdbcEC = Akka.system(play.api.Play.current).dispatchers.lookup("jdbc-execution-context")

  val list = Action.async { implicit request =>
    Future {
      val items = shop.list()
      render {
        case Accepts.Html() => Ok(views.html.list(items))
        case Accepts.Json() => Ok(Json.toJson(items))
      }
    }
  }

  val create = Action.async { implicit request =>
    Future {
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
  }

  val createForm = Action { implicit request =>
    Ok(views.html.createForm(CreateItem.form))
  }

  def details(id: Long) = Action.async { implicit request =>
    Future {
      shop.get(id) match {
        case Some(item) => render {
          case Accepts.Html() => Ok(views.html.details(item))
          case Accepts.Json() => Ok(Json.toJson(item))
        }
        case None => NotFound
      }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    Future {
      CreateItem.form.bindFromRequest().fold(
        formWithErrors => BadRequest(formWithErrors.errorsAsJson),
        updateItem => shop.update(id, updateItem.name, updateItem.price) match {
          case Some(item) => Ok(Json.toJson(item))
          case None => InternalServerError
        }
      )
    }
  }

  def delete(id: Long) = Action.async {
    Future {
      if (shop.delete(id)) Ok else BadRequest
    }
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
