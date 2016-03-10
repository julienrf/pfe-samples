package models

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

case class Item(id: Long, name: String, price: Double)

class Shop(schema: db.Schema) {

  import schema.{db, items}
  import schema.queryLanguage._

  def list(): Future[Seq[Item]] = db.run(items.result)

  def create(name: String, price: Double): Future[Item] =
    db.run(
      for {
        insertedId <- items.returning(items.map(_.id)) += Item(0, name, price)
        item <- items.byId(insertedId).result.head
      } yield item
    )

  def get(id: Long): Future[Option[Item]] =
    db.run(items.byId(id).result.headOption)

  def update(id: Long, name: String, price: Double): Future[Option[Item]] =
    db.run(
      items.byId(id).update(Item(id, name, price)) >>
      items.byId(id).result.headOption
    )

  def delete(id: Long): Future[Boolean] =
    db.run(items.byId(id).delete.map(_ != 0))

}
