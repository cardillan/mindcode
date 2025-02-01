package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicParameter;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.Icons;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/// This class resolves source code identifiers into variables represented by ValueStore interface,
/// and tracks temporary variables used within AST nodes for stack management.
///
/// Types of source code identifiers:
/// - processor variables
///   - global
///   - main/local
///   - function parameters
/// - external (memory-backed) variables
///
/// Features to be implemented:
/// - variable declarations
/// - namespaces
/// - arrays, records and other complex types
@NullMarked
public class Variables extends AbstractMessageEmitter {
    private final Set<AstMindcodeNode> reportedErrors = Collections.newSetFromMap(new IdentityHashMap<>());

    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final Map<String, ValueStore> globalVariables = Icons.createIconMapAsValueStore();
    private final Deque<FunctionContext> contextStack = new ArrayDeque<>();
    private FunctionContext functionContext = new GlobalContext();

    private HeapTracker heapTracker;

    public Variables(VariablesContext context) {
        super(context.messageConsumer());
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        heapTracker = HeapTracker.createDefaultTracker(context);
    }

    public void setHeapTracker(HeapTracker heapTracker) {
        this.heapTracker = heapTracker;
    }

    public boolean isVarargParameter(@Nullable AstExpression expression) {
        return expression instanceof AstIdentifier id && currentFunction().isVarargs(id.getName());
    }

    public MindcodeFunction currentFunction() {
        return functionContext.function();
    }

    public Collection<ValueStore> getActiveVariables() {
        return functionContext.getActiveVariables();
    }

    public LoopStack getLoopStack() {
        return functionContext.loopStack();
    }

    public List<FunctionArgument> getVarargs() {
        return functionContext.getVarargs();
    }

    private boolean isGlobalVariable(AstIdentifier identifier) {
        return identifier.isExternal() || processor.isGlobalName(identifier.getName());
    }

    private boolean isLinkedVariable(AstIdentifier identifier) {
        return processor.isBlockName(identifier.getName());
    }

