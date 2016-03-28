package commandlineparser.parser;

import java.util.List;

public interface CommandHandler {
    String getKey();
    String getDescription();
    String getDetailedDescription();
    List<CommandArgument> getArguments();

    CommandResult ExecuteCommand(ArgumentList args);
}
