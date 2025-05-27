// techstore/patterns/strategy/CreditCardPaymentStrategy.java
package techstore.patterns.strategy;

import techstore.patterns.adapter.CreditCardAdapter; // Using Adapter
import techstore.patterns.adapter.ExternalPaymentGateway; // Using Adapter
import techstore.patterns.adapter.CreditCardApi; // Using Adapter

public class CreditCardPaymentStrategy implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private ExternalPaymentGateway paymentGateway; // Adapter

    public CreditCardPaymentStrategy(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        // In a real app, you might inject this or get from a factory
        this.paymentGateway = new CreditCardAdapter(new CreditCardApi());
    }

    @Override
    public boolean pay(double amount) {
        // Simulate credit card payment
        System.out.println("Processing credit card payment for $" + amount);
        // Add payment logic here
        return true; // Return true for successful payment
    }
}