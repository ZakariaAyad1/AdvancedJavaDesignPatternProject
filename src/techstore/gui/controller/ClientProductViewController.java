package techstore.gui.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import techstore.model.*;
import techstore.patterns.decorator.ProductComponent;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClientProductViewController {
    @FXML private FlowPane productGrid;
    @FXML private ComboBox<Category> categoryFilterComboBox;

    private Client currentClient;
    private CatalogueService catalogueService; // Proxy
    private Stage ownerStage;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredProducts;
    private Consumer<String> statusUpdater;
    public void initData(Client client, CatalogueService catalogueService, Stage ownerStage, Consumer<String> statusUpdater) {
        this.currentClient = client;
        this.catalogueService = catalogueService;
        this.ownerStage = ownerStage;
        this.statusUpdater = statusUpdater;

        setupCategoryFilter();
        loadProducts();
    }    private void setupCategoryFilter() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        categories.add(new Category("All Categories"));
        categories.addAll(catalogueService.getAllCategories());
        categoryFilterComboBox.setItems(categories);
        categoryFilterComboBox.getSelectionModel().selectFirst();
    }

    private void loadProducts() {
        productGrid.getChildren().clear();
        productList = FXCollections.observableArrayList(catalogueService.getAllProducts());
        
        for (Product product : productList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductCard.fxml"));
                VBox productCard = loader.load();
                
                ProductCardController controller = loader.getController();
                controller.setProduct(product, currentClient, catalogueService, ownerStage, statusUpdater);
                
                productGrid.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load product card: " + e.getMessage());
            }
        }
    }    @FXML
    private void handleCategoryFilter() {
        Category selectedCategory = categoryFilterComboBox.getValue();
        if (selectedCategory == null) return;

        productGrid.getChildren().clear();
        List<Product> filteredList;
        
        if (selectedCategory.getName().equals("All Categories")) {
            filteredList = productList;
        } else {
            filteredList = productList.stream()
                .filter(product -> product.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        }

        for (Product product : filteredList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductCard.fxml"));
                VBox productCard = loader.load();
                
                ProductCardController controller = loader.getController();
                controller.setProduct(product, currentClient, catalogueService, ownerStage, statusUpdater);
                
                productGrid.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load product card: " + e.getMessage());
            }
        }
    }

    public void showProductDetails(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Product Details: " + product.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ProductDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product, catalogueService.getAllCategories());
            controller.setReadOnly();

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open product details.");
        }
    }    public void showAddToCartDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/AddToCartDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add to Cart");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AddToCartDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                ProductComponent productToAdd = controller.getConfiguredProduct();
                int quantity = controller.getQuantity();
                currentClient.getShoppingCart().addProduct(productToAdd, quantity);
                statusUpdater.accept(productToAdd.getName() + " (x" + quantity + ") added to cart.");
                showAlert(Alert.AlertType.INFORMATION, "Cart Updated", productToAdd.getName() + " added to your cart.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open add to cart dialog.");
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