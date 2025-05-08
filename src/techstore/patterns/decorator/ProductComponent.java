// techstore/patterns/decorator/ProductComponent.java
// This is the interface that both base products and decorators will implement.
package techstore.patterns.decorator;

public interface ProductComponent {
    int getId();
    String getName();
    String getDescription();
    double getPrice();
    // Potentially other common methods
}

// Modify techstore.model.Product to implement this:
// public class Product implements ProductComponent { ... }
// Add getId(), getName(), getDescription(), getPrice() to Product if not already compatible.
// We've already designed Product with these methods, so this is straightforward.
// Just add `implements ProductComponent` to `Product.java`