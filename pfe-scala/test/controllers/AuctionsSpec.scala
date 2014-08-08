package controllers

import com.google.inject.{AbstractModule, Module}
import models.{Item, Shop}
import org.specs2.mock.Mockito
import play.api.test.{FakeRequest, FakeApplication, WithApplication, PlaySpecification}
import shop.GuiceInjector

class AuctionsSpec extends PlaySpecification with Mockito {

  class WithMockedShopApplication extends { val service = mock[Service] } with WithApplication(FakeApplication(withGlobal = Some(new GuiceInjector {
    override val additionalModules: Seq[Module] = Seq(new AbstractModule {
      def configure(): Unit = {
        bind(classOf[Service]).toInstance(service)
      }
    })
  })))

  "Auctions controller" should {

    "redirect unauthenticated users to a login page" in new WithMockedShopApplication {
      route(FakeRequest(routes.Auctions.room(1))) must beSome.which(status(_) must equalTo (SEE_OTHER))
    }

    "show auction rooms for authenticated users" in new WithMockedShopApplication {
      val shop = mock[Shop]
      service.shop returns shop
      shop.get(1) returns Some(Item(1, "Play Framework Essentials", 42))

      route(FakeRequest(routes.Auctions.room(1)).withSession(Authentication.UserKey -> "Alice")) must beSome.which(status(_) must equalTo (OK))
    }

  }

}
