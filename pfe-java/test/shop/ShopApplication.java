package shop;

import static play.test.Helpers.*;

public class ShopApplication {
    public static void runningShopApplication(Runnable runnable) {
        running(fakeApplication(inMemoryDatabase(), fakeGlobal()), runnable);
    }
}
