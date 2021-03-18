package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;
import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts from the Mindcode AST into a list of Logic instructions.
 * <p>
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<Tuple2<Optional<String>, List<LogicInstruction>>> {
    private int tmp;
    private int label;

    public static List<LogicInstruction> generateFrom(Seq program) {
        final Tuple2<Optional<String>, List<LogicInstruction>> instructions =
                new LogicInstructionGenerator().visit(program);
        final List<LogicInstruction> result = new ArrayList<>(instructions._2);
        result.add(new LogicInstruction("end"));
        return result;
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitHeapAccess(HeapAccess node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> addr = visit(node.getAddress());
        if (!addr._1.isPresent()) {
            throw new GenerationException("Expected to find an rvalue from HeapAccess address, found " + node);
        }

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(addr._2);
        result.add(new LogicInstruction("read", tmp, node.getCellName(), addr._1.get()));
        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitComment(Comment node) {
        return new Tuple2<>(Optional.empty(), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitControl(Control node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> value = visit(node.getValue());
        final List<LogicInstruction> result = new ArrayList<>(value._2);
        if (!value._1.isPresent()) {
            throw new GenerationException("Expected to find tmp variable from control node, found: " + value);
        }

        result.add(new LogicInstruction("control", node.getProperty(), node.getTarget(), value._1.get()));

        return new Tuple2<>(value._1, result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitIfExpression(IfExpression node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> cond = visit(node.getCondition());
        final Tuple2<Optional<String>, List<LogicInstruction>> trueBranch = visit(node.getTrueBranch());
        final Tuple2<Optional<String>, List<LogicInstruction>> falseBranch = visit(node.getFalseBranch());
        if (!cond._1.isPresent()) {
            throw new GenerationException("Expected to receive a value from the cond branch of an if expression; received " + cond);
        }
        if (!trueBranch._1.isPresent()) {
            throw new GenerationException("Expected to receive a value from the true branch of an if expression; received " + trueBranch);
        }

        final String tmp = nextTemp();
        final String elseBranch = nextLabel();
        final String endBranch = nextLabel();

        final List<LogicInstruction> result = new ArrayList<>(cond._2);
        result.add(new LogicInstruction("jump", elseBranch, "notEqual", cond._1.get(), "true"));
        result.addAll(trueBranch._2);
        result.add(new LogicInstruction("set", tmp, trueBranch._1.get()));
        result.add(new LogicInstruction("jump", endBranch, "always"));
        result.add(new LogicInstruction("label", elseBranch));
        result.addAll(falseBranch._2);
        result.add(new LogicInstruction("set", tmp, falseBranch._1.orElse("null")));
        result.add(new LogicInstruction("label", endBranch));

        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitSeq(Seq seq) {
        final Tuple2<Optional<String>, List<LogicInstruction>> rest = visit(seq.getRest());
        final Tuple2<Optional<String>, List<LogicInstruction>> last = visit(seq.getLast());
        final List<LogicInstruction> result = new ArrayList<>();
        result.addAll(rest._2);
        result.addAll(last._2);

        return new Tuple2<>(last._1, result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitNoOp(NoOp node) {
        return new Tuple2<>(Optional.of("null"), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitAssignment(Assignment node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> target = visit(node.getVar());
        final Tuple2<Optional<String>, List<LogicInstruction>> rvalue = visit(node.getValue());
        final List<LogicInstruction> result = new ArrayList<>(rvalue._2);
        if (!target._1.isPresent()) {
            throw new GenerationException("Expected a target from an assignment, found none in " + target);
        }

        if (!rvalue._1.isPresent()) {
            throw new GenerationException("Expected a variable name, found none in " + result);
        }

        if (node.getVar() instanceof HeapAccess) {
            final HeapAccess heapAccess = (HeapAccess) node.getVar();
            final Tuple2<Optional<String>, List<LogicInstruction>> address = visit(heapAccess.getAddress());
            if (!address._1.isPresent()) {
                throw new GenerationException("Expected to find variable from address calculation in " + node);
            }
            result.addAll(address._2);
            result.add(new LogicInstruction("write", rvalue._1.get(), heapAccess.getCellName(), address._1.get()));
        } else if (node.getVar() instanceof PropertyAccess) {
            final PropertyAccess propertyAccess = (PropertyAccess) node.getVar();
            final Tuple2<Optional<String>, List<LogicInstruction>> propTarget = visit(propertyAccess.getTarget());
            if (!propTarget._1.isPresent()) {
                throw new GenerationException("Expected to find variable from address calculation in " + node);
            }
            result.addAll(propTarget._2);
            result.add(new LogicInstruction("control", propertyAccess.getProperty(), propTarget._1.get(), rvalue._1.get()));
        } else if (node.getVar() instanceof VarRef) {
            result.add(new LogicInstruction("set", List.of(target._1.get(), rvalue._1.get())));
        } else {
            throw new GenerationException("Unhandled assignment target in " + node);
        }

        return new Tuple2<>(rvalue._1, result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitUnaryOp(UnaryOp node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> expression = visit(node.getExpression());
        if (!expression._1.isPresent()) {
            throw new GenerationException("Expected to have a variable in " + expression);
        }

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(expression._2);
        result.add(new LogicInstruction("op", List.of(translateUnaryOpToCode(node.getOp()), tmp, expression._1.get())));
        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitWhileStatement(WhileStatement node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> cond = visit(node.getCondition());
        if (!cond._1.isPresent()) {
            throw new GenerationException("Expected a variable name for the while condition, found none in " + cond);
        }

        final Tuple2<Optional<String>, List<LogicInstruction>> body = visit(node.getBody());

        final List<LogicInstruction> result = new ArrayList<>();
        final String condLabel = nextLabel();
        final String doneLabel = nextLabel();
        result.add(new LogicInstruction("label", List.of(condLabel)));
        result.addAll(cond._2);
        result.add(new LogicInstruction("jump", List.of(doneLabel, "notEqual", cond._1.get(), "true")));
        result.addAll(body._2);
        result.add(new LogicInstruction("jump", List.of(condLabel, "always")));
        result.add(new LogicInstruction("label", List.of(doneLabel)));

        return new Tuple2<>(body._1, result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitFunctionCall(FunctionCall node) {
        final List<Tuple2<Optional<String>, List<LogicInstruction>>> params =
                node.getParams().stream().map(this::visit).collect(Collectors.toList());
        final List<LogicInstruction> result = new ArrayList<>();
        if (!params.stream().allMatch((param) -> param._1.isPresent())) {
            throw new GenerationException("Expected all parameters to function calls to return values, found " + params);
        }

        params.forEach((param) -> result.addAll(param._2));
        final Optional<String> tmp = handleFunctionCall(node.getFunctionName(), params.stream().map(Tuple2::getT1).map(Optional::get).collect(Collectors.toList()), result);
        return new Tuple2<>(tmp, result);
    }

    private Optional<String> handleFunctionCall(String functionName, List<String> params, List<LogicInstruction> result) {
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

            default:
                throw new GenerationException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private Optional<String> handleDrawflush(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("drawflush", params.get(0)));
        return Optional.of(params.get(0));
    }

    private Optional<String> handleImage(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("image", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return Optional.of("null");
    }

    private Optional<String> handleTriangle(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("triangle", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5)));
        return Optional.of("null");
    }

    private Optional<String> handleLinePoly(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("linePoly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return Optional.of("null");
    }

    private Optional<String> handlePoly(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("poly", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return Optional.of("null");
    }

    private Optional<String> handleLineRect(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("lineRect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return Optional.of("null");
    }

    private Optional<String> handleRect(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("rect", params.get(0), params.get(1), params.get(2), params.get(3)));
        return Optional.of("null");
    }

    private Optional<String> handleLine(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("line", params.get(0), params.get(1), params.get(2), params.get(3)));
        return Optional.of("null");
    }

    private Optional<String> handleStroke(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("stroke", params.get(0)));
        return Optional.of("null");
    }

    private Optional<String> handleColor(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("color", params.get(0), params.get(1), params.get(2), params.get(3)));
        return Optional.of("null");
    }

    private Optional<String> handleClear(List<String> params, List<LogicInstruction> result) {
        result.add(new LogicInstruction("clear", params.get(0), params.get(1), params.get(2)));
        return Optional.of("null");
    }

    private Optional<String> handleMath(String functionName, List<String> params, List<LogicInstruction> result) {
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", functionName, tmp, params.get(0)));
        return Optional.of(tmp);
    }

    private Optional<String> handleWithin(List<String> params, List<LogicInstruction> result) {
        // ucontrol within x y radius result 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("ucontrol", "within", params.get(0), params.get(1), params.get(2), tmp));
        return Optional.of(tmp);
    }

    private Optional<String> handleGetBlock(List<String> params, List<LogicInstruction> result) {
        // ucontrol getBlock x y resultType resultBuilding 0
        // TODO: either handle multiple return values, or provide a better abstraction over getBlock
        result.add(new LogicInstruction("ucontrol", "getBlock", params.get(0), params.get(1), params.get(2), params.get(3)));
        return Optional.of("null");
    }

    private Optional<String> handleBuild(List<String> params, List<LogicInstruction> result) {
        // ucontrol build x y block rotation config
        result.add(new LogicInstruction("ucontrol", "build", params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
        return Optional.of("null");
    }

    private Optional<String> handlePayTake(List<String> params, List<LogicInstruction> result) {
        // ucontrol payTake takeUnits 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "payTake", params.get(0)));
        return Optional.of("null");
    }

    private Optional<String> handlePayDrop(List<String> params, List<LogicInstruction> result) {
        // ucontrol payDrop 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "payDrop"));
        return Optional.of("null");
    }

    private Optional<String> handleItemTake(List<String> params, List<LogicInstruction> result) {
        // ucontrol itemTake from item amount 0 0
        result.add(new LogicInstruction("ucontrol", "itemTake", params.get(0), params.get(1), params.get(2)));
        return Optional.of("null");
    }

    private Optional<String> handleTargetp(List<String> params, List<LogicInstruction> result) {
        // ucontrol targetp unit shoot 0 0 0
        result.add(new LogicInstruction("ucontrol", "targetp", params.get(0), params.get(1)));
        return Optional.of("null");
    }

    private Optional<String> handleTarget(List<String> params, List<LogicInstruction> result) {
        // ucontrol target x y shoot 0 0
        result.add(new LogicInstruction("ucontrol", "target", params.get(0), params.get(1), params.get(2)));
        return Optional.of("null");
    }

    private Optional<String> handleBoost(List<String> params, List<LogicInstruction> result) {
        // ucontrol boost enable 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "boost", params.get(1)));
        return Optional.of(params.get(1));
    }

    private Optional<String> handlePathfind(List<String> params, List<LogicInstruction> result) {
        // ucontrol pathfind 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "pathfind"));
        return Optional.of("null");
    }

    private Optional<String> handleIdle(List<String> params, List<LogicInstruction> result) {
        // ucontrol idle 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "idle"));
        return Optional.of("null");
    }

    private Optional<String> handleStop(List<String> params, List<LogicInstruction> result) {
        // ucontrol stop 0 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "stop"));
        return Optional.of("null");
    }

    private Optional<String> handleApproach(List<String> params, List<LogicInstruction> result) {
        // ucontrol approach x y radius 0 0
        result.add(new LogicInstruction("ucontrol", "approach", params.get(0), params.get(1), params.get(2)));
        return Optional.of("null");
    }

    private Optional<String> handleFlag(List<String> params, List<LogicInstruction> result) {
        // ucontrol flag value 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "flag", params.get(0)));
        return Optional.of(params.get(0));
    }

    private Optional<String> handleItemDrop(List<String> params, List<LogicInstruction> result) {
        // ucontrol itemDrop to amount 0 0 0
        result.add(new LogicInstruction("ucontrol", "itemDrop", params.get(0), params.get(1)));
        return Optional.of("null");
    }

    private Optional<String> handleMine(List<String> params, List<LogicInstruction> result) {
        // ucontrol mine x y 0 0 0
        result.add(new LogicInstruction("ucontrol", "mine", params.get(0), params.get(1)));
        return Optional.of("null");
    }

    private Optional<String> handleGetlink(List<String> params, List<LogicInstruction> result) {
        // getlink result 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("getlink", tmp, params.get(0)));
        return Optional.of(tmp);
    }

    private Optional<String> handleRand(List<String> params, List<LogicInstruction> result) {
        // op rand result 200 0
        final String tmp = nextTemp();
        result.add(new LogicInstruction("op", "rand", tmp, params.get(0)));
        return Optional.of(tmp);
    }

    private Optional<String> handleMove(List<String> params, List<LogicInstruction> result) {
        // ucontrol move 14 15 0 0 0
        result.add(new LogicInstruction("ucontrol", "move", params.get(0), params.get(1)));
        return Optional.of("null");
    }

    private Optional<String> handleUbind(List<String> params, List<LogicInstruction> result) {
        // ubind @poly
        result.add(new LogicInstruction("ubind", params.get(0)));
        return Optional.of("null");
    }

    private Optional<String> handlePrintflush(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("printflush", List.of(param))));
        return Optional.of("null");
    }

    private Optional<String> handlePrint(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("print", List.of(param))));
        return Optional.of(params.get(params.size() - 1));
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitBinaryOp(BinaryOp node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> left = visit(node.getLeft());
        if (!left._1.isPresent()) {
            throw new GenerationException("Expected a variable name, found none in " + left);
        }

        final Tuple2<Optional<String>, List<LogicInstruction>> right = visit(node.getRight());
        if (!right._1.isPresent()) {
            throw new GenerationException("Expected a variable name, found none in " + right);
        }

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>();
        result.addAll(left._2);
        result.addAll(right._2);
        result.add(
                new LogicInstruction(
                        "op",
                        List.of(translateBinaryOpToCode(node.getOp()), tmp, left._1.get(), right._1.get())
                )
        );

        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitUnitAssignment(UnitAssignment node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> value = visit(node.getValue());
        return new Tuple2<>(Optional.of("@" + node.getName()), value._2);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitRef(Ref node) {
        return new Tuple2<>(Optional.of("@" + node.getName()), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitSensorReading(SensorReading node) {
        final String tmp = nextTemp();
        return new Tuple2<>(Optional.of(tmp), List.of(new LogicInstruction("sensor", tmp, node.getTarget(), node.getSensor())));
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitNullLiteral(NullLiteral node) {
        return new Tuple2<>(Optional.of("null"), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitBooleanLiteral(BooleanLiteral node) {
        return new Tuple2<>(Optional.of(String.valueOf(node.getValue())), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitVarRef(VarRef node) {
        return new Tuple2<>(
                Optional.of(node.getName()),
                List.of()
        );
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitStringLiteral(StringLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                Optional.of(tmp),
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
    public Tuple2<Optional<String>, List<LogicInstruction>> visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                Optional.of(tmp),
                List.of(new LogicInstruction("set", List.of(tmp, node.getLiteral()))
                )
        );
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitPropertyAccess(PropertyAccess node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> target = visit(node.getTarget());
        if (!target._1.isPresent()) {
            throw new GenerationException("Expected to find a variable from the target in " + node);
        }

        final String tmp = nextTemp();
        final List<LogicInstruction> result = new ArrayList<>(target._2);
        result.add(new LogicInstruction("sensor", tmp, target._1.get(), "@" + node.getProperty()));
        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitCaseExpression(CaseExpression node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> cond = visit(node.getCondition());
        if (!cond._1.isPresent()) {
            throw new GenerationException("Expected to find a variable to compare against in " + node);
        }

        final String condVar = nextTemp();
        final String resultVar = nextTemp();
        final String exitLabel = nextLabel();

        final List<LogicInstruction> result = new ArrayList<>(cond._2);
        result.add(new LogicInstruction("set", condVar, cond._1.get()));
        for (final CaseAlternative alternative : node.getAlternatives()) {
            final Tuple2<Optional<String>, List<LogicInstruction>> altValue = visit(alternative.getValue());
            if (!altValue._1.isPresent()) {
                throw new GenerationException("Expected to find alternative variable in " + alternative);
            }

            final Tuple2<Optional<String>, List<LogicInstruction>> body = visit(alternative.getBody());
            if (!body._1.isPresent()) {
                throw new GenerationException("Expected to find a body value in " + alternative);
            }

            final String nextCond = nextLabel();

            result.addAll(altValue._2);
            result.add(new LogicInstruction("jump", nextCond, "notEqual", condVar, altValue._1.get()));
            result.addAll(body._2);
            result.add(new LogicInstruction("set", resultVar, body._1.get()));
            result.add(new LogicInstruction("jump", exitLabel, "always"));
            result.add(new LogicInstruction("label", nextCond));
        }


        final Tuple2<Optional<String>, List<LogicInstruction>> elseBranch = visit(node.getElseBranch());
        if (!elseBranch._1.isPresent()) {
            throw new GenerationException("Expected else branch to return a value in " + node);
        }

        result.addAll(elseBranch._2);
        result.add(new LogicInstruction("set", resultVar, elseBranch._1.get()));
        result.add(new LogicInstruction("label", exitLabel));

        return new Tuple2<>(Optional.of(resultVar), result);
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
        /*

            jump -1 equal x false
            jump 0 notEqual x false
            jump 0 lessThan x false
            jump 0 lessThanEq x false
            jump 0 greaterThan x false
            jump 0 greaterThanEq x false
            jump 0 strictEqual x false
            jump 0 always x false

        */

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
