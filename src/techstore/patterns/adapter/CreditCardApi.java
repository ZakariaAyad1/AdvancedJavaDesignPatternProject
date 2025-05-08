package techstore.patterns.adapter;

public class CreditCardApi {
    public boolean chargeCreditCard(String cardNumber, String expiry, String cvv, double amount) {
        System.out.println("CreditCard API: Charged $" + String.format("%.2f", amount) + " to card ending " + cardNumber.substring(cardNumber.length()-4) + ".");
        return true; // Simulate success
    }
}