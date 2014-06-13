package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText}
import scala.Some
import play.api.i18n.Messages

object Authentication extends Controller {

  val UserKey = "username"

  val users = models.Users

  type Login = (String, String)
  object Login {
    val form = Form(tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    ))
  }

  def login(returnTo: String) = Action { implicit request =>
    Ok(views.html.login(Login.form, returnTo))
  }

  def authenticate(returnTo: String) = Action { implicit request =>
    val submission = Login.form.bindFromRequest()
    submission.fold(
      errors => BadRequest(views.html.login(errors, returnTo)),
      {
        case (username, password) =>
          if (users.authenticate(username, password)) {
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

  def authenticated[A](f: String => A, g: => A)(implicit request: RequestHeader): A =
    request.session.get(UserKey) match {
      case Some(username) => f(username)
      case None => g
    }

  def authenticatedAction(f: String => Result)(implicit request: RequestHeader): Result =
    authenticated(f, Redirect(routes.Authentication.login(request.uri)))

}
