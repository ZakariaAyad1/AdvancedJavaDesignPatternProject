package techstore.model;

import techstore.patterns.decorator.ProductComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator; // For Iterator Pattern

public class ShoppingCart {
    private final Client client;
    private final List<CartItem> items;

    public ShoppingCart(Client client) {
        this.client = client;
        this.items = new ArrayList<>();
    }

    public void addProduct(ProductComponent product, int quantity) {
        for (CartItem item : items) {
            // If product (or its base if decorated) is already in cart, update quantity
            Product baseProduct = getBaseProduct(item.getProduct());
            Product newBaseProduct = getBaseProduct(product);
            if (baseProduct.getId() == newBaseProduct.getId() && item.getProduct().getClass() == product.getClass()) { // Ensure same decorators
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    private Product getBaseProduct(ProductComponent pc) {
        while (pc instanceof techstore.patterns.decorator.ProductDecorator) {
            pc = ((techstore.patterns.decorator.ProductDecorator) pc).getWrappedProduct();
        }
        return (Product) pc; // Assuming the base is always Product
    }


    public void removeProduct(int productId) {
        items.removeIf(item -> getBaseProduct(item.getProduct()).getId() == productId);
    }

    public void updateQuantity(int productId, int quantity) {
        for (CartItem item : items) {
            if (getBaseProduct(item.getProduct()).getId() == productId) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    items.remove(item);
                }
                return;
            }
        }
    }

    public List<CartItem> getItems() { // Allows for Iterator pattern usage (List.iterator())
        return new ArrayList<>(items); // Return a copy
    }

    public double getTotalCost() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void clearCart() {
        items.clear();
    }

    public Client getClient() { return client; }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}