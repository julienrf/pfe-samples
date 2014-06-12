package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.AuctionRooms
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.Future

object Auctions extends Controller {

  val shop = models.Shop

  type Bid = (String, Double)
  implicit val writesNotification = Writes[Bid] {
    case (string: String, double: Double) => Json.obj("name" -> string, "price" -> double)
  }

  val bidValidator = (__ \ "price").read[Double]

  def room(id: Long) = Action { implicit request =>
    request.session.get(Authentication.UserKey) match {
      case Some(username) =>
        shop.get(id) match {
          case Some(item) => Ok(views.html.auctionRoom(item))
          case None => NotFound
        }
      case None =>
        Redirect(routes.Authentication.login(request.uri))
    }
  }

  def bid(id: Long) = Action(parse.json) { implicit request =>
    Authentication.authenticatedAction { username =>
      for (bid <- request.body.validate(bidValidator)) {
        AuctionRooms.bid(id, username, bid)
      }
      Ok
    }
  }

  def notifications(id: Long) = Action.async { implicit request =>
    Authentication.authenticated(
      _ => AuctionRooms.notifications(id).map { case (currentState, notifications) =>
        val allNotifications = Enumerator(currentState.to[Seq]: _*) andThen notifications
        Ok.chunked(allNotifications &> Json.toJson[Bid] &> EventSource()).as(EVENT_STREAM)
      },
      Future.successful(Redirect(routes.Authentication.login(request.uri)))
    )
  }

  def roomWs(id: Long) = Action { implicit request =>
    Authentication.authenticatedAction { username =>
      shop.get(id) match {
        case Some(item) => Ok(views.html.auctionRoomWs(item))
        case None => NotFound
      }
    }
  }

  def channel(id: Long) = WebSocket.tryAccept[JsValue] { implicit request =>
    Authentication.authenticated(
     name => {
       AuctionRooms.notifications(id).map { case (currentState, notifications) =>
         val bidsHandler = Iteratee.foreach[JsValue] { json =>
           for (bid <- json.validate(bidValidator)) {
             AuctionRooms.bid(id, name, bid)
           }
         }
         val allNotifications = (Enumerator(currentState.to[Seq]: _*) andThen notifications) &> Json.toJson[Bid]
         Right((bidsHandler, allNotifications))
       }
     },
      Future.successful(Left(Forbidden))
    )
  }

}
