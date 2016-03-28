package commandlineparser.handlers;

import commandlineparser.Product;
import commandlineparser.StorageManager;
import commandlineparser.parser.*;
import java.util.Arrays;

import java.util.List;

public class ListCommandHandler implements CommandHandler {
    private StorageManager storageManager;
    private final List<CommandArgument> arguments = Arrays.asList(
            new CommandArgument("priceFrom", "Lowest price.", true),
            new CommandArgument("priceTo", "Highest price.", true)
    );

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public String getKey() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Print list of products.";
    }
    
    @Override
    public String getDetailedDescription() {
        return null;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public CommandResult ExecuteCommand(ArgumentList args) {
        if (storageManager == null) {
            return new CommandResult(false, "Storage manager not initialized", true, new NullPointerException("storageManager"));
        }

        double lowestPrice = 0;
        double highestPrice = 0;
        if (args != null) {
            ArgumentPair low = args.getByArgumentKey("priceFrom");
            ArgumentPair height = args.getByArgumentKey("priceTo");

            try {
                if (low != null) {
                    lowestPrice = Double.parseDouble(low.getParameter());
                }
                if (height != null) {
                    highestPrice = Double.parseDouble(height.getParameter());
                }
            } catch (NumberFormatException e) {
                return new CommandResult(false, "Error parsing price parameter", true, e);
            }

            if (lowestPrice < 0 || highestPrice < 0) {
                return new CommandResult(false, "Price can't be less than zero.", true, null);
            }
        }

        List<Product> products = storageManager.getProducts();
        StringBuilder sb = new StringBuilder();

        for (Product product: products) {
            if (product.getPrice() < lowestPrice)
                continue;

            if (highestPrice > 0 && product.getPrice() > highestPrice)
                continue;

            String name = product.getName();
            sb.append(name != null ? name : "(unnamed)").append(":\t")
                    .append(product.getPrice()).append("₽\n");
        }

        return new CommandResult(true, sb.toString(), true, null);
    }
}
