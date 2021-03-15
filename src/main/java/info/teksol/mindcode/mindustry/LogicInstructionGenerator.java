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
 *
 * LogicInstruction stands for Logic Instruction, the Mindustry assembly code.
 */
public class LogicInstructionGenerator extends BaseAstVisitor<Tuple2<Optional<String>, List<LogicInstruction>>> {
    private int tmp;
    private int label;

    /*

        read result cell1 0
        write result cell1 0
        print "frog"
        printflush message1
        draw clear r g b 0 0 0
        draw color r g b a 0 0
        draw stroke strokewidth 0 0 255 0 0
        draw line linex liney linex1 liney2 0 0
        draw rect rectx recty rectw recth 0 0
        draw lineRect rectx recty rectw recth 0 0
        draw poly polyx polyy polysides polyradius polyrotation 0
        draw linePoly polyx polyy polysides polyradius polyrotation 0
        draw triangle trix0 triy1 trix1 triy1 trix2 triy2
        draw image imgx imgy @copper imgsize imgrot triy2
        drawflush display1
        getlink result 0
        control enabled block1 yesno 0 0 0
        control shoot block1 sx sy enableshooting 0
        control color block1 colr colg colb 0
        control configure block1 conf 0 0 0
        sensor result block1 @copper
        op add result a b
        op strictEqual result a b
        op max result a b
        op angle result a b
        op noise result a b
        op rand result 0 0
        set result setval
        jump 0 notEqual iszerobased false
        jump 27 strictEqual x false
        jump -1 always x false
        end

     */

    public static List<LogicInstruction> generateFrom(Seq program) {
        final Tuple2<Optional<String>, List<LogicInstruction>> instructions =
                new LogicInstructionGenerator().visit(program);
        final List<LogicInstruction> result = new ArrayList<>(instructions._2);
        result.add(new LogicInstruction("end"));
        return result;
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitHeapRead(HeapRead node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                Optional.of(tmp),
                List.of(new LogicInstruction("read", tmp, node.getCellName(), node.getAddress()))
        );
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitHeapWrite(HeapWrite node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> value = visit(node.getValue());
        if (!value._1.isPresent()) {
            throw new GenerationException("Expected to find tmp variable from heap write node, found: " + value);
        }

        final List<LogicInstruction> result = new ArrayList<>(value._2);
        result.add(new LogicInstruction("write", value._1.get(), node.getCellName(), node.getAddress()));
        return new Tuple2<>(value._1, result);
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
        return new Tuple2<>(Optional.empty(), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<LogicInstruction>> visitVarAssignment(VarAssignment node) {
        final Tuple2<Optional<String>, List<LogicInstruction>> rvalue = visit(node.getRvalue());
        final List<LogicInstruction> result = new ArrayList<>(rvalue._2);
        if (!rvalue._1.isPresent()) {
            throw new GenerationException("Expected a variable name, found none in " + result);
        }

        result.add(new LogicInstruction("set", List.of(node.getVarName(), rvalue._1.get())));
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

            case "flag":
                return handleFlag(params, result);

            case "approach":
                return handleApproach(params, result);

            default:
                throw new GenerationException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private Optional<String> handleApproach(List<String> params, List<LogicInstruction> result) {
        // ucontrol approach 1 2 3 0 0
        result.add(new LogicInstruction("ucontrol", "approach", params.get(0), params.get(1), params.get(2)));
        return Optional.empty();
    }

    private Optional<String> handleFlag(List<String> params, List<LogicInstruction> result) {
        // ucontrol flag FLAG 0 0 0 0
        result.add(new LogicInstruction("ucontrol", "flag", params.get(0)));
        return Optional.of(params.get(0));
    }

    private Optional<String> handleItemDrop(List<String> params, List<LogicInstruction> result) {
        // ucontrol itemDrop vault1 100 0 0 0
        result.add(new LogicInstruction("ucontrol", "itemDrop", params.get(0), params.get(1)));
        return Optional.of("null");
    }

    private Optional<String> handleMine(List<String> params, List<LogicInstruction> result) {
        // ucontrol mine 1 2 0 0 0
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
        return Optional.empty();
    }

    private Optional<String> handleUbind(List<String> params, List<LogicInstruction> result) {
        // ubind @poly
        result.add(new LogicInstruction("ubind", params.get(0)));
        return Optional.empty();
    }

    private Optional<String> handlePrintflush(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("printflush", List.of(param))));
        return Optional.empty();
    }

    private Optional<String> handlePrint(List<String> params, List<LogicInstruction> result) {
        params.forEach((param) -> result.add(new LogicInstruction("print", List.of(param))));
        return Optional.empty();
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
    public Tuple2<Optional<String>, List<LogicInstruction>> visitUnitRef(UnitRef node) {
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
        label++;
        return "label" + (label - 1);
    }

    private String nextTemp() {
        tmp++;
        return "tmp" + (tmp - 1);
    }
}
