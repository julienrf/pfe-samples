package controllers

import play.api.mvc.{Controller, Action}
import play.api.routing.JavaScriptReverseRouter
import play.twirl.api.JavaScript
import play.api.cache.Cached
import play.api.i18n.{Messages, I18nSupport, MessagesApi, Lang}

class Application(cached: Cached, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val index = cached(implicit request => s"main-html-${implicitly[Messages].lang.code}") {
    Action { implicit request =>
      Ok(views.html.main())
    }
  }

  def javascriptRouter = Action { implicit request =>
    val router = JavaScriptReverseRouter("routes")(
      routes.javascript.Items.delete,
      routes.javascript.Auctions.bid,
      routes.javascript.Auctions.notifications,
      routes.javascript.Auctions.channel
    )
    Ok(JavaScript(s"""define(function () { $router; return routes })"""))
  }

}
