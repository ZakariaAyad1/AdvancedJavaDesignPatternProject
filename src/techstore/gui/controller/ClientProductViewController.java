package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techstore.model.Category;
import techstore.model.Client;
import techstore.model.Product;
import techstore.patterns.decorator.ProductComponent;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.util.function.Consumer;

public class ClientProductViewController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Category> categoryColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, String> brandColumn;
    @FXML private Button detailsButton;
    @FXML private Button addToCartButton;

    private Client currentClient;
    private CatalogueService catalogueService; // Proxy
    private Stage ownerStage;
    private ObservableList<Product> productList;
    private Consumer<String> statusUpdater;


    public void initData(Client client, CatalogueService catalogueService, Stage ownerStage, Consumer<String> statusUpdater) {
        this.currentClient = client;
        this.catalogueService = catalogueService;
        this.ownerStage = ownerStage;
        this.statusUpdater = statusUpdater;

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); // This will show discounted price
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));

        loadProducts();
    }

    private void loadProducts() {
        productList = FXCollections.observableArrayList(catalogueService.getAllProducts());
        productTable.setItems(productList);
    }

    @FXML
    private void handleViewDetails() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Réutiliser ProductDialog en mode non éditable ou créer une vue de détail dédiée
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductDialog.fxml"));
                VBox page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Product Details: " + selectedProduct.getName());
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ownerStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                ProductDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setProduct(selectedProduct, catalogueService.getAllCategories());
                // Désactiver les champs pour la vue de détail pure
                controller.setReadOnly(); // Vous devrez ajouter cette méthode à ProductDialogController

                dialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to view details.");
        }
    }

    @FXML
    private void handleAddToCart() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
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
                controller.setProduct(selectedProduct);

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
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to add to cart.");
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