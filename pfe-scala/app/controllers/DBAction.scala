package controllers

import play.api.libs.concurrent.Akka
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.Future

object DBAction extends ActionBuilder[Request] {

  override protected lazy val executionContext = Akka.system(play.api.Play.current).dispatchers.lookup("jdbc-execution-context")

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = block(request)

}
