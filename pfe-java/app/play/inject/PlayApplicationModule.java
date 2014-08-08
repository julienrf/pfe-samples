package play.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import play.Application;

public class PlayApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Application.class).toProvider(new Provider<Application>() {
            @Override
            public Application get() {
                return play.Play.application();
            }
        });
    }
}
