package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.Product;
import techstore.model.Category;
import techstore.service.CatalogueService;

import java.io.IOException;

public class ProductManagementController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Category> categoryColumn;
    @FXML private TableColumn<Product, String> brandColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private ComboBox<Category> categoryFilterComboBox;

    private CatalogueService catalogueService;
    private Stage ownerStage;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredProducts;

    public void initData(CatalogueService catalogueService, Stage ownerStage) {
        this.catalogueService = catalogueService;
        this.ownerStage = ownerStage;

        // Initialize columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        // Setup category filter
        setupCategoryFilter();

        // Load products
        loadProducts();

        // Disable buttons when no selection
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                editButton.setDisable(newVal == null);
                deleteButton.setDisable(newVal == null);
            });
    }    private void setupCategoryFilter() {
        ObservableList<Category> categories = FXCollections.observableArrayList(catalogueService.getAllCategories());
        Category allCategory = new Category("All Categories");
        categories.add(0, allCategory);
        categoryFilterComboBox.setItems(categories);
        categoryFilterComboBox.getSelectionModel().selectFirst();
    }

    private void loadProducts() {
        productList = FXCollections.observableArrayList(catalogueService.getAllProducts());
        filteredProducts = new FilteredList<>(productList);
        productTable.setItems(filteredProducts);
    }

    @FXML
    private void handleCategoryFilter() {
        Category selectedCategory = categoryFilterComboBox.getValue();
        if (selectedCategory == null) return;

        if (selectedCategory.getName().equals("All Categories")) {
            filteredProducts.setPredicate(null); // Show all products
        } else {
            filteredProducts.setPredicate(product -> 
                product.getCategory() != null && 
                product.getCategory().getName().equals(selectedCategory.getName())
            );
        }
    }

    @FXML
    private void handleNewProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ProductDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCatalogueService(catalogueService); // Add this line
            controller.setProduct(null, catalogueService.getAllCategories());

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                loadProducts(); // Refresh the product list
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load product dialog.");
        }
    }

    @FXML
    private void handleEditProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
                VBox page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Product");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ownerStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                ProductDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setCatalogueService(catalogueService);
                controller.setProduct(selectedProduct, catalogueService.getAllCategories());

                dialogStage.showAndWait();

                if (controller.isOkClicked()) {
                    loadProducts(); // Refresh the table view to show changes
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load product dialog.");
            }
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Product");
            alert.setContentText("Are you sure you want to delete " + selectedProduct.getName() + "?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                catalogueService.removeProduct(selectedProduct.getId());
                loadProducts();
            }
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