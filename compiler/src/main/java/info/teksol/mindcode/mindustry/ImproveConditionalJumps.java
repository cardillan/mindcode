package info.teksol.mindcode.mindustry;

import java.util.Map;
import java.util.Set;

// Turns the following sequence of instructions:
//    op <comparison> var1 A B
//    jump label notEqual var2 true
//
// into
//    jump label <inverse of comparison> A B
//
// Requirements:
// 1. jump is a notEqual comparison to true
// 2. var1 and var2 are identical
// 3. var1 is a __tmp variable
// 4. <comparison> has an inverse
public class ImproveConditionalJumps implements LogicInstructionPipeline {
    private static final Map<String, String> inverses = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan"
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

            // Not a conditional jump
            if (!instruction.isJump() || !instruction.getArgs().get(1).equals("notEqual")) {
                next.emit(op);
                next.emit(instruction);
                return new EmptyState();
            }

            // Other preconditions for the optimisation
            boolean isSameVariable = instruction.getArgs().get(2).equals(op.getArgs().get(1));
            boolean jumpComparesToTrue = instruction.getArgs().get(3).equals("true");
            
            if (isSameVariable && jumpComparesToTrue) {
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
            
            next.emit(op);
            next.emit(instruction);
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
