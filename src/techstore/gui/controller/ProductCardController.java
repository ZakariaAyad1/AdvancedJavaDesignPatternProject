package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import techstore.model.Product;

import java.io.File;

public class ProductCardController {
    @FXML private ImageView productImage;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Label brandLabel;

    private Product product;
    private ClientProductViewController parentController;
    private static final String DEFAULT_IMAGE = "/techstore/resources/images/default-product.png";

    public void setData(Product product, ClientProductViewController parentController) {
        this.product = product;
        this.parentController = parentController;

        nameLabel.setText(product.getName());
        priceLabel.setText(String.format("$%.2f", product.getPrice()));
        brandLabel.setText(product.getBrand());

        loadProductImage();
    }

    private void loadProductImage() {
        try {
            if (product.getImagePath() != null) {
                String imagePath = "src/techstore/resources/" + product.getImagePath();
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    if (!image.isError()) {
                        productImage.setImage(image);
                        return;
                    }
                }
            }
            // If we get here, either there's no image path, file doesn't exist, or loading failed
            loadDefaultImage();
        } catch (Exception e) {
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            // First try loading from resources
            Image defaultImage = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE));
            if (!defaultImage.isError()) {
                productImage.setImage(defaultImage);
                return;
            }
        } catch (Exception e) {
            // If resource loading fails, try file system
            try {
                File defaultImageFile = new File("src/techstore/resources/images/default-product.png");
                if (defaultImageFile.exists()) {
                    Image defaultImage = new Image(defaultImageFile.toURI().toString());
                    if (!defaultImage.isError()) {
                        productImage.setImage(defaultImage);
                        return;
                    }
                }
            } catch (Exception ex) {
                // If all attempts fail, set a color background
                productImage.setStyle("-fx-background-color: #f0f0f0;");
            }
        }
    }

    @FXML
    private void handleViewDetails() {
        parentController.showProductDetails(product);
    }

    @FXML
    private void handleAddToCart() {
        parentController.showAddToCartDialog(product);
    }
}
