// This will also serve as the RealSubject for the Proxy pattern.
// For simplicity, we'll make it a Singleton.
// It can implement an interface if we want to hide its Singleton nature from clients.
package techstore.patterns.singleton;

import techstore.model.Product;
import techstore.model.Category;
import techstore.service.CatalogueService; // For Proxy Pattern

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CatalogueManager implements CatalogueService { // Implements interface for Proxy
    private static CatalogueManager instance;
    private final List<Product> products;
    private final List<Category> categories;
    private int nextProductId = 1;

    private CatalogueManager() {
        products = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public static synchronized CatalogueManager getInstance() {
        if (instance == null) {
            instance = new CatalogueManager();
        }
        return instance;
    }

    public int getNextProductId() {
        return nextProductId++;
    }

    @Override
    public void addProduct(Product product) {
        // In a real system, ID would be managed by DB or ensure product given has unique ID
        // For simplicity, we assume product passed has ID already set (e.g. via builder + getNextProductId)
        products.removeIf(p -> p.getId() == product.getId()); // Remove if exists to update
        products.add(product);
        System.out.println("Product added/updated in RealCatalogueService: " + product.getName());
    }

    @Override
    public void updateProduct(Product product) {
        Optional<Product> existingProductOpt = findProductById(product.getId());
        if (existingProductOpt.isPresent()) {
            products.remove(existingProductOpt.get());
            products.add(product);
            System.out.println("Product updated in RealCatalogueService: " + product.getName());
        } else {
            System.out.println("Product with ID " + product.getId() + " not found for update.");
        }
    }


    @Override
    public void deleteProduct(int productId) {
        products.removeIf(p -> p.getId() == productId);
        System.out.println("Product deleted from RealCatalogueService: ID " + productId);
    }

    @Override
    public Optional<Product> findProductById(int productId) {
        return products.stream().filter(p -> p.getId() == productId).findFirst();
    }

    @Override
    public List<Product> getAllProducts() { // Iterator Pattern implicitly used by returning List
        return new ArrayList<>(products);
    }

    @Override
    public List<Product> findProductsByCategory(Category category) {
        return products.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public Optional<Category> findCategoryByName(String name) {
        return categories.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }
    public Optional<Category> findCategoryById(int id) {
        return categories.stream().filter(c -> c.getId() == id).findFirst();
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
}