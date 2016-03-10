package controllers

import controllers.oauth.OAuth
import play.api.Logger
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Controller, Action}
import play.api.libs.json._
import models.{SocialNetwork, Shop, Item}
import play.api.data.Form
import play.api.data.Forms.{mapping, text, of}
import play.api.data.format.Formats.doubleFormat
import play.api.data.validation.Constraints
import play.filters.csrf.CSRFAddToken

import scala.concurrent.Future

case class CreateItem(name: String, price: Double)

object CreateItem {
  val form = Form(mapping(
    "name" -> text.verifying(Constraints.nonEmpty),
    "price" -> of[Double].verifying(Constraints.min(0.0, strict = true))
  )(CreateItem.apply)(CreateItem.unapply))
}

class Items(shop: Shop, socialNetwork: SocialNetwork, oauth: OAuth, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  import Items._

  val list = Action.async { implicit request =>
    shop.list().map { items =>
      render {
        case Accepts.Html() => Ok(views.html.list(items))
        case Accepts.Json() => Ok(Json.toJson(items))
      }
    }
  }

  val create = Action.async { implicit request =>
    CreateItem.form.bindFromRequest().fold(
      formWithErrors => Future.successful(
        render {
          case Accepts.Html() => BadRequest(views.html.createForm(formWithErrors))
          case Accepts.Json() => BadRequest(formWithErrors.errorsAsJson)
        }
      ),
      createItem => {
        shop.create(createItem.name, createItem.price).map { item =>
          render {
            case Accepts.Html() => Redirect(routes.Items.details(item.id))
            case Accepts.Json() => Ok(Json.toJson(item))
          }
        }
      }
    )
  }

  val createForm = Action { implicit request =>
    Ok(views.html.createForm(CreateItem.form))
  }

  def details(id: Long) = Action.async { implicit request =>
    shop.get(id).map {
      case Some(item) => render {
        case Accepts.Html() => Ok(views.html.details(item))
        case Accepts.Json() => Ok(Json.toJson(item))
      }
      case None => NotFound
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    CreateItem.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      updateItem => shop.update(id, updateItem.name, updateItem.price).map {
        case Some(item) => Ok(Json.toJson(item))
        case None => InternalServerError
      }
    )
  }

  def delete(id: Long) = Action.async {
    shop.delete(id).map(deleted => if (deleted) Ok else BadRequest)
  }

  def share(id: Long) = Action { implicit request =>
    oauth.authenticated( token => {
      socialNetwork.share(routes.Items.details(id).absoluteURL(), token).foreach { response =>
        Logger.info(s"Sharing request successfully sent for item #$id. Got response status ${response.statusText}")
      }
      Ok
    }, Redirect(oauth.authorizeUrl(routes.Items.details(id))))
  }

}

object Items {

  implicit val writesItem: Writes[Item] = Json.writes[Item]

}