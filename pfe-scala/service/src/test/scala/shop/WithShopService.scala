package shop

import db.Schema
import models.Shop
import play.api.test.{WithApplication, Helpers, FakeApplication}

class WithShopService extends WithApplication(FakeApplication(additionalConfiguration = Helpers.inMemoryDatabase())) {
  lazy val shop = new Shop(new Schema(app))
}
