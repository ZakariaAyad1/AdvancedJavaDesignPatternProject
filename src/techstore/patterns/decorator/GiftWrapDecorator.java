package techstore.patterns.decorator;

public class GiftWrapDecorator extends ProductDecorator {
    private static final double GIFT_WRAP_COST = 5.00;

    public GiftWrapDecorator(ProductComponent wrappedProduct) {
        super(wrappedProduct);
    }

    @Override
    public String getName() {
        return super.getName() + " (Gift Wrapped)";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n  + Includes premium gift wrapping.";
    }

    @Override
    public double getPrice() {
        return super.getPrice() + GIFT_WRAP_COST;
    }
}