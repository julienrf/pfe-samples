package shop

import com.google.inject.{Injector, Provider, Guice, AbstractModule}
import controllers.Service
import controllers.oauth.OAuth
import play.api.libs.ws.WSClient
import play.api.{Application, GlobalSettings}

import scala.concurrent.ExecutionContext

trait GuiceInjector extends GlobalSettings {

  val injector: Injector = Guice.createInjector(new AbstractModule { self =>
    def configure() = {
      bind(classOf[Application]) toProvider new Provider[Application] {
        def get() = play.api.Play.current
      }
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
      bind(classOf[ExecutionContext]) toProvider new Provider[ExecutionContext] {
        def get() = play.api.libs.concurrent.Execution.defaultContext
      }
    }
  })

  override def getControllerInstance[A](controllerClass: Class[A]) =
    injector.getInstance(controllerClass)
}
