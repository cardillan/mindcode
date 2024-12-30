package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicParameter;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.Icons;
import info.teksol.mc.util.StringUtils;
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
    // TODO Add proper detection of relaxed syntax
    private static final boolean RELAXED_SYNTAX = true;

    private final InstructionProcessor processor;
    private final CallGraph callGraph;
    private final Map<String, ValueStore> globalVariables = Icons.createIconMapAsValueStore();
    private final Deque<FunctionContext> contextStack = new ArrayDeque<>();
    private FunctionContext functionContext = new GlobalContext();

    private boolean heapAllocated = false;
    private HeapTracker heapTracker;

    public Variables(CodeGeneratorContext context) {
        super(context.messageConsumer());
        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        heapAllocated = context.heapAllocation() != null;
        heapTracker = HeapTracker.createDefaultTracker(context);
    }

    public void setHeapTracker(HeapTracker heapTracker) {
        heapAllocated = true;
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

    private boolean isLocalContext() {
        MindcodeFunction function = functionContext.function();
        // Under relaxed syntax, the main function isn't considered local
        return !function.isMain() || !RELAXED_SYNTAX;
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
        String name = identifier.getName();
        if (identifier.isExternal()) {
            if (!heapAllocated) {
                error(identifier, "The heap must be allocated before using it.");
            }
            return registerGlobalVariable(identifier, heapTracker.createVariable(identifier));
        } else if (isLinkedVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.block(name));
        } else if (isGlobalVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.global(name, callGraph.getSyncedVariables().contains(name)));
        } else {
            return functionContext.registerFunctionVariable(identifier);
        }
    }

    /// Registers a constant. Reports possible name clashes.
    ///
    /// @param constant constant declaration to process
    /// @param value compile-time value associated with the constant
    public void createConstant(AstConstant constant, ValueStore value) {
        verifyConstantOrParameter(constant, constant.getName(), value, "constant");
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
        ValueStore result = verifyConstantOrParameter(parameter, parameter.getName(), value, "parameter")
                ? LogicParameter.parameter(parameter.getParameterName(), value)
                : LogicParameter.parameter("invalid", value);

        globalVariables.putIfAbsent(parameter.getParameterName(), result);
        return result;
    }

    private boolean verifyConstantOrParameter(AstMindcodeNode element, AstIdentifier identifierNode,
            ValueStore value, String elementName) {
        String identifier = identifierNode.getName();

        if (isLocalContext()) {
            error(element, "%s must be declared outside a function/a main code block.",
                    StringUtils.firstUpperCase(elementName));
        }

        if (isLinkedVariable(identifierNode)) {
            error(identifierNode, "Identifier '%s' is reserved for linked blocks.", identifier);
        }

        if (functionContext.variables().containsKey(identifier)) {
            error(element, "Cannot redefine variable '%s' as a %s.", identifier, elementName);
            return false;
        } else if (globalVariables.containsKey(identifier)) {
            error(element,
                    globalVariables.get(identifier).isLvalue()
                            ? "Cannot redefine variable '%s' as a %s."
                            : "Multiple declarations of '%s'.",
                    identifier, elementName);
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
    /// @return Value instance containing the variable
    public ValueStore resolveVariable(AstIdentifier identifier) {
        // Look for local variables first
        if (functionContext.variables().containsKey(identifier.getName())) {
            return functionContext.variables().get(identifier.getName());
        } else if (globalVariables.containsKey(identifier.getName())) {
            return globalVariables.get(identifier.getName());
        }

        if (RELAXED_SYNTAX) {
            return createImplicitVariable(identifier);
        } else {
            error(identifier, "Variable '%s' is not defined.", identifier.getName());
            return LogicVariable.main("invalid");
        }
    }

    /// Indicates a new function is being processed. Function processing can become nested when
    /// processing inline function calls. In this case, previous function context is stored on a stack
    /// and restored when exiting the function processing. Non-inlined functions can't be nested.
    ///
    /// When processing the global level, no function is active. A global context is used in this case.
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
            return null;
        });
    }
}
