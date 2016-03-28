package commandlineparser.parser;

import java.util.ArrayList;

/**
 * Small Extension to ArrayList with getByArgumentKey method.
 */
public class ArgumentList extends ArrayList<ArgumentPair> {
    public ArgumentList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Iterate collection for ArgumentPair with specified argument.
     * @param argument
     * @return ArgumentPair if found, null if not.
     */
    public ArgumentPair getByArgumentKey(String argument) {
        return this.stream().filter(a -> a.getArgument().equals(argument)).findFirst().orElse(null);
    }
}
