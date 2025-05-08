// techstore/patterns/templatemethod/OnlineOrderProcess.java
package techstore.patterns.templatemethod;

import techstore.model.Order;

public class OnlineOrderProcess extends OrderProcessingTemplate {
    @Override
    protected void updateInventory() {
        System.out.println("OnlineOrder: Updating inventory levels from warehouse.");
        // Logic to decrease stock counts
    }

    @Override
    protected void notifyClient(Order order) {
        System.out.println("OnlineOrder: Sending email confirmation to " + order.getClient().getEmail() + " for order " + order.getId());
    }

    // We can override other steps if needed, for example:
    @Override
    protected void finalizeOrder(Order order) {
        super.finalizeOrder(order); // Call base class logic
        order.setStatus("SHIPPED");
        System.out.println("OnlineOrder: Order " + order.getId() + " has been shipped.");
    }
}
// StorePickupOrderProcess could be another subclass with different inventory/notification