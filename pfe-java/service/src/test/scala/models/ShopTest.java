package models;

import org.junit.Test;

import static org.junit.Assert.*;
import static shop.ShopService.withShop;

public class ShopTest {

    @Test
    public void addItem() {
        withShop(shop -> {
            Item item = shop.create("Play Framework Essentials", 42.0);
            assertNotNull(item);
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void listItem() {
        withShop(shop -> {
            Integer previousSize = shop.list().size();
            shop.create("Play Framework Essentials", 42.0);
            assertEquals(previousSize + 1, shop.list().size());
            Item item = shop.list().toArray(new Item[1])[0];
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void getItem() {
        withShop(shop -> {
            Item createdItem = shop.create("Play Framework Essentials", 42.0);
            Item item = shop.get(createdItem.id);
            assertNotNull(item);
            assertEquals("Play Framework Essentials", item.name);
            assertEquals(new Double(42.0), item.price);
        });
    }

    @Test
    public void updateItem() {
        withShop(shop -> {
            Item createdItem = shop.create("Play Framework Essentials", 42.0);
            Item updatedItem = shop.update(createdItem.id, createdItem.name, 10.0);
            assertNotNull(updatedItem);
            assertEquals("Play Framework Essentials", updatedItem.name);
            assertEquals(new Double(10.0), updatedItem.price);
            Item item = shop.get(createdItem.id);
            assertEquals(updatedItem.name, item.name);
            assertEquals(updatedItem.price, item.price);
        });
    }

    @Test
    public void deleteItem() {
        withShop(shop -> {
            Item item = shop.create("Play Framework Essentials", 42.0);
            assertNotNull(shop.get(item.id));
            assertTrue(shop.delete(item.id));
            assertNull(shop.get(item.id));
        });
    }

}
