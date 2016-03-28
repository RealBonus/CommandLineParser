package commandlineparser.parser;

public interface CommandHandler {
    String getKey();
    String getDescription();
    String getDetailedDescription();
    CommandArgument[] getArguments();

    CommandResult ExecuteCommand(ArgumentList args);
}
