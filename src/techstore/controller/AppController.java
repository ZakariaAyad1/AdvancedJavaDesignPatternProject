package techstore.controller;

import techstore.model.*;
import techstore.patterns.factory.UserFactory;
import techstore.patterns.observer.AdminNotifier;
import techstore.patterns.observer.NotificationService;
import techstore.patterns.singleton.CatalogueManager;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;
import techstore.patterns.proxy.CatalogueServiceProxy; // Using Proxy
import techstore.view.ConsoleView;
import techstore.view.InputUtil;

import java.util.Optional;

// MVC: Controller
public class AppController {
    private final ConsoleView view;
    private final UserManager userManager;
    private final UserFactory userFactory;
    private final NotificationService notificationService; // Observer
    private User currentUser;
    // Proxy for catalogue access, behavior depends on currentUser
    private CatalogueService catalogueService;
    private final OrderManager orderManager;


    public AppController() {
        this.view = new ConsoleView();
        this.userManager = UserManager.getInstance();
        this.userFactory = new UserFactory();
        this.orderManager = OrderManager.getInstance();

        // Observer Setup
        this.notificationService = new NotificationService();
        AdminNotifier globalAdminNotifier = new AdminNotifier("SystemWideAdmin");
        this.notificationService.registerObserver(globalAdminNotifier);
        this.userManager.setNotificationService(this.notificationService);
        this.orderManager.setNotificationService(this.notificationService);

        // Initial catalogue service (no user logged in, or a default read-only view)
        // When a user logs in, this will be updated with a proxy specific to their role.
        this.catalogueService = new CatalogueServiceProxy(null); // Default to restricted access

        // Seed some data
        seedData();
    }

    private void seedData() {
        // Create a default admin
        User admin = userFactory.createUser(UserFactory.UserType.ADMIN, "admin", "admin123", "admin@techstore.com");
        userManager.addUser(admin);

        // Create some categories
        Category laptops = new Category("Laptops");
        Category smartphones = new Category("Smartphones");
        CatalogueManager.getInstance().addCategory(laptops); // Use Singleton directly for seeding or proxy if admin
        CatalogueManager.getInstance().addCategory(smartphones);

        // Create some products using ProductFactory (which uses Builder)
        // Note: For seeding, we might directly use CatalogueManager or ensure admin context for proxy
        CatalogueService adminCatalogueAccess = new CatalogueServiceProxy(admin);

        techstore.patterns.factory.ProductFactory productFactory = new techstore.patterns.factory.ProductFactory();

        Product laptop1 = productFactory.createProduct("ProBook X", "Powerful Laptop", 1200.00, laptops, "TechBrand", 24, 0.1);
        adminCatalogueAccess.addProduct(laptop1);

        Product phone1 = productFactory.createBasicProduct("Galaxy S25", 999.99, smartphones);
        adminCatalogueAccess.addProduct(phone1);

        Product phone2 = productFactory.createProduct("Pixel 10", "AI Powered Phone", 899.00, smartphones, "Google", 12, 0.05);
        adminCatalogueAccess.addProduct(phone2);
    }


    public void run() {
        view.displayMessage("Welcome to TechStore!");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                if (currentUser instanceof Admin) {
                    AdminController adminController = new AdminController(view, (Admin) currentUser, catalogueService, userManager, orderManager);
                    adminController.showAdminMenu();
                    if (adminController.isLogout()) currentUser = null;
                } else if (currentUser instanceof Client) {
                    ClientController clientController = new ClientController(view, (Client) currentUser, catalogueService, orderManager);
                    clientController.showClientMenu();
                    if (clientController.isLogout()) currentUser = null;
                }
            }
            if (currentUser == null && !askToContinue()) { // If logged out, ask to continue or exit
                break;
            }
        }
        view.displayMessage("Thank you for using TechStore. Goodbye!");
        InputUtil.closeScanner();
    }

    private boolean askToContinue() {
        String choice = InputUtil.getString("Do you want to perform another action (login/register) or exit? (yes/exit): ").toLowerCase();
        return choice.equals("yes");
    }


    private void showLoginMenu() {
        view.displayMessage("\n--- Main Menu ---");
        view.displayMessage("1. Login");
        view.displayMessage("2. Register Client Account");
        view.displayMessage("3. Exit");
        int choice = InputUtil.getInt("Enter your choice: ");

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                registerClient();
                break;
            case 3:
                currentUser = null; // Ensure exit condition for run() loop if it's checked there
                view.displayMessage("Exiting application...");
                System.exit(0); // Or set a flag to break the main loop
                return; // Exit this menu
            default:
                view.displayMessage("Invalid choice. Please try again.");
        }
    }

    private void login() {
        String username = InputUtil.getString("Enter username: ");
        String password = InputUtil.getString("Enter password: ");
        Optional<User> userOpt = userManager.findUserByUsername(username);

        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            currentUser = userOpt.get();
            this.catalogueService = new CatalogueServiceProxy(currentUser); // Update proxy with current user
            view.displayMessage("Login successful! Welcome " + currentUser.getUsername());
        } else {
            view.displayMessage("Invalid username or password.");
        }
    }

    private void registerClient() {
        view.displayMessage("\n--- Client Registration ---");
        String username = InputUtil.getString("Enter username: ");
        if (userManager.findUserByUsername(username).isPresent()) {
            view.displayMessage("Username already exists. Please choose another.");
            return;
        }
        String password = InputUtil.getString("Enter password: ");
        String email = InputUtil.getString("Enter email: ");

        User newClient = userFactory.createUser(UserFactory.UserType.CLIENT, username, password, email);
        userManager.addUser(newClient); // Observer will notify admin
        view.displayMessage("Client registration successful for " + username);
    }
}
