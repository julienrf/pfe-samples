package controllers;

import models.Users;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Authentication extends Controller {

    public static class Login {
        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
    }

    static final String USER_KEY = "username";

    static final Users users = models.Users.Users;

    public static Result login(String returnTo) {
        return ok(views.html.login.render(Form.form(Login.class), returnTo));
    }

    public static Result authenticate(String returnTo) {
        Form<Login> submission = Form.form(Login.class).bindFromRequest();
        if (submission.hasErrors()) {
            return badRequest(views.html.login.render(submission, returnTo));
        } else {
            Login login = submission.get();
            if (users.authenticate(login.username, login.password)) {
                session().put(USER_KEY, login.username);
                return redirect(returnTo);
            } else {
                submission.reject(Messages.get("auth.unknown", login.username));
                return badRequest(views.html.login.render(submission, returnTo));
            }
        }
    }

    public static Result logout() {
        session().remove(USER_KEY);
        return redirect(routes.Items.list());
    }

    public static <A> A authenticated(Http.Context ctx, F.Function<String, A> f, F.Function0<A> g) throws Throwable {
        String username = ctx.session().get(USER_KEY);
        if (username != null) {
            return f.apply(username);
        } else {
            return g.apply();
        }
    }

}
