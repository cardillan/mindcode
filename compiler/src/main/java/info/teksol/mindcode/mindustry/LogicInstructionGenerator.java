package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import info.teksol.mindcode.ast.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mindcode.mindustry.Opcode.*;
import info.teksol.mindcode.mindustry.optimisation.DebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.DiffDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.NullDebugPrinter;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<String> {
    public static final String TMP_PREFIX = "__tmp";

    private final LogicInstructionPipeline pipeline;
    private int tmpIndex;
    private int labelIndex;
    private StackAllocation allocatedStack;
    private final Map<String, FunctionDeclaration> declaredFunctions = new HashMap<>();
    private final Map<String, String> functionLabels = new HashMap<>();
    private final LoopStack loopStack = new LoopStack();

    LogicInstructionGenerator(LogicInstructionPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public static List<LogicInstruction> generateAndOptimize(Seq program, CompileProfile profile,
            Consumer<String> messageConsumer) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        final DebugPrinter debugPrinter = profile.getDebugLevel() == 0 
                ? new NullDebugPrinter() : new DiffDebugPrinter(profile.getDebugLevel());
        LogicInstructionPipeline pipeline = Optimisation.createPipelineForProfile(terminus, profile, debugPrinter, messageConsumer);
        LogicInstructionGenerator generator = new LogicInstructionGenerator(pipeline);
        generator.start(program);
        pipeline.flush();

        debugPrinter.print(messageConsumer);
        
        return terminus.getResult();
    }

    public static List<LogicInstruction> generateAndOptimize(Seq program) {
        return generateAndOptimize(program, CompileProfile.fullOptimizations(), s -> {});
    }

    public static void generateInto(LogicInstructionPipeline pipeline, Seq program) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator(pipeline);
        generator.start(program);
        pipeline.flush();
    }

    public static List<LogicInstruction> generateUnoptimized(Seq program) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();

        LogicInstructionGenerator generator = new LogicInstructionGenerator(terminus);
        generator.start(program);
        terminus.flush();

        return terminus.getResult();
    }

    private void start(Seq program) {
        visit(program);
        appendFunctionDeclarations();
    }

    private void appendFunctionDeclarations() {
        // TODO: pass parameters through local variables? This would save a ton of instructions when going through the stack

        pipeline.emit(new LogicInstruction(END));
        for (Map.Entry<String, FunctionDeclaration> pair : declaredFunctions.entrySet()) {
            final String label = functionLabels.get(pair.getKey());
            pipeline.emit(new LogicInstruction(Opcode.LABEL, label));
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
            pipeline.emit(new LogicInstruction(SET, "@counter", returnAddress));
            pipeline.emit(new LogicInstruction(END));
        }

    }

    @Override
    public String visitHeapAccess(HeapAccess node) {
        final String addr = visit(node.getAddress());
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(READ, tmp, node.getCellName(), addr));
        return tmp;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        final String cond = visit(node.getCondition());

        final String tmp = nextTemp();
        final String elseBranch = nextLabel();
        final String endBranch = nextLabel();

        pipeline.emit(new LogicInstruction(JUMP, elseBranch, "equal", cond, "false"));

        final String trueBranch = visit(node.getTrueBranch());
        pipeline.emit(new LogicInstruction(SET, tmp, trueBranch));
        pipeline.emit(new LogicInstruction(JUMP, endBranch, "always"));

        pipeline.emit(new LogicInstruction(LABEL, elseBranch));
        final String falseBranch = visit(node.getFalseBranch());
        pipeline.emit(new LogicInstruction(SET, tmp, falseBranch));
        pipeline.emit(new LogicInstruction(LABEL, endBranch));

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
            pipeline.emit(new LogicInstruction(Opcode.WRITE, rvalue, heapAccess.getCellName(), address));
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final String propTarget = visit(propertyAccess.getTarget());
            String prop = visit(propertyAccess.getProperty());
            if (prop.startsWith("@")) prop = prop.replaceFirst("@", "");
            pipeline.emit(new LogicInstruction(Opcode.CONTROL, prop, propTarget, rvalue));
        } else if (node.getVar() instanceof VarRef) {
            final String target = visit(node.getVar());
            pipeline.emit(new LogicInstruction(Opcode.SET, List.of(target, rvalue)));
        } else {
            throw new GenerationException("Unhandled assignment target in " + node);
        }

        return rvalue;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        final String expression = visit(node.getExpression());

        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, List.of(translateUnaryOpToCode(node.getOp()), tmp, expression)));
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
            pipeline.emit(new LogicInstruction(SET, iterator, nextValueLabel));
            pipeline.emit(new LogicInstruction(SET, variable, visit(values.get(i))));
            pipeline.emit(new LogicInstruction(JUMP, bodyLabel, "always"));
            pipeline.emit(new LogicInstruction(LABEL, nextValueLabel));
        }

        // Last value
        pipeline.emit(new LogicInstruction(SET, iterator, exitLabel));
        pipeline.emit(new LogicInstruction(SET, variable, visit(values.get(values.size() - 1))));

        pipeline.emit(new LogicInstruction(LABEL, bodyLabel));
        visit(node.getBody());

        // Label for continue statements
        pipeline.emit(new LogicInstruction(LABEL, contLabel));
        pipeline.emit(new LogicInstruction(SET, "@counter", iterator));
        pipeline.emit(new LogicInstruction(LABEL, exitLabel));
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
        pipeline.emit(new LogicInstruction(LABEL, List.of(beginLabel)));
        final String cond = visit(node.getCondition());
        pipeline.emit(new LogicInstruction(JUMP, List.of(doneLabel, "equal", cond, "false")));
        visit(node.getBody());
        pipeline.emit(new LogicInstruction(LABEL, List.of(continueLabel)));
        visit(node.getUpdate());
        pipeline.emit(new LogicInstruction(JUMP, List.of(beginLabel, "always")));
        pipeline.emit(new LogicInstruction(LABEL, List.of(doneLabel)));
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
        pipeline.emit(new LogicInstruction(LABEL, beginLabel));
        visit(node.getBody());
        pipeline.emit(new LogicInstruction(LABEL, continueLabel));
        final String cond = visit(node.getCondition());
        pipeline.emit(new LogicInstruction(JUMP, beginLabel, "notEqual", cond, "false"));
        pipeline.emit(new LogicInstruction(LABEL, doneLabel));
        loopStack.exitLoop(node.getLabel());
        return "null";
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        final List<String> params = node.getParams().stream().map(this::visit).collect(Collectors.toList());
        return handleFunctionCall(node.getFunctionName(), params);
    }

    private String handleFunctionCall(String functionName, List<String> params) {
        switch (functionName) {
            case "print":
                return handlePrint(params);

            case "printflush":
                return handlePrintflush(params);

            case "wait":
                return handleWait(params);

            case "ubind":
                return handleUbind(params);

            case "move":
                return handleMove(params);

            case "rand":
                return handleRand(params);

            case "getlink":
                return handleGetlink(params);

            case "radar":
                return handleRadar(params);

            case "mine":
                return handleMine(params);

            case "itemDrop":
                return handleItemDrop(params);

            case "itemTake":
                return handleItemTake(params);

            case "flag":
                return handleFlag(params);

            case "approach":
                return handleApproach(params);

            case "idle":
                return handleIdle();

            case "pathfind":
                return handlePathfind();

            case "stop":
                return handleStop();

            case "boost":
                return handleBoost(params);

            case "target":
                return handleTarget(params);

            case "targetp":
                return handleTargetp(params);

            case "payDrop":
                return handlePayDrop();

            case "payTake":
                return handlePayTake(params);

            case "build":
                return handleBuild(params);

            case "getBlock":
                return handleGetBlock(params);

            case "within":
                return handleWithin(params);

            case "tan":
            case "sin":
            case "cos":
            case "log":
            case "abs":
            case "floor":
            case "ceil":
                return handleMath(functionName, params);

            case "clear":
                return handleClear(params);

            case "color":
                return handleColor(params);

            case "stroke":
                return handleStroke(params);

            case "line":
                return handleLine(params);
            case "rect":
                return handleRect(params);

            case "lineRect":
                return handleLineRect(params);

            case "poly":
                return handlePoly(params);

            case "linePoly":
                return handleLinePoly(params);

            case "triangle":
                return handleTriangle(params);

            case "image":
                return handleImage(params);

            case "drawflush":
                return handleDrawflush(params);

            case "uradar":
                return handleURadar(params);

            case "ulocate":
                return handleULocate(params);

            case "end":
                return handleEnd();

            case "sqrt":
                return handleSqrt(params);

            case "min":
                return handleMin(params);

            case "max":
                return handleMax(params);

            case "len":
                return handleLen(params);

            case "angle":
                return handleAngle(params);

            case "log10":
                return handleLog10(params);

            case "noise":
                return handleNoise(params);

            default:
                if (declaredFunctions.containsKey(functionName)) {
                    return handleInternalFunctionCall(functionName, params);
                } else {
                    throw new UndeclaredFunctionException("Don't know how to handle function named [" + functionName + "]");
                }
        }
    }

    private String handleNoise(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "noise", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleLog10(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "log10", tmp, params.get(0)));
        return tmp;
    }

    private String handleAngle(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "angle", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleLen(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "len", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleMax(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "max", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleMin(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "min", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleSqrt(List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "sqrt", tmp, params.get(0)));
        return tmp;
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
        pipeline.emit(new LogicInstruction(SET, "@counter", functionLabels.get(functionName))); // actually call function
        pipeline.emit(new LogicInstruction(LABEL, returnLabel)); // where the function must return

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

    private String handleEnd() {
        pipeline.emit(new LogicInstruction(END));
        return "null";
    }

    private String handleULocate(List<String> params) {
        /*
            found = ulocate(ore, @surge-alloy, outx, outy)
                    ulocate ore core true @surge-alloy outx outy found building

            found = ulocate(building, core, ENEMY, outx, outy, outbuilding)
                    ulocate building core true @copper outx outy found building

            found = ulocate(spawn, outx, outy, outbuilding)
                    ulocate spawn core true @copper outx outy found building

            found = ulocate(damaged, outx, outy, outbuilding)
                    ulocate damaged core true @copper outx outy found building
        */
        final String tmp = nextTemp();
        switch (params.get(0)) {
            case "ore":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(ore) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(new LogicInstruction(ULOCATE, "ore", "core", "true", params.get(1), params.get(2), params.get(3), tmp, nextTemp()));
                break;
            case "building":
                if (params.size() < 6) {
                    throw new InsufficientArgumentsException("ulocate(building) requires 6 arguments, received " + params.size());
                }

                pipeline.emit(new LogicInstruction(ULOCATE, "building", params.get(1), params.get(2), "@copper", params.get(3), params.get(4), tmp, params.get(5)));
                break;

            case "spawn":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(spawn) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(new LogicInstruction(ULOCATE, "spawn", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            case "damaged":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(damaged) requires 4 arguments, received " + params.size());
                }

                pipeline.emit(new LogicInstruction(ULOCATE, "damaged", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            default:
                throw new GenerationException("Unhandled type of ulocate in " + params);
        }

        return tmp;
    }

    private String handleURadar(List<String> params) {
        // uradar enemy attacker ground armor 0 order result
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(URADAR, params.get(0), params.get(1), params.get(2), params.get(3), "0", params.get(4), tmp));
        return tmp;
    }

    private String handleDrawflush(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAWFLUSH, params.get(0)));
        return params.get(0);
    }

    private String handleImage(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "image", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleTriangle(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "triangle", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5)));
        return "null";
    }

    private String handleLinePoly(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "linePoly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePoly(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "poly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleLineRect(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "lineRect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleRect(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "rect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleLine(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "line", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleStroke(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "stroke", params.get(0)));
        return "null";
    }

    private String handleColor(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "color", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleClear(List<String> params) {
        pipeline.emit(new LogicInstruction(DRAW, "clear", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleMath(String functionName, List<String> params) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, functionName, tmp, params.get(0)));
        return tmp;
    }

    private String handleWithin(List<String> params) {
        // ucontrol within x y radius result 0
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(UCONTROL, "within", params.get(0), params.get(1), params.get(2), tmp));
        return tmp;
    }

    private String handleGetBlock(List<String> params) {
        // ucontrol getBlock x y resultType resultBuilding 0
        // TODO: either handle multiple return values, or provide a better abstraction over getBlock
        pipeline.emit(new LogicInstruction(UCONTROL, "getBlock", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleBuild(List<String> params) {
        // ucontrol build x y block rotation config
        pipeline.emit(new LogicInstruction(UCONTROL, "build", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePayTake(List<String> params) {
        // ucontrol payTake takeUnits 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "payTake", params.get(0)));
        return "null";
    }

    private String handlePayDrop() {
        // ucontrol payDrop 0 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "payDrop"));
        return "null";
    }

    private String handleItemTake(List<String> params) {
        // ucontrol itemTake from item amount 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "itemTake", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleTargetp(List<String> params) {
        // ucontrol targetp unit shoot 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "targetp", params.get(0), params.get(1)));
        return "null";
    }

    private String handleTarget(List<String> params) {
        // ucontrol target x y shoot 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "target", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleBoost(List<String> params) {
        // ucontrol boost enable 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "boost", params.get(0)));
        return params.get(0);
    }

    private String handlePathfind() {
        // ucontrol pathfind 0 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "pathfind"));
        return "null";
    }

    private String handleIdle() {
        // ucontrol idle 0 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "idle"));
        return "null";
    }

    private String handleStop() {
        // ucontrol stop 0 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "stop"));
        return "null";
    }

    private String handleApproach(List<String> params) {
        // ucontrol approach x y radius 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "approach", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleFlag(List<String> params) {
        // ucontrol flag value 0 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "flag", params.get(0)));
        return params.get(0);
    }

    private String handleItemDrop(List<String> params) {
        // ucontrol itemDrop to amount 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "itemDrop", params.get(0), params.get(1)));
        return "null";
    }

    private String handleMine(List<String> params) {
        // ucontrol mine x y 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "mine", params.get(0), params.get(1)));
        return "null";
    }

    private String handleGetlink(List<String> params) {
        // getlink result 0
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(GETLINK, tmp, params.get(0)));
        return tmp;
    }

    private boolean isRadarSearchProperty(String prop) {
        return List.of("attacker", "enemy", "ally", "player", "flying", "ground", "boss", "any").contains(prop);
    }
    private boolean isRadarSortbyOption(String sortby) {
        return List.of("distance", "health", "shield", "armor", "maxHealth").contains(sortby);
    }

    private String handleRadar(List<String> params) {
        // radar prop1 prop2 prop3 sortby target order out
        final String tmp = nextTemp();
        final String prop1 = params.get(0);
        final String prop2 = params.get(1);
        final String prop3 = params.get(2);
        final String sortby = params.get(3);
        // Radar search properties should be hardcoded and can't be indirectly referenced. (Last test: v7.0.1.)
        if (!isRadarSearchProperty(prop1)) {
            throw new GenerationException("Invalid radar search property [" + prop1 + "]");
        }
        if (!isRadarSearchProperty(prop2)) {
            throw new GenerationException("Invalid radar search property [" + prop2 + "]");
        }
        if (!isRadarSearchProperty(prop3)) {
            throw new GenerationException("Invalid radar search property [" + prop3 + "]");
        }
        if (!isRadarSortbyOption(sortby)) {
            throw new GenerationException("Invalid radar sort option [" + sortby + "]");
        }
        pipeline.emit(new LogicInstruction(RADAR, params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), tmp));
        return tmp;
    }

    private String handleRand(List<String> params) {
        // op rand result 200 0
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(OP, "rand", tmp, params.get(0)));
        return tmp;
    }

    private String handleMove(List<String> params) {
        // ucontrol move 14 15 0 0 0
        pipeline.emit(new LogicInstruction(UCONTROL, "move", params.get(0), params.get(1)));
        return "null";
    }

    private String handleUbind(List<String> params) {
        // ubind @poly
        pipeline.emit(new LogicInstruction(UBIND, params.get(0)));
        return "null";
    }

    private String handlePrintflush(List<String> params) {
        params.forEach((param) -> pipeline.emit(new LogicInstruction(PRINTFLUSH, List.of(param))));
        return "null";
    }

    private String handlePrint(List<String> params) {
        params.forEach((param) -> pipeline.emit(new LogicInstruction(PRINT, List.of(param))));
        return params.get(params.size() - 1);
    }

    private String handleWait(List<String> params) {
        pipeline.emit(new LogicInstruction(WAIT, params.get(0)));
        return "null";
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        final String left = visit(node.getLeft());
        final String right = visit(node.getRight());

        final String tmp = nextTemp();
        pipeline.emit(
                new LogicInstruction(
                        OP,
                        List.of(translateBinaryOpToCode(node.getOp()), tmp, left, right)
                )
        );

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
        pipeline.emit(
                new LogicInstruction(
                        SET,
                        List.of(
                                tmp,
                                "\"" + node.getText().replaceAll("\"", "\\\"") + "\""
                        )
                )
        );
        return tmp;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(SET, List.of(tmp, node.getLiteral())));
        return tmp;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        final String target = visit(node.getTarget());
        final String prop = visit(node.getProperty());
        final String tmp = nextTemp();
        pipeline.emit(new LogicInstruction(SENSOR, tmp, target, prop));
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
                    pipeline.emit(new LogicInstruction(JUMP, nextExp, translateBinaryOpToCode("<"), caseValue, minValue));
                    // The max value is only evaluated when the min value lets us through
                    final String maxValue = visit(range.getLastValue());
                    pipeline.emit(new LogicInstruction(JUMP, bodyLabel, translateBinaryOpToCode(range.maxValueComparison()), caseValue, maxValue));
                    pipeline.emit(new LogicInstruction(LABEL, nextExp));
                }
                else {
                    final String whenValue = visit(value);
                    pipeline.emit(new LogicInstruction(JUMP, bodyLabel, "equal", caseValue, whenValue));
                }
            }

            // No match in the when value list: skip to the next alternative
            pipeline.emit(new LogicInstruction(JUMP, nextAlt, "always"));

            // Body of the alternative
            pipeline.emit(new LogicInstruction(LABEL, bodyLabel));
            final String body = visit(alternative.getBody());
            pipeline.emit(new LogicInstruction(SET, resultVar, body));
            pipeline.emit(new LogicInstruction(JUMP, exitLabel, "always"));

            pipeline.emit(new LogicInstruction(LABEL, nextAlt));
        }

        final String elseBranch = visit(node.getElseBranch());
        pipeline.emit(new LogicInstruction(SET, resultVar, elseBranch));
        pipeline.emit(new LogicInstruction(LABEL, exitLabel));

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
        pipeline.emit(new LogicInstruction(JUMP, label, "always"));
        return "null";
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        final String label = loopStack.getContinueLabel(node.getLabel());
        pipeline.emit(new LogicInstruction(JUMP, label, "always"));
        return "null";
    }

    @Override
    public String visitControl(Control node) {
        final String target = visit(node.getTarget());

        final List<String> args = new ArrayList<>();
        args.add(node.getProperty());
        args.add(target);
        for (final AstNode param : node.getParams()) {
            final String arg = visit(param);
            args.add(arg);
        }

        pipeline.emit(new LogicInstruction(CONTROL, args));
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
        return "__label" + labelIndex++;
    }

    private String nextTemp() {
        return TMP_PREFIX + this.tmpIndex++;
    }
}
