package techstore.patterns.decorator;

public class ExtendedWarrantyDecorator extends ProductDecorator {
    private static final double WARRANTY_COST_PER_YEAR = 20.00;
    private int extraYears;

    public ExtendedWarrantyDecorator(ProductComponent wrappedProduct, int extraYears) {
        super(wrappedProduct);
        this.extraYears = extraYears;
    }

    @Override
    public String getName() {
        return super.getName() + " (+" + extraYears + "yr Warranty)";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n  + Includes an additional " + extraYears + "-year extended warranty.";
    }

    @Override
    public double getPrice() {
        return super.getPrice() + (WARRANTY_COST_PER_YEAR * extraYears);
    }
}