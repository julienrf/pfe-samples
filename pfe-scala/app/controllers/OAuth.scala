package controllers

import play.api.libs.ws.WS
import play.api.mvc.{RequestHeader, Call, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

object OAuth extends Controller {

  val tokenKey = "oauth-token"
  
  val authorizationEndpoint = "https://accounts.google.com/o/oauth2/auth"
  val tokenEndpoint = "https://accounts.google.com/o/oauth2/token"
  val clientId = "1079303020045-4kie53crgo06su51pi3dnbm90thc2q33.apps.googleusercontent.com"
  val clientSecret = "9-PoA1ZwynHJlE4Y3VY8fONX"

  val ws = WS.client(play.api.Play.current)

  def authorizeUrl(returnTo: Call)(implicit request: RequestHeader): String =
    makeUrl(authorizationEndpoint,
        "response_type" -> "code",
        "client_id" -> clientId,
        "redirect_uri" -> routes.OAuth.callback().absoluteURL(),
        "scope" -> "https://www.googleapis.com/auth/plus.login",
        "state" -> returnTo.url
      )

  def makeUrl(endpoint: String, qs: (String, String)*): String = {
    import java.net.URLEncoder.{encode => enc}
    val params = for ((n, v) <- qs) yield s"""${enc(n, "utf-8")}=${enc(v, "utf-8")}"""
    endpoint + params.toSeq.mkString("?", "&", "")
  }

  val callback = Action.async { implicit request =>
    request.getQueryString("code") match {
      case Some(code) =>
        val returnTo = request.getQueryString("state") getOrElse routes.Items.list().url
        for {
          response <- ws.url(tokenEndpoint).post(Map(
            "code" -> Seq(code),
            "client_id" -> Seq(clientId),
            "client_secret" -> Seq(clientSecret),
            "redirect_uri" -> Seq(routes.OAuth.callback().absoluteURL()),
            "grant_type" -> Seq("authorization_code")
          ))
        } yield {
          (response.json \ "access_token").validate[String].fold(
            _ => InternalServerError,
            token => Redirect(returnTo).addingToSession(tokenKey -> token)
          )
        }
      case None =>
        Future.successful(InternalServerError)
    }
  }

}
