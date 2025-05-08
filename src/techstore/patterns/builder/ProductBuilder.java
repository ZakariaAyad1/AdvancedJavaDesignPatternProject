// techstore/patterns/builder/ProductBuilder.java
package techstore.patterns.builder;

import techstore.model.Product;
import techstore.model.Category;

public class ProductBuilder {
    private final int id; // ID is now mandatory for the builder
    private final String name; // Mandatory
    private final double price; // Mandatory

    private String description = ""; // Optional
    private Category category; // Optional, but good to have
    private String brand; // Optional
    private int warrantyMonths = 0; // Optional
    private double discount = 0.0; // Optional

    public ProductBuilder(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder category(Category category) {
        this.category = category;
        return this;
    }

    public ProductBuilder brand(String brand) {
        this.brand = brand;
        return this;
    }

    public ProductBuilder warrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    public ProductBuilder discount(double discount) {
        if (discount >= 0 && discount <= 1.0) { // e.g., 0.1 for 10%
            this.discount = discount;
        }
        return this;
    }

    public Product build() {
        return new Product(id, name, description, price, category, brand, warrantyMonths, discount);
    }
}