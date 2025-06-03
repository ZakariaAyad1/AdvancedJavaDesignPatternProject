package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.Client;
import techstore.model.Product;
import techstore.patterns.decorator.ProductComponent;
import techstore.service.CatalogueService;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ProductCardController {
    @FXML private ImageView productImage;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Label categoryLabel;

    private Product product;
    private Client client;
    private CatalogueService catalogueService;
    private Stage ownerStage;
    private Consumer<String> statusUpdater;
    
    private static final String DEFAULT_IMAGE_PATH = "/techstore/resources/images/default-product.png";

    @FXML
    private void initialize() {
        // Verify that FXML elements are properly injected
        assert productImage != null : "fx:id=\"productImage\" was not injected: check your FXML file 'ProductCard.fxml'.";
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'ProductCard.fxml'.";
        assert priceLabel != null : "fx:id=\"priceLabel\" was not injected: check your FXML file 'ProductCard.fxml'.";
        assert categoryLabel != null : "fx:id=\"categoryLabel\" was not injected: check your FXML file 'ProductCard.fxml'.";
    }

    public void setProduct(Product product, Client client, CatalogueService catalogueService, 
                         Stage ownerStage, Consumer<String> statusUpdater) {
        this.product = product;
        this.client = client;
        this.catalogueService = catalogueService;
        this.ownerStage = ownerStage;
        this.statusUpdater = statusUpdater;

        // Set product details
        if (product != null) {
            nameLabel.setText(product.getName());
            priceLabel.setText(String.format("$%.2f", product.getPrice()));
            if (product.getCategory() != null) {
                categoryLabel.setText(product.getCategory().getName());
            } else {
                categoryLabel.setText("Uncategorized");
            }

            // Load and set product image
            loadProductImage();
        }
    }

    private void loadProductImage() {
        String imagePath = product.getImagePath();
        Image image;

        try {
            if (imagePath != null) {
                // First try to load from the specified path
                File imageFile = new File("src/techstore/resources/" + imagePath);
                if (imageFile.exists()) {
                    image = new Image(imageFile.toURI().toString());
                } else {
                    // If file doesn't exist, load default image
                    image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
                }
            } else {
                // If no image path specified, load default image
                image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
            }
        } catch (Exception e) {
            // If any error occurs, load default image
            e.printStackTrace();
            image = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
        }

        productImage.setImage(image);
        productImage.setFitWidth(180);
        productImage.setFitHeight(180);
        productImage.setPreserveRatio(true);
    }

    @FXML
    private void handleViewDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
            Scene scene = new Scene(loader.load());

            ProductDialogController controller = loader.getController();
            controller.setDialogStage(new Stage());
            controller.setCatalogueService(catalogueService);
            controller.setProduct(product, catalogueService.getAllCategories());
            controller.setReadOnly(); // View mode only

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Product Details - " + product.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            statusUpdater.accept("Error: Could not open product details.");
        }
    }    @FXML
    private void handleAddToCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/AddToCartDialog.fxml"));
            Scene scene = new Scene(loader.load());

            AddToCartDialogController controller = loader.getController();
            controller.setProduct(product);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add to Cart - " + product.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                ProductComponent configuredProduct = controller.getConfiguredProduct();
                int quantity = controller.getQuantity();
                
                // Add the configured product to cart
                client.getShoppingCart().addProduct(configuredProduct, quantity);
                statusUpdater.accept("Added " + quantity + " " + configuredProduct.getName() + " to cart");
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusUpdater.accept("Error: Could not open add to cart dialog.");
        }
    }
}
