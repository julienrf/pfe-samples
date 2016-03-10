package models

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import shop.WithShopServiceLoader

class ShopSpec(implicit ee: ExecutionEnv) extends Specification {

  "A Shop" should {

    "add an item" in new WithShopServiceLoader {
      shop.create("Play Framework Essentials", 42) must beLike[Item] { case item =>
        (item.name must beEqualTo("Play Framework Essentials")) and (item.price must beEqualTo(42))
      }.await
    }

    "list items" in new WithShopServiceLoader {
      val previousSize = shop.list().map(_.size)
      val list = for {
        _ <- previousSize
        _ <- shop.create("Play Framework Essentials", 42)
        list <- shop.list()
      } yield list
      list.zip(previousSize) must beLike[(Seq[Item], Int)]{ case (l, ps) => l.size must_=== (ps + 1) }.await
      list.map(_.find(item => item.name == "Play Framework Essentials" && item.price == 42)) must beSome[Item].await
    }

    "get an item" in new WithShopServiceLoader {
      val result = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        maybeItem <- shop.get(createdItem.id)
      } yield createdItem.id -> maybeItem
      result must beLike[(Long, Option[Item])] {
        case (id, Some(item)) => (item.id must_=== id) and (item.name must_=== "Play Framework Essentials") and (item.price must_=== 42)
      }.await
    }

    "update an item" in new WithShopServiceLoader {
      val maybeItem = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        updatedItem <- shop.update(createdItem.id, createdItem.name, 10)
        item <- shop.get(createdItem.id)
        if item == updatedItem // Be sure that `Shop.update` returns the updated item
      } yield item
      maybeItem must beSome[Item].which(item => item.name == "Play Framework Essentials" && item.price == 10).await
    }

    "delete an item" in new WithShopServiceLoader {
      val maybeDeleted = for {
        createdItem <- shop.create("Play Framework Essentials", 42)
        deleted <- shop.delete(createdItem.id)
        maybeItem <- shop.get(createdItem.id)
      } yield (deleted, maybeItem)
      maybeDeleted must beEqualTo((true, None)).await
    }

  }

}
