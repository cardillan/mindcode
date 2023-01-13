package info.teksol.mindcode.mindustry;

import java.util.Map;
import java.util.Set;

// Turns the following sequence of instructions:
//    op <comparison> __tmpX A B
//    jump label notEqual __tmpX true
//
// into
//    jump label <inverse of comparison> A B
//
public class ImproveConditionalJumps implements LogicInstructionPipeline {
    private static final Map<String, String> inverses = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan",
            "strictEqual", "notEqual"
    );
    private static final Set<String> COMPARISON_OPERATORS = inverses.keySet();
    private final LogicInstructionPipeline next;
    private State state;

    ImproveConditionalJumps(LogicInstructionPipeline next) {
        this.next = next;
        this.state = new EmptyState();
    }

    @Override
    public void emit(LogicInstruction instruction) {
        state = state.emit(instruction);
    }

    @Override
    public void flush() {
        state = state.flush();
        next.flush();
    }

    private interface State {
        State emit(LogicInstruction instruction);

        State flush();
    }

    private final class EmptyState implements State {

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isOp() && isComparisonOperatorToTmp(instruction)) {
                return new ExpectJump(instruction);
            } else {
                next.emit(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectJump implements State {
        private final LogicInstruction op;

        ExpectJump(LogicInstruction op) {
            this.op = op;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isOp()) {
                if (!isComparisonOperatorToTmp(instruction)) {
                    next.emit(op);
                    next.emit(instruction);
                    return new EmptyState();
                } else {
                    next.emit(op);
                    return new ExpectJump(instruction);
                }
            }

            if (!instruction.isJump()) {
                next.emit(op);
                next.emit(instruction);
                return new EmptyState();
            }
            
            boolean merge = instruction.getArgs().get(1).equals("notEqual") &&
                    instruction.getArgs().get(2).equals(op.getArgs().get(1)) &&
                    instruction.getArgs().get(3).equals("true");
            
            if (!merge) {
                next.emit(op);
                next.emit(instruction);
                return new EmptyState();
            }
            
            if (!inverses.containsKey(op.getArgs().get(0))) {
                throw new IllegalArgumentException("Unknown operation passed-in; can't find the inverse of [" + op.getArgs().get(0) + "]");
            }
            
            next.emit(
                    new LogicInstruction(
                            "jump",
                            instruction.getArgs().get(0),
                            inverses.get(op.getArgs().get(0)),
                            op.getArgs().get(2),
                            op.getArgs().get(3)
                    )
            );
            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(op);
            return new EmptyState();
        }

    }
    
    private boolean isComparisonOperatorToTmp(LogicInstruction instruction) {
        return COMPARISON_OPERATORS.contains(instruction.getArgs().get(0)) &&
                instruction.getArgs().get(1).startsWith(LogicInstructionGenerator.TMP_PREFIX);
    }
}
