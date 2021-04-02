package info.teksol.mindcode.mindustry;

import java.util.Map;
import java.util.Set;

public class ImproveConditionalJumps implements LogicInstructionPipeline {
    private static final Set<String> COMPARISON_OPERATORS = Set.of(
            "equal",
            "notEqual",
            "lessThan",
            "lessThanEq",
            "greaterThan",
            "greaterThanEq",
            "strictEqual"
    );
    private static final Map<String, String> inverses = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan",
            "strictEqual", "notEqual"
    );
    private final LogicInstructionPipeline next;
    private LogicInstruction previous;

    ImproveConditionalJumps(LogicInstructionPipeline next) {
        this.next = next;
    }

    @Override
    public void emit(LogicInstruction instruction) {
        if (instruction.isJump()) {
            handleJump(instruction);
        } else if (instruction.isOp()) {
            handleOp(instruction);
        } else {
            flushNext(instruction);
        }
    }

    @Override
    public void flush() {
        if (previous != null) {
            next.emit(previous);
            previous = null;
        }

        next.flush();
    }

    private void handleJump(LogicInstruction instruction) {
        if (previous == null) {
            flushNext(instruction);
            return;
        }

        if (!instruction.getArgs().get(1).equals("notEqual")) {
            flushNext(instruction);
            return;
        }

        if (!inverses.containsKey(previous.getArgs().get(0))) {
            throw new IllegalArgumentException("Unknown operation passed-in; can't find the inverse of [" + previous.getArgs().get(0) + "]");
        }

        next.emit(
                new LogicInstruction(
                        "jump",
                        instruction.getArgs().get(0),
                        inverses.get(previous.getArgs().get(0)),
                        previous.getArgs().get(2),
                        previous.getArgs().get(3)
                )
        );
        previous = null;
    }

    private void handleOp(LogicInstruction instruction) {
        if (!isComparisonOperator(instruction)) {
            flushNext(instruction);
            return;
        }

        previous = instruction;
    }

    private void flushNext(LogicInstruction instruction) {
        flush();
        next.emit(instruction);
    }

    private boolean isComparisonOperator(LogicInstruction instruction) {
        return COMPARISON_OPERATORS.contains(instruction.getArgs().get(0));
    }
}
