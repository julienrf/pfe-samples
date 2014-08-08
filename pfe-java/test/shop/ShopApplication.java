package shop;

import com.google.inject.Guice;
import com.google.inject.Injector;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.inject.PlayApplicationModule;
import play.test.FakeApplication;

import static play.test.Helpers.*;

public class ShopApplication {

    public static FakeApplication shopApplication() {
        return fakeApplication(inMemoryDatabase(), new GlobalSettings() {
            final Injector injector = Guice.createInjector(new PlayApplicationModule());

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

}
