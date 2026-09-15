package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class StackTracker {
    private final LogicVariable stackPointer;
    private final LogicVariable stackMemory;

    public StackTracker(LogicVariable stackPointer, LogicVariable stackMemory) {
        this.stackPointer = stackPointer;
        this.stackMemory = stackMemory;
    }

    private List<LogicVariable> stackStorage = List.of();
    private int allocationStart;
    private int allocationEnd;

    public boolean externalStack() {
        return !stackStorage.isEmpty();
    }

    public boolean simpleStack() {
        return stackStorage.size() == 1;
    }

    public boolean largeStack() {
        return stackStorage.size() > 1;
    }

    public void setStackMemory(List<LogicVariable> stackMemory, int allocationStart, int allocationEnd) {
        this.stackStorage = Objects.requireNonNull(stackMemory);
        this.allocationStart = allocationStart;
        this.allocationEnd = allocationEnd;
    }

    public int getAllocationStart() {
        return allocationStart;
    }

    public int getAllocationEnd() {
        return allocationEnd;
    }

    public LogicVariable getStackPointer() {
        return stackPointer;
    }

    public LogicVariable getStackMemory() {
        return stackStorage.size() == 1 ? stackStorage.getFirst() : stackMemory;
    }

    public List<LogicVariable> getStackStorage() {
        return stackStorage;
    }

    public static StackTracker mockInternalStack() {
        return new StackTracker(LogicVariable.INVALID, LogicVariable.INVALID);
    }

    public static StackTracker mockExternalStack() {
        return new StackTracker(LogicVariable.preserved("*sp"),
                LogicVariable.block(SourcePosition.EMPTY, "bank0", "bank")) {
            @Override
            public boolean externalStack() {
                return true;
            }
        };
    }
}
