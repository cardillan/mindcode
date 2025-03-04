package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionParameter;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator.TRACE;

@NullMarked
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

    /// Provides unique IDs to instances for easy identification during debug.
    private final AtomicInteger counter = new AtomicInteger();

    /// Describes known states of variables. Instances are primarily created by analysing code blocks, and then
    /// merged when two or more code branches merge. Merging operations may produce variables with multiple definitions.
    /// When merging two instances prescribing conflicting values/expressions to the same variable,
    /// the values/expressions are purged.
    class VariableStates {
        private final int id;

        /// Modification counter for change detection
        private int modifications = 0;

        /// Maps variables to their known values, which might be a constant represented by a literal,
        /// or an expression represented by an instruction.
        private final Map<LogicVariable, VariableValue> values;

        /// Maps variables to a list of instructions defining its current value (potentially more than one
        /// due to branching).
        private final Map<LogicVariable, Definition> definitions;

        /// Maps variables to their last encountered read instruction position.
        private final Map<LogicVariable, Integer> reads;

        /// Identifies instructions that do not have to be kept, because they set value the variable already had.
        /// Organized by variable for easier housekeeping.
        private final Map<LogicVariable, LogicInstruction> useless;

        /// Maps variables to prior variables containing the same expression.
        private final Map<LogicVariable, LogicVariable> equivalences;

        /// Set of initialized variables.
        private final Set<LogicVariable> initialized;

        /// Set of variables stored on stack. Storing a variable on stack preserves its value during subsequent
        /// modification (i.e. when setting parameter value for the recursive call) - the exact same value will be
        /// restored from stack later on.
        private final Set<LogicVariable> stored;

        /// Indicates the program flow of this instance is terminated: an unconditional jump outside local context
        /// was encountered. A new copy was created to be merged at the jump target was created, and this copy must
        /// be discarded in merges down the line.
        private boolean dead;

        /// Indicates the instance is reachable. Unreachable instances represent contexts that were unreachable right
        /// from the start (e.g. inactive branches of if statements). Unreachable instances are discarded in merges.
        ///
        /// TODO: Unreachable variable states are probably the same as dead ones. Investigate possible merging.
        private final boolean reachable;

        /// An isolated instance only tracks values of variables, cannot be used to determine definitions or reaches.
        private boolean isolated;

        public String getId() {
            return "vs#" + id + (dead ? " DEAD!" : "") + (reachable ? "" : " UNREACHABLE!");
        }

        public long digest() {
            return ((long) id) << 32 | (long) modifications;
        }

        public VariableStates() {
            id = counter.incrementAndGet();
            values = new LinkedHashMap<>();
            definitions = new HashMap<>();
            equivalences = new HashMap<>();
            reads = new HashMap<>();
            useless = new HashMap<>();
            initialized = new HashSet<>();
            stored = new HashSet<>();
            reachable = true;
        }

        private VariableStates(VariableStates other, int id, boolean isolated, boolean reachable) {
            this.id = id;
            this.isolated = isolated;
            values = new LinkedHashMap<>(other.values);
            definitions = new HashMap<>(other.definitions);        // Definitions are shared
            equivalences = new HashMap<>(other.equivalences);
            reads = new HashMap<>(other.reads);
            useless = new HashMap<>(other.useless);
            initialized = new HashSet<>(other.initialized);
            stored = new HashSet<>(other.stored);
            dead = other.dead;
            this.reachable = reachable;
        }

        private VariableStates(VariableStates other, int id, boolean isolated) {
            this(other, id, isolated, other.reachable);
        }

        /// @return useless instructions, organized by variable they produce.
        public Map<LogicVariable, LogicInstruction> getUseless() {
            return useless;
        }

        /// Creates a copy of variable states, for evaluating parallel branches.
        ///
        /// @param reason debug message
        /// @return an independent copy of this instance
        public VariableStates copy(String reason) {
            return copy(reason, reachable);
        }

        /// Creates a copy of variable states, for evaluating parallel branches.
        ///
        /// @param reason debug message
        /// @return an independent copy of this instance
        public VariableStates copy(String reason, boolean reachable) {
            VariableStates copy = new VariableStates(this, counter.incrementAndGet(), isolated, reachable && this.reachable);
            trace(() -> "*** " + reason + ": created VariableStates instance " + copy.getId() + " as a copy of " + getId());
            return copy;
        }

        /// Creates an isolated copy of this instance. An isolated copy only tracks values of variables, it doesn't
        /// propagate definitions or reaches. It is used when processing the context is incomplete, e.g. for first
        /// iteration of loops.
        ///
        /// @return an isolated copy of this instance
        public VariableStates isolatedCopy() {
            return new VariableStates(this, id, true);
        }

        /// An isolated instance only tracks values of variables, but cannot be used to determine definitions or reaches.
        ///
        /// @return true if the instance is isolated.
        public boolean isIsolated() {
            return isolated;
        }

        /// Marks this instance dead - current execution path was terminated (either by an unconditional jump,
        /// or by the END instruction). The context will be ignored upon merging. If the path was terminated by a jump,
        /// a copy of this context must have been created to be joined at the target label.
        ///
        /// @param markRead when set to true, all active definitions of user defined variables will be preserved.
        ///                 To be used with the END instruction to make sure the assignments to user defined variables
        ///                 won't be removed.
        /// @return this instance marked as dead.
        public VariableStates setDead(boolean markRead) {
            if (markRead && !isolated) {
                definitions.entrySet().stream()
                        .filter(e -> e.getKey().isMainVariable() || e.getKey().isGlobalVariable())
                        .forEachOrdered(e -> optimizer.orphans.computeIfAbsent(e.getKey(),
                                l -> new ArrayList<>()).addAll(e.getValue().instructions));
            }
            dead = true;
            modifications++;
            return this;
        }

        public boolean isReachable() {
            return reachable;
        }

        private String printInstruction(LogicInstruction instruction) {
            return "    " + optimizer.instructionIndex(instruction) + ": " + LogicInstructionPrinter.toString(instructionProcessor, instruction);
        }

        /// Purges all expressions and values depending on given variable. To be called after the variable value changes.
        ///
        /// @param variable variable to be purged
        // Called when a variable value changes to purge all dependent expressions
        private void invalidateVariable(LogicVariable variable) {
            modifications++;
            Set<LogicVariable> invalids = new HashSet<>();
            invalids.add(variable);

            while (true) {
                Set<LogicVariable> dependants = values.values().stream()
                        .filter(exp -> exp.dependsOn(invalids))
                        .map(VariableValue::getVariable)
                        .collect(Collectors.toSet());
                if (!invalids.addAll(dependants)) break;
            }

            if (TRACE) {
                trace(values.values().stream().filter(exp -> exp.dependsOn(invalids))
                        .map(exp -> "   Invalidating expression: " + exp.variable.toMlog() + ": " + exp.getInstructionMlog()));
                trace(equivalences.entrySet().stream().filter(e -> invalids.contains(e.getKey()) || invalids.contains(e.getValue()))
                        .map(e -> "   Invalidating equivalence: " + e.getKey().toMlog() + ": " + e.getValue().toMlog()));
            }

            values.values().removeIf(exp -> exp.dependsOn(invalids));
            equivalences.keySet().removeIf(invalids::contains);
            equivalences.values().removeIf(invalids::contains);
        }

        /// Called when a variable has been stored on stack.
        ///
        /// @param variable variable to be stored
        /// @return this
        public VariableStates pushVariable(LogicVariable variable) {
            modifications++;
            trace(() -> "Pushing variable " + variable.toMlog());
            if (!stored.add(variable)) {
                throw new MindcodeInternalError("Push called twice on " + variable.toMlog());
            }
            return this;
        }

        /// Called when a variable has been restored from stack.
        ///
        /// @param variable variable to be restored
        /// @return this
        public VariableStates popVariable(LogicVariable variable) {
            modifications++;
            trace(() -> "Popping variable " + variable.toMlog());
            if (!stored.remove(variable)) {
                throw new MindcodeInternalError("Pop without push on " + variable.toMlog());
            }
            return this;
        }

        /// Called to record a new value assigned to a variable.
        ///
        /// @param variable    variable being assigned a new value
        /// @param instruction instruction performing the assignment
        /// @param value       the value being assigned, null means the instruction assigns an unknown value (e.g. value
        ///                    provided by sensor instruction, or random value)
        /// @param reuseValue  indicates the value assigned to the variable can be reused by later optimizations
        ///                    (values inferred on the first pass through a loop must not be reused)
        public void valueSet(LogicVariable variable, LogicInstruction instruction, @Nullable LogicValue value, boolean reuseValue) {
            modifications++;
            if (stored.contains(variable)) {
                invalidateVariable(variable);
                trace(() -> "Not setting value of variable " + variable.toMlog() + ", because it is stored on stack.");
                return;
            }

            trace(() -> "Value set: " + variable.toMlog());
            trace(() -> printInstruction(instruction));

            if (optimizer.canEliminate(instruction, variable)) {
                // Storing the variable value means the instruction can be completely eliminated (constant propagation)
                if (value == null || !value.isImmutable()) {
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
            } else if (!isolated && reachable && !dead) {
                // Variable cannot be eliminated --> its instruction needs to be kept
                trace(() -> "--> Keeping instruction: " + instruction.toMlog() + " (value set)");
                optimizer.keep.add(instruction);      // Ensure the instruction is kept
            }

            if (!isolated) {
                optimizer.defines.add(instruction);
            }
            initialized.add(variable);

            Definition definition = new Definition(variable, optimizer.getCounter(), instruction);
            optimizer.definitions.add(definition);
            definitions.put(variable, definition);
            reads.remove(variable);

            // Purge expressions based on the previous value of this variable
            invalidateVariable(variable);

            // Handle expressions only if the value is not exactly known
            if (value == null) {
                // Find the oldest equivalent expression: an expression based on the same values.
                // Recognizes that in "c = a + b; d = a + b" c and d is the same.
                if (reuseValue) {
                    for (VariableValue expression : values.values()) {
                        if (expression.isExpression() && expression.isEqual(this, instruction)) {
                            trace(() -> "    Adding inferred equivalence " + variable.toMlog() + " == " + expression.variable.toMlog());
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

        /// Marks the variable as initialized, typically when a value is assigned to it.
        ///
        /// @param variable variable that is being initialized
        public void markInitialized(LogicVariable variable) {
            modifications++;
            initialized.add(variable);
        }

        /// Resets the variable after a call to a function that writes to the variable, eliminating all information
        /// about possible values. Variables stored on stack during the call aren't reset, because they're protected
        /// by being stored on the stack.
        ///
        /// @param variable variable to reset
        public void valueReset(LogicVariable variable) {
            modifications++;
            if (stored.contains(variable)) {
                trace(() -> "Variable " + variable.toMlog() + " not reset after function call, because it is stored on stack.");
            } else {
                trace(() -> "Value reset: " + variable.toMlog());
                values.remove(variable);
                reads.remove(variable);
                invalidateVariable(variable);
                definitions.computeIfAbsent(variable, v -> new Definition(variable, optimizer.getCounter())).inexact = true;
            }
        }

        /// Updates states of variables when a function call occurs.
        ///
        /// @param function    function to process
        /// @param instruction instruction that caused the call
        public void updateAfterFunctionCall(MindcodeFunction function, @Nullable LogicInstruction instruction) {
            modifications++;
            optimizationContext.getFunctionReads(function).forEach(variable -> valueRead(variable, instruction, false, true));
            optimizationContext.getFunctionWrites(function).forEach(this::valueReset);
            function.getParameters().stream().filter(FunctionParameter::isOutput).map(LogicVariable.class::cast).forEach(initialized::add);
            initialized.add(LogicVariable.fnRetVal(function));
        }

        /// Returns a VariableValue instance keeping the information about the variable's value, or null if nothing
        /// is known.
        ///
        /// @param variable variable to test (null is acceptable)
        /// @return known value of the variable
        public @Nullable VariableValue findVariableValue(@Nullable LogicValue variable) {
            return variable instanceof LogicVariable v && !stored.contains(v) ? values.get(v) : null;
        }

        /// Marks the variable as read, to protect instructions assigning a value to the variable. This is a generic
        /// version that doesn't take a specific instruction performing the access to the variable.
        ///
        /// @param variable variable to be marked as read
        /// @return constant value of the variable, or null if there isn't a known constant value of the variable
        public @Nullable LogicValue valueRead(LogicVariable variable) {
            return valueRead(variable, null, true, true);
        }

        /// Marks a variable as read by given instruction. The instruction might not be given if the read status
        /// doesn't come from a specific instruction. Returns the value of the variable, if it is known the variable
        /// has a constant value. Reports uninitialized variables.
        ///
        /// @param variable    variable to be marked
        /// @param instruction instruction that reads the variable, may be null
        /// @return constant value of the variable, or null if there isn't a known constant value of the variable
        public @Nullable LogicValue valueRead(LogicVariable variable, LogicInstruction instruction, boolean reachable) {
            return valueRead(variable, instruction, true, reachable);
        }

        /// Marks a variable as read by given instruction. The instruction might not be given if the read status
        /// doesn't come from a specific instruction. Returns the value of the variable, if it is known the variable
        /// has a constant value.
        ///
        /// @param variable            variable to be marked
        /// @param instruction         instruction that reads the variable, may be null
        /// @param reportUninitialized true to report variables that might not be initialized at this read
        /// @param ixReachable         indicates whether the instruction is reachable
        /// @return constant value of the variable, or null if there isn't a known constant value of the variable
        public @Nullable LogicValue valueRead(LogicVariable variable, @Nullable LogicInstruction instruction, boolean reportUninitialized,
                boolean ixReachable) {
            modifications++;
            trace(() -> "Value read: " + variable.toMlog() + " (instance " + getId() + ")" + (ixReachable ? "" : " instruction unreachable)"));

            reads.put(variable, optimizer.getCounter());

            // Do not report uninitialized reads in unreachable and dead states
            boolean report = reportUninitialized && !isolated && !dead && reachable && ixReachable;
            if (report && !initialized.contains(variable) && variable.getType() != ArgumentType.BLOCK) {
                print("!!! Detected uninitialized read of " + variable.toMlog());
                optimizer.addUninitialized(variable);
            }

            // TODO Use uninitialized definitions to track uninitialized variables
            Definition definition = definitions.get(variable);

            if (definition != null) {
                List<LogicInstruction> definingInstructions = definition.instructions;
                if (TRACE) {
                    trace("Definitions of " + variable.toMlog() + " (*" + definition.counter + "): ");
                    trace(definingInstructions.stream().map(this::printInstruction));
                }
                // Variable value was read, keep all instructions that define its value
                if (!isolated && ixReachable) {
                    if (TRACE) {
                        trace(definingInstructions.stream().map(ix -> "--> Keeping instruction: " + ix.toMlog() + " (variable read)"));
                    }
                    optimizer.keep.addAll(definingInstructions);
                }

                // When a variable is read, we're storing information about that read with all defining instructions
                // of that variable. When it is discovered that there's precisely one set instruction reading that variable,
                // the target of the set instruction can be injected into the definition and the set instruction can be
                // removed.
                //
                // However, this is no possible if the target of the set instruction has been read between the defining
                // instruction and current instruction, since this would change the value of the target variable in that
                // read.
                //
                // When the value is read by an instruction other than set, the information is recorded too, because
                // it means the substitution cannot be made.
                //
                // Process:
                // - all defining instructions are inspected
                // - a valid reference is added when these conditions are met:
                //   - the referring instruction is a set instruction reading current variable
                //   - definition of the variable is known (it is, we're here)
                //   - definition of the variable is just one instruction (we're being safe)
                //   - the result of the set wasn't read after this variable was defined
                //   - the set is not part of for-each iterator setup
                // - in the other case, an invalid reference is added
                // Note: presence of a null reference prevents the optimization
                int defineIndex = definition.counter;
                LogicInstruction reference;
                if (instruction instanceof SetInstruction set && set.getValue() == variable
                        && !definition.inexact && !set.getAstContext().matches(ITR_LEADING, ITR_TRAILING)) {
                    Integer readIndex = reads.get(set.getResult());
                    reference = readIndex == null || readIndex < defineIndex ? instruction : null;
                } else {
                    reference = null;
                }

                definition.references.add(reference);
                trace("Registering usage of " + variable.toMlog());
            }

            if (instruction != null && !isolated) {
                optimizer.references.computeIfAbsent(variable, v -> new ArrayList<>()).add(instruction);
            }

            VariableValue variableValue = values.get(variable);
            return variableValue == null || stored.contains(variable) ? null : variableValue.constantValue;
        }

        public void protectVariable(LogicVariable variable) {
            if (definitions.containsKey(variable)) {
                if (TRACE) {
                    trace(definitions.get(variable).instructions.stream().map(ix -> "--> Keeping instruction: " + ix.toMlog() + " (variable protected)"));
                }
                optimizer.keep.addAll(definitions.get(variable).instructions);
            }
        }

        /// Returns a variable that is known to contain the same value as given variable, meaning that the given variable
        /// can be safely replaced by the returned one. The concrete value is not necessarily known, only the fact
        /// that they're the same.
        ///
        /// @param variable variable to inspect
        /// @return a prior variable known to contain the same value
        public @Nullable LogicVariable findEquivalent(LogicVariable variable) {
            return stored.contains(variable) ? null : equivalences.get(variable);
        }

        /// Merges two variable states. Each state was produced by a code path, and it isn't known which one was
        /// executed.
        ///
        /// @param other                  states to merge to this one
        /// @param propagateUninitialized propagate uninitialized variables from the other instance
        /// @param reason                 reasons for the merge, for debug purposes
        public VariableStates merge(VariableStates other, boolean propagateUninitialized, String reason) {
            trace("*** Merge " + reason);
            optimizationContext.indentInc();
            print("This");
            other.print("Other");

            if (!stored.isEmpty() || !other.stored.isEmpty()) {
                throw new MindcodeInternalError("Trying to merge variable states having variables on stack.");
            }

            if (other.dead || !other.reachable) {
                print("Result");
                optimizationContext.indentDec();
                return this;        // Merging two dead ends produces dead end again
            } else if (dead || !reachable) {
                other.print("Result");
                optimizationContext.indentDec();
                return other;
            }

            modifications++;

            merge(definitions, other.definitions);

            for (LogicVariable variable : other.reads.keySet()) {
                if (!reads.containsKey(variable) || reads.get(variable) < other.reads.get(variable)) {
                    reads.put(variable, other.reads.get(variable));
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

            print("Result");
            optimizationContext.indentDec();

            return this;
        }

        /// Merges variable definitions.
        ///
        /// @param map1 instance to merge into
        /// @param map2 instance to be merged
        private void merge(Map<LogicVariable, Definition> map1, Map<LogicVariable, Definition> map2) {
            for (LogicVariable variable : map2.keySet()) {
                if (map1.containsKey(variable)) {
                    Definition current = map1.get(variable);
                    Definition other = map2.get(variable);
                    if (current == other) continue;

                    Definition merged = current.merge(other);
                    optimizer.definitions.remove(current);
                    optimizer.definitions.remove(other);
                    optimizer.definitions.add(merged);
                    map1.put(variable, merged);
                    invalidateVariable(variable);
                } else {
                    Definition other = map2.get(variable);
                    other.inexact = true;
                    map1.put(variable, other);
                    invalidateVariable(variable);
                }
            }

            for (Definition definition : map1.values()) {
                if (!map2.containsKey(definition.variable)) {
                    definition.inexact = true;
                    invalidateVariable(definition.variable);
                }
            }
        }

        void print(String title) {
            if (TRACE) {
                trace(title + ": VariableStates instance " + getId());
                values.values().forEach(v -> trace("    " + v));
                definitions.forEach((k, v) -> {
                    trace("    Definitions of " + k.toMlog() + " (*" + v.counter + "):");
                    v.instructions.forEach(ix -> trace("        " + optimizer.instructionIndex(ix)
                            + ": " + LogicInstructionPrinter.toString(instructionProcessor, ix)));
                });
                equivalences.forEach((k, v) -> trace("    Equivalence: " + k.toMlog() + " = " + v.toMlog()));
                if (initialized.isEmpty()) {
                    trace("    Initialized: <none>");
                } else {
                    trace("    Initialized: " + initialized.stream().map(LogicVariable::toMlog).collect(Collectors.joining(", ")));
                }
                if (!reads.isEmpty()) {
                    trace("    Reads:");
                    reads.forEach((k, v) -> trace("        " + k.toMlog() + " (*" + v + ")"));
                }
            }
        }
    }

    class VariableValue {
        /// Variable containing the result of the expression.
        private final LogicVariable variable;

        /// Constant value of the variable, if known
        private final @Nullable LogicValue constantValue;

        /// The instruction producing the expression.
        private final @Nullable LogicInstruction instruction;

        public VariableValue(LogicVariable variable, LogicValue constantValue) {
            this.variable = Objects.requireNonNull(variable);
            this.constantValue = Objects.requireNonNull(constantValue);
            this.instruction = null;

            if (!constantValue.isImmutable()) {
                throw new IllegalArgumentException("Non-constant value " + constantValue);
            }
        }

        public VariableValue(LogicVariable variable, LogicInstruction instruction) {
            this.variable = Objects.requireNonNull(variable);
            this.constantValue = null;
            this.instruction = Objects.requireNonNull(instruction);
        }

        public @Nullable LogicInstruction getInstruction() {
            return instruction;
        }

        public String getInstructionMlog() {
            return instruction == null ? "<no instruction>" : instruction.toMlog();
        }

        public @Nullable LogicValue getConstantValue() {
            return constantValue;
        }

        public LogicVariable getVariable() {
            return variable;
        }

        public boolean isExpression() {
            return constantValue == null;
        }

        /// Determines whether the expression depends on the given variable.
        ///
        /// @param variable variable to inspect
        /// @return true if the expression reads the value of given variable
        public boolean dependsOn(LogicVariable variable) {
            return instruction != null && instruction.usesAsInput(variable);
        }

        /// Determines whether the expression depends on any of the given variables.
        ///
        /// @param variables variables to inspect
        /// @return true if the expression reads the value of some of the variables
        public boolean dependsOn(Set<LogicVariable> variables) {
            return instruction != null && instruction.inputArgumentsStream()
                    .anyMatch(arg -> arg instanceof LogicVariable v && variables.contains(v));
        }

        /// Determines whether this expression is equivalent to the one produced by the given instruction.
        ///
        /// @param instruction instruction defining the second expression
        /// @return true if the two expressions are equal
        public boolean isEqual(VariableStates variableStates, LogicInstruction instruction) {
            if (this.instruction == null || this.instruction.getOpcode() != instruction.getOpcode()) {
                return false;
            }

            return switch (instruction) {
                case OpInstruction op when op.getOperation().isDeterministic() ->
                        isOpEqual(variableStates, (OpInstruction) this.instruction, op);

                case PackColorInstruction ix ->
                        isInstructionEqual(variableStates, this.instruction, ix);

                case ReadInstruction ix ->
                    // TODO read instructions will depend on memory model
                    // isInstructionEqual(variableStates, this.instruction, ix);
                        false;

                case SensorInstruction ix when instructionProcessor.isDeterministic(ix) ->
                         isInstructionEqual(variableStates, this.instruction, ix);

                default -> false;
            };
        }

        private boolean isVolatile(LogicInstruction instruction) {
            return isExpression() && instruction.inputArgumentsStream().anyMatch(LogicArgument::isVolatile);
        }

        /// If the passed in value is a variable and there exists a preexisting variable holding the same value,
        /// returns the preexisting variable.
        ///
        /// @param value value to remap
        /// @return an equivalent variable if it exists, otherwise the original value
        public LogicArgument remap(VariableStates variableStates, LogicArgument value) {
            return value instanceof LogicVariable var && variableStates.equivalences.containsKey(var)
                    ? variableStates.equivalences.get(var) : value;
        }

        /// Determines whether the two instructions are equal. Both instructions need to have the same opcode
        /// (the opcode is not checked).
        ///
        /// @param first  instruction to compare
        /// @param second instruction to compare
        /// @return true if the two instructions are equal
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

        /// Determines whether two OP instructions produce the same expression. It is already known at least one
        /// of the instructions has a deterministic operation. The obvious case is when the two instructions have
        /// equivalent operations and inputs. Additionally, commutative and inverse operations are handled.
        ///
        /// @param first  first instruction to compare
        /// @param second second instruction to compare
        /// @return true if the two instructions produce the same expression
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
            return "variable " + variable.toMlog() + ": " + (
                    instruction != null
                            ? " expression " + LogicInstructionPrinter.toString(instructionProcessor, instruction)
                            : " constant " + constantValue
            );
        }
    }

    /// A definition of a variable. A definition is created when a value is assigned to a variable.
    /// When a variable is updated inexactly (e.g. by a function call), the definition is marked as such.
    /// When code paths split, the definition is inherited by both code paths - reads in both code paths
    /// update the same definition.
    /// When code paths merge, the definitions merge too - this is the only way for a definition to
    /// obtain multiple defining instructions. If a variable isn't defined on all code paths, it will become inexact.
    /// TODO: distinguish uninitialized and inexact.
    static class Definition {
        final LogicVariable variable;

        /// The instruction counter of this variable's definition
        final int counter;

        /// The value of this variable was changed inexactly (i.e. by a function call).
        boolean inexact;

        /// List of defining instructions
        final List<LogicInstruction> instructions;

        /// List of instructions depending on this version of the variable
        final Set<@Nullable LogicInstruction> references = createIdentitySet(5);

        public Definition(LogicVariable variable, int counter) {
            this(variable, counter, true, List.of());
        }

        public Definition(LogicVariable variable, int counter, LogicInstruction instruction) {
            this(variable, counter, false, List.of(instruction));
        }

        public Definition(LogicVariable variable, int counter, boolean inexact, List<LogicInstruction> instructions) {
            this.variable = variable;
            this.counter = counter;
            this.inexact = inexact;
            this.instructions = instructions;
        }

        public @Nullable LogicInstruction getReference() {
            return references.size() == 1 ? references.iterator().next() : null;
        }

        Definition merge(Definition other) {
            if (!variable.equals(other.variable)) {
                throw new MindcodeInternalError("Trying to merge definitions of different variables.");
            }

            Definition result = new Definition(variable, Math.min(counter, other.counter), inexact || other.inexact,
                    union(instructions, other.instructions));

            result.references.addAll(references);
            result.references.addAll(other.references);
            return result;
        }

        @Override
        public String toString() {
            return "Definition{" +
                    "variable=" + variable +
                    ", counter=" + counter +
                    ", inexact=" + inexact +
                    ", instructions=" + instructions.size() +
                    ", references=" + references.size() +
                    '}';
        }
    }

    private static List<LogicInstruction> union(List<LogicInstruction> list1, List<LogicInstruction> list2) {
        if (sameInstances(list1, list2)) {
            return list1;
        } else {
            Set<LogicInstruction> union = createIdentitySet(list1.size() + list2.size());
            union.addAll(list1);
            union.addAll(list2);
            return List.copyOf(union);
        }
    }

    private static <E> boolean sameInstances(List<E> list1, List<E> list2) {
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

    private void trace(Stream<String> text) {
        optimizationContext.trace(text);
    }

    private void trace(Supplier<String> text) {
        optimizationContext.trace(text);
    }

    private void trace(String text) {
        optimizationContext.trace(text);
    }

    private static <T> Set<T> createIdentitySet(int size) {
        return Collections.newSetFromMap(new IdentityHashMap<>(size));
    }
}
