package commandlineparser.handlers;

import commandlineparser.parser.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class PrintCommandHandler implements CommandHandler {
    private final List<CommandArgument> arguments = Arrays.asList(
        new CommandArgument("file", "File path to save output.", true),
        new CommandArgument("display", "Print to display flag.", false)
    );

    @Override
    public String getKey() {
        return "print";
    }

    @Override
    public String getDescription() {
        return "Prints passed output from previous command.";
    }
    
    @Override
    public String getDetailedDescription() {
        return "If no parameter specified, prints to display.";
    }

    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public CommandResult ExecuteCommand(ArgumentList args) {
        if (args == null)
            return null;

        String stringToPrint = args.getByArgumentKey(ComLineParser.getPassedOutputArgumentName()).getParameter();
        if (stringToPrint == null) {
            return new CommandResult(false, "No params to print.", true, null);
        }

        if (args.size() == 1) {
            System.out.println(stringToPrint);
            return new CommandResult(true, null, false, null);
        }

        Exception exception = null;
        String output = null;

        for (ArgumentPair arg: args) {
            switch (arg.getArgument()) {
                case "file":
                    String path = arg.getParameter();
                    try {
                        if (path.charAt(0) == '~') {    // paths doesn't recognize unix home directory shortcut
                            path = System.getProperty("user.home").concat(path.substring(1));
                        }

                        Files.write(Paths.get(path), stringToPrint.concat("\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    } catch (Exception e) {
                        exception = e;
                    }
                    output = exception == null ? "Message successfully written to ".concat(path)
                            : String.format("Attempt to write a message to '%s' threw an exception: %s", path, exception);
                    break;

                case "display":
                    System.out.println(stringToPrint);
                    break;
            }
        }

        return new CommandResult(exception == null, output, exception != null, exception);
    }
}
