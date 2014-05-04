package controllers

import play.api.mvc._
import play.api.Routes

object Application extends Controller {

  def index = Action {
    Ok(views.html.main())
  }

  def javascriptRouter = Action { implicit request =>
    Ok(Routes.javascriptRouter("routes")(
      routes.javascript.Items.delete
    ))
  }

}
