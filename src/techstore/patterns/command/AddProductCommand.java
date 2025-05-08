// techstore/patterns/command/AddProductCommand.java
package techstore.patterns.command;

import techstore.model.Product;
import techstore.service.CatalogueService; // Using Proxy via interface

public class AddProductCommand implements Command {
    private CatalogueService catalogueService; // Could be proxy or real service
    private Product product;

    public AddProductCommand(CatalogueService catalogueService, Product product) {
        this.catalogueService = catalogueService;
        this.product = product;
    }

    @Override
    public void execute() {
        catalogueService.addProduct(product);
        System.out.println("Command: Added product " + product.getName());
    }
}