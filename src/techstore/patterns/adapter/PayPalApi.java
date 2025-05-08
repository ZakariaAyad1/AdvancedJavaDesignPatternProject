package techstore.patterns.adapter;

public class PayPalApi {
    public void sendPayment(String email, double totalAmount) {
        System.out.println("PayPal API: Payment of $" + String.format("%.2f", totalAmount) + " processed for " + email + ".");
    }
}