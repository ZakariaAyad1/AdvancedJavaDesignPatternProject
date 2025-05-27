package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import techstore.patterns.strategy.CreditCardPaymentStrategy;
import techstore.patterns.strategy.PayPalPaymentStrategy;
import techstore.patterns.strategy.PaymentStrategy;

public class CheckoutDialogController {

    @FXML private ComboBox<String> paymentTypeComboBox;
    @FXML private GridPane creditCardPane;
    @FXML private TextField ccNumberField;
    @FXML private TextField ccCvvField;
    @FXML private TextField ccExpiryField;
    @FXML private GridPane payPalPane;
    @FXML private TextField payPalEmailField;
    @FXML private PasswordField payPalPasswordField;
    @FXML private Label errorMessageLabel;

    private Stage dialogStage;
    private PaymentStrategy paymentStrategy;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        paymentTypeComboBox.setItems(FXCollections.observableArrayList("Credit Card", "PayPal"));
        paymentTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            creditCardPane.setVisible("Credit Card".equals(newVal));
            creditCardPane.setManaged("Credit Card".equals(newVal));
            payPalPane.setVisible("PayPal".equals(newVal));
            payPalPane.setManaged("PayPal".equals(newVal));
            errorMessageLabel.setText("");
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    @FXML
    private void handleOk() {
        String selectedType = paymentTypeComboBox.getValue();
        if (selectedType == null) {
            errorMessageLabel.setText("Please select a payment type.");
            return;
        }

        if ("Credit Card".equals(selectedType)) {
            if (ccNumberField.getText().isEmpty() || ccCvvField.getText().isEmpty() || ccExpiryField.getText().isEmpty()) {
                errorMessageLabel.setText("All credit card fields are required.");
                return;
            }
            // Ajouter des validations plus robustes ici (format, etc.)
            paymentStrategy = new CreditCardPaymentStrategy(ccNumberField.getText(), ccCvvField.getText(), ccExpiryField.getText());
        } else if ("PayPal".equals(selectedType)) {
            if (payPalEmailField.getText().isEmpty() || payPalPasswordField.getText().isEmpty()) {
                errorMessageLabel.setText("PayPal email and password are required.");
                return;
            }
            paymentStrategy = new PayPalPaymentStrategy(payPalEmailField.getText(), payPalPasswordField.getText());
        } else {
            errorMessageLabel.setText("Invalid payment type selected.");
            return;
        }

        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}