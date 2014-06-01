package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Timeout;
import play.libs.Akka;
import play.libs.F;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;

/* In-memory publish/subscribe system */
public class AuctionRooms extends UntypedActor {

    Map<Long, Room> rooms = new HashMap<>();

    static class Room {
        Map<String, Double> bids = new HashMap<>();
        List<Consumer<Bid>> subscribers = new ArrayList<>();

        void addBid(String name, Double price) {
            if (bids.values().stream().allMatch(p -> p < price)) {
                bids.put(name, price);
                subscribers.forEach(subscriber -> subscriber.accept(new Bid(name, price)));
            }
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Subscribe) {
            Subscribe subscribe = (Subscribe) message;
            Room room = lookupOrCreate(subscribe.id);
            room.subscribers.add(subscribe.subscriber);
            Subscription subscription = new Subscription(subscribe.id, subscribe.subscriber);
            List<Bid> currentState = room.bids.entrySet().stream()
                    .map(e -> new Bid(e.getKey(), e.getValue()))
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
        public final Consumer<Bid> subscriber;

        public Subscribe(Long id, Consumer<Bid> subscriber) {
            this.id = id;
            this.subscriber = subscriber;
        }
    }

    static class Unsubscribe {
        public final Long id;
        public final Consumer<Bid> subscriber;

        public Unsubscribe(Long id, Consumer<Bid> subscriber) {
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

    static final ActorRef ref = Akka.system().actorOf(Props.create(AuctionRooms.class));
    static final Timeout t = new Timeout(Duration.create(1, TimeUnit.SECONDS));

    // --- Public API

    /**
     * Subscribe to bid notifications for a given item
     * @param id Item id
     * @param subscriber Notification callback
     * @return A pair containing the current bids and a Subscription object (for un-subscribing)
     */
    public static F.Promise<F.Tuple<List<Bid>, Subscription>> subscribe(Long id, Consumer<Bid> subscriber) {
        return F.Promise.wrap((Future)ask(ref, new Subscribe(id, subscriber), t));
    }

    /**
     * Bid for an item
     * @param id Item id
     * @param name User name
     * @param price Bid price
     */
    public static void bid(Long id, String name, Double price) {
        ref.tell(new ItemBid(id, name, price), null);
    }

    public static class Subscription {
        private final Long id;
        private final Consumer<Bid> subscriber;

        public Subscription(Long id, Consumer<Bid> subscriber) {
            this.id = id;
            this.subscriber = subscriber;
        }

        /**
         * Tell the subscription system that this subscription is not anymore interested in receiving notifications
         */
        public void cancel() {
            ref.tell(new Unsubscribe(id, subscriber), null);
        }
    }

    public static class Bid {
        public String name;
        public Double price;

        public Bid() {}

        public Bid(String name, Double price) {
            this.name = name;
            this.price = price;
        }
    }

}
