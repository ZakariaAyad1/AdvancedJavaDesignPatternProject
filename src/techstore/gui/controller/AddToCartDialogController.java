package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import techstore.model.Product;
import techstore.patterns.decorator.ExtendedWarrantyDecorator;
import techstore.patterns.decorator.GiftWrapDecorator;
import techstore.patterns.decorator.ProductComponent;

public class AddToCartDialogController {

    @FXML private Label productNameLabel;
    @FXML private Label priceLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private CheckBox giftWrapCheckBox;
    @FXML private CheckBox warrantyCheckBox;
    @FXML private Spinner<Integer> warrantyYearsSpinner;

    private Stage dialogStage;
    private Product baseProduct;
    private ProductComponent configuredProduct;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        // Lier la désactivation du spinner d'années de garantie à la case à cocher de garantie
        warrantyYearsSpinner.disableProperty().bind(warrantyCheckBox.selectedProperty().not());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProduct(Product product) {
        this.baseProduct = product;
        productNameLabel.setText(product.getName());
        priceLabel.setText(String.format("Price: $%.2f", product.getPrice()));
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public ProductComponent getConfiguredProduct() {
        return configuredProduct;
    }

    public int getQuantity() {
        return quantitySpinner.getValue();
    }

    @FXML
    private void handleOk() {
        configuredProduct = baseProduct; // Commence avec le produit de base

        if (giftWrapCheckBox.isSelected()) {
            configuredProduct = new GiftWrapDecorator(configuredProduct);
        }
        if (warrantyCheckBox.isSelected()) {
            int years = warrantyYearsSpinner.getValue();
            configuredProduct = new ExtendedWarrantyDecorator(configuredProduct, years);
        }
        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}