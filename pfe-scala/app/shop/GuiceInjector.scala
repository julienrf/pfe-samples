package shop

import com.google.inject._
import play.api.{Application, GlobalSettings}

import scala.concurrent.ExecutionContext

trait GuiceInjector extends GlobalSettings {

  val playApplicationModule = new AbstractModule {
    def configure() = {
      bind(classOf[Application]) toProvider new Provider[Application] {
        def get() = play.api.Play.current
      }
      bind(classOf[ExecutionContext]) toProvider new Provider[ExecutionContext] {
        def get() = play.api.libs.concurrent.Execution.defaultContext
      }
    }
  }

  /**
   * Override this method to use additional modules
   */
  val additionalModules: Seq[Module] = Seq.empty

  lazy val injector: Injector = Guice.createInjector(playApplicationModule +: additionalModules: _*)

  override def getControllerInstance[A](controllerClass: Class[A]) =
    injector.getInstance(controllerClass)
}
