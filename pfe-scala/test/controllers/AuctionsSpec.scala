package controllers

import com.google.inject.{AbstractModule, Module}
import models.{Item, Shop}
import org.specs2.mock.Mockito
import play.api.{Configuration, Environment}
import play.api.inject.guice.GuiceApplicationLoader
import play.api.test._
import shop.WithShopApplication

class AuctionsSpec extends PlaySpecification with Mockito {

  class WithMockedShopApplicationLoader extends { val service = mock[Service] } with WithApplicationLoader(new GuiceApplicationLoader(new AbstractModule {
    def configure() = bind(classOf[Service]).toInstance(service)
  }))

  "Auctions controller" should {

    "redirect unauthenticated users to a login page" in new WithMockedShopApplicationLoader {
      route(FakeRequest(routes.Auctions.room(1))) must beSome.which(status(_) must equalTo (SEE_OTHER))
    }

    "show auction rooms for authenticated users" in new WithMockedShopApplicationLoader {
      val shop = mock[Shop]
      service.shop returns shop
      shop.get(1) returns Some(Item(1, "Play Framework Essentials", 42))

      route(FakeRequest(routes.Auctions.room(1)).withSession(Authentication.UserKey -> "Alice")) must beSome.which(status(_) must equalTo (OK))
    }

  }

}
