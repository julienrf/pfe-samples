package shop

import com.google.inject.{Provider, AbstractModule}
import controllers.Service
import controllers.oauth.OAuth
import play.api.libs.ws.WSClient

trait ShopInjector extends GuiceInjector {

  override val additionalModules = Seq(new AbstractModule {
    def configure() = {
      bind(classOf[OAuth.Configuration]) toProvider new Provider[OAuth.Configuration] {
        def get() = OAuth.Configuration(
          authorizationEndpoint = "https://accounts.google.com/o/oauth2/auth",
          tokenEndpoint = "https://accounts.google.com/o/oauth2/token",
          clientId = "1079303020045-4kie53crgo06su51pi3dnbm90thc2q33.apps.googleusercontent.com",
          clientSecret = "9-PoA1ZwynHJlE4Y3VY8fONX",
          scope = "https://www.googleapis.com/auth/plus.login",
          defaultReturnUrl = controllers.routes.Items.list().url
        )
      }
      bind(classOf[WSClient]) toProvider new Provider[WSClient] {
        def get() = injector.getInstance(classOf[Service]).ws
      }
    }
  })

}
