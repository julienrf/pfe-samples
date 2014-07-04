package controllers;

import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Authentication extends Controller {

    @Inject
    public Authentication(Service service) {
        super(service);
    }

    public static class Login {
        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
    }

    static final String USER_KEY = "username";

    public Result login(String returnTo) {
        return ok(views.html.login.render(Form.form(Login.class), returnTo));
    }

    public Result authenticate(String returnTo) {
        Form<Login> submission = Form.form(Login.class).bindFromRequest();
        if (submission.hasErrors()) {
            return badRequest(views.html.login.render(submission, returnTo));
        } else {
            Login login = submission.get();
            if (service.users.authenticate(login.username, login.password)) {
                session().put(USER_KEY, login.username);
                return redirect(returnTo);
            } else {
                submission.reject(Messages.get("auth.unknown", login.username));
                return badRequest(views.html.login.render(submission, returnTo));
            }
        }
    }

    public Result logout() {
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
