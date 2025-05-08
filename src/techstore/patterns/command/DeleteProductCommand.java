package techstore.patterns.command;
import techstore.service.CatalogueService;

public class DeleteProductCommand implements Command {
    private CatalogueService catalogueService;
    private int productIdToDelete;

    public DeleteProductCommand(CatalogueService catalogueService, int productIdToDelete) {
        this.catalogueService = catalogueService;
        this.productIdToDelete = productIdToDelete;
    }
    @Override
    public void execute() {
        catalogueService.deleteProduct(productIdToDelete);
        System.out.println("Command: Deleted product with ID " + productIdToDelete);
    }
}