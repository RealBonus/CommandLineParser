package commandlineparser.parser;

import java.util.Objects;

public class ArgumentPair {
    private final String argument;
    private final String parameter;

    public String getArgument() {
        return argument;
    }

    public String getParameter() {
        return parameter;
    }

    public ArgumentPair(String argument, String parameter) {
        this.argument = argument;
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if (!(obj instanceof ArgumentPair)) {
            return false;
        } else {
            ArgumentPair other = ArgumentPair.class.cast(obj);
            return argument.equals(other.argument) && parameter.equals(other.parameter);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.argument);
        hash = 97 * hash + Objects.hashCode(this.parameter);
        return hash;
    }
}
