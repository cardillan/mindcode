package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.instructions.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.PackColorInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.OUT_OF_LINE_CALL;
import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.RECURSIVE_CALL;

public class DataFlowVariableStates {

    private final DataFlowOptimizer optimizer;

    private final InstructionProcessor instructionProcessor;

    public DataFlowVariableStates(DataFlowOptimizer optimizer) {
        this.optimizer = optimizer;
        instructionProcessor = optimizer.instructionProcessor;
    }

    VariableStates createVariableStates() {
        return new VariableStates();
    }

    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Describes known states of variables. Instances are primarily created by analysing code blocks, and then
     * merged when two or more code branches merge. Merging operations may produce variables with multiple definitions.
     * When merging two instances prescribing different values/expressions to the same variable, the values/expressions
     * are purged.
     */
    class VariableStates {
        private final int id = counter.incrementAndGet();

        /**
         *  Maps variables to their known values, which might be a constant represented by a literal,
         *  or an expression represented by an instruction.
         */
        private final Map<LogicVariable, VariableValue> values;

        /**
         * Maps variables to a list of instructions defining its current value (potentially more than one
         * due to branching).
         */
        private final Map<LogicVariable, List<LogicInstruction>> definitions;

        /** Identifies instructions that do not have to be kept, because they set identical value. */
        private final Map<LogicVariable, LogicInstruction> useless;

        /** Maps variables to prior variables containing the same expression. */
        private final Map<LogicVariable, LogicVariable> equivalences;

        /** Set of initialized variables. */
        private final Set<LogicVariable> initialized;

        /** Set of variables stored on stack. */
        private final Set<LogicVariable> stored;

        public VariableStates() {
            values = new LinkedHashMap<>();
            definitions = new HashMap<>();
            equivalences = new HashMap<>();
            useless = new HashMap<>();
            initialized = new HashSet<>();
            stored = new HashSet<>();
        }

        private VariableStates(VariableStates other) {
            values = new LinkedHashMap<>(other.values);
            definitions = new HashMap<>(other.definitions);
            equivalences = new HashMap<>(other.equivalences);
            useless = new HashMap<>(other.useless);
            initialized = new HashSet<>(other.initialized);
            stored = new HashSet<>(other.stored);
        }

        public Map<LogicVariable, LogicInstruction> getUseless() {
            return useless;
        }

        public VariableStates copy() {
            return new VariableStates(this);
        }

