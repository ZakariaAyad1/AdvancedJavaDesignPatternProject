package techstore.patterns.singleton;

import techstore.model.Order;
import techstore.patterns.observer.NotificationService; // For Observer

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orders;
    private NotificationService notificationService; // Observer Pattern

    private OrderManager() {
        orders = new ArrayList<>();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void placeOrder(Order order) {
        orders.add(order);
        if (notificationService != null) {
            notificationService.notifyObservers("New order placed: ID " + order.getId() + " by " + order.getClient().getUsername());
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    public Optional<Order> findOrderById(int orderId) {
        return orders.stream().filter(o -> o.getId() == orderId).findFirst();
    }
}