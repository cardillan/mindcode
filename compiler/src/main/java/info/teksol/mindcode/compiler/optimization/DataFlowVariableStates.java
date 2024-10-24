package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.generator.CallGraph;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.generator.AstSubcontextType.PARAMETERS;
import static info.teksol.mindcode.compiler.generator.AstSubcontextType.RECURSIVE_CALL;

class DataFlowVariableStates {

    private final DataFlowOptimizer optimizer;

    private final InstructionProcessor instructionProcessor;
    private final OptimizationContext optimizationContext;

    public DataFlowVariableStates(DataFlowOptimizer optimizer) {
        this.optimizer = optimizer;
        instructionProcessor = optimizer.instructionProcessor;
        optimizationContext = optimizer.optimizationContext;
    }

    VariableStates createVariableStates() {
        return new VariableStates();
    }

    /** Provides unique IDs to instances for easy identification during debug. */
    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Describes known states of variables. Instances are primarily created by analysing code blocks, and then
     * merged when two or more code branches merge. Merging operations may produce variables with multiple definitions.
     * When merging two instances prescribing conflicting values/expressions to the same variable,
     * the values/expressions are purged.
     */
    class VariableStates {
        private final int id;

        /**
         * Maps variables to their known values, which might be a constant represented by a literal,
         * or an expression represented by an instruction.
         */
        private final Map<LogicVariable, VariableValue> values;

        /**
         * Maps variables to a list of instructions defining its current value (potentially more than one
         * due to branching).
         */
        private final Map<LogicVariable, List<LogicInstruction>> definitions;

        /**
         * Identifies instructions that do not have to be kept, because they set value the variable already had.
         * Organized by variable for easier housekeeping.
         */
        private final Map<LogicVariable, LogicInstruction> useless;

        /** Maps variables to prior variables containing the same expression. */
        private final Map<LogicVariable, LogicVariable> equivalences;

        /** Set of initialized variables. */
        private final Set<LogicVariable> initialized;

        /**
         * Set of variables stored on stack. Storing a variable on stack preserves its value during subsequent
         * modification (i.e. when setting parameter value for the recursive call) - the exact same value will be
         * restored from stack later on.
         */
        private final Set<LogicVariable> stored;

        /**
         * Indicates the program flow of this instance is terminated: an unconditional jump outside local context
         * was encountered. A new copy was created to be merged at the jump target was created, and this copy must
         * be discarded in merges down the line.
         */
        private boolean dead;

        /**
         * Indicates the instance is reachable - contain at least one instruction that is not unreachable. Instances
         * which are not reachable are ignored on merges.
         */
        private boolean reachable = true;

        /** An isolated instance only tracks values of variables, cannot be used to determine definitions or reaches. */
        private boolean isolated;

        public VariableStates() {
            id = counter.incrementAndGet();
            values = new LinkedHashMap<>();
            definitions = new HashMap<>();
            equivalences = new HashMap<>();
            useless = new HashMap<>();
            initialized = new HashSet<>();
            stored = new HashSet<>();
        }

        private VariableStates(VariableStates other, int id, boolean isolated) {
            this.id = id;
            this.isolated = isolated;
            values = new LinkedHashMap<>(other.values);
            definitions = deepCopy(other.definitions);
            equivalences = new HashMap<>(other.equivalences);
            useless = new HashMap<>(other.useless);
            initialized = new HashSet<>(other.initialized);
            stored = new HashSet<>(other.stored);
            dead = other.dead;
            reachable = other.reachable;
        }

