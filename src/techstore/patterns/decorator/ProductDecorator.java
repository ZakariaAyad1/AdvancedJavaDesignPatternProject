// Modify techstore.model.Product to implement this:
// public class Product implements ProductComponent { ... }
// Add getId(), getName(), getDescription(), getPrice() to Product if not already compatible.
// We've already designed Product with these methods, so this is straightforward.
// Just add `implements ProductComponent` to `Product.java`

// techstore/patterns/decorator/ProductDecorator.java
package techstore.patterns.decorator;

public abstract class ProductDecorator implements ProductComponent {
    protected ProductComponent wrappedProduct;

    public ProductDecorator(ProductComponent wrappedProduct) {
        this.wrappedProduct = wrappedProduct;
    }

    @Override
    public int getId() {
        return wrappedProduct.getId();
    }

    @Override
    public String getName() {
        return wrappedProduct.getName();
    }

    @Override
    public String getDescription() {
        return wrappedProduct.getDescription();
    }

    @Override
    public double getPrice() {
        return wrappedProduct.getPrice();
    }

    public ProductComponent getWrappedProduct() { // Helper to unwrap if needed
        return wrappedProduct;
    }
}
