package commandlineparser.parser;

public class CommandArgument {
    private final String key;
    private final String description;
    private final boolean requireParam;

    String getKey() {
        return key;
    }
    String getDescription() {
        return description;
    }
    public boolean isRequireParam() {
        return requireParam;
    }

    public CommandArgument(String key, String description, boolean requireParam) {
        this.key = key;
        this.description = description;
        this.requireParam = requireParam;
    }
}
