package models

case class Item(id: Long, name: String, price: Double)

class Shop(schema: db.Schema) {

  import schema.{db, items}
  import schema.queryLanguage._

  def list(): Iterable[Item] = db withSession { implicit session =>
    items.list
  }

  def create(name: String, price: Double): Option[Item] = db withSession { implicit session =>
    val id = items.returning(items.map(_.id)) += Item(0, name, price)
    items.byId(id).firstOption
  }

  def get(id: Long): Option[Item] = db withSession { implicit session =>
    items.byId(id).firstOption
  }

  def update(id: Long, name: String, price: Double): Option[Item] = db withSession { implicit session =>
    items.byId(id).update(Item(id, name, price))
    items.byId(id).firstOption
  }

  def delete(id: Long): Boolean = db withSession { implicit session =>
    items.byId(id).delete != 0
  }

}
