package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public interface Shop {

    Collection<Item> list();

    Item create(String name, Double price);

    Item get(Long id);

    Item update(Long id, String name, Double price);

    Boolean delete(Long id);

    public final static Shop Shop = new Shop() {

        SortedMap<Long, Item> items = new ConcurrentSkipListMap<>();
        AtomicLong seq = new AtomicLong();

        @Override
        public Collection<Item> list() {
            return new ArrayList<>(items.values());
        }

        @Override
        public Item create(String name, Double price) {
            final Long id = seq.incrementAndGet();
            final Item item = new Item(id, name, price);
            items.put(id, item);
            return item;
        }

        @Override
        public Item get(Long id) {
            return items.get(id);
        }

        @Override
        public synchronized Item update(Long id, String name, Double price) {
            Item item = items.get(id);
            if (item != null) {
                Item updated = new Item(id, name, price);
                items.put(id, updated);
                return updated;
            } else return null;
        }

        @Override
        public Boolean delete(Long id) {
            return items.remove(id) != null;
        }
    };

}
