package controllers;

import models.Item;
import models.Shop;
import play.core.j.HttpExecutionContext;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import java.util.function.Supplier;

import static controllers.Render.render;
import static controllers.Render.version;
import static play.mvc.Http.MimeTypes;

public class Items extends Controller {

    public static class CreateItem {
        @Constraints.Required
        public String name;
        @Constraints.Required
        @Constraints.Min(0)
        public Double price;
    }

    static Shop shop = models.Shop.Shop;

    static ExecutionContext jdbcEC = Akka.system().dispatchers().lookup("jdbc-execution-context");

    static <A> F.Promise<A> async(Supplier<A> f) {
        return F.Promise.promise(f::get, HttpExecutionContext.fromThread(jdbcEC));
    }

    public static F.Promise<Result> list() {
        return async(() -> render(
            version(MimeTypes.HTML, () -> ok(views.html.list.render(shop.list()))),
            version(MimeTypes.JSON, () -> ok(Json.toJson(shop.list())))
        ));
    }

    public static Result createForm() {
        return ok(views.html.createForm.render(Form.form(CreateItem.class)));
    }

    public static F.Promise<Result> create() {
        return async(() -> {
            Form<CreateItem> submission = Form.form(CreateItem.class).bindFromRequest();
            if (submission.hasErrors()) {
                return render(
                        version(MimeTypes.HTML, () -> badRequest(views.html.createForm.render(submission))),
                        version(MimeTypes.JSON, () -> badRequest(submission.errorsAsJson()))
                );
            } else {
                CreateItem createItem = submission.get();
                Item item = shop.create(createItem.name, createItem.price);
                if (item != null) {
                    return render(
                            version(MimeTypes.HTML, () -> redirect(routes.Items.details(item.id))),
                            version(MimeTypes.JSON, () -> ok(Json.toJson(item)))
                    );
                } else {
                    return internalServerError();
                }
            }
        });
    }

    public static F.Promise<Result> details(Long id) {
        return async(() -> {
            Item item = shop.get(id);
            if (item != null) {
                return render(
                        version(MimeTypes.HTML, () -> ok(views.html.details.render(item))),
                        version(MimeTypes.JSON, () -> ok(Json.toJson(item)))
                );
            } else {
                return notFound();
            }
        });
    }

    public static F.Promise<Result> update(Long id) {
        return async(() -> {
            Form<CreateItem> submission = Form.form(CreateItem.class).bindFromRequest();
            if (submission.hasErrors()) {
                return badRequest(submission.errorsAsJson());
            } else {
                CreateItem updateItem = submission.get();
                Item updated = shop.update(id, updateItem.name, updateItem.price);
                if (updated != null) {
                    return ok(Json.toJson(updated));
                } else {
                    return internalServerError();
                }
            }
        });
    }

    public static F.Promise<Result> delete(Long id) {
        return async(() -> {
            if (shop.delete(id)) {
                return ok();
            } else {
                return badRequest();
            }
        });
    }

}
