package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import techstore.model.Category;
import techstore.model.Product;
import techstore.patterns.factory.ProductFactory;
import techstore.service.CatalogueService;

import java.util.List;

public class ProductDialogController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField brandField;
    @FXML private Label errorMessageLabel;

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;
    private final ProductFactory productFactory = new ProductFactory();
    private CatalogueService catalogueService;

    @FXML
    private void initialize() {
        // Initialization code if needed
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCatalogueService(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    public void setProduct(Product product, List<Category> categories) {
        categoryComboBox.getItems().addAll(categories);

        if (product != null) {
            // Edit mode
            this.product = product;
            nameField.setText(product.getName());
            descriptionArea.setText(product.getDescription());
            priceField.setText(String.valueOf(product.getPrice()));
            categoryComboBox.setValue(product.getCategory());
            brandField.setText(product.getBrand());
        }
    }

    public void setReadOnly() {
        nameField.setEditable(false);
        descriptionArea.setEditable(false);
        priceField.setEditable(false);
        categoryComboBox.setDisable(true);
        brandField.setEditable(false);
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            String name = nameField.getText();
            String description = descriptionArea.getText();
            double price = Double.parseDouble(priceField.getText());
            Category category = categoryComboBox.getValue();
            String brand = brandField.getText();

            if (product == null) {
                // Create new product
                product = productFactory.createProduct(name, description, price, category, brand, 0, 0);
                catalogueService.addProduct(product);
            } else {
                // Update existing product
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                product.setBrand(brand);
                catalogueService.updateProduct(product);
            }

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Name is required\n";
        }
        if (categoryComboBox.getValue() == null) {
            errorMessage += "Category must be selected\n";
        }
        try {
            if (!priceField.getText().isEmpty()) {
                Double.parseDouble(priceField.getText());
            } else {
                errorMessage += "Price is required\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Price must be a valid number\n";
        }

        if (errorMessage.isEmpty()) {
            errorMessageLabel.setText("");
            return true;
        } else {
            errorMessageLabel.setText(errorMessage);
            return false;
        }
    }

    public Product getProduct() {
        return product;
    }
}