        private void printInstruction(LogicInstruction instruction) {
            if (DataFlowOptimizer.DEBUG) {
                System.out.println("   " + optimizer.instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
            }
        }

        // Called when a variable value changes to purge all dependent expressions
        private void invalidateVariable(LogicVariable variable) {
            if (stored.contains(variable)) {
                debug("    Not invalidating variable " + variable.getFullName() + ", because it is stored on stack.");
            } else {
                if (DataFlowOptimizer.DEBUG) {
                    values.values().stream().filter(exp -> exp.dependsOn(variable))
                            .forEach(exp -> System.out.println("   Invalidating expression: " + exp.variable.getFullName() + ": " + exp.instruction));
                    equivalences.entrySet().stream().filter(e -> e.getValue().equals(variable))
                            .forEach(e -> System.out.println("   Invalidating equivalence: " + e.getKey().getFullName() + ": " + e.getValue().getFullName()));
                }
                values.values().removeIf(exp -> exp.dependsOn(variable));
                equivalences.keySet().removeIf(var -> var.equals(variable));
                equivalences.values().removeIf(var -> var.equals(variable));
            }
        }

        public void pushVariable(LogicVariable variable) {
            debug("Pushing variable " + variable);
            if (!stored.add(variable)) {
                throw new MindcodeInternalError("Push called twice on " + variable.getFullName());
            }
        }

        public void popVariable(LogicVariable variable) {
            debug("Popping variable " + variable);
            if (!stored.remove(variable)) {
                throw new MindcodeInternalError("Pop without push on " + variable.getFullName());
            }
        }

        public void valueSet(LogicVariable variable, LogicInstruction instruction, LogicValue value) {
            if (stored.contains(variable)) {
                debug("Not setting value of variable " + variable.getFullName() + ", because it is stored on stack.");
                return;
            }

            debug("Value set: " + variable);
            printInstruction(instruction);

            if (optimizer.canEliminate(variable)) {
                // Only store the variable's value if it can be eliminated
                // Otherwise the value itself could be used - we do not want this for global variables.
                if (value == null) {
                    values.remove(variable);
                } else if (values.get(variable) != null && value.equals(values.get(variable).constantValue)) {
                    AstSubcontextType type = instruction.getAstContext().subcontextType();
                    if (type == OUT_OF_LINE_CALL || type == RECURSIVE_CALL) {
                        // A function argument is being set to the same value it already has. Skip it.
                        useless.put(variable, instruction);
                    }
                } else {
                    values.put(variable, new VariableValue(variable, value));
                }
            } else {
                // Variable cannot be eliminated --> its instruction needs to be kept
                optimizer.keep.add(instruction);      // Ensure the instruction is kept
            }

            optimizer.defines.add(instruction);
            initialized.add(variable);
            definitions.put(variable, List.of(instruction));

            // Update expressions
            invalidateVariable(variable);

            // Handle expressions only if the value is not exactly known
            if (value == null) {
                // Find the oldest equivalent expression
                for (VariableValue expression : values.values()) {
                    if (expression.isExpression() && expression.isEqual(instruction)) {
                        if (DataFlowOptimizer.DEBUG) {
                            System.out.println("    Adding inferred equivalence " + variable.getFullName() + " == " + expression.variable.getFullName());
                        }
                        equivalences.put(variable, expression.variable);
                        break;
                    }
                }

                if (instruction instanceof SetInstruction set) {
                    if (set.getResult() == variable && set.getValue() instanceof LogicVariable variable2) {
                        debug("    Adding direct equivalence " + variable.getFullName() + " == " + variable2.getFullName());
                        equivalences.put(variable, variable2);
                    }
                } else if (instruction.getOutputs() == 1) {
                    VariableValue expression = new VariableValue(variable, instruction);
                    // Do not reuse expressions with self-modifying variables (they effectively invalidate themselves)
                    if (!expression.dependsOn(variable)) {
                        values.put(variable, expression);
                    }
                }
            }
        }

        public void markInitialized(LogicVariable variable) {
            initialized.add(variable);
        }

        public void valueReset(LogicVariable variable) {
            if (stored.contains(variable)) {
                debug("Variable " + variable + " not reset after function call, because it is stored on stack.");
            } else {
                debug("Value reset: " + variable);
                values.remove(variable);
                invalidateVariable(variable);
            }
        }

        public void updateAfterFunctionCall(String localPrefix) {
            optimizer.functionReads.get(localPrefix).forEach(variable -> valueRead(variable, false));
            optimizer.functionWrites.get(localPrefix).forEach(this::valueReset);
        }

        public VariableValue findVariableValue(LogicValue variable) {
            return variable instanceof LogicVariable v && !stored.contains(v) ? values.get(v) : null;
        }

        public LogicValue valueRead(LogicVariable variable) {
            return valueRead(variable, true);
        }

        public LogicValue valueRead(LogicVariable variable, boolean markUninitialized) {
            debug("Value read: " + variable);

            if (markUninitialized && !initialized.contains(variable)) {
                optimizer.uninitialized.add(variable);
            }

            if (definitions.containsKey(variable)) {
                if (DataFlowOptimizer.DEBUG) {
                    definitions.get(variable).forEach(this::printInstruction);
                }
                // Variable value was read, keep all instructions that define its value
                optimizer.keep.addAll(definitions.get(variable));
            }

            VariableValue variableValue = values.get(variable);
            return variableValue == null || stored.contains(variable) ? null : variableValue.constantValue;
        }

        public LogicVariable findEquivalent(LogicVariable value) {
            return stored.contains(value) ? null : equivalences.get(value);
        }

        /**
         * Merges two variable states. Each state was produced by a code path, and it isn't known which one was
         * executed.
         *
         * @param other states to merge to this one.
         */
        public VariableStates merge(VariableStates other, String reason) {
            print("*** Merge " + reason + ":\n  this: ");
            other.print("  other:");

            if (!stored.isEmpty() || !other.stored.isEmpty()) {
                throw new MindcodeInternalError("Trying to merge variable states with variables on stack.");
            }

            // Definitions are merged together
            for (LogicVariable variable : other.definitions.keySet()) {
                if (definitions.containsKey(variable)) {
                    List<LogicInstruction> current = definitions.get(variable);
                    List<LogicInstruction> theOther = other.definitions.get(variable);
                    if (!sameInstances(current, theOther)) {
                        // Merge the two lists
                        if (current.size() == 1 && theOther.size() == 1) {
                            definitions.put(variable, List.of(current.get(0), theOther.get(0)));
                        } else {
                            Set<LogicInstruction> union = Collections.newSetFromMap(new IdentityHashMap<>(current.size() + theOther.size()));
                            union.addAll(current);
                            union.addAll(theOther);
                            definitions.put(variable, List.copyOf(union));
                        }

                        invalidateVariable(variable);
                    }
                } else {
                    definitions.put(variable, other.definitions.get(variable));
                    invalidateVariable(variable);
                }
            }

            // Only keep values that are the same in both instances
            values.keySet().retainAll(other.values.keySet());
            for (LogicVariable variable : other.values.keySet()) {
                if (!Objects.equals(values.get(variable), other.values.get(variable))) {
                    values.remove(variable);
                }
            }

            // Only keep equivalences that are the same in both instances
            equivalences.keySet().retainAll(other.equivalences.keySet());
            for (LogicVariable variable : other.equivalences.keySet()) {
                if (!Objects.equals(equivalences.get(variable), other.equivalences.get(variable))) {
                    equivalences.remove(variable);
                }
            }

            // Variable is initialized only if it was initialized by both code paths
            initialized.retainAll(other.initialized);

            useless.keySet().retainAll(other.useless.keySet());
            for (LogicVariable variable : other.useless.keySet()) {
                if (values.get(variable) != other.values.get(variable)) {
                    values.remove(variable);
                }
            }

            print("  result:");

            return this;
        }

        void print(String title) {
            if (DataFlowOptimizer.DEBUG) {
                System.out.println(title);
                System.out.println("    VariableStates instance #" + id);
                values.values().forEach(v -> System.out.println("    " + v));
                definitions.forEach((k, v) -> {
                    System.out.println("    Definitions of " + k.getFullName());
                    v.forEach(ix -> System.out.println("      " + optimizer.instructionIndex(ix)
                            + ": " + LogicInstructionPrinter.toString(instructionProcessor, ix)));
                });
                equivalences.forEach((k, v) -> System.out.println("    Equivalence: " + k.getFullName() + " = " + v.getFullName()));
                System.out.println("    Initialized: " + initialized.stream().map(LogicVariable::getFullName).collect(Collectors.joining(", ")));
            }
        }

        private <E> boolean sameInstances(List<E> list1, List<E> list2) {
            if (list1.size() != list2.size()) {
                return false;
            }

            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i) != list2.get(i)) {
                    return false;
                }
            }

