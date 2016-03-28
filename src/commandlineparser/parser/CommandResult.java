package commandlineparser.parser;

public class CommandResult {
    final boolean isSuccess;
    final String output;
    final Exception exception;
    final boolean requireOutput;

    public CommandResult(boolean isSuccess, String output, boolean requireOutput, Exception exception) {
        this.isSuccess = isSuccess;
        this.output = output;
        this.exception = exception;
        this.requireOutput = requireOutput;
    }
}
