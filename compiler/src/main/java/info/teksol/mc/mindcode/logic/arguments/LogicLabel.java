package info.teksol.mc.mindcode.logic.arguments;

public class LogicLabel extends AbstractArgument implements LogicAddress {
    private final String label;
    private final int address;

    private LogicLabel(String label, int address) {
        super(ArgumentType.LABEL);
        this.label = label;
        this.address = address;
    }

    public int getAddress() {
        return address;
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
        return new LogicLabel(name, -1);
    }

    public static LogicLabel absolute(int address) {
        return new LogicLabel(String.valueOf(address), address);
    }

    public static final LogicLabel INVALID = symbolic("invalid");
}
