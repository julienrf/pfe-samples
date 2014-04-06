package models;

public class Item {

    public final Long id;
    public final String name;
    public final Double price;

    public Item(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
