package info.teksol.mindcode.logic;

import java.util.ArrayList;
import java.util.List;

import static info.teksol.mindcode.logic.FunctionMapping.*;
import static info.teksol.mindcode.logic.ProcessorEdition.S;
import static info.teksol.mindcode.logic.ProcessorEdition.W;
import static info.teksol.mindcode.logic.ProcessorVersion.*;

public class MindustryOpcodeVariants {
    private static final MindustryOpcodeVariants data = new MindustryOpcodeVariants();

    public static List<OpcodeVariant> getSpecificOpcodeVariants(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        return data.variants.stream()
                .filter(o -> o.isAvailableIn(processorVersion, processorEdition))
                .toList();
    }

    ///////////////////////////////////////////////////////////////

    private final List<OpcodeVariant> variants = initialize();

    private MindustryOpcodeVariants() {
    }

    private void add(List<OpcodeVariant> variants, ProcessorVersion versionFrom, ProcessorVersion versionTo,
            ProcessorEdition edition, FunctionMapping functionMapping, Opcode opcode, NamedParameter... arguments) {
        variants.add(new OpcodeVariant(versionFrom, versionTo, edition, functionMapping, opcode, arguments));
    }

    private List<OpcodeVariant> initialize() {
        List<OpcodeVariant> list = new ArrayList<>();
        add(list, V6, V7A, S, NONE, Opcode.READ,       out("result"), block("cell1"), in("at"));
        add(list, V6, V7A, S, NONE, Opcode.WRITE,      in("value"),   block("cell1"), in("at"));

        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("clear"),    in("r"), in("g"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("color"),    in("r"), in("g"), in("b"), in("a"));
        add(list, V7, V7A, S, FUNC, Opcode.DRAW,       draw("col"),      in("color"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("stroke"),   in("width"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("line"),     in("x"), in("y"), in("x2"),    in("y2"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("rect"),     in("x"), in("y"), in("width"), in("height"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("lineRect"), in("x"), in("y"), in("width"), in("height"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("poly"),     in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("linePoly"), in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("triangle"), in("x"), in("y"), in("x2"),    in("y2"),     in("x3"), in("y3"));
        add(list, V6, V7A, S, FUNC, Opcode.DRAW,       draw("image"),    in("x"), in("y"), in("image"), in("size"),   in("rotation"));

        add(list, V6, V7A, S, FUNC, Opcode.PRINT,      in("what"));

        add(list, V6, V7A, S, BOTH, Opcode.DRAWFLUSH,  block("display1"));
        add(list, V6, V7A, S, BOTH, Opcode.PRINTFLUSH, block("message1"));

        add(list, V6, V7A, S, FUNC, Opcode.GETLINK,    res("block"), in("linkNum"));

        add(list, V6, V7A, S, PROP, Opcode.CONTROL,    bctrl("enabled"),   block("block"), in("value"));
        add(list, V6, V7A, S, PROP, Opcode.CONTROL,    bctrl("shoot"),     block("block"), in("x"),    in("y"), in("shoot"));
        add(list, V6, V7A, S, PROP, Opcode.CONTROL,    bctrl("shootp"),    block("block"), in("unit"), in("shoot"));
        add(list, V6, V6,  S, PROP, Opcode.CONTROL,    bctrl("configure"), block("block"), in("value"));
        add(list, V7, V7A, S, PROP, Opcode.CONTROL,    bctrl("config"),    block("block"), in("value"));
        add(list, V6, V7A, S, PROP, Opcode.CONTROL,    bctrl("color"),     block("block"), in("r"), in("g"), in("b"));

        add(list, V6, V7A, S, BOTH, Opcode.RADAR,      radar("attr1"), radar("attr2"), radar("attr3"), sort("sort"), block("turret"), in("order"), res("result"));

        add(list, V6, V7A, S, PROP, Opcode.SENSOR,     res("result"),      block("object"), sensor("property"));

        add(list, V6, V7A, S, NONE, Opcode.SET,        out("result"),      block("value"));

        add(list, V6, V7A, S, NONE, Opcode.OP,         op("add"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("sub"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("mul"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("div"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("idiv"),         res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("mod"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("pow"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("equal"),        res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("notEqual"),     res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("land"),         res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("lessThan"),     res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("lessThanEq"),   res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("greaterThan"),  res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("greaterThanEq"),res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("strictEqual"),  res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("shl"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("shr"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("or"),           res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("and"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("xor"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, NONE, Opcode.OP,         op("not"),          res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("max"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("min"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("angle"),        res("result"),  in("a"), in("b"));
        add(list, V7, V7A, S, FUNC, Opcode.OP,         op("angleDiff"),    res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("len"),          res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("noise"),        res("result"),  in("a"), in("b"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("abs"),          res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("log"),          res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("log10"),        res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("floor"),        res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("ceil"),         res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("sqrt"),         res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("rand"),         res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("sin"),          res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("cos"),          res("result"),  in("a"));
        add(list, V6, V7A, S, FUNC, Opcode.OP,         op("tan"),          res("result"),  in("a"));
        add(list, V7, V7A, S, FUNC, Opcode.OP,         op("asin"),         res("result"),  in("a"));
        add(list, V7, V7A, S, FUNC, Opcode.OP,         op("acos"),         res("result"),  in("a"));
        add(list, V7, V7A, S, FUNC, Opcode.OP,         op("atan"),         res("result"),  in("a"));

        add(list, V7, V7A, S, FUNC, Opcode.LOOKUP,     lookup("type"), res("result"), in("index"));

        add(list, V7, V7A, S, FUNC, Opcode.PACKCOLOR,  res("result"), in("r"), in("g"), in("b"), in("a"));

        add(list, V7, V7A, S, FUNC, Opcode.WAIT,       in("sec"));
        add(list, V7, V7A, S, FUNC, Opcode.STOP);
        add(list, V6, V7A, S, FUNC, Opcode.END);

        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("equal"),         in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("notEqual"),      in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("lessThan"),      in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("lessThanEq"),    in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThan"),   in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThanEq"), in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("strictEqual"),   in("x"), in("y"));
        add(list, V6, V7A, S, NONE, Opcode.JUMP,       label("label"), cond("always"));

        add(list, V6, V7A, S, FUNC, Opcode.UBIND,      unit("type"));

        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("idle"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("stop"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("move"),      in("x"), in("y"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("approach"),  in("x"), in("y"), in("radius"));
        add(list, V7, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("pathfind"),  in("x"), in("y"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("boost"),     in("enable"));
        add(list, V6, V6,  S, FUNC, Opcode.UCONTROL,   uctrl("pathfind"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("target"),    in("x"), in("y"), in("shoot"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("targetp"),   in("unit"), in("shoot"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("itemDrop"),  block("to"), in("amount"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("itemTake"),  block("from"), ore("item"), in("amount"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("payDrop"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("payTake"),   in("takeUnits"));
        add(list, V7, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("payEnter"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("mine"),      in("x"), in("y"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("flag"),      in("value"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("build"),     in("x"), in("y"), in("block"), in("rotation"), in("config"));
        add(list, V6, V6,  S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"));
        add(list, V7, V7,  S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"), out("floor"));
        add(list, V7A,V7A, S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), res("building"), out("floor"));
        add(list, V6, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("within"),    in("x"), in("y"), in("radius"), res("result"));
        add(list, V7, V7A, S, FUNC, Opcode.UCONTROL,   uctrl("unbind"));

        add(list, V6, V7A, S, FUNC, Opcode.URADAR, radar("attr1"), radar("attr2"), radar("attr3"), sort("sort"), unused("0"), in("order"), res("result"));

        add(list, V6, V7A, S, FUNC, Opcode.ULOCATE, locate("ore"),      unused("core"), unused("true"), ore("oreType"), out("outx"), out("outy"), res("found"), unusedOut("building"));
        add(list, V6, V7,  S, FUNC, Opcode.ULOCATE, locate("building"), group("group"), in("enemy"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6, V7,  S, FUNC, Opcode.ULOCATE, locate("spawn"),    unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6, V7,  S, FUNC, Opcode.ULOCATE, locate("damaged"),  unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V7A,V7A, S, FUNC, Opcode.ULOCATE, locate("building"), group("group"), in("enemy"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));
        add(list, V7A,V7A, S, FUNC, Opcode.ULOCATE, locate("spawn"),    unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));
        add(list, V7A,V7A, S, FUNC, Opcode.ULOCATE, locate("damaged"),  unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));

        add(list, V7, V7A, W, FUNC, Opcode.GETBLOCK, layer("layer"), res("result"), in("x"), in("y"));

        add(list, V7, V7A, W, FUNC, Opcode.SETBLOCK, layerset("floor"), in("to"), in("x"), in("y"));
        add(list, V7, V7A, W, FUNC, Opcode.SETBLOCK, layerset("ore"),  ore("to"), in("x"), in("y"));
        add(list, V7, V7A, W, FUNC, Opcode.SETBLOCK, layerset("block"), in("to"), in("x"), in("y"), in("team"), in("rotation"));

        add(list, V7, V7A, W, FUNC, Opcode.SPAWN, unit("unit"), in("x"), in("y"), in("rotation"), in("team"), res("result"));

        add(list, V7, V7A, W, FUNC, Opcode.STATUS, clear("false"), status("status"), in("unit"), in("duration"));
        add(list, V7, V7A, W, FUNC, Opcode.STATUS, clear("true"),  status("status"), in("unit"));

        add(list, V7, V7A, W, FUNC, Opcode.SPAWNWAVE, in("x"),  in("y"), in("natural"));

        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("currentWaveTime"),       in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("waveTimer"),             in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("waves"),                 in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("wave"),                  in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("waveSpacing"),           in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("waveSending"),           in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("attackMode"),            in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("enemyCoreBuildRadius"),  in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("dropZoneRadius"),        in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("unitCap"),               in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("mapArea"),               unused("0"), in("x"), in("y"), in("width"), in("height"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("lighting"),              in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("ambientLight"),          in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("solarMultiplier"),       in("value"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("buildSpeed"),            in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("unitHealth"),            in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("unitBuildSpeed"),        in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("unitCost"),              in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("unitDamage"),            in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("blockHealth"),           in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("blockDamage"),           in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("rtsMinWeight"),          in("value"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRULE,   rule("rtsMinSquad"),           in("value"), in("team"));

        add(list, V7, V7A, W, FUNC, Opcode.MESSAGE,   message("notify"));
        add(list, V7, V7A, W, FUNC, Opcode.MESSAGE,   message("mission"));
        add(list, V7, V7A, W, FUNC, Opcode.MESSAGE,   message("announce"), in("duration"));
        add(list, V7, V7A, W, FUNC, Opcode.MESSAGE,   message("toast"),    in("duration"));

        add(list, V7, V7A, W, FUNC, Opcode.CUTSCENE,  cutscene("pan"),  in("x"), in("y"), in("speed"));
        add(list, V7, V7A, W, FUNC, Opcode.CUTSCENE,  cutscene("zoom"), in("level"));
        add(list, V7, V7A, W, FUNC, Opcode.CUTSCENE,  cutscene("stop"));

        add(list, V7, V7A, W, FUNC, Opcode.EXPLOSION, in("team"), in("x"), in("y"), in("radius"), in("damage"), in("air"), in("ground"), in("pierce"));
        add(list, V7, V7A, W, FUNC, Opcode.SETRATE, in("ipt"));

        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("unitCount"),   res("result"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("playerCount"), res("result"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("coreCount"),   res("result"), in("team"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("buildCount"),  res("result"), in("team"), unused("0"), in("type"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("unit"),        res("result"), in("team"), in("index"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("player"),      res("result"), in("team"), in("index"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("core"),        res("result"), in("team"), in("index"));
        add(list, V7, V7A, W, FUNC, Opcode.FETCH, fetch("build"),       res("result"), in("team"), in("index"), in("type"));

        add(list, V7, V7A, W, FUNC, Opcode.GETFLAG, res("result"), in("flag"));
        add(list, V7, V7A, W, FUNC, Opcode.SETFLAG, in("flag"), in("value"));
        add(list, V7, V7A, W, PROP, Opcode.SETPROP, sensor("property"), block("object"), in("value"));

        // Virtual instructions
        add(list, V6, V7A, S, NONE, Opcode.NOOP);
        add(list, V6, V7A, S, NONE, Opcode.LABEL,       label("label"));
        add(list, V6, V7A, S, NONE, Opcode.GOTOLABEL,   label("address"), label("marker"));
        add(list, V6, V7A, S, NONE, Opcode.PUSH,        block("memory"), in("value"));
        add(list, V6, V7A, S, NONE, Opcode.POP,         block("memory"), out("value"));
        add(list, V6, V7A, S, NONE, Opcode.CALL,        label("callAddr"));
        add(list, V6, V7A, S, NONE, Opcode.CALLREC,     block("memory"), label("callAddr"), label("retAddr"));
        add(list, V6, V7A, S, NONE, Opcode.RETURN,      block("memory"));
        add(list, V6, V7A, S, NONE, Opcode.GOTO,        in("address"), label("marker"));
        add(list, V6, V7A, S, NONE, Opcode.SETADDR,     out("result"),   in("address"));

        return List.copyOf(list);
    }

    public static NamedParameter bctrl(String name) {
        return new NamedParameter(LogicParameter.BLOCK_CONTROL, name);
    }

    public static NamedParameter block(String name) {
        return new NamedParameter(LogicParameter.BLOCK, name);
    }

    public static NamedParameter clear(String name) {
        return new NamedParameter(LogicParameter.CLEAR, name);
    }

    public static NamedParameter cond(String name) {
        return new NamedParameter(LogicParameter.CONDITION, name);
    }

    public static NamedParameter cutscene(String name) {
        return new NamedParameter(LogicParameter.CUTSCENE, name);
    }

    public static NamedParameter draw(String name) {
        return new NamedParameter(LogicParameter.DRAW, name);
    }

    public static NamedParameter fetch(String name) {
        return new NamedParameter(LogicParameter.FETCH, name);
    }

    public static NamedParameter group(String name) {
        return new NamedParameter(LogicParameter.GROUP, name);
    }

    public static NamedParameter in(String name) {
        return new NamedParameter(LogicParameter.INPUT, name);
    }

    public static NamedParameter label(String name) {
        return new NamedParameter(LogicParameter.LABEL, name);
    }

    public static NamedParameter layer(String name) {
        return new NamedParameter(LogicParameter.LAYER, name);
    }

    public static NamedParameter layerset(String name) {
        return new NamedParameter(LogicParameter.SETTABLE_LAYER, name);
    }

    public static NamedParameter locate(String name) {
        return new NamedParameter(LogicParameter.LOCATE, name);
    }

    public static NamedParameter lookup(String name) {
        return new NamedParameter(LogicParameter.LOOKUP, name);
    }

    public static NamedParameter message(String name) {
        return new NamedParameter(LogicParameter.MESSAGE, name);
    }

    public static NamedParameter op(String name) {
        return new NamedParameter(LogicParameter.OPERATION, name);
    }

    public static NamedParameter ore(String name) {
        return new NamedParameter(LogicParameter.ORE, name);
    }

    public static NamedParameter out(String name) {
        return new NamedParameter(LogicParameter.OUTPUT, name);
    }

    public static NamedParameter radar(String name) {
        return new NamedParameter(LogicParameter.RADAR, name);
    }

    public static NamedParameter res(String name) {
        return new NamedParameter(LogicParameter.RESULT, name);
    }

    public static NamedParameter rule(String name) {
        return new NamedParameter(LogicParameter.RULE, name);
    }

    public static NamedParameter sensor(String name) {
        return new NamedParameter(LogicParameter.SENSOR, name);
    }

    public static NamedParameter sort(String name) {
        return new NamedParameter(LogicParameter.SORT, name);
    }

    public static NamedParameter status(String name) {
        return new NamedParameter(LogicParameter.STATUS, name);
    }

    public static NamedParameter uctrl(String name) {
        return new NamedParameter(LogicParameter.UNIT_CONTROL, name);
    }

    public static NamedParameter unit(String name) {
        return new NamedParameter(LogicParameter.UNIT, name);
    }

    public static NamedParameter unused(String name) {
        return new NamedParameter(LogicParameter.UNUSED, name);
    }

    public static NamedParameter unusedOut(String name) {
        return new NamedParameter(LogicParameter.UNUSED_OUTPUT, name);
    }
}
