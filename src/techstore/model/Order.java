package techstore.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import techstore.patterns.strategy.PaymentStrategy;

public class Order {
    private static int nextId = 1;
    private final int id;
    private final Client client;
    private final List<CartItem> orderedItems;
    private final double totalAmount;
    private final LocalDateTime orderDate;
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED
    private PaymentStrategy paymentStrategy; // Strategy Pattern
    private String rejectionReason;

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public Order(Client client, List<CartItem> orderedItems, double totalAmount) {
        this.id = nextId++;
        this.client = client;
        this.orderedItems = new ArrayList<>(orderedItems); // Copy items
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = STATUS_PENDING; // All new orders start as PENDING
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public List<CartItem> getOrderedItems() { return orderedItems; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        System.out.println("Order #" + id + " status updated to: " + status); // Debug line
    }
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) { this.paymentStrategy = paymentStrategy; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }

    public boolean processPayment() {
        if (paymentStrategy == null) {
            System.out.println("Payment strategy not set for order " + id);
            return false;
        }
        System.out.println("Processing payment for order " + id + " amount $" + String.format("%.2f", totalAmount));
        paymentStrategy.pay(totalAmount);
        // In a real app, payment success/failure would be handled
        this.status = STATUS_PAID;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(id)
                .append("\nClient: ").append(client.getUsername())
                .append("\nDate: ").append(orderDate.toLocalDate())
                .append("\nStatus: ").append(status)
                .append("\nTotal Amount: $").append(String.format("%.2f", totalAmount))
                .append("\nItems:");
        for (CartItem item : orderedItems) {
            sb.append("\n  - ").append(item.getProduct().getName()).append(" (x").append(item.getQuantity()).append(")");
        }
        return sb.toString();
    }
}