package controllers

import models.{AuctionRooms, Item, Shop}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test._

class AuctionsSpec extends PlaySpecification with Mockito {

  class WithMockedServices extends Scope {
    val shop = mock[Shop]
    val auctionRooms = mock[AuctionRooms]
    val dbAction = new DBAction(akka.actor.ActorSystem())
    val auctions = new Auctions(shop, auctionRooms, dbAction)
  }

  "Auctions controller" should {

    val request = FakeRequest(routes.Auctions.room(1))

    "redirect unauthenticated users to a login page" in new WithMockedServices {
      status(call(auctions.room(1), request)) must equalTo (SEE_OTHER)
    }

    "show auction rooms for authenticated users" in new WithMockedServices {
      shop.get(1) returns Some(Item(1, "Play Framework Essentials", 42))
      status(call(auctions.room(1), request.withSession(Authentication.UserKey -> "Alice"))) must equalTo (OK)
    }

  }

}
