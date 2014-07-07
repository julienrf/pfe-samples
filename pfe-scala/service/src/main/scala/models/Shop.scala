package models

case class Item(id: Long, name: String, price: Double)

class Shop(schema: db.Schema) {

  import schema.{ds, items}
  import schema.queryLanguage._

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
