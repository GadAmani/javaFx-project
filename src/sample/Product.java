package sample;

import javafx.beans.property.*;

public class Product {

    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty price;

    public Product(int id, String name, int quantity, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty priceProperty() { return price; }
}