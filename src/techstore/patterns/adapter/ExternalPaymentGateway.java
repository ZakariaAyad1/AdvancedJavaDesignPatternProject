package techstore.patterns.adapter;

public interface ExternalPaymentGateway {
    boolean processPayment(double amount);
}