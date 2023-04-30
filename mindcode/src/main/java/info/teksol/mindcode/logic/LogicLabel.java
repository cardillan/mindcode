package info.teksol.mindcode.logic;

public class LogicLabel extends AbstractArgument implements LogicAddress {
    private final String label;

    private LogicLabel(String label) {
        super(ArgumentType.LABEL);
        this.label = label;
    }

    @Override
    public String toMlog() {
        return label;
    }

    @Override
    public String toString() {
        return "LogicLabel{" +
                "label='" + label + '\'' +
                '}';
    }

    public static LogicLabel symbolic(String name) {
        return new LogicLabel(name);
    }

    public static LogicLabel absolute(int address) {
        return new LogicLabel(String.valueOf(address));
    }
}
