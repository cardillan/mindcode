package info.teksol.mindcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MOpcodeGenerator extends BaseAstVisitor<Tuple2<Optional<String>, List<MOpcode>>> {
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

    public static List<MOpcode> generateFrom(Seq program) {
        final Tuple2<Optional<String>, List<MOpcode>> opcodes = new MOpcodeGenerator().visit(program);
        final List<MOpcode> result = new ArrayList<>(opcodes._2);
        result.add(new MOpcode("end"));
        return result;
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitControl(Control node) {
        final Tuple2<Optional<String>, List<MOpcode>> rest = visit(node.getValue());
        final List<MOpcode> result = new ArrayList<>(rest._2);
        if (!rest._1.isPresent()) {
            throw new MindustryConverterException("Expected to find tmp variable from control node, found: " + rest);
        }

        result.add(new MOpcode("control", node.getProperty(), node.getTarget(), rest._1.get()));

        return new Tuple2<>(Optional.empty(), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitSeq(Seq seq) {
        final Tuple2<Optional<String>, List<MOpcode>> rest = visit(seq.getRest());
        final Tuple2<Optional<String>, List<MOpcode>> last = visit(seq.getLast());
        final List<MOpcode> result = new ArrayList<>();
        result.addAll(rest._2);
        result.addAll(last._2);

        return new Tuple2<>(Optional.empty(), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitNoOp(NoOp node) {
        return new Tuple2<>(Optional.empty(), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitVarAssignment(VarAssignment node) {
        final Tuple2<Optional<String>, List<MOpcode>> rvalue = visit(node.getRvalue());
        final List<MOpcode> result = new ArrayList<>(rvalue._2);
        if (!rvalue._1.isPresent()) {
            throw new MindustryConverterException("Expected a variable name, found none in " + result);
        }

        result.add(new MOpcode("set", List.of(node.getVarName(), rvalue._1.get())));
        return new Tuple2<>(Optional.empty(), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitUnaryOp(UnaryOp node) {
        final Tuple2<Optional<String>, List<MOpcode>> expression = visit(node.getExpression());
        if (!expression._1.isPresent()) {
            throw new MindustryConverterException("Expected to have a variable in " + expression);
        }

        final String tmp = nextTemp();
        final List<MOpcode> result = new ArrayList<>(expression._2);
        result.add(new MOpcode("op", List.of(translateUnaryOpToCode(node.getOp()), tmp, expression._1.get())));
        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitWhileStatement(WhileStatement node) {
        final Tuple2<Optional<String>, List<MOpcode>> cond = visit(node.getCondition());
        if (!cond._1.isPresent()) {
            throw new MindustryConverterException("Expected a variable name for the while condition, found none in " + cond);
        }

        final Tuple2<Optional<String>, List<MOpcode>> body = visit(node.getBody());
        if (body._1.isPresent()) {
            throw new MindustryConverterException("Expected while body not to return a variable name; found " + body);
        }

        final List<MOpcode> result = new ArrayList<>();
        final String condLabel = nextLabel();
        final String doneLabel = nextLabel();
        result.add(new MOpcode("label", List.of(condLabel)));
        result.addAll(cond._2);
        result.add(new MOpcode("jump", List.of(doneLabel, "notEqual", cond._1.get(), "true")));
        result.addAll(body._2);
        result.add(new MOpcode("jump", List.of(condLabel, "always")));
        result.add(new MOpcode("label", List.of(doneLabel)));

        return new Tuple2<>(Optional.empty(), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitFunctionCall(FunctionCall node) {
        final List<Tuple2<Optional<String>, List<MOpcode>>> params =
                node.getParams().stream().map(this::visit).collect(Collectors.toList());
        final List<MOpcode> result = new ArrayList<>();
        if (!params.stream().allMatch((param) -> param._1.isPresent())) {
            throw new MindustryConverterException("Expected all parameters to function calls to return values, found " + params);
        }

        params.forEach((param) -> result.addAll(param._2));
        handleFunctionCall(node.getFunctionName(), params.stream().map(Tuple2::getT1).map(Optional::get).collect(Collectors.toList()), result);
        return new Tuple2<>(Optional.empty(), result);
    }

    private void handleFunctionCall(String functionName, List<String> params, List<MOpcode> result) {
        switch (functionName) {
            case "print":
                handlePrint(params, result);
                break;
            case "printflush":
                handlePrintflush(params, result);
                break;

            default:
                throw new MindustryConverterException("Don't know how to handle function named [" + functionName + "]");
        }
    }

    private void handlePrintflush(List<String> params, List<MOpcode> result) {
        params.forEach((param) -> result.add(new MOpcode("printflush", List.of(param))));
    }


    private void handlePrint(List<String> params, List<MOpcode> result) {
        params.forEach((param) -> result.add(new MOpcode("print", List.of(param))));
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitBinaryOp(BinaryOp node) {
        final Tuple2<Optional<String>, List<MOpcode>> left = visit(node.getLeft());
        if (!left._1.isPresent()) {
            throw new MindustryConverterException("Expected a variable name, found none in " + left);
        }

        final Tuple2<Optional<String>, List<MOpcode>> right = visit(node.getRight());
        if (!right._1.isPresent()) {
            throw new MindustryConverterException("Expected a variable name, found none in " + right);
        }

        final String tmp = nextTemp();
        final List<MOpcode> result = new ArrayList<>();
        result.addAll(left._2);
        result.addAll(right._2);
        result.add(
                new MOpcode(
                        "op",
                        List.of(translateBinaryOpToCode(node.getOp()), tmp, left._1.get(), right._1.get())
                )
        );

        return new Tuple2<>(Optional.of(tmp), result);
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitSensorReading(SensorReading node) {
        final String tmp = nextTemp();
        return new Tuple2<>(Optional.of(tmp), List.of(new MOpcode("sensor", tmp, node.getTarget(), node.getSensor())));
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitNullLiteral(NullLiteral node) {
        return new Tuple2<>(Optional.of("null"), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitBooleanLiteral(BooleanLiteral node) {
        return new Tuple2<>(Optional.of(String.valueOf(node.getValue())), List.of());
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitVarRef(VarRef node) {
        return new Tuple2<>(
                Optional.of(node.getName()),
                List.of()
        );
    }

    @Override
    public Tuple2<Optional<String>, List<MOpcode>> visitStringLiteral(StringLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                Optional.of(tmp),
                List.of(
                        new MOpcode(
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
    public Tuple2<Optional<String>, List<MOpcode>> visitNumericLiteral(NumericLiteral node) {
        final String tmp = nextTemp();
        return new Tuple2<>(
                Optional.of(tmp),
                List.of(new MOpcode("set", List.of(tmp, node.getLiteral()))
                )
        );
    }

    private String translateUnaryOpToCode(String op) {
        switch (op) {
            case "not":
            case "!":
                return "not";

            default:
                throw new MindustryConverterException("Could not optimize unary op [" + op + "]");
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

            default:
                throw new MindustryConverterException("Failed to translate binary op to word: [" + op + "] is not handled");
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
