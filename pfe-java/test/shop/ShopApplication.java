package shop;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import controllers.Service;
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.F;
import play.test.FakeApplication;
import play.test.TestBrowser;

import java.util.function.Consumer;

import static play.test.Helpers.*;

public class ShopApplication {

    public static FakeApplication shopApplication() {
        return fakeApplication(inMemoryDatabase(), new GlobalSettings() {
            final Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Application.class).toProvider(new Provider<Application>() {
                        @Override
                        public Application get() {
                            return play.Play.application();
                        }
                    });
                }
            });

            @Override
            public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
                return injector.getInstance(controllerClass);
            }

            @Override
            public <T extends EssentialFilter> Class<T>[] filters() {
                return new Class[]{CSRFFilter.class};
            }

        });
    }

    public static void withApplication(Consumer<Service> consumer) {
        FakeApplication app = shopApplication();
        running(app, () -> consumer.accept(app.getWrappedApplication().global().getControllerInstance(Service.class)));
    }

    public static void withBrowser(F.Callback<TestBrowser> callback) {
        running(testServer(play.api.test.Helpers.testServerPort(), shopApplication()), HTMLUNIT, callback);
    }

}
