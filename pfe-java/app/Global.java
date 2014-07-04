import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import controllers.Service;
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;

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
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return injector.getInstance(controllerClass);
    }

}
