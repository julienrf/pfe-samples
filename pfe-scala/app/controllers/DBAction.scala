package controllers

import javax.inject.{Singleton, Inject}

import play.api.libs.concurrent.Akka
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.Future

@Singleton class DBAction @Inject() (app: play.api.Application) extends ActionBuilder[Request] {

  override protected val executionContext = Akka.system(app).dispatchers.lookup("jdbc-execution-context")

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = block(request)

}