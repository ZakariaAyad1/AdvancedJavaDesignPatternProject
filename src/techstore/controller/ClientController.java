package techstore.controller;

import techstore.model.*;
import techstore.patterns.decorator.ExtendedWarrantyDecorator;
import techstore.patterns.decorator.GiftWrapDecorator;
import techstore.patterns.decorator.ProductComponent;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.strategy.CreditCardPaymentStrategy;
import techstore.patterns.strategy.PayPalPaymentStrategy;
import techstore.patterns.strategy.PaymentStrategy;
import techstore.patterns.templatemethod.OnlineOrderProcess;
import techstore.patterns.templatemethod.OrderProcessingTemplate;
import techstore.service.CatalogueService;
import techstore.view.ConsoleView;
import techstore.view.InputUtil;

import java.util.List;
import java.util.Optional;

public class ClientController {
    private final ConsoleView view;
    private final Client client;
    private final CatalogueService catalogueService; // Proxy (read-only access for client)
    private final OrderManager orderManager;
    private boolean logout = false;

    public ClientController(ConsoleView view, Client client, CatalogueService catalogueService, OrderManager orderManager) {
        this.view = view;
        this.client = client;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
    }

    public boolean isLogout() {
        return logout;
    }

    public void showClientMenu() {
        logout = false;
        while (!logout) {
            view.displayMessage("\n--- Client Menu (" + client.getUsername() + ") ---");
            view.displayMessage("1. View Products");
            view.displayMessage("2. View Product Details");
            view.displayMessage("3. Add Product to Cart");
            view.displayMessage("4. View Shopping Cart");
            view.displayMessage("5. Update Cart Item Quantity");
            view.displayMessage("6. Remove Item from Cart");
            view.displayMessage("7. Checkout");
            view.displayMessage("8. View My Orders");
            view.displayMessage("9. Logout");
            int choice = InputUtil.getInt("Enter choice: ");

            switch (choice) {
                case 1: viewProducts(); break;
                case 2: viewProductDetails(); break;
                case 3: addProductToCart(); break;
                case 4: view.displayShoppingCart(client.getShoppingCart()); break;
                case 5: updateCartItemQuantity(); break;
                case 6: removeItemFromCart(); break;
                case 7: checkout(); break;
                case 8: viewMyOrders(); break;
                case 9: logout = true; view.displayMessage("Logging out..."); break;
                default: view.displayMessage("Invalid choice.");
            }
        }
    }

    private void viewProducts() {
        view.displayMessage("Available Products:");
        view.displayProducts(catalogueService.getAllProducts()); // Iterator used by displayProducts
    }

    private void viewProductDetails() {
        viewProducts();
        int productId = InputUtil.getInt("Enter Product ID to view details: ");
        Optional<Product> productOpt = catalogueService.findProductById(productId);
        if (productOpt.isPresent()) {
            view.displayProductDetails(productOpt.get()); // Display base product
        } else {
            view.displayMessage("Product not found.");
        }
    }

    private void addProductToCart() {
        viewProducts();
        int productId = InputUtil.getInt("Enter Product ID to add to cart: ");
        Optional<Product> productOpt = catalogueService.findProductById(productId);

        if (productOpt.isPresent()) {
            Product baseProduct = productOpt.get();
            ProductComponent productToAdd = baseProduct; // Start with base product

            int quantity = InputUtil.getInt("Enter quantity: ");
            if (quantity <= 0) {
                view.displayMessage("Quantity must be positive.");
                return;
            }

            // Decorator Pattern in action
            String giftWrapChoice = InputUtil.getString("Add gift wrap? ($5.00 extra) (yes/no): ").toLowerCase();
            if ("yes".equals(giftWrapChoice)) {
                productToAdd = new GiftWrapDecorator(productToAdd);
            }

            String warrantyChoice = InputUtil.getString("Add extended warranty? ($20.00/year extra) (yes/no): ").toLowerCase();
            if ("yes".equals(warrantyChoice)) {
                int years = InputUtil.getInt("Enter number of extra warranty years: ");
                if (years > 0) {
                    productToAdd = new ExtendedWarrantyDecorator(productToAdd, years);
                }
            }

            client.getShoppingCart().addProduct(productToAdd, quantity);
            view.displayMessage(productToAdd.getName() + " (x" + quantity + ") added to cart.");
            view.displayShoppingCart(client.getShoppingCart());
        } else {
            view.displayMessage("Product not found.");
        }
    }

