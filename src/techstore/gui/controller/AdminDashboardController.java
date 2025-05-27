package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import techstore.MainApp;
import techstore.model.Admin;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.util.function.Consumer; // Pour le status updater

public class AdminDashboardController {

    @FXML private Label adminWelcomeLabel;
    @FXML private BorderPane adminContentArea; // Zone où charger les autres vues admin

    private MainApp mainApp; // Si besoin de naviguer ou accéder à la stage principale
    private Admin currentAdmin;
    private CatalogueService catalogueService; // Proxy
    private OrderManager orderManager;
    private UserManager userManager;
    private Consumer<String> statusUpdater; // Pour mettre à jour le statut dans MainController

    public void initData(MainApp mainApp, Admin admin, CatalogueService catalogueService, OrderManager orderManager, UserManager userManager, Consumer<String> statusUpdater) {
        this.mainApp = mainApp;
        this.currentAdmin = admin;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
        this.userManager = userManager;
        this.statusUpdater = statusUpdater;

        adminWelcomeLabel.setText("Welcome, Admin " + currentAdmin.getUsername() + "!");
        // Charger une vue par défaut, par exemple la gestion des produits
        handleManageProducts();
    }

    @FXML
    private void handleManageProducts() {
        loadAdminView("/techstore/gui/fxml/ProductManagementView.fxml", controller -> {
            if (controller instanceof ProductManagementController) {
                ((ProductManagementController) controller).initData(catalogueService, mainApp.getPrimaryStage());
            }
        });
        statusUpdater.accept("Product Management loaded.");
    }

    @FXML
    private void handleManageCategories() {
        loadAdminView("/techstore/gui/fxml/CategoryManagementView.fxml", controller -> {
            if (controller instanceof CategoryManagementController) {
                ((CategoryManagementController) controller).initData(catalogueService);
            }
        });
        statusUpdater.accept("Category Management loaded.");
    }

    @FXML
    private void handleViewAllOrders() {
         loadAdminView("/techstore/gui/fxml/ViewAllOrdersView.fxml", controller -> {
            if (controller instanceof ViewAllOrdersController) {
                ((ViewAllOrdersController) controller).initData(orderManager, mainApp.getPrimaryStage());
            }
        });
        statusUpdater.accept("All Orders view loaded.");
    }

    @FXML
    private void handleViewAllUsers() {
        loadAdminView("/techstore/gui/fxml/ViewAllUsersView.fxml", controller -> {
            if (controller instanceof ViewAllUsersController) {
                ((ViewAllUsersController) controller).initData(userManager);
            }
        });
        statusUpdater.accept("All Users view loaded.");
    }

    /**
     * Méthode générique pour charger une vue FXML dans adminContentArea.
     * @param fxmlPath Chemin vers le fichier FXML.
     * @param postLoadAction Action à exécuter après le chargement, typiquement pour initialiser le contrôleur.
     */
    private void loadAdminView(String fxmlPath, Consumer<Object> postLoadAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewNode = loader.load();
            Object controller = loader.getController();
            if (postLoadAction != null) {
                postLoadAction.accept(controller);
            }
            adminContentArea.setCenter(viewNode);
        } catch (IOException e) {
            e.printStackTrace();
            statusUpdater.accept("Error loading view: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1));
            // Afficher une alerte ou un message d'erreur dans l'UI ici aussi
        }
    }
}