package shop

import controllers.Service
import play.api.mvc.WithFilters
import play.api.test.{WithApplication, WithBrowser, Helpers, FakeApplication}
import play.filters.csrf.CSRFFilter

object `package` {

  def fakeShopApplication() = FakeApplication(
    withGlobal = Some(new WithFilters(CSRFFilter()) with GuiceInjector),
    additionalConfiguration = Helpers.inMemoryDatabase()
  )

}

class WithShopApplication extends WithApplication(fakeShopApplication()) {
  lazy val shop = app.global.getControllerInstance(classOf[Service]).shop
}

class WithShopBrowser extends WithBrowser(app = fakeShopApplication())