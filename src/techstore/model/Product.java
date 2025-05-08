// techstore/model/Product.java
package techstore.model;

import techstore.patterns.decorator.ProductComponent;

import java.util.Objects;

// Part of Model (MVC)
// Base for Decorator (will implement ProductComponent)
// Built by Builder
public class Product implements ProductComponent { // Will implement ProductComponent for Decorator
    private final int id;
    private String name;
    private String description;
    private double price;
    private Category category;
    private String brand; // Optional, for Builder
    private int warrantyMonths; // Optional, for Builder
    private double discount; // Optional, for Builder

    // Constructor for Builder
    public Product(int id, String name, String description, double price, Category category,
                   String brand, int warrantyMonths, double discount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
        this.discount = discount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() {
        return price * (1 - discount); // Apply discount
    }
    public void setPrice(double price) { this.price = price; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }
    public double getDiscount() { return discount; }


    @Override
    public String toString() {
        return "Product ID: " + id +
                "\n  Name: " + name +
                "\n  Description: " + description +
                "\n  Price: $" + String.format("%.2f", getPrice()) +
                (brand != null ? "\n  Brand: " + brand : "") +
                (warrantyMonths > 0 ? "\n  Warranty: " + warrantyMonths + " months" : "") +
                (discount > 0 ? "\n  Discount: " + (discount * 100) + "%" : "") +
                "\n  Category: " + (category != null ? category.getName() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}