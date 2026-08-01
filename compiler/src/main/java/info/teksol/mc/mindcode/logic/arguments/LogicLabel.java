package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LogicLabel extends AbstractArgument implements LogicAddress {
    private final String label;
    private final String mlog;
    private final int address;
    private final boolean stateTransfer;
    private final boolean remote;
    private final int offset;

    private LogicLabel(String label, int address, boolean stateTransfer, boolean remote, int offset) {
        super(ArgumentType.LABEL, ValueMutability.IMMUTABLE);
        this.label = label;
        this.mlog = label + (offset == 0 ? "" : offset < 0 ? "" + offset : "+" + offset);
        this.address = address;
        this.stateTransfer = stateTransfer;
        this.remote = remote;
        this.offset = offset;
    }

    public boolean isRemote() {
        return remote;
    }

    public boolean isStateTransfer() {
        return stateTransfer;
    }

    public String getLabel() {
        return label;
    }

    public int getAddress() {
        return address;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    @Override
    public String toString() {
        return "LogicLabel{" +
                "label='" + label + '\'' +
                '}';
    }

    public LogicLabel remote() {
        return new LogicLabel(label, address, stateTransfer, true, 0);
    }

    public LogicLabel withoutStateTransfer() {
        return new LogicLabel(label, address, false, remote, 0);
    }

    public LogicLabel withOffset(int offset) {
        if (offset == 0) return this;
        if (address == -1) return new LogicLabel(label, -1, stateTransfer, remote, offset);
        return new LogicLabel(String.valueOf(address + offset), address + offset, stateTransfer, remote, 0);
    }

    public static LogicLabel symbolic(String name) {
        return new LogicLabel(name, -1, true, false, 0);
    }

    public static LogicLabel absolute(int address) {
        return new LogicLabel(String.valueOf(address), address, true, false, 0);
    }

    public static final LogicLabel EMPTY = symbolic("");
    public static final LogicLabel INVALID = symbolic("invalid");
}
