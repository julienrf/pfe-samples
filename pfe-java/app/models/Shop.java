package models;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.japi.STM;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public interface Shop {

    Collection<Item> list();

    Item create(String name, Double price);

    Item get(Long id);

    Item update(Long id, String name, Double price);

    Boolean delete(Long id);

    public final static Shop Shop = new Shop() {

        Ref.View<SortedMap<Long, Item> > items = STM.<SortedMap<Long, Item> >newRef(new TreeMap<>());
        Ref.View<Long> seq = STM.newRef(0L);

        @Override
        public Collection<Item> list() {
            return items.get().values();
        }

        @Override
        public Item create(String name, Double price) {
            final Long id = STM.transformAndGet(seq, new STM.Transformer<Long>() {
                @Override
                public Long apply(Long v) {
                    return v + 1;
                }
            });
            final Item item = new Item(id, name, price);
            STM.transform(items, new STM.Transformer<SortedMap<Long, Item>>() {
                @Override
                public SortedMap<Long, Item> apply(SortedMap<Long, Item> v) {
                    SortedMap<Long, Item> map = new TreeMap<>(v);
                    map.put(id, item);
                    return map;
                }
            });
            return item;
        }

        @Override
        public Item get(Long id) {
            return items.get().get(id);
        }

        @Override
        public Item update(Long id, String name, Double price) {
            return STM.atomic(() -> {
                Item item = items.get().get(id);
                if (item != null) {
                    Item updated = new Item(id, name, price);
                    STM.transform(items, new STM.Transformer<SortedMap<Long, Item>>() {
                        @Override
                        public SortedMap<Long, Item> apply(SortedMap<Long, Item> map) {
                            map.put(id, updated);
                            return map;
                        }
                    });
                    return updated;
                } else return null;
            });
        }

        @Override
        public Boolean delete(Long id) {
            return STM.atomic(() -> {
                if (items.get().containsKey(id)) {
                    STM.transform(items, new STM.Transformer<SortedMap<Long, Item>>() {
                        @Override
                        public SortedMap<Long, Item> apply(SortedMap<Long, Item> map) {
                            map.remove(id);
                            return map;
                        }
                    });
                    return true;
                } else return false;
            });
        }
    };

}
