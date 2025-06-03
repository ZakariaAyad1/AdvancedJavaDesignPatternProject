package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import techstore.model.Category;
import techstore.model.Product;
import techstore.patterns.factory.ProductFactory;
import techstore.service.CatalogueService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProductDialogController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField brandField;
    @FXML private TextField imagePathField;
    @FXML private ImageView imagePreview;
    @FXML private Label errorMessageLabel;

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;
    private final ProductFactory productFactory = new ProductFactory();
    private CatalogueService catalogueService;
    private File selectedImageFile;

    @FXML
    private void initialize() {
        // Initialization code if needed
    }

    @FXML
    private void handleImageBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            selectedImageFile = file;
            imagePathField.setText(file.getName());
            Image image = new Image(file.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    private String saveImage(File file) throws IOException {
        String uploadsDir = "src/techstore/resources/uploads/products";
        File directory = new File(uploadsDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getName();
        Path targetPath = Paths.get(uploadsDir, fileName);
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "products/" + fileName;
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
            
            if (product.getImagePath() != null) {
                imagePathField.setText(product.getImagePath());
                String imagePath = "src/techstore/resources/" + product.getImagePath();
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imagePreview.setImage(image);
                }
            }
        }
    }

    public void setReadOnly() {
        nameField.setEditable(false);
        descriptionArea.setEditable(false);
        priceField.setEditable(false);
        categoryComboBox.setDisable(true);
        brandField.setEditable(false);
        imagePathField.setEditable(false);
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            try {
                String name = nameField.getText();
                String description = descriptionArea.getText();
                double price = Double.parseDouble(priceField.getText());
                Category category = categoryComboBox.getValue();
                String brand = brandField.getText();
                String imagePath = null;

                if (selectedImageFile != null) {
                    imagePath = saveImage(selectedImageFile);
                } else if (product != null) {
                    imagePath = product.getImagePath();
                }

                if (product == null) {
                    // Create new product
                    product = productFactory.createProduct(name, description, price, category, brand, 0, 0, imagePath);
                    catalogueService.addProduct(product);
                } else {
                    // Update existing product
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setCategory(category);
                    product.setBrand(brand);
                    if (imagePath != null) {
                        product.setImagePath(imagePath);
                    }
                    catalogueService.updateProduct(product);
                }

                okClicked = true;
                dialogStage.close();
            } catch (IOException e) {
                errorMessageLabel.setText("Error saving image: " + e.getMessage());
            }
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