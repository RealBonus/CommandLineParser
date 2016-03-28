package commandlineparser.parser;

/**
 * Command execution result.
 */
public class CommandResult {
    private final boolean success;
    private final String output;
    private final Exception exception;
    private final boolean requireOutput;

    /**
     * Command executed successfully.
     * @return success.
     */
    public boolean isIsSuccess() {
        return success;
    }

    /**
     * Command output.
     * @return output.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Exception, if command failed.
     * @return exception.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * If this flag is false, output will be printed only with "print" command.
     * @return flag.
     */
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
