package controllers

import play.api.mvc.{RequestHeader, Controller, Action}
import play.api.Routes
import play.twirl.api.JavaScript
import play.api.cache.Cached
import play.api.i18n.{MessagesApi, Messages, Lang}

import scala.language.implicitConversions

class Application(cached: Cached, messagesApi: MessagesApi) extends Controller {

  implicit def request2Messages(implicit requestHeader: RequestHeader): Messages = messagesApi.preferred(requestHeader)

  val index = cached(implicit request => s"main-html-${request2Messages.lang.code}") {
    Action { implicit request =>
      Ok(views.html.main())
    }
  }

  def javascriptRouter = Action { implicit request =>
    val router = Routes.javascriptRouter("routes")(
      routes.javascript.Items.delete,
      routes.javascript.Auctions.bid,
      routes.javascript.Auctions.notifications,
      routes.javascript.Auctions.channel
    )
    Ok(JavaScript(s"""define(function () { $router; return routes })"""))
  }

}
