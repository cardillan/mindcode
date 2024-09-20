package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.CallGraph.LogicFunction;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.mimex.Icons;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    private static final int LOOP_REPETITIONS = 25;             // Estimated number of repetitions for normal loops

    private final CompilerProfile profile;
    private final Consumer<CompilerMessage> messageConsumer;
    private final InstructionProcessor instructionProcessor;
    private final FunctionMapper functionMapper;
    private final ReturnStack returnStack = new ReturnStack();

    private LoopStack loopStack = new LoopStack();

    // Contains the information about functions built from the program to support code generation
    private CallGraph callGraph;

    private final ConstantExpressionEvaluator expressionEvaluator;

    // These instances track variables that need to be stored on stack for recursive function calls.
    
    // Constants and global variables
    // Key is the name of variable/constant
    // Value is either an LogicLiteral (for a constant), LogicVariable (for a parameter) or null (for a variable)
    // Initialized to contain icon constants
    private final Map<String, LogicValue> identifiers = Icons.createIconMap();

    // Tracks all local function variables, including function parameters - once accessed, they have to be preserved.
    private LocalContext functionContext = new LocalContext();

    // Tracks variables whose scope is limited to the node being processed and have no meaning outside the node.
    private NodeContext nodeContext = new NodeContext();

    // Tracks variables whose scope is limited to the parent node. These are variables that transfer the return value
    // of a node to its parent. When a new node is visited, nodeContext becomes parentContext and a new node context
    // is created.
    private NodeContext parentContext = new NodeContext();

    // Function definition being presently compiled (including inlined functions)
    private LogicFunction currentFunction;

    // Prefix for local variables (depends on function being processed)
    private String functionPrefix = "";

    private int heapBaseAddress = 0;
    
    private int heapSize = 0;

    private int markerIndex = 0;

    private final List<LogicInstruction> instructions = new ArrayList<>();

    private AstContext astContext;

    public LogicInstructionGenerator(CompilerProfile profile, InstructionProcessor instructionProcessor,
            Consumer<CompilerMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
        this.instructionProcessor = instructionProcessor;
        this.functionMapper = FunctionMapperFactory.getFunctionMapper( instructionProcessor,
                () -> astContext, messageConsumer);
        this.profile = profile;
        this.astContext = AstContext.createRootNode(profile);
        this.expressionEvaluator = new ConstantExpressionEvaluator(instructionProcessor);
    }

    public GeneratorOutput generate(Seq program) {
        callGraph = CallGraphCreator.createFunctionGraph(program, instructionProcessor);
        currentFunction = callGraph.getMain();
        verifyStackAllocation();
        setSubcontextType(AstSubcontextType.BODY, 1.0);
        visit(program);
        clearSubcontextType();
        appendFunctionDeclarations();
        return new GeneratorOutput(callGraph, List.copyOf(instructions), astContext);
    }

    private void emit(LogicInstruction instruction) {
        instructions.add(instruction);
    }

    private void enterAstNode(AstNode node) {
        enterAstNode(node, node.getContextType());
    }

    private void enterAstNode(AstNode node, AstContextType contextType) {
        if (node.getContextType() != AstContextType.NONE) {
            astContext = astContext.createChild(profile, node, contextType);
        }
    }

    private void enterFunctionAstNode(LogicFunction function, AstNode node, double weight) {
        astContext = astContext.createFunctionDeclaration(profile, function, node, node.getContextType(), weight);
    }

    private void exitAstNode(AstNode node) {
        if (node.getContextType() != AstContextType.NONE) {
            if (astContext.subcontextType() != node.getSubcontextType() || astContext.node() != node) {
                throw new IllegalStateException("Unexpected AST context " + astContext);
            }
            astContext = astContext.parent();
        }
    }

    private void setSubcontextType(AstSubcontextType subcontextType, double multiplier) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(subcontextType, multiplier);
    }

    private void setSubcontextType(LogicFunction function, AstSubcontextType subcontextType) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(function, subcontextType, 1.0);
    }

    private void clearSubcontextType() {
        astContext = astContext.parent();
    }
    
    // Visit is called for every node. This overridden function ensures proper transition of variable contexts between
    // parent and child nodes.
    @Override
    public LogicValue visit(AstNode node) {
        NodeContext previousParent = parentContext;
        parentContext = nodeContext;
        nodeContext = new NodeContext(parentContext);  // inherit variables from parent context
        enterAstNode(node);

        // Perform constant expression evaluation
        LogicValue visited = super.visit(expressionEvaluator.evaluate(node));

        exitAstNode(node);
        nodeContext = parentContext;
        parentContext = previousParent;
        return visited;
    }

    public LogicVariable visitVariable(AstNode node) {
        LogicValue result = visit(node);
        if (result instanceof LogicVariable variable) {
            return variable;
        }

        // Internal error
        throw new MindcodeInternalError("Expected variable, got " + result);
    }


    public CallRecInstruction createCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr, LogicVariable returnValue) {
        return instructionProcessor.createCallRecursive(astContext, stack, callAddr, retAddr, returnValue);
    }

    public EndInstruction createEnd() {
        return instructionProcessor.createEnd(astContext);
    }

    public GotoInstruction createGoto(LogicVariable address, LogicLabel marker) {
        return instructionProcessor.createGoto(astContext, address, marker);
    }

    public GotoOffsetInstruction createGotoOffset(AstContext astContext, LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        return instructionProcessor.createGotoOffset(astContext, target, value, offset, marker);
    }

    public JumpInstruction createJump(LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        return instructionProcessor.createJump(astContext, target, condition, x, y);
    }

    public JumpInstruction createJumpUnconditional(LogicLabel target) {
        return instructionProcessor.createJumpUnconditional(astContext, target);
    }

    public LabelInstruction createLabel(LogicLabel label) {
        return instructionProcessor.createLabel(astContext, label);
    }

    public GotoLabelInstruction createGotoLabel(LogicLabel label, LogicLabel marker) {
        return instructionProcessor.createGotoLabel(astContext, label, marker);
    }

    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first) {
        return instructionProcessor.createOp(astContext, operation, target, first);
    }

    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return instructionProcessor.createOp(astContext, operation, target, first, second);
    }

    public PopInstruction createPop(LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPop(astContext, memory, value);
    }

    public PrintInstruction createPrint(LogicValue what) {
        return instructionProcessor.createPrint(astContext, what);
    }

    public PrintflushInstruction createPrintflush(LogicVariable messageBlock) {
        return instructionProcessor.createPrintflush(astContext, messageBlock);
    }

    public PushInstruction createPush(LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPush(astContext, memory, value);
    }

    public ReadInstruction createRead(LogicVariable result, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createRead(astContext, result, memory, index);
    }

    public RemarkInstruction createRemark(LogicValue what) {
        return instructionProcessor.createRemark(astContext, what);
    }

    public ReturnInstruction createReturn(LogicVariable memory) {
        return instructionProcessor.createReturn(astContext, memory);
    }

    public SensorInstruction createSensor(LogicVariable result, LogicValue target, LogicValue property) {
        return instructionProcessor.createSensor(astContext, result, target, property);
    }

    public SetInstruction createSet(LogicVariable target, LogicValue value) {
        return instructionProcessor.createSet(astContext, target, value);
    }

    // TODO bind together SetAddress, Goto and GotoLabel so that use of regular label with goto is precluded
    public SetAddressInstruction createSetAddress(LogicVariable variable, LogicLabel address) {
        return instructionProcessor.createSetAddress(astContext, variable, address);
    }

    public CallInstruction createCallStackless(LogicAddress value, LogicVariable returnValue) {
        return instructionProcessor.createCallStackless(astContext, value, returnValue);
    }

    public StopInstruction createStop() {
        return instructionProcessor.createStop(astContext);
    }

    public WriteInstruction createWrite(LogicValue value, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createWrite(astContext, value, memory, index);
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
        return parentContext.registerVariable(instructionProcessor.nextTemp());
    }

    private LogicLabel nextMarker() {
        return LogicLabel.symbolic("marker" + markerIndex++);
    }

    private void verifyStackAllocation() {
        if (callGraph.containsRecursiveFunction()) {
            if (callGraph.getAllocatedStack() == null) {
                throw new MindcodeException("Cannot use recursive functions when no stack was allocated.");
            }
        }
    }

    private LogicValue queryConstantName(String name) {
        if (identifiers.get(name) != null) {
            return identifiers.get(name);
        } else {
            // Register the identifier as a variable name
            identifiers.put(name, null);
            return null;
        }
    }

    private void registerConstant(String name, ConstantAstNode value) {
        if (identifiers.get(name) != null) {
            throw new MindcodeException(value.startToken(), "multiple declarations of '%s'.", name);
        } else if (identifiers.containsKey(name)) {
            throw new MindcodeException(value.startToken(), "cannot redefine variable or function parameter '%s' as a constant.", name);
        }
        identifiers.put(name, value.toLogicLiteral(instructionProcessor));
    }

    private LogicParameter registerParameter(Parameter parameter, LogicValue logicValue) {
        final String name = parameter.getName();
        if (identifiers.get(name) != null) {
            throw new MindcodeException(parameter.startToken(), "multiple declarations of '%s'.", name);
        } else if (identifiers.containsKey(name)) {
            throw new MindcodeException(parameter.startToken(), "cannot redefine variable or function parameter '%s' as a global parameter.", name);
        }
        LogicParameter logicParameter = LogicParameter.parameter(name, logicValue);
        identifiers.put(name, logicParameter);
        return logicParameter;
    }

    private void emitEnd() {
        setSubcontextType(AstSubcontextType.END, 1.0);
        emit(createEnd());
        clearSubcontextType();
    }

    private void appendFunctionDeclarations() {
        emitEnd();

        for (LogicFunction function : callGraph.getFunctions()) {
            if (function.isInline() || !function.isUsed()) {
                continue;
            }

            enterFunctionAstNode(function, function.getDeclaration(), function.getUseCount());
            currentFunction = function;
            functionPrefix = function.getPrefix();
            functionContext = new LocalContext();
            emit(createLabel(function.getLabel()));
            returnStack.enterFunction(nextLabel(), LogicVariable.fnRetVal(functionPrefix));

            if (function.isRecursive()) {
                appendRecursiveFunctionDeclaration(function);
            } else {
                appendStacklessFunctionDeclaration(function);
            }

            emitEnd();
            exitAstNode(function.getDeclaration());

            functionPrefix = "";
            returnStack.exitFunction();
            currentFunction = callGraph.getMain();
        }

    }

    private void appendRecursiveFunctionDeclaration(LogicFunction function) {
        // Register all parameters for stack storage
        function.getParams().stream().map(this::visitVariableVarRef).forEach(functionContext::registerVariable);

        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(functionPrefix), body));
        emit(createLabel(returnStack.getReturnLabel()));
        emit(createReturn(stackName()));
    }

    private void appendStacklessFunctionDeclaration(LogicFunction function) {
        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(functionPrefix), body));
        emit(createLabel(returnStack.getReturnLabel()));
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        emit(createGoto(LogicVariable.fnRetAddr(functionPrefix), LogicLabel.symbolic(functionPrefix)));
    }

    @Override
    public LogicValue visitFunctionCall(FunctionCall node) {
        setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        final List<LogicValue> arguments = nodeContext.encapsulate(
                () -> node.getParams().stream().map(this::visit).collect(Collectors.toList()));

        setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        // Special cases
        LogicValue returnValue = switch (node.getFunctionName()) {
            case "printf"   -> handlePrintf(node, arguments);
            case "remark"   -> handleRemark(node, arguments);
            default         -> handleFunctionCall(node.getFunctionName(), arguments);
        };

        clearSubcontextType();

        return returnValue;
    }

    private LogicValue handleFunctionCall(String functionName, List<LogicValue> arguments) {
        LogicValue output = functionMapper.handleFunction(instructions::add, functionName, arguments);
        if (output != null) {
            return output;
        } else if (callGraph.containsFunction(functionName)) {
            return handleUserFunctionCall(functionName, arguments);
        } else {
            throw new MindcodeException("Undefined function '" + functionName + "'");
        }
    }

    private LogicValue handleUserFunctionCall(String functionName, List<LogicValue> arguments) {
        LogicFunction function = callGraph.getFunction(functionName);
        if (arguments.size() != function.getParamCount()) {
            throw new MindcodeException("Function '" + functionName + "': wrong number of arguments (expected "
                    + function.getParamCount() + ", found " + arguments.size() + ")");
        }

        // Switching to new function prefix -- save/restore old one
        String previousPrefix = functionPrefix;
        try {
            // Entire inline function evaluates using given prefix (different invocations use different variables).
            // For other functions, the prefix is only used to set up variables representing function parameters.
            functionPrefix = function.isInline() ? instructionProcessor.nextFunctionPrefix() : function.getPrefix();

            if (function.isInline()) {
                return handleInlineFunctionCall(function, arguments);
            } else if (!function.isRecursive()) {
                return handleStacklessFunctionCall(function, arguments);
            } else {
                return handleRecursiveFunctionCall(function, arguments);
            }
        } finally {
            functionPrefix = previousPrefix;
        }
    }

    private LogicValue handleInlineFunctionCall(LogicFunction function, List<LogicValue> paramValues) {
        // Switching to inline function context -- save/restore old one
        final LogicFunction previousFunction = currentFunction;
        final LocalContext previousContext = functionContext;
        final LoopStack previousLoopStack = loopStack;

        setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        currentFunction = function;
        functionContext = new LocalContext();
        loopStack = new LoopStack();

        emit(createLabel(nextLabel()));
        setupFunctionParameters(function, paramValues);

        // Retval gets registered in nodeContext, but we don't mind -- inline functions do not use stack
        final LogicVariable returnValue = nextReturnValue();
        final LogicLabel returnLabel = nextLabel();
        returnStack.enterFunction(returnLabel, returnValue);
        LogicValue result = visit(function.getBody());
        emit(createSet(returnValue, result));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        emit(createLabel(returnLabel));
        returnStack.exitFunction();

        loopStack = previousLoopStack;
        functionContext = previousContext;
        currentFunction = previousFunction;
        return returnValue;
    }

    private LogicValue handleStacklessFunctionCall(LogicFunction function, List<LogicValue> paramValues) {
        setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, paramValues);

        setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
        final LogicLabel returnLabel = nextLabel();
        emit(createSetAddress(LogicVariable.fnRetAddr(functionPrefix), returnLabel));
        emit(createCallStackless(function.getLabel(), LogicVariable.fnRetVal(functionPrefix)));
        // Mark position where the function must return
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        emit(createGotoLabel(returnLabel, LogicLabel.symbolic(functionPrefix)));

        return passReturnValue(function);
    }

    private List<LogicVariable> getContextVariables() {
        Set<LogicVariable> result = new LinkedHashSet<>(functionContext.getVariables());
        result.addAll(nodeContext.getVariables());
        return new ArrayList<>(result);
    }

    private LogicValue handleRecursiveFunctionCall(LogicFunction function, List<LogicValue> paramValues) {
        setSubcontextType(function, AstSubcontextType.RECURSIVE_CALL);
        boolean useStack = currentFunction.isRecursiveCall(function.getName());
        List<LogicVariable> variables = useStack ? getContextVariables() : List.of();

        if (useStack) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> emit(createPush(stackName(), v)));
        }

        setupFunctionParameters(function, paramValues);

         // Recursive function call
        final LogicLabel returnLabel = nextLabel();
        emit(createCallRecursive(stackName(), function.getLabel(), returnLabel,
                LogicVariable.fnRetVal(functionPrefix)));
        emit(createLabel(returnLabel)); // where the function must return

        if (useStack) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> emit(createPop(stackName(), v)));
        }

        return passReturnValue(function);
    }

    private void setupFunctionParameters(LogicFunction function, List<LogicValue> arguments) {
        // Make sure parameter names are formed using function name
        LogicFunction previousFunction = currentFunction;
        currentFunction = function;

        // Setup variables representing function parameters with values from this call
        List<VarRef> paramsRefs = function.getParams();
        for (int i = 0; i < arguments.size(); i++) {
            // Visiting paramRefs (which are VarRefs) creates local variables from them
            // paramValues were visited in caller's context
            VarRef varRef = paramsRefs.get(i);
            LogicVariable argument = visitVariableVarRef(varRef, "Function '" + function.getName() +
                    "': parameter name '" + varRef.getName() + "' conflicts with existing constant or global parameter.");

            emit(createSet(argument, arguments.get(i)));
        }

        currentFunction = previousFunction;
    }

    private LogicValue passReturnValue(LogicFunction function) {
        if (currentFunction.isRepeatedCall(function.getName())) {
            // Copy default return variable to new temp, for the function is called multiple times,
            // and we must not overwrite result of previous call(s) with this one
            //
            // Allocate 'return value' type temp variable for it, so that it won't be eliminated;
            // this is easier than ensuring optimizers do not eliminate normal temporary variables
            // that received return values from functions.
            final LogicVariable resultVariable = nextReturnValue();
            setSubcontextType(function, AstSubcontextType.RETURN_VALUE);
            emit(createSet(resultVariable, LogicVariable.fnRetVal(functionPrefix)));
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this function's call tree
            return LogicVariable.fnRetVal(functionPrefix);
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
                throw new MindcodeException("Allocated heap is too small! Increase the size of the allocation," +
                        " or switch to a Memory Bank to give the heap even more space.");
            }
            return LogicNumber.get(index + heapBaseAddress);
        }
    }

    @Override
    public LogicValue visitHeapAccess(HeapAccess node) {
        final LogicVariable tmp = nextNodeResult();
        final LogicValue index = resolveHeapIndex(node);
        emit(createRead(tmp, createMemoryVariable(node.getCellName()), index));
        return tmp;
    }

    @Override
    public LogicValue visitIfExpression(IfExpression node) {
        if (profile.isShortCircuitEval() && node.getCondition() instanceof BoolBinaryOp boolNode) {
            setSubcontextType(AstSubcontextType.CONDITION, 1.0);
            final LogicVariable tmp = nextNodeResult();
            final LogicLabel shortCircuitLabel = nextLabel();
            final LogicLabel finishLabel = nextLabel();
            final boolean naturalOrder = nodeContext.encapsulate(() -> processBoolBinaryOp(boolNode, shortCircuitLabel));

            setSubcontextType(AstSubcontextType.BODY, 0.5);
            final LogicValue firstBranch = visit(naturalOrder ? node.getTrueBranch() : node.getFalseBranch());
            emit(createSet(tmp, firstBranch));
            setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
            emit(createJumpUnconditional(finishLabel));

            setSubcontextType(AstSubcontextType.BODY, 0.5);
            emit(createLabel(shortCircuitLabel));
            final LogicValue secondBranch = visit(naturalOrder ? node.getFalseBranch() : node.getTrueBranch());
            emit(createSet(tmp, secondBranch));
            setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
            emit(createLabel(finishLabel));

            clearSubcontextType();
            return tmp;
        } else {
            setSubcontextType(AstSubcontextType.CONDITION, 1.0);
            final LogicValue cond = nodeContext.encapsulate(() -> visit(node.getCondition()));

            final LogicVariable tmp = nextNodeResult();
            final LogicLabel elseBranch = nextLabel();
            final LogicLabel endBranch = nextLabel();

            emit(createJump(elseBranch, Condition.EQUAL, cond, FALSE));

            setSubcontextType(AstSubcontextType.BODY, 0.5);
            final LogicValue trueBranch = visit(node.getTrueBranch());
            emit(createSet(tmp, trueBranch));
            setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
            emit(createJumpUnconditional(endBranch));
            emit(createLabel(elseBranch));

            setSubcontextType(AstSubcontextType.BODY, 0.5);
            final LogicValue falseBranch = visit(node.getFalseBranch());
            emit(createSet(tmp, falseBranch));
            setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
            emit(createLabel(endBranch));

            clearSubcontextType();
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
        if (!functionPrefix.isEmpty()) {
            throw new MindcodeException(node.startToken(), "constant declaration not allowed in user function '%s'.", node.getName());
        }
        AstNode value = expressionEvaluator.evaluate(node.getValue());
        if (value instanceof ConstantAstNode constant) {
            registerConstant(node.getName(), constant);
        } else {
            throw new MindcodeException(node.startToken(), "Constant declaration of '%s' does not use a constant expression.", node.getName());
        }
        return NULL;
    }

    @Override
    public LogicParameter visitParameter(Parameter node) {
        if (!functionPrefix.isEmpty()) {
            throw new MindcodeException(node.startToken(), "parameter declaration not allowed in user function '%s'.", node.getName());
        }
        LogicValue logicValue = extractParameterValue(node);    // throws exception if not permissible
        LogicParameter parameter = registerParameter(node, logicValue);
        emit(createSet(parameter, logicValue));
        return parameter;
    }

    public LogicValue extractParameterValue(Parameter node) {
        if (node.getValue() instanceof ConstantAstNode v){
            return v.toLogicLiteral(instructionProcessor);
        } else if (node.getValue() instanceof Ref r) {
            LogicValue value = visitRef(r);
            if (value.isConstant()) return value;
        } else if (node.getValue() instanceof VarRef r && instructionProcessor.isBlockName(r.getName())) {
            return LogicVariable.block(r.getName());
        }

        throw new MindcodeException(node.startToken(),
                "Parameter declaration of '%s' does not use a constant expression, linked block name or constant mlog variable.", node.getName());
    }

    @Override
    public LogicValue visitDirective(Directive node) {
        // Do nothing - directives are preprocessed
        return null;
    }

    @Override
    public LogicValue visitAssignment(Assignment node) {
        final LogicValue eval = visit(node.getValue());
        final LogicValue rvalue;

        // Reusing volatile variables might assign different values to each variable in chain
        if (eval.isVolatile()) {
            LogicVariable tmp = nextTemp();
            emit(createSet(tmp, eval));
            rvalue = tmp;
        } else {
            rvalue = eval;
        }

        if (node.getVar() instanceof HeapAccess heapAccess) {
            final LogicValue address = resolveHeapIndex(heapAccess);
            emit(createWrite(rvalue, createMemoryVariable(heapAccess.getCellName()), address));
        } else if (node.getVar() instanceof PropertyAccess propertyAccess) {
            LogicValue propTarget = visit(propertyAccess.getTarget());
            LogicArgument prop = visit(propertyAccess.getProperty());
            String propertyName = prop instanceof LogicBuiltIn lb ? lb.getName() : prop.toMlog();
            if (functionMapper.handleProperty(instructions::add, propertyName, propTarget, List.of(rvalue)) == null) {
                throw new MindcodeException(node.startToken(), "undefined property '%s.%s'.", propTarget, prop);
            }
        } else if (node.getVar() instanceof VarRef varRef) {
            String name = varRef.getName();
            if (identifiers.get(name) != null) {
                throw new MindcodeException(node.startToken(), "assignment to constant or parameter '%s' not allowed.", name);
            }

            final LogicValue target = visit(node.getVar());
            if (target instanceof LogicVariable variable) {
                if (target.getType() != ArgumentType.BLOCK) {
                    emit(createSet(variable, rvalue));
                    return target;
                } else {
                    throw new MindcodeException(node.startToken(), "assignment to variable '%s' not allowed (name reserved for linked blocks).", target);
                }
            } else {
                throw new MindcodeInternalError("Unsupported assignment target '" + target + "'.");
            }
        } else {
            throw new MindcodeInternalError("Unhandled assignment target in " + node);
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
        final LogicLabel marker = nextMarker();

        loopStack.enterLoop(node.getLabel(), exitLabel, contLabel);

        // All but the last value
        for (int i = 0; i < values.size() - 1; i++) {
            // Multiplier is set to 1: all instructions execute exactly once
            setSubcontextType(AstSubcontextType.ITERATOR, 1.0);
            LogicLabel nextValueLabel = nextLabel();

            // Setting the iterator first. It is possible to use continue in the value expression,
            // in which case the next iteration is performed.
            emit(createSetAddress(iterator, nextValueLabel));
            emit(createSet(variable, visit(values.get(i))));
            emit(createJumpUnconditional(bodyLabel));
            emit(createGotoLabel(nextValueLabel, marker));
        }

        // Last value
        setSubcontextType(AstSubcontextType.ITERATOR, 1.0);
        LogicLabel lastValueLabel = nextLabel();
        emit(createSetAddress(iterator, lastValueLabel));
        emit(createSet(variable, visit(values.get(values.size() - 1))));

        setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        emit(createLabel(bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        emit(createLabel(contLabel));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        emit(createGoto(iterator, marker));
        emit(createGotoLabel(lastValueLabel, marker));
        emit(createLabel(exitLabel));

        clearSubcontextType();
        loopStack.exitLoop(node.getLabel());

        return NULL;
    }

    @Override
    public LogicValue visitRangedForExpression(RangedForExpression node) {
        final AstNode lowerBound = expressionEvaluator.evaluate(node.getRange().getFirstValue());
        final AstNode upperBound = expressionEvaluator.evaluate(node.getRange().getLastValue());

        int multiplier = lowerBound instanceof LogicLiteral lowerValue && lowerValue.isNumericLiteral()
                && upperBound instanceof LogicLiteral upperValue && upperValue.isNumericLiteral()
                ? (int) (upperValue.getDoubleValue() - lowerValue.getDoubleValue()) : LOOP_REPETITIONS;

        setSubcontextType(AstSubcontextType.INIT, multiplier);
        final LogicVariable variable = visitVariable(node.getVariable());

        // Encapsulate both: they're evaluated just here and not propagated anywhere
        final LogicValue lowerValue = nodeContext.encapsulate(() -> visit(node.getRange().getFirstValue()));
        final LogicValue upperValue = nodeContext.encapsulate(() -> visit(node.getRange().getLastValue()));

        LogicValue fixedUpperBound;
        if (upperValue.isLiteral()) {
            fixedUpperBound = upperValue;
        } else {
            LogicVariable tmp = nextTemp();
            emit(createSet(tmp, upperValue));
            fixedUpperBound = tmp;
        }

        emit(createSet(variable, lowerValue));

        final LogicLabel beginLabel = nextLabel();
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        setSubcontextType(AstSubcontextType.CONDITION, multiplier);
        emit(createLabel(beginLabel));
        emit(createJump(doneLabel, node.getRange().maxValueComparison().inverse(), variable, fixedUpperBound));
        setSubcontextType(AstSubcontextType.BODY, multiplier);
        visit(node.getBody());
        emit(createLabel(continueLabel));
        setSubcontextType(AstSubcontextType.UPDATE, multiplier);
        emit(createOp(Operation.ADD, variable, variable, LogicNumber.ONE));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        emit(createJumpUnconditional(beginLabel));
        emit(createLabel(doneLabel));
        clearSubcontextType();
        loopStack.exitLoop(node.getLabel());
        return NULL;
    }

    @Override
    public LogicValue visitWhileStatement(WhileExpression node) {
        setSubcontextType(AstSubcontextType.INIT, LOOP_REPETITIONS);
        visit(node.getInitialization());

        final LogicLabel beginLabel = nextLabel();
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        emit(createLabel(beginLabel));
        final LogicValue cond = visit(node.getCondition());
        emit(createJump(doneLabel, Condition.EQUAL, cond, FALSE));
        setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visit(node.getBody());
        emit(createLabel(continueLabel));
        setSubcontextType(AstSubcontextType.UPDATE, LOOP_REPETITIONS);
        visit(node.getUpdate());
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        emit(createJumpUnconditional(beginLabel));
        emit(createLabel(doneLabel));
        loopStack.exitLoop(node.getLabel());
        clearSubcontextType();
        return NULL;
    }

    @Override
    public LogicValue visitDoWhileStatement(DoWhileExpression node) {
        final LogicLabel beginLabel = nextLabel();
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        emit(createLabel(beginLabel));
        visit(node.getBody());
        emit(createLabel(continueLabel));
        setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        final LogicValue cond = visit(node.getCondition());
        emit(createJump(beginLabel, Condition.NOT_EQUAL, cond, FALSE));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        emit(createLabel(doneLabel));
        clearSubcontextType();
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
            default -> throw new MindcodeInternalError("Unhandled BoolBinaryOp operation in " + node);
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
        // If the name refers to a constant/parameter, use it.
        // If it wasn't a constant already, the name will be reserved for a variable
        LogicValue constant = queryConstantName(identifier);
        if (constant != null) {
            if (requireVariable) {
                throw new MindcodeException(errorMessage != null ? errorMessage
                        : '\'' + identifier + "' is a constant or parameter; a variable is expected here.");
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
        } else if (functionPrefix.isEmpty()) {
            // Main variable - again not pushed onto stack
            return LogicVariable.main(identifier);
        } else {
            // A truly local variable
            return functionContext.registerVariable(
                    LogicVariable.local(currentFunction.getName(), functionPrefix, identifier));
        }
    }

    // A variable is required here
    private LogicVariable createMemoryVariable(String identifier) {
        LogicValue constant = queryConstantName(identifier);
        if (constant != null) {
            if (constant instanceof LogicParameter p && p.getValue().getType() == ArgumentType.BLOCK) {
                return p;
            } else {
                throw new MindcodeException("'" + identifier + "' cannot be used for external memory access.");
            }
        }

        return (LogicVariable) createVariableOrConstant(identifier, true, null);
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
        throw new MindcodeInternalError("Unexpected call to visitNumericValue");
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

        final double multiplier = 1.0 / node.getAlternatives().size();
        int remain = node.getAlternatives().size();
        setSubcontextType(AstSubcontextType.INIT, 1.0);
        final LogicValue caseValue = visit(node.getCondition());
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final LogicLabel nextAlt = nextLabel();         // Next alternative
            final LogicLabel bodyLabel = nextLabel();       // Body of this alternative
            final double branchMultiplier = multiplier * remain--;

            nodeContext.encapsulate(() -> {
                // Each matching value, including the last one, causes a jump to the "when" body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstNode value : alternative.getValues()) {
                    if (value instanceof Range range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        setSubcontextType(AstSubcontextType.CONDITION, branchMultiplier);
                        LogicLabel nextExp = nextLabel();       // Next value in when list
                        final LogicValue minValue = visit(range.getFirstValue());
                        emit(createJump(nextExp, Condition.LESS_THAN, caseValue, minValue));
                        // The max value is only evaluated when the min value lets us through
                        setSubcontextType(AstSubcontextType.CONDITION, branchMultiplier);
                        final LogicValue maxValue = visit(range.getLastValue());
                        emit(createJump(bodyLabel, range.maxValueComparison(), caseValue, maxValue));
                        emit(createLabel(nextExp));
                    }
                    else {
                        setSubcontextType(AstSubcontextType.CONDITION, branchMultiplier);
                        final LogicValue whenValue = visit(value);
                        emit(createJump(bodyLabel, Condition.EQUAL, caseValue, whenValue));
                    }
                }
            });

            // No match in the "when" value list: skip to the next alternative
            emit(createJumpUnconditional(nextAlt));

            // Body of the alternative
            setSubcontextType(AstSubcontextType.BODY, multiplier);
            emit(createLabel(bodyLabel));
            final LogicValue body = visit(alternative.getBody());
            emit(createSet(resultVar, body));
            setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
            emit(createJumpUnconditional(exitLabel));
            emit(createLabel(nextAlt));
            clearSubcontextType();
        }

        setSubcontextType(AstSubcontextType.ELSE, multiplier);
        final LogicValue elseBranch = visit(node.getElseBranch());
        emit(createSet(resultVar, elseBranch));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        emit(createLabel(exitLabel));
        clearSubcontextType();

        return resultVar;
    }

    @Override
    public LogicValue visitCaseAlternative(CaseAlternative node) {
        // Case alternatives are handled in visitCaseExpression
        throw new MindcodeInternalError("Unexpected call to visitCaseAlternative");
    }

    @Override
    public LogicValue visitRange(Range node) {
        // Ranges are handled elsewhere
        throw new MindcodeInternalError("Unexpected call to visitRange");
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
                    throw new MindcodeException(heap.startToken(), "heap declarations must use integer range.");
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (heap.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    throw new MindcodeException(heap.startToken(), "empty or invalid range in heap declaration.");
                }

                heapBaseAddress = firstInt;
                heapSize = lastInt - firstInt;
            } else {
                throw new MindcodeException(heap.startToken(), "heap declarations must use constant range.");
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
                    throw new MindcodeException(stack.startToken(), "stack declarations must use integer range.");
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (stack.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    throw new MindcodeException(stack.startToken(), "empty or invalid range in stack declaration.");
                }

                start = firstInt;
            } else {
                throw new MindcodeException(stack.startToken(), "stack declarations must use constant range.");
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
        LogicValue value = functionMapper.handleProperty(instructions::add, node.getProperty(), target, args);
        if (value == null) {
            throw new MindcodeException(node.startToken(), "undefined property '%s.%s'.", target, node.getProperty());
        }
        return value;
    }

    enum Formatter {
        PRINTF("printf", LogicInstructionGenerator::createPrint),
        REMARK("remark", LogicInstructionGenerator::createRemark);

        final String function;
        final BiFunction<LogicInstructionGenerator, LogicValue, LogicInstruction> creator;

        Formatter(String function, BiFunction<LogicInstructionGenerator, LogicValue, LogicInstruction> creator) {
            this.function = function;
            this.creator = creator;
        }

        LogicInstruction createInstruction(LogicInstructionGenerator generator, LogicValue value) {
            return creator.apply(generator, value);
        }
    }

    private LogicValue handlePrintf(FunctionCall node, List<LogicValue> params) {
        return handleFormattedOutput(Formatter.PRINTF, node, params);
    }

    private LogicValue handleRemark(FunctionCall node, List<LogicValue> params) {
        return handleFormattedOutput(Formatter.REMARK, node, params);
    }

    private LogicValue handleFormattedOutput(Formatter formatter, FunctionCall node, List<LogicValue> params) {
        // Printf format string may contain references to variables, which is practically a code
        // Must be therefore handled here and not by the FunctionMapper

        if (params.isEmpty()) {
            throw new MindcodeException(node.startToken(), "first parameter of %s() function must be a constant string value.", formatter.function);
        }

        AstNode astFormat = expressionEvaluator.evaluate(node.getParams().get(0));
        if (astFormat instanceof StringLiteral format) {
            return handleFormattedOutput(formatter, node, format.getText(), params);
        } else {
            throw new MindcodeException(node.startToken(), "first parameter of %s() function must be a constant string value.", formatter.function);
        }
    }

    private LogicValue handleFormattedOutput(Formatter formatter, FunctionCall node, String format, List<LogicValue> params) {
        boolean escape = false;
        StringBuilder accumulator = new StringBuilder();
        int position = 1;       // Skipping the 1st param, which is the format string

        // Skip leading and trailing quotes
        for (int i = 0; i < format.length(); i++) {
            char ch = format.charAt(i);
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
                        emit(formatter.createInstruction(this, LogicString.create(accumulator.toString())));
                        accumulator.setLength(0);
                    }

                    String variable = extractVariable(format.substring(i + 1));
                    if (variable.isEmpty()) {
                        // No variable, emit next argument
                        if (position < params.size()) {
                            emit(formatter.createInstruction(this,params.get(position++)));
                        } else {
                            throw new MindcodeException(node.startToken(), "not enough arguments for %s() format string.", formatter.function);
                        }
                    } else {
                        // Going through createValue ensures proper handling and registering of local variables
                        LogicValue eval = variable.startsWith("@")
                                ? LogicBuiltIn.create(variable.substring(1))
                                : createValue(variable);
                        emit(formatter.createInstruction(this,eval));
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
            emit(formatter.createInstruction(this,LogicString.create(accumulator.toString())));
        }

        if (position < params.size()) {
            throw new MindcodeException(node.startToken(), "too many arguments for %s() format string.", formatter.function);
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
