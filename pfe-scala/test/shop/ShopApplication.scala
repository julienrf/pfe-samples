package shop

import play.api.mvc.WithFilters
import play.api.test.{Helpers, FakeApplication, WithApplication, WithBrowser}
import play.filters.csrf.CSRFFilter

object `package` {

  def fakeShopApplication() = FakeApplication(
    withGlobal = Some(new WithFilters(CSRFFilter()) with GuiceInjector),
    additionalConfiguration = Helpers.inMemoryDatabase()
  )

}

class WithShopApplication extends WithApplication(app = fakeShopApplication())

class WithShopBrowser extends WithBrowser(app = fakeShopApplication())