package info.teksol.mindcode.mindustry.logic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.teksol.mindcode.mindustry.logic.ArgumentType.*;
import static info.teksol.mindcode.mindustry.logic.ProcessorVersion.*;

/**
 * Defines instruction opcodes and all information about instruction arguments for given opcode.
 * For instructions whose argument list depend on a specific argument, all existing variants
 * are described.
 */
public enum Opcode {
    READ            (V6, "read",        OUTPUT, BLOCK, INPUT),
    WRITE           (V6, "write",       INPUT, BLOCK, INPUT),
    DRAW            (V6, "draw",        List.of(
            new OpcodeVariant("clear",      ArgumentType.DRAW, INPUT, INPUT, INPUT),
            new OpcodeVariant("color",      ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("stroke",     ArgumentType.DRAW, INPUT),
            new OpcodeVariant("line",       ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("rect",       ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("lineRect",   ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("poly",       ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("linePoly",   ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("triangle",   ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("image",      ArgumentType.DRAW, INPUT, INPUT, INPUT, INPUT, INPUT))),
    PRINT           (V6, "print",       INPUT),
    DRAWFLUSH       (V6, "drawflush",   BLOCK),
    PRINTFLUSH      (V6, "printflush",  BLOCK),
    GETLINK         (V6, "getlink",     OUTPUT, INPUT),
    CONTROL         (V6, "control",     List.of(
            new OpcodeVariant("enabled",    BLOCK_CONTROL, BLOCK, INPUT),
            new OpcodeVariant("shoot",      BLOCK_CONTROL, BLOCK, INPUT, INPUT, INPUT),
            new OpcodeVariant("shootp",     BLOCK_CONTROL, BLOCK, INPUT, INPUT),
            new OpcodeVariant("configure",  BLOCK_CONTROL, BLOCK, INPUT),
            new OpcodeVariant("color",      BLOCK_CONTROL, BLOCK, INPUT, INPUT, INPUT)),
                    1),
    RADAR           (V6, "radar",       ArgumentType.RADAR, ArgumentType.RADAR, ArgumentType.RADAR, SORT, BLOCK, INPUT, OUTPUT),
    SENSOR          (V6, "sensor",      OUTPUT, BLOCK, ArgumentType.SENSOR),
    SET             (V6, "set",         OUTPUT, INPUT),
    END             (V6, "end"),
    OP              (V6, "op",          convertOpcodeMultiVariants(List.of(
            new OpcodeMultiVariant("add,sub,mul,div,idiv,mod,pow,equal,notEqual,lessThan,lessThanEq,greaterThan," +
                    "greaterThanEq,strictEqual,land,or,and,xor,shl,shr,max,min,angle,len,noise",
                    List.of(OPERATION, OUTPUT, INPUT, INPUT)),
            new OpcodeMultiVariant("not,abs,log,log10,sin,cos,tan,floor,ceil,sqrt,rand",
                    List.of(OPERATION, OUTPUT, INPUT))))),
    JUMP            (V6, "jump",        List.of(
            new OpcodeVariant("always",     ArgumentType.LABEL, CONDITION),
            new OpcodeVariant("",           ArgumentType.LABEL, CONDITION, INPUT, INPUT))) {
        @Override protected String getOpcodeVariantKeyword(List<String> arguments) { return arguments.get(1); }
    },
    UBIND           (V6, "ubind",       UNIT),
    UCONTROL        (V6, "ucontrol",    List.of(
            new OpcodeVariant("idle",       UNIT_CONTROL),
            new OpcodeVariant("stop",       UNIT_CONTROL),
            new OpcodeVariant("move",       UNIT_CONTROL, INPUT, INPUT),
            new OpcodeVariant("approach",   UNIT_CONTROL, INPUT, INPUT, INPUT),
            new OpcodeVariant("boost",      UNIT_CONTROL, INPUT),
            new OpcodeVariant("pathfind",   UNIT_CONTROL),
            new OpcodeVariant("target",     UNIT_CONTROL, INPUT, INPUT, INPUT),
            new OpcodeVariant("targetp",    UNIT_CONTROL, INPUT, INPUT),
            new OpcodeVariant("itemDrop",   UNIT_CONTROL, BLOCK, INPUT),
            new OpcodeVariant("itemTake",   UNIT_CONTROL, BLOCK, ORE, INPUT),
            new OpcodeVariant("payDrop",    UNIT_CONTROL),
            new OpcodeVariant("payTake",    UNIT_CONTROL, INPUT),
            new OpcodeVariant("mine",       UNIT_CONTROL, INPUT, INPUT),
            new OpcodeVariant("flag",       UNIT_CONTROL, INPUT),
            new OpcodeVariant("build",      UNIT_CONTROL, INPUT, INPUT, INPUT, INPUT, INPUT),
            new OpcodeVariant("getBlock",   UNIT_CONTROL, INPUT, INPUT, OUTPUT, OUTPUT),
            new OpcodeVariant("within",     UNIT_CONTROL, INPUT, INPUT, INPUT, OUTPUT))),
    URADAR          (V6, "uradar",      ArgumentType.RADAR, ArgumentType.RADAR, ArgumentType.RADAR, SORT, UNUSED, INPUT, OUTPUT),
    ULOCATE         (V6, "ulocate",     List.of(
            new OpcodeVariant("ore",        LOCATE, UNUSED, UNUSED, ORE, OUTPUT, OUTPUT, OUTPUT, UNUSED),
            new OpcodeVariant("building",   LOCATE, GROUP, INPUT, UNUSED, OUTPUT, OUTPUT, OUTPUT, OUTPUT),
            new OpcodeVariant("spawn",      LOCATE, UNUSED, UNUSED, UNUSED, OUTPUT, OUTPUT, OUTPUT, OUTPUT),
            new OpcodeVariant("damaged",    LOCATE, UNUSED, UNUSED, UNUSED, OUTPUT, OUTPUT, OUTPUT, OUTPUT))),
    WAIT            (V7, "wait",        INPUT),
    LABEL           (NONE, "label",     ArgumentType.LABEL),
    ;
    
    private final String opcode;
    private final Map<String, OpcodeVariant> argumentTypeMap;
    private final int totalArguments;
    private final int totalInputs;
    private final int totalOutputs;
    private final int additionalArgumentForPrint;
    private final ProcessorVersion mindustryVersion;

    private Opcode(ProcessorVersion mindustryVersion, String code, ArgumentType... argumentTypes) {
        this(mindustryVersion, code, Map.of("", new OpcodeVariant("", argumentTypes)), 0);
    }

    private Opcode(ProcessorVersion mindustryVersion, String code, List<OpcodeVariant> variants) {
        this(mindustryVersion, code, convertOpcodeVariants(variants), 0);
    }

    private Opcode(ProcessorVersion mindustryVersion, String code, List<OpcodeVariant> variants, int additionalArgumentForPrint) {
        this(mindustryVersion, code, convertOpcodeVariants(variants), additionalArgumentForPrint);
    }

    private Opcode(ProcessorVersion mindustryVersion, String code, Map<String, OpcodeVariant> argumentTypeMap) {
        this(mindustryVersion, code, argumentTypeMap, 0);
    }

    private Opcode(ProcessorVersion mindustryVersion, String code, Map<String, OpcodeVariant> argumentTypeMap,
            int additionalArgumentForPrint) {
        this.mindustryVersion = mindustryVersion;
        this.opcode = code;
        this.argumentTypeMap = argumentTypeMap;
        this.additionalArgumentForPrint = additionalArgumentForPrint;
        totalArguments = argumentTypeMap.values().stream().mapToInt(OpcodeVariant::getArgumentCount).max().getAsInt();
        totalInputs = argumentTypeMap.values().stream().mapToInt(OpcodeVariant::getInputs).max().getAsInt();
        totalOutputs = argumentTypeMap.values().stream().mapToInt(OpcodeVariant::getOutputs).max().getAsInt();
    }
    
    private static Map<String, OpcodeVariant> convertOpcodeVariants(List<OpcodeVariant> variants) {
        return variants.stream().collect(Collectors.toUnmodifiableMap(v -> v.keyword, v -> v));
    }

    private static Map<String, OpcodeVariant> convertOpcodeMultiVariants(List<OpcodeMultiVariant> variants) {
        return variants.stream().flatMap(v -> v.keywords.stream().map(k -> new OpcodeVariant(k, v.argumentTypeList)))
                .collect(Collectors.toUnmodifiableMap(v -> v.keyword, v -> v));
    }

    public String getOpcode() {
        return opcode;
    }
    
    public int getNumArgsForPrint() {
        return totalArguments + additionalArgumentForPrint;
    }
    
    public int getTotalArguments() {
        return totalArguments;
    }

    public int getTotalInputs() {
        return totalInputs;
    }
    
    public int getTotalOutputs() {
        return totalOutputs;
    }
    
    protected String getOpcodeVariantKeyword(List<String> arguments) {
        return arguments.isEmpty() ? "" : arguments.get(0);
    }

    /**
     * Returns list of argument types based on instruction opcode and instruction variant. The variant
     * of the instruction is determined by inspecting its arguments.
     * 
     * @param arguments arguments to the instruction
     * @return list of types of given arguments
     */
    public List<ArgumentType> getArgumentTypes(List<String> arguments) {
        String keyword = getOpcodeVariantKeyword(arguments);
        return argumentTypeMap.containsKey(keyword)
                ? argumentTypeMap.get(keyword).argumentTypeList
                : argumentTypeMap.get("").argumentTypeList;
    }

    /**
     * Assigns types to instruction arguments. Types depend on the opcode and instruction variant. The variant
     * of the instruction is determined by inspecting its arguments.
     * 
     * @param arguments arguments to the instruction
     * @return list of typed arguments
     */
    public Stream<TypedArgument> getTypedArguments(List<String> arguments) {
        List<ArgumentType> types = getArgumentTypes(arguments);
        return IntStream.range(0, arguments.size())
                .mapToObj(i -> new TypedArgument(types.get(i), arguments.get(i)));
    }

    /**
     * Determines the types of arguments based on instruction variant and returns values of arguments
     * which are input in the particular instruction variant.
     * 
     * @param arguments actual argument values
     * @return list of argument values assigned to input arguments
     */
    public List<String> getInputValues(List<String> arguments) {
        return getTypedArguments(arguments)
                .filter(a -> a.getArgumentType().isInput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Determines the types of arguments based on instruction variant and returns values of arguments
     * which are output in the particular instruction variant.
     * 
     * @param arguments actual argument values
     * @return list of argument values assigned to output arguments
     */
    public List<String> getOutputValues(List<String> arguments) {
        return getTypedArguments(arguments)
                .filter(a -> a.getArgumentType().isOutput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return opcode;
    }
    
    public static class TypedArgument {
        private final ArgumentType argumentType;
        private final String value;

        public TypedArgument(ArgumentType argumentType, String value) {
            this.argumentType = argumentType;
            this.value = value;
        }

        public ArgumentType getArgumentType() {
            return argumentType;
        }

        public String getValue() {
            return value;
        }
    }

    private static class OpcodeVariant {
        public final String keyword;
        public final List<ArgumentType> argumentTypeList;
        public final int inputs;
        public final int outputs;

        public OpcodeVariant(String keyword, ArgumentType... argumentTypes) {
            this(keyword, List.of(argumentTypes));
        }
        
        public OpcodeVariant(String keyword, List<ArgumentType> argumentTypeList) {
            this.keyword = keyword;
            this.argumentTypeList = argumentTypeList;
            inputs = (int) argumentTypeList.stream().filter(ArgumentType::isInput).count();
            outputs = (int) argumentTypeList.stream().filter(ArgumentType::isOutput).count();
        }

        public int getArgumentCount() {
            return argumentTypeList.size();
        }

        public int getInputs() {
            return inputs;
        }

        public int getOutputs() {
            return outputs;
        }
    }

    private static class OpcodeMultiVariant {
        public final List<String> keywords;
        public final List<ArgumentType> argumentTypeList;

        public OpcodeMultiVariant(String keywords, List<ArgumentType> argumentTypeList) {
            this.keywords = List.of(keywords.split(","));
            this.argumentTypeList = argumentTypeList;
        }
    }
}
