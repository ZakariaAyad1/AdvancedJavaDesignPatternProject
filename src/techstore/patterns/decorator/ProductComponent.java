// techstore/patterns/decorator/ProductComponent.java
// This is the interface that both base products and decorators will implement.
package techstore.patterns.decorator;

public interface ProductComponent {
    int getId();
    String getName();
    String getDescription();
    double getPrice();
    // Potentially other common methods
}
