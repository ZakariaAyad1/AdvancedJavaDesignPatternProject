package techstore.gui.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.CartItem;
import techstore.model.Client;
import techstore.model.Order;
import techstore.model.Product; // Pour le getBaseProduct
import techstore.patterns.decorator.ProductComponent;
import techstore.patterns.decorator.ProductDecorator; // Pour le getBaseProduct
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.strategy.PaymentStrategy;
import techstore.patterns.templatemethod.OnlineOrderProcess;
import techstore.patterns.templatemethod.OrderProcessingTemplate;


import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> productNameColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> unitPriceColumn;
    @FXML private TableColumn<CartItem, Double> subtotalColumn;
    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;


    private Client currentClient;
    private OrderManager orderManager;
    private Stage ownerStage;
    private ObservableList<CartItem> cartItemsList;
    private Consumer<String> statusUpdater;


    public void initData(Client client, OrderManager orderManager, Stage ownerStage, Consumer<String> statusUpdater) {
        this.currentClient = client;
        this.orderManager = orderManager;
        this.ownerStage = ownerStage;
        this.statusUpdater = statusUpdater;

        // CellValueFactory pour les colonnes
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        loadCartItems();
        updateTotal();
    }

    private void loadCartItems() {
        cartItemsList = FXCollections.observableArrayList(currentClient.getShoppingCart().getItems());
        cartTable.setItems(cartItemsList);
        checkoutButton.setDisable(cartItemsList.isEmpty());
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: $%.2f", currentClient.getShoppingCart().getTotalCost()));
    }

    private Product getBaseProduct(ProductComponent pc) {
        while (pc instanceof ProductDecorator) {
            pc = ((ProductDecorator) pc).getWrappedProduct();
        }
        return (Product) pc; // Assumant que la base est toujours Product
    }

    @FXML
    private void handleUpdateQuantity() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedItem.getQuantity()));
            dialog.setTitle("Update Quantity");
            dialog.setHeaderText("Update quantity for: " + selectedItem.getProduct().getName());
            dialog.setContentText("New quantity:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(qtyStr -> {
                try {
                    int newQuantity = Integer.parseInt(qtyStr);
                    if (newQuantity < 0) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity cannot be negative.");
                        return;
                    }
                    // L'ID du produit de base est nécessaire pour la méthode updateQuantity du panier
                    int baseProductId = getBaseProduct(selectedItem.getProduct()).getId();
                    currentClient.getShoppingCart().updateQuantity(baseProductId, newQuantity);
                    refreshCartView();
                    statusUpdater.accept("Cart updated.");
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for quantity.");
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to update.");
        }
    }

    @FXML
    private void handleRemoveItem() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Remove " + selectedItem.getProduct().getName() + " from cart?", ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Confirm Removal");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                int baseProductId = getBaseProduct(selectedItem.getProduct()).getId();
                currentClient.getShoppingCart().removeProduct(baseProductId);
                refreshCartView();
                statusUpdater.accept(selectedItem.getProduct().getName() + " removed from cart.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to remove.");
        }
    }

    @FXML
    private void handleCheckout() {
        if (currentClient.getShoppingCart().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart", "Your cart is empty. Nothing to checkout.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/CheckoutDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Checkout - Payment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CheckoutDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                PaymentStrategy paymentStrategy = controller.getPaymentStrategy();
                if (paymentStrategy != null) {
                    Order order = new Order(currentClient, currentClient.getShoppingCart().getItems(), 
                            currentClient.getShoppingCart().getTotalCost());
                    
                    // Process payment first
                    boolean paymentSuccess = paymentStrategy.pay(order.getTotalAmount());
                    
                    if (paymentSuccess) {
                        order.setStatus(Order.STATUS_PAID); // Set to PAID after successful payment
                        orderManager.placeOrder(order); // This will notify admin for approval
                        currentClient.getShoppingCart().clearCart();
                        refreshCartView();
                        statusUpdater.accept("Order " + order.getId() + " placed successfully. Awaiting admin approval.");
                        showAlert(Alert.AlertType.INFORMATION, "Order Placed", 
                            "Your order has been placed and is awaiting admin approval.");
                    } else {
                        statusUpdater.accept("Payment failed.");
                        showAlert(Alert.AlertType.ERROR, "Payment Failed", 
                            "Your payment could not be processed. Please try again.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not complete checkout.");
        }
    }

    private void refreshCartView() {
        loadCartItems();
        updateTotal();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}