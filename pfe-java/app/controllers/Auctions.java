package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.AuctionRooms;
import models.Item;
import play.libs.EventSource;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;

import static controllers.AuthenticatedAction.Authenticated;
import static controllers.DBAction.DB;
import static play.libs.EventSource.Event.event;

@Singleton
public class Auctions extends Controller {

    @Inject
    public Auctions(Service service) {
        super(service);
    }

    public static class CreateBid {
        public Double price;
        public CreateBid() {}
    }

    @Authenticated
    @DB
    public Result room(Long id) {
        Item item = service.shop().get(id);
        if (item != null) {
            return ok(views.html.auctionRoom.render(item));
        } else return notFound();
    }

    @Authenticated
    @BodyParser.Of(BodyParser.Json.class)
    public Result bid(Long id) {
        CreateBid bid = Json.fromJson(request().body().asJson(), CreateBid.class);
        service.auctionRooms.bid(id, AuthenticatedAction.getUsername(ctx()), bid.price);
        return ok();
    }

    @Authenticated
    public Result notifications(Long id) {
        return ok(EventSource.whenConnected(eventSource -> service.auctionRooms
                .subscribe(id, (bid) -> eventSource.send(event(Json.toJson(bid))))
                .onRedeem(stateAndSubscription -> {
                    for (AuctionRooms.Bid bid : stateAndSubscription._1) {
                        eventSource.send(event(Json.toJson(bid)));
                    }
                    eventSource.onDisconnected(stateAndSubscription._2::cancel);
        })));
    }

    @Authenticated
    @DB
    public Result roomWs(Long id) {
        Item item = service.shop.get(id);
        if (item != null) {
            return ok(views.html.auctionRoomWs.render(item));
        } else return notFound();
    }

    public WebSocket<JsonNode> channel(Long id) throws Throwable {
        return Authentication.authenticated(ctx(), username -> WebSocket.<JsonNode>whenReady((in, out) -> {
            in.onMessage(json -> {
                CreateBid bid = Json.fromJson(json, CreateBid.class);
                service.auctionRooms.bid(id, username, bid.price);
            });
            service.auctionRooms
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
