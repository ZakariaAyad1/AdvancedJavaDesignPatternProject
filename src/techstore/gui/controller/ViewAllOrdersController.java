package techstore.gui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.Order;
import techstore.patterns.singleton.OrderManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ViewAllOrdersController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> clientNameColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, Double> totalAmountColumn;
    @FXML private TableColumn<Order, String> statusColumn;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    private OrderManager orderManager;
    private Stage ownerStage;
    private ObservableList<Order> ordersList;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initData(OrderManager orderManager, Stage ownerStage) {
        this.orderManager = orderManager;
        this.ownerStage = ownerStage;

        // Initialize columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClient().getUsername()));
        orderDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getOrderDate().format(DATE_FORMATTER)));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Enable/disable approve/reject buttons based on selection and status
        ordersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                boolean isSelected = newVal != null;
                boolean isPending = isSelected && "PENDING".equals(newVal.getStatus());
                if (approveButton != null) {
                    approveButton.setDisable(!isPending);
                }
                if (rejectButton != null) {
                    rejectButton.setDisable(!isPending);
                }
            });

        // Setup double-click handler
        ordersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleViewOrderDetails();
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        ordersList = FXCollections.observableArrayList(orderManager.getAllOrders());
        ordersTable.setItems(ordersList);
    }

    @FXML
    private void handleViewOrderDetails() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/OrderDetailsDialog.fxml"));
                VBox page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Order Details - ID: " + selectedOrder.getId());
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ownerStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                OrderDetailsDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setOrder(selectedOrder);

                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load order details.");
            }
        }
    }

    @FXML
    private void handleApproveOrder() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            System.out.println("Current order status: " + selectedOrder.getStatus()); // Debug line
            if ("PENDING".equals(selectedOrder.getStatus()) || "PAID".equals(selectedOrder.getStatus())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to approve Order #" + selectedOrder.getId() + "?",
                        ButtonType.YES, ButtonType.NO);
                alert.setTitle("Confirm Approval");

                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    selectedOrder.setStatus("APPROVED");
                    orderManager.updateOrder(selectedOrder);
                    ordersList = FXCollections.observableArrayList(orderManager.getAllOrders());
                    ordersTable.setItems(ordersList);
                    ordersTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Order Approved",
                            "Order #" + selectedOrder.getId() + " has been approved.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Cannot Approve",
                        "Only PENDING or PAID orders can be approved. Current status: " + selectedOrder.getStatus());
            }
        }
    }

    @FXML
    private void handleRejectOrder() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && Order.STATUS_PENDING.equals(selectedOrder.getStatus())) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reject Order");
            dialog.setHeaderText("Reject Order #" + selectedOrder.getId());
            dialog.setContentText("Please enter rejection reason:");

            dialog.showAndWait().ifPresent(reason -> {
                selectedOrder.setStatus(Order.STATUS_REJECTED);
                selectedOrder.setRejectionReason(reason);
                orderManager.updateOrder(selectedOrder);
                loadOrders(); // Refresh the table
                showAlert(Alert.AlertType.INFORMATION, "Order Rejected",
                        "Order #" + selectedOrder.getId() + " has been rejected.");
            });
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        // Add color coding for status column
        statusColumn.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "APPROVED": setStyle("-fx-text-fill: green;"); break;
                        case "REJECTED": setStyle("-fx-text-fill: red;"); break;
                        case "PENDING": setStyle("-fx-text-fill: orange;"); break;
                        default: setStyle(""); break;
                    }
                }
            }
        });

        // Enable buttons based on selection
        ordersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> updateButtonStates(newVal)
        );
    }

    private void updateButtonStates(Order selectedOrder) {
        boolean isPending = selectedOrder != null && "PENDING".equals(selectedOrder.getStatus());
        approveButton.setDisable(!isPending);
        rejectButton.setDisable(!isPending);
    }
}