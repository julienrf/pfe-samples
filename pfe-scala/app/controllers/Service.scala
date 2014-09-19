package controllers

import javax.inject.{Inject, Singleton}

import db.Schema
import models.{AuctionRooms, Shop, SocialNetwork, Users}
import play.api.libs.ws.WSClient

@Singleton class Service @Inject() (val app: play.api.Application) {

  val shop = new Shop(new Schema(app))

  val auctionRooms = new AuctionRooms(app)

  val users = new Users

  val socialNetwork = new SocialNetwork(app.injector.instanceOf[WSClient])

}
