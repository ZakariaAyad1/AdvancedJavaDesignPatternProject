package techstore.patterns.adapter;

public class PayPalAdapter implements ExternalPaymentGateway {
    private PayPalApi payPalApi;
    // In a real app, you'd get user's PayPal email somehow
    private String userPayPalEmail = "user@example.com";

    public PayPalAdapter(PayPalApi payPalApi) {
        this.payPalApi = payPalApi;
    }

    @Override
    public boolean processPayment(double amount) {
        payPalApi.sendPayment(userPayPalEmail, amount);
        return true; // Simulate success
    }
}