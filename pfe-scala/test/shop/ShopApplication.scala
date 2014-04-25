package shop

import play.api.test.{WithApplication, WithBrowser, Helpers, FakeApplication}
import play.api.DefaultGlobal

object `package` {
  def fakeShopApplication() = FakeApplication(
    withGlobal = Some(DefaultGlobal),
    additionalConfiguration = Helpers.inMemoryDatabase()
  )

}

class WithShopApplication extends WithApplication(fakeShopApplication())

class WithShopBrowser extends WithBrowser(app = fakeShopApplication())