package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.Iterator;
import info.teksol.mindcode.ast.*;
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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final InstructionProcessor instructionProcessor;
    private final FunctionMapper functionMapper;
    private final ReturnStack returnStack;
    private LoopStack loopStack;

    // Contains the information about functions built from the program to support code generation
    private CallGraph callGraph;

    private final ConstantExpressionEvaluator expressionEvaluator;

    // These instances track variables that need to be stored on stack for recursive function calls.

    // Constants and global variables
    // Key is the name of variable/constant
    // Value is either an LogicLiteral (for a constant), LogicVariable (for a parameter) or null (for a variable)
    // Initialized to contain icon constants
    // TODO move to a separate class
    private final Map<String, LogicValue> identifiers = Icons.createIconMap();
    private final Map<String, FormattableLiteral> formattables = new HashMap<>();

    // Tracks all local function variables, including function parameters - once accessed, they have to be preserved.
    private LocalContext functionContext = new LocalContext();

    // Tracks variables whose scope is limited to the node being processed and have no meaning outside the node.
    private NodeContext nodeContext = new NodeContext(null);

    // Tracks variables whose scope is limited to the parent node. These are variables that transfer the return value
    // of a node to its parent. When a new node is visited, nodeContext becomes parentContext and a new node context
    // is created.
    private NodeContext parentContext = new NodeContext(null);

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
            Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        this.returnStack = new ReturnStack(messageConsumer);
        this.loopStack = new LoopStack(messageConsumer);
        this.instructionProcessor = instructionProcessor;
        this.functionMapper = FunctionMapperFactory.getFunctionMapper(instructionProcessor,
                () -> astContext, messageConsumer);
        this.profile = profile;
        this.astContext = AstContext.createRootNode(profile);
        this.expressionEvaluator = new ConstantExpressionEvaluator(instructionProcessor, messageConsumer);
    }

    public GeneratorOutput generate(Seq program) {
        callGraph = CallGraphCreator.createFunctionGraph(program, messageConsumer, instructionProcessor);
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
        nodeContext = new NodeContext(node, parentContext);  // inherit variables from parent context
        enterAstNode(node);

        // Perform constant expression evaluation
        LogicValue visited = super.visit(expressionEvaluator.evaluate(node));

        exitAstNode(node);
        nodeContext = parentContext;
        parentContext = previousParent;
        return visited;
    }

    @Override
    public LogicValue visitIterator(Iterator node) {
        // Do nothing - iterators are processed by visitForEachStatement
        return NULL;
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

    public FormatInstruction createFormat(LogicValue what) {
        return instructionProcessor.createFormat(astContext, what);
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
        if (callGraph.getAllocatedStack() == null) {
            callGraph.recursiveFunctions().forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }
    }

    private final Set<String> checked = new HashSet<>();

    private void generateDeprecationWarnings(AstNode node, String name) {
        if ("configure".equals(name)) {
            error(node, "'%s' is not allowed as a variable name.", name);
        } else if (checked.add(name)) {
            if (name.contains("-")) {
                warn(node, "Identifier '%s': kebab-case identifiers are deprecated.", name);
            }
        }
    }

    private LogicValue queryConstantName(AstNode node, String name) {
        generateDeprecationWarnings(node, name);

        if (formattables.containsKey(name)) {
            error(node, "Constant '%s' represents a formattable string literal. It can only be used with print() functions.", name);
            return null;
        } else if (identifiers.get(name) != null) {
            return identifiers.get(name);
        } else {
            // Register the identifier as a variable name
            identifiers.put(name, null);
            return null;
        }
    }

    private void registerConstant(String name, ConstantAstNode value) {
        if (identifiers.get(name) != null || formattables.containsKey(name)) {
            error(value, "Multiple declarations of '%s'.", name);
        } else if (identifiers.containsKey(name)) {
            error(value, "Cannot redefine variable or function parameter '%s' as a constant.", name);
        } else if (value instanceof FormattableLiteral fmt) {
            formattables.put(name, fmt);
        } else {
            try {
                identifiers.put(name, value.toLogicLiteral(instructionProcessor));
            } catch (NumericLiteral.NoValidMlogRepresentationException ex) {
                error(value, "%s", ex.getMessage());
                identifiers.put(name, LogicNumber.ZERO);
            }
        }
    }

    private LogicParameter registerParameter(ProgramParameter programParameter, LogicValue logicValue) {
        final String name = programParameter.getName();
        generateDeprecationWarnings(programParameter, name);
        if (identifiers.get(name) != null || formattables.containsKey(name)) {
            error(programParameter, "Multiple declarations of '%s'.", name);
        } else if (identifiers.containsKey(name)) {
            error(programParameter, "Cannot redefine variable or function parameter '%s' as a program parameter.", name);
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
            returnStack.enterFunction(nextLabel(), LogicVariable.fnRetVal(function));

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
        // Register all input parameters for stack storage
        function.getParameters().forEach(functionContext::registerVariable);

        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(function), body));
        emit(createLabel(returnStack.getReturnLabel(function.getInputPosition())));
        emit(createReturn(stackName()));
    }

    private void appendStacklessFunctionDeclaration(LogicFunction function) {
        // Function parameters and return address are set up at the call site
        final LogicValue body = visit(function.getBody());
        emit(createSet(LogicVariable.fnRetVal(function), body));
        emit(createLabel(returnStack.getReturnLabel(function.getInputPosition())));
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        emit(createGoto(LogicVariable.fnRetAddr(functionPrefix), LogicLabel.symbolic(functionPrefix)));
    }

    @Override
    public LogicValue visitFunctionArgument(FunctionArgument node) {
        return NULL;
    }

    @Override
    public LogicValue visitFunctionCall(FunctionCall call) {
        // Solve special cases
        return switch (call.getFunctionName()) {
            case "min", "max"   -> handleMinMax(call);
            case "printf"       -> handlePrintf(call);
            case "print"        -> handleFormattedOutput(call, Formatter.PRINT);
            case "println"      -> handleFormattedOutput(call, Formatter.PRINTLN);
            case "remark"       -> handleFormattedOutput(call, Formatter.REMARK);
            default             -> handleFunctionCall(call);
        };
    }

    private void validateStandardFunctionArguments(List<FunctionArgument> declaredArguments) {
        for (FunctionArgument argument : declaredArguments) {
            if (!argument.hasExpression()) {
                error(argument, "Parameter corresponding to this argument isn't optional, a value must be provided.");
            }
            if (argument.hasOutModifier()) {
                error(argument, "Parameter corresponding to this argument isn't output, 'out' modifier cannot be used.");
            }
        }
    }

    private LogicFunctionArgument process(FunctionArgument argument) {
        return argument.hasExpression()
                ? new LogicFunctionArgument(argument, visit(argument.getExpression()))
                : new LogicFunctionArgument(argument);
    }

    private List<LogicFunctionArgument> processArguments(List<FunctionArgument> declaredArguments) {
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        return nodeContext.encapsulate(() -> declaredArguments.stream().map(this::process).toList());
    }

    private LogicValue handleFunctionCall(FunctionCall call) {
        String functionName = call.getFunctionName();
        setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<LogicFunctionArgument> arguments = processArguments(call.getArguments());

        setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        LogicValue output = functionMapper.handleFunction(call, instructions::add, functionName, arguments);

        if (output == null) {
            if (callGraph.containsFunction(functionName)) {
                output = handleUserFunctionCall(call, arguments);
            } else {
                error(call, "Undefined function '%s'", functionName);
                output = NULL;
            }
        }

        clearSubcontextType();
        return output;
    }

    private void validateUserFunctionArguments(LogicFunction function, List<FunctionArgument> arguments) {
        if (function.getDeclaredParameters().size() < arguments.size()) {
            // This needs to be checked beforehand
            throw new MindcodeInternalError("Argument size mismatch.");
        }

        for (int i = 0; i < arguments.size(); i++) {
            FunctionParameter parameter = function.getDeclaredParameters().get(i);
            FunctionArgument argument = arguments.get(i);

            if (!parameter.isOptional() && !argument.hasExpression()) {
                error(argument, "Parameter '%s' isn't optional, a value must be provided.", parameter.getName());
            }

            if (parameter.isOutput()) {
                if (parameter.isInput()) {
                    // In or out modifier needs to be used
                    if (!argument.hasModifier()) {
                        warn(argument, "Parameter '%s' is declared 'in out' and no 'in' or 'out' argument modifier was used, assuming 'in out'.", parameter.getName());
                    }
                } else {
                    // Output parameter: the 'in' modifier is forbidden
                    if (argument.hasInModifier()) {
                        error(argument, "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.getName());
                    } else if (argument.hasExpression() && !argument.hasOutModifier()) {
                        // Out modifier needs to be used
                        warn(argument, "Parameter '%s' is output and 'out' modifier was not used, assuming 'out'.", parameter.getName());
                    }
                }
            } else {
                // Input parameter: the 'out' modifier is forbidden
                if (argument.hasOutModifier()) {
                    error(argument, "Parameter '%s' isn't output, 'out' modifier not allowed.", parameter.getName());
                }
            }
        }
    }

    private List<LogicFunctionArgument> addOptionalArguments(LogicFunction function, List<LogicFunctionArgument> arguments) {
        List<FunctionParameter> parameters = function.getDeclaredParameters();
        if (arguments.size() >= parameters.size()) {
            return arguments;
        }

        List<LogicFunctionArgument> result = new ArrayList<>(arguments);
        while (result.size() < parameters.size()) {
            if (parameters.get(result.size()).isOptional()) {
                result.add(new LogicFunctionArgument(null, null, false, false));
            } else {
                break;
            }
        }

        return result;
    }

    private LogicValue handleUserFunctionCall(FunctionCall call, List<LogicFunctionArgument> callArguments) {
        String functionName = call.getFunctionName();
        LogicFunction function = callGraph.getFunction(functionName);

        List<LogicFunctionArgument> arguments = addOptionalArguments(function, callArguments);
        if (arguments.size() != function.getParameterCount()) {
            error(call, "Function '%s': wrong number of arguments (expected %d, found %d).",
                    functionName, function.getParameterCount(), arguments.size());
            return NULL;
        }

        validateUserFunctionArguments(function, call.getArguments());

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

    private LogicValue handleInlineFunctionCall(LogicFunction function, List<LogicFunctionArgument> arguments) {
        // Switching to inline function context -- save/restore old one
        final LogicFunction previousFunction = currentFunction;
        final LocalContext previousContext = functionContext;
        final LoopStack previousLoopStack = loopStack;

        setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        currentFunction = function;
        functionContext = new LocalContext();
        loopStack = new LoopStack(messageConsumer);

        emit(createLabel(nextLabel()));
        setupFunctionParameters(function, arguments);

        // Retval gets registered in nodeContext, but we don't mind -- inline functions do not use stack
        final LogicVariable returnValue = nextReturnValue();
        final LogicLabel returnLabel = nextLabel();
        returnStack.enterFunction(returnLabel, returnValue);
        LogicValue result = visit(function.getBody());
        emit(createSet(returnValue, result));
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        emit(createLabel(returnLabel));
        returnStack.exitFunction();

        retrieveFunctionParameters(function, arguments, false);
        loopStack = previousLoopStack;
        functionContext = previousContext;
        currentFunction = previousFunction;
        return returnValue;
    }

    private LogicValue handleStacklessFunctionCall(LogicFunction function, List<LogicFunctionArgument> arguments) {
        setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, arguments);

        setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
        final LogicLabel returnLabel = nextLabel();
        emit(createSetAddress(LogicVariable.fnRetAddr(functionPrefix), returnLabel));
        emit(createCallStackless(function.getLabel(), LogicVariable.fnRetVal(function)));
        // Mark position where the function must return
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        emit(createGotoLabel(returnLabel, LogicLabel.symbolic(functionPrefix)));

        retrieveFunctionParameters(function, arguments, false);
        return passReturnValue(function);
    }

    private List<LogicFunctionArgument> substituteArguments(LogicFunction function, List<LogicFunctionArgument> arguments) {
        // Filter variables representing input parameters
        Predicate<LogicFunctionArgument> predicate = a -> a.value() instanceof LogicVariable variable && function.isInputFunctionParameter(variable);

        long count = IntStream.range(0, arguments.size())
                .filter(i -> predicate.test(arguments.get(i)) && !function.getParameters().get(i).equals(arguments.get(i).value()))
                .count();

        // One reassignment is ok
        if (count > 1) {
            List<LogicFunctionArgument> result = new ArrayList<>(arguments);
            for (int i = 0; i < result.size(); i++) {
                // Handle arguments if assigned to a different argument
                LogicFunctionArgument argument = result.get(i);
                if (predicate.test(argument) && !function.getParameters().get(i).equals(argument.value())) {
                    LogicVariable temp = nextTemp();
                    emit(createSet(temp, argument.value()));
                    result.set(i, new LogicFunctionArgument(null, temp, false, false));
                }
            }
            return result;
        } else {
            return arguments;
        }
    }

    private LogicValue handleRecursiveFunctionCall(LogicFunction function, List<LogicFunctionArgument> arguments) {
        setSubcontextType(function, AstSubcontextType.RECURSIVE_CALL);
        boolean recursiveCall = currentFunction.isRecursiveCall(function.getName());
        List<LogicVariable> variables = recursiveCall
                ? getContextVariables().stream().filter(function::isNotOutputFunctionParameter)
                        .collect(Collectors.toCollection(ArrayList::new))
                : List.of();

        if (recursiveCall) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> emit(createPush(stackName(), v)));
        }

        setupFunctionParameters(function, substituteArguments(function, arguments));

         // Recursive function call
        final LogicLabel returnLabel = nextLabel();
        emit(createCallRecursive(stackName(), function.getLabel(), returnLabel, LogicVariable.fnRetVal(function)));
        emit(createLabel(returnLabel)); // where the function must return

        retrieveFunctionParameters(function, arguments, recursiveCall);

        if (recursiveCall) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> emit(createPop(stackName(), v)));
        }

        return passReturnValue(function);
    }

    private List<LogicVariable> getContextVariables() {
        Set<LogicVariable> result = new LinkedHashSet<>(functionContext.getVariables());
        result.addAll(nodeContext.getVariables());
        return new ArrayList<>(result);
    }

    private void setupFunctionParameters(LogicFunction function, List<LogicFunctionArgument> arguments) {
        // Make sure parameter names are formed using function name
        LogicFunction previousFunction = currentFunction;
        currentFunction = function;

        // Setup variables representing function parameters with values from this call
        List<FunctionParameter> parameters = function.getDeclaredParameters();
        for (int i = 0; i < arguments.size(); i++) {
            // Visiting paramRefs (which are VarRefs) creates local variables from them
            // paramValues were visited in caller's context
            FunctionParameter declaredParameter = parameters.get(i);
            LogicVariable parameter = convertFunctionParameter(declaredParameter, "Function '" + function.getName() +
                    "': parameter name '" + declaredParameter.getName() + "' conflicts with existing constant or global parameter.");

            if (declaredParameter.isInput()) {
                emit(createSet(parameter, arguments.get(i).value()));
            }
        }

        currentFunction = previousFunction;
    }

    private void retrieveFunctionParameters(LogicFunction function, List<LogicFunctionArgument> arguments, boolean recursiveCall) {
        // Filter variables representing input parameters
        Predicate<LogicFunctionArgument> predicate = a -> a.value() instanceof LogicVariable variable && function.isOutputFunctionParameter(variable);

        long count = recursiveCall
                ? IntStream.range(0, arguments.size())
                .filter(i -> predicate.test(arguments.get(i)) && !function.getParameters().get(i).equals(arguments.get(i).value()))
                .count()
                : 0;

        // Make sure parameter names are formed using function name
        LogicFunction previousFunction = currentFunction;
        currentFunction = function;

        List<LogicInstruction> finalizers = new ArrayList<>();

        // Setup variables representing function parameters with values from this call
        List<FunctionParameter> parameters = function.getDeclaredParameters();
        for (int i = 0; i < arguments.size(); i++) {
            // Visiting paramRefs (which are VarRefs) creates local variables from them
            // paramValues were visited in caller's context
            FunctionParameter declaredParameter = parameters.get(i);
            if (declaredParameter.isOutput()) {
                LogicVariable parameter = convertFunctionParameter(declaredParameter, "Function '" + function.getName() +
                        "': parameter name '" + declaredParameter.getName() + "' conflicts with existing constant or global parameter.");
                LogicFunctionArgument argument = arguments.get(i);

                if (argument.hasValue() && !argument.hasInModifierOnly()) {
                    if (argument.value() instanceof LogicVariable target && target.isUserWritable()) {
                        if (count > 1 && predicate.test(argument) && !function.getParameters().get(i).equals(argument.value())) {
                            LogicVariable temp = nextTemp();
                            emit(createSet(temp, parameter));
                            finalizers.add(createSet(target, temp));
                        } else {
                            emit(createSet(target, parameter));
                        }
                    } else {
                        error(argument.pos(),
                                "Argument assigned to output parameter '%s' is not writable.", declaredParameter.getName());
                    }
                }
            }
        }

        finalizers.forEach(this::emit);

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
            // TODO try using normal temp to see what happens
            final LogicVariable resultVariable = nextReturnValue();
            setSubcontextType(function, AstSubcontextType.RETURN_VALUE);
            emit(createSet(resultVariable, LogicVariable.fnRetVal(function)));
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this function's call tree
            return LogicVariable.fnRetVal(function);
        }
    }

    private LogicVariable stackName() {
        return callGraph.getAllocatedStack() != null
                ? callGraph.getAllocatedStack().getStack()
                : LogicVariable.special("invalid");
    }

    private LogicValue resolveHeapIndex(HeapAccess node) {
        if (node.isAbsolute()) {
            return visit(node.getAddress());
        } else {
            int index = ((NumericLiteral)node.getAddress()).getAsInteger();
            if (index >= heapSize) {
                error(node, "Allocated heap is too small! Increase the size of the allocation," +
                        " or switch to a Memory Bank to give the heap even more space.");
            }
            return LogicNumber.get(index + heapBaseAddress);
        }
    }

    @Override
    public LogicValue visitHeapAccess(HeapAccess node) {
        final LogicVariable tmp = nextNodeResult();
        final LogicValue index = resolveHeapIndex(node);
        emit(createRead(tmp, createMemoryVariable(node, node.getCellName()), index));
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
            error(node, "Constant declaration not allowed in user function '%s'.", node.getName());
        } else {
            AstNode value = expressionEvaluator.evaluate(node.getValue());
            if (value instanceof ConstantAstNode constant) {
                registerConstant(node.getName(), constant);
            } else {
                error(node, "Constant declaration of '%s' does not use a constant expression.", node.getName());
            }
        }

        return NULL;
    }

    @Override
    public LogicParameter visitParameter(ProgramParameter node) {
        if (!functionPrefix.isEmpty()) {
            error(node, "Parameter declaration not allowed in user function '%s'.", node.getName());
        }
        LogicValue logicValue = extractParameterValue(node);
        LogicParameter parameter = registerParameter(node, logicValue);
        emit(createSet(parameter, logicValue));
        return parameter;
    }

    public LogicValue extractParameterValue(ProgramParameter node) {
        if (node.getValue() instanceof ConstantAstNode v){
            try {
                return v.toLogicLiteral(instructionProcessor);
            } catch (NumericLiteral.NoValidMlogRepresentationException ex) {
                error(node, "%s", ex.getMessage());
                return LogicNumber.ZERO;
            }
        } else if (node.getValue() instanceof Ref r) {
            LogicValue value = visitRef(r);
            if (value.isConstant()) return value;
        } else if (node.getValue() instanceof VarRef r && instructionProcessor.isBlockName(r.getName())) {
            return LogicVariable.block(r.getName());
        }

        error(node,"Parameter declaration of '%s' does not use a literal value, linked block name or constant built-in variable.",
                node.getName());
        return NULL;
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
            emit(createWrite(rvalue, createMemoryVariable(heapAccess, heapAccess.getCellName()), address));
        } else if (node.getVar() instanceof PropertyAccess propertyAccess) {
            LogicValue propTarget = visit(propertyAccess.getTarget());
            LogicArgument prop = visit(propertyAccess.getProperty());
            String propertyName = prop instanceof LogicBuiltIn lb ? lb.getName() : prop.toMlog();
            // Implicit out modifier as this is an assignment
            LogicFunctionArgument argument = new LogicFunctionArgument(node.getValue().getInputPosition(), rvalue, false, false);
            if (functionMapper.handleProperty(node, instructions::add, propertyName, propTarget, List.of(argument)) == null) {
                error(node, "Undefined property '%s.%s'.", propTarget, prop);
                return NULL;
            }
        } else if (node.getVar() instanceof VarRef varRef) {
            String name = varRef.getName();
            if (identifiers.get(name) != null || formattables.containsKey(name)) {
                error(node, "Assignment to constant or parameter '%s' not allowed.", name);
                return NULL;
            }

            final LogicValue target = visit(node.getVar());
            if (target instanceof LogicVariable variable) {
                if (target.getType() != ArgumentType.BLOCK) {
                    emit(createSet(variable, rvalue));
                    return target;
                } else {
                    error(node, "Assignment to variable '%s' not allowed (name reserved for linked blocks).", variable.getName());
                    return NULL;
                }
            } else {
                throw new MindcodeInternalError("Unsupported assignment target '%s'.", name);
            }
        } else if (node.getVar() instanceof Ref r) {
            error(node, "Assignment to built-in variable '@%s' not allowed.", r.getName());
        } else {
            throw new MindcodeInternalError("Unhandled assignment target in %s.", node);
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

    private record ForEachIterator(boolean in, boolean out, LogicVariable var) { }

    private ForEachIterator processIterator(AstNode node) {
        if (node instanceof Iterator it) {
            return new ForEachIterator(it.isInModifier(), it.isOutModifier(), visitVariable(it.getVarRef()));
        } else {
            throw new MindcodeInternalError("Expected Iterator but got %s", node);
        }
    }

    @Override
    public LogicValue visitForEachStatement(ForEachExpression node) {
        List<AstNode> values = node.getValues();
        if (values.isEmpty()) {
            // Empty list -- nothing to do
            return NULL;
        }

        final List<ForEachIterator> iterators = node.getIterators().stream().map(this::processIterator).toList();
        final LogicLabel contLabel = nextLabel();
        final LogicLabel bodyLabel = nextLabel();
        final LogicLabel exitLabel = nextLabel();
        final LogicVariable nextAddr = nextTemp();      // Holds instruction address of the next iteration
        final LogicLabel marker = nextMarker();
        final List<LogicVariable> outValues = new ArrayList<>();

        if (values.size() % iterators.size() != 0) {
            error(node, "The number of values in the list must be an integer multiple of the number of iterators.");
            return NULL;
        }

        loopStack.enterLoop(node.getInputPosition(), node.getLabel(), exitLabel, contLabel);

        // All but the last value
        int limit = values.size() - iterators.size();
        int index = 0;
        while (index < limit) {
            // Multiplier is set to 1: all instructions execute exactly once
            setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);
            LogicLabel nextValueLabel = nextLabel();

            // Setting the iterator first. It is possible to use continue in the value expression,
            // in which case the next iteration is performed.
            emit(createSetAddress(nextAddr, nextValueLabel));
            outValues.clear();
            for (ForEachIterator iterator : iterators) {
                LogicValue element = visit(values.get(index));
                if (iterator.out) {
                    if (element instanceof LogicVariable var && var.isUserWritable()) {
                        outValues.add(var);
                    } else {
                        error(values.get(index), "Element assigned to '%s' is not writable.",
                                iterator.var.getFullName());
                        outValues.add(LogicVariable.special("invalid"));
                    }
                }
                if (iterator.in) {
                    emit(createSet(iterator.var, element));
                }
                index++;
            }
            emit(createJumpUnconditional(bodyLabel));
            setSubcontextType(AstSubcontextType.ITR_TRAILING, 1.0);
            emit(createGotoLabel(nextValueLabel, marker));
            int outIndex = 0;
            for (ForEachIterator iterator : iterators) {
                if (iterator.out) {
                    emit(createSet(outValues.get(outIndex++), iterator.var));
                }
            }
        }

        // Last value
        setSubcontextType(AstSubcontextType.ITR_LEADING, 1.0);
        LogicLabel lastValueLabel = nextLabel();
        emit(createSetAddress(nextAddr, lastValueLabel));
        outValues.clear();
        for (ForEachIterator iterator : iterators) {
            LogicValue element = visit(values.get(index));
            if (iterator.out) {
                if (element instanceof LogicVariable var && var.isUserWritable()) {
                    outValues.add(var);
                } else {
                    error(values.get(index), "Element assigned to '%s' is not writable.", iterator.var.getFullName());
                    outValues.add(LogicVariable.special("invalid"));
                }
            }
            if (iterator.in) {
                emit(createSet(iterator.var, element));
            }
            index++;
        }

        setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        emit(createLabel(bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        emit(createLabel(contLabel));

        setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        emit(createGoto(nextAddr, marker));

        setSubcontextType(AstSubcontextType.ITR_TRAILING, 1.0);
        emit(createGotoLabel(lastValueLabel, marker));
        int outIndex = 0;
        for (ForEachIterator iterator : iterators) {
            if (iterator.out) {
                emit(createSet(outValues.get(outIndex++), iterator.var));
            }
        }
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
        loopStack.enterLoop(node.getInputPosition(), node.getLabel(), doneLabel, continueLabel);
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
        loopStack.enterLoop(node.getInputPosition(), node.getLabel(), doneLabel, continueLabel);
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
        loopStack.enterLoop(node.getInputPosition(), node.getLabel(), doneLabel, continueLabel);
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

    // Code generation optimization:
    // If the BOOL_OR is reused in one of these operations, LOGICAL_OR can be used instead
    // - the exact numerical value of the result isn't important.
    private static final Set<String> LOGICAL_OPERATIONS = Set.of("and", "or", "||", "&&");

    @Override
    public LogicValue visitBinaryOp(BinaryOp node) {
        String op = node.getOp().equals("||") && parentContext.node instanceof BinaryOp opNode && LOGICAL_OPERATIONS.contains(opNode.getOp())
                ? "or" : node.getOp();

        final LogicValue left = visit(node.getLeft());
        final LogicValue right = visit(node.getRight());
        final LogicVariable tmp = nextNodeResult();
        emit(createOp(Operation.fromMindcode(op), tmp, left, right));
        if (op.equals("||")) {
            final LogicVariable tmp2 = nextNodeResult();
            emit(createOp(Operation.NOT_EQUAL, tmp2, tmp, FALSE));
            return tmp2;
        } else {
            return tmp;
        }
    }

    @Override
    public LogicValue visitRef(Ref node) {
        // TODO: warn when "configure" is used in V7
        return LogicBuiltIn.create(node.getName());
    }

    @Override
    public LogicValue visitVarRef(VarRef node) {
        return createVariableOrConstant(node,node.getName(), false, null);
    }

    private LogicVariable visitVariableVarRef(VarRef node) {
        return (LogicVariable) createVariableOrConstant(node, node.getName(), true, null);
    }

    private LogicVariable visitVariableVarRef(VarRef node, String errorMessage) {
        return (LogicVariable) createVariableOrConstant(node, node.getName(), true, errorMessage);
    }

    private LogicVariable convertFunctionParameter(FunctionParameter node) {
        return (LogicVariable) createVariableOrConstant(node, node.getName(), true, null);
    }

    private LogicVariable convertFunctionParameter(FunctionParameter node, String errorMessage) {
        return (LogicVariable) createVariableOrConstant(node, node.getName(), true, errorMessage);
    }

    private LogicValue createValue(AstNode node, String identifier) {
        return createVariableOrConstant(node, identifier, false, null);
    }

    private LogicValue createVariableOrConstant(AstNode node, String identifier, boolean requireVariable, String errorMessage) {
        // If the name refers to a constant/parameter, use it.
        // If it wasn't a constant already, the name will be reserved for a variable
        LogicValue constant = queryConstantName(node, identifier);
        if (constant != null) {
            if (requireVariable) {
                error(node, "%s", errorMessage != null
                        ? errorMessage
                        : "'" + identifier + "' is a constant or parameter; a variable is expected here.");
                return LogicVariable.special("invalid");
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
            return LogicVariable.global(identifier, callGraph.getSyncedVariables().contains(identifier));
        } else if (functionPrefix.isEmpty()) {
            // Main variable - again not pushed onto stack
            return LogicVariable.main(identifier);
        } else {
            // Either a local variable, or a function parameter
            // Note: parameters of inlined functions are treated as local variables
            FunctionParameter parameter = currentFunction.getDeclaredParameter(identifier);
            return functionContext.registerVariable(parameter == null
                    ? LogicVariable.local(currentFunction.getName(), functionPrefix, identifier)
                    : LogicVariable.local(currentFunction.getName(), functionPrefix, parameter));
        }
    }

    // A variable is required here
    private LogicVariable createMemoryVariable(AstNode node, String identifier) {
        LogicValue constant = queryConstantName(node, identifier);
        if (constant != null) {
            if (constant instanceof LogicParameter p && p.getValue().getType() == ArgumentType.BLOCK) {
                return p;
            } else {
                error(node, "'%s' cannot be used for external memory access.", identifier);
            }
        }

        return (LogicVariable) createVariableOrConstant(node, identifier, true, null);
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
    public LogicValue visitFormattableLiteral(FormattableLiteral node) {
        error(node, "Formattable string not allowed here. It can only be used with 'print', 'println' and 'format' functions.");
        return NULL;
    }

    @Override
    public LogicValue visitNumericLiteral(NumericLiteral node) {
        try {
            return node.toLogicLiteral(instructionProcessor);
        } catch (NumericLiteral.NoValidMlogRepresentationException ex) {
            error(node, "%s", ex.getMessage());
            return LogicNumber.ZERO;
        }
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
        // Do nothing - function declarations are processed by CallGraphCreator
        return NULL;
    }

    @Override
    public LogicValue visitFunctionParameter(FunctionParameter node) {
        // Do nothing - function declarations are processed by CallGraphCreator
        return NULL;
    }

    @Override
    public LogicValue visitHeapAllocation(HeapAllocation heap) {
        if (heap.hasRange()) {
            AstNode first = expressionEvaluator.evaluate(heap.getRange().getFirstValue());
            AstNode last = expressionEvaluator.evaluate(heap.getRange().getLastValue());

            if (first instanceof NumericLiteral firstLit && last instanceof NumericLiteral lastLit) {
                if (firstLit.notInteger() || lastLit.notInteger()) {
                    error(heap, "Heap declarations must use integer range.");
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (heap.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    error(heap, "Empty or invalid range in heap declaration.");
                }

                heapBaseAddress = firstInt;
                heapSize = lastInt - firstInt;
            } else {
                error(heap, "Heap declarations must use constant range.");
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
                    error(stack, "Stack declarations must use integer range.");
                }

                int firstInt = firstLit.getAsInteger();
                int lastInt = lastLit.getAsInteger() + (stack.getRange() instanceof InclusiveRange ? 1 : 0);

                if (firstInt >= lastInt) {
                    error(stack, "Empty or invalid range in stack declaration.");
                }

                start = firstInt;
            } else {
                error(stack, "Stack declarations must use constant range.");
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
        final LogicLabel label = loopStack.getBreakLabel(node.getInputPosition(), node.getLabel());
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitContinueStatement(ContinueStatement node) {
        final LogicLabel label = loopStack.getContinueLabel(node.getInputPosition(), node.getLabel());
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitReturnStatement(ReturnStatement node) {
        final LogicVariable retval = returnStack.getReturnValue(node.getInputPosition());
        final LogicLabel label = returnStack.getReturnLabel(node.getInputPosition());
        final LogicValue expression = visit(node.getRetval());
        emit(createSet(retval, expression));
        emit(createJumpUnconditional(label));
        return NULL;
    }

    @Override
    public LogicValue visitControl(Control node) {
        final LogicValue target = visit(node.getTarget());
        List<LogicFunctionArgument> arguments = processArguments(node.getArguments());
        LogicValue value = functionMapper.handleProperty(node, instructions::add, node.getProperty(), target, arguments);
        if (value == null) {
            error(node, "Undefined property '%s.%s'.", target, node.getProperty());
            return NULL;
        }
        return value;
    }

    enum Formatter {
        PRINT("print", LogicInstructionGenerator::createPrint),
        PRINTLN("println", LogicInstructionGenerator::createPrint),
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

        boolean createsNewLine() {
            return this == PRINTLN;
        }

        boolean formatVariantOnly() {
            return this == PRINTF || this == REMARK;
        }

        boolean requiresParameter() {
            return this == PRINTF || this == REMARK;
        }
    }

    private static final Pattern PLACEHOLDER_MATCHER = Pattern.compile("\\{\\d}");

    private LogicValue handleMinMax(FunctionCall call) {
        if (call.getArguments().size() < 2) {
            error(call, "Not enough arguments to the '%s' function (expected 2 or more, found %d).",
                    call.getFunctionName(), call.getArguments().size());
        }
        validateStandardFunctionArguments(call.getArguments());

        setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<LogicFunctionArgument> arguments = processArguments(call.getArguments());

        setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        LogicValue result;
        if (arguments.size() >= 2) {
            Operation op = Operation.fromMlog(call.getFunctionName());
            LogicVariable tmp = instructionProcessor.nextTemp();
            emit(createOp(op, tmp, arguments.get(0).value(), arguments.get(1).value()));
            for (int i = 2; i < arguments.size(); i++) {
                emit(createOp(op, tmp, tmp, arguments.get(i).value()));
            }
            result = tmp;
        } else {
            result = NULL;
        }

        clearSubcontextType();
        return result;
    }

    private LogicValue handlePrintf(FunctionCall call) {
        validateStandardFunctionArguments(call.getArguments());
        String functionName = call.getFunctionName();
        if (instructionProcessor.isSupported(Opcode.FORMAT, List.of(LogicNull.NULL))) {
            setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
            final List<LogicFunctionArgument> arguments = processArguments(call.getArguments());

            if (!arguments.isEmpty() && arguments.get(0).value() instanceof LogicString str) {
                long placeholders = PLACEHOLDER_MATCHER.matcher(str.format()).results().count();
                if (placeholders == 0) {
                    warn(call, "The 'printf' function is called with a literal format string which doesn't contain any format placeholders.");
                }
                if (placeholders > arguments.size() - 1) {
                    warn(call, "The 'printf' function doesn't have enough arguments for placeholders: %d placeholder(s), %d argument(s).",
                            placeholders, arguments.size() - 1);
                } else if (placeholders < arguments.size() - 1) {
                    warn(call, "The 'printf' function has more arguments than placeholders: %d placeholder(s), %d argument(s).",
                            placeholders, arguments.size() - 1);
                }
                warn(call, "The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.");
            }
            setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            for (int i = 0; i < arguments.size(); i++) {
                emit(i == 0 ? createPrint(arguments.get(i).value()) : createFormat(arguments.get(i).value()));
            }
            clearSubcontextType();
            return arguments.isEmpty() ? NULL : arguments.get(arguments.size() - 1).value();
        } else {
            warn(call, "The '%s' function is deprecated.", functionName);
            return handleFormattedOutput(call, Formatter.PRINTF);
        }
    }

    private String evaluateFormattableNode(FunctionArgument node) {
        if (node.getExpression() instanceof FormattableLiteral fmt) {
            return fmt.getText();
        } else if (node.getExpression() instanceof VarRef var && formattables.containsKey(var.getName())) {
            return formattables.get(var.getName()).getText();
        } else {
            return null;
        }
    }

    private LogicValue handleFormattedOutput(FunctionCall call, Formatter formatter) {
        validateStandardFunctionArguments(call.getArguments());

        if (call.getArguments().isEmpty()) {
            if (formatter.requiresParameter()) {
                error(call, "First parameter of the '%s' function must be a formattable string or constant string value.", formatter.function);
            } else if (formatter.createsNewLine()) {
                emit(formatter.createInstruction(this,LogicString.NEW_LINE));
            }
            return NULL;
        }

        String pattern = evaluateFormattableNode(call.getArguments().get(0));
        boolean formatting = pattern != null || formatter.formatVariantOnly();
        if (formatting && pattern == null) {
            // Use normal string constant/literal as pattern
            AstNode astFormat = expressionEvaluator.evaluate(call.getArguments().get(0).getExpression());
            if (astFormat instanceof StringLiteral format) {
                pattern = format.getText();
            } else {
                error(call, "First parameter of the '%s' function must be a formattable string or constant string value.", formatter.function);
                pattern = "";
            }
        }

        // Process arguments except the format string, if any
        List<FunctionArgument> params = call.getArguments().subList(formatting ? 1 : 0, call.getArguments().size());
        setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<LogicFunctionArgument> arguments = processArguments(params);
        setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);

        LogicValue returnValue;
        if (formatting) {
            returnValue = handleFormattedOutput(formatter, call, pattern, arguments);
        } else {
            // Only create instruction for each argument
            arguments.forEach(argument -> emit(formatter.createInstruction(this, argument.value())));
            returnValue = arguments.get(arguments.size() - 1).value();
        }

        if (formatter.createsNewLine()) {
            emit(formatter.createInstruction(this,LogicString.NEW_LINE));
        }
        clearSubcontextType();
        return returnValue;
    }

    private LogicValue handleFormattedOutput(Formatter formatter, FunctionCall node, String format, List<LogicFunctionArgument> arguments) {
        boolean escape = false;
        StringBuilder accumulator = new StringBuilder();
        int position = 0;
        boolean notEnoughArguments = false;

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
                    if (!accumulator.isEmpty()) {
                        emit(formatter.createInstruction(this, LogicString.create(accumulator.toString())));
                        accumulator.setLength(0);
                    }

                    final String variable;
                    if (i + 1 < format.length() && format.charAt(i + 1) == '{') {
                        int closeBracket = format.indexOf('}', i + 2);
                        if (closeBracket == -1) {
                            error(node, "Invalid format string (missing '}' after '${').");
                            break;
                        }

                        variable = format.substring(i + 2, closeBracket).trim();
                        i = closeBracket;
                        if (!variable.isEmpty() && !Pattern.matches("^[@_a-zA-Z][-a-zA-Z_0-9]*$", variable)) {
                            error(node, "Unsupported expression '%s' inside format string - only variable names are allowed.", variable);
                            break;
                        }
                    } else {
                        variable = extractVariable(format.substring(i + 1));
                        i += variable.length();
                    }

                    if (variable.isEmpty()) {
                        // No variable, emit next argument
                        if (position < arguments.size()) {
                            emit(formatter.createInstruction(this, arguments.get(position++).value()));
                        } else if (!notEnoughArguments) {
                            error(node, "Not enough arguments for '%s' format string.", formatter.function);
                            notEnoughArguments = true;
                        }
                    } else {
                        // Going through createValue ensures proper handling and registering of local variables
                        LogicValue eval = variable.startsWith("@")
                                ? LogicBuiltIn.create(variable.substring(1))
                                : createValue(node, variable);
                        emit(formatter.createInstruction(this, eval));
                    }

                    break;

                case '\\':
                    if (escape) {
                        accumulator.append('\\');
                    }
                    escape = !escape;
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

        if (!accumulator.isEmpty()) {
            emit(formatter.createInstruction(this,LogicString.create(accumulator.toString())));
        }

        if (position < arguments.size()) {
            error(node, "Too many arguments for '%s' format string.", formatter.function);
        }

        return NULL;
    }

    private static final Pattern REGEX_VARIABLE = Pattern.compile("^([@_a-zA-Z][-a-zA-Z_0-9]*)");

    private String extractVariable(String string) {
        Matcher matcher = REGEX_VARIABLE.matcher(string);
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
        final AstNode node;

        public NodeContext(AstNode node) {
            this.node = node;
        }

        public NodeContext(AstNode node, LocalContext parent) {
            this.node = node;
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
