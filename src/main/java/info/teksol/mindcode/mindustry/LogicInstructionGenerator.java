package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;
import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<Tuple2<String, List<LogicInstruction>>> {
    private int tmp;
    private int label;

    private StackAllocation allocatedStack;
    private Map<String, FunctionDeclaration> declaredFunctions = new HashMap<>();
    private Map<String, String> functionLabels = new HashMap<>();

    public static List<LogicInstruction> generateFrom(Seq program) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator();
        final Tuple2<String, List<LogicInstruction>> instructions = generator.start(program);
        final List<LogicInstruction> result = new ArrayList<>(instructions._2);
        return result;
    }

    private Tuple2<String, List<LogicInstruction>> start(Seq program) {
        final Tuple2<String, List<LogicInstruction>> target = visit(program);
        appendFunctionDeclarations(target._2);
        return target;
    }

    private void appendFunctionDeclarations(List<LogicInstruction> result) {
        // TODO: pass parameters through local variables? This would save a ton of instructions when going through the stack

        result.add(new LogicInstruction("end"));
        for (Map.Entry<String, FunctionDeclaration> pair : declaredFunctions.entrySet()) {
            final String label = functionLabels.get(pair.getKey());
            result.add(new LogicInstruction("label", label));
            // caller pushes arguments left-to-right
            // we have to pop right-to-left, hence the reverse iteration here
            final ListIterator<AstNode> iterator = pair.getValue().getParams().listIterator(pair.getValue().getParams().size());
            while (iterator.hasPrevious()) {
                final AstNode var = iterator.previous();
                final String param = nextTemp();
                popValueFromStack(param, result);
                final Tuple2<String, List<LogicInstruction>> assignParam = visit(new Assignment(var, new VarRef(param)));
                result.addAll(assignParam._2);
            }

            final Tuple2<String, List<LogicInstruction>> body = visit(pair.getValue().getBody());
            result.addAll(body._2);
            final String returnAddress = nextTemp();
            popValueFromStack(returnAddress, result);
            pushValueOnStack(body._1, result);
            result.add(new LogicInstruction("set", "@counter", returnAddress));
            result.add(new LogicInstruction("end"));
        }

    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitHeapAccess(HeapAccess node) {
        final Tuple2<String, List<LogicInstruction>> addr = visit(node.getAddress());
        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(addr._2);
        result.add(new LogicInstruction("read", tmp, node.getCellName(), addr._1));
        return new Tuple2<>(tmp, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitComment(Comment node) {
        return new Tuple2<>("null", List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitIfExpression(IfExpression node) {
        final Tuple2<String, List<LogicInstruction>> cond = visit(node.getCondition());
        final Tuple2<String, List<LogicInstruction>> trueBranch = visit(node.getTrueBranch());
        final Tuple2<String, List<LogicInstruction>> falseBranch = visit(node.getFalseBranch());

        final String tmp = nextTemp();
        final String elseBranch = nextLabel();
        final String endBranch = nextLabel();

        final List<LogicInstruction> result = new ArrayList<>(cond._2);
        result.add(new LogicInstruction("jump", elseBranch, "notEqual", cond._1, "true"));
        result.addAll(trueBranch._2);
        result.add(new LogicInstruction("set", tmp, trueBranch._1));
        result.add(new LogicInstruction("jump", endBranch, "always"));
        result.add(new LogicInstruction("label", elseBranch));
        result.addAll(falseBranch._2);
        result.add(new LogicInstruction("set", tmp, falseBranch._1));
        result.add(new LogicInstruction("label", endBranch));

        return new Tuple2<>(tmp, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitSeq(Seq seq) {
        final Tuple2<String, List<LogicInstruction>> rest = visit(seq.getRest());
        final Tuple2<String, List<LogicInstruction>> last = visit(seq.getLast());
        final List<LogicInstruction> result = new ArrayList<>();
        result.addAll(rest._2);
        result.addAll(last._2);

        return new Tuple2<>(last._1, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitNoOp(NoOp node) {
        return new Tuple2<>("null", List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitAssignment(Assignment node) {
        final Tuple2<String, List<LogicInstruction>> target = visit(node.getVar());
        final Tuple2<String, List<LogicInstruction>> rvalue = visit(node.getValue());
        final List<LogicInstruction> result = new ArrayList<>(rvalue._2);

        if (node.getVar() instanceof HeapAccess) {
            final HeapAccess heapAccess = (HeapAccess) node.getVar();
            final Tuple2<String, List<LogicInstruction>> address = visit(heapAccess.getAddress());
            result.addAll(address._2);
            result.add(new LogicInstruction("write", rvalue._1, heapAccess.getCellName(), address._1));
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final Tuple2<String, List<LogicInstruction>> propTarget = visit(propertyAccess.getTarget());
            result.addAll(propTarget._2);
            result.add(new LogicInstruction("control", propertyAccess.getProperty(), propTarget._1, rvalue._1));
        } else if (node.getVar() instanceof VarRef) {
            result.add(new LogicInstruction("set", List.of(target._1, rvalue._1)));
        } else {
            throw new GenerationException("Unhandled assignment target in " + node);
        }

        return new Tuple2<>(rvalue._1, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitUnaryOp(UnaryOp node) {
        final Tuple2<String, List<LogicInstruction>> expression = visit(node.getExpression());

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(expression._2);
        result.add(new LogicInstruction("op", List.of(translateUnaryOpToCode(node.getOp()), tmp, expression._1)));
        return new Tuple2<>(tmp, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitWhileStatement(WhileExpression node) {
        final Tuple2<String, List<LogicInstruction>> cond = visit(node.getCondition());
        final Tuple2<String, List<LogicInstruction>> body = visit(node.getBody());

        final List<LogicInstruction> result = new ArrayList<>();
        final String condLabel = nextLabel();
        final String doneLabel = nextLabel();
        result.add(new LogicInstruction("label", List.of(condLabel)));
        result.addAll(cond._2);
        result.add(new LogicInstruction("jump", List.of(doneLabel, "notEqual", cond._1, "true")));
        result.addAll(body._2);
        result.add(new LogicInstruction("jump", List.of(condLabel, "always")));
        result.add(new LogicInstruction("label", List.of(doneLabel)));

        return new Tuple2<>(body._1, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitFunctionCall(FunctionCall node) {
        final List<Tuple2<String, List<LogicInstruction>>> params =
                node.getParams().stream().map(this::visit).collect(Collectors.toList());
        final List<LogicInstruction> result = new ArrayList<>();
        params.forEach((param) -> result.addAll(param._2));
        final String tmp = handleFunctionCall(node.getFunctionName(), params.stream().map(Tuple2::getT1).collect(Collectors.toList()), result);
        return new Tuple2<>(tmp, result);
    }

    private String handleFunctionCall(String functionName, List<String> params, List<LogicInstruction> result) {
        switch (functionName) {
            case "print":
                return handlePrint(params, result);

            case "printflush":
                return handlePrintflush(params, result);

            case "ubind":
                return handleUbind(params, result);

            case "move":
                return handleMove(params, result);

            case "rand":
                return handleRand(params, result);

            case "getlink":
                return handleGetlink(params, result);

            case "mine":
                return handleMine(params, result);

            case "itemDrop":
                return handleItemDrop(params, result);

            case "itemTake":
                return handleItemTake(params, result);

            case "flag":
                return handleFlag(params, result);

            case "approach":
                return handleApproach(params, result);

            case "idle":
                return handleIdle(params, result);

            case "pathfind":
                return handlePathfind(params, result);

            case "stop":
                return handleStop(params, result);

            case "boot":
                return handleBoost(params, result);

            case "target":
                return handleTarget(params, result);

            case "targetp":
                return handleTargetp(params, result);

            case "payDrop":
                return handlePayDrop(params, result);

            case "payTake":
                return handlePayTake(params, result);

            case "build":
                return handleBuild(params, result);

            case "getBlock":
                return handleGetBlock(params, result);

            case "within":
                return handleWithin(params, result);

            case "tan":
            case "sin":
            case "cos":
            case "log":
            case "abs":
            case "floor":
            case "ceil":
                return handleMath(functionName, params, result);

            case "clear":
                return handleClear(params, result);
            case "color":
                return handleColor(params, result);
            case "stroke":
                return handleStroke(params, result);
            case "line":
                return handleLine(params, result);
            case "rect":
                return handleRect(params, result);
            case "lineRect":
                return handleLineRect(params, result);
            case "poly":
                return handlePoly(params, result);
            case "linePoly":
                return handleLinePoly(params, result);
            case "triangle":
                return handleTriangle(params, result);
            case "image":
                return handleImage(params, result);
            case "drawflush":
                return handleDrawflush(params, result);

            case "uradar":
                return handleURadar(params, result);

            case "ulocate":
                return handleULocate(params, result);

            case "end":
                return handleEnd(params, result);

            case "sqrt":
                return handleSqrt(params, result);

            case "min":
                return handleMin(params, result);

            case "max":
                return handleMax(params, result);

            default:
                if (declaredFunctions.containsKey(functionName)) {
                    return handleInternalFunctionCall(functionName, params, result);
                } else {
                    throw new UndeclaredFunctionException("Don't know how to handle function named [" + functionName + "]");
                }
        }
    }

    private String handleMax(List<String> params, List<LogicInstruction> result) {
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", "max", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleMin(List<String> params, List<LogicInstruction> result) {
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", "min", tmp, params.get(0), params.get(1)));
        return tmp;
    }

    private String handleSqrt(List<String> params, List<LogicInstruction> result) {
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", "sqrt", tmp, params.get(0)));
        return tmp;
    }

    private String handleInternalFunctionCall(String functionName, List<String> params, List<LogicInstruction> result) {
        // TODO: assert number of parameters matches expected number from declared function
        // This might require declaring functions before they are used. Otherwise, this would become a runtime exception.
        // It would be preferable to have functions declared early, so that the compiler can check number of parameters.
        final String returnLabel = nextLabel();

        pushValueOnStack(returnLabel, result);
        for (final String param : params) {
            pushValueOnStack(param, result);
        }
        result.add(new LogicInstruction("set", "@counter", functionLabels.get(functionName))); // actually call function
        result.add(new LogicInstruction("label", returnLabel)); // where the function must return

        final String returnValue = nextTemp();
        popValueFromStack(returnValue, result);

        return returnValue;
    }

    private void pushValueOnStack(String value, List<LogicInstruction> result) {
        final String stackPointer = nextTemp();
        Tuple2<String, List<LogicInstruction>> push;

        push = visit(
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

        result.addAll(push._2);
    }

    private void popValueFromStack(String value, List<LogicInstruction> result) {
        final String stackPointer = nextTemp();
        Tuple2<String, List<LogicInstruction>> pop;
        pop = visit(
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

        result.addAll(pop._2);
    }

    private int stackTop() {
        return allocatedStack.getLast();
    }

    private String stackName() {
        return allocatedStack.getName();
    }

    private String handleEnd(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("end"));
        return "null";
    }

    private String handleULocate(List<String> params, List<LogicInstruction> result) {
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

                result.add(new LogicInstruction("ulocate", "ore", "core", "true", params.get(1), params.get(2), params.get(3), tmp, nextTemp()));
                break;
            case "building":
                if (params.size() < 6) {
                    throw new InsufficientArgumentsException("ulocate(building) requires 6 arguments, received " + params.size());
                }

                result.add(new LogicInstruction("ulocate", "building", params.get(1), params.get(2), "@copper", params.get(3), params.get(4), tmp, params.get(5)));
                break;

            case "spawn":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(spawn) requires 4 arguments, received " + params.size());
                }

                result.add(new LogicInstruction("ulocate", "spawn", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            case "damaged":
                if (params.size() < 4) {
                    throw new InsufficientArgumentsException("ulocate(damaged) requires 4 arguments, received " + params.size());
                }

                result.add(new LogicInstruction("ulocate", "damaged", "core", "true", "@copper", params.get(1), params.get(2), tmp, params.get(3)));
                break;

            default:
                throw new GenerationException("Unhandled type of ulocate in " + params);
        }

        return tmp;
    }

    private String handleURadar(List<String> params, List<LogicInstruction> result) {
        // uradar enemy attacker ground armor 0 order result
        final String tmp = nextTemp();
        result.add(new LogicInstruction("uradar", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), tmp));
        return tmp;
    }

    private String handleDrawflush(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("drawflush", params.get(0)));
        return params.get(0);
    }

    private String handleImage(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "image", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleTriangle(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "triangle", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5)));
        return "null";
    }

    private String handleLinePoly(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "linePoly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePoly(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "poly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handleLineRect(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "lineRect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleRect(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "rect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleLine(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "line", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleStroke(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "stroke", params.get(0)));
        return "null";
    }

    private String handleColor(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "color", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleClear(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("draw", "clear", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleMath(String functionName, List<String> params, List<LogicInstruction> result) {
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", functionName, tmp, params.get(0)));
        return tmp;
    }

    private String handleWithin(List<String> params, List<LogicInstruction> result) {
        // ucontrol within x y radius result 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("ucontrol", "within", params.get(0), params.get(1), params.get(2), tmp));
        return tmp;
    }

    private String handleGetBlock(List<String> params, List<LogicInstruction> result) {
        // ucontrol getBlock x y resultType resultBuilding 0
        // TODO: either handle multiple return values, or provide a better abstraction over getBlock
        result.add(new LogicInstruction("ucontrol", "getBlock", params.get(0), params.get(1), params.get(2), params.get(3)));
        return "null";
    }

    private String handleBuild(List<String> params, List<LogicInstruction> result) {
        // ucontrol build x y block rotation config
        result.add(new LogicInstruction("ucontrol", "build", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return "null";
    }

    private String handlePayTake(List<String> params, List<LogicInstruction> result) {
        // ucontrol payTake takeUnits 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "payTake", params.get(0)));
        return "null";
    }

    private String handlePayDrop(List<String> params, List<LogicInstruction> result) {
        // ucontrol payDrop 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "payDrop"));
        return "null";
    }

    private String handleItemTake(List<String> params, List<LogicInstruction> result) {
        // ucontrol itemTake from item amount 0 0
        result.add(new LogicInstruction("ucontrol", "itemTake", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleTargetp(List<String> params, List<LogicInstruction> result) {
        // ucontrol targetp unit shoot 0 0 0
        result.add(new LogicInstruction("ucontrol", "targetp", params.get(0), params.get(1)));
        return "null";
    }

    private String handleTarget(List<String> params, List<LogicInstruction> result) {
        // ucontrol target x y shoot 0 0
        result.add(new LogicInstruction("ucontrol", "target", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleBoost(List<String> params, List<LogicInstruction> result) {
        // ucontrol boost enable 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "boost", params.get(1)));
        return params.get(1);
    }

    private String handlePathfind(List<String> params, List<LogicInstruction> result) {
        // ucontrol pathfind 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "pathfind"));
        return "null";
    }

    private String handleIdle(List<String> params, List<LogicInstruction> result) {
        // ucontrol idle 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "idle"));
        return "null";
    }

    private String handleStop(List<String> params, List<LogicInstruction> result) {
        // ucontrol stop 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "stop"));
        return "null";
    }

    private String handleApproach(List<String> params, List<LogicInstruction> result) {
        // ucontrol approach x y radius 0 0
        result.add(new LogicInstruction("ucontrol", "approach", params.get(0), params.get(1), params.get(2)));
        return "null";
    }

    private String handleFlag(List<String> params, List<LogicInstruction> result) {
        // ucontrol flag value 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "flag", params.get(0)));
        return params.get(0);
    }

    private String handleItemDrop(List<String> params, List<LogicInstruction> result) {
        // ucontrol itemDrop to amount 0 0 0
        result.add(new LogicInstruction("ucontrol", "itemDrop", params.get(0), params.get(1)));
        return "null";
    }

    private String handleMine(List<String> params, List<LogicInstruction> result) {
        // ucontrol mine x y 0 0 0
        result.add(new LogicInstruction("ucontrol", "mine", params.get(0), params.get(1)));
        return "null";
    }

    private String handleGetlink(List<String> params, List<LogicInstruction> result) {
        // getlink result 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("getlink", tmp, params.get(0)));
        return tmp;
    }

    private String handleRand(List<String> params, List<LogicInstruction> result) {
        // op rand result 200 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", "rand", tmp, params.get(0)));
        return tmp;
    }

    private String handleMove(List<String> params, List<LogicInstruction> result) {
        // ucontrol move 14 15 0 0 0
        result.add(new LogicInstruction("ucontrol", "move", params.get(0), params.get(1)));
        return "null";
    }

    private String handleUbind(List<String> params, List<LogicInstruction> result) {
        // ubind @poly
        result.add(new LogicInstruction("ubind", params.get(0)));
        return "null";
    }

    private String handlePrintflush(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("printflush", List.of(param))));
        return "null";
    }

    private String handlePrint(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("print", List.of(param))));
        return params.get(params.size() - 1);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitBinaryOp(BinaryOp node) {
        final Tuple2<String, List<LogicInstruction>> left = visit(node.getLeft());
        final Tuple2<String, List<LogicInstruction>> right = visit(node.getRight());

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>();
        result.addAll(left._2);
        result.addAll(right._2);
        result.add(
                new LogicInstruction(
                        "op",
                        List.of(translateBinaryOpToCode(node.getOp()), tmp, left._1, right._1)
                )
        );

        return new Tuple2<>(tmp, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitRef(Ref node) {
    return new Tuple2<>("@" + node.getName(), List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitNullLiteral(NullLiteral node) {
        return new Tuple2<>("null", List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitBooleanLiteral(BooleanLiteral node) {
        return new Tuple2<>(String.valueOf(node.getValue()), List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitVarRef(VarRef node) {
        return new Tuple2<>(
                node.getName(),
                List.of()
        );
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitStringLiteral(StringLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                tmp,
                List.of(
                        new LogicInstruction(
                                "set",
                                List.of(
                                        tmp,
                                        "\"" + node.getText().replaceAll("\"", "\\\"") + "\""
                                )
                        )
                )
        );
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                tmp,
                List.of(new LogicInstruction("set", List.of(tmp, node.getLiteral()))
                )
        );
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitPropertyAccess(PropertyAccess node) {
        final Tuple2<String, List<LogicInstruction>> target = visit(node.getTarget());
        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(target._2);
        result.add(new LogicInstruction("sensor", tmp, target._1, "@" + node.getProperty()));
        return new Tuple2<>(tmp, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitCaseExpression(CaseExpression node) {
        final Tuple2<String, List<LogicInstruction>> cond = visit(node.getCondition());
        final String condVar = nextTemp();
        final String resultVar = nextTemp();
        final String exitLabel = nextLabel();

        final List<LogicInstruction> result = new ArrayList<>(cond._2);
        result.add(new LogicInstruction("set", condVar, cond._1));
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final Tuple2<String, List<LogicInstruction>> altValue = visit(alternative.getValue());
            final Tuple2<String, List<LogicInstruction>> body = visit(alternative.getBody());
            final String nextCond = nextLabel();

            result.addAll(altValue._2);
            result.add(new LogicInstruction("jump", nextCond, "notEqual", condVar, altValue._1));
            result.addAll(body._2);
            result.add(new LogicInstruction("set", resultVar, body._1));
            result.add(new LogicInstruction("jump", exitLabel, "always"));
            result.add(new LogicInstruction("label", nextCond));
        }


        final Tuple2<String, List<LogicInstruction>> elseBranch = visit(node.getElseBranch());
        result.addAll(elseBranch._2);
        result.add(new LogicInstruction("set", resultVar, elseBranch._1));
        result.add(new LogicInstruction("label", exitLabel));

        return new Tuple2<>(resultVar, result);
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitFunctionDeclaration(FunctionDeclaration node) {
        if (allocatedStack == null) {
            throw new MissingStackException("Cannot declare functions when no stack was allocated");
        }

        declaredFunctions.put(node.getName(), node);
        functionLabels.put(node.getName(), nextLabel());
        return new Tuple2<>("null", List.of());
    }

    @Override
    public Tuple2<String, List<LogicInstruction>> visitStackAllocation(StackAllocation node) {
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
    public Tuple2<String, List<LogicInstruction>> visitControl(Control node) {
        final Tuple2<String, List<LogicInstruction>> target = visit(node.getTarget());
        final List<LogicInstruction> result = new ArrayList<>(target._2);

        final List<String> args = new ArrayList<>();
        args.add(node.getProperty());
        args.add(target._1);
        for (final AstNode param : node.getParams()) {
            final Tuple2<String, List<LogicInstruction>> arg = visit(param);

            result.addAll(arg._2);
            args.add(arg._1);
        }

        result.add(new LogicInstruction("control", args));
        return new Tuple2<>("null", result);
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
        return "label" + label++;
    }

    private String nextTemp() {
        return "tmp" + tmp++;
    }
}
