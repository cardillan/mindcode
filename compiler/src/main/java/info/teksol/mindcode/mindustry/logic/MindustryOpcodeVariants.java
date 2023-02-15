package info.teksol.mindcode.mindustry.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static info.teksol.mindcode.mindustry.logic.OpcodeVariant.FunctionMapping.*;
import static info.teksol.mindcode.mindustry.logic.ProcessorEdition.*;
import static info.teksol.mindcode.mindustry.logic.ProcessorVersion.*;

public class MindustryOpcodeVariants {
    private static final MindustryOpcodeVariants data = new MindustryOpcodeVariants();

    public static List<OpcodeVariant> getAllOpcodeVariants() {
        return data.variants;
    }

    public static List<OpcodeVariant> getSpecificOpcodeVariants(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        return data.variants.stream()
                .filter(o -> o.isAvailableIn(processorVersion, processorEdition))
                .collect(Collectors.toUnmodifiableList());
    }

    ///////////////////////////////////////////////////////////////

    private final List<OpcodeVariant> variants = initialize();

    private MindustryOpcodeVariants() {
    }

    private void add(List<OpcodeVariant> variants, ProcessorVersion versionFrom, ProcessorVersion versionTo,
            ProcessorEdition edition, OpcodeVariant.FunctionMapping functionMapping, Opcode opcode, NamedArgument... arguments) {
        variants.add(new OpcodeVariant(versionFrom, versionTo, edition, functionMapping, opcode, arguments));
    }