    private void updateCartItemQuantity() {
        view.displayShoppingCart(client.getShoppingCart());
        if (client.getShoppingCart().isEmpty()) return;

        int productId = InputUtil.getInt("Enter Product ID from your cart to update quantity: ");
        // Note: This simple ID check might not distinguish between decorated versions of the same base product.
        // A more robust cart would handle this, perhaps by cart item index.
        // For now, we assume client means the base product ID.

        // Check if product (base) is in cart
        boolean productInCart = client.getShoppingCart().getItems().stream()
                .anyMatch(item -> getBaseProductId(item.getProduct()) == productId);

        if (!productInCart) {
            view.displayMessage("Product ID " + productId + " not found in your cart.");
            return;
        }

        int newQuantity = InputUtil.getInt("Enter new quantity (0 to remove): ");
        client.getShoppingCart().updateQuantity(productId, newQuantity);
        view.displayMessage("Cart updated.");
        view.displayShoppingCart(client.getShoppingCart());
    }

    private int getBaseProductId(ProductComponent pc) {
        while (pc instanceof techstore.patterns.decorator.ProductDecorator) {
            pc = ((techstore.patterns.decorator.ProductDecorator) pc).getWrappedProduct();
        }
        // Assuming the base is always Product and has getId()
        if (pc instanceof Product) {
            return ((Product) pc).getId();
        }
        return -1; // Should not happen with current setup
    }


    private void removeItemFromCart() {
        view.displayShoppingCart(client.getShoppingCart());
        if (client.getShoppingCart().isEmpty()) return;

        int productId = InputUtil.getInt("Enter Product ID to remove from cart: ");
        // Similar to update, this removes based on base product ID.
        client.getShoppingCart().removeProduct(productId);
        view.displayMessage("Item removed if it was in the cart.");
        view.displayShoppingCart(client.getShoppingCart());
    }


    private void checkout() {
        ShoppingCart cart = client.getShoppingCart();
        if (cart.isEmpty()) {
            view.displayMessage("Your cart is empty. Nothing to checkout.");
            return;
        }
        view.displayShoppingCart(cart);
        view.displayMessage("Total amount: $" + String.format("%.2f", cart.getTotalCost()));

        view.displayMessage("Select Payment Method:");
        view.displayMessage("1. Credit Card");
        view.displayMessage("2. PayPal");
        int paymentChoice = InputUtil.getInt("Enter choice: ");

        PaymentStrategy paymentStrategy = null; // Strategy Pattern
        // Adapter pattern is used within these strategies
        switch (paymentChoice) {
            case 1:
                String ccNum = InputUtil.getString("Enter Credit Card Number: ");
                String cvv = InputUtil.getString("Enter CVV: ");
                String expiry = InputUtil.getString("Enter Expiry Date (MM/YY): ");
                paymentStrategy = new CreditCardPaymentStrategy(ccNum, cvv, expiry);
                break;
            case 2:
                String email = InputUtil.getString("Enter PayPal Email: ");
                String pwd = InputUtil.getString("Enter PayPal Password (for demo only): ");
                paymentStrategy = new PayPalPaymentStrategy(email, pwd);
                break;
            default:
                view.displayMessage("Invalid payment method. Checkout cancelled.");
                return;
        }

        Order order = new Order(client, cart.getItems(), cart.getTotalCost());

        // Template Method Pattern
        OrderProcessingTemplate orderProcess = new OnlineOrderProcess();
        orderProcess.processOrder(order, paymentStrategy); // This calls strategy's pay method

        if ("PAID".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
            orderManager.placeOrder(order); // Observer will notify admin
            view.displayMessage("Order " + order.getId() + " placed successfully!");
            cart.clearCart();
        } else {
            view.displayMessage("Order processing failed. Status: " + order.getStatus());
        }
    }

    private void viewMyOrders() {
        List<Order> clientOrders = orderManager.getAllOrders().stream()
                .filter(o -> o.getClient().getId() == client.getId())
                .collect(java.util.stream.Collectors.toList());

        if (clientOrders.isEmpty()) {
            view.displayMessage("You have no orders.");
            return;
        }
        view.displayOrders(clientOrders);
    }
}