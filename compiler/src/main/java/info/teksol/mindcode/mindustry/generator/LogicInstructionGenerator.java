package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.functions.FunctionMapper;
import info.teksol.mindcode.mindustry.functions.WrongNumberOfParametersException;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<String> {
    private static final String RETURN_ADDRESS = "retaddr";
    private static final String RETURN_VALUE = "retval";

    // The version-dependent functionality is encapsulated in InstructionProcessor and FunctionMapper.
    // If future Mindustry versions offer more capabilities (such as native stack support),
    // even LogicInstructionGenerator might be made version dependent.
    private final InstructionProcessor instructionProcessor;
    private final FunctionMapper functionMapper;
    private final LogicInstructionPipeline pipeline;
    private final ReturnStack returnStack = new ReturnStack();

    private LoopStack loopStack = new LoopStack();

    // Contains the infromation about functions built from the program to support code generation
    private CallGraph callGraph;

    private ConstantExpressionEvaluator expressionEvaluator = new ConstantExpressionEvaluator();

    // These instances track variables that need to be stored on stack for recursive function calls.
    
    // Constants and global variables
    // Key is the name of variable/constant
    // Value is either an ConstantAstNode (for constant) or null (for variable)
    private Map<String, ConstantAstNode> constants = new HashMap<>();

    // Tracks all local function variables, including function parameters - once accessed, they have to be preserved.
    private LocalContext functionContext = new LocalContext();

    // Tracks variables whose scope is limited to the node being processed and have no meaning outside of the node.
    private NodeContext nodeContext = new NodeContext();

    // Tracks variables whose scope is limited to the parent node. These are variables that transfer the return value
    // of a node to its parent. When a new node is visited, nodeContext becomes parentContext and a new node context
    // is created.
    private NodeContext parentContext = new NodeContext();

    // Function definition being presently compiled (including inlined functions)
    private CallGraph.Function currentFunction; 

    // Prefix for local variables (depends on function being processed)
    private String localPrefix = "";

    private int markerIndex = 0;

    public LogicInstructionGenerator(InstructionProcessor instructionProcessor, FunctionMapper functionMapper,
            LogicInstructionPipeline pipeline) {
        this.instructionProcessor = instructionProcessor;
        this.functionMapper = functionMapper;
        this.pipeline = pipeline;
    }

    public void start(Seq program) {
        callGraph = CallGraphCreator.createFunctionGraph(program, instructionProcessor);
        currentFunction = callGraph.getMain();
        verifyStackAllocation();
        visit(program);
        appendFunctionDeclarations();
    }

    // Visit is called for every node. This overriden function ensures proper transition of variable contexts between
    // parent and child nodes.
    @Override
    public String visit(AstNode node) {
        NodeContext previousParent = parentContext;
        parentContext = nodeContext;
        nodeContext = new NodeContext(parentContext);  // inherit variables from parent context
        try {
            // Perform constant expresion evaluation
            return super.visit(expressionEvaluator.evaluate(node));
        } finally {
            nodeContext = parentContext;
            parentContext = previousParent;
        }
    }

    private void emitInstruction(Opcode opcode, String... args) {
        pipeline.emit(instructionProcessor.createInstruction(opcode, args));
    }

    private void emitInstruction(String marker, Opcode opcode, String... args) {
        pipeline.emit(instructionProcessor.createInstruction(marker, opcode, args));
    }

    private String translateOpToCode(String op) {
        return instructionProcessor.translateOpToCode(op);
    }

    private String nextLabel() {
        return instructionProcessor.nextLabel();
    }

    // Allocates a new temporary variable whose scope is limited to a node (ie. not needed outside that node)
    private String nextTemp() {
        return nodeContext.registerVariable(instructionProcessor.nextTemp());
    }

    // Allocates a new temporary variable which holds the evaluated value of a node
    private String nextNodeResult() {
        return parentContext.registerVariable(instructionProcessor.nextTemp());
    }

    // Allocates a new temporary variable which holds the return value of a function
    private String nextReturnValue() {
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

    private String queryConstantName(String name) {
        if (constants.get(name) != null) {
            return constants.get(name).getLiteral();
        } else {
            // Register the identifier as a variable name
            constants.put(name, null);
            return null;
        }
    }

    private String registerConstant(String name, ConstantAstNode value) {
        if (constants.get(name) != null) {
            throw new GenerationException("Multiple declarations of constant [" + name + "]");
        } else if (constants.containsKey(name)) {
            throw new GenerationException("Cannot redefine variable [" + name + "] as a constant");
        }
        constants.put(name, value);
        return name;
    }

    private void appendFunctionDeclarations() {
        emitInstruction(END);

        for (CallGraph.Function function : callGraph.getFunctions()) {
            if (function.isInline() || !function.isUsed()) {
                continue;
            }

            currentFunction = function;
            localPrefix = function.getLocalPrefix();
            functionContext = new LocalContext();
            emitInstruction(localPrefix, Opcode.LABEL, function.getLabel());
            returnStack.enterFunction(nextLabel(), localPrefix + RETURN_VALUE);

            if (function.isRecursive()) {
                appendRecursiveFunctionDeclaration(function);
            } else {
                appendStacklessFunctionDeclaration(function);
            }

            emitInstruction(END);
            localPrefix = "";
            returnStack.exitFunction();
            currentFunction = callGraph.getMain();
        }

    }

    private void appendRecursiveFunctionDeclaration(CallGraph.Function function) {
        // Register all parameters for stack storage
        function.getParams().stream().map(this::visitVarRef).forEach(functionContext::registerVariable);

        // Function parameters and return address are set up at the call site
        final String body = visit(function.getBody());
        emitInstruction(SET, localPrefix + RETURN_VALUE, body);
        emitInstruction(localPrefix, LABEL, returnStack.getReturnLabel());
        emitInstruction(RETURN, stackName());
    }

    private void appendStacklessFunctionDeclaration(CallGraph.Function function) {
        // Function parameters and return address are set up at the call site
        final String body = visit(function.getBody());
        emitInstruction(SET, localPrefix + RETURN_VALUE, body);
        emitInstruction(localPrefix, LABEL, returnStack.getReturnLabel());
        emitInstruction(SET, "@counter", localPrefix + RETURN_ADDRESS);
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        final List<String> params = nodeContext.encapsulate(
                () -> node.getParams().stream().map(this::visit).collect(Collectors.toList()));

        // Special cases
        switch (node.getFunctionName()) {
            case "printf":      return handlePrintf(node, params);
        }

        return handleFunctionCall(node.getFunctionName(), params);
    }

    private String handleFunctionCall(String functionName, List<String> params) {
        String output = functionMapper.handleFunction(pipeline, functionName, params);
        if (output != null) {
            return output;
        } else if (callGraph.containsFunction(functionName)) {
            return handleUserFunctionCall(functionName, params);
        } else {
            throw new UndeclaredFunctionException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private String handleUserFunctionCall(String functionName, List<String> params) {
        CallGraph.Function function = callGraph.getFunction(functionName);
        if (params.size() != function.getParamCount()) {
            throw new WrongNumberOfParametersException("Function '" + functionName + "': wrong number of parameters (expected "
                    + function.getParamCount() + ", found " + params.size() + ")");
        }

        // Switching to new function prefix -- save/restore old one
        String previousPrefix = localPrefix;
        try {
            // Entire inline function evaluates using given prefix (differnent invocations use different variables).
            // For other functions, the prefix is only used to set up variables representing function parameters.
            localPrefix = function.isInline() ? instructionProcessor.nextLocalPrefix(functionName) : function.getLocalPrefix();

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

    private String handleInlineFunctionCall(CallGraph.Function function, List<String> paramValues) {
        // Switching to inline function context -- save/restore old one
        final CallGraph.Function previousFunction = currentFunction;
        final LocalContext previousContext = functionContext;
        final LoopStack previousLoopStack = loopStack;
        try {
            currentFunction = function;
            functionContext = new LocalContext();
            loopStack = new LoopStack();

            emitInstruction(localPrefix, LABEL, nextLabel());
            setupFunctionParameters(function, paramValues);

            // Retval gets registered in nodeContext, but we don't mind -- inline fucntions do not use stack
            final String returnValue = nextReturnValue();
            final String returnLabel = nextLabel();
            returnStack.enterFunction(returnLabel, returnValue);
            String result = visit(function.getBody());
            emitInstruction(SET, returnValue, result);
            emitInstruction(localPrefix, LABEL, returnLabel);
            returnStack.exitFunction();
            return returnValue;
        } finally {
            loopStack = previousLoopStack;
            functionContext = previousContext;
            currentFunction = previousFunction;
        }
    }

    private String handleStacklessFunctionCall(CallGraph.Function function, List<String> paramValues) {
        setupFunctionParameters(function, paramValues);

        final String returnLabel = nextLabel();
        emitInstruction(SET, localPrefix + RETURN_ADDRESS, returnLabel);

         // Stackless function call (could be a jump as well, but this lets us distinguish call and jump easily in the code)
        emitInstruction(SET, "@counter", function.getLabel());
        emitInstruction(LABEL, returnLabel); // where the function must return

        return passReturnValue(function);
    }

    private List<String> getContextVariables() {
        Set<String> result = new LinkedHashSet<>(functionContext.getVariables());
        result.addAll(nodeContext.getVariables());
        return new ArrayList<>(result);
    }

    private String handleRecursiveFunctionCall(CallGraph.Function function, List<String> paramValues) {
        String marker = nextMarker();

        boolean useStack = currentFunction.isRecursiveCall(function.getName());
        List<String> variables = useStack ? getContextVariables() : List.of();

        if (useStack) {
            // Store all local varables (both user defined and temporary) on the stack
            variables.forEach(v -> emitInstruction(marker, PUSH, stackName(), v));
        }

        setupFunctionParameters(function, paramValues);

         // Recursive function call
        final String returnLabel = nextLabel();
        emitInstruction(marker, CALL, stackName(), function.getLabel(), returnLabel);
        emitInstruction(LABEL, returnLabel); // where the function must return

        if (useStack) {
            // Restore all local varables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> emitInstruction(marker, POP, stackName(), v));
        }

        return passReturnValue(function);
    }

    private void setupFunctionParameters(CallGraph.Function function, List<String> paramValues) {
        // Setup variables representing function parameters with values from this call
        List<VarRef> paramsRefs = function.getParams();
        for (int i = 0; i < paramValues.size(); i++) {
            // Visiting paramRefs (which are VarRefs) decorated them with localPrefix
            // paramValues were visited in caller's context
            emitInstruction(SET, visit(paramsRefs.get(i)), paramValues.get(i));
        }
    }

    private String passReturnValue(CallGraph.Function function) {
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
            final String resultVariable = nextReturnValue();
            emitInstruction(SET, resultVariable, localPrefix + RETURN_VALUE);
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this funcion's call tree
            return localPrefix + RETURN_VALUE;
        }
    }

    private String stackName() {
        return callGraph.getAllocatedStack().getName();
    }




    @Override
    public String visitHeapAccess(HeapAccess node) {
        final String addr = visit(node.getAddress());
        final String tmp = nextNodeResult();
        emitInstruction(READ, tmp, node.getCellName(), addr);
        return tmp;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        final String cond = nodeContext.encapsulate(() -> visit(node.getCondition()));

        final String tmp = nextNodeResult();
        final String elseBranch = nextLabel();
        final String endBranch = nextLabel();

        emitInstruction(JUMP, elseBranch, "equal", cond, "false");

        final String trueBranch = visit(node.getTrueBranch());
        emitInstruction(SET, tmp, trueBranch);
        emitInstruction(JUMP, endBranch, "always");

        emitInstruction(LABEL, elseBranch);
        final String falseBranch = visit(node.getFalseBranch());
        emitInstruction(SET, tmp, falseBranch);
        emitInstruction(LABEL, endBranch);

        return tmp;
    }

    @Override
    public String visitSeq(Seq seq) {
        visit(seq.getRest());
        return visit(seq.getLast());
    }

    @Override
    public String visitNoOp(NoOp node) {
        return "null";
    }

    @Override
    public String visitConstant(Constant node) {
        if (!localPrefix.isEmpty()) {
            throw new GenerationException("Constant declaration not allowed in user function [" + node.getName() + "]");
        }
        AstNode value = expressionEvaluator.evaluate(node.getValue());
        if (!(value instanceof ConstantAstNode)) {
            throw new GenerationException("Constant declaration of [" + node.getName() + "] does not use a constant expression");
        }
        registerConstant(node.getName(), (ConstantAstNode)value);
        return "null";
    }

    @Override
    public String visitAssignment(Assignment node) {
        final String rvalue = visit(node.getValue());

        if (node.getVar() instanceof HeapAccess) {
            final HeapAccess heapAccess = (HeapAccess) node.getVar();
            final String target = heapAccess.getCellName();
            final String address = visit(heapAccess.getAddress());
            emitInstruction(Opcode.WRITE, rvalue, target, address);
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final String propTarget = visit(propertyAccess.getTarget());
            String prop = visit(propertyAccess.getProperty());
            if (prop.startsWith("@")) prop = prop.replaceFirst("@", "");  // TODO (@francois): is it still relevant?
            if (functionMapper.handleProperty(pipeline, prop, propTarget, List.of(rvalue)) == null) {
                throw new UndeclaredFunctionException("Don't know how to handle property [" + propTarget + "." + prop + "]");
            }
        } else if (node.getVar() instanceof VarRef) {
            String name = ((VarRef)node.getVar()).getName();
            if (constants.get(name) != null) {
                throw new GenerationException("Assignment to constant [" + name + "] not allowed");
            }

            final String target = visit(node.getVar());
            if (instructionProcessor.isBlockName(target)) {
                throw new GenerationException("Assignment to variable [" + target + "] not allowed (name reserved for linked blocks)");
            }
            emitInstruction(Opcode.SET, target, rvalue);
        } else {
            throw new GenerationException("Unhandled assignment target in " + node);
        }

        // rvalue holds the result of this node -- needs to be explicitly registered in parent node context.
        // This can place user variable on node context. This should be all right, any such variable would be
        // registered in functionContext as well. Additional registration here is harmless.
        parentContext.registerVariable(rvalue);
        return rvalue;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        final String expression = visit(node.getExpression());

        final String tmp = nextNodeResult();
        emitInstruction(OP, translateOpToCode(node.getOp()), tmp, expression);
        return tmp;
    }

    @Override
    public String visitForEachStatement(ForEachExpression node) {
        List<AstNode> values = node.getValues();
        if (values.isEmpty()) {
            // Empty list -- nothing to do
            return null;
        }

        final String variable = visit(node.getVariable());
        final String contLabel = nextLabel();
        final String bodyLabel = nextLabel();
        final String exitLabel = nextLabel();
        final String iterator = nextTemp();        // Holds instruction address of the next iteration
        final String marker = nextMarker();

        loopStack.enterLoop(node.getLabel(), exitLabel, contLabel);

        // All but the last value
        for (int i = 0; i < values.size() - 1; i++) {
            String nextValueLabel = nextLabel();
            // Setting the iterator first. It is possible to use continue in the value expression,
            // in which case the next iteration is performed.
            emitInstruction(SET, iterator, nextValueLabel);
            emitInstruction(SET, variable, visit(values.get(i)));
            emitInstruction(JUMP, bodyLabel, "always");
            emitInstruction(marker, LABEL, nextValueLabel);
        }

        // Last value
        emitInstruction(SET, iterator, exitLabel);
        emitInstruction(SET, variable, visit(values.get(values.size() - 1)));

        emitInstruction(LABEL, bodyLabel);
        visit(node.getBody());

        // Label for continue statements
        emitInstruction(LABEL, contLabel);
        emitInstruction(marker, GOTO, iterator);
        emitInstruction(LABEL, exitLabel);
        loopStack.exitLoop(node.getLabel());

        return "null";
    }

    @Override
    public String visitWhileStatement(WhileExpression node) {
        final String beginLabel = nextLabel();
        final String continueLabel = nextLabel();
        final String doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        emitInstruction(LABEL, beginLabel);
        final String cond = visit(node.getCondition());
        emitInstruction(JUMP, doneLabel, "equal", cond, "false");
        visit(node.getBody());
        emitInstruction(LABEL, continueLabel);
        visit(node.getUpdate());
        emitInstruction(JUMP, beginLabel, "always");
        emitInstruction(LABEL, doneLabel);
        loopStack.exitLoop(node.getLabel());
        return "null";
    }

    @Override
    public String visitDoWhileStatement(DoWhileExpression node) {
        final String beginLabel = nextLabel();
        final String continueLabel = nextLabel();
        final String doneLabel = nextLabel();
        // Not using try/finally to ensure stack consistency - any exception stops compilation anyway
        loopStack.enterLoop(node.getLabel(), doneLabel, continueLabel);
        emitInstruction(LABEL, beginLabel);
        visit(node.getBody());
        emitInstruction(LABEL, continueLabel);
        final String cond = visit(node.getCondition());
        emitInstruction(JUMP, beginLabel, "notEqual", cond, "false");
        emitInstruction(LABEL, doneLabel);
        loopStack.exitLoop(node.getLabel());
        return "null";
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        final String left = visit(node.getLeft());
        final String right = visit(node.getRight());

        final String tmp = nextNodeResult();
        emitInstruction(OP, translateOpToCode(node.getOp()), tmp, left, right);
        return tmp;
    }

    @Override
    public String visitRef(Ref node) {
        // TODO: warn when "configure" is used in V7
        return "@" + node.getName();
    }

    @Override
    public String visitVarRef(VarRef node) {
        // If the name refers to a constant, use it.
        // If it wasn't a constant already, the name will be reserved for a variable
        String constant = queryConstantName(node.getName());
        if (constant != null) {
            return constant;
        }

        // Global (main program body) variables aren't registered locally -- they must not be pushed onto stack!
        // Adds underscore after non-empty local prefix, to make sure return value and return address variables
        // cannot ever collide with user-defined local variables.
        return localPrefix.isEmpty()
                || instructionProcessor.isGlobalName(node.getName())
                || instructionProcessor.isBlockName(node.getName())
                ? node.getName()
                : functionContext.registerVariable(localPrefix + "_" + node.getName());
    }

    @Override
    public String visitNullLiteral(NullLiteral node) {
        return node.getLiteral();
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteral node) {
        return node.getLiteral();
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        return node.getLiteral();
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        return node.getLiteral();
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        final String target = visit(node.getTarget());
        final String prop = visit(node.getProperty());
        final String tmp = nextNodeResult();
        emitInstruction(SENSOR, tmp, target, prop);
        return tmp;
    }

    @Override
    public String visitCaseExpression(CaseExpression node) {
        final String resultVar = nextNodeResult();
        final String exitLabel = nextLabel();

        final String caseValue = visit(node.getCondition());
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final String nextAlt = nextLabel();         // Next alternative
            final String bodyLabel = nextLabel();       // Body of this alternative

            nodeContext.encapsulate(() -> {
                // Each matching value, including the last one, causes a jump to the "when" body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstNode value : alternative.getValues()) {
                    if (value instanceof Range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        String nextExp = nextLabel();       // Next value in when list
                        Range range = (Range) value;
                        final String minValue = visit(range.getFirstValue());
                        emitInstruction(JUMP, nextExp, "lessThan", caseValue, minValue);
                        // The max value is only evaluated when the min value lets us through
                        final String maxValue = visit(range.getLastValue());
                        emitInstruction(JUMP, bodyLabel, translateOpToCode(range.maxValueComparison()), caseValue, maxValue);
                        emitInstruction(LABEL, nextExp);
                    }
                    else {
                        final String whenValue = visit(value);
                        emitInstruction(JUMP, bodyLabel, "equal", caseValue, whenValue);
                    }
                }
            });

            // No match in the when value list: skip to the next alternative
            emitInstruction(JUMP, nextAlt, "always");

            // Body of the alternative
            emitInstruction(LABEL, bodyLabel);
            final String body = visit(alternative.getBody());
            emitInstruction(SET, resultVar, body);
            emitInstruction(JUMP, exitLabel, "always");

            emitInstruction(LABEL, nextAlt);
        }

        final String elseBranch = visit(node.getElseBranch());
        emitInstruction(SET, resultVar, elseBranch);
        emitInstruction(LABEL, exitLabel);

        return resultVar;
    }

    @Override
    public String visitCaseAlternative(CaseAlternative node) {
        // Case alternatives are handled in visitCaseExpression
        return null;
    }

    @Override
    public String visitRange(Range node) {
        // Ranges are handled elsewhere
        return null;
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        // Do nothing - function definitions are procesed by CallGraphCreator
        return "null";
    }

    @Override
    public String visitStackAllocation(StackAllocation node) {
        // Do not initialize stack if no recursive functions are present
        if (callGraph.containsRecursiveFunction()) {
            StackAllocation stack = callGraph.getAllocatedStack();
            String sp = instructionProcessor.getStackPointer();

            // Initialize stack pointer variable
            if (node.rangeSpecified()) {
                emitInstruction(SET, sp, String.valueOf(stack.getLast()));
            } else {
                // Range not specified. Determine memory size dynamically.
                String label = nextLabel();
                String tmp = nextTemp();
                emitInstruction(SET, sp, "511");
                emitInstruction(SENSOR, tmp, stack.getName(), "@type");
                emitInstruction(JUMP, label, "equal", tmp, "@memory-bank");
                emitInstruction(SET, sp, "63");
                emitInstruction(LABEL, label);
            }
        }
        return "null";
    }

    @Override
    public String visitBreakStatement(BreakStatement node) {
        final String label = loopStack.getBreakLabel(node.getLabel());
        emitInstruction(JUMP, label, "always");
        return "null";
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        final String label = loopStack.getContinueLabel(node.getLabel());
        emitInstruction(JUMP, label, "always");
        return "null";
    }

    @Override
    public String visitReturnStatement(ReturnStatement node) {
        final String retval = returnStack.getReturnValue();
        final String label = returnStack.getReturnLabel();
        final String expression = visit(node.getRetval());
        emitInstruction(SET, retval, expression);
        emitInstruction(JUMP, label, "always");
        return "null";
    }

    @Override
    public String visitControl(Control node) {
        final String target = visit(node.getTarget());
        final List<String> args = node.getParams().stream().map(this::visit).collect(Collectors.toList());
        String value = functionMapper.handleProperty(pipeline, node.getProperty(), target, args);
        if (value == null) {
            throw new UndeclaredFunctionException("Don't know how to handle property [" + target + "." + node.getProperty() + "]");
        }
        return value;
    }

    private String handlePrintf(FunctionCall node, List<String> params) {
        // Printf format string may contain references to variables, which is practically a code
        // Must be therefore handled here and not by the FunctionMapper
        
        if (params.isEmpty()) {
            // TODO: throw an error -- the format string *should* be here
            return "null";
        }

        AstNode astFormat = expressionEvaluator.evaluate(node.getParams().get(0));
        if (!(astFormat instanceof StringLiteral)) {
            throw new GenerationException("First parameter of printf() function must be a constant string.");
        }

        String format = ((StringLiteral) astFormat).getLiteral();
        boolean escape = false;
        StringBuilder accumulator = new StringBuilder();
        int position = 1;       // Skipping the 1st param, which is the format string

        // Skip leading and trailing quotes
        for (int i = 1; i < format.length() - 1; i++) {
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
                        emitInstruction(Opcode.PRINT, toStringLiteral(accumulator));
                        accumulator.setLength(0);
                    }

                    String variable = extractVariable(format.substring(i + 1));
                    if (variable.isEmpty()) {
                        // No variable, emit next argument
                        if (position < params.size()) {
                            emitInstruction(Opcode.PRINT, params.get(position++));
                        } else {
                            throw new TooFewPrintfArgumentsException("Not enough arguments for printf format string " + format);
                        }
                    } else {
                        // Going through visitVarRef ensures proper handling and registering of local variables
                        String eval = variable.startsWith("@") ? variable : visitVarRef(new VarRef(variable));
                        emitInstruction(Opcode.PRINT, eval);
                    }

                    if (format.charAt(i + 1) == '{') {
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
            emitInstruction(Opcode.PRINT, toStringLiteral(accumulator));
        }

        if (position < params.size()) {
            throw new TooManyPrintfArgumentsException("Too many arguments for printf format string " + format);
        }

        return "null";
    }

    private static final Pattern REGEX_VARIABLE = Pattern.compile("^([@_a-zA-Z][-a-zA-Z_0-9]*)");
    private static final Pattern REGEX_BRACKETS = Pattern.compile("^\\{\\s*([@_a-zA-Z][-a-zA-Z_0-9]*)?\\s*\\}");

    private String extractVariable(String string) {
        Matcher matcher = REGEX_BRACKETS.matcher(string);
        if (matcher.find()) return matcher.group(1) == null ? "" : matcher.group(1);

        matcher = REGEX_VARIABLE.matcher(string);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String toStringLiteral(StringBuilder sbr) {
        return sbr.insert(0, '"').append('"').toString();
    }

    private class LocalContext {
        protected final List<String> variables = new ArrayList<>();

        String registerVariable(String name) {
            if (!variables.contains(name)) {
                variables.add(name);
            }
            return name;
        }

        Collection<String> getVariables() {
            return variables;
        }
    }

    private class NodeContext extends LocalContext {
        public NodeContext() {
        }

        public NodeContext(LocalContext parent) {
            variables.addAll(parent.variables);
        }

        /**
         * Encapsulates processing of given expression, by keeping temporary variable(s) created while evaluating
         * the expression out of current node context. Suitable when the generated temporary variables are known
         * not to be used outside of the context of the expression. A good example is the condition expression of the
         * if statement: the condition is evaluated and the result is used to choose the branch to execute, but
         * all this happens before either of the branches are executed and the temporary variable holding the condition
         * value will not be used again.
         *
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
