package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class StackTracker {
    private static final LogicVariable NO_STACK = LogicVariable.block(SourcePosition.EMPTY, "bank0");

    private LogicVariable stackMemory = NO_STACK;
    private int allocationStart;
    private int allocationEnd;

    public boolean externalStack() {
        return stackMemory != NO_STACK;
    }

    public void setStackMemory(LogicVariable stackMemory, int allocationStart, int allocationEnd) {
        this.stackMemory = Objects.requireNonNull(stackMemory);
        this.allocationStart = allocationStart;
        this.allocationEnd = allocationEnd;
    }

    public int getAllocationStart() {
        return allocationStart;
    }

    public int getAllocationEnd() {
        return allocationEnd;
    }

    public LogicVariable getStackMemory() {
        return stackMemory;
    }

    public static StackTracker withExternalStack() {
        return new StackTracker() {
            @Override
            public boolean externalStack() {
                return true;
            }
        };
    }
}
