package techstore.model;

// Product of UserFactory
public class Client extends User {
    private ShoppingCart shoppingCart;

    public Client(String username, String password, String email) {
        super(username, password, email);
        this.shoppingCart = new ShoppingCart(this); // Each client has their own cart
    }

    @Override
    public String getRole() {
        return "Client";
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }
}