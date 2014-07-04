package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.Action
import play.api.Routes
import play.twirl.api.JavaScript
import play.api.cache.Cached
import play.api.i18n.Lang

@Singleton class Application @Inject() (service: Service) extends Controller(service) {

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
