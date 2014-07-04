package controllers

import play.api.libs.concurrent.Akka
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.Future

trait WithDBAction { this: Controller =>

  object DBAction extends ActionBuilder[Request] {

    override protected val executionContext = Akka.system.dispatchers.lookup("jdbc-execution-context")

    def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = block(request)

  }

}