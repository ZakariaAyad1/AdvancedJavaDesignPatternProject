package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import techstore.MainApp;
import techstore.model.Admin;
import techstore.model.Client;
import techstore.model.User;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;
import techstore.gui.controller.CategoryManagementController; // Ajouter cet import
import techstore.gui.controller.ViewAllOrdersController; // Ajouter cet import
import techstore.gui.controller.ViewAllUsersController; // Ajouter cet import

import java.io.IOException;

public class MainController {

    @FXML private BorderPane mainBorderPane;
    @FXML private MenuBar menuBar;
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;

    private MainApp mainApp;
    private User currentUser;
    private CatalogueService catalogueService; // Le Proxy sera injecté ici
    private OrderManager orderManager;
    private UserManager userManager;



    private void setupMenus() {
        menuBar.getMenus().removeIf(menu -> !"File".equals(menu.getText())); // Clear old role-specific menus

        if (currentUser instanceof Admin) {
            Menu adminMenu = new Menu("Admin");
            MenuItem manageProductsItem = new MenuItem("Manage Products");
            manageProductsItem.setOnAction(e -> loadProductManagementView());
            MenuItem manageCategoriesItem = new MenuItem("Manage Categories");
            // manageCategoriesItem.setOnAction(e -> loadCategoryManagementView()); // TODO
            MenuItem viewOrdersItem = new MenuItem("View All Orders");
            // viewOrdersItem.setOnAction(e -> loadAllOrdersView()); // TODO
            MenuItem viewUsersItem = new MenuItem("View All Users");
            // viewUsersItem.setOnAction(e -> loadAllUsersView()); // TODO
            adminMenu.getItems().addAll(manageProductsItem, manageCategoriesItem, viewOrdersItem, viewUsersItem);
            menuBar.getMenus().add(adminMenu);
        } else if (currentUser instanceof Client) {
            Menu clientMenu = new Menu("Client");
            MenuItem viewProductsItem = new MenuItem("View Products");
            viewProductsItem.setOnAction(e -> loadClientProductView());
            MenuItem viewCartItem = new MenuItem("View Cart");
            viewCartItem.setOnAction(e -> loadCartView());
            MenuItem viewMyOrdersItem = new MenuItem("My Orders");
            // viewMyOrdersItem.setOnAction(e -> loadMyOrdersView()); // TODO
            clientMenu.getItems().addAll(viewProductsItem, viewCartItem, viewMyOrdersItem);
            menuBar.getMenus().add(clientMenu);
        }

        if (currentUser instanceof Admin) {
            Menu adminMenu = new Menu("Admin");
            MenuItem manageProductsItem = new MenuItem("Manage Products");
            manageProductsItem.setOnAction(e -> loadProductManagementView());

            MenuItem manageCategoriesItem = new MenuItem("Manage Categories"); // NOUVEAU
            manageCategoriesItem.setOnAction(e -> loadCategoryManagementView()); // NOUVEAU

            MenuItem viewOrdersItem = new MenuItem("View All Orders"); // NOUVEAU
            viewOrdersItem.setOnAction(e -> loadAllOrdersView()); // NOUVEAU

            MenuItem viewUsersItem = new MenuItem("View All Users"); // NOUVEAU
            viewUsersItem.setOnAction(e -> loadAllUsersView()); // NOUVEAU

            adminMenu.getItems().addAll(manageProductsItem, manageCategoriesItem, viewOrdersItem, viewUsersItem);
            menuBar.getMenus().add(adminMenu);
        } else if (currentUser instanceof Client) {
            // ... (menu client existant)
        }
    }

    private void loadAdminDashboard() {
        // Pour l'instant, chargeons directement la gestion des produits
        loadProductManagementView();
    }

