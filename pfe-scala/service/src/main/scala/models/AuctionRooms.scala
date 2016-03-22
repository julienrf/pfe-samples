package models

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

class AuctionRoomsActor extends Actor {
  import AuctionRoomsActor._

  implicit val materializer: Materializer = ActorMaterializer()
  var rooms = Map.empty[Long, Room]

  class Room {
    var bids = Map.empty[String, Double]
    val (ref, notifications) =
      Source.actorRef(1000, OverflowStrategy.dropNew)
        .toMat(Sink.asPublisher[(String, Double)](fanout = true)) { case (ref, publisher) => (ref, Source.fromPublisher(publisher)) }
        .run()

    def stateAndNotifications() = (bids, notifications)

    def addBid(name: String, price: Double): Unit = {
      if (bids.forall { case (_, p) => p < price}) {
        bids += name -> price
        ref ! (name -> price)
      }
    }

  }

  def lookupOrCreate(id: Long): Room = rooms.getOrElse(id, {
    val room = new Room
    rooms += id -> room
    room
  })

  def receive = {
    case Notifications(id) =>
      sender() ! lookupOrCreate(id).stateAndNotifications()
    case ItemBid(id, name, price) =>
      lookupOrCreate(id).addBid(name, price)
  }

}

object AuctionRoomsActor {
  case class Notifications(id: Long)
  case class ItemBid(id: Long, name: String, price: Double)
}

class AuctionRooms(actorSystem: ActorSystem) {

  import AuctionRoomsActor._
  import akka.pattern.ask

  import concurrent.duration.DurationInt

  implicit val timeout: akka.util.Timeout = 1.second

  private lazy val ref = actorSystem.actorOf(Props(new AuctionRoomsActor))

  /**
   * Ask for the notifications stream of an auction room
   * @param id Item id
   */
  def notifications(id: Long): Future[(Map[String, Double], Source[(String, Double), _])] =
    (ref ? Notifications(id)).mapTo[(Map[String, Double], Source[(String, Double), _])]

  /**
   * Bid for an item
   * @param id Item id
   * @param name User name
   * @param price Bid price
   */
  def bid(id: Long, name: String, price: Double): Unit =
    ref ! ItemBid(id, name, price)

}