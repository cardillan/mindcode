package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.functions.FunctionMapper;
import info.teksol.mindcode.mindustry.functions.WrongNumberOfParametersException;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.*;
import java.util.function.Supplier;
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
    private final LoopStack loopStack = new LoopStack();

    // Contains the infromation about functions built from the program to support code generation
    private CallGraph callGraph;

    // These instances track variables that need to be stored on stack for recursive function calls.
    
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
        appendStackAllocation();
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
            return super.visit(node);
        } finally {
            nodeContext = parentContext;
            parentContext = previousParent;
        }
    }

    private LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private LogicInstruction createInstruction(String marker, Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(marker, opcode, args);
    }

    private String translateUnaryOpToCode(String op) {
        return instructionProcessor.translateUnaryOpToCode(op);
    }

    private String translateBinaryOpToCode(String op) {
        return instructionProcessor.translateBinaryOpToCode(op);
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

    private void appendStackAllocation() {
        if (callGraph.containsRecursiveFunction()) {
            if (callGraph.getAllocatedStack() == null) {
                throw new MissingStackException("Cannot declare functions when no stack was allocated");
            }

            // Initialize stack pointer variable
            pipeline.emit(
                    createInstruction(SET,
                            instructionProcessor.getStackPointer(),
                            String.valueOf(callGraph.getAllocatedStack().getLast())
                    )
            );
        }
    }

    private void appendFunctionDeclarations() {
        pipeline.emit(createInstruction(END));

        for (CallGraph.Function function : callGraph.getFunctions()) {
            if (function.isInline() || !function.isUsed()) {
                continue;
            }

            currentFunction = function;
            localPrefix = function.getLocalPrefix();
            functionContext = new LocalContext();
            pipeline.emit(createInstruction(Opcode.LABEL, function.getLabel()));

            if (function.isRecursive()) {
                appendRecursiveFunctionDeclaration(function);
            } else {
                appendStacklessFunctionDeclaration(function);
            }

            pipeline.emit(createInstruction(END));
            localPrefix = "";
            currentFunction = callGraph.getMain();
        }

    }

    private void appendRecursiveFunctionDeclaration(CallGraph.Function function) {
        // Register all parameters for stack storage
        function.getParams().stream().map(this::visitVarRef).forEach(functionContext::registerVariable);

        // Function parameters and return address are set up at the call site
        final String body = visit(function.getBody());
        pipeline.emit(createInstruction(SET, localPrefix + RETURN_VALUE, body));
        pipeline.emit(createInstruction(RETURN, stackName()));
    }

    private void appendStacklessFunctionDeclaration(CallGraph.Function function) {
        // Function parameters and return address are set up at the call site
        final String body = visit(function.getBody());
        pipeline.emit(createInstruction(SET, localPrefix + RETURN_VALUE, body));
        pipeline.emit(createInstruction(SET, "@counter", localPrefix + RETURN_ADDRESS));
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        final List<String> params = nodeContext.encapsulate(
                () -> node.getParams().stream().map(this::visit).collect(Collectors.toList()));

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
        try {
            currentFunction = function;
            functionContext = new LocalContext();
            setupFunctionParameters(function, paramValues);
            return visit(function.getBody());
        } finally {
            functionContext = previousContext;
            currentFunction = previousFunction;
        }
    }

    private String handleStacklessFunctionCall(CallGraph.Function function, List<String> paramValues) {
        setupFunctionParameters(function, paramValues);

        final String returnLabel = nextLabel();
        pipeline.emit(createInstruction(SET, localPrefix + RETURN_ADDRESS, returnLabel));

         // Stackless function call (could be a jump as well, but this lets us distinguish call and jump easily in the code)
        pipeline.emit(createInstruction(SET, "@counter", function.getLabel()));
        pipeline.emit(createInstruction(LABEL, returnLabel)); // where the function must return

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
            variables.forEach(v -> pipeline.emit(createInstruction(marker, PUSH, stackName(), v)));
        }

        setupFunctionParameters(function, paramValues);

         // Recursive function call
        final String returnLabel = nextLabel();
        pipeline.emit(createInstruction(marker, CALL, stackName(), function.getLabel(), returnLabel));
        pipeline.emit(createInstruction(LABEL, returnLabel)); // where the function must return

        if (useStack) {
            // Restore all local varables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> pipeline.emit(createInstruction(marker, POP, stackName(), v)));
        }

        return passReturnValue(function);
    }

    private void setupFunctionParameters(CallGraph.Function function, List<String> paramValues) {
        // Setup variables representing function parameters with values from this call
        List<VarRef> paramsRefs = function.getParams();
        for (int i = 0; i < paramValues.size(); i++) {
            // Visiting paramRefs (which are VarRefs) decorated them with localPrefix
            // paramValues were visited in caller's context
            pipeline.emit(createInstruction(SET, visit(paramsRefs.get(i)), paramValues.get(i)));
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
            pipeline.emit(createInstruction(SET, resultVariable, localPrefix + RETURN_VALUE));
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
        pipeline.emit(createInstruction(READ, tmp, node.getCellName(), addr));
        return tmp;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        final String cond = nodeContext.encapsulate(() -> visit(node.getCondition()));

        final String tmp = nextNodeResult();
        final String elseBranch = nextLabel();
        final String endBranch = nextLabel();

        pipeline.emit(createInstruction(JUMP, elseBranch, "equal", cond, "false"));

        final String trueBranch = visit(node.getTrueBranch());
        pipeline.emit(createInstruction(SET, tmp, trueBranch));
        pipeline.emit(createInstruction(JUMP, endBranch, "always"));

        pipeline.emit(createInstruction(LABEL, elseBranch));
        final String falseBranch = visit(node.getFalseBranch());
        pipeline.emit(createInstruction(SET, tmp, falseBranch));
        pipeline.emit(createInstruction(LABEL, endBranch));

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
    public String visitAssignment(Assignment node) {
        final String rvalue = visit(node.getValue());

        if (node.getVar() instanceof HeapAccess) {
            final HeapAccess heapAccess = (HeapAccess) node.getVar();
            final String target = heapAccess.getCellName();
            final String address = visit(heapAccess.getAddress());
            pipeline.emit(createInstruction(Opcode.WRITE, rvalue, target, address));
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final String propTarget = visit(propertyAccess.getTarget());
            String prop = visit(propertyAccess.getProperty());
            if (prop.startsWith("@")) prop = prop.replaceFirst("@", "");  // TODO (@francois): is it still relevant?
            if (functionMapper.handleProperty(pipeline, prop, propTarget, List.of(rvalue)) == null) {
                throw new UndeclaredFunctionException("Don't know how to handle property [" + propTarget + "." + prop + "]");
            }
        } else if (node.getVar() instanceof VarRef) {
            final String target = visit(node.getVar());
            pipeline.emit(createInstruction(Opcode.SET, target, rvalue));
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
        pipeline.emit(createInstruction(OP, translateUnaryOpToCode(node.getOp()), tmp, expression));
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
            pipeline.emit(createInstruction(SET, iterator, nextValueLabel));
            pipeline.emit(createInstruction(SET, variable, visit(values.get(i))));
            pipeline.emit(createInstruction(JUMP, bodyLabel, "always"));
            pipeline.emit(createInstruction(marker, LABEL, nextValueLabel));
        }

        // Last value
        pipeline.emit(createInstruction(SET, iterator, exitLabel));
        pipeline.emit(createInstruction(SET, variable, visit(values.get(values.size() - 1))));

        pipeline.emit(createInstruction(LABEL, bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        pipeline.emit(createInstruction(LABEL, contLabel));
        pipeline.emit(createInstruction(marker, GOTO, iterator));
        pipeline.emit(createInstruction(LABEL, exitLabel));
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
        pipeline.emit(createInstruction(LABEL, beginLabel));
        final String cond = visit(node.getCondition());
        pipeline.emit(createInstruction(JUMP, doneLabel, "equal", cond, "false"));
        visit(node.getBody());
        pipeline.emit(createInstruction(LABEL, continueLabel));
        visit(node.getUpdate());
        pipeline.emit(createInstruction(JUMP, beginLabel, "always"));
        pipeline.emit(createInstruction(LABEL, doneLabel));
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
        pipeline.emit(createInstruction(LABEL, beginLabel));
        visit(node.getBody());
        pipeline.emit(createInstruction(LABEL, continueLabel));
        final String cond = visit(node.getCondition());
        pipeline.emit(createInstruction(JUMP, beginLabel, "notEqual", cond, "false"));
        pipeline.emit(createInstruction(LABEL, doneLabel));
        loopStack.exitLoop(node.getLabel());
        return "null";
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        final String left = visit(node.getLeft());
        final String right = visit(node.getRight());

        final String tmp = nextNodeResult();
        pipeline.emit(createInstruction(OP, translateBinaryOpToCode(node.getOp()), tmp, left, right));
        return tmp;
    }

    @Override
    public String visitRef(Ref node) {
        return "@" + node.getName();
    }

    @Override
    public String visitNullLiteral(NullLiteral node) {
        return "null";
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteral node) {
        return String.valueOf(node.getValue());
    }

    @Override
    public String visitVarRef(VarRef node) {
        // Global (main program body) variables aren't registered -- they must not be pushed onto stack!
        // Adds underscore after non-empty local prefix, to make sure return value and return address variables
        // cannot ever collide with user-defined local variables.
        return localPrefix.isEmpty() || instructionProcessor.isGlobalName(node.getName())
                ? node.getName()
                : functionContext.registerVariable(localPrefix + "_" + node.getName());
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        final String tmp = nextNodeResult();
        pipeline.emit(createInstruction(SET, tmp, "\"" + node.getText().replaceAll("\"", "\\\"") + "\""));
        return tmp;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextNodeResult();
        pipeline.emit(createInstruction(SET, tmp, node.getLiteral()));
        return tmp;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        final String target = visit(node.getTarget());
        final String prop = visit(node.getProperty());
        final String tmp = nextNodeResult();
        pipeline.emit(createInstruction(SENSOR, tmp, target, prop));
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
                // Each matching value, including the last one, causes a jump to the when body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstNode value : alternative.getValues()) {
                    if (value instanceof Range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        String nextExp = nextLabel();       // Next value in when list
                        Range range = (Range) value;
                        final String minValue = visit(range.getFirstValue());
                        pipeline.emit(createInstruction(JUMP, nextExp, translateBinaryOpToCode("<"), caseValue, minValue));
                        // The max value is only evaluated when the min value lets us through
                        final String maxValue = visit(range.getLastValue());
                        pipeline.emit(createInstruction(JUMP, bodyLabel, translateBinaryOpToCode(range.maxValueComparison()), caseValue, maxValue));
                        pipeline.emit(createInstruction(LABEL, nextExp));
                    }
                    else {
                        final String whenValue = visit(value);
                        pipeline.emit(createInstruction(JUMP, bodyLabel, "equal", caseValue, whenValue));
                    }
                }
            });

            // No match in the when value list: skip to the next alternative
            pipeline.emit(createInstruction(JUMP, nextAlt, "always"));

            // Body of the alternative
            pipeline.emit(createInstruction(LABEL, bodyLabel));
            final String body = visit(alternative.getBody());
            pipeline.emit(createInstruction(SET, resultVar, body));
            pipeline.emit(createInstruction(JUMP, exitLabel, "always"));

            pipeline.emit(createInstruction(LABEL, nextAlt));
        }

        final String elseBranch = visit(node.getElseBranch());
        pipeline.emit(createInstruction(SET, resultVar, elseBranch));
        pipeline.emit(createInstruction(LABEL, exitLabel));

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
        // Do nothing - stack allocations are procesed by CallGraphCreator
        return "null";
    }

    @Override
    public String visitBreakStatement(BreakStatement node) {
        final String label = loopStack.getBreakLabel(node.getLabel());
        pipeline.emit(createInstruction(JUMP, label, "always"));
        return "null";
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        final String label = loopStack.getContinueLabel(node.getLabel());
        pipeline.emit(createInstruction(JUMP, label, "always"));
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
