package shop

import db.Schema
import models.{AuctionRooms, Users, SocialNetwork, Shop}
import play.api.BuiltInComponents
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.{BoneCPComponents, DBComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router

trait ShopServiceComponents extends BuiltInComponents with AhcWSComponents with DBComponents with BoneCPComponents with EvolutionsComponents {

  val dynamicEvolutions = new DynamicEvolutions // WTF???
  applicationEvolutions // Force the application of evolutions, if needed

  val shop = new Shop(new Schema(dbApi.database("default").dataSource))
  val socialNetwork = new SocialNetwork(wsClient)
  val users = new Users
  val auctionRooms = new AuctionRooms(actorSystem)

  def router: Router = Router.empty

}
