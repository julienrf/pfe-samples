package loaders

import db.Schema
import models.{AuctionRooms, Shop, SocialNetwork, Users}
import play.api.BuiltInComponents
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.{BoneCPComponents, DBComponents}
import play.api.libs.concurrent.AkkaComponents
import play.api.libs.ws.ning.NingWSComponents
import play.core.Router

trait ShopServiceComponents extends BuiltInComponents with NingWSComponents with DBComponents with BoneCPComponents
    with EvolutionsComponents with AkkaComponents {

  val dynamicEvolutions = new DynamicEvolutions // WTF???
  applicationEvolutions // Force the application of evolutions, if needed

  val shop = new Shop(new Schema(dbApi.database("default").dataSource))
  val socialNetwork = new SocialNetwork(wsClient)
  val users = new Users
  val auctionRooms = new AuctionRooms(actorSystem)

  def routes: Router.Routes = Router.Null

}