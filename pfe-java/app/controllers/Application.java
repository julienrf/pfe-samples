package controllers;

import play.Routes;
import play.cache.Cache;
import play.cache.Cached;
import play.mvc.*;
import play.twirl.api.JavaScript;

public class Application extends Controller {

    @Cached(key = "main-html")
    public static Result index() {
        return ok(views.html.main.render());
    }

    public static Result javascriptRouter() {
        JavaScript router = Routes.javascriptRouter("routes",
                routes.javascript.Items.delete(),
                routes.javascript.Auctions.bid(),
                routes.javascript.Auctions.notifications(),
                routes.javascript.Auctions.channel()
        );
        return ok(JavaScript.apply("define(function () { " + router.body() + "; return routes })"));
    }

}
