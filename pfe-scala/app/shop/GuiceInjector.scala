package shop

import com.google.inject.{Provider, Guice, AbstractModule}
import play.api.{Application, GlobalSettings}

trait GuiceInjector extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    def configure() = bind(classOf[Application]) toProvider new Provider[Application] {
      def get() = play.api.Play.current
    }
  })

  override def getControllerInstance[A](controllerClass: Class[A]) =
    injector.getInstance(controllerClass)
}
