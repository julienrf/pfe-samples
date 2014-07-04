package models;

import org.junit.Test;

import static org.junit.Assert.*;
import static shop.ShopApplication.withApplication;

public class ShopTest {

    @Test
    public void addItem() {
        withApplication((service) -> {
            Item item = service.shop.create("Play Framework Essentials", 42.0);
            assertNotNull(item);
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void listItem() {
        withApplication((service) -> {
            Integer previousSize = service.shop.list().size();
            service.shop.create("Play Framework Essentials", 42.0);
            assertEquals(previousSize + 1, service.shop.list().size());
            Item item = service.shop.list().toArray(new Item[1])[0];
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void getItem() {
        withApplication((service) -> {
            Item createdItem = service.shop.create("Play Framework Essentials", 42.0);
            Item item = service.shop.get(createdItem.id);
            assertNotNull(item);
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void updateItem() {
        withApplication((service) -> {
            Item createdItem = service.shop.create("Play Framework Essentials", 42.0);
            Item updatedItem = service.shop.update(createdItem.id, createdItem.name, 10.0);
            assertNotNull(updatedItem);
            assertEquals("Play Framework Essentials", updatedItem.name);
            assertEquals(new Double(10.0), updatedItem.price);
            Item item = service.shop.get(createdItem.id);
            assertEquals(updatedItem.name, item.name);
            assertEquals(updatedItem.price, item.price);
        });
    }

    @Test
    public void deleteItem() {
        withApplication((service) -> {
            Item item = service.shop.create("Play Framework Essentials", 42.0);
            assertNotNull(service.shop.get(item.id));
            assertTrue(service.shop.delete(item.id));
            assertNull(service.shop.get(item.id));
        });
    }

}
