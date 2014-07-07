package shop;

import models.Shop;
import play.test.FakeApplication;

import java.util.function.Consumer;

import static play.test.Helpers.*;

public class ShopService {

    static FakeApplication shopApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public static void withShop(Consumer<Shop> consumer) {
        FakeApplication app = shopApplication();
        running(app, () -> consumer.accept(new Shop()));
    }


}
