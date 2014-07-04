package controllers;

import play.Routes;
import play.cache.Cached;
import play.mvc.Result;
import play.twirl.api.JavaScript;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Application extends Controller {

    @Inject
    public Application(Service service) {
        super(service);
    }

    @Cached(key = "main-html")
    public Result index() {
        return ok(views.html.main.render());
    }

    public Result javascriptRouter() {
        JavaScript router = Routes.javascriptRouter("routes",
                routes.javascript.Items.delete(),
                routes.javascript.Auctions.bid(),
                routes.javascript.Auctions.notifications(),
                routes.javascript.Auctions.channel()
        );
        return ok(JavaScript.apply("define(function () { " + router.body() + "; return routes })"));
    }

}
