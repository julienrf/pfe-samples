package models

import org.specs2.mutable.Specification
import shop.ShopApplication

class ShopSpec extends Specification {

  "A Shop" should {

    val shop = Shop

    "add an item" in new ShopApplication {
      shop.create("Play! Framework Essentials", 42) must beSome (Item(1, "Play! Framework Essentials", 42))
    }

    "list items" in new ShopApplication {
      shop.list must beEmpty
      shop.create("Play! Framework Essentials", 42)
      shop.list must haveSize (1)
      shop.list must contain (Item(1, "Play! Framework Essentials", 42)).exactly(1)
    }

    "get an item" in new ShopApplication {
      val maybeItem = for {
        createdItem <- shop.create("Play! Framework Essentials", 42)
        item <- shop.get(createdItem.id)
      } yield item
      maybeItem must beSome (Item(1, "Play! Framework Essentials", 42))
    }

    "update an item" in new ShopApplication {
      val maybeItem = for {
        createdItem <- shop.create("Play! Framework Essentials", 42)
        updatedItem <- shop.update(createdItem.id, createdItem.name, 10)
        item <- shop.get(updatedItem.id)
        if item == updatedItem // Be sure that `Shop.update` returns the updated item
      } yield item
      maybeItem must beSome (Item(1, "Play! Framework Essentials", 10))
    }

    "delete an item" in new ShopApplication {
      val maybeDeleted = for {
        createdItem <- shop.create("Play! Framework Essentials", 42)
        deleted = shop.delete(createdItem.id)
      } yield (deleted, shop.get(createdItem.id))
      maybeDeleted must beSome ((true, None))
    }

  }

}
