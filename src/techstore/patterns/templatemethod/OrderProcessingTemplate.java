package techstore.patterns.templatemethod;

import techstore.model.Order;
import techstore.patterns.strategy.PaymentStrategy;

public abstract class OrderProcessingTemplate {
    // The template method
    public final void processOrder(Order order, PaymentStrategy paymentStrategy) {
        initializeOrder(order);
        assignPaymentMethod(order, paymentStrategy);
        if (validatePayment(order)) {
            confirmPayment(order);
            updateInventory(); // Abstract step
            notifyClient(order); // Abstract step
            finalizeOrder(order);
        } else {
            handlePaymentFailure(order);
        }
    }

    protected void initializeOrder(Order order) {
        System.out.println("Template: Initializing order " + order.getId());
        order.setStatus("PROCESSING");
    }

    protected void assignPaymentMethod(Order order, PaymentStrategy paymentStrategy) {
        System.out.println("Template: Assigning payment method to order " + order.getId());
        order.setPaymentStrategy(paymentStrategy);
    }

    protected boolean validatePayment(Order order) {
        System.out.println("Template: Validating payment for order " + order.getId());
        // This would call the payment gateway via the strategy
        return order.processPayment(); // Uses Strategy pattern here
    }

    protected void confirmPayment(Order order) {
        System.out.println("Template: Payment confirmed for order " + order.getId());
        order.setStatus("PAID");
    }

    // Abstract methods to be implemented by subclasses
    protected abstract void updateInventory();
    protected abstract void notifyClient(Order order);

    protected void finalizeOrder(Order order) {
        System.out.println("Template: Finalizing order " + order.getId());
        order.setStatus("COMPLETED"); // Or SHIPPED depending on context
    }

    protected void handlePaymentFailure(Order order) {
        System.out.println("Template: Payment failed for order " + order.getId());
        order.setStatus("PAYMENT_FAILED");
    }
}