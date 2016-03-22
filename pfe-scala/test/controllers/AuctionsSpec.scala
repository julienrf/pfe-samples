package controllers

import models.{AuctionRooms, Item, Shop}
import org.specs2.mock.Mockito
import play.api.ApplicationLoader.Context
import play.api.inject.{NewInstanceInjector, SimpleInjector}
import play.api.libs.Crypto
import play.api.libs.crypto.AESCTRCrypter
import play.api.routing.Router
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}

import scala.concurrent.Future

class AuctionsSpec extends PlaySpecification with Mockito {

  // Here we need an application just because our controllers are (implicitly) using Play’s Crypto. But note that this application does not contain our controllers or services
  class WithMockedServices extends WithApplication(
    app =
      new ApplicationLoader {
        def load(context: Context) =
          new BuiltInComponentsFromContext(context) { val router = Router.empty }.application
      }.load(shop.fakeContext)
  ) {
    lazy val shop = mock[Shop]
    lazy val auctionRooms = mock[AuctionRooms]
    lazy val auctions = new Auctions(shop, auctionRooms)
  }

  "Auctions controller" should {

    val request = FakeRequest(routes.Auctions.room(1))

    "redirect unauthenticated users to a login page" in new WithMockedServices {
      status(call(auctions.room(1), request)) must equalTo (SEE_OTHER)
    }

    "show auction rooms for authenticated users" in new WithMockedServices {
      shop.get(1) returns Future.successful(Some(Item(1, "Play Framework Essentials", 42)))
      status(call(auctions.room(1), request.withSession(Authentication.UserKey -> "Alice"))) must equalTo (OK)
    }

  }

}
