package commandlineparser.parser;

import java.util.*;

/**
 * Command line parser.
 * @author Anokhov Pavel
 */
public class ComLineParser {
    private static final String HELP_COMMAND_KEY = "help";
    private static final String PASS_OUTPUT_COMMAND = ">>";
    private static final String PASSED_OUTPUT_ARGUMENT_NAME = "output";

    /**
     * Passed output argument name.
     * Using ">>" command, you can pass previous command output to next command (usually print).
     * @return Argument name.
     */
    public static String getPassedOutputArgumentName() {
        return PASSED_OUTPUT_ARGUMENT_NAME;
    }
    
    private String commandDelimiter;
    private String argumentDelimiter;
    private String title;

    /**
     * Sets Application title. Used as header in help command.
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private final CommandHandler passCommand;
    private final ArrayList<CommandHandler> handlers;
    {
        passCommand = new PassCommand();
        handlers = new ArrayList<>();
        handlers.add(passCommand);
        handlers.add(new HelpCommand(this));
    }

    public void AddCommandHandler(CommandHandler commandDelegate) {
        handlers.add(handlers.size() - 2, commandDelegate);   // help and pass command handlers always stays last
    }

    public ComLineParser(String title, String commandDelimiter, String argumentDelimiter) {
        // No equal
        if (commandDelimiter == null || argumentDelimiter == null
            || commandDelimiter.equals(argumentDelimiter))
            throw new IllegalArgumentException("Delimiters can't be null or equal.");

        if (argumentDelimiter.length() == 0)
            throw new IllegalArgumentException("Argument delimiter can't be empty.");

        this.title = title;
        this.commandDelimiter = commandDelimiter;
        this.argumentDelimiter = argumentDelimiter;
    }

    //region Main logic
    public void Parse(String[] args) {
        int comDelimLength = commandDelimiter.length();
        int argDelimLength = argumentDelimiter.length();

        ArrayList<HandlerBucket> handlerBuckets = new ArrayList<>();
        HandlerBucket activeBucket = null;

        for (int i = 0; i < args.length; i++) {
            if (IsCommand(args[i])) {
                String key = args[i].substring(comDelimLength);

                CommandHandler handler = null;
                for (CommandHandler h : handlers) {
                    if (h.getKey().equalsIgnoreCase(key)) {
                        handler = h;
                        break;
                    }
                }

                if (handler == null) {
                    ReportUnknownCommand(args[i]);
                    return;
                }

                if (activeBucket != null) {
                    handlerBuckets.add(activeBucket);
                }

                activeBucket = new HandlerBucket(handler);
            }

            // Arguments
            else if (args[i].startsWith(argumentDelimiter)) {
                String argument = args[i].substring(argDelimLength);
                if (activeBucket == null) {
                    ReportArgumentWithoutCommand(argument);
                    return;
                }

                if (argument.equals(HELP_COMMAND_KEY)) {
                    activeBucket.setHelpBucket(true);
                    continue;
                }

                CommandArgument commandArgument = null;
                for (CommandArgument a: activeBucket.handler.getArguments()) {
                    if (a.getKey().equals(argument)) {
                        commandArgument = a;
                        break;
                    }
                }

                if (commandArgument == null) {
                    ReportUnknownArg(activeBucket.handler, argument);
                    return;
                }

                String param = null;
                if (commandArgument.isRequireParam()) {
                    if (i+1 < args.length) {
                        String nextArg = args[i+1];

                        if ((!commandDelimiter.isEmpty() && nextArg.startsWith(commandDelimiter))
                                || nextArg.startsWith(argumentDelimiter)) {
                            ReportLackOfParameter(argument);
                            return;
                        }

                        if (nextArg.charAt(0) == '\'') {
                            StringBuilder sb = new StringBuilder();
                            do {
                                sb.append(' ').append(args[++i]);
                            } while (i < args.length && args[i].charAt(args[i].length()-1) != '\'');

                            int lastChar = sb.length() - 1;
                            if (sb.charAt(lastChar) == '\'') {
                                param = sb.substring(2, lastChar);
                            } else {
                                param = sb.substring(2);
                            }
                        } else {
                            param = nextArg;
                            i++;
                        }
                    } else {
                        ReportLackOfParameter(argument);
                        return;
                    }
                }


                if (activeBucket.arguments == null) {
                    activeBucket.arguments = new ArgumentList(1);
                }
                activeBucket.arguments.add(new ArgumentPair(argument, param));
            }

            // Unknown argument
            else {
                ReportUnknownCommand(args[i]);
                return;
            }
        }

        if (activeBucket != null) {
            handlerBuckets.add(activeBucket);
        }

        // Execute commands
        CommandResult prevResult = null;
        boolean passFlag = false;

        for (HandlerBucket bucket: handlerBuckets) {
            if (bucket.handler == passCommand) {
                passFlag = true;
                continue;
            } else if (!passFlag && prevResult != null && prevResult.isRequireOutput()) {
                System.out.println(prevResult.getOutput());
                prevResult = null;
            }

            if (bucket.isHelpBucket()) {
                prevResult = new CommandResult(true, GenerateCommandHandlerHelp(bucket.handler), true, null);
                continue;
            }

            if (passFlag) {
                if (bucket.arguments == null) {
                    bucket.arguments = new ArgumentList(1);
                }
                bucket.arguments.add(new ArgumentPair(PASSED_OUTPUT_ARGUMENT_NAME, prevResult != null ? prevResult.getOutput() : null));
                passFlag = false;
            }

            CommandResult result;
            try {
                result = bucket.handler.ExecuteCommand(bucket.arguments);
            } catch (Exception e) {
                result = new CommandResult(false, "Exception in handler: " + e.getMessage(), true, e);
            }

            prevResult = result;
        }

        if (prevResult != null && prevResult.isRequireOutput()) {
            System.out.println(prevResult.getOutput());
        }
    }

    private boolean IsCommand(String arg) {
        return commandDelimiter.isEmpty() && !arg.startsWith(argumentDelimiter)
                || (arg.startsWith(commandDelimiter) && !arg.startsWith(argumentDelimiter));
    }
    //endregion

    //region Report messages
    private void ReportArgumentWithoutCommand(String argument) {
        String message = "You must specify command for argument ".concat(argumentDelimiter).concat(argument);
        ReportError(message);
    }

    private void ReportUnknownCommand(String command) {
        String message = "Unknown command key: "
                .concat(commandDelimiter).concat(command)
                .concat(", use ")
                .concat(commandDelimiter).concat(HELP_COMMAND_KEY)
                .concat(" for command list.");
        ReportError(message);
    }

    private void ReportUnknownArg(CommandHandler command, String argument) {
        String message = "Command "
                .concat(commandDelimiter).concat(command.getKey())
                .concat(" doesn't has argument '")
                .concat(argumentDelimiter).concat(argument)
                .concat("', use '")
                .concat(commandDelimiter).concat(command.getKey())
                .concat(" ")
                .concat(argumentDelimiter).concat(HELP_COMMAND_KEY)
                .concat("' for argument list.");
        ReportError(message);
    }

    private void ReportLackOfParameter(String argument) {
        String message ="Parameter needed for argument "
                .concat(argumentDelimiter).concat(argument);
        ReportError(message);
    }

    private void ReportUnsuccessfulCommand(String key, String output, Exception e) {
        StringBuilder message = new StringBuilder("Error with ");
        message.append(key);

        if (e != null) {
            message.append(", exception was thrown: ").append(e.getMessage())
                    .append("\nTo get full output, use ").append(PASS_OUTPUT_COMMAND).append(" command.");
        } else if (output != null) {
            message.append(": ").append(output);
        }

        ReportError(message.toString());
    }

    private void ReportError(String error) {
        System.out.println(error);
    }
    //endregion

    //region Help messages
    private String GenerateHelp() {
        StringBuilder sb = new StringBuilder(getTitle() != null ? getTitle() : "");
        sb.append("\nUsage:\n").append(commandDelimiter).append("command")
                .append(' ').append(argumentDelimiter).append("argument")
                .append(' ').append("argumentParameter")
                .append(' ').append(argumentDelimiter).append("argument")
                .append(' ').append("'complex parameter'")
                .append(' ').append(commandDelimiter).append("nextCommand")
                .append(' ').append(argumentDelimiter).append("argument")
                .append(' ').append(commandDelimiter).append(PASS_OUTPUT_COMMAND)
                .append(' ').append(commandDelimiter).append("thirdCommand")
                .append('\n').append("The ").append(commandDelimiter).append(PASS_OUTPUT_COMMAND)
                .append(' ').append("command will pass previous command output to next as attribute.")
                .append('\n');

        sb.append("\nAvailable commands:\n");
        handlers.stream().forEach((handler) -> {
            sb.append('\t').append(commandDelimiter).append(handler.getKey()).append(": ")
                    .append(handler.getDescription()).append('\n');
        });
        sb.append("\nType '").append(commandDelimiter).append("command ")
                .append(argumentDelimiter).append("help' to see command's help.\n");

        return sb.toString();
    }

    private String GenerateCommandHandlerHelp(CommandHandler handler) {
        StringBuilder sb = new StringBuilder(handler.getKey());
        sb.append(": ").append(handler.getDescription());
        
        String detailed = handler.getDetailedDescription();
        if (detailed != null && detailed.length() > 0) {
            sb.append('\n').append(detailed);
        }
        
        List<CommandArgument> arguments = handler.getArguments();
        if (arguments != null) {
            sb.append("\n\nArguments:");

            for (CommandArgument argument: arguments) {
                sb.append('\n').append(argumentDelimiter).append(argument.getKey());
                if (argument.isRequireParam()) {
                    sb.append(" {param}");
                }
                sb.append(": ").append(argument.getDescription());
            }
        }

        return sb.toString();
    }
    //endregion

    //region Private classes
    private class HandlerBucket {
        final CommandHandler handler;
        private boolean helpBucket;
        ArgumentList arguments;

        private HandlerBucket(CommandHandler handler) {
            this.handler = handler;
        }

        boolean isHelpBucket() {
            return helpBucket;
        }

        void setHelpBucket(boolean helpBucket) {
            this.helpBucket = helpBucket;
        }
    }

    private class HelpCommand implements CommandHandler {
        private final ComLineParser owner;

        HelpCommand(ComLineParser owner) {
            this.owner = owner;
        }

        @Override
        public String getKey() {
            return ComLineParser.HELP_COMMAND_KEY;
        }

        @Override
        public String getDescription() {
            return "Displays help.";
        }
        
        @Override
        public String getDetailedDescription() {
            return null;
        }

        @Override
        public List<CommandArgument> getArguments() {
            return null;
        }

        @Override
        public CommandResult ExecuteCommand(ArgumentList args)
        {
            String output = owner.GenerateHelp();
            return new CommandResult(true, output, true, null);
        }
    }

    private class PassCommand implements CommandHandler {
        @Override
        public String getKey() {
            return ComLineParser.PASS_OUTPUT_COMMAND;
        }

        @Override
        public String getDescription() {
            return "Pass previous command output to next command.";
        }

        @Override
        public String getDetailedDescription() {
            return null;
        }
        
        @Override
        public List<CommandArgument> getArguments() {
            return null;
        }

        @Override
        public CommandResult ExecuteCommand(ArgumentList args) {
            return null;
        }
    }
    //endregion
}
