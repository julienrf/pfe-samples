package shop;

import play.libs.F;
import play.test.FakeApplication;
import play.test.TestBrowser;

import static play.test.Helpers.*;

public class ShopApplication {

    public static FakeApplication shopApplication() {
        return fakeApplication(inMemoryDatabase(), fakeGlobal());
    }

    public static void withApplication(Runnable runnable) {
        running(shopApplication(), runnable);
    }

    public static void withBrowser(F.Callback<TestBrowser> callback) {
        running(testServer(play.api.test.Helpers.testServerPort(), shopApplication()), HTMLUNIT, callback);
    }

}
