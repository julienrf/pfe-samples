package controllers;


import play.core.j.HttpExecutionContext;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import scala.concurrent.ExecutionContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class DBAction extends Action<Void> {

    @With(DBAction.class)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface DB {}

    static ExecutionContext jdbcEC = Akka.system().dispatchers().lookup("jdbc-execution-context");

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        return F.Promise.promise(() -> delegate.call(ctx), HttpExecutionContext.fromThread(jdbcEC)).flatMap(r -> r);
    }
}
