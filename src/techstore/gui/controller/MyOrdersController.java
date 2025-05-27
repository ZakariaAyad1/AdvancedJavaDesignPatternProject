package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.Client;
import techstore.model.Order;
import techstore.patterns.singleton.OrderManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class MyOrdersController {

    @FXML private TableView<Order> myOrdersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML private TableColumn<Order, Double> totalAmountColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private Button viewOrderDetailsButton;


    private Client currentClient;
    private OrderManager orderManager;
    private Stage ownerStage;
    private ObservableList<Order> clientOrdersList;

    public void initData(Client client, OrderManager orderManager, Stage ownerStage) {
        this.currentClient = client;
        this.orderManager = orderManager;
        this.ownerStage = ownerStage;

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadClientOrders();
    }

    private void loadClientOrders() {
        clientOrdersList = FXCollections.observableArrayList(
                orderManager.getAllOrders().stream()
                        .filter(order -> order.getClient().getId() == currentClient.getId())
                        .collect(Collectors.toList())
        );
        myOrdersTable.setItems(clientOrdersList);
        viewOrderDetailsButton.setDisable(clientOrdersList.isEmpty());
    }

    @FXML
    private void initialize() {
        // Add custom status cell factory
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

    @FXML
    private void handleViewOrderDetails() {
        Order selectedOrder = myOrdersTable.getSelectionModel().getSelectedItem();
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

                // Add rejection reason if order was rejected
                if (Order.STATUS_REJECTED.equals(selectedOrder.getStatus()) &&
                        selectedOrder.getRejectionReason() != null) {
                    showAlert(Alert.AlertType.INFORMATION, "Order Rejected",
                            "Reason: " + selectedOrder.getRejectionReason());
                }

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load order details view.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to view its details.");
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