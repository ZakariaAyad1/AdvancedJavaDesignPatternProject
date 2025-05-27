package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import techstore.model.CartItem;
import techstore.model.Order;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class OrderDetailsDialogController {

    @FXML private Label orderIdLabel;
    @FXML private Label clientNameLabel;
    @FXML private Label orderDateLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalAmountLabel;
    @FXML private ListView<CartItem> orderedItemsListView;

    private Stage dialogStage;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);


    @FXML
    private void initialize() {
        orderedItemsListView.setCellFactory(lv -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Qty: %d) - $%.2f each",
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getProduct().getPrice()));
                }
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOrder(Order order) {
        if (order != null) {
            orderIdLabel.setText(String.valueOf(order.getId()));
            clientNameLabel.setText(order.getClient().getUsername());
            orderDateLabel.setText(order.getOrderDate().format(DATE_TIME_FORMATTER));
            statusLabel.setText(order.getStatus());
            totalAmountLabel.setText(String.format("$%.2f", order.getTotalAmount()));
            orderedItemsListView.setItems(FXCollections.observableArrayList(order.getOrderedItems()));
        }
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}