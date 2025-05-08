// Similarly, create UpdateProductCommand and DeleteProductCommand
// techstore/patterns/command/UpdateProductCommand.java
package techstore.patterns.command;
import techstore.model.Product;
import techstore.service.CatalogueService;

public class UpdateProductCommand implements Command {
    private CatalogueService catalogueService;
    private Product productToUpdate;

    public UpdateProductCommand(CatalogueService catalogueService, Product productToUpdate) {
        this.catalogueService = catalogueService;
        this.productToUpdate = productToUpdate;
    }
    @Override
    public void execute() {
        catalogueService.updateProduct(productToUpdate);
        System.out.println("Command: Updated product " + productToUpdate.getName());
    }
}