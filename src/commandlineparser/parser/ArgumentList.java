package commandlineparser.parser;

import java.util.ArrayList;

public class ArgumentList extends ArrayList<ArgumentPair> {
    public ArgumentList(int initialCapacity) {
        super(initialCapacity);
    }

    public ArgumentPair getByArgumentKey(String argument) {
        return this.stream().filter(a -> a.getArgument().equals(argument)).findFirst().orElse(null);
    }
}
