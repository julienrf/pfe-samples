package models

import org.specs2.mutable.Specification
import shop.WithShopService

class ShopSpec extends Specification {

  "A Shop" should {

    "add an item" in new WithShopService {
      shop.create("Play Framework Essentials", 42) must beSome[Item].which { item =>
        item.name == "Play Framework Essentials" && item.price == 42
      }
    }

    "list items" in new WithShopService {
      val previousSize = shop.list().size
      shop.create("Play Framework Essentials", 42)
      shop.list() must haveSize (previousSize + 1)
      shop.list().find(item => item.name == "Play Framework Essentials" && item.price == 42) must beSome
    }

    "get an item" in new WithShopService {
      val maybeItem = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        item <- shop.get(createdItem.id)
      } yield createdItem.id -> item
      maybeItem must beSome[(Long, Item)].which {
        case (id, item) => item.id == id && item.name == "Play Framework Essentials" && item.price == 42
      }
    }

    "update an item" in new WithShopService {
      val maybeItem = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        updatedItem <- shop.update(createdItem.id, createdItem.name, 10)
        item <- shop.get(updatedItem.id)
        if item == updatedItem // Be sure that `Shop.update` returns the updated item
      } yield item
      maybeItem must beSome[Item].which(item => item.name == "Play Framework Essentials" && item.price == 10)
    }

    "delete an item" in new WithShopService {
      val maybeDeleted = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        deleted = shop.delete(createdItem.id)
      } yield (deleted, shop.get(createdItem.id))
      maybeDeleted must beSome ((true, None))
    }

  }

}
