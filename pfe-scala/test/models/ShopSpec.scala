package models

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

class ShopSpec extends Specification with Mockito {
  "A Shop" should {
    "add items" in {
      // Mock creation and configuration
      val shopMock = mock[Shop]
      shopMock.create("Play! Framework Essentials", 42) returns Some(Item(1, "Play! Framework Essentials", 42))
      // Usage
      shopMock.create("Play! Framework Essentials", 42) must beSome(Item(1, "Play! Framework Essentials", 42))
    }
  }
}
