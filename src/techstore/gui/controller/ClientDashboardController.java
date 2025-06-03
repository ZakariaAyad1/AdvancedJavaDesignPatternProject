package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import techstore.MainApp;
import techstore.model.Client;
import techstore.model.Order;
import techstore.model.Product;
import techstore.patterns.singleton.OrderManager;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClientDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label cartItemsLabel;
    @FXML private Label totalSpentLabel;
    
    @FXML private TableView<Order> recentOrdersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, Double> orderTotalColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;
    
    @FXML private FlowPane featuredProductsPane;

    private MainApp mainApp;
    private Client currentClient;
    private CatalogueService catalogueService;
    private OrderManager orderManager;
    private Consumer<String> statusUpdater;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initData(MainApp mainApp, Client client, CatalogueService catalogueService, 
                        OrderManager orderManager, Consumer<String> statusUpdater) {
        this.mainApp = mainApp;
        this.currentClient = client;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
        this.statusUpdater = statusUpdater;

        // Initialize welcome message
        welcomeLabel.setText("Welcome back, " + client.getUsername() + "!");

        // Initialize table columns
        initializeTableColumns();
        
        // Load dashboard data
        updateStatistics();
        loadRecentOrders();
        loadFeaturedProducts();

        // Set up auto-refresh (every 30 seconds)
        setupAutoRefresh();
    }

    private void initializeTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDateColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getOrderDate().format(DATE_FORMATTER),
                javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue())
            )
        );
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void updateStatistics() {
        // Update total orders
        long totalOrders = orderManager.getAllOrders().stream()
                .filter(order -> order.getClient().getId() == currentClient.getId())
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        // Update cart items
        int cartItems = currentClient.getShoppingCart().getItems().size();
        cartItemsLabel.setText(String.valueOf(cartItems));

        // Update total spent - include all completed order statuses
        double totalSpent = orderManager.getAllOrders().stream()
                .filter(order -> order.getClient().getId() == currentClient.getId())
                .filter(order -> Order.STATUS_APPROVED.equals(order.getStatus()) ||
                        Order.STATUS_PAID.equals(order.getStatus()) ||
                        Order.STATUS_SHIPPED.equals(order.getStatus()) ||
                        Order.STATUS_COMPLETED.equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        totalSpentLabel.setText(String.format("$%.2f", totalSpent));
    }

    private void loadRecentOrders() {
        List<Order> clientOrders = orderManager.getAllOrders().stream()
                .filter(order -> order.getClient().getId() == currentClient.getId())
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .collect(Collectors.toList());

        recentOrdersTable.setItems(FXCollections.observableArrayList(clientOrders));
    }

    private void loadFeaturedProducts() {
        featuredProductsPane.getChildren().clear();
        List<Product> featuredProducts = catalogueService.getAllProducts().stream()
                .filter(product -> product.getDiscount() > 0)
                .limit(4)
                .collect(Collectors.toList());

        for (Product product : featuredProducts) {
            VBox productCard = createProductCard(product);
            featuredProductsPane.getChildren().add(productCard);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> handleViewProductDetails(product));

        card.getChildren().addAll(nameLabel, priceLabel, viewButton);
        return card;
    }

    private void setupAutoRefresh() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(30),
                event -> {
                    updateStatistics();
                    loadRecentOrders();
                    loadFeaturedProducts();
                }
            )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleViewAllOrders() {
        // Delegate to ClientViewController to show all orders
        if (mainApp != null) {
            statusUpdater.accept("Loading order history...");
            loadView("/techstore/gui/fxml/MyOrdersView.fxml");
        }
    }

    @FXML
    private void handleBrowseProducts() {
        statusUpdater.accept("Loading products...");
        loadView("/techstore/gui/fxml/ClientProductView.fxml");
    }

    @FXML
    private void handleViewCart() {
        statusUpdater.accept("Loading shopping cart...");
        loadView("/techstore/gui/fxml/CartView.fxml");
    }

    private void handleViewProductDetails(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Product Details: " + product.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ProductDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product, catalogueService.getAllCategories());
            controller.setReadOnly();

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load product details.");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            if (loader.getController() instanceof MyOrdersController) {
                ((MyOrdersController) loader.getController()).initData(currentClient, orderManager, mainApp.getPrimaryStage());
            } else if (loader.getController() instanceof ClientProductViewController) {
                ((ClientProductViewController) loader.getController()).initData(currentClient, catalogueService, mainApp.getPrimaryStage(), statusUpdater);
            } else if (loader.getController() instanceof CartController) {
                ((CartController) loader.getController()).initData(currentClient, orderManager, mainApp.getPrimaryStage(), statusUpdater);
            }

            // Find the clientContentArea in the parent scene and set the new view
            Scene scene = welcomeLabel.getScene();
            BorderPane clientView = (BorderPane) scene.lookup("#clientView");
            if (clientView != null) {
                BorderPane contentArea = (BorderPane) clientView.lookup("#clientContentArea");
                if (contentArea != null) {
                    contentArea.setCenter(view);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the requested view.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
