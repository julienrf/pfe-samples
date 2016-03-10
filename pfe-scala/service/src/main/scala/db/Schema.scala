package db

import javax.sql.DataSource

import models.Item

import scala.language.higherKinds

class Schema(dataSource: DataSource) {

  val queryLanguage = slick.driver.H2Driver.api
  import queryLanguage._

  class Items(tag: Tag) extends Table[Item](tag, "ITEMS") {
    val id = column[Long]("ID", O.AutoInc)
    val name = column[String]("NAME")
    val price = column[Double]("PRICE")
    override def * = (id, name, price) <> (Item.tupled, Item.unapply)
  }
  val items = TableQuery[Items]

  object Items {
    implicit class ItemsExtensions[A, F[_]](val q: Query[Items, A, F]) {
      val byId = Compiled { (id: Rep[Long]) =>
        q.filter(_.id === id)
      }
    }
  }

  val db = Database.forDataSource(dataSource)

}
