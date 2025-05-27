package techstore.patterns.strategy;

public interface PaymentStrategy {
    boolean pay(double amount); // Changed to return boolean indicating success/failure
}