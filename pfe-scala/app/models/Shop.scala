package models

import scala.concurrent.stm.{Ref, atomic}
import scala.collection.immutable.SortedMap

case class Item(id: Long, name: String, price: Double)

trait Shop {

  def list: Iterable[Item]

  def create(name: String, price: Double): Option[Item]

  def get(id: Long): Option[Item]

  def update(id: Long, name: String, price: Double): Option[Product]

  def delete(id: Long): Boolean

}

object Shop extends Shop {

  private val items = Ref(SortedMap.empty[Long, Item])
  private val seq = Ref(0L)

  def list: Iterable[Item] = items.single().values

  def create(name: String, price: Double): Option[Item] = {
    val id = seq.single.transformAndGet(_ + 1)
    val item = Item(id, name, price)
    items.single.transform(_ + (id -> item))
    Some(item)
  }

  def get(id: Long): Option[Item] = items.single().get(id)

  def update(id: Long, name: String, price: Double): Option[Item] = atomic { implicit txn =>
    for (_ <- items().get(id)) yield {
      val updated = Item(id, name, price)
      items.transform(_.updated(id, updated))
      updated
    }
  }

  def delete(id: Long): Boolean = atomic { implicit txn =>
    items().isDefinedAt(id) && {
      items.transform(_ - id)
      true
    }
  }

}
