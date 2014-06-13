package controllers;

import models.Users;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class Authentication extends Controller {

    public static class Login {
        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
    }

    static final String UserKey = "username";

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
                session().put(UserKey, login.username);
                return redirect(returnTo);
            } else {
                submission.reject(Messages.get("auth.unknown", login.username));
                return badRequest(views.html.login.render(submission, returnTo));
            }
        }
    }

    public static Result logout() {
        session().remove(UserKey);
        return redirect(routes.Items.list());
    }

}
