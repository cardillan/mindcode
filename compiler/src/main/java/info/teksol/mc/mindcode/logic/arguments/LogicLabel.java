package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LogicLabel extends AbstractArgument implements LogicAddress {
    private final String label;
    private final int address;
    private final boolean stateTransfer;

    private LogicLabel(String label, int address, boolean stateTransfer) {
        super(ArgumentType.LABEL, ValueMutability.IMMUTABLE);
        this.label = label;
        this.address = address;
        this.stateTransfer = stateTransfer;
    }

    public boolean isStateTransfer() {
        return stateTransfer;
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

    public LogicLabel withoutStateTransfer() {
        return new LogicLabel(label, address, false);
    }

    public static LogicLabel symbolic(String name) {
        return new LogicLabel(name, -1, true);
    }

    public static LogicLabel absolute(int address) {
        return new LogicLabel(String.valueOf(address), address, true);
    }

    public static final LogicLabel INVALID = symbolic("invalid");
}
