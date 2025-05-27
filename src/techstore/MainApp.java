package techstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import techstore.gui.controller.*;
import techstore.model.User;
import techstore.patterns.factory.UserFactory;
import techstore.patterns.observer.AdminNotifier;
import techstore.patterns.observer.NotificationService;
import techstore.patterns.singleton.CatalogueManager;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;
import techstore.patterns.proxy.CatalogueServiceProxy;
import techstore.model.Category;
import techstore.model.Product;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import techstore.model.Admin;
import techstore.model.Client;
import javafx.scene.layout.BorderPane;


import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private UserManager userManager;
    private CatalogueManager catalogueManager; // Direct access for seeding
    private OrderManager orderManager;
    private NotificationService notificationService;
    private User currentUser;
    private MenuBar menuBar;
    private BorderPane mainBorderPane;

    @Override
    public void init() throws Exception {
        // Initialisation des Singletons et services de base
        userManager = UserManager.getInstance();
        catalogueManager = CatalogueManager.getInstance(); // Real service for seeding
        orderManager = OrderManager.getInstance();

        // Setup Observer
        notificationService = new NotificationService();
        AdminNotifier globalAdminNotifier = new AdminNotifier("SystemWideAdminGUI"); // Peut-être afficher dans un log GUI
        notificationService.registerObserver(globalAdminNotifier);
        userManager.setNotificationService(notificationService);
        orderManager.setNotificationService(notificationService);

        // Seed Data (Similaire à votre AppController console)
        seedData();
    }

    private void seedData() {
        UserFactory userFactory = new UserFactory();
        User admin = userFactory.createUser(UserFactory.UserType.ADMIN, "admin", "admin123", "admin@techstore.com");
        userManager.addUser(admin);

        // Utiliser le catalogue manager directement pour le seeding initial (avant que le proxy soit actif avec un utilisateur)
        Category laptops = new Category("Laptops");
        Category smartphones = new Category("Smartphones");
        catalogueManager.addCategory(laptops);
        catalogueManager.addCategory(smartphones);

        techstore.patterns.factory.ProductFactory productFactory = new techstore.patterns.factory.ProductFactory();
        Product laptop1 = productFactory.createProduct("ProBook X GUI", "Powerful Laptop", 1200.00, laptops, "TechBrand", 24, 0.1);
        catalogueManager.addProduct(laptop1); // Direct add
        Product phone1 = productFactory.createBasicProduct("Galaxy S25 GUI", 999.99, smartphones);
        catalogueManager.addProduct(phone1); // Direct add
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TechStore");
        showLoginView();
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/LoginView.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this); // Pour permettre au contrôleur de rappeler MainApp

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'erreur de manière appropriée
        }
    }

    public void showMainView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/MainView.fxml"));
            Parent root = loader.load();

            this.currentUser = user;
            MainController controller = loader.getController();
            
            // Get references to FXML elements
            this.mainBorderPane = (BorderPane) root;
            this.menuBar = (MenuBar) mainBorderPane.getTop();
            
            CatalogueService proxiedCatalogueService = new CatalogueServiceProxy(user);
            controller.setMainApp(this, user, proxiedCatalogueService, orderManager, userManager);

            setupMenus(); // Now safe to call after menuBar is initialized

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("TechStore - " + user.getUsername() + " (" + user.getRole() + ")");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading MainView.fxml: " + e.getMessage());
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


    private void setupMenus() {
        menuBar.getMenus().removeIf(menu -> !"File".equals(menu.getText()));

        if (currentUser instanceof Admin) {
            // ... (menu admin existant)
        } else if (currentUser instanceof Client) {
            Menu clientMenu = new Menu("Client");
            MenuItem viewProductsItem = new MenuItem("View Products");
            viewProductsItem.setOnAction(e -> loadClientProductView());
            MenuItem viewCartItem = new MenuItem("View Cart");
            viewCartItem.setOnAction(e -> loadCartView());

            MenuItem viewMyOrdersItem = new MenuItem("My Orders"); // ÉTAIT DÉJÀ LÀ
            viewMyOrdersItem.setOnAction(e -> loadMyOrdersView()); // LIER L'ACTION MAINTENANT

            clientMenu.getItems().addAll(viewProductsItem, viewCartItem, viewMyOrdersItem);
            menuBar.getMenus().add(clientMenu);
        }
    }

    private void loadClientProductView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/ClientProductView.fxml"));
            Parent productView = loader.load();
            
            ClientProductViewController controller = loader.getController();
            controller.initData(
                (Client) currentUser, 
                new CatalogueServiceProxy(currentUser),
                primaryStage,
                this::updateStatus
            );
            
            mainBorderPane.setCenter(productView);
            updateStatus("Product view loaded");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading product view");
        }
    }

    private void loadCartView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/CartView.fxml"));
            Parent cartView = loader.load();
            
            CartController controller = loader.getController();
            controller.initData(
                (Client) currentUser,
                orderManager,
                primaryStage,
                this::updateStatus
            );
            
            mainBorderPane.setCenter(cartView);
            updateStatus("Cart view loaded");
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Error loading cart view");
        }
    }

    private void updateStatus(String message) {
        // Implement status update logic here
        System.out.println(message); // Simple console output for now
    }
    // NOUVELLE MÉTHODE DE CHARGEMENT DE VUE DANS MainController.java:
    private void loadMyOrdersView() {
        if (currentUser instanceof Client) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/MyOrdersView.fxml"));
                Parent myOrdersNode = loader.load();
                MyOrdersController controller = loader.getController();
                controller.initData((Client) currentUser, orderManager, this.getPrimaryStage());
                mainBorderPane.setCenter(myOrdersNode);
                updateStatus("My Orders view loaded.");
            } catch (IOException e) {
                e.printStackTrace();
                updateStatus("Error loading My Orders view.");
            }
        }
    }

    // Modifier ViewAllOrdersController pour qu'il puisse ouvrir OrderDetailsDialog
    // Dans ViewAllOrdersController.java, vous pourriez ajouter un bouton ou un double-clic sur la table
    // pour appeler une méthode similaire à handleViewOrderDetails de MyOrdersController.
    // Par exemple, dans ViewAllOrdersController.java :
    /*
    @FXML
    private void initialize() { // S'il existe, sinon créez-le
        ordersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && ordersTable.getSelectionModel().getSelectedItem() != null) {
                handleViewOrderDetails(ordersTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void handleViewOrderDetails(Order selectedOrder) {
        if (selectedOrder != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/techstore/gui/fxml/OrderDetailsDialog.fxml"));
                VBox page = loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Order Details - ID: " + selectedOrder.getId());
                dialogStage.initModality(Modality.WINDOW_MODAL);
                // dialogStage.initOwner(ownerStage); // Vous aurez besoin d'une référence au stage principal
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);
                OrderDetailsDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setOrder(selectedOrder);
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */
    // N'oubliez pas d'injecter ownerStage dans ViewAllOrdersController si vous l'utilisez pour initOwner.

    public static void main(String[] args) {
        launch(args);
    }
}