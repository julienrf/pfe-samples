package views;

import org.junit.Test;
import play.test.FakeApplication;
import play.test.WithBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static shop.ShopApplication.shopApplication;

public class UITest extends WithBrowser {

    @Override
    protected FakeApplication provideFakeApplication() {
        return shopApplication();
    }

    @Test
    public void addItem() {
        browser.goTo(controllers.routes.Items.list().url());
        assertThat(browser.$("ul").getText()).isEqualTo("");
        browser.$("a[href=\"" + controllers.routes.Items.createForm().url() + "\"]").click();
        browser.fill("input[name=name]").with("Play Framework Essentials");
        browser.fill("input[name=price]").with("42");
        browser.submit("form");
        assertThat(browser.$("body").getText()).contains("Play Framework Essentials: 42.00 €");
    }

}
