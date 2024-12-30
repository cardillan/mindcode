package info.teksol.mc.mindcode.logic.opcodes;

import info.teksol.mc.mindcode.logic.arguments.Operation;

import java.util.ArrayList;
import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.FunctionMapping.*;
import static info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition.S;
import static info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition.W;
import static info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion.*;

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
        add(list, V6,  MAX, S, NONE, Opcode.READ,       res("result"), block("cell1"), in("at"));
        add(list, V6,  MAX, S, NONE, Opcode.WRITE,      in("value"),   block("cell1"), in("at"));

        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("clear"),      in("r"), in("g"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("color"),      in("r"), in("g"), in("b"), in("a"));
        add(list, V7,  MAX, S, FUNC, Opcode.DRAW,       draw("col"),        in("color"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("stroke"),     in("width"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("line"),       in("x"), in("y"), in("x2"),    in("y2"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("rect"),       in("x"), in("y"), in("width"), in("height"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("lineRect"),   in("x"), in("y"), in("width"), in("height"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("poly"),       in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("linePoly"),   in("x"), in("y"), in("sides"), in("radius"), in("rotation"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("triangle"),   in("x"), in("y"), in("x2"),    in("y2"),     in("x3"), in("y3"));
        add(list, V6,  MAX, S, FUNC, Opcode.DRAW,       draw("image"),      in("x"), in("y"), in("image"), in("size"),   in("rotation"));
        add(list, V8A, MAX, S, FUNC, Opcode.DRAW,       draw("print"),      in("x"), in("y"), align("align"));
        add(list, V8A, MAX, S, FUNC, Opcode.DRAW,       draw("translate"),  in("x"), in("y"));
        add(list, V8A, MAX, S, FUNC, Opcode.DRAW,       draw("scale"),      in("x"), in("y"));
        add(list, V8A, MAX, S, FUNC, Opcode.DRAW,       draw("rotate"),     unused("0"), unused("0"), in("degrees"));
        add(list, V8A, MAX, S, FUNC, Opcode.DRAW,       draw("reset"));

        add(list, V6,  MAX, S, FUNC, Opcode.PRINT,      in("what"));
        add(list, V8A, MAX, S, FUNC, Opcode.FORMAT,     in("value"));

        add(list, V6,  MAX, S, BOTH, Opcode.DRAWFLUSH,  block("display1"));
        add(list, V6,  MAX, S, BOTH, Opcode.PRINTFLUSH, block("message1"));

        add(list, V6,  MAX, S, FUNC, Opcode.GETLINK,    res("block"), in("linkNum"));

        add(list, V6,  MAX, S, PROP, Opcode.CONTROL,    bctrl("enabled"),   block("block"), in("value"));
        add(list, V6,  MAX, S, PROP, Opcode.CONTROL,    bctrl("shoot"),     block("block"), in("x"),    in("y"), in("shoot"));
        add(list, V6,  MAX, S, PROP, Opcode.CONTROL,    bctrl("shootp"),    block("block"), in("unit"), in("shoot"));
        add(list, V6,  V6,  S, PROP, Opcode.CONTROL,    bctrl("configure"), block("block"), in("value"));
        add(list, V7,  MAX, S, PROP, Opcode.CONTROL,    bctrl("config"),    block("block"), in("value"));
        add(list, V6,  V6,  S, PROP, Opcode.CONTROL,    bctrl("color"),     block("block"), in("r"), in("g"), in("b"));
        add(list, V7,  MAX, S, PROP, Opcode.CONTROL,    bctrl("color"),     block("block"), in("packedColor"));

        add(list, V6,  MAX, S, BOTH, Opcode.RADAR,      radar("attr1"), radar("attr2"), radar("attr3"), radarSort("sort"), block("turret"), in("order"), res("result"));

        add(list, V6,  MAX, S, PROP, Opcode.SENSOR,     res("result"),      block("object"), sensor("property"));

        add(list, V6,  MAX, S, NONE, Opcode.SET,        res("result"),      block("value"));

        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("add"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("sub"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("mul"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("div"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("idiv"),         res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("mod"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("pow"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("equal"),        res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("notEqual"),     res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("land"),         res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("lessThan"),     res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("lessThanEq"),   res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("greaterThan"),  res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("greaterThanEq"),res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("strictEqual"),  res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("shl"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("shr"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("or"),           res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("and"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("xor"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, NONE, Opcode.OP,         op("not"),          res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("max"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("min"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("angle"),        res("result"),  in("a"), in("b"));
        add(list, V7,  MAX, S, FUNC, Opcode.OP,         op("angleDiff"),    res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("len"),          res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("noise"),        res("result"),  in("a"), in("b"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("abs"),          res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("log"),          res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("log10"),        res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("floor"),        res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("ceil"),         res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("sqrt"),         res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("rand"),         res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("sin"),          res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("cos"),          res("result"),  in("a"));
        add(list, V6,  MAX, S, FUNC, Opcode.OP,         op("tan"),          res("result"),  in("a"));
        add(list, V7,  MAX, S, FUNC, Opcode.OP,         op("asin"),         res("result"),  in("a"));
        add(list, V7,  MAX, S, FUNC, Opcode.OP,         op("acos"),         res("result"),  in("a"));
        add(list, V7,  MAX, S, FUNC, Opcode.OP,         op("atan"),         res("result"),  in("a"));

        add(list, V7,  MAX, S, FUNC, Opcode.LOOKUP,     lookup("type"), res("result"), in("index"));

        add(list, V7,  MAX, S, FUNC, Opcode.PACKCOLOR,  res("result"), in("r"), in("g"), in("b"), in("a"));

        add(list, V7,  MAX, S, FUNC, Opcode.WAIT,       in("sec"));
        add(list, V7,  MAX, S, FUNC, Opcode.STOP);
        add(list, V6,  MAX, S, FUNC, Opcode.END);

        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("equal"),         in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("notEqual"),      in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("lessThan"),      in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("lessThanEq"),    in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThan"),   in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("greaterThanEq"), in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("strictEqual"),   in("x"), in("y"));
        add(list, V6,  MAX, S, NONE, Opcode.JUMP,       label("label"), cond("always"));

        add(list, V6,  MAX, S, FUNC, Opcode.UBIND,      unit("type"));

        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("idle"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("stop"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("move"),      in("x"), in("y"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("approach"),  in("x"), in("y"), in("radius"));
        add(list, V7,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("autoPathfind"));
        add(list, V7,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("pathfind"),  in("x"), in("y"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("boost"),     in("enable"));
        add(list, V6,  V6,  S, FUNC, Opcode.UCONTROL,   uctrl("pathfind"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("target"),    in("x"), in("y"), in("shoot"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("targetp"),   in("unit"), in("shoot"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("itemDrop"),  block("to"), in("amount"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("itemTake"),  block("from"), ore("item"), in("amount"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("payDrop"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("payTake"),   in("takeUnits"));
        add(list, V7,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("payEnter"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("mine"),      in("x"), in("y"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("flag"),      in("value"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("build"),     in("x"), in("y"), in("block"), in("rotation"), in("config"));
        add(list, V6,  V6,  S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"));
        add(list, V7,  V7,  S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), out("building"), out("floor"));
        add(list, V7A, MAX, S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), out("type"), res("building"), out("floor"));
        add(list, V6,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("within"),    in("x"), in("y"), in("radius"), res("result"));
        add(list, V7,  MAX, S, FUNC, Opcode.UCONTROL,   uctrl("unbind"));

        add(list, V6,  MAX, S, FUNC, Opcode.URADAR, radar("attr1"), radar("attr2"), radar("attr3"), radarSort("sort"), unused("0"), in("order"), res("result"));

        add(list, V6,  MAX, S, FUNC, Opcode.ULOCATE, locate("ore"),      unused("core"), unused("true"), ore("oreType"), out("outx"), out("outy"), res("found"), unusedOut("building"));
        add(list, V6,  V7,  S, FUNC, Opcode.ULOCATE, locate("building"), group("group"), in("enemy"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6,  V7,  S, FUNC, Opcode.ULOCATE, locate("spawn"),    unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V6,  V7,  S, FUNC, Opcode.ULOCATE, locate("damaged"),  unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), res("found"), out("building"));
        add(list, V7A, MAX, S, FUNC, Opcode.ULOCATE, locate("building"), group("group"), in("enemy"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));
        add(list, V7A, MAX, S, FUNC, Opcode.ULOCATE, locate("spawn"),    unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));
        add(list, V7A, MAX, S, FUNC, Opcode.ULOCATE, locate("damaged"),  unused("core"), unused("true"), unused("@copper"), out("outx"), out("outy"), out("found"), res("building"));

        add(list, V7,  MAX, W, FUNC, Opcode.GETBLOCK, layer("layer"), res("result"), in("x"), in("y"));

        add(list, V7,  MAX, W, FUNC, Opcode.SETBLOCK, layerset("floor"), in("to"), in("x"), in("y"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETBLOCK, layerset("ore"),  ore("to"), in("x"), in("y"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETBLOCK, layerset("block"), in("to"), in("x"), in("y"), in("team"), in("rotation"));

        add(list, V7,  MAX, W, FUNC, Opcode.SPAWN, unit("unit"), in("x"), in("y"), in("rotation"), in("team"), res("result"));

        add(list, V7,  MAX, W, FUNC, Opcode.STATUS, clear("false"), status("status"), in("unit"), in("duration"));
        add(list, V7,  MAX, W, FUNC, Opcode.STATUS, clear("true"),  status("status"), in("unit"));

        add(list, V8A, MAX, W, FUNC, Opcode.WEATHERSENSE, res("result"),  weather("weather"));
        add(list, V8A, MAX, W, FUNC, Opcode.WEATHERSET,   weather("weather"),  bool("active"));

        add(list, V7,  MAX, W, FUNC, Opcode.SPAWNWAVE, in("x"),  in("y"), in("natural"));

        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("currentWaveTime"),       in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("waveTimer"),             in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("waves"),                 in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("wave"),                  in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("waveSpacing"),           in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("waveSending"),           in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("attackMode"),            in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("enemyCoreBuildRadius"),  in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("dropZoneRadius"),        in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("unitCap"),               in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("mapArea"),               unused("0"), in("x"), in("y"), in("width"), in("height"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("lighting"),              in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("ambientLight"),          in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("solarMultiplier"),       in("value"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("buildSpeed"),            in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("unitHealth"),            in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("unitBuildSpeed"),        in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("unitCost"),              in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("unitDamage"),            in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("blockHealth"),           in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("blockDamage"),           in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("rtsMinWeight"),          in("value"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETRULE,   rule("rtsMinSquad"),           in("value"), in("team"));

        add(list, V7,  V7A, W, FUNC, Opcode.MESSAGE,   message("notify"));
        add(list, V7,  V7A, W, FUNC, Opcode.MESSAGE,   message("mission"));
        add(list, V7,  V7A, W, FUNC, Opcode.MESSAGE,   message("announce"), in("duration"));
        add(list, V7,  V7A, W, FUNC, Opcode.MESSAGE,   message("toast"),    in("duration"));

        add(list, V8A, MAX, W, FUNC, Opcode.MESSAGE,   message("notify"),   unused("0"),    out("success"));
        add(list, V8A, MAX, W, FUNC, Opcode.MESSAGE,   message("mission"),  unused("0"),    out("success"));
        add(list, V8A, MAX, W, FUNC, Opcode.MESSAGE,   message("announce"), in("duration"), out("success"));
        add(list, V8A, MAX, W, FUNC, Opcode.MESSAGE,   message("toast"),    in("duration"), out("success"));

        add(list, V7,  MAX, W, FUNC, Opcode.CUTSCENE,  cutscene("pan"),  in("x"), in("y"), in("speed"));
        add(list, V7,  MAX, W, FUNC, Opcode.CUTSCENE,  cutscene("zoom"), in("level"));
        add(list, V7,  MAX, W, FUNC, Opcode.CUTSCENE,  cutscene("stop"));

        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("warn"),            in("x"), in("y"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("cross"),           in("x"), in("y"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("blockFall"),       in("x"), in("y"), unused("0"), unused("0"), in("blocktype"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("placeBlock"),      in("x"), in("y"), in("size"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("placeBlockSpark"), in("x"), in("y"), in("size"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("breakBlock"),      in("x"), in("y"), in("size"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("spawn"),           in("x"), in("y"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("trail"),           in("x"), in("y"), in("size"),     in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("breakProp"),       in("x"), in("y"), in("size"),     in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeCloud"),      in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("vapor"),           in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("hit"),             in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("hitSquare"),       in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("shootSmall"),      in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("shootBig"),        in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeSmall"),      in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeBig"),        in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeColor"),      in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeSquare"),     in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokeSquareBig"),  in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("spark"),           in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("sparkBig"),        in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("sparkShoot"),      in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("sparkShootBig"),   in("x"), in("y"), in("rotation"), in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("drill"),           in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("drillBig"),        in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("lightBlock"),      in("x"), in("y"), in("size"),     in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("explosion"),       in("x"), in("y"), in("size"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("smokePuff"),       in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("sparkExplosion"),  in("x"), in("y"), unused("0"),    in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("crossExplosion"),  in("x"), in("y"), in("size"),     in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("wave"),            in("x"), in("y"), in("size"),     in("color"));
        add(list, V7,  MAX, W, FUNC, Opcode.EFFECT,  cutscene("bubble"),          in("x"), in("y"));

        add(list, V7,  V7A, W, FUNC, Opcode.EXPLOSION, in("team"), in("x"), in("y"), in("radius"), in("damage"), in("air"), in("ground"), in("pierce"));
        add(list, V8A, MAX, W, FUNC, Opcode.EXPLOSION, in("team"), in("x"), in("y"), in("radius"), in("damage"), in("air"), in("ground"), in("pierce"), bool("effect"));

        add(list, V7,  MAX, W, FUNC, Opcode.SETRATE, in("ipt"));

        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("unitCount"),   res("result"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("playerCount"), res("result"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("coreCount"),   res("result"), in("team"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("buildCount"),  res("result"), in("team"), unused("0"), in("type"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("unit"),        res("result"), in("team"), in("index"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("player"),      res("result"), in("team"), in("index"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("core"),        res("result"), in("team"), in("index"));
        add(list, V7,  MAX, W, FUNC, Opcode.FETCH, fetch("build"),       res("result"), in("team"), in("index"), in("type"));

        add(list, V7,  MAX, W, FUNC, Opcode.SYNC,    glob("var"));
        add(list, V7,  MAX, W, FUNC, Opcode.GETFLAG, res("result"), in("flag"));
        add(list, V7,  MAX, W, FUNC, Opcode.SETFLAG, in("flag"), in("value"));
        add(list, V7,  MAX, W, PROP, Opcode.SETPROP, sensor("property"), block("object"), in("value"));

        add(list, V8A, MAX, W, FUNC, Opcode.PLAYSOUND, scope("true"),  sound("sound"), in("volume"), in("pitch"), unused("0"), in("x"), in("y"), bool("limit"));
        add(list, V8A, MAX, W, FUNC, Opcode.PLAYSOUND, scope("false"), sound("sound"), in("volume"), in("pitch"), in("pan"), unused("0"), unused("0"), bool("limit"));

        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("remove"),      in("id"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("world"),       in("id"), in("boolean"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("minimap"),     in("id"), in("boolean"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("autoscale"),   in("id"), in("boolean"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("pos"),         in("id"), in("x"), in("y"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("endPos"),      in("id"), in("x"), in("y"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("drawLayer"),   in("id"), in("layer"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("color"),       in("id"), in("color"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("radius"),      in("id"), in("radius"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("stroke"),      in("id"), in("stroke"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("rotation"),    in("id"), in("rotation"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("shape"),       in("id"), in("sides"), in("fill"), in("outline"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("arc"),         in("id"), in("from"), in("to"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("flushText"),   in("id"), in("fetch"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("fontSize"),    in("id"), in("size"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("textHeight"),  in("id"), in("height"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("labelFlags"),  in("id"), in("background"), in("outline"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("texture"),     in("id"), in("printFlush"), in("name"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("textureSize"), in("id"), in("width"), in("height"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("posi"),        in("id"), in("index"), in("x"), in("y"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("uvi"),         in("id"), in("index"), in("x"), in("y"));
        add(list, V8A, MAX, W, FUNC, Opcode.SETMARKER, setmarker("colori"),      in("id"), in("index"), in("color"));

        add(list, V8A, MAX, W, FUNC, Opcode.MAKEMARKER, makemarker("marker"),    in("id"), in("x"), in("y"), bool("replace"));

        add(list, V8A, MAX, W, FUNC, Opcode.LOCALEPRINT, in("property"));

        // Unit testing support
        add(list, V6,  MAX, S, NONE, Opcode.ASSERT_EQUALS, in("expected"), in("actual"), in("title"));
        add(list, V6,  MAX, S, NONE, Opcode.ASSERT_PRINTS, in("expected"), in("title"));
        add(list, V6,  MAX, S, NONE, Opcode.ASSERT_FLUSH);

        // Virtual instructions
        add(list, V6,  MAX, S, NONE, Opcode.NOOP);
        add(list, V6,  MAX, S, NONE, Opcode.LABEL,       label("label"));
        add(list, V6,  MAX, S, NONE, Opcode.GOTOLABEL,   label("address"), label("marker"));
        add(list, V6,  MAX, S, NONE, Opcode.PUSH,        block("memory"), in("value"));
        add(list, V6,  MAX, S, NONE, Opcode.POP,         block("memory"), out("value"));
        add(list, V6,  MAX, S, NONE, Opcode.CALL,        label("callAddr"), out("retval"));
        add(list, V6,  MAX, S, NONE, Opcode.CALLREC,     block("memory"), label("callAddr"), label("retAddr"), out("retval"));
        add(list, V6,  MAX, S, NONE, Opcode.RETURN,      block("memory"));
        add(list, V6,  MAX, S, NONE, Opcode.GOTO,        in("address"), label("marker"));
        add(list, V6,  MAX, S, NONE, Opcode.GOTOOFFSET,  label("address"), in("value"), in("offset"), label("marker"));
        add(list, V6,  MAX, S, NONE, Opcode.SETADDR,     out("result"),   in("address"));
        add(list, V6,  MAX, S, NONE, Opcode.REMARK,      in("remark"));

        return List.copyOf(list);
    }

    public static NamedParameter align(String name) {
        return new NamedParameter(InstructionParameterType.ALIGNMENT, name);
    }

    public static NamedParameter bctrl(String name) {
        return new NamedParameter(InstructionParameterType.BLOCK_CONTROL, name);
    }

    public static NamedParameter block(String name) {
        return new NamedParameter(InstructionParameterType.BLOCK, name);
    }

    public static NamedParameter bool(String name) {
        return new NamedParameter(InstructionParameterType.BOOL, name);
    }

    public static NamedParameter clear(String name) {
        return new NamedParameter(InstructionParameterType.CLEAR, name);
    }

    public static NamedParameter cond(String name) {
        return new NamedParameter(InstructionParameterType.CONDITION, name);
    }

    public static NamedParameter cutscene(String name) {
        return new NamedParameter(InstructionParameterType.CUTSCENE, name);
    }

    public static NamedParameter draw(String name) {
        return new NamedParameter(InstructionParameterType.DRAW, name);
    }

    public static NamedParameter effect(String name) {
        return new NamedParameter(InstructionParameterType.EFFECT, name);
    }

    public static NamedParameter fetch(String name) {
        return new NamedParameter(InstructionParameterType.FETCH, name);
    }

    public static NamedParameter glob(String name) {
        return new NamedParameter(InstructionParameterType.GLOBAL, name);
    }
    public static NamedParameter group(String name) {
        return new NamedParameter(InstructionParameterType.GROUP, name);
    }

    public static NamedParameter in(String name) {
        return new NamedParameter(InstructionParameterType.INPUT, name);
    }

    public static NamedParameter label(String name) {
        return new NamedParameter(InstructionParameterType.LABEL, name);
    }

    public static NamedParameter layer(String name) {
        return new NamedParameter(InstructionParameterType.LAYER, name);
    }

    public static NamedParameter layerset(String name) {
        return new NamedParameter(InstructionParameterType.SETTABLE_LAYER, name);
    }

    public static NamedParameter locate(String name) {
        return new NamedParameter(InstructionParameterType.LOCATE, name);
    }

    public static NamedParameter lookup(String name) {
        return new NamedParameter(InstructionParameterType.LOOKUP, name);
    }

    public static NamedParameter makemarker(String name) {
        return new NamedParameter(InstructionParameterType.MAKE_MARKER, name);
    }

    public static NamedParameter message(String name) {
        return new NamedParameter(InstructionParameterType.MESSAGE, name);
    }

    public static NamedParameter op(String name) {
        return new NamedParameter(InstructionParameterType.OPERATION, name, Operation.fromMlog(name));
    }

    public static NamedParameter ore(String name) {
        return new NamedParameter(InstructionParameterType.ORE, name);
    }

    public static NamedParameter out(String name) {
        return new NamedParameter(InstructionParameterType.OUTPUT, name);
    }

    public static NamedParameter radar(String name) {
        return new NamedParameter(InstructionParameterType.RADAR, name);
    }

    public static NamedParameter radarSort(String name) {
        return new NamedParameter(InstructionParameterType.RADAR_SORT, name);
    }

    public static NamedParameter res(String name) {
        return new NamedParameter(InstructionParameterType.RESULT, name);
    }

    public static NamedParameter rule(String name) {
        return new NamedParameter(InstructionParameterType.RULE, name);
    }

    public static NamedParameter scope(String name) {
        return new NamedParameter(InstructionParameterType.SCOPE, name);
    }

    public static NamedParameter setmarker(String name) {
        return new NamedParameter(InstructionParameterType.SET_MARKER, name);
    }

    public static NamedParameter sensor(String name) {
        return new NamedParameter(InstructionParameterType.SENSOR, name);
    }

    public static NamedParameter sound(String name) {
        return new NamedParameter(InstructionParameterType.SOUND, name);
    }

    public static NamedParameter status(String name) {
        return new NamedParameter(InstructionParameterType.STATUS, name);
    }

    public static NamedParameter uctrl(String name) {
        return new NamedParameter(InstructionParameterType.UNIT_CONTROL, name);
    }

    public static NamedParameter unit(String name) {
        return new NamedParameter(InstructionParameterType.UNIT, name);
    }

    public static NamedParameter unused(String name) {
        return new NamedParameter(InstructionParameterType.UNUSED, name);
    }

    public static NamedParameter unusedOut(String name) {
        return new NamedParameter(InstructionParameterType.UNUSED_OUTPUT, name);
    }

    public static NamedParameter weather(String name) {
        return new NamedParameter(InstructionParameterType.WEATHER, name);
    }
}
