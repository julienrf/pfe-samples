package models;

import org.junit.Test;
import play.test.FakeApplication;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

public class ShopTest extends WithApplication {

    @Override
    protected FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    @Test
    public void addItem() {
        Shop shop = new Shop();
        Item item = shop.create("Play Framework Essentials", 42.0);
        assertNotNull(item);
        assertEquals("Play Framework Essentials", item.name);
        assertEquals(new Double(42.0), item.price);
    }

    @Test
    public void listItem() {
        Shop shop = new Shop();
        Integer previousSize = shop.list().size();
        shop.create("Play Framework Essentials", 42.0);
        assertEquals(previousSize + 1, shop.list().size());
        Item item = shop.list().toArray(new Item[1])[0];
        assertEquals("Play Framework Essentials", item.name);
        assertEquals(new Double(42.0), item.price);
    }

    @Test
    public void getItem() {
        Shop shop = new Shop();
        Item createdItem = shop.create("Play Framework Essentials", 42.0);
        Item item = shop.get(createdItem.id);
        assertNotNull(item);
        assertEquals("Play Framework Essentials", item.name);
        assertEquals(new Double(42.0), item.price);
    }

    @Test
    public void updateItem() {
        Shop shop = new Shop();
        Item createdItem = shop.create("Play Framework Essentials", 42.0);
        Item updatedItem = shop.update(createdItem.id, createdItem.name, 10.0);
        assertNotNull(updatedItem);
        assertEquals("Play Framework Essentials", updatedItem.name);
        assertEquals(new Double(10.0), updatedItem.price);
        Item item = shop.get(createdItem.id);
        assertEquals(updatedItem.name, item.name);
        assertEquals(updatedItem.price, item.price);
    }

    @Test
    public void deleteItem() {
        Shop shop = new Shop();
        Item item = shop.create("Play Framework Essentials", 42.0);
        assertNotNull(shop.get(item.id));
        assertTrue(shop.delete(item.id));
        assertNull(shop.get(item.id));
    }

}
