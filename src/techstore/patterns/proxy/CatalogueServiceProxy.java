// RealCatalogueService is CatalogueManager, which already implements CatalogueService.

// techstore/patterns/proxy/CatalogueServiceProxy.java
package techstore.patterns.proxy;

import techstore.model.Admin;
import techstore.model.Product;
import techstore.model.Category;
import techstore.model.User;
import techstore.patterns.singleton.CatalogueManager;
import techstore.service.CatalogueService;

import java.util.List;
import java.util.Optional;

public class CatalogueServiceProxy implements CatalogueService {
    private CatalogueManager realCatalogueService; // Real Subject (Singleton)
    private User currentUser;

    public CatalogueServiceProxy(User currentUser) {
        this.realCatalogueService = CatalogueManager.getInstance();
        this.currentUser = currentUser;
    }

    private boolean isAdmin() {
        return currentUser instanceof Admin;
    }

    @Override
    public void addProduct(Product product) {
        if (isAdmin()) {
            realCatalogueService.addProduct(product);
        } else {
            System.out.println("Access Denied: Only Admins can add products.");
        }
    }

    @Override
    public void updateProduct(Product product) {
        if (isAdmin()) {
            realCatalogueService.updateProduct(product);
        } else {
            System.out.println("Access Denied: Only Admins can update products.");
        }
    }

    @Override
    public void deleteProduct(int productId) {
        if (isAdmin()) {
            realCatalogueService.deleteProduct(productId);
        } else {
            System.out.println("Access Denied: Only Admins can delete products.");
        }
    }

    @Override
    public int getNextProductId() {
        // ID generation might be considered a privileged operation or not.
        // If it's just reading, anyone can. If it implies a write lock, admin only.
        // For now, delegate directly.
        return realCatalogueService.getNextProductId();
    }


    // Read operations are allowed for everyone
    @Override
    public Optional<Product> findProductById(int productId) {
        return realCatalogueService.findProductById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return realCatalogueService.getAllProducts();
    }

    @Override
    public List<Product> findProductsByCategory(Category category) {
        return realCatalogueService.findProductsByCategory(category);
    }

    @Override
    public void addCategory(Category category) {
        if (isAdmin()) {
            realCatalogueService.addCategory(category);
        } else {
            System.out.println("Access Denied: Only Admins can add categories.");
        }
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return realCatalogueService.findCategoryByName(name);
    }
    @Override
    public Optional<Category> findCategoryById(int id) {
        return realCatalogueService.findCategoryById(id);
    }


    @Override
    public List<Category> getAllCategories() {
        return realCatalogueService.getAllCategories();
    }
}