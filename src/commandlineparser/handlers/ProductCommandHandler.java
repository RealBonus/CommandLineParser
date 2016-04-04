package commandlineparser.handlers;

import commandlineparser.AccountManager;
import commandlineparser.Product;
import commandlineparser.StorageManager;
import commandlineparser.User;
import commandlineparser.parser.*;
import java.util.Arrays;
import java.util.List;

public class ProductCommandHandler implements CommandHandler {
    private AccountManager accountManager;
    private StorageManager storageManager;
    private final List<CommandArgument> arguments = Arrays.asList(
        new CommandArgument("create", "Create product with name and set it as active", true),
        new CommandArgument("find", "Search product by name and set it as active.", true),
        new CommandArgument("remove", "Remove active product.", false),
        new CommandArgument("setName", "Rename active product.", true),
        new CommandArgument("setPrice", "Set active product price.", true),
        new CommandArgument("setAmount", "Set active product amount in storage.", true)
    );

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }


    @Override
    public String getKey() {
        return "product";
    }

    @Override
    public String getDescription() {
        return "Operations with products.";
    }
    
    @Override
    public String getDetailedDescription() {
        return "Use 'find' argument to find a product or 'create' to create new product and set it as Active."
                + " After that, use other arguments to manipulate Active product."
                + "\n\nExample:\nproduct -create 'Sony Toothpick MDR50' -setPrice 4900.75 -setStorage 170 -find 'Xiaomi Toothpick M2' -remove";
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public CommandResult ExecuteCommand(ArgumentList args) {
        if (accountManager == null){
            return new CommandResult(false, "Account manager not initialized", true, new NullPointerException("accountManager"));
        }

        if (storageManager == null) {
            return new CommandResult(false, "Storage manager not initialized", true, new NullPointerException("storageManager"));
        }

        User user = accountManager.getLoggedUser();
        if (user == null || !user.getLogin().equals("admin")) {
            return new CommandResult(false, "Only admin and managers can work with products.", true, null);
        }

        if (args == null) {
            return new CommandResult(false, "No params", true, null);
        }

        int created = 0;
        int removed = 0;

        Product activeProduct = null;
        for (ArgumentPair arg: args) {
            switch (arg.getArgument()) {
                case "create":
                    String name = arg.getParameter();
                    if (name == null || name.isEmpty())
                        return ErrorResult("create", "Specify product name.", true, null);

                    activeProduct = getStorageManager().CreateNewProduct(name);
                    created++;
                    break;

                case "find":
                    name = arg.getParameter();
                    if (name == null || name.isEmpty())
                        return ErrorResult("find", "Specify product name.", true, null);
                    activeProduct = getStorageManager().FindProduct(name);
                    if (activeProduct == null)
                        return ErrorResult("find", "Can't find product named ".concat(name), true, null);
                    break;

                case "setName":
                    if (activeProduct == null)
                        return ErrorResult("setName", "Find or create product first", true, null);
                    name = arg.getParameter();
                    if (name == null || name.length() == 0)
                        return ErrorResult("setName", "Specify new product name.", true, null);

                    break;

                case "setPrice":
                    if (activeProduct == null)
                        return ErrorResult("setPrice", "Find or create product first", true, null);

                    try {
                        double price = Double.parseDouble(arg.getParameter());
                        activeProduct.setPrice(price);
                    } catch (Exception e) {
                        return ErrorResult("setPrice", "Error parsing price", true, e);
                    }
                    break;

                case "setAmount":
                    if (activeProduct == null)
                        return ErrorResult("setAmount", "Find or create product first", true, null);

                    try {
                        int amount = Integer.parseInt(arg.getParameter());
                        activeProduct.setAmount(amount);
                    } catch (Exception e) {
                        return ErrorResult("setAmount", "Error parsing price", true, e);
                    }
                    break;

                case "remove":
                    if (activeProduct == null)
                        return ErrorResult("remove", "Find or create product first", true, null);

                    if (getStorageManager().RemoveProduct(activeProduct))
                        removed++;

                    break;
            }
        }

        StringBuilder sb = new StringBuilder();
        if (created > 0) {
            sb.append("Products created: ").append(created);
        }
        if (removed > 0) {
            if (sb.length() > 0) {
                sb.append(", removed: ").append(removed);
            } else {
                sb.append("Products removed: ").append(removed);
            }
        }
        if (sb.length() > 0)
            sb.append('.');

        try {
            storageManager.SaveStorage();
        } catch (Exception e) {
            return new CommandResult(false, "Problem saving storage.", true, e);
        }

        return new CommandResult(true, sb.toString(), true, null);
    }

    private CommandResult ErrorResult(String command, String message, boolean required, Exception e) {
        message = String.format("%s: %s", command, message);
        return new CommandResult(false, message, required, e);
    }
}
