package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.WebSocket
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.Future

@Singleton class Auctions @Inject() (service: Service) extends Controller(service) {

  type Bid = (String, Double)
  implicit val writesNotification = Writes[Bid] {
    case (string: String, double: Double) => Json.obj("name" -> string, "price" -> double)
  }

  val bidValidator = (__ \ "price").read[Double]

  def room(id: Long) = AuthenticatedAction { implicit request =>
    service.shop.get(id) match {
      case Some(item) => Ok(views.html.auctionRoom(item))
      case None => NotFound
    }
  }

  def bid(id: Long) = AuthenticatedAction(parse.json) { implicit request =>
      for (bid <- request.body.validate(bidValidator)) {
        service.auctionRooms.bid(id, request.username, bid)
      }
      Ok
  }

  def notifications(id: Long) = AuthenticatedAction.async { implicit request =>
    service.auctionRooms.notifications(id).map { case (currentState, notifications) =>
      val allNotifications = Enumerator(currentState.to[Seq]: _*) andThen notifications
      Ok.chunked(allNotifications &> Json.toJson[Bid] &> EventSource()).as(EVENT_STREAM)
    }
  }

  def roomWs(id: Long) = AuthenticatedAction { implicit request =>
    service.shop.get(id) match {
      case Some(item) => Ok(views.html.auctionRoomWs(item))
      case None => NotFound
    }
  }

  def channel(id: Long) = WebSocket.tryAccept[JsValue] { implicit request =>
    Authentication.authenticated(
     name => {
       service.auctionRooms.notifications(id).map { case (currentState, notifications) =>
         val bidsHandler = Iteratee.foreach[JsValue] { json =>
           for (bid <- json.validate(bidValidator)) {
             service.auctionRooms.bid(id, name, bid)
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