    private List<OpcodeVariant> initialize() {
        List<OpcodeVariant> list = new ArrayList<>();
        add(list, V6, V7, S, NONE, Opcode.READ,       out("result"), block("memory"), in("at"));
        add(list, V6, V7, S, NONE, Opcode.WRITE,      in("value"),   block("memory"), in("at"));

        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("clear"),    in("r"), in("g"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("color"),    in("r"), in("g"), in("b"), in("a"));
        add(list, V7, V7, S, FUNC, Opcode.DRAW,       draw("col"),      in("color"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("stroke"),   in("width"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("line"),     in("x"), in("y"), in("x2"),    in("y2"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("rect"),     in("x"), in("y"), in("width"), in("height"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("lineRect"), in("x"), in("y"), in("width"), in("height"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("poly"),     in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("linePoly"), in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("triangle"), in("x"), in("y"), in("x2"),    in("y2"),     in("x3"), in("y3"));
        add(list, V6, V7, S, FUNC, Opcode.DRAW,       draw("image"),    in("x"), in("y"), in("image"), in("size"),   in("rotation"));

        add(list, V6, V7, S, FUNC, Opcode.PRINT,      in("what"));

        add(list, V6, V7, S, FUNC, Opcode.DRAWFLUSH,  block("to"));
        add(list, V6, V7, S, FUNC, Opcode.PRINTFLUSH, block("to"));

        add(list, V6, V7, S, FUNC, Opcode.GETLINK,    res("block"), in("link#"));

        add(list, V6, V7, S, PROP, Opcode.CONTROL,    bctrl("enabled"),   block("of"), in("to"));
        add(list, V6, V7, S, PROP, Opcode.CONTROL,    bctrl("shoot"),     block("of"), in("x"),    in("y"), in("shoot"));
        add(list, V6, V7, S, PROP, Opcode.CONTROL,    bctrl("shootp"),    block("of"), in("unit"), in("shoot"));
        add(list, V6, V6, S, PROP, Opcode.CONTROL,    bctrl("configure"), block("of"), in("to"));
        add(list, V7, V7, S, PROP, Opcode.CONTROL,    bctrl("config"),    block("of"), in("to"));
        add(list, V6, V7, S, PROP, Opcode.CONTROL,    bctrl("color"),     block("of"), in("r"), in("g"), in("b"));

        add(list, V6, V7, S, FUNC, Opcode.RADAR,      radar("attr1"), radar("attr2"), radar("attr3"), sort("sort"), block("turret"), in("order"), res("result"));

        add(list, V6, V7, S, FUNC, Opcode.SENSOR,     res("result"),      block("in"), sensor("data"));

        add(list, V6, V7, S, NONE, Opcode.SET,        out("result"),      block("value"));

        add(list, V6, V7, S, NONE, Opcode.OP,         op("add"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("sub"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("mul"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("div"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("idiv"),         res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("mod"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("pow"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("equal"),        res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("notEqual"),     res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("land"),         res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("lessThan"),     res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("lessThanEq"),   res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("greaterThan"),  res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("greaterThanEq"),res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("strictEqual"),  res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("shl"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("shr"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("or"),           res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("and"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("xor"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, NONE, Opcode.OP,         op("not"),          res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("max"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("min"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("angle"),        res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("len"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("noise"),        res("result"),  in("a"), in("b"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("abs"),          res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("log"),          res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("log10"),        res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("floor"),        res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("ceil"),         res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("sqrt"),         res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("rand"),         res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("sin"),          res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("cos"),          res("result"),  in("a"));
        add(list, V6, V7, S, FUNC, Opcode.OP,         op("tan"),          res("result"),  in("a"));
        add(list, V7, V7, S, FUNC, Opcode.OP,         op("asin"),         res("result"),  in("a"));
        add(list, V7, V7, S, FUNC, Opcode.OP,         op("acos"),         res("result"),  in("a"));
        add(list, V7, V7, S, FUNC, Opcode.OP,         op("atan"),         res("result"),  in("a"));

        add(list, V7, V7, S, FUNC, Opcode.LOOKUP,     lookup("lookup"), res("result"), in("#"));

        add(list, V7, V7, S, FUNC, Opcode.PACKCOLOR,  res("result"), in("r"), in("g"), in("b"), in("a"));

        add(list, V7, V7, S, FUNC, Opcode.WAIT,       in("sec"));
        add(list, V7, V7, S, FUNC, Opcode.STOP);
        add(list, V6, V7, S, FUNC, Opcode.END);

        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("equal"),         in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("notEqual"),      in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("lessThan"),      in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("lessThanEq"),    in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThan"),   in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThanEq"), in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("strictEqual"),   in("x"), in("y"));
        add(list, V6, V7, S, NONE, Opcode.JUMP,       label("label"), cond("always"));

        add(list, V6, V7, S, FUNC, Opcode.UBIND,      unit("type"));

        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("idle"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("stop"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("move"),      in("x"), in("y"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("approach"),  in("x"), in("y"), in("radius"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("boost"),     in("enable"));
        add(list, V6, V6, S, FUNC, Opcode.UCONTROL,   uctrl("pathfind"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("target"),    in("x"), in("y"), in("shoot"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("targetp"),   in("unit"), in("shoot"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("itemDrop"),  block("to"), in("amount"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("itemTake"),  block("from"), ore("item"), in("amount"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("payDrop"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("payTake"),   in("takeUnits"));
        add(list, V7, V7, S, FUNC, Opcode.UCONTROL,   uctrl("payEnter"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("mine"),      in("x"), in("y"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("flag"),      in("value"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("build"),     in("x"), in("y"), in("block"), in("rotation"), in("config"));
        // TODO: either handle multiple return values, or provide a better abstraction over getBlock
        add(list, V6, V6, S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"));
        add(list, V7, V7, S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"), out("floor"));
        add(list, V6, V7, S, FUNC, Opcode.UCONTROL,   uctrl("within"),    in("x"), in("y"), in("radius"), res("result"));
        add(list, V7, V7, S, FUNC, Opcode.UCONTROL,   uctrl("unbind"));

        add(list, V6, V7, S, FUNC, Opcode.URADAR, radar("attr1"), radar("attr1"), radar("attr1"), sort("sort"), unused("0"), in("order"), res("result"));

        add(list, V6, V7, S, FUNC, Opcode.ULOCATE, locate("ore"),      unused("core"), unused("true"), ore("ore"), out("outx"), out("outy"), res("found"), unusedOut("building"));
        add(list, V6, V7, S, FUNC, Opcode.ULOCATE, locate("building"), group("group"), in("enemy"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6, V7, S, FUNC, Opcode.ULOCATE, locate("spawn"),    unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6, V7, S, FUNC, Opcode.ULOCATE, locate("damaged"),  unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));

        add(list, V6, V7, S, NONE, Opcode.LABEL, label("label"));

        return List.copyOf(list);
    }

    public static NamedArgument bctrl(String name) {
        return new NamedArgument(ArgumentType.BLOCK_CONTROL, name);
    }

    public static NamedArgument block(String name) {
        return new NamedArgument(ArgumentType.BLOCK, name);
    }

    public static NamedArgument cond(String name) {
        return new NamedArgument(ArgumentType.CONDITION, name);
    }

    public static NamedArgument draw(String name) {
        return new NamedArgument(ArgumentType.DRAW, name);
    }

    public static NamedArgument group(String name) {
        return new NamedArgument(ArgumentType.GROUP, name);
    }

    public static NamedArgument in(String name) {
        return new NamedArgument(ArgumentType.INPUT, name);
    }

    public static NamedArgument label(String name) {
        return new NamedArgument(ArgumentType.LABEL, name);
    }

    public static NamedArgument locate(String name) {
        return new NamedArgument(ArgumentType.LOCATE, name);
    }

    public static NamedArgument lookup(String name) {
        return new NamedArgument(ArgumentType.LOOKUP, name);
    }

    public static NamedArgument op(String name) {
        return new NamedArgument(ArgumentType.OPERATION, name);
    }

    public static NamedArgument ore(String name) {
        return new NamedArgument(ArgumentType.ORE, name);
    }

    public static NamedArgument out(String name) {
        return new NamedArgument(ArgumentType.OUTPUT, name);
    }

    public static NamedArgument radar(String name) {
        return new NamedArgument(ArgumentType.RADAR, name);
    }

    public static NamedArgument res(String name) {
        return new NamedArgument(ArgumentType.RESULT, name);
    }

    public static NamedArgument sensor(String name) {
        return new NamedArgument(ArgumentType.SENSOR, name);
    }

    public static NamedArgument sort(String name) {
        return new NamedArgument(ArgumentType.SORT, name);
    }

    public static NamedArgument uctrl(String name) {
        return new NamedArgument(ArgumentType.UNIT_CONTROL, name);
    }

    public static NamedArgument unit(String name) {
        return new NamedArgument(ArgumentType.UNIT, name);
    }

    public static NamedArgument unused(String name) {
        return new NamedArgument(ArgumentType.UNUSED, name);
    }

    public static NamedArgument unusedOut(String name) {
        return new NamedArgument(ArgumentType.UNUSED_OUTPUT, name);
    }
}