        private static Map<LogicVariable, List<LogicInstruction>> deepCopy(Map<LogicVariable, List<LogicInstruction>> original) {
            return original.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    e -> new ArrayList<>(e.getValue())));
        }

        /**
         * @return useless instructions, organized by variable they produce.
         */
        public Map<LogicVariable, LogicInstruction> getUseless() {
            return useless;
        }

        /**
         * Creates a copy of variable states, for evaluating parallel branches.
         *
         * @param reason debug message
         * @return a independent copy of this instance
         */
        public VariableStates copy(String reason) {
            VariableStates copy = new VariableStates(this, counter.incrementAndGet(), isolated);
            trace(() -> "*** " + reason + ": created VariableStates instance #" + copy.id + " as a copy of #" + id);
            return copy;
        }

        /**
         * Creates an isolated copy of this instance. An isolated copy only tracks values of variables, it doesn't
         * propagate definitions or reaches. It is used when processing the context is incomplete, e.g. for first
         * iteration of loops.
         *
         * @return an isolated copy of this instance
         */
        public VariableStates isolatedCopy() {
            return new VariableStates(this, id, true);
        }

        /**
         * An isolated instance only tracks values of variables, but cannot be used to determine definitions or reaches.
         *
         * @return true if the instance is isolated.
         */
        public boolean isIsolated() {
            return isolated;
        }

        /**
         * Marks this instance dead - current execution path was terminated (either by an unconditional jump,
         * or by the END instruction). The context will be ignored upon merging. If the path was terminated by a jump,
         * a copy of this context must have been created to be joined at the target label.
         *
         * @param markRead when set to true, all active definitions of user defined variables will be preserved.
         *                 To be used with the END instruction to make sure the assignments to user defined variables
         *                 won't be removed.
         * @return this instance marked as dead.
         */
        public VariableStates setDead(boolean markRead) {
            if (markRead && !isolated) {
                definitions.entrySet().stream()
                        .filter(e -> e.getKey().isMainVariable())
                        .forEachOrdered(e -> optimizer.orphans.computeIfAbsent(e.getKey(),
                                l -> new ArrayList<>()).addAll(e.getValue()));
            }
            dead = true;
            return this;
        }

        public void setReachable() {
            this.reachable = true;
        }

        public void setUnreachable() {
            this.reachable = false;
        }

        private void printInstruction(LogicInstruction instruction) {
            if (BaseOptimizer.TRACE) {
                System.out.println("   " + optimizer.instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction));
            }
        }

        /**
         * Purges all expressions and values depending on given variable. To be called after the variable value changes.
         *
         * @param variable variable to be purged
         */
        // Called when a variable value changes to purge all dependent expressions
        private void invalidateVariable(LogicVariable variable) {
            if (stored.contains(variable)) {
                trace(() -> "    Not invalidating variable " + variable.toMlog() + ", because it is stored on stack.");
            } else {
                Set<LogicVariable> invalids = new HashSet<>();
                invalids.add(variable);

                while (true) {
                    Set<LogicVariable> dependants = values.values().stream()
                            .filter(exp -> exp.dependsOn(invalids))
                            .map(VariableValue::getVariable)
                            .collect(Collectors.toSet());
                    if (!invalids.addAll(dependants)) break;
                }

                if (BaseOptimizer.TRACE) {
                    values.values().stream().filter(exp -> exp.dependsOn(invalids))
                            .forEach(exp -> System.out.println("   Invalidating expression: " + exp.variable.toMlog() + ": " + exp.instruction));
                    equivalences.entrySet().stream().filter(e -> invalids.contains(e.getKey()) || invalids.contains(e.getValue()))
                            .forEach(e -> System.out.println("   Invalidating equivalence: " + e.getKey().toMlog() + ": " + e.getValue().toMlog()));
                }

                values.values().removeIf(exp -> exp.dependsOn(invalids));
                equivalences.keySet().removeIf(invalids::contains);
                equivalences.values().removeIf(invalids::contains);
            }
        }

        /**
         * Called when a variable has been stored on stack.
         *
         * @param variable variable to be stored
         * @return this
         */
        public VariableStates pushVariable(LogicVariable variable) {
            trace(() -> "Pushing variable " + variable);
            if (!stored.add(variable)) {
                throw new MindcodeInternalError("Push called twice on " + variable.toMlog());
            }
            return this;
        }

        /**
         * Called when a variable has been restored from stack.
         *
         * @param variable variable to be restored
         * @return this
         */
        public VariableStates popVariable(LogicVariable variable) {
            trace(() -> "Popping variable " + variable);
            if (!stored.remove(variable)) {
                throw new MindcodeInternalError("Pop without push on " + variable.toMlog());
            }
            return this;
        }

        /**
         * Called to record a new value assigned to a variable.
         *
         * @param variable    variable being assigned a new value
         * @param instruction instruction performing the assignment
         * @param value       the value being assigned, null means the instruction assigns an unknown value (e.g. value
         *                    provided by sensor instruction, or random value)
         * @param reuseValue  indicates the value assigned to the variable can be reused by later optimizations
         *                    (values inferred on the first pass through a loop must not be reused)
         */
        public void valueSet(LogicVariable variable, LogicInstruction instruction, LogicValue value, boolean reuseValue) {
            if (stored.contains(variable)) {
                trace(() -> "Not setting value of variable " + variable.toMlog() + ", because it is stored on stack.");
                return;
            }

            trace(() -> "Value set: " + variable);
            printInstruction(instruction);

            if (optimizer.canEliminate(instruction, variable)) {
                // Storing the variable value means the instruction can be completely eliminated (constant propagation)
                if (value == null) {
                    values.remove(variable);
                } else if (values.get(variable) != null && value.equals(values.get(variable).constantValue)) {
                    AstSubcontextType type = instruction.getAstContext().subcontextType();
                    if (type == PARAMETERS || type == RECURSIVE_CALL) {
                        // A function argument is being set to the same value it already has. Skip it.
                        useless.put(variable, instruction);
                    }
                } else {
                    values.put(variable, new VariableValue(variable, value));
                }
            } else if (!isolated) {
                // Variable cannot be eliminated --> its instruction needs to be kept
                optimizer.keep.add(instruction);      // Ensure the instruction is kept
            }

            if (!isolated) {
                optimizer.defines.add(instruction);
            }
            initialized.add(variable);
            definitions.put(variable, List.of(instruction));

            // Purge expressions based on the previous value of this variable
            invalidateVariable(variable);

            // Handle expressions only if the value is not exactly known
            if (value == null) {
                // Find the oldest equivalent expression: an expression based on the same values.
                // Recognizes that in "c = a + b; d = a + b" c and d is the same.
                if (reuseValue) {
                    for (VariableValue expression : values.values()) {
                        if (expression.isExpression() && expression.isEqual(this, instruction)) {
                            if (BaseOptimizer.TRACE) {
                                System.out.println("    Adding inferred equivalence " + variable.toMlog() + " == " + expression.variable.toMlog());
                            }
                            equivalences.put(variable, expression.variable);
                            break;
                        }
                    }
                }

                if (instruction instanceof SetInstruction set) {
                    if (reuseValue && set.getResult() == variable && set.getValue() instanceof LogicVariable variable2
                            && !variable.isVolatile() && !variable2.isVolatile()) {
                        trace(() -> "    Adding direct equivalence " + variable.toMlog() + " == " + variable2.toMlog());
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

        /**
         * Marks the variable as initialized, typically when a value is assigned to it.
         *
         * @param variable variable that is being initialized
         */
        public void markInitialized(LogicVariable variable) {
            initialized.add(variable);
        }

        /**
         * Resets the variable after a call to a function that writes to the variable, eliminating all information
         * about possible values. Variables stored on stack during the call aren't reset, because they're protected
         * by being stored on the stack.
         *
         * @param variable variable to reset
         */
        public void valueReset(LogicVariable variable) {
            if (stored.contains(variable)) {
                trace(() -> "Variable " + variable + " not reset after function call, because it is stored on stack.");
            } else {
                trace(() -> "Value reset: " + variable);
                values.remove(variable);
                invalidateVariable(variable);
            }
        }

        /**
         * Updates states of variables when a function call occurs.
         *
         * @param function    function to process
         * @param instruction instruction that caused the call
         */
        public void updateAfterFunctionCall(CallGraph.LogicFunction function, LogicInstruction instruction) {
            optimizationContext.getFunctionReads(function).forEach(variable -> valueRead(variable, instruction, false));
            optimizationContext.getFunctionWrites(function).forEach(this::valueReset);
            initialized.add(LogicVariable.fnRetVal(function));
        }

        /**
         * Returns a VariableValue instance keeping the information about the variable's value, or null if nothing
         * is known.
         *
         * @param variable variable to test
         * @return known value of the variable
         */
        public VariableValue findVariableValue(LogicValue variable) {
            return variable instanceof LogicVariable v && !stored.contains(v) ? values.get(v) : null;
        }

        /**
         * Marks the variable as read, to protect instructions assigning a value to the variable. This is a generic
         * version that doesn't take a specific instruction performing the access to the variable.
         *
         * @param variable variable to be marked as read
         * @return constant value of the variable, or null if there isn't a known constant value of the variable
         */
        public LogicValue valueRead(LogicVariable variable) {
            return valueRead(variable, null, true);
        }

        /**
         * Marks a variable as read by given instruction. The instruction might not be given if the read status
         * doesn't come from a specific instruction. Returns the value of the variable, if it is known the variable
         * has a constant value. Reports uninitialized variables.
         *
         * @param variable    variable to be marked
         * @param instruction instruction that reads the variable, may be null
         * @return constant value of the variable, or null if there isn't a known constant value of the variable
         */
        public LogicValue valueRead(LogicVariable variable, LogicInstruction instruction) {
            return valueRead(variable, instruction, true);
        }

        /**
         * Marks a variable as read by given instruction. The instruction might not be given if the read status
         * doesn't come from a specific instruction. Returns the value of the variable, if it is known the variable
         * has a constant value.
         *
         * @param variable            variable to be marked
         * @param instruction         instruction that reads the variable, may be null
         * @param reportUninitialized true to report variables that might not be initialized at this read
         * @return constant value of the variable, or null if there isn't a known constant value of the variable
         */
        public LogicValue valueRead(LogicVariable variable, LogicInstruction instruction, boolean reportUninitialized) {
            trace(() -> "Value read: " + variable + " (instance #" + id + (dead ? " DEAD!" : "") + (reachable ? "" : " UNREACHABLE!") + ")");

            if (reportUninitialized && !initialized.contains(variable) && !isolated && variable.getType() != ArgumentType.BLOCK) {
                trace(() -> "*** Detected uninitialized read of " + variable.toMlog());
                print("Current context:");
                optimizer.addUninitialized(variable);
            }

            if (definitions.containsKey(variable)) {
                if (BaseOptimizer.TRACE) {
                    definitions.get(variable).forEach(this::printInstruction);
                }
                // Variable value was read, keep all instructions that define its value
                if (!isolated) {
                    optimizer.keep.addAll(definitions.get(variable));
                }
            }

            if (instruction != null && !isolated) {
                optimizer.references.computeIfAbsent(variable, v -> new ArrayList<>()).add(instruction);
            }

            VariableValue variableValue = values.get(variable);
            return variableValue == null || stored.contains(variable) ? null : variableValue.constantValue;
        }

        public void protectVariable(LogicVariable variable) {
            if (definitions.containsKey(variable)) {
                optimizer.keep.addAll(definitions.get(variable));
            }
        }

        /**
         * Returns a variable that is known to contain the same value as given variable, meaning that the given variable
         * can be safely replaced by the returned one. The concrete value is not necessarily known, only the fact
         * that they're the same.
         *
         * @param variable variable to inspect
         * @return a prior variable known to contain the same value
         */
        public LogicVariable findEquivalent(LogicVariable variable) {
            return stored.contains(variable) ? null : equivalences.get(variable);
        }

        /**
         * Merges two variable states. Each state was produced by a code path, and it isn't known which one was
         * executed.
         *
         * @param other                  states to merge to this one
         * @param propagateUninitialized propagate uninitialized variables from the other instance
         * @param reason                 reasons for the merge, for debug purposes
         */
        public VariableStates merge(VariableStates other, boolean propagateUninitialized, String reason) {
            print("*** Merge " + reason + ":\n  this: ");
            other.print("  other:");

            if (!stored.isEmpty() || !other.stored.isEmpty()) {
                throw new MindcodeInternalError("Trying to merge variable states having variables on stack.");
            }

            if (other.dead || !other.reachable) {
                print("  result:");
                return this;        // Merging two dead ends produces dead end again
            } else if (dead || !reachable) {
                other.print("  result:");
                return other;
            }

            merge(definitions, other.definitions);

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

            if (propagateUninitialized) {
                // Variable is initialized only if it was initialized by both code paths
                initialized.retainAll(other.initialized);
            }

            useless.keySet().retainAll(other.useless.keySet());
            for (LogicVariable variable : other.useless.keySet()) {
                if (values.get(variable) != other.values.get(variable)) {
                    values.remove(variable);
                }
            }

            print("  result:");

            return this;
        }

        /**
         * Merges variable definitions.
         *
         * @param map1 instance to merge into
         * @param map2 instance to be merged
         */
        private void merge(Map<LogicVariable, List<LogicInstruction>> map1, Map<LogicVariable, List<LogicInstruction>> map2) {
            for (LogicVariable variable : map2.keySet()) {
                if (map1.containsKey(variable)) {
                    List<LogicInstruction> current = map1.get(variable);
                    List<LogicInstruction> theOther = map2.get(variable);
                    if (!sameInstances(current, theOther)) {
                        // Merge the two lists
                        if (current.size() == 1 && theOther.size() == 1) {
                            ArrayList<LogicInstruction> union = new ArrayList<>();
                            union.add(current.get(0));
                            union.add(theOther.get(0));
                            map1.put(variable, union);
                        } else {
                            Set<LogicInstruction> union = createIdentitySet(current.size() + theOther.size());
                            union.addAll(current);
                            union.addAll(theOther);
                            map1.put(variable, new ArrayList<>(union));
                        }
                        invalidateVariable(variable);
                    }
                } else {
                    map1.put(variable, map2.get(variable));
                    invalidateVariable(variable);
                }
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

        void print(String title) {
            if (BaseOptimizer.TRACE) {
                System.out.println(title);
                System.out.println("    VariableStates instance #" + id + (dead ? " DEAD!" : "") + (reachable ? "" : " UNREACHABLE!"));
                values.values().forEach(v -> System.out.println("    " + v));
                definitions.forEach((k, v) -> {
                    System.out.println("    Definitions of " + k.toMlog());
                    v.forEach(ix -> System.out.println("      " + optimizer.instructionIndex(ix)
                            + ": " + LogicInstructionPrinter.toString(instructionProcessor, ix)));
                });
                equivalences.forEach((k, v) -> System.out.println("    Equivalence: " + k.toMlog() + " = " + v.toMlog()));
                System.out.println("    Initialized: " + initialized.stream().map(LogicVariable::toMlog).collect(Collectors.joining(", ")));
            }
        }
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

        public LogicValue getConstantValue() {
            return constantValue;
        }

        public LogicVariable getVariable() {
            return variable;
        }

        public boolean isExpression() {
            return constantValue == null;
        }

        /**
         * Determines whether the expression depends on the given variable.
         *
         * @param variable variable to inspect
         * @return true if the expression reads the value of given variable
         */
        public boolean dependsOn(LogicVariable variable) {
            return isExpression() && instruction.inputArgumentsStream().anyMatch(variable::equals);
        }

        /**
         * Determines whether the expression depends on any of the given variables.
         *
         * @param variables variables to inspect
         * @return true if the expression reads the value of some of the variables
         */
        public boolean dependsOn(Set<LogicVariable> variables) {
            return isExpression() && instruction.inputArgumentsStream()
                    .anyMatch(arg -> arg instanceof LogicVariable v && variables.contains(v));
        }

        /**
         * Determines whether this expression is equivalent to the one produced by the given instruction.
         *
         * @param instruction instruction defining the second expression
         * @return true if the two expressions are equal
         */
        public boolean isEqual(VariableStates variableStates, LogicInstruction instruction) {
            if (!isExpression() || this.instruction.getOpcode() != instruction.getOpcode()) {
                return false;
            }

            if (instruction instanceof OpInstruction op && op.getOperation().isDeterministic()) {
                return isOpEqual(variableStates, (OpInstruction) this.instruction, op);
            } else if (instruction instanceof PackColorInstruction ix) {
                return isInstructionEqual(variableStates, this.instruction, ix);
            } else if (instruction instanceof ReadInstruction ix) {
                // TODO read instructions will depend on memory model
                // return isInstructionEqual(variableStates, this.instruction, ix);
                return false;
            } else if (instruction instanceof SensorInstruction ix) {
                return instructionProcessor.isDeterministic(ix)
                        && isInstructionEqual(variableStates, this.instruction, ix);
            }

            return false;
        }

        private boolean isVolatile(LogicInstruction instruction) {
            return isExpression() && instruction.inputArgumentsStream().anyMatch(LogicArgument::isVolatile);
        }

        /**
         * If the passed in value is a variable and there exists a preexisting variable holding the same value,
         * returns the preexisting variable.
         *
         * @param value value to remap
         * @return an equivalent variable if it exists, otherwise the original value
         */
        public LogicArgument remap(VariableStates variableStates, LogicArgument value) {
            return value instanceof LogicVariable var && variableStates.equivalences.containsKey(var)
                    ? variableStates.equivalences.get(var) : value;
        }

        /**
         * Determines whether the two instructions are equal. Both instructions need to have the same opcode
         * (the opcode is not checked).
         *
         * @param first  instruction to compare
         * @param second instruction to compare
         * @return true if the two instructions are equal
         */
        private boolean isInstructionEqual(VariableStates variableStates, LogicInstruction first, LogicInstruction second) {
            List<LogicArgument> args1 = first.getArgs();
            List<LogicArgument> args2 = second.getArgs();

            if (isVolatile(first) || args1.size() != args2.size()) {
                return false;
            }

            for (int i = 0; i < args1.size(); i++) {
                if (!first.getArgumentType(i).isOutput() && !Objects.equals(
                        remap(variableStates, args1.get(i)),
                        remap(variableStates, args2.get(i))
                )) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Determines whether two OP instructions produce the same expression. It is already known at least one
         * of the instructions has a deterministic operation. The obvious case is when the two instructions have
         * equivalent operations and inputs. Additionally, commutative and inverse operations are handled.
         *
         * @param first  first instruction to compare
         * @param second second instruction to compare
         * @return true if the two instructions produce the same expression
         */
        private boolean isOpEqual(VariableStates variableStates, OpInstruction first, OpInstruction second) {
            if (isVolatile(first)) {
                return false;
            }

            LogicArgument x1 = remap(variableStates, first.getX());
            LogicArgument x2 = remap(variableStates, second.getX());
            LogicArgument y1 = first.hasSecondOperand() ? remap(variableStates, first.getY()) : null;
            LogicArgument y2 = second.hasSecondOperand() ? remap(variableStates, second.getY()) : null;

            if (first.getOperation() == second.getOperation() && Objects.equals(x1, x2) && Objects.equals(y1, y2)) {
                return true;
            }

            // For commutative operations, swapped arguments are equal
            // For inequality operators, swapped operation and arguments are also equal
            if (first.getOperation() == second.getOperation() && first.getOperation().isCommutative()
                    || first.getOperation().swapped() == second.getOperation()) {
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
            return "variable " + variable.toMlog() + ": " + (isExpression()
                    ? " expression " + LogicInstructionPrinter.toString(instructionProcessor, instruction)
                    : " constant " + constantValue);
        }
    }

    private void trace(Supplier<String> text) {
        if (BaseOptimizer.TRACE) {
            System.out.println(text.get());
        }
    }

    private static <T> Set<T> createIdentitySet(int size) {
        return Collections.newSetFromMap(new IdentityHashMap<>(size));
    }
}
