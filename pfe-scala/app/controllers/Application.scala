package controllers

import play.api.mvc.{Controller, Action}
import play.api.Routes
import play.twirl.api.JavaScript
import play.api.cache.Cached
import play.api.Play.current
import play.api.i18n.Lang

object Application extends Controller {

  val index = Cached(implicit request => s"main-html-${implicitly[Lang].code}") {
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
