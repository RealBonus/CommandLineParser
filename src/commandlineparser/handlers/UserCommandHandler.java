package commandlineparser.handlers;

import commandlineparser.AccountManager;
import commandlineparser.User;
import commandlineparser.parser.*;

public class UserCommandHandler implements CommandHandler {
    private AccountManager accountManager;

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
    
    private final CommandArgument[] arguments = new CommandArgument[] {
            new CommandArgument("login", "User login.", true),
            new CommandArgument("pass", "User password.", true),
            new CommandArgument("logout", "Logout from current user.", false)
    };

    @Override
    public String getKey() {
        return "user";
    }

    @Override
    public String getDescription() {
        return "User related operations.";
    }
    
    @Override
    public String getDetailedDescription() {
        return "If no attributes specified, return information about current user."
                + " To login, you must specify booth login and password attributes.";
    }

    @Override
    public CommandArgument[] getArguments() {
        return arguments;
    }

    @Override
    public CommandResult ExecuteCommand(ArgumentList args) {
        if (accountManager == null){
            return new CommandResult(false, "Account manager not initialized", true, new NullPointerException("accountManager"));
        }

        if (args == null) {
            return ResponseCurrentUser();
        }

        ArgumentPair logout = args.getByArgumentKey("logout");
        if (logout != null) {
            if (getAccountManager().getLoggedUser() != null) {
                getAccountManager().Logout();
                return new CommandResult(true, "User logged out.", true, null);
            } else {
                return new CommandResult(true, "No current user to log out.", false, null);
            }
        }

        ArgumentPair login = args.getByArgumentKey("login");
        ArgumentPair pass = args.getByArgumentKey("pass");

        if (login == null && pass == null) {
            return ResponseCurrentUser();
        }

        if ((login == null || pass == null)) {
            return new CommandResult(false, "You must specify booth login and password to login.", true, null);
        }

        boolean success;
        Exception e = null;
        try {
            success = getAccountManager().LogUser(login.getParameter(), pass.getParameter());
        } catch (Exception exc) {
            e = exc;
            success = false;
        }

        String output;
        if (success) {
            User user = getAccountManager().getLoggedUser();
            output = "Logged as ".concat(user.getLogin()).concat(".");
        }
        else {
            output = "Failed login for ".concat(login.getParameter());
        }

        return new CommandResult(success, output, true, e);
    }

    private CommandResult ResponseCurrentUser() {
        User loggedUser = getAccountManager().getLoggedUser();
        String output = "Logged user: ".concat(loggedUser != null ? loggedUser.getLogin() : "(none)");
        return new CommandResult(true, output, true, null);
    }
}
