package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.mindustry.AccumulatingLogicInstructionPipeline;
import info.teksol.mindcode.mindustry.CompilerProfile;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.functions.BaseFunctionMapper;
import info.teksol.mindcode.mindustry.functions.FunctionMapper;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.optimisation.DebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.DiffDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.NullDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.OptimisationPipeline;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<String> {
    private final InstructionProcessor instructionProcessor;
    private final FunctionMapper functionMapper;
    private final LogicInstructionPipeline pipeline;
    private StackAllocation allocatedStack;
    private final Map<String, FunctionDeclaration> declaredFunctions = new HashMap<>();
    private final Map<String, String> functionLabels = new HashMap<>();
    private final LoopStack loopStack = new LoopStack();

    LogicInstructionGenerator(InstructionProcessor instructionProcessor, FunctionMapper functionMapper, LogicInstructionPipeline pipeline) {
        this.instructionProcessor = instructionProcessor;
        this.functionMapper = functionMapper;
        this.pipeline = pipeline;
    }

    public static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program,
            CompilerProfile profile, Consumer<String> messageConsumer) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        final DebugPrinter debugPrinter = profile.getDebugLevel() == 0 
                ? new NullDebugPrinter() : new DiffDebugPrinter(profile.getDebugLevel());
        LogicInstructionPipeline pipeline = OptimisationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, debugPrinter, messageConsumer);
        LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                new BaseFunctionMapper(instructionProcessor), pipeline);
        generator.start(program);
        pipeline.flush();

        debugPrinter.print(messageConsumer);
        
        return terminus.getResult();
    }

    public static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.fullOptimizations(), s -> {});
    }

    public static void generateInto(InstructionProcessor instructionProcessor, LogicInstructionPipeline pipeline, Seq program) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                new BaseFunctionMapper(instructionProcessor), pipeline);
        generator.start(program);
        pipeline.flush();
    }

    public static List<LogicInstruction> generateUnoptimized(InstructionProcessor instructionProcessor, Seq program) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();

        LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                new BaseFunctionMapper(instructionProcessor), terminus);
        generator.start(program);
        terminus.flush();

        return terminus.getResult();
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, List<String> args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private void start(Seq program) {
        visit(program);
        appendFunctionDeclarations();
    }

    private void appendFunctionDeclarations() {
        // TODO: pass parameters through local variables? This would save a ton of instructions when going through the stack

        pipeline.emit(createInstruction(END));
        for (Map.Entry<String, FunctionDeclaration> pair : declaredFunctions.entrySet()) {
            final String label = functionLabels.get(pair.getKey());
            pipeline.emit(createInstruction(Opcode.LABEL, label));
            // caller pushes arguments left-to-right
            // we have to pop right-to-left, hence the reverse iteration here
            final ListIterator<AstNode> iterator = pair.getValue().getParams().listIterator(pair.getValue().getParams().size());
            while (iterator.hasPrevious()) {
                final AstNode var = iterator.previous();
                final String param = nextTemp();
                popValueFromStack(param);
                visit(new Assignment(var, new VarRef(param)));
            }

            final String body = visit(pair.getValue().getBody());
            final String returnAddress = nextTemp();
            popValueFromStack(returnAddress);
            pushValueOnStack(body);
            pipeline.emit(createInstruction(SET, "@counter", returnAddress));
            pipeline.emit(createInstruction(END));
        }

    }

    @Override
    public String visitHeapAccess(HeapAccess node) {
        final String addr = visit(node.getAddress());
        final String tmp = nextTemp();
        pipeline.emit(createInstruction(READ, tmp, node.getCellName(), addr));
        return tmp;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        final String cond = visit(node.getCondition());

        final String tmp = nextTemp();
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
            final String address = visit(heapAccess.getAddress());
            pipeline.emit(createInstruction(Opcode.WRITE, rvalue, heapAccess.getCellName(), address));
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final String propTarget = visit(propertyAccess.getTarget());
            String prop = visit(propertyAccess.getProperty());
            if (prop.startsWith("@")) prop = prop.replaceFirst("@", "");
            pipeline.emit(createInstruction(Opcode.CONTROL, prop, propTarget, rvalue));
        } else if (node.getVar() instanceof VarRef) {
            final String target = visit(node.getVar());
            pipeline.emit(createInstruction(Opcode.SET, target, rvalue));
        } else {
            throw new GenerationException("Unhandled assignment target in " + node);
        }

        return rvalue;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        final String expression = visit(node.getExpression());

        final String tmp = nextTemp();
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

        loopStack.enterLoop(node.getLabel(), exitLabel, contLabel);

        // All but the last value
        for (int i = 0; i < values.size() - 1; i++) {
            String nextValueLabel = nextLabel();
            // Setting the iterator first. It is possible to use continue in the value expression,
            // in which case the next iteration is performed.
            pipeline.emit(createInstruction(SET, iterator, nextValueLabel));
            pipeline.emit(createInstruction(SET, variable, visit(values.get(i))));
            pipeline.emit(createInstruction(JUMP, bodyLabel, "always"));
            pipeline.emit(createInstruction(LABEL, nextValueLabel));
        }

        // Last value
        pipeline.emit(createInstruction(SET, iterator, exitLabel));
        pipeline.emit(createInstruction(SET, variable, visit(values.get(values.size() - 1))));

        pipeline.emit(createInstruction(LABEL, bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        pipeline.emit(createInstruction(LABEL, contLabel));
        pipeline.emit(createInstruction(SET, "@counter", iterator));
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
    public String visitFunctionCall(FunctionCall node) {
        final List<String> params = node.getParams().stream().map(this::visit).collect(Collectors.toList());
        return handleFunctionCall(node.getFunctionName(), params);
    }

    private String handleFunctionCall(String functionName, List<String> params) {
        String output = functionMapper.handleFunction(pipeline, functionName, params);

        if (output != null) {
            return output;
        }

        if (declaredFunctions.containsKey(functionName)) {
            return handleInternalFunctionCall(functionName, params);
        } else {
            throw new UndeclaredFunctionException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private String handleInternalFunctionCall(String functionName, List<String> params) {
        // TODO: assert number of parameters matches expected number from declared function
        // This might require declaring functions before they are used. Otherwise, this would become a runtime exception.
        // It would be preferable to have functions declared early, so that the compiler can check number of parameters.
        final String returnLabel = nextLabel();

        pushValueOnStack(returnLabel);
        for (final String param : params) {
            pushValueOnStack(param);
        }
        pipeline.emit(createInstruction(SET, "@counter", functionLabels.get(functionName))); // actually call function
        pipeline.emit(createInstruction(LABEL, returnLabel)); // where the function must return

        final String returnValue = nextTemp();
        popValueFromStack(returnValue);
        return returnValue;
    }

    private void pushValueOnStack(String value) {
        final String stackPointer = nextTemp();
        visit(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef(stackPointer), new HeapAccess(stackName(), new NumericLiteral(stackTop()))),
                                new Assignment(new VarRef(stackPointer), new BinaryOp(new VarRef(stackPointer), "-", new NumericLiteral(1)))
                        ), // if we checked for stack overflows, this is where we'd do it
                        new Seq(
                                new Assignment(new HeapAccess(stackName(), new VarRef(stackPointer)), new VarRef(value)),
                                new Assignment(new HeapAccess(stackName(), new NumericLiteral(stackTop())), new VarRef(stackPointer))
                        )
                )
        );
    }

    private void popValueFromStack(String value) {
        final String stackPointer = nextTemp();
        visit(
                new Seq(
                        new Seq(
                                new Assignment(new VarRef(stackPointer), new HeapAccess(stackName(), new NumericLiteral(stackTop()))),
                                new Assignment(new VarRef(value), new HeapAccess(stackName(), new VarRef(stackPointer)))
                        ), // if we checked for stack underflow, this is where we'd do it
                        new Seq(
                                new Assignment(new VarRef(stackPointer), new BinaryOp(new VarRef(stackPointer), "+", new NumericLiteral(1))),
                                new Assignment(new HeapAccess(stackName(), new NumericLiteral(stackTop())), new VarRef(stackPointer))
                        )
                )
        );
    }

    private int stackTop() {
        return allocatedStack.getLast();
    }

    private String stackName() {
        return allocatedStack.getName();
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        final String left = visit(node.getLeft());
        final String right = visit(node.getRight());

        final String tmp = nextTemp();
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
        return node.getName();
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        final String tmp = nextTemp();
        pipeline.emit(createInstruction(SET, tmp, "\"" + node.getText().replaceAll("\"", "\\\"") + "\""));
        return tmp;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextTemp();
        pipeline.emit(createInstruction(SET, tmp, node.getLiteral()));
        return tmp;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        final String target = visit(node.getTarget());
        final String prop = visit(node.getProperty());
        final String tmp = nextTemp();
        pipeline.emit(createInstruction(SENSOR, tmp, target, prop));
        return tmp;
    }

    @Override
    public String visitCaseExpression(CaseExpression node) {
        final String resultVar = nextTemp();
        final String exitLabel = nextLabel();

        final String caseValue = visit(node.getCondition());
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final String nextAlt = nextLabel();         // Next alternative
            final String bodyLabel = nextLabel();       // Body of this alternative

            // Each mathing value, including the last one, causes a jump to the when body
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
        if (allocatedStack == null) {
            throw new MissingStackException("Cannot declare functions when no stack was allocated");
        }

        declaredFunctions.put(node.getName(), node);
        functionLabels.put(node.getName(), nextLabel());
        return "null";
    }

    @Override
    public String visitStackAllocation(StackAllocation node) {
        if (allocatedStack != null) {
            throw new DuplicateStackAllocationException("Found a a 2nd stack allocation in " + node);
        }

        allocatedStack = node;
        return visit(
                new Assignment(
                        new HeapAccess(
                                node.getName(),
                                new NumericLiteral(node.getLast())),
                        new NumericLiteral(node.getLast())
                )
        );
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
        // TODO: version-specific function mapping
        final String target = visit(node.getTarget());

        final List<String> args = new ArrayList<>();
        args.add(node.getProperty());
        args.add(target);
        for (final AstNode param : node.getParams()) {
            final String arg = visit(param);
            args.add(arg);
        }

        pipeline.emit(createInstruction(CONTROL, args));
        return "null";
    }

    private String translateUnaryOpToCode(String op) {
        switch (op) {
            case "not":
            case "!":
                return "not";

            default:
                throw new GenerationException("Could not optimize unary op [" + op + "]");
        }
    }

    private String translateBinaryOpToCode(String op) {
        switch (op) {
            case "+":
                return "add";

            case "-":
                return "sub";

            case "*":
                return "mul";

            case "/":
                return "div";

            case "\\":
                return "idiv";

            case "==":
                return "equal";

            case "!=":
                return "notEqual";

            case "<":
                return "lessThan";

            case "<=":
                return "lessThanEq";

            case ">=":
                return "greaterThanEq";

            case ">":
                return "greaterThan";

            case "===":
                return "strictEqual";

            case "**":
                return "pow";

            case "||":
            case "or":
                return "or";

            case "&&":
            case "and":
                return "land"; // logical-and

            case "%":
                return "mod";

            case "<<":
                return "shl";

            case ">>":
                return "shr";

            case "&":
                return "and";

            case "|":
                return "or";

            case "^":
                return "xor";

            default:
                throw new GenerationException("Failed to translate binary op to word: [" + op + "] is not handled");
        }
    }

    private String nextLabel() {
        return instructionProcessor.nextLabel();
    }

    private String nextTemp() {
        return instructionProcessor.nextTemp();
    }
}