    private ValueStore registerGlobalVariable(AstIdentifier identifier, ValueStore variable) {
        ValueStore existing = globalVariables.put(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of global variable (existing variable: %s, new variable: %s).",
                    existing, variable);
        }
        return variable;
    }

    /// Creates an implicit variable. Used in the relaxed syntax setting only. Analyzes the identifier to create
    ///  the correct variable type and put it into the correct variable list.
    ///
    /// @param identifier variable identifier
    /// @return ValueStore instance representing the variable
    private ValueStore createImplicitVariable(AstIdentifier identifier) {
        if (identifier.isExternal()) {
            return registerGlobalVariable(identifier, heapTracker.createVariable(identifier, Set.of()));
        } else if (isLinkedVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.block(identifier));
        } else if (isGlobalVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.global(identifier));
        } else {
            return functionContext.registerFunctionVariable(identifier, VariableScope.FUNCTION, true);
        }
    }

    /// Registers a constant. Reports possible name clashes.
    ///
    /// @param constant constant declaration to process
    /// @param value compile-time value associated with the constant
    public void createConstant(AstConstant constant, ValueStore value) {
        verifyGlobalDeclaration(constant, constant.getName());
        globalVariables.putIfAbsent(constant.getConstantName(), value);
    }

    /// Registers a parameter. Reports possible name clashes.
    ///
    /// Parameter is an mlog variable. The initialization of the variable must be done by the caller.
    ///
    /// @param parameter parameter declaration to process.
    /// @param value value assigned to the parameter
    /// @return ValueStore instance representing the parameter's variable
    public ValueStore createParameter(AstParameter parameter, LogicValue value) {
        ValueStore result = verifyGlobalDeclaration(parameter, parameter.getName())
                ? LogicParameter.parameter(parameter.getName(), value)
                : LogicParameter.parameter(new AstIdentifier(SourcePosition.EMPTY, "invalid"), value);

        globalVariables.putIfAbsent(parameter.getParameterName(), result);
        return result;
    }

    /// Registers a linked variable (implicitly global). Reports possible name clashes. Warns when the variable
    /// doesn't conform to linked variable names.
    ///
    /// @param identifier variable name
    /// @return ValueStore instance representing the linked variable
    public LogicVariable createLinkedVariable(AstIdentifier identifier, AstIdentifier linkedTo) {
        verifyGlobalDeclaration(identifier, identifier);

        if (!isLinkedVariable(linkedTo)) {
            warn(linkedTo, WARN.LINKED_VARIABLE_NOT_RECOGNIZED, linkedTo.getName());
        }

        LogicVariable result = LogicVariable.block(linkedTo);
        globalVariables.putIfAbsent(identifier.getName(), result);
        return result;
    }

    /// Registers an external variable in given scope. Reports possible name clashes.
    ///
    /// @param identifier variable name
    /// @return ValueStore instance representing the created variable
    public ValueStore createExternalVariable(AstIdentifier identifier, Set<Modifier> modifiers) {
        ValueStore result = verifyGlobalDeclaration(identifier, identifier)
                ? heapTracker.createVariable(identifier, modifiers)
                : LogicVariable.INVALID;

        globalVariables.putIfAbsent(identifier.getName(), result);
        return result;
    }

    /// Registers an array. Scope is always global.
    ///
    /// @param variable variable specification
    /// @return ValueStore instance representing the created variable
    public ArrayStore<?> createArray(AstIdentifier identifier, int size, Set<Modifier> modifiers) {
        ArrayStore<?> result;

        if (!verifyGlobalDeclaration(identifier, identifier)) {
            result = InternalArray.createInvalid(identifier, size);
        } else if (modifiers.contains(Modifier.EXTERNAL)) {
            result = heapTracker.createArray(identifier, size);
        } else {
            result = InternalArray.create(identifier, size);
        }

        globalVariables.putIfAbsent(identifier.getName(), result);
        return result;
    }

    /// Registers a standard variable in given scope. Reports possible name clashes.
    ///
    /// @param local      true if the variable should be registered in local scope, false for global scope
    /// @param identifier variable name
    /// @param scope      scope of the variable
    /// @param modifiers  declaration modifiers
    /// @return ValueStore instance representing the created variable
    public ValueStore createVariable(boolean local, AstIdentifier identifier, VariableScope scope, Set<Modifier> modifiers) {
        String name = identifier.getName();

        if (local) {
            if (functionContext.variables().containsKey(name)) {
                error(identifier, ERR.VARIABLE_MULTIPLE_DECLARATIONS, name);
                return Objects.requireNonNull(functionContext.variables().get(identifier.getName()));
            }
            return functionContext.registerFunctionVariable(identifier, scope, false);
        } else {
            if (globalVariables.containsKey(name)) {
                error(identifier, ERR.VARIABLE_MULTIPLE_DECLARATIONS, name);
                return Objects.requireNonNull(globalVariables.get(identifier.getName()));
            }
            return registerGlobalVariable(identifier, LogicVariable.global(identifier,
                    modifiers.contains(Modifier.VOLATILE), modifiers.contains(Modifier.NOINIT)));
        }
    }

    private boolean verifyGlobalDeclaration(AstMindcodeNode element, AstIdentifier identifierNode) {
        String identifier = identifierNode.getName();

        if (functionContext.variables().containsKey(identifier) || globalVariables.containsKey(identifier)) {
            error(element, ERR.VARIABLE_MULTIPLE_DECLARATIONS, identifier);
            return false;
        } else {
            return true;
        }
    }

    /// Resolves the variable encountered in the source code.
    ///
    /// Unknown variables handling:
    /// - Relaxed syntax: the variable is created implicitly.
    /// - Strict syntax: an error is reported, and an invalid variable instance is returned.
    ///
    /// @param identifier variable identifier
    /// @param local `true` if a local context is active. When local context is not active, local function variables
    ///         are excluded
    /// @param allowUndeclaredLinks `true` to automatically declare undeclared linked variables without an error.
    ///         Used by `param` and `allocate` nodes.
    /// @return ValueStore instance containing the variable
    public ValueStore resolveVariable(AstIdentifier identifier, boolean local, boolean allowUndeclaredLinks) {
        // Look for local variables first
        if (local && functionContext.variables().containsKey(identifier.getName())) {
            return Objects.requireNonNull(functionContext.variables().get(identifier.getName()));
        } else if (globalVariables.containsKey(identifier.getName())) {
            return Objects.requireNonNull(globalVariables.get(identifier.getName()));
        }

        if (allowUndeclaredLinks && isLinkedVariable(identifier)) {
            return LogicVariable.block(identifier);
        }

        if (reportedErrors.add(identifier)) {
            switch (profile.getSyntacticMode()) {
                case STRICT -> error(identifier, ERR.VARIABLE_NOT_DEFINED, identifier.getName());
                case MIXED  -> warn(identifier, WARN.VARIABLE_NOT_DEFINED, identifier.getName());
            }
        }

        return createImplicitVariable(identifier);
    }

    /// Tries to find a variable among declared variables. Returns null when not found.
    ///
    /// @param identifier variable identifier
    /// @param local      `true` to search in the local context as well.
    /// @return ValueStore instance containing the variable
    public @Nullable ValueStore findVariable(AstIdentifier identifier, boolean local) {
        // Look for local variables first
        if (local && functionContext.variables().containsKey(identifier.getName())) {
            return Objects.requireNonNull(functionContext.variables().get(identifier.getName()));
        } else if (globalVariables.containsKey(identifier.getName())) {
            return Objects.requireNonNull(globalVariables.get(identifier.getName()));
        }
        return null;
    }

    /// Indicates a new function is being processed. Function processing can become nested when
    /// processing inline function calls. In this case, previous function context is stored on a stack
    /// and restored when exiting the function processing. Non-inlined functions can't be nested.
    public void enterFunction(MindcodeFunction function, List<FunctionArgument> varargs) {
        contextStack.push(functionContext);
        functionContext = function.isRecursive()
                ? new RecursiveContext(messageConsumer, function, varargs)
                : new LocalContext(messageConsumer, function, varargs);
    }

    /// Called when function processing is finished. Previous function context is restored from the stack.
    public void exitFunction(MindcodeFunction function) {
        if (function != functionContext.function()) {
            throw new MindcodeInternalError("Wrong exitFunction order.");
        }
        if (contextStack.isEmpty()) {
            throw new MindcodeInternalError("Function stack is empty.");
        }
        functionContext = contextStack.pop();
    }

    public void enterAstNode() {
        functionContext.enterAstNode();
    }

    public void exitAstNode() {
        functionContext.exitAstNode();
    }

    public void registerNodeVariable(LogicVariable variable) {
        functionContext.registerNodeVariable(variable);
    }

    public void registerParentNodeVariable(LogicVariable variable) {
        functionContext.registerParentNodeVariable(variable);
    }

    /// Encapsulates processing of given expression, by keeping temporary variable(s) created while evaluating
    /// the expression out of current node context. Suitable when the generated temporary variables are known
    /// not to be used outside the context of the expression. A good example is the condition expression of the
    /// if statement: the condition is evaluated and the result is used to choose the branch to execute, but
    /// all this happens before either of the branches are executed and the temporary variable holding the condition
    /// value will not be used again.
    ///
    /// Note: if x = a > b then ... else ... end; print(x) is not a problem, because x is a user variable and
    /// is registered separately.
    ///
    /// @param <T> type of return value
    /// @param expression expression to evaluate
    /// @return value provided by the expression
    public <T> T excludeVariablesFromTracking(Supplier<T> expression) {
        return functionContext.excludeVariablesFromNode(expression);
    }

    /// Encapsulates processing of given expression, by keeping temporary variable(s) created while evaluating
    /// the expression out of current node context (see above). To be used on expressions that do not return value.
    public void excludeVariablesFromTracking(Runnable expression) {
        excludeVariablesFromTracking(() -> {
            expression.run();
            return Void.TYPE;
        });
    }
}
