package controllers;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AuthenticatedAction extends Action.Simple {

    @With(AuthenticatedAction.class)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Authenticated {}

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        return Authentication.authenticated(ctx, username -> {
            ctx.args.put(Authentication.USER_KEY, username);
            return delegate.call(ctx);
        }, () -> F.Promise.pure(redirect(routes.Authentication.login(ctx.request().uri()))));
    }

    public static String getUsername(Http.Context ctx) {
        return (String)(ctx.args.get(Authentication.USER_KEY));
    }

}
