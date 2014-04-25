package views;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static shop.ShopApplication.withBrowser;

public class UITest {

    @Test
    public void addItem() {
        withBrowser((browser) -> {
            browser.goTo(controllers.routes.Items.list().url());
            assertThat(browser.$("ul").getText()).isEqualTo("");
            browser.$("a[href=\"" + controllers.routes.Items.createForm().url() + "\"]").click();
            browser.fill("input[name=name]").with("Play Framework Essentials");
            browser.fill("input[name=price]").with("42");
            browser.submit("form");
            assertThat(browser.$("body").getText()).contains("Play Framework Essentials: 42.00 €");
        });
    }

}
