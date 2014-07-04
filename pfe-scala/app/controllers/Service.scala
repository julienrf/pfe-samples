package controllers

import javax.inject.{Singleton, Inject}

import models.{SocialNetwork, Users, AuctionRooms, Shop}
import db.Schema
import play.api.libs.ws.WS

@Singleton class Service @Inject() (val app: play.api.Application) {

  val ws = WS.client(app)

  val shop = new Shop(new Schema(app))

  val auctionRooms = new AuctionRooms(app)

  val users = new Users

  val socialNetwork = new SocialNetwork(ws)

}
