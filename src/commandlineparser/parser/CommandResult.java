package commandlineparser.parser;

public class CommandResult {
    private final boolean success;
    private final String output;
    private final Exception exception;
    private final boolean requireOutput;

    public boolean isIsSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isRequireOutput() {
        return requireOutput;
    }
    
    public CommandResult(boolean success, String output, boolean requireOutput, Exception exception) {
        this.success = success;
        this.output = output;
        this.exception = exception;
        this.requireOutput = requireOutput;
    }
}
