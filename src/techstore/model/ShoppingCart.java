package techstore.model;

import techstore.patterns.decorator.ProductComponent;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final Client client;
    private final List<CartItem> items;

    public ShoppingCart(Client client) {
        this.client = client;
        this.items = new ArrayList<>();
    }    public void addProduct(ProductComponent product, int quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid product or quantity");
        }

        // First find if an identical product configuration exists
        CartItem existingItem = items.stream()
            .filter(item -> {
                // Check if products have same base ID and same decorator chain
                ProductComponent existing = item.getProduct();
                return isSameProductConfiguration(existing, product);
            })
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            // Update quantity of existing item
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add as new item
            items.add(new CartItem(product, quantity));
        }
    }

    private boolean isSameProductConfiguration(ProductComponent p1, ProductComponent p2) {
        // If they're not the same type, they can't be the same configuration
        if (!p1.getClass().equals(p2.getClass())) {
            return false;
        }

        // If they're base products, compare IDs
        if (p1 instanceof Product && p2 instanceof Product) {
            return ((Product) p1).getId() == ((Product) p2).getId();
        }

        // If they're decorators, check if they wrap the same type of product
        if (p1 instanceof techstore.patterns.decorator.ProductDecorator &&
            p2 instanceof techstore.patterns.decorator.ProductDecorator) {
            return isSameProductConfiguration(
                ((techstore.patterns.decorator.ProductDecorator) p1).getWrappedProduct(),
                ((techstore.patterns.decorator.ProductDecorator) p2).getWrappedProduct()
            );
        }

        return false;
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