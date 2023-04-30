package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.WrongNumberOfParametersException;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;
import static info.teksol.mindcode.logic.LogicBoolean.TRUE;
import static info.teksol.mindcode.logic.LogicNull.NULL;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<LogicValue> {

    private final CompilerProfile profile;
    // The version-dependent functionality is encapsulated in InstructionProcessor and FunctionMapper.
    // If future Mindustry versions offer more capabilities (such as native stack support),
    // even LogicInstructionGenerator might be made version dependent.
    private final InstructionProcessor instructionProcessor;
    private final FunctionMapper functionMapper;
    private final LogicInstructionPipeline pipeline;
    private final ReturnStack returnStack = new ReturnStack();

    private LoopStack loopStack = new LoopStack();

    // Contains the information about functions built from the program to support code generation
    private CallGraph callGraph;

    private final ConstantExpressionEvaluator expressionEvaluator;

    // These instances track variables that need to be stored on stack for recursive function calls.
    
    // Constants and global variables
    // Key is the name of variable/constant
    // Value is either an ConstantAstNode (for constant) or null (for variable)
    private final Map<String, LogicLiteral> constants = new HashMap<>();

    // Tracks all local function variables, including function parameters - once accessed, they have to be preserved.
    private LocalContext functionContext = new LocalContext();

    // Tracks variables whose scope is limited to the node being processed and have no meaning outside the node.
    private NodeContext nodeContext = new NodeContext();

    // Tracks variables whose scope is limited to the parent node. These are variables that transfer the return value
    // of a node to its parent. When a new node is visited, nodeContext becomes parentContext and a new node context
    // is created.
    private NodeContext parentContext = new NodeContext();

    // Function definition being presently compiled (including inlined functions)
    private CallGraph.Function currentFunction; 

    // Prefix for local variables (depends on function being processed)
    private String localPrefix = "";

    private int heapBaseAddress = 0;
    
    private int heapSize = 0;

    private int markerIndex = 0;

    public LogicInstructionGenerator(CompilerProfile profile, InstructionProcessor instructionProcessor,
                                     FunctionMapper functionMapper, LogicInstructionPipeline pipeline) {
        this.instructionProcessor = instructionProcessor;
        this.functionMapper = functionMapper;
        this.pipeline = pipeline;
        this.profile = profile;
        this.expressionEvaluator = new ConstantExpressionEvaluator(instructionProcessor);
    }

    public void start(Seq program) {
        callGraph = CallGraphCreator.createFunctionGraph(program, instructionProcessor);
        currentFunction = callGraph.getMain();
        verifyStackAllocation();
        visit(program);
        appendFunctionDeclarations();
    }

    public void emit(LogicInstruction instruction) {
        pipeline.emit(instruction);
    }

    // Visit is called for every node. This overridden function ensures proper transition of variable contexts between
    // parent and child nodes.
    @Override
    public LogicValue visit(AstNode node) {
        NodeContext previousParent = parentContext;
        parentContext = nodeContext;
        nodeContext = new NodeContext(parentContext);  // inherit variables from parent context
        try {
            // Perform constant expression evaluation
            return super.visit(expressionEvaluator.evaluate(node));
        } finally {
            nodeContext = parentContext;
            parentContext = previousParent;
        }
    }

    public LogicVariable visitVariable(AstNode node) {
        LogicValue result = visit(node);
        if (result instanceof LogicVariable variable) {
            return variable;
        }

        // Internal error
        throw new GenerationException("Expected variable, got " + result);
    }


    public CallRecInstruction createCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr) {
        return instructionProcessor.createCallRecursive(stack, callAddr, retAddr);
    }

    public EndInstruction createEnd() {
        return instructionProcessor.createEnd();
    }

    public GotoInstruction createGoto(LogicVariable address) {
        return instructionProcessor.createGoto(address);
    }

    public JumpInstruction createJump(LogicLabel label, Condition condition, LogicValue x, LogicValue y) {
        return instructionProcessor.createJump(label, condition, x, y);
    }

    public JumpInstruction createJumpUnconditional(LogicLabel label) {
        return instructionProcessor.createJumpUnconditional(label);
    }

    public LabelInstruction createLabel(LogicLabel label) {
        return instructionProcessor.createLabel(label);
    }

    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first) {
        return instructionProcessor.createOp(operation, target, first);
    }

    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return instructionProcessor.createOp(operation, target, first, second);
    }

    public PopInstruction createPop(LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPop(memory, value);
    }

    public PrintInstruction createPrint(LogicValue what) {
        return instructionProcessor.createPrint(what);
    }

    public PrintflushInstruction createPrintflush(LogicVariable messageBlock) {
        return instructionProcessor.createPrintflush(messageBlock);
    }

    public PushInstruction createPush(LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPush(memory, value);
    }

    public ReadInstruction createRead(LogicVariable result, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createRead(result, memory, index);
    }

    public ReturnInstruction createReturn(LogicVariable memory) {
        return instructionProcessor.createReturn(memory);
    }

    public SensorInstruction createSensor(LogicVariable result, LogicValue target, LogicValue property) {
        return instructionProcessor.createSensor(result, target, property);
    }

    public SetInstruction createSet(LogicVariable target, LogicValue value) {
        return instructionProcessor.createSet(target, value);
    }

    public SetAddressInstruction createSetAddress(LogicVariable variable, LogicLabel address) {
        return instructionProcessor.createSetAddress(variable, address);
    }

    public CallInstruction createCallStackless(LogicAddress value) {
        return instructionProcessor.createCallStackless(value);
    }

    public StopInstruction createStop() {
        return instructionProcessor.createStop();
    }

    public WriteInstruction createWrite(LogicValue value, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createWrite(value, memory, index);
    }

    public WriteInstruction createWriteAddress(LogicAddress value, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createWriteAddress(value, memory, index);
    }

    private LogicLabel nextLabel() {
        return instructionProcessor.nextLabel();
    }

    // Allocates a new temporary variable whose scope is limited to a node (i.e. not needed outside that node)
    private LogicVariable nextTemp() {
        return nodeContext.registerVariable(instructionProcessor.nextTemp());
    }

    // Allocates a new temporary variable which holds the evaluated value of a node
    private LogicVariable nextNodeResult() {
        return parentContext.registerVariable(instructionProcessor.nextTemp());
    }

    // Allocates a new temporary variable which holds the return value of a function
    private LogicVariable nextReturnValue() {
        return parentContext.registerVariable(instructionProcessor.nextReturnValue());
    }

    private String nextMarker() {
        return "marker" + markerIndex++;
    }

    private void verifyStackAllocation() {
        if (callGraph.containsRecursiveFunction()) {
            if (callGraph.getAllocatedStack() == null) {
                throw new MissingStackException("Cannot declare functions when no stack was allocated");
            }
        }
    }

    private LogicLiteral queryConstantName(String name) {
        if (constants.get(name) != null) {
            return constants.get(name);
        } else {
            // Register the identifier as a variable name
            constants.put(name, null);
            return null;
        }
    }

    private void registerConstant(String name, ConstantAstNode value) {
        if (constants.get(name) != null) {
            throw new GenerationException("Multiple declarations of constant '" + name + "'");
        } else if (constants.containsKey(name)) {
            throw new GenerationException("Cannot redefine variable or function parameter '" + name + "' as a constant");
        }
        constants.put(name, value.toLogicLiteral(instructionProcessor));
    }

    private void appendFunctionDeclarations() {
        emit(createEnd());

        for (CallGraph.Function function : callGraph.getFunctions()) {
            if (function.isInline() || !function.isUsed()) {
                continue;
            }

            currentFunction = function;
            localPrefix = function.getLocalPrefix();
            functionContext = new LocalContext();
            emit(createLabel(function.getLabel()).withMarker(localPrefix));
            returnStack.enterFunction(nextLabel(), LogicVariable.fnRetVal(localPrefix));

            if (function.isRecursive()) {
                appendRecursiveFunctionDeclaration(function);
            } else {
                appendStacklessFunctionDeclaration(function);
            }

            emit(createEnd());
            localPrefix = "";
            returnStack.exitFunction();
            currentFunction = callGraph.getMain();
        }

    }

    private void appendRecursiveFunctionDeclaration(CallGraph.Function function) {
        // Register all parameters for stack storage
        function.getParams().stream().map(this::visitVariableVarRef).forEach(functionContext::registerVariable);

        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(localPrefix), body));
        emit(createLabel(returnStack.getReturnLabel()).withMarker(localPrefix));
        emit(createReturn(stackName()));
    }

    private void appendStacklessFunctionDeclaration(CallGraph.Function function) {
        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(localPrefix), body));
        emit(createLabel(returnStack.getReturnLabel()).withMarker(localPrefix));
        emit(createGoto(LogicVariable.fnRetAddr(localPrefix)));
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public LogicValue visitFunctionCall(FunctionCall node) {
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        final List<LogicValue> params = nodeContext.encapsulate(
                () -> node.getParams().stream().map(this::visit).collect(Collectors.toList()));

        // Special cases
        return switch (node.getFunctionName()) {
            case "printf"   -> handlePrintf(node, params);
            default         -> handleFunctionCall(node.getFunctionName(), params);
        };
    }

    private LogicValue handleFunctionCall(String functionName, List<LogicValue> params) {
        LogicValue output = functionMapper.handleFunction(pipeline, functionName, params);
        if (output != null) {
            return output;
        } else if (callGraph.containsFunction(functionName)) {
            return handleUserFunctionCall(functionName, params);
        } else {
            throw new UndeclaredFunctionException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private LogicValue handleUserFunctionCall(String functionName, List<LogicValue> params) {
        CallGraph.Function function = callGraph.getFunction(functionName);
        if (params.size() != function.getParamCount()) {
            throw new WrongNumberOfParametersException("Function '" + functionName + "': wrong number of parameters (expected "
                    + function.getParamCount() + ", found " + params.size() + ")");
        }

        // Switching to new function prefix -- save/restore old one
        String previousPrefix = localPrefix;
        try {
            // Entire inline function evaluates using given prefix (different invocations use different variables).
            // For other functions, the prefix is only used to set up variables representing function parameters.
            localPrefix = function.isInline() ? instructionProcessor.nextLocalPrefix() : function.getLocalPrefix();

            if (function.isInline()) {
                return handleInlineFunctionCall(function, params);
            } else if (!function.isRecursive()) {
                return handleStacklessFunctionCall(function, params);
            } else {
                return handleRecursiveFunctionCall(function, params);
            }
        } finally {
            localPrefix = previousPrefix;
        }
    }

    private LogicValue handleInlineFunctionCall(CallGraph.Function function, List<LogicValue> paramValues) {
        // Switching to inline function context -- save/restore old one
        final CallGraph.Function previousFunction = currentFunction;
        final LocalContext previousContext = functionContext;
        final LoopStack previousLoopStack = loopStack;
        try {
            currentFunction = function;
            functionContext = new LocalContext();
            loopStack = new LoopStack();

            emit(createLabel(nextLabel()).withMarker(localPrefix));
            setupFunctionParameters(function, paramValues);

            // Retval gets registered in nodeContext, but we don't mind -- inline functions do not use stack
            final LogicVariable returnValue = nextReturnValue();
            final LogicLabel returnLabel = nextLabel();
            returnStack.enterFunction(returnLabel, returnValue);
            LogicValue result = visit(function.getBody());
            emit(createSet(returnValue, result));
            emit(createLabel(returnLabel).withMarker(localPrefix));
            returnStack.exitFunction();
            return returnValue;
        } finally {
            loopStack = previousLoopStack;
            functionContext = previousContext;
            currentFunction = previousFunction;
        }
    }

    private LogicValue handleStacklessFunctionCall(CallGraph.Function function, List<LogicValue> paramValues) {
        setupFunctionParameters(function, paramValues);

        final LogicLabel returnLabel = nextLabel();
        emit(createSetAddress(LogicVariable.fnRetAddr(localPrefix), returnLabel));
        emit(createCallStackless(function.getLabel()));
        emit(createLabel(returnLabel)); // where the function must return

        return passReturnValue(function);
    }

    private List<LogicVariable> getContextVariables() {
        Set<LogicVariable> result = new LinkedHashSet<>(functionContext.getVariables());
        result.addAll(nodeContext.getVariables());
        return new ArrayList<>(result);
    }

    private LogicValue handleRecursiveFunctionCall(CallGraph.Function function, List<LogicValue> paramValues) {
        String marker = nextMarker();

        boolean useStack = currentFunction.isRecursiveCall(function.getName());
        List<LogicVariable> variables = useStack ? getContextVariables() : List.of();

        if (useStack) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> emit(createPush(stackName(), v).withMarker(marker)));
        }

        setupFunctionParameters(function, paramValues);

         // Recursive function call
        final LogicLabel returnLabel = nextLabel();
        emit(createCallRecursive(stackName(), function.getLabel(), returnLabel).withMarker(marker));
        emit(createLabel(returnLabel)); // where the function must return

        if (useStack) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> emit(createPop(stackName(), v).withMarker(marker)));
        }

        return passReturnValue(function);
    }

    private void setupFunctionParameters(CallGraph.Function function, List<LogicValue> arguments) {
        // Setup variables representing function parameters with values from this call
        List<VarRef> paramsRefs = function.getParams();
        for (int i = 0; i < arguments.size(); i++) {
            // Visiting paramRefs (which are VarRefs) creates local variables from them
            // paramValues were visited in caller's context
            VarRef varRef = paramsRefs.get(i);
            LogicVariable argument = visitVariableVarRef(varRef, "Function '" + function.getName() +
                    "': parameter name '" + varRef.getName() + "' clashes with existing constant.");

            emit(createSet(argument, arguments.get(i)));
        }
    }

    private LogicValue passReturnValue(CallGraph.Function function) {
        if (currentFunction.isRepeatedCall(function.getName())) {
            // Copy default return variable to new temp, for the function is called multiple times
            // and we must not overwrite result of previous call(s) with this one
            //
            // Allocate 'return value' type temp variable for it, so that it won't be eliminated;
            // this is easier than ensuring optimizers do not eliminate normal temporary variables
            // that received return values from functions.
            //
            // TODO: optimizer to remove resultVariable when not needed (not used across same function calls).
            //       Will be complicated; the optimizer would need to utilize the call graph
            final LogicVariable resultVariable = nextReturnValue();
            emit(createSet(resultVariable, LogicVariable.fnRetVal(localPrefix)));
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this function's call tree
            return LogicVariable.fnRetVal(localPrefix);
        }
    }

    private LogicVariable stackName() {
        return callGraph.getAllocatedStack().getStack();
    }



    private LogicValue resolveHeapIndex(HeapAccess node) {
        if (node.isAbsolute()) {
            return visit(node.getAddress());
        } else {
            int index = ((NumericLiteral)node.getAddress()).getAsInteger();
            if (index >= heapSize) {
                throw new OutOfHeapSpaceException("Allocated heap is too small! Increase the size of the allocation, or switch to a Memory Bank to give the heap even more space");
            }
            return LogicNumber.get(index + heapBaseAddress);
        }
    }

    @Override
    public LogicValue visitHeapAccess(HeapAccess node) {
        final LogicVariable tmp = nextNodeResult();
        final LogicValue index = resolveHeapIndex(node);
        emit(createRead(tmp, createVariable(node.getCellName()), index));
        return tmp;
    }

    @Override
    public LogicValue visitIfExpression(IfExpression node) {
        if (profile.isShortCircuitEval() && node.getCondition() instanceof BoolBinaryOp boolNode) {
            final LogicVariable tmp = nextNodeResult();
            final LogicLabel shortCircuitLabel = nextLabel();
            final LogicLabel finishLabel = nextLabel();
            final boolean naturalOrder = nodeContext.encapsulate(() -> processBoolBinaryOp(boolNode, shortCircuitLabel));

            final LogicValue firstBranch = visit(naturalOrder ? node.getTrueBranch() : node.getFalseBranch());
            emit(createSet(tmp, firstBranch));
            emit(createJumpUnconditional(finishLabel));

            emit(createLabel(shortCircuitLabel));
            final LogicValue secondBranch = visit(naturalOrder ? node.getFalseBranch() : node.getTrueBranch());
            emit(createSet(tmp, secondBranch));
            emit(createLabel(finishLabel));

            return tmp;
        } else {
            final LogicValue cond = nodeContext.encapsulate(() -> visit(node.getCondition()));

            final LogicVariable tmp = nextNodeResult();
            final LogicLabel elseBranch = nextLabel();
            final LogicLabel endBranch = nextLabel();

            emit(createJump(elseBranch, Condition.EQUAL, cond, FALSE));

            final LogicValue trueBranch = visit(node.getTrueBranch());
            emit(createSet(tmp, trueBranch));
            emit(createJumpUnconditional(endBranch));

            emit(createLabel(elseBranch));
            final LogicValue falseBranch = visit(node.getFalseBranch());
            emit(createSet(tmp, falseBranch));
            emit(createLabel(endBranch));

            return tmp;
        }
    }

    @Override
    public LogicValue visitSeq(Seq seq) {
        visit(seq.getRest());
        return visit(seq.getLast());
    }

    @Override
    public LogicValue visitNoOp(NoOp node) {
        return NULL;
    }

    @Override
    public LogicValue visitConstant(Constant node) {
        if (!localPrefix.isEmpty()) {
            throw new GenerationException("Constant declaration not allowed in user function [" + node.getName() + "]");
        }
        AstNode value = expressionEvaluator.evaluate(node.getValue());
        if (value instanceof ConstantAstNode constant) {
            registerConstant(node.getName(), constant);
        } else {
            throw new GenerationException("Constant declaration of [" + node.getName() + "] does not use a constant expression");
        }
        return NULL;
    }

    @Override
    public LogicValue visitDirective(Directive node) {
        // Do nothing - directives are preprocessed
        return null;
    }

    @Override
    public LogicValue visitAssignment(Assignment node) {
        final LogicValue rvalue = visit(node.getValue());

        switch (node.getVar()) {
            case HeapAccess heapAccess -> {
                final LogicValue address = resolveHeapIndex(heapAccess);
                emit(createWrite(rvalue, createVariable(heapAccess.getCellName()), address));
            }

            case PropertyAccess propertyAccess -> {
                LogicValue propTarget = visit(propertyAccess.getTarget());
                LogicArgument prop = visit(propertyAccess.getProperty());
                // TODO modify PropertyAccess to distinguish direct and indirect properties
                String propertyName = prop instanceof LogicBuiltIn lb ? lb.getName() : prop.toMlog();
                if (functionMapper.handleProperty(pipeline, propertyName, propTarget, List.of(rvalue)) == null) {
                    throw new UndeclaredFunctionException("Don't know how to handle property [" + propTarget + "." + prop + "]");
                }
            }

            case VarRef varRef -> {
                String name = varRef.getName();
                if (constants.get(name) != null) {
                    throw new GenerationException("Assignment to constant '" + name + "' not allowed");
                }

                final LogicValue target = visit(node.getVar());
                if (target instanceof LogicVariable variable) {
                    if (target.getType() != ArgumentType.BLOCK) {
                        emit(createSet(variable, rvalue));
                        // TODO returning rvalue causes temp variable to be used twice, disallowing some optimizations
                        //      investigate returning variable instead of rvalue here
                    } else {
                        throw new GenerationException("Assignment to variable '" + target + "' not allowed (name reserved for linked blocks)");
                    }
                } else {
                    throw new GenerationException("Impossible assignment target " + target);
                }
            }

            default -> throw new GenerationException("Unhandled assignment target in " + node);
        }

        if (rvalue instanceof LogicVariable variable) {
            // rvalue holds the result of this node -- when it is a variable, it needs to be explicitly registered
            // in parent node context.
            // Note: this call can place user variable on node context. This should be all right, any such variable
            // would be registered in functionContext as well. Additional registration here is harmless.
            parentContext.registerVariable(variable);
        }

        return rvalue;
    }

    @Override
    public LogicValue visitUnaryOp(UnaryOp node) {
        LogicValue expression = visit(node.getExpression());
        LogicVariable  tmp = nextNodeResult();
        emit(createOp(Operation.fromMindcode(node.getOp()), tmp, expression));
        return tmp;
    }

    @Override
    public LogicValue visitForEachStatement(ForEachExpression node) {
        List<AstNode> values = node.getValues();
        if (values.isEmpty()) {
            // Empty list -- nothing to do
            return NULL;
        }

        final LogicVariable variable = visitVariable(node.getVariable());
        final LogicLabel contLabel = nextLabel();
        final LogicLabel bodyLabel = nextLabel();
        final LogicLabel exitLabel = nextLabel();
        final LogicVariable iterator = nextTemp();        // Holds instruction address of the next iteration
        final String marker = nextMarker();

        loopStack.enterLoop(node.getLabel(), exitLabel, contLabel);

        // All but the last value
        for (int i = 0; i < values.size() - 1; i++) {
            LogicLabel nextValueLabel = nextLabel();
            // Setting the iterator first. It is possible to use continue in the value expression,
            // in which case the next iteration is performed.
            emit(createSetAddress(iterator, nextValueLabel));
            emit(createSet(variable, visit(values.get(i))));
            emit(createJumpUnconditional(bodyLabel));
            emit(createLabel(nextValueLabel).withMarker(marker));
        }

        // Last value
        emit(createSetAddress(iterator, exitLabel));
        emit(createSet(variable, visit(values.get(values.size() - 1))));

        emit(createLabel(bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        emit(createLabel(contLabel));
        emit(createGoto(iterator).withMarker(marker));
        emit(createLabel(exitLabel));
        loopStack.exitLoop(node.getLabel());

        return NULL;
    }

    @Override
    public LogicValue visitWhileStatement(WhileExpression node) {
        final LogicLabel beginLabel = nextLabel();
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        emit(createLabel(beginLabel));
        final LogicValue cond = visit(node.getCondition());
        emit(createJump(doneLabel, Condition.EQUAL, cond, FALSE));
        visit(node.getBody());
        emit(createLabel(continueLabel));
        visit(node.getUpdate());
        emit(createJumpUnconditional(beginLabel));
        emit(createLabel(doneLabel));
        loopStack.exitLoop(node.getLabel());
        return NULL;
    }

    @Override
    public LogicValue visitDoWhileStatement(DoWhileExpression node) {
        final LogicLabel beginLabel = nextLabel();
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        emit(createLabel(beginLabel));
        visit(node.getBody());
        emit(createLabel(continueLabel));
        final LogicValue cond = visit(node.getCondition());
        emit(createJump(beginLabel, Condition.NOT_EQUAL, cond, FALSE));
        emit(createLabel(doneLabel));
        loopStack.exitLoop(node.getLabel());
        return NULL;
    }

    @Override
    public LogicValue visitBoolBinaryOp(BoolBinaryOp node) {
        if (!profile.isShortCircuitEval()) {
            return visitBinaryOp(node);
        }

        LogicLabel shortCircuitLabel = nextLabel();
        LogicLabel finishLabel = nextLabel();
        LogicVariable tmp = nextNodeResult();
        boolean naturalOrder = processBoolBinaryOp(node, shortCircuitLabel);

        emit(createSet(tmp, naturalOrder ? TRUE : FALSE));
        emit(createJumpUnconditional(finishLabel));
        emit(createLabel(shortCircuitLabel));
        emit(createSet(tmp, naturalOrder ? FALSE : TRUE));
        emit(createLabel(finishLabel));
        return tmp;
    }

    /**
     *
     * @param node node to evaluate
     * @param shortCircuitLabel label to use for when short-circuit is possible
     * @return true to emin true branch first, false to emit false branch first
     */
    private boolean processBoolBinaryOp(BoolBinaryOp node, LogicLabel shortCircuitLabel) {
        final boolean logicalAnd = switch (node.getOp()) {
            case "and" -> true;
            case "or" -> false;
            default -> throw new GenerationException("Unhandled BoolBinaryOp operation in " + node);
        };
        final Condition condition = logicalAnd ? Condition.EQUAL : Condition.NOT_EQUAL;

        final LogicValue left = visit(node.getLeft());
        emit(createJump(shortCircuitLabel, condition, left, FALSE));
        final LogicValue right = visit(node.getRight());
        emit(createJump(shortCircuitLabel, condition, right, FALSE));
        return logicalAnd;
    }

    @Override
    public LogicValue visitBinaryOp(BinaryOp node) {
        final LogicValue left = visit(node.getLeft());
        final LogicValue right = visit(node.getRight());
        final LogicVariable tmp = nextNodeResult();
        emit(createOp(Operation.fromMindcode(node.getOp()), tmp, left, right));
        return tmp;
    }

    @Override
    public LogicValue visitRef(Ref node) {
        // TODO: warn when "configure" is used in V7
        return LogicBuiltIn.create(node.getName());
    }

    @Override
    public LogicValue visitVarRef(VarRef node) {
        return createVariableOrConstant(node.getName(), false, null);
    }

    private LogicVariable visitVariableVarRef(VarRef node) {
        return (LogicVariable) createVariableOrConstant(node.getName(), true, null);
    }

    private LogicVariable visitVariableVarRef(VarRef node, String errorMessage) {
        return (LogicVariable) createVariableOrConstant(node.getName(), true, errorMessage);
    }

    // A variable is required here
    private LogicVariable createVariable(String identifier) {
        return (LogicVariable) createVariableOrConstant(identifier, true, null);
    }

    private LogicValue createValue(String identifier) {
        return createVariableOrConstant(identifier, false, null);
    }

    private LogicValue createVariableOrConstant(String identifier, boolean requireVariable, String errorMessage) {
        // If the name refers to a constant, use it.
        // If it wasn't a constant already, the name will be reserved for a variable
        LogicValue constant = queryConstantName(identifier);
        if (constant != null) {
            if (requireVariable) {
                throw new GenerationException(errorMessage != null ? errorMessage
                        : '\'' + identifier + "' is a constant; variable is expected here.");
            }
            return constant;
        }

        if (identifier.startsWith(AstNodeBuilder.AST_PREFIX)) {
            // Encoded into a VarRef in AstNodeBuilder
            return LogicVariable.ast(identifier);
        } else if (instructionProcessor.isBlockName(identifier)) {
            return LogicVariable.block(identifier);
        } else if (instructionProcessor.isGlobalName(identifier)) {
            // Global variables aren't registered locally -- they must not be pushed onto stack!
            return LogicVariable.global(identifier);
        } else if (localPrefix.isEmpty()) {
            // Main variable - again not pushed onto stack
            return LogicVariable.main(identifier);
        } else {
            // A truly local variable
            return functionContext.registerVariable(LogicVariable.local(currentFunction.getName(), localPrefix, identifier));
        }
    }

    @Override
    public LogicValue visitNullLiteral(NullLiteral node) {
        return NULL;
    }

    @Override
    public LogicValue visitBooleanLiteral(BooleanLiteral node) {
        return node.toLogicLiteral(instructionProcessor);
    }

    @Override
    public LogicValue visitStringLiteral(StringLiteral node) {
        return node.toLogicLiteral(instructionProcessor);
    }

    @Override
    public LogicValue visitNumericLiteral(NumericLiteral node) {
        return node.toLogicLiteral(instructionProcessor);
    }

    @Override
    public LogicValue visitNumericValue(NumericValue node) {
        // Should never happen; NumericValues are handled in constant expression evaluation
        throw new GenerationException("Unexpected call to visitNumericValue");
    }

    @Override
    public LogicValue visitPropertyAccess(PropertyAccess node) {
        final LogicValue target = visit(node.getTarget());
        final LogicValue prop = visit(node.getProperty());
        final LogicVariable tmp = nextNodeResult();
        emit(createSensor(tmp, target, prop));
        return tmp;
    }

    @Override
    public LogicValue visitCaseExpression(CaseExpression node) {
        final LogicVariable resultVar = nextNodeResult();
        final LogicLabel exitLabel = nextLabel();

        final LogicValue caseValue = visit(node.getCondition());
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final LogicLabel nextAlt = nextLabel();         // Next alternative
            final LogicLabel bodyLabel = nextLabel();       // Body of this alternative

            nodeContext.encapsulate(() -> {
                // Each matching value, including the last one, causes a jump to the "when" body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstNode value : alternative.getValues()) {
                    if (value instanceof Range range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        LogicLabel nextExp = nextLabel();       // Next value in when list
                        final LogicValue minValue = visit(range.getFirstValue());
                        emit(createJump(nextExp, Condition.LESS_THAN, caseValue, minValue));
                        // The max value is only evaluated when the min value lets us through
                        final LogicValue maxValue = visit(range.getLastValue());
                        emit(createJump(bodyLabel, range.maxValueComparison(), caseValue, maxValue));
                        emit(createLabel(nextExp));
                    }
                    else {
                        final LogicValue whenValue = visit(value);
                        emit(createJump(bodyLabel, Condition.EQUAL, caseValue, whenValue));
                    }
                }
            });

            // No match in the "when" value list: skip to the next alternative
            emit(createJumpUnconditional(nextAlt));

            // Body of the alternative
            emit(createLabel(bodyLabel));
            final LogicValue body = visit(alternative.getBody());
            emit(createSet(resultVar, body));
            emit(createJumpUnconditional(exitLabel));

            emit(createLabel(nextAlt));
        }

        final LogicValue elseBranch = visit(node.getElseBranch());
        emit(createSet(resultVar, elseBranch));
        emit(createLabel(exitLabel));

        return resultVar;
    }

    @Override
    public LogicValue visitCaseAlternative(CaseAlternative node) {
        // Case alternatives are handled in visitCaseExpression
        throw new GenerationException("Unexpected call to visitCaseAlternative");
    }

    @Override
    public LogicValue visitRange(Range node) {
        // Ranges are handled elsewhere
        throw new GenerationException("Unexpected call to visitRange");
    }

    @Override
    public LogicValue visitFunctionDeclaration(FunctionDeclaration node) {
        // Do nothing - function definitions are processed by CallGraphCreator
        return NULL;
    }

    @Override
    public LogicValue visitHeapAllocation(HeapAllocation heap) {
        if (heap.hasRange()) {
            AstNode first = expressionEvaluator.evaluate(heap.getRange().getFirstValue());
            AstNode last = expressionEvaluator.evaluate(heap.getRange().getLastValue());

            if (first instanceof NumericLiteral firstLit && last instanceof NumericLiteral lastLit) {
                if (firstLit.notInteger() || lastLit.notInteger()) {
                    throw new InvalidMemoryAllocationException("Heap declarations must use integer range; received " + heap.getRange());
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (heap.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    throw new InvalidMemoryAllocationException("Empty or invalid range in heap declaration: " + heap.getRange());
                }

                heapBaseAddress = firstInt;
                heapSize = lastInt - firstInt;
            } else {
                throw new InvalidMemoryAllocationException("Heap declarations must use constant range; received " + heap.getRange());
            }
        } else {
            heapBaseAddress = 0;
            heapSize = 64;
        }
        return NULL;
    }

    @Override
    public LogicValue visitStackAllocation(StackAllocation node) {
        int start = 0;      // Start at the bottom by default
        StackAllocation stack = callGraph.getAllocatedStack();

        // Verify stack properties
        if (stack.hasRange()) {
            AstNode first = expressionEvaluator.evaluate(stack.getRange().getFirstValue());
            AstNode last  = expressionEvaluator.evaluate(stack.getRange().getLastValue());

            if (first instanceof NumericLiteral firstLit && last instanceof NumericLiteral lastLit) {
                if (firstLit.notInteger() || lastLit.notInteger()) {
                    throw new InvalidMemoryAllocationException("Stack declarations must use integer range; received " + stack.getRange());
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (stack.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    throw new InvalidMemoryAllocationException("Empty or invalid range in stack declaration: " + stack.getRange());
                }

                start = firstInt;
            } else {
                throw new InvalidMemoryAllocationException("Stack declarations must use constant range; received " + stack.getRange());
            }
        }

        // Do not initialize stack variable if no recursive functions are present
        if (callGraph.containsRecursiveFunction()) {
            emit(createSet(LogicVariable.STACK_POINTER, LogicNumber.get(start)));
        }

        return NULL;
    }

    @Override
    public LogicValue visitBreakStatement(BreakStatement node) {
        final LogicLabel label = loopStack.getBreakLabel(node.getLabel());
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitContinueStatement(ContinueStatement node) {
        final LogicLabel label = loopStack.getContinueLabel(node.getLabel());
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitReturnStatement(ReturnStatement node) {
        final LogicVariable retval = returnStack.getReturnValue();
        final LogicLabel label = returnStack.getReturnLabel();
        final LogicValue expression = visit(node.getRetval());
        emit(createSet(retval, expression));
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitControl(Control node) {
        final LogicValue target = visit(node.getTarget());
        final List<LogicValue> args = node.getParams().stream().map(this::visit).collect(Collectors.toList());
        LogicValue value = functionMapper.handleProperty(pipeline, node.getProperty(), target, args);
        if (value == null) {
            throw new UndeclaredFunctionException("Don't know how to handle property [" + target + "." + node.getProperty() + "]");
        }
        return value;
    }

    private LogicValue handlePrintf(FunctionCall node, List<LogicValue> params) {
        // Printf format string may contain references to variables, which is practically a code
        // Must be therefore handled here and not by the FunctionMapper
        
        if (params.isEmpty()) {
            throw new GenerationException("First parameter of printf() function must be a constant string value.");
        }

        AstNode astFormat = expressionEvaluator.evaluate(node.getParams().get(0));
        if (astFormat instanceof StringLiteral format) {
            return handlePrintf(format.getText(), params);
        } else {
            throw new GenerationException("First parameter of printf() function must be a constant string value.");
        }
    }

    private LogicValue handlePrintf(String format, List<LogicValue> params) {
        boolean escape = false;
        StringBuilder accumulator = new StringBuilder();
        int position = 1;       // Skipping the 1st param, which is the format string

        // Skip leading and trailing quotes
        for (int i = 0; i < format.length(); i++) {
            char ch = format.charAt(i);
            //noinspection EnhancedSwitchMigration
            switch (ch) {
                case '$':
                    if (escape) {
                        accumulator.append('$');
                        escape = false;
                        break;
                    }

                    // Found a variable or argument reference
                    // Emit accumulator
                    if (accumulator.length() > 0) {
                        emit(createPrint(LogicString.create(accumulator.toString())));
                        accumulator.setLength(0);
                    }

                    String variable = extractVariable(format.substring(i + 1));
                    if (variable.isEmpty()) {
                        // No variable, emit next argument
                        if (position < params.size()) {
                            emit(createPrint(params.get(position++)));
                        } else {
                            throw new TooFewPrintfArgumentsException("Not enough arguments for printf format string " + format);
                        }
                    } else {
                        // Going through createVariable ensures proper handling and registering of local variables
                        LogicValue eval = variable.startsWith("@")
                                ? LogicBuiltIn.create(variable.substring(1))
                                : createValue(variable);
                        emit(createPrint(eval));
                    }

                    if (i + 1 < format.length() && format.charAt(i + 1) == '{') {
                        i = format.indexOf('}', i + 2);
                    } else {
                        i += variable.length();
                    }

                    break;

                case '\\':
                    escape = true;
                    break;

                default:
                    // Escape is only used to escape $ sign, nothing else
                    if (escape) {
                        accumulator.append('\\');
                        escape = false;
                    }
                    accumulator.append(ch);
            }
        }

        if (accumulator.length() > 0) {
            emit(createPrint(LogicString.create(accumulator.toString())));
        }

        if (position < params.size()) {
            throw new TooManyPrintfArgumentsException("Too many arguments for printf format string " + format);
        }

        return NULL;
    }

    private static final Pattern REGEX_VARIABLE = Pattern.compile("^([@_a-zA-Z][-a-zA-Z_0-9]*)");
    private static final Pattern REGEX_BRACKETS = Pattern.compile("^\\{\\s*([@_a-zA-Z][-a-zA-Z_0-9]*)?\\s*}");

    private String extractVariable(String string) {
        Matcher matcher = REGEX_BRACKETS.matcher(string);
        if (matcher.find()) return matcher.group(1) == null ? "" : matcher.group(1);

        matcher = REGEX_VARIABLE.matcher(string);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static class LocalContext {
        protected final List<LogicVariable> variables = new ArrayList<>();

        LogicVariable registerVariable(LogicVariable argument) {
            if (!variables.contains(argument)) {
                variables.add(argument);
            }
            return argument;
        }

        Collection<LogicVariable> getVariables() {
            return variables;
        }
    }

    private static class NodeContext extends LocalContext {
        public NodeContext() {
        }

        public NodeContext(LocalContext parent) {
            variables.addAll(parent.variables);
        }

        /**
         * Encapsulates processing of given expression, by keeping temporary variable(s) created while evaluating
         * the expression out of current node context. Suitable when the generated temporary variables are known
         * not to be used outside the context of the expression. A good example is the condition expression of the
         * if statement: the condition is evaluated and the result is used to choose the branch to execute, but
         * all this happens before either of the branches are executed and the temporary variable holding the condition
         * value will not be used again.
         * <p>
         * Note: if x = a > b then ... else ... end; print(x) is not a problem, because x is a user variable and
         * is registered inside functionContext.
         *
         * @param <T> type of return value
         * @param expression expression to evaluate
         * @return value provided by the expression
         */
        public <T> T encapsulate(Supplier<T> expression) {
            int savepoint = variables.size();
            T result = expression.get();
            variables.subList(savepoint, variables.size()).clear();
            return result;
        }

        public void encapsulate(Runnable expression) {
            this.<Void>encapsulate(() -> { expression.run(); return null; });
        }
    }
}