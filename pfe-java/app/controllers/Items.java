package controllers;

import controllers.oauth.OAuth;
import models.Item;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.function.Function;

import static controllers.DBAction.DB;
import static controllers.Render.render;
import static controllers.Render.version;
import static play.mvc.Http.MimeTypes;

@Singleton
public class Items extends Controller {

    final OAuth oauth;

    @Inject
    public Items(Service service, OAuth oauth) {
        super(service);
        this.oauth = oauth;
    }

    public static class CreateItem {
        @Constraints.Required
        public String name;
        @Constraints.Required
        @Constraints.Min(0)
        public Double price;
    }

    @DB
    public Result list() {
        return render(
            version(MimeTypes.HTML, () -> ok(views.html.list.render(service.shop.list()))),
            version(MimeTypes.JSON, () -> ok(Json.toJson(service.shop.list())))
        );
    }

    public Result createForm() {
        return ok(views.html.createForm.render(Form.form(CreateItem.class)));
    }

    @DB
    public Result create() {
        Form<CreateItem> submission = Form.form(CreateItem.class).bindFromRequest();
        if (submission.hasErrors()) {
            return render(
                    version(MimeTypes.HTML, () -> badRequest(views.html.createForm.render(submission))),
                    version(MimeTypes.JSON, () -> badRequest(submission.errorsAsJson()))
            );
        } else {
            CreateItem createItem = submission.get();
            Item item = service.shop.create(createItem.name, createItem.price);
            if (item != null) {
                return render(
                        version(MimeTypes.HTML, () -> redirect(routes.Items.details(item.id))),
                        version(MimeTypes.JSON, () -> ok(Json.toJson(item)))
                );
            } else {
                return internalServerError();
            }
        }
    }

    @DB
    public Result details(Long id) {
        Item item = service.shop.get(id);
        if (item != null) {
            return render(
                    version(MimeTypes.HTML, () -> ok(views.html.details.render(item))),
                    version(MimeTypes.JSON, () -> ok(Json.toJson(item)))
            );
        } else {
            return notFound();
        }
    }

    @DB
    public Result update(Long id) {
        Form<CreateItem> submission = Form.form(CreateItem.class).bindFromRequest();
        if (submission.hasErrors()) {
            return badRequest(submission.errorsAsJson());
        } else {
            CreateItem updateItem = submission.get();
            Item updated = service.shop.update(id, updateItem.name, updateItem.price);
            if (updated != null) {
                return ok(Json.toJson(updated));
            } else {
                return internalServerError();
            }
        }
    }

    @DB
    public Result delete(Long id) {
        if (service.shop.delete(id)) {
            return ok();
        } else {
            return badRequest();
        }
    }

    public Result share(Long id) {
        return oauth.authenticated(token -> {
            service.socialNetwork.share(routes.Items.details(id).absoluteURL(request()), token);
            return ok();
        }, (() -> redirect(oauth.authorizeUrl(routes.Items.details(id)))));
    }

}
