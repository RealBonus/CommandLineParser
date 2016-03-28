package commandlineparser.parser;

/**
 * Describes command argument.
 * @author Pavel
 */
public class CommandArgument {
    private final String key;
    private final String description;
    private final boolean requireParam;

    /**
     * Argument key.
     * @return key.
     */
    String getKey() {
        return key;
    }
    
    /**
     * Argument short description.
     * @return description.
     */
    String getDescription() {
        return description;
    }
    
    /**
     * Argument require additional parameter.
     * @return flag.
     */
    public boolean isRequireParam() {
        return requireParam;
    }

    public CommandArgument(String key, String description, boolean requireParam) {
        this.key = key;
        this.description = description;
        this.requireParam = requireParam;
    }
}
