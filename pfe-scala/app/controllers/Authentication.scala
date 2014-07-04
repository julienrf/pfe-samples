package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import play.api.mvc.Results.Redirect
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText}
import play.api.i18n.Messages
import scala.concurrent.Future

@Singleton class Authentication @Inject() (service: Service) extends Controller(service) {

  import Authentication._

  def login(returnTo: String) = Action { implicit request =>
    Ok(views.html.login(Login.form, returnTo))
  }

  def authenticate(returnTo: String) = Action { implicit request =>
    val submission = Login.form.bindFromRequest()
    submission.fold(
      errors => BadRequest(views.html.login(errors, returnTo)),
      {
        case (username, password) =>
          if (service.users.authenticate(username, password)) {
            Redirect(returnTo).addingToSession(UserKey -> username)
          } else {
            BadRequest(views.html.login(submission.withGlobalError(Messages("auth.unknown", username)), returnTo))
          }
      }
    )
  }

  val logout = Action { implicit request =>
    Redirect(routes.Items.list()).removingFromSession(UserKey)
  }


}

object Authentication {

  val UserKey = "username"

  type Login = (String, String)
  object Login {
    val form = Form(tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    ))
  }

  def authenticated[A](f: String => A, g: => A)(implicit request: RequestHeader): A =
    request.session.get(UserKey) match {
      case Some(username) => f(username)
      case None => g
    }
}

class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]) =
    Authentication.authenticated(
      username => block(new AuthenticatedRequest(username, request)),
      Future.successful(Redirect(routes.Authentication.login(request.uri)))
    )(request)
}
