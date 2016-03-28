package commandlineparser;

import commandlineparser.handlers.*;
import commandlineparser.parser.ComLineParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandLineParser {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AccountManager accountManager = new AccountManager("users.json");
        StorageManager storageManager = new StorageManager("products.json");

        UserCommandHandler userCommandHandler = new UserCommandHandler();
        ListCommandHandler listCommandHandler = new ListCommandHandler();
        ProductCommandHandler productCommandHandler = new ProductCommandHandler();
        userCommandHandler.setAccountManager(accountManager);
        listCommandHandler.setStorageManager(storageManager);
        productCommandHandler.setAccountManager(accountManager);
        productCommandHandler.setStorageManager(storageManager);

        ExitCommandHandler exitCommandHandler = new ExitCommandHandler();

        ComLineParser parser = new ComLineParser("<<< CommandLineParser Mega Demo >>>", "", "-");
        parser.AddCommandHandler(userCommandHandler);
        parser.AddCommandHandler(listCommandHandler);
        parser.AddCommandHandler(productCommandHandler);
        parser.AddCommandHandler(new PrintCommandHandler());
        parser.AddCommandHandler(exitCommandHandler);

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        boolean exitRequested = false;
        while (!exitCommandHandler.isExitRequested() || exitRequested) {
            System.out.println("Enter command:");
            try{
                String input = bufferRead.readLine();

                parser.Parse(input.split(" "));
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                exitRequested = true;
            }
        }

        System.out.println("Good bye!");
    }
}
