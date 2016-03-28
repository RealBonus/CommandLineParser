package commandlineparser.parser;

import java.util.List;

/**
 * Command handler.
 * @author Pavel
 */
public interface CommandHandler {
    /**
     * Command key.
     * @return key.
     */
    String getKey();
    
    /**
     * Command short description. 
     * Used in general help.
     * @return description.
     */
    String getDescription();
    
    /**
     * Command detailed description.
     * Used in command's help (-help attribute).
     * @return detailed description.
     */
    String getDetailedDescription();
    
    /**
     * Command arguments.
     * List of supported arguments with descriptions.
     * @return arguments.
     */
    List<CommandArgument> getArguments();

    /**
     * Execute command with arguments and parameters.
     * @param args Ordered list of arguments.
     * @return Execution result.
     */
    CommandResult ExecuteCommand(ArgumentList args);
}
