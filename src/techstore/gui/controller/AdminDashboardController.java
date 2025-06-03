package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import techstore.MainApp;
import techstore.model.Admin;
import techstore.model.Order;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer; // Pour le status updater

public class AdminDashboardController {

    @FXML private Label adminWelcomeLabel;
    @FXML private BorderPane adminContentArea; // Zone où charger les autres vues admin

    // Statistics Labels
    @FXML private Label totalProductsLabel;
    @FXML private Label totalCategoriesLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label totalUsersLabel;
    
    // Order Status Distribution Labels
    @FXML private Label pendingOrdersLabel;
    @FXML private Label approvedOrdersLabel;
    @FXML private Label rejectedOrdersLabel;

    // Recent Orders Table
    @FXML private TableView<Order> recentOrdersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    @FXML private TableColumn<Order, Double> amountColumn;
    @FXML private TableColumn<Order, String> statusColumn;

    private MainApp mainApp; // Si besoin de naviguer ou accéder à la stage principale
    private Admin currentAdmin;
    private CatalogueService catalogueService; // Proxy
    private OrderManager orderManager;
    private UserManager userManager;
    private Consumer<String> statusUpdater; // Pour mettre à jour le statut dans MainController

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initData(MainApp mainApp, Admin admin, CatalogueService catalogueService, OrderManager orderManager, UserManager userManager, Consumer<String> statusUpdater) {
        this.mainApp = mainApp;
        this.currentAdmin = admin;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
        this.userManager = userManager;
        this.statusUpdater = statusUpdater;

        adminWelcomeLabel.setText("Welcome, Admin " + currentAdmin.getUsername() + "!");
        
        // Initialize statistics and recent orders
        updateStatistics();
        updateRecentOrders();

        // Set up periodic updates (every 30 seconds)
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(30),
                event -> {
                    updateStatistics();
                    updateRecentOrders();
                }
            )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();

        // Load the product management view by default
        handleManageProducts();
    }

    @FXML
    private void handleManageProducts() {
        loadAdminView("/techstore/gui/fxml/ProductManagementView.fxml", controller -> {
            if (controller instanceof ProductManagementController) {
                ((ProductManagementController) controller).initData(catalogueService, mainApp.getPrimaryStage());
            }
        });
        statusUpdater.accept("Product Management loaded");
    }

    @FXML
    private void handleManageCategories() {
        loadAdminView("/techstore/gui/fxml/CategoryManagementView.fxml", controller -> {
            if (controller instanceof CategoryManagementController) {
                ((CategoryManagementController) controller).initData(catalogueService);
            }
        });
        statusUpdater.accept("Category Management loaded");
    }

    @FXML
    private void handleViewAllOrders() {
        loadAdminView("/techstore/gui/fxml/ViewAllOrdersView.fxml", controller -> {
            if (controller instanceof ViewAllOrdersController) {
                ((ViewAllOrdersController) controller).initData(orderManager, mainApp.getPrimaryStage());
            }
        });
        statusUpdater.accept("Order Management loaded");
        
        // Update statistics after loading orders view
        updateStatistics();
        updateRecentOrders();
    }

    @FXML
    private void handleViewAllUsers() {
        loadAdminView("/techstore/gui/fxml/ViewAllUsersView.fxml", controller -> {
            if (controller instanceof ViewAllUsersController) {
                ((ViewAllUsersController) controller).initData(userManager);
            }
        });
        statusUpdater.accept("User Management loaded");
        
        // Update user statistics
        updateStatistics();
    }

    /**
     * Helper method to load FXML views into the admin content area
     * @param fxmlPath Path to the FXML file to load
     * @param postLoadAction Action to execute after loading to configure the controller
     */
    private void loadAdminView(String fxmlPath, Consumer<Object> postLoadAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewNode = loader.load();
            Object controller = loader.getController();
            if (postLoadAction != null) {
                postLoadAction.accept(controller);
            }
            adminContentArea.setCenter(viewNode);
        } catch (IOException e) {
            e.printStackTrace();
            String viewName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
            statusUpdater.accept("Error loading " + viewName + ": " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        // Initialize recent orders table columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClient().getUsername()));
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getOrderDate().format(DATE_FORMATTER)));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up status column styling
        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case Order.STATUS_APPROVED:
                            setStyle("-fx-text-fill: green;");
                            break;
                        case Order.STATUS_REJECTED:
                            setStyle("-fx-text-fill: red;");
                            break;
                        case Order.STATUS_PENDING:
                            setStyle("-fx-text-fill: orange;");
                            break;
                        case Order.STATUS_PAID:
                        case Order.STATUS_SHIPPED:
                        case Order.STATUS_COMPLETED:
                            setStyle("-fx-text-fill: blue;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void updateStatistics() {
        // Update total counts
        totalProductsLabel.setText(String.valueOf(catalogueService.getAllProducts().size()));
        totalCategoriesLabel.setText(String.valueOf(catalogueService.getAllCategories().size()));
        totalOrdersLabel.setText(String.valueOf(orderManager.getAllOrders().size()));
        totalUsersLabel.setText(String.valueOf(userManager.getAllUsers().size()));

        // Update order status distribution
        long pendingCount = orderManager.getAllOrders().stream()
            .filter(order -> Order.STATUS_PENDING.equals(order.getStatus()))
            .count();
        long approvedCount = orderManager.getAllOrders().stream()
            .filter(order -> Order.STATUS_APPROVED.equals(order.getStatus()) || 
                           Order.STATUS_PAID.equals(order.getStatus()) ||
                           Order.STATUS_SHIPPED.equals(order.getStatus()) || 
                           Order.STATUS_COMPLETED.equals(order.getStatus()))
            .count();
        long rejectedCount = orderManager.getAllOrders().stream()
            .filter(order -> Order.STATUS_REJECTED.equals(order.getStatus()))
            .count();

        pendingOrdersLabel.setText(String.valueOf(pendingCount));
        approvedOrdersLabel.setText(String.valueOf(approvedCount));
        rejectedOrdersLabel.setText(String.valueOf(rejectedCount));
    }

    private void updateRecentOrders() {
        // Get all orders and sort by date (most recent first)
        ObservableList<Order> recentOrders = FXCollections.observableArrayList(
            orderManager.getAllOrders()
        );
        recentOrders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

        // Take only the last 5 orders (or less if there are fewer orders)
        int endIndex = Math.min(5, recentOrders.size());
        recentOrdersTable.setItems(FXCollections.observableArrayList(
            recentOrders.subList(0, endIndex)
        ));
    }
}