package techstore.model;

import techstore.patterns.decorator.ProductComponent;

public class CartItem {
    private ProductComponent product; // Can hold a base Product or a DecoratedProduct
    private int quantity;

    public CartItem(ProductComponent product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductComponent getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return product.getPrice() * quantity; }

    @Override
    public String toString() {
        return product.getName() + " (x" + quantity + ") - $" + String.format("%.2f", getTotalPrice());
    }
}