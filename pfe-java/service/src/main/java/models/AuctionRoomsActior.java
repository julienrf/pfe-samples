package models;

import akka.actor.UntypedActor;
import play.libs.F;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class AuctionRoomsActor extends UntypedActor {

    Map<Long, Room> rooms = new HashMap<>();

    static class Room {
        Map<String, Double> bids = new HashMap<>();
        List<Consumer<AuctionRooms.Bid>> subscribers = new ArrayList<>();

        void addBid(String name, Double price) {
            if (bids.values().stream().allMatch(p -> p < price)) {
                bids.put(name, price);
                subscribers.forEach(subscriber -> subscriber.accept(new AuctionRooms.Bid(name, price)));
            }
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Subscribe) {
            Subscribe subscribe = (Subscribe) message;
            Room room = lookupOrCreate(subscribe.id);
            room.subscribers.add(subscribe.subscriber);
            AuctionRooms.Subscription subscription = new AuctionRooms.Subscription(self(), subscribe.id, subscribe.subscriber);
            List<AuctionRooms.Bid> currentState = room.bids.entrySet().stream()
                    .map(e -> new AuctionRooms.Bid(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            sender().tell(new F.Tuple<>(currentState, subscription), self());
        } else if (message instanceof Unsubscribe) {
            Unsubscribe unsubscribe = (Unsubscribe) message;
            Room room = rooms.get(unsubscribe.id);
            if (room != null) {
                room.subscribers.remove(unsubscribe.subscriber);
            }
        } else if (message instanceof ItemBid) {
            ItemBid itemBid = (ItemBid) message;
            Room room = lookupOrCreate(itemBid.id);
            room.addBid(itemBid.name, itemBid.price);
        } else unhandled(message);
    }

    Room lookupOrCreate(Long id) {
        Room room = rooms.get(id);
        if (room == null) {
            room = new Room();
            rooms.put(id, room);
        }
        return room;
    }

    static class Subscribe {
        public final Long id;
        public final Consumer<AuctionRooms.Bid> subscriber;

        public Subscribe(Long id, Consumer<AuctionRooms.Bid> subscriber) {
            this.id = id;
            this.subscriber = subscriber;
        }
    }

    static class Unsubscribe {
        public final Long id;
        public final Consumer<AuctionRooms.Bid> subscriber;

        public Unsubscribe(Long id, Consumer<AuctionRooms.Bid> subscriber) {
            this.id = id;
            this.subscriber = subscriber;
        }
    }

    static class ItemBid {
        public final Long id;
        public final String name;
        public final Double price;

        public ItemBid(Long id, String name, Double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

}
