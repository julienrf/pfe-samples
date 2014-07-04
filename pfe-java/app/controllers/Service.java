package controllers;

import com.google.inject.Singleton;
import models.AuctionRooms;
import models.Shop;
import models.SocialNetwork;
import models.Users;
import play.libs.ws.WSClient;
import play.libs.ws.WS;

@Singleton
public class Service {

    public final WSClient ws = WS.client();

    public final Shop shop = new Shop();

    public final AuctionRooms auctionRooms = new AuctionRooms();

    public final Users users = new Users();

    public final SocialNetwork socialNetwork = new SocialNetwork(ws);

}
