package views

import play.api.test.PlaySpecification
import shop.WithShopBrowser

class UISpec extends PlaySpecification {

  "A user" should {
    "add a new item to the item list" in new WithShopBrowser {
      browser.goTo(controllers.routes.Items.list().url)
      // No item yet
      browser.$("ul").getText must equalTo ("")

      // Click on the “Add a new item” button
      browser.$(s"""a[href="${controllers.routes.Items.createForm().url}"]""").click()

      browser.submit("form",
        "name" -> "Play Framework Essentials",
        "price" -> "42")

      browser.$("body").getText must contain ("Play Framework Essentials: 42.00 €")

    }
  }

}
