package models

case class Item(id: Long, name: String, price: Double)

trait Shop {

  def list(): Iterable[Item]

  def create(name: String, price: Double): Option[Item]

  def get(id: Long): Option[Item]

  def update(id: Long, name: String, price: Double): Option[Product]

  def delete(id: Long): Boolean

}

object Shop extends Shop {

  import db.Schema.{ds, items}
  import db.Schema.queryLanguage._
  import play.api.Play.current

  def list(): Iterable[Item] = ds withSession { implicit session =>
    items.list()
  }

  def create(name: String, price: Double): Option[Item] = ds withSession { implicit session =>
    val id = items.returning(items.map(_.id)) += Item(0, name, price)
    items.byId(id).firstOption()
  }

  def get(id: Long): Option[Item] = ds withSession { implicit session =>
    items.byId(id).firstOption()
  }

  def update(id: Long, name: String, price: Double): Option[Item] = ds withSession { implicit session =>
    items.byId(id).update(Item(id, name, price))
    items.byId(id).firstOption()
  }

  def delete(id: Long): Boolean = ds withSession { implicit session =>
    items.byId(id).delete != 0
  }

}
