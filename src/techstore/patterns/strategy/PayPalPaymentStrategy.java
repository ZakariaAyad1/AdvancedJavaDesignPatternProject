// techstore/patterns/strategy/PayPalPaymentStrategy.java
package techstore.patterns.strategy;

import techstore.patterns.adapter.ExternalPaymentGateway; // Using Adapter
import techstore.patterns.adapter.PayPalAdapter;       // Using Adapter
import techstore.patterns.adapter.PayPalApi;           // Using Adapter

public class PayPalPaymentStrategy implements PaymentStrategy {
    private String email;
    private String password; // Highly insecure, just for demo
    private ExternalPaymentGateway paymentGateway; // Adapter

    public PayPalPaymentStrategy(String email, String password) {
        this.email = email;
        this.password = password;
        this.paymentGateway = new PayPalAdapter(new PayPalApi());
    }

    @Override
    public boolean pay(double amount) {
        // Simulate PayPal payment
        System.out.println("Processing PayPal payment for $" + amount);
        // Add payment logic here
        return true; // Return true for successful payment
    }
}