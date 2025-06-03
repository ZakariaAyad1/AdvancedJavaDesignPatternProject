package techstore.patterns.factory;

import techstore.model.Product;
import techstore.model.Category;
import techstore.patterns.builder.ProductBuilder;
import techstore.patterns.singleton.CatalogueManager;


public class ProductFactory {
    private static final String DEFAULT_IMAGE_PATH = "images/default-product.png";

    public Product createProduct(String name, String description, double price, Category category,
                                 String brand, int warrantyMonths, double discount, String imagePath) {
        // The factory can use the builder for complex object creation
        return new ProductBuilder(CatalogueManager.getInstance().getNextProductId(), name, price) // ID is now handled here
                .description(description)
                .category(category)
                .brand(brand)
                .warrantyMonths(warrantyMonths)
                .discount(discount)
                .imagePath(imagePath)
                .build();
    }
    // A simpler version if some fields are mandatory for a basic product
    public Product createBasicProduct(String name, double price, Category category) {
        return new ProductBuilder(CatalogueManager.getInstance().getNextProductId(), name, price)
                .category(category)
                .imagePath(DEFAULT_IMAGE_PATH)
                .build();
    }
}