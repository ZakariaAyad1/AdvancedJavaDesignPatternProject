package techstore.controller;

import techstore.model.*;
import techstore.patterns.builder.ProductBuilder;
import techstore.patterns.command.*;
import techstore.patterns.factory.ProductFactory;
import techstore.patterns.singleton.CatalogueManager;
import techstore.patterns.singleton.OrderManager;
import techstore.patterns.singleton.UserManager;
import techstore.service.CatalogueService;
import techstore.view.ConsoleView;
import techstore.view.InputUtil;

import java.util.List;
import java.util.Optional;

public class AdminController {
    private final ConsoleView view;
    private final Admin admin;
    private final CatalogueService catalogueService; // This will be the Proxy
    private final AdminCommandInvoker commandInvoker;
    private final ProductFactory productFactory;
    private final UserManager userManager;
    private final OrderManager orderManager;
    private boolean logout = false;


    public AdminController(ConsoleView view, Admin admin, CatalogueService catalogueService, UserManager userManager, OrderManager orderManager) {
        this.view = view;
        this.admin = admin;
        this.catalogueService = catalogueService;
        this.commandInvoker = new AdminCommandInvoker();
        this.productFactory = new ProductFactory();
        this.userManager = userManager;
        this.orderManager = orderManager;
    }

    public boolean isLogout() {
        return logout;
    }

    public void showAdminMenu() {
        logout = false;
        while (!logout) {
            view.displayMessage("\n--- Admin Menu (" + admin.getUsername() + ") ---");
            view.displayMessage("1. Manage Products");
            view.displayMessage("2. Manage Categories");
            view.displayMessage("3. View All Orders");
            view.displayMessage("4. View All Users");
            view.displayMessage("5. Logout");
            int choice = InputUtil.getInt("Enter choice: ");

            switch (choice) {
                case 1: manageProducts(); break;
                case 2: manageCategories(); break;
                case 3: viewAllOrders(); break;
                case 4: viewAllUsers(); break;
                case 5: logout = true; view.displayMessage("Logging out..."); break;
                default: view.displayMessage("Invalid choice.");
            }
        }
    }

    private void manageProducts() {
        view.displayMessage("\n--- Product Management ---");
        view.displayMessage("1. Add Product");
        view.displayMessage("2. Update Product");
        view.displayMessage("3. Delete Product");
        view.displayMessage("4. View All Products");
        view.displayMessage("5. Back to Admin Menu");
        int choice = InputUtil.getInt("Enter choice: ");

        switch (choice) {
            case 1: addProduct(); break;
            case 2: updateProduct(); break;
            case 3: deleteProduct(); break;
            case 4: viewAllProducts(); break;
            case 5: return;
            default: view.displayMessage("Invalid choice.");
        }
        if (choice >= 1 && choice <=4) manageProducts(); // Loop back
    }

    private void addProduct() {
        view.displayMessage("--- Add New Product ---");
        String name = InputUtil.getString("Product Name: ");
        String description = InputUtil.getString("Description: ");
        double price = InputUtil.getDouble("Price: ");

        view.displayCategories(catalogueService.getAllCategories());
        int catId = InputUtil.getInt("Category ID (or 0 if none/new): ");
        Category category = null;
        if (catId != 0) {
            Optional<Category> catOpt = catalogueService.findCategoryById(catId);
            if (catOpt.isPresent()) category = catOpt.get();
            else { view.displayMessage("Category not found."); return; }
        }

        String brand = InputUtil.getString("Brand (optional, press Enter to skip): ");
        int warranty = InputUtil.getInt("Warranty in Months (optional, 0 for none): ");
        double discount = InputUtil.getDouble("Discount (e.g., 0.1 for 10%, 0 for none): ");

        // Product ID is handled by CatalogueManager via ProductFactory and Builder
        Product newProduct = new ProductBuilder(catalogueService.getNextProductId(), name, price)
                .description(description)
                .category(category)
                .brand(brand.isEmpty() ? null : brand)
                .warrantyMonths(warranty)
                .discount(discount)
                .build();

        Command addCmd = new AddProductCommand(catalogueService, newProduct);
        commandInvoker.setCommand(addCmd);
        commandInvoker.executeCommand(); // Proxy will check admin rights
    }

    private void updateProduct() {
        viewAllProducts();
        int productId = InputUtil.getInt("Enter ID of product to update: ");
        Optional<Product> productOpt = catalogueService.findProductById(productId);
        if (!productOpt.isPresent()) {
            view.displayMessage("Product not found.");
            return;
        }
        Product existingProduct = productOpt.get();
        view.displayMessage("Updating product: " + existingProduct.getName());

        String name = InputUtil.getString("New Name (current: " + existingProduct.getName() + ", Enter to keep): ");
        String description = InputUtil.getString("New Description (current: " + existingProduct.getDescription() + ", Enter to keep): ");
        String priceStr = InputUtil.getString("New Price (current: " + existingProduct.getPrice() + ", Enter to keep): ");
        // ... (similar for other fields: category, brand, warranty, discount)

        Product updatedProduct = new ProductBuilder(existingProduct.getId(),
                name.isEmpty() ? existingProduct.getName() : name,
                priceStr.isEmpty() ? existingProduct.getPrice() : Double.parseDouble(priceStr))
                .description(description.isEmpty() ? existingProduct.getDescription() : description)
                .category(existingProduct.getCategory()) // For simplicity, not changing category here, add if needed
                .brand(existingProduct.getBrand())
                .warrantyMonths(existingProduct.getWarrantyMonths())
                .discount(existingProduct.getDiscount())
                .build();

        // Further prompts to update other fields can be added here.
        // For example, for category:
        view.displayCategories(catalogueService.getAllCategories());
        int catId = InputUtil.getInt("New Category ID (current: " + (existingProduct.getCategory() != null ? existingProduct.getCategory().getId() : "N/A") + ", 0 to keep): ");
        if (catId != 0) {
            Optional<Category> catOpt = catalogueService.findCategoryById(catId);
            if (catOpt.isPresent()) updatedProduct.setCategory(catOpt.get());
            else view.displayMessage("Category not found, keeping old one.");
        }


        Command updateCmd = new UpdateProductCommand(catalogueService, updatedProduct);
        commandInvoker.setCommand(updateCmd);
        commandInvoker.executeCommand();
    }

    private void deleteProduct() {
        viewAllProducts();
        int productId = InputUtil.getInt("Enter ID of product to delete: ");
        Command deleteCmd = new DeleteProductCommand(catalogueService, productId);
        commandInvoker.setCommand(deleteCmd);
        commandInvoker.executeCommand();
    }

    private void viewAllProducts() {
        view.displayProducts(catalogueService.getAllProducts());
    }

    private void manageCategories() {
        view.displayMessage("\n--- Category Management ---");
        view.displayMessage("1. Add Category");
        view.displayMessage("2. View All Categories");
        view.displayMessage("3. Back to Admin Menu");
        int choice = InputUtil.getInt("Enter choice: ");

        switch (choice) {
            case 1: addCategory(); break;
            case 2: viewAllCategories(); break;
            case 3: return;
            default: view.displayMessage("Invalid choice.");
        }
        if (choice >=1 && choice <=2) manageCategories();
    }

    private void addCategory() {
        String name = InputUtil.getString("Enter new category name: ");
        if (catalogueService.findCategoryByName(name).isPresent()) {
            view.displayMessage("Category '" + name + "' already exists.");
            return;
        }
        Category newCategory = new Category(name);
        catalogueService.addCategory(newCategory); // Proxy checks rights
        view.displayMessage("Category '" + name + "' added.");
    }

    private void viewAllCategories() {
        view.displayCategories(catalogueService.getAllCategories());
    }

    private void viewAllOrders() {
        List<Order> orders = orderManager.getAllOrders();
        view.displayOrders(orders);
        if (!orders.isEmpty()) {
            int orderId = InputUtil.getInt("Enter Order ID to view details (or 0 to skip): ");
            if (orderId != 0) {
                orderManager.findOrderById(orderId)
                        .ifPresentOrElse(
                                view::displayOrderDetails,
                                () -> view.displayMessage("Order not found.")
                        );
            }
        }
    }

    private void viewAllUsers() {
        view.displayMessage("\n--- All Users ---");
        userManager.getAllUsers().forEach(user -> view.displayMessage(user.toString()));
        view.displayMessage("-----------------");
    }
}