    private void loadProductManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ProductManagementView.fxml"));
            Parent productManagementNode = loader.load();
            ProductManagementController controller = loader.getController();
            controller.initData(catalogueService, mainApp.getPrimaryStage()); // Passer le proxy
            mainBorderPane.setCenter(productManagementNode);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading product management view.");
        }
    }

    private void loadClientProductView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ClientProductView.fxml"));
            Parent clientProductNode = loader.load();
            ClientProductViewController controller = loader.getController();
            controller.initData((Client) currentUser, catalogueService, mainApp.getPrimaryStage(), this::updateStatus); // Passer le client, proxy, etc.
            mainBorderPane.setCenter(clientProductNode);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading client product view.");
        }
    }

    private void loadCartView() {
        if (currentUser instanceof Client) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/CartView.fxml"));
                Parent cartNode = loader.load();
                CartController controller = loader.getController();
                controller.initData((Client)currentUser, orderManager, mainApp.getPrimaryStage(), this::updateStatus);
                mainBorderPane.setCenter(cartNode);
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error loading cart view.");
            }
        }
    }

    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("Status: " + message);
        }
    }


    @FXML
    private void handleLogout() {
        currentUser = null;
        mainApp.showLoginView();
    }

    @FXML
    private void handleExit() {
        mainApp.getPrimaryStage().close();
    }



    // NOUVELLES MÉTHODES DE CHARGEMENT DE VUE DANS MainController.java:
    private void loadCategoryManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/CategoryManagementView.fxml"));
            Parent categoryNode = loader.load();
            CategoryManagementController controller = loader.getController();
            controller.initData(catalogueService); // Proxy
            mainBorderPane.setCenter(categoryNode);
            updateStatus("Category Management loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading category management view.");
        }
    }

    private void loadAllOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ViewAllOrdersView.fxml"));
            Parent ordersNode = loader.load();
            ViewAllOrdersController controller = loader.getController();
            controller.initData(orderManager, mainApp.getPrimaryStage()); // Passer le stage principal            mainBorderPane.setCenter(ordersNode);
            updateStatus("All Orders view loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading all orders view.");
        }
    }

    private void loadAllUsersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ViewAllUsersView.fxml"));
            Parent usersNode = loader.load();
            ViewAllUsersController controller = loader.getController();
            controller.initData(userManager); // UserManager (Singleton)
            mainBorderPane.setCenter(usersNode);
            updateStatus("All Users view loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading all users view.");
        }
    }
    
    
    public void setMainApp(MainApp mainApp, User user, CatalogueService catalogueService, OrderManager orderManager, UserManager userManager) {
        this.mainApp = mainApp;
        this.currentUser = user;
        this.catalogueService = catalogueService;
        this.orderManager = orderManager;
        this.userManager = userManager; // Assurez-vous que userManager est bien passé et stocké si besoin dans les dashboards

        welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        setupMenus(); // Les menus du MainController peuvent rester pour Logout/Exit

        // Charger le dashboard approprié en fonction du rôle de l'utilisateur
        if (currentUser instanceof Admin) {
            loadAdminDashboardView();
        } else if (currentUser instanceof Client) {
            loadClientDashboardView();
        }
    }

    // La méthode setupMenus() peut rester telle quelle pour les menus "File",
    // ou vous pouvez déplacer les menus spécifiques au rôle dans les contrôleurs de Dashboard respectifs si la barre de menu principale ne doit contenir que "File".
    // Pour l'instant, gardons les menus spécifiques au rôle dans MainController pour la simplicité des actions globales comme le logout.

    private void loadAdminDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/AdminDashboard.fxml"));
            Parent adminDashboardNode = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.initData(mainApp, (Admin) currentUser, catalogueService, orderManager, userManager, this::updateStatus);
            mainBorderPane.setCenter(adminDashboardNode);
            updateStatus("Admin Dashboard loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            String errorMsg = "Error loading Admin Dashboard: " + e.getMessage();
            updateStatus(errorMsg);
            System.err.println(errorMsg);
        }
    }

    private void loadClientDashboardView() {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ClientView.fxml")); // Ou ClientDashboard.fxml si vous le renommez
            Parent clientDashboardNode = loader.load();
            ClientViewController controller = loader.getController();
            // Passer les dépendances nécessaires au ClientViewController
            controller.initData(mainApp, (Client) currentUser, catalogueService, orderManager, this::updateStatus);
            mainBorderPane.setCenter(clientDashboardNode);
            updateStatus("Client Dashboard loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading Client Dashboard.");
        }
    }

    // Les anciennes méthodes comme loadProductManagementView, loadClientProductView, etc.
    // NE SONT PLUS APPELÉES DIRECTEMENT PAR MainController. Elles sont maintenant appelées
    // par AdminDashboardController ou ClientViewController.
    // Vous pouvez les supprimer de MainController si elles ne sont plus utilisées ailleurs.



}