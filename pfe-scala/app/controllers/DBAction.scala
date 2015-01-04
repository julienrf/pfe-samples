package controllers

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import play.api.libs.concurrent.Akka
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.Future

class DBAction(system: ActorSystem) extends ActionBuilder[Request] {

  override protected val executionContext = system.dispatchers.lookup("jdbc-execution-context")

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = block(request)

}