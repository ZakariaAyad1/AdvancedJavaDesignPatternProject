package techstore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import techstore.model.Category;
import techstore.service.CatalogueService;

public class CategoryManagementController {
    
    @FXML private ListView<Category> categoryListView;
    @FXML private TextField categoryNameField;
    private CatalogueService catalogueService;
    private ObservableList<Category> categories;

    public void initData(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
        loadCategories();
    }

    private void loadCategories() {
        categories = FXCollections.observableArrayList(catalogueService.getAllCategories());
        categoryListView.setItems(categories);
    }

    @FXML
    private void handleAddCategory() {
        String name = categoryNameField.getText().trim();
        if (!name.isEmpty()) {
            Category newCategory = new Category(name);
            catalogueService.addCategory(newCategory);
            loadCategories(); // Reload the list
            categoryNameField.clear();
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Category name cannot be empty.");
        }
    }

    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                if (catalogueService.isCategoryInUse(selectedCategory)) {
                    showAlert(Alert.AlertType.ERROR, "Cannot Delete Category",
                            "This category is currently in use by one or more products.");
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete category: " + selectedCategory.getName() + "?",
                        ButtonType.YES, ButtonType.NO);
                alert.setTitle("Confirm Delete");

                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    catalogueService.removeCategory(selectedCategory);
                    loadCategories(); // Reload the list after deletion
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to delete category: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a category to delete.");
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