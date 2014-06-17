package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.AuctionRooms;
import models.Item;
import models.Shop;
import play.libs.EventSource;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import static controllers.AuthenticatedAction.Authenticated;
import static play.libs.EventSource.Event.event;

public class Auctions extends Controller {

    @Authenticated
    public static Result room(Long id) {
        Item item = Shop.Shop.get(id);
        if (item != null) {
            return ok(views.html.auctionRoom.render(item));
        } else return notFound();
    }

    @Authenticated
    @BodyParser.Of(BodyParser.Json.class)
    public static Result bid(Long id) {
        AuctionRooms.Bid bid = Json.fromJson(request().body().asJson(), AuctionRooms.Bid.class);
        AuctionRooms.bid(id, bid.name, bid.price);
        return ok();
    }

    @Authenticated
    public static Result notifications(Long id) {
        return ok(EventSource.whenConnected(eventSource -> AuctionRooms
                .subscribe(id, (bid) -> eventSource.send(event(Json.toJson(bid))))
                .onRedeem(stateAndSubscription -> {
                    for (AuctionRooms.Bid bid : stateAndSubscription._1) {
                        eventSource.send(event(Json.toJson(bid)));
                    }
                    eventSource.onDisconnected(stateAndSubscription._2::cancel);
        })));
    }

    @Authenticated
    public static Result roomWs(Long id) {
        Item item = Shop.Shop.get(id);
        if (item != null) {
            return ok(views.html.auctionRoomWs.render(item));
        } else return notFound();
    }

    public static WebSocket<JsonNode> channel(Long id) {
        return WebSocket.whenReady((in, out) -> {
            in.onMessage(json -> {
                AuctionRooms.Bid bid = Json.fromJson(json, AuctionRooms.Bid.class);
                AuctionRooms.bid(id, bid.name, bid.price);
            });
            AuctionRooms
                    .subscribe(id, bid -> out.write(Json.toJson(bid)))
                    .onRedeem(stateAndSubscription -> {
                        for (AuctionRooms.Bid bid : stateAndSubscription._1) {
                            out.write(Json.toJson(bid));
                        }
                        in.onClose(stateAndSubscription._2::cancel);
            });
        });
    }

}
