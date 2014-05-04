package controllers;

import play.Routes;
import play.mvc.*;

public class Application extends Controller {

  public static Result index()  {
    return ok(views.html.main.render());
  }

    public static Result javascriptRouter() {
        return ok(Routes.javascriptRouter("routes",
                routes.javascript.Items.delete()));
    }

}
