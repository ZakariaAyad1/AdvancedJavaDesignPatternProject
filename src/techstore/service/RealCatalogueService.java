package techstore.service;

import techstore.model.Category;
import techstore.model.Product;
import techstore.patterns.singleton.CatalogueManager;

import java.util.List;
import java.util.Optional;

public class RealCatalogueService implements CatalogueService {
    @Override
    public void addProduct(Product product) {

    }

    @Override
    public void updateProduct(Product product) {

    }

    @Override
    public void deleteProduct(int productId) {

    }

    @Override
    public void removeProduct(int productId) {
        CatalogueManager.getInstance().removeProduct(productId);
    }

    @Override
    public Optional<Product> findProductById(int productId) {
        return Optional.empty();
    }

    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public List<Product> findProductsByCategory(Category category) {
        return List.of();
    }

    @Override
    public void addCategory(Category category) {

    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Category> findCategoryById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }

    @Override
    public void removeCategory(Category category) {

    }

    @Override
    public boolean isCategoryInUse(Category category) {
        return false;
    }

    @Override
    public int getNextProductId() {
        return 0;
    }

    @Override
    public List<Product> getProductsByCategory(Category selectedCategory) {
        return List.of();
    }
}
