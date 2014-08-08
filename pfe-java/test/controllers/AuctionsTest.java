package controllers;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import models.Item;
import models.Shop;
import org.junit.Test;
import play.GlobalSettings;
import play.inject.PlayApplicationModule;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.WithApplication;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;
import static org.mockito.Mockito.*;

public class AuctionsTest extends WithApplication {

    Service service;

    @Override
    protected FakeApplication provideFakeApplication() {
        return fakeApplication(new GlobalSettings() {
            {
                service = mock(Service.class);
            }
            final Injector injector = Guice.createInjector(new PlayApplicationModule(), new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Service.class).toInstance(service);
                }
            });

            @Override
            public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
                return injector.getInstance(controllerClass);
            }
        });
    }

    @Test
    public void redirectUnauthenticatedUsers() {
        Result response = route(fakeRequest(routes.Auctions.room(1)));
        assertThat(status(response)).isEqualTo(SEE_OTHER);
    }

    @Test
    public void acceptAuthenticatedUsers() {
        Shop shop = mock(Shop.class);
        when(service.shop()).thenReturn(shop);
        when(shop.get(1L)).thenReturn(new Item(1L, "Play Framework Essentials", 42.0));
        Result response = route(fakeRequest(routes.Auctions.room(1)).withSession(Authentication.USER_KEY, "Alice"));
        assertThat(status(response)).isEqualTo(OK);
    }

}
