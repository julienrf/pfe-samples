package models;

import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.function.Function;

public class Shop {

    private <A> A withTransaction(Function<EntityManager, A> f) {
        try {
            return JPA.withTransaction(() -> f.apply(JPA.em()));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Collection<Item> list() {
        return withTransaction(em -> em.createQuery("SELECT i FROM Item i", Item.class).getResultList());
    }

    public Item create(String name, Double price) {
        return withTransaction(em -> {
            Item item = new Item(null, name, price);
            em.persist(item);
            return item;
        });
    }

    public Item get(Long id) {
        return withTransaction(em -> em.find(Item.class, id));
    }

    public Item update(Long id, String name, Double price) {
        return withTransaction(em -> {
            Item item = em.find(Item.class, id);
            if (item == null) {
                return null;
            } else {
                item.name = name;
                item.price = price;
                em.persist(item);
                return item;
            }
        });
    }

    public Boolean delete(Long id) {
        return withTransaction(em -> {
            Item item = em.find(Item.class, id);
            if (item != null) {
                em.remove(item);
                return true;
            } else return false;
        });
    }

}
