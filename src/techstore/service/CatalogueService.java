package techstore.service;

import techstore.model.Product;
import techstore.model.Category;
import java.util.List;
import java.util.Optional;

public interface CatalogueService {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int productId);
    void removeProduct(int productId);
    Optional<Product> findProductById(int productId);
    List<Product> getAllProducts();
    List<Product> findProductsByCategory(Category category);
    // Category methods can also be here if proxied
    void addCategory(Category category);
    Optional<Category> findCategoryByName(String name);
    Optional<Category> findCategoryById(int id);
    List<Category> getAllCategories();
    void removeCategory(Category category);
    boolean isCategoryInUse(Category category);
    int getNextProductId(); //  for proxy to delegate if needed
}

// RealCatalogueService is CatalogueManager, which already implements CatalogueService.