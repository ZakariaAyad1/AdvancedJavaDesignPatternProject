package techstore.patterns.adapter;

public class CreditCardAdapter implements ExternalPaymentGateway {
    private CreditCardApi creditCardApi;
    // In a real app, these details would be securely obtained
    private String cardNumber = "xxxx-xxxx-xxxx-1234";
    private String expiryDate = "12/25";
    private String cvv = "123";

    public CreditCardAdapter(CreditCardApi creditCardApi) {
        this.creditCardApi = creditCardApi;
    }

    @Override
    public boolean processPayment(double amount) {
        return creditCardApi.chargeCreditCard(cardNumber, expiryDate, cvv, amount);
    }
}