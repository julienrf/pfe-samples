package shop

import play.api.test.{Helpers, FakeApplication, WithApplication}

class ShopApplication extends WithApplication(FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase()))