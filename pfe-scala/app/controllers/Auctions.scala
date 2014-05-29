package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.AuctionRooms
import play.api.libs.iteratee.{Iteratee, Enumerator}

object Auctions extends Controller {

  val shop = models.Shop

  type Bid = (String, Double)
  implicit val writesNotification = Writes[Bid] {
    case (string: String, double: Double) => Json.obj("name" -> string, "price" -> double)
  }

  val bidValidator =
    ((__ \ "name").read(Reads.minLength[String](1)) and (__ \ "price").read[Double]).tupled

  def room(id: Long) = Action {
    shop.get(id) match {
      case Some(item) => Ok(views.html.auctionRoom(item))
      case None => NotFound
    }
  }

  def bid(id: Long) = Action(parse.json) { implicit request =>
    for ((name, bid) <- request.body.validate(bidValidator)) {
      AuctionRooms.bid(id, name, bid)
    }
    Ok
  }

  def notifications(id: Long) = Action.async {
    AuctionRooms.notifications(id).map { case (currentState, notifications) =>
      val allNotifications = Enumerator(currentState.to[Seq]: _*) andThen notifications
      Ok.chunked(allNotifications &> Json.toJson[Bid] &> EventSource()).as(EVENT_STREAM)
    }
  }

  def roomWs(id: Long) = Action {
    shop.get(id) match {
      case Some(item) => Ok(views.html.auctionRoomWs(item))
      case None => NotFound
    }
  }

  def channel(id: Long) = WebSocket.tryAccept[JsValue] { _ =>
    AuctionRooms.notifications(id).map { case (currentState, notifications) =>
      val bidsHandler = Iteratee.foreach[JsValue] { json =>
        for ((name, bid) <- json.validate(bidValidator)) {
          AuctionRooms.bid(id, name, bid)
        }
      }
      val allNotifications = (Enumerator(currentState.to[Seq]: _*) andThen notifications) &> Json.toJson[Bid]
      Right((bidsHandler, allNotifications))
    }
  }

}
