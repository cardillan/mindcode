package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstConstant;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstParameter;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/// This class resolves source code identifiers into variables represented by NodeValue interface,
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
    private final Map<String, NodeValue> globalVariables = new HashMap<>();
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

    private boolean isLocalContext() {
        LogicFunction function = functionContext.function();
        // Under relaxed syntax, the main function isn't considered local
        return function != null && (!function.isMain() || !RELAXED_SYNTAX);
    }

    private boolean isGlobalVariable(AstIdentifier identifier) {
        return identifier.isExternal() || processor.isGlobalName(identifier.getName());
    }

    private boolean isLinkedVariable(AstIdentifier identifier) {
        return processor.isBlockName(identifier.getName());
    }

    private NodeValue registerGlobalVariable(AstIdentifier identifier, NodeValue variable) {
        NodeValue existing = globalVariables.put(identifier.getName(), variable);
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
    /// @return NodeValue instance representing the variable
    private NodeValue createImplicitVariable(AstIdentifier identifier) {
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
    public void createConstant(AstConstant constant, LogicValue value) {
        if (isLocalContext()) {
            error(constant, "Constants must be declared outside a function/a main code block..");
        }

        if (globalVariables.containsKey(constant.getConstantName())) {
            error(constant,
                    globalVariables.get(constant.getConstantName()).isLvalue()
                            ? "Cannot redefine variable '%s' as a constant."
                            : "Multiple declarations of '%s'.",
                    constant.getConstantName());
        } else {
            globalVariables.put(constant.getConstantName(), value);
        }
    }

    /// Registers a parameter. Reports possible name clashes.
    ///
    /// Parameter is an mlog variable. The initialization of the variable must be done by the caller.
    ///
    /// @param parameter parameter declaration to process.
    /// @param value value assigned to the parameter
    /// @return NodeValue instance representing the parameter's variable
    public NodeValue createParameter(AstParameter parameter, LogicValue value) {
        if (isLocalContext()) {
            error(parameter, "Parameter must be declared outside a function/a main code block.");
        }

        if (globalVariables.containsKey(parameter.getParameterName())) {
            error(parameter,
                    globalVariables.get(parameter.getParameterName()).isLvalue()
                            ? "Cannot redefine variable '%s' as a parameter."
                            : "Multiple declarations of '%s'.",
                    parameter.getParameterName());
            return LogicParameter.parameter("invalid", value);
        } else {
            return globalVariables.computeIfAbsent(parameter.getParameterName(),
                    name -> LogicParameter.parameter(parameter.getParameterName(), value));
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
    public NodeValue resolveVariable(AstIdentifier identifier) {
        // Look for local variables first
        if (functionContext.variables().containsKey(identifier.getName())) {
            return functionContext.variables().get(identifier.getName());
        } else if (globalVariables.containsKey(identifier.getName())) {
            // `variables` only contains global variables
            // Under relaxed syntax, automatically generated local variables won't make it there
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
    public void enterFunction(LogicFunction function, String functionPrefix) {
        contextStack.push(functionContext);
        functionContext = function.isRecursive()
                ? new RecursiveContext(function, functionPrefix)
                : new LocalContext(function, functionPrefix);
    }

    /// Called when function processing is finished. Previous function context is restored from the stack.
    public void exitFunction(LogicFunction function) {
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
}
