package controllers

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Flow, Source}
import models.{AuctionRooms, Shop}
import play.api.mvc.{Controller, WebSocket}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.EventSource
import play.api.libs.json._
import scala.concurrent.Future

class Auctions(shop: Shop, auctionRooms: AuctionRooms) extends Controller {

  type Bid = (String, Double)
  implicit val writesNotification = Writes[Bid] {
    case (string: String, double: Double) => Json.obj("name" -> string, "price" -> double)
  }

  def jsonFlow[A : Writes]: Flow[A, JsValue, NotUsed] =
    Flow[A].map(a => Json.toJson(a))

  val bidValidator = (__ \ "price").read[Double]

  def room(id: Long) = AuthenticatedAction.async { implicit request =>
    shop.get(id) map {
      case Some(item) => Ok(views.html.auctionRoom(item))
      case None => NotFound
    }
  }

  def bid(id: Long) = AuthenticatedAction(parse.json(bidValidator)) { implicit request =>
      auctionRooms.bid(id, request.username, request.body)
      Ok
  }

  def notifications(id: Long) = AuthenticatedAction.async { implicit request =>
    auctionRooms.notifications(id).map { case (currentState, notifications) =>
      val allNotifications = Source(currentState.to[collection.immutable.Seq]).concat(notifications)
      Ok.chunked(allNotifications.via(jsonFlow).via(EventSource.flow))
    }
  }

  def roomWs(id: Long) = AuthenticatedAction.async { implicit request =>
    shop.get(id) map {
      case Some(item) => Ok(views.html.auctionRoomWs(item))
      case None => NotFound
    }
  }

  def channel(id: Long) = WebSocket.acceptOrResult[JsValue, JsValue] { implicit request =>
    Authentication.authenticated(name => {
       auctionRooms.notifications(id).map { case (currentState, notifications) =>
         val bidsHandler = Sink.foreach[JsValue] { json =>
           for (bid <- json.validate(bidValidator)) {
             auctionRooms.bid(id, name, bid)
           }
         }
         val allNotifications = Source(currentState.to[collection.immutable.Seq]).concat(notifications).via(jsonFlow)
         Right(Flow.fromSinkAndSource(bidsHandler, allNotifications))
       }
     },
      Future.successful(Left(Forbidden))
    )
  }

}
