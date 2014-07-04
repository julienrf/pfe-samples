package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.util.Timeout;
import play.libs.Akka;
import play.libs.F;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static akka.pattern.Patterns.ask;

/* In-memory publish/subscribe system */
public class AuctionRooms {

    final ActorRef ref = Akka.system().actorOf(Props.create(AuctionRoomsActor.class));
    final Timeout t = new Timeout(Duration.create(1, TimeUnit.SECONDS));

    /**
     * Subscribe to bid notifications for a given item
     * @param id Item id
     * @param subscriber Notification callback
     * @return A pair containing the current bids and a Subscription object (for un-subscribing)
     */
    public F.Promise<F.Tuple<List<Bid>, Subscription>> subscribe(Long id, Consumer<Bid> subscriber) {
        return F.Promise.wrap((Future)ask(ref, new AuctionRoomsActor.Subscribe(id, subscriber), t));
    }

    /**
     * Bid for an item
     * @param id Item id
     * @param name User name
     * @param price Bid price
     */
    public void bid(Long id, String name, Double price) {
        ref.tell(new AuctionRoomsActor.ItemBid(id, name, price), null);
    }

    public static class Subscription {
        private final Long id;
        private final Consumer<Bid> subscriber;
        private final ActorRef ref;

        public Subscription(ActorRef ref, Long id, Consumer<Bid> subscriber) {
            this.ref = ref;
            this.id = id;
            this.subscriber = subscriber;
        }

        /**
         * Tell the subscription system that this subscription is not anymore interested in receiving notifications
         */
        public void cancel() {
            ref.tell(new AuctionRoomsActor.Unsubscribe(id, subscriber), null);
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
