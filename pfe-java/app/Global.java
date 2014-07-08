import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import controllers.Service;
import controllers.oauth.OAuth;
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.ws.WSClient;

public class Global extends GlobalSettings {

    final Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(Application.class).toProvider(new Provider<Application>() {
                @Override
                public Application get() {
                    return play.Play.application();
                }
            });
            bind(OAuth.Configuration.class).toProvider(new Provider<OAuth.Configuration>() {
                @Override
                public OAuth.Configuration get() {
                    return new OAuth.Configuration(
                            "https://accounts.google.com/o/oauth2/auth",
                            "https://accounts.google.com/o/oauth2/token",
                            "1079303020045-4kie53crgo06su51pi3dnbm90thc2q33.apps.googleusercontent.com",
                            "9-PoA1ZwynHJlE4Y3VY8fONX",
                            "https://www.googleapis.com/auth/plus.login",
                            controllers.routes.Items.list().url()
                    );
                }
            });
            bind(WSClient.class).toProvider(new Provider<WSClient>() {
                @Override
                public WSClient get() {
                    return injector.getInstance(Service.class).ws;
                }
            });
        }
    });

    public void onStart(Application app) {
        super.onStart(app);
        Service service = injector.getInstance(Service.class);
        if (service.shop.list().isEmpty()) {
            service.shop.create("Play Framework Essentials", 42.0);
        }
    }

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) {
        return injector.getInstance(controllerClass);
    }

}
