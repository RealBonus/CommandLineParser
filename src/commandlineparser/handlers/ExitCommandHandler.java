package commandlineparser.handlers;

import commandlineparser.parser.*;

public class ExitCommandHandler implements CommandHandler {
    private boolean exitRequested;

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void setExitRequested(boolean exitRequested) {
        this.exitRequested = exitRequested;
    }

    @Override
    public String getKey() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit program";
    }
    
    @Override
    public String getDetailedDescription() {
        return null;
    }

    @Override
    public CommandArgument[] getArguments() {
        return null;
    }

    @Override
    public CommandResult ExecuteCommand(ArgumentList args) {
        setExitRequested(true);
        return null;
    }
}
