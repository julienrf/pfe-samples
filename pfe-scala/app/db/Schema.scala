package db

import models.Item
import play.api.Application

class Schema(app: Application) {

  val queryLanguage = scala.slick.driver.H2Driver.simple
  import queryLanguage._
  import scala.slick.lifted.{Tag, TableQuery}

  class Items(tag: Tag) extends Table[Item](tag, "ITEMS") {
    val id = column[Long]("ID", O.AutoInc)
    val name = column[String]("NAME")
    val price = column[Double]("PRICE")
    override def * = (id, name, price) <> (Item.tupled, Item.unapply)
  }
  val items = TableQuery[Items]

  object Items {
    implicit class ItemsExtensions[A](val q: Query[Items, A]) {
      val byId = Compiled { (id: Column[Long]) =>
        q.filter(_.id === id)
      }
    }
  }

  val ds = Database.forDataSource(play.api.db.DB.getDataSource()(app))

}
