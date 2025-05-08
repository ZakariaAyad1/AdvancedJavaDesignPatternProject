// techstore/view/ConsoleView.java
package techstore.view;

import techstore.model.*;
import techstore.patterns.decorator.ProductComponent;

import java.util.List;

public class ConsoleView {
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products to display.");
            return;
        }
        System.out.println("\n--- Products ---");
        for (Product product : products) { // Iterator Pattern via List.forEach or for-loop
            System.out.println("ID: " + product.getId() + ", Name: " + product.getName() +
                    ", Price: $" + String.format("%.2f", product.getPrice()) +
                    (product.getCategory() != null ? ", Category: " + product.getCategory().getName() : ""));
        }
        System.out.println("-----------------");
    }

    public void displayProductDetails(ProductComponent product) { // Can display decorated product
        System.out.println("\n--- Product Details ---");
        System.out.println(product.toString()); // Relies on Product's or Decorator's toString()
        System.out.println("-----------------------");
    }

    public void displayShoppingCart(ShoppingCart cart) {
        System.out.println("\n--- Your Shopping Cart ---");
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
        } else {
            // Iterator Pattern via cart.getItems().forEach() or for-loop
            for (CartItem item : cart.getItems()) {
                System.out.println("- " + item.getProduct().getName() +
                        " (Qty: " + item.getQuantity() +
                        ") Price: $" + String.format("%.2f", item.getProduct().getPrice()) +
                        " Subtotal: $" + String.format("%.2f", item.getTotalPrice()));
            }
            System.out.println("Total Cart Value: $" + String.format("%.2f", cart.getTotalCost()));
        }
        System.out.println("--------------------------");
    }

    public void displayCategories(List<Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        System.out.println("\n--- Categories ---");
        for (Category category : categories) {
            System.out.println("ID: " + category.getId() + ", Name: " + category.getName());
        }
        System.out.println("------------------");
    }

    public void displayOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        System.out.println("\n--- Orders ---");
        for (Order order : orders) {
            System.out.println(order.toString()); // Using Order's toString()
            System.out.println("---");
        }
        System.out.println("--------------");
    }
    public void displayOrderDetails(Order order) {
        System.out.println("\n--- Order Details ---");
        System.out.println(order.toString());
        System.out.println("---------------------");
    }
}