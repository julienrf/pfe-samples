package models

import akka.actor.{Props, Actor}
import play.api.libs.iteratee.{Enumerator, Concurrent}
import play.api.libs.concurrent.Akka
import scala.concurrent.Future

class AuctionRooms extends Actor {
  import AuctionRooms._

  class Room {
    var bids = Map.empty[String, Double]
    val (enumerator, channel) = Concurrent.broadcast[(String, Double)]

    def stateAndNotifications() = (bids, enumerator)

    def addBid(name: String, price: Double): Unit = {
      if (bids.forall { case (_, p) => p < price}) {
        bids += name -> price
        channel.push(name -> price)
      }
    }

  }

  var rooms = Map.empty[Long, Room]

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

object AuctionRooms {

  import play.api.Play.current
  import akka.pattern.ask
  import scala.concurrent.duration.DurationInt
  implicit val timeout: akka.util.Timeout = 1.second

  private lazy val ref = Akka.system.actorOf(Props[AuctionRooms])

  /**
   * Ask for the notifications stream of an auction room
   * @param id Item id
   */
  def notifications(id: Long): Future[(Map[String, Double], Enumerator[(String, Double)])] = {
    (ref ? Notifications(id)).mapTo[(Map[String, Double], Enumerator[(String, Double)])]
  }

  /**
   * Bid for an item
   * @param id Item id
   * @param name User name
   * @param price Bid price
   */
  def bid(id: Long, name: String, price: Double): Unit = {
    ref ! ItemBid(id, name, price)
  }

  case class Notifications(id: Long)
  case class ItemBid(id: Long, name: String, price: Double)

}