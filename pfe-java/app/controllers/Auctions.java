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

    public static class CreateBid {
        public Double price;
        public CreateBid() {}
    }

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
        CreateBid bid = Json.fromJson(request().body().asJson(), CreateBid.class);
        AuctionRooms.bid(id, AuthenticatedAction.getUsername(ctx()), bid.price);
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

    public static WebSocket<JsonNode> channel(Long id) throws Throwable {
        return Authentication.authenticated(ctx(), username -> WebSocket.<JsonNode>whenReady((in, out) -> {
            in.onMessage(json -> {
                CreateBid bid = Json.fromJson(json, CreateBid.class);
                AuctionRooms.bid(id, username, bid.price);
            });
            AuctionRooms
                    .subscribe(id, bid -> out.write(Json.toJson(bid)))
                    .onRedeem(stateAndSubscription -> {
                        for (AuctionRooms.Bid bid : stateAndSubscription._1) {
                            out.write(Json.toJson(bid));
                        }
                        in.onClose(stateAndSubscription._2::cancel);
                    });
        }), () -> WebSocket.reject(forbidden()));
    }

}
