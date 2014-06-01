package controllers

import play.api.mvc._
import play.api.Routes
import play.twirl.api.JavaScript

object Application extends Controller {

  def index = Action {
    Ok(views.html.main())
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
