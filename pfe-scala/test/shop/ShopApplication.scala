package shop

import play.api.test.{Helpers, FakeApplication, WithApplication}
import play.api.DefaultGlobal

class ShopApplication extends WithApplication(
  FakeApplication(
    withGlobal = Some(DefaultGlobal),
    additionalConfiguration = Helpers.inMemoryDatabase()
  )
)