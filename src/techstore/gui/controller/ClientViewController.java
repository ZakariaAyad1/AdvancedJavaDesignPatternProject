package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import techstore.MainApp;
import techstore.model.Client;
import techstore.patterns.singleton.OrderManager;
import techstore.service.CatalogueService;

import java.io.IOException;
import java.util.function.Consumer;

public class ClientViewController {

    @FXML private Label clientWelcomeLabel;
    @FXML private BorderPane clientContentArea; // Zone où charger les autres vues client

    private MainApp mainApp;
    private Client currentClient;
    private CatalogueService catalogueService; // Proxy
    private OrderManager orderManager;
    private Consumer<String> statusUpdater; // Pour mettre à jour le statut dans MainController

    public void initData(MainApp mainApp, Client client, CatalogueService catalogueService, OrderManager orderManager, Consumer<String> statusUpdater) {
        this.mainApp = mainApp;
        this.currentClient = client;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
        this.statusUpdater = statusUpdater;

        clientWelcomeLabel.setText("Welcome, " + currentClient.getUsername() + "!");
        // Charger une vue par défaut, par exemple la vue des produits
        handleViewProducts();
    }

    @FXML
    private void handleViewProducts() {
        loadClientView("/techstore/gui/fxml/ClientProductView.fxml", controller -> {
            if (controller instanceof ClientProductViewController) {
                ((ClientProductViewController) controller).initData(currentClient, catalogueService, mainApp.getPrimaryStage(), statusUpdater);
            }
        });
        statusUpdater.accept("Products view loaded.");
    }

    @FXML
    private void handleViewCart() {
        loadClientView("/techstore/gui/fxml/CartView.fxml", controller -> {
            if (controller instanceof CartController) {
                ((CartController) controller).initData(currentClient, orderManager, mainApp.getPrimaryStage(), statusUpdater);
            }
        });
        statusUpdater.accept("Shopping Cart view loaded.");
    }

    @FXML
    private void handleViewMyOrders() {
        loadClientView("/techstore/gui/fxml/MyOrdersView.fxml", controller -> {
            if (controller instanceof MyOrdersController) {
                ((MyOrdersController) controller).initData(currentClient, orderManager, mainApp.getPrimaryStage());
            }
        });
        statusUpdater.accept("My Orders view loaded.");
    }

    /**
     * Méthode générique pour charger une vue FXML dans clientContentArea.
     * @param fxmlPath Chemin vers le fichier FXML.
     * @param postLoadAction Action à exécuter après le chargement, typiquement pour initialiser le contrôleur.
     */
    private void loadClientView(String fxmlPath, Consumer<Object> postLoadAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewNode = loader.load();
            Object controller = loader.getController();
            if (postLoadAction != null) {
                postLoadAction.accept(controller);
            }
            clientContentArea.setCenter(viewNode);
        } catch (IOException e) {
            e.printStackTrace();
            statusUpdater.accept("Error loading view: " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1));
            // Afficher une alerte ou un message d'erreur dans l'UI ici aussi
        }
    }
}