package controllers.oauth

import javax.inject.{Inject, Singleton}

import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Call, Controller, RequestHeader}

import scala.concurrent.{ExecutionContext, Future}

@Singleton class OAuth @Inject() (ws: WSClient, val configuration: OAuth.Configuration)(implicit ec: ExecutionContext) extends Controller {

  val callback = Action.async { implicit request =>
    request.getQueryString("code") match {
      case Some(code) =>
//        val returnTo = request.getQueryString("state") getOrElse routes.Items.list().url
        val returnTo = request.getQueryString("state") getOrElse configuration.defaultReturnUrl
        for {
          response <- ws.url(configuration.tokenEndpoint).post(Map(
            "code" -> Seq(code),
            "client_id" -> Seq(configuration.clientId),
            "client_secret" -> Seq(configuration.clientSecret),
            "redirect_uri" -> Seq(routes.OAuth.callback().absoluteURL()),
            "grant_type" -> Seq("authorization_code")
          ))
        } yield {
          (response.json \ "access_token").validate[String].fold(
            _ => InternalServerError,
            token => Redirect(returnTo).addingToSession(configuration.tokenKey -> token)
          )
        }
      case None =>
        Future.successful(InternalServerError)
    }
  }

  def authorizeUrl(returnTo: Call)(implicit request: RequestHeader): String =
    OAuth.makeUrl(configuration.authorizationEndpoint,
      "response_type" -> "code",
      "client_id" -> configuration.clientId,
      "redirect_uri" -> routes.OAuth.callback().absoluteURL(),
      "scope" -> configuration.scope,
      "state" -> returnTo.url
    )

  def authenticated[A](f: String => A, g: => A)(implicit request: RequestHeader): A =
    request.session.get(configuration.tokenKey) match {
      case Some(token) => f(token)
      case None => g
    }

}

object OAuth {

  case class Configuration @Inject() (
    authorizationEndpoint: String,
    tokenEndpoint: String,
    clientId: String,
    clientSecret: String,
    scope: String,
    tokenKey: String = "oauth-token",
    defaultReturnUrl: String = "/")

  def makeUrl(endpoint: String, qs: (String, String)*): String = {
    import java.net.URLEncoder.{encode => enc}
    val params = for ((n, v) <- qs) yield s"""${enc(n, "utf-8")}=${enc(v, "utf-8")}"""
    endpoint + params.toSeq.mkString("?", "&", "")
  }

}