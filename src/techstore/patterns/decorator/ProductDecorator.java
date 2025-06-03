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