            return true;
        }

        class VariableValue {
            /** Variable containing the result of the expression. */
            private final LogicVariable variable;

            /** Constant value of the variable, if known */
            private final LogicValue constantValue;

            /** The instruction producing the expression. */
            private final LogicInstruction instruction;

            public VariableValue(LogicVariable variable, LogicValue constantValue) {
                if (!constantValue.isConstant()) {
                    throw new IllegalArgumentException("Non-constant value " + constantValue);
                }
                this.variable = Objects.requireNonNull(variable);
                this.constantValue = Objects.requireNonNull(constantValue);
                this.instruction = null;
            }

            public VariableValue(LogicVariable variable, LogicInstruction instruction) {
                this.variable = Objects.requireNonNull(variable);
                this.constantValue = null;
                this.instruction = Objects.requireNonNull(instruction);
            }

            public LogicInstruction getInstruction() {
                return instruction;
            }

            public boolean isExpression() {
                return constantValue == null;
            }

            /**
             * Determines whether the expression depends on the given variable.
             *
             * @param variable variable to inspect
             * @return true if th expression reads the value of given variable
             */
            public boolean dependsOn(LogicVariable variable) {
                return isExpression() && instruction.inputArgumentsStream().anyMatch(arg -> arg.equals(variable));
            }

            /**
             * Determines whether this expression is equivalent to the one produced by the given instruction.
             *
             * @param instruction instruction defining the second expression
             * @return true if the two expressions are equal
             */
            public boolean isEqual(LogicInstruction instruction) {
                if (!isExpression() || this.instruction.getOpcode() != instruction.getOpcode()) {
                    return false;
                }

                // Equivalence for SENSOR instruction is not supported. Sensed values are considered volatile.
                return switch (instruction) {
                    case OpInstruction op && op.getOperation().isDeterministic()
                            -> isOpEqual((OpInstruction) this.instruction, op);
                    case PackColorInstruction ix    -> isInstructionEqual(this.instruction, ix);
                    //case ReadInstruction ix         -> isInstructionEqual(this.instruction, ix);
                    default                         -> false;
                };
            }

            private boolean isVolatile(LogicInstruction instruction) {
                return isExpression() && instruction.inputArgumentsStream().anyMatch(LogicArgument::isVolatile);
            }

            @SuppressWarnings("SuspiciousMethodCalls")
            public LogicArgument remap(LogicArgument value) {
                return equivalences.containsKey(value) ? equivalences.get(value) : value;
            }

            private boolean isInstructionEqual(LogicInstruction first, LogicInstruction second) {
                List<LogicArgument> args1 = first.getArgs();
                List<LogicArgument> args2 = second.getArgs();

                if (isVolatile(first) || args1.size() != args2.size()) {
                    return false;
                }

                for (int i = 0; i < args1.size(); i++) {
                    if (!first.getParam(i).isOutput() && !Objects.equals(remap(args1.get(i)), remap(args2.get(i)))) {
                        return false;
                    }
                }

                return true;
            }

            /**
             * Determines whether two OP instructions produce the same expression. It is already known the first
             * instruction operation is deterministic. THe obvious case is when the two instructions are completely
             * identical. Additionally, commutative and inverse operations are handled.
             *
             * @param first first instruction to compare
             * @param second second instruction to compare
             * @return true if the two instructions produce the same expression
             */
            private boolean isOpEqual(OpInstruction first, OpInstruction second) {
                if (isVolatile(first)) {
                    return false;
                }

                LogicArgument x1 = remap(first.getX());
                LogicArgument x2 = remap(second.getX());
                LogicArgument y1 = first.hasSecondOperand() ? remap(first.getY()) : null;
                LogicArgument y2 = second.hasSecondOperand() ? remap(second.getY()) : null;

                if (first.getOperation() == second.getOperation() && Objects.equals(x1, x2) && Objects.equals(y1, y2)) {
                    return true;
                }

                // For commutative operations, swapped arguments are equal
                // For inverted operations, swapped arguments are also equal
                if (first.getOperation() == second.getOperation() && first.getOperation().isCommutative()
                        || first.getOperation().hasInverse() && first.getOperation().inverse() == second.getOperation()) {
                    return Objects.equals(x1, y2) && Objects.equals(y1, x2);
                }

                return false;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                VariableValue that = (VariableValue) o;
                return this.variable.equals(that.variable) && this.instruction == that.instruction
                        && Objects.equals(this.constantValue, that.constantValue);
            }

            @Override
            public int hashCode() {
                return Objects.hash(variable, constantValue, instruction);
            }

            @Override
            public String toString() {
                return "variable " + variable.getFullName() + ": " + (isExpression()
                        ? " expression " + LogicInstructionPrinter.toString(instructionProcessor, instruction)
                        : " constant " + constantValue);
            }
        }
    }

    private void debug(String text) {
        if (DataFlowOptimizer.DEBUG) {
            System.out.println(text);
        }
    }
}
