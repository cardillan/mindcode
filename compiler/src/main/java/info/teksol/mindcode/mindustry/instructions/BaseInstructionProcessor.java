package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.logic.ArgumentType;
import info.teksol.mindcode.mindustry.logic.NamedArgument;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.OpcodeVariant;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.teksol.util.CollectionUtils.*;

public class BaseInstructionProcessor implements InstructionProcessor {
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<ArgumentType, Set<String>> validArgumentValues;
    private int tmpIndex = 0;
    private int labelIndex = 0;

    // Protected to allow a subclass to use this constructor in unit tests
    protected BaseInstructionProcessor(ProcessorVersion processorVersion, ProcessorEdition processorEdition,
            List<OpcodeVariant> opcodeVariants) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        this.opcodeVariants = opcodeVariants;
        variantsByOpcode = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::getOpcode));
        opcodeKeywordPosition = variantsByOpcode.keySet().stream()
                .collect(Collectors.toMap(k -> k, k -> getOpcodeVariantSelectorPosition(k, variantsByOpcode.get(k))));

        variantsByKeyword = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::getOpcode,
                Collectors.toMap(this::getOpcodeVariantKeyword, v -> v)));

        validArgumentValues = createAllowedArgumentValuesMap();
    }


    @Override
    public String nextLabel() {
        return getLabelPrefix() + labelIndex++;
    }

    @Override
    public String nextTemp() {
        return getTempPrefix() + tmpIndex++;
    }

    @Override
    public ProcessorVersion getProcessorVersion() {
        return processorVersion;
    }

    @Override
    public ProcessorEdition getProcessorEdition() {
        return processorEdition;
    }

    @Override
    public List<OpcodeVariant> getOpcodeVariants() {
        return opcodeVariants;
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<String> arguments) {
        return validate(new LogicInstruction(opcode, arguments));
    }

    @Override
    public LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant) {
        return new LogicInstruction(opcodeVariant.getOpcode(),
                opcodeVariant.getArguments().stream().map(NamedArgument::getName).collect(Collectors.toList())
        );
    }

    @Override
    public LogicInstruction createInstructionUnchecked(Opcode opcode, List<String> arguments) {
        return new LogicInstruction(opcode, arguments);
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, String... arguments) {
        return createInstruction(opcode, List.of(arguments));
    }

    @Override
    public LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String arg) {
        if (instruction.getArg(argIndex).equals(arg)) {
            return instruction;
        }
        else {
            List<String> newArgs = new ArrayList<>(instruction.getArgs());
            newArgs.set(argIndex, arg);
            return createInstruction(instruction.getOpcode(), newArgs);
        }
    }

    @Override
    public LogicInstruction replaceAllArgs(LogicInstruction instruction, String oldArg, String newArg) {
        List<String> args = new ArrayList<>(instruction.getArgs());
        args.replaceAll(arg -> arg.equals(oldArg) ? newArg : arg);
        return createInstruction(instruction.getOpcode(), args);
    }

    @Override
    public List<ArgumentType> getArgumentTypes(LogicInstruction instruction) {
        return getArgumentTypes(instruction.getOpcode(), instruction.getArgs());
    }

    @Override
    public int getTotalInputs(LogicInstruction instruction) {
        return (int) getArgumentTypes(instruction).stream().filter(ArgumentType::isInput).count();
    }

    @Override
    public int getTotalOutputs(LogicInstruction instruction) {
        return (int) getArgumentTypes(instruction).stream().filter(ArgumentType::isOutput).count();
    }

    @Override
    public int getPrintArgumentCount(LogicInstruction instruction) {
        // Maximum over all existing opcode variants, plus additional opcode-specific unused arguments
        return variantsByOpcode.get(instruction.getOpcode()).stream()
                .mapToInt(v -> v.getArguments().size()).max().orElse(0)
                + instruction.getOpcode().getAdditionalPrintArguments();
    }

    @Override
    public List<String> getInputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isInput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<String> getOutputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isOutput())
                .map(TypedArgument::getValue)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Stream<TypedArgument> getTypedArguments(LogicInstruction instruction) {
        List<ArgumentType> types = getArgumentTypes(instruction);
        return IntStream.range(0, types.size())
                .mapToObj(i -> new TypedArgument(types.get(i), instruction.getArgs().get(i)));
    }


    @Override
    public boolean isValid(ArgumentType type, String value) {
        if (type.restrictValues()) {
            Set<String> values = validArgumentValues.get(type);
            return values.contains(value);
        } else {
            return true;
        }
    }

    protected OpcodeVariant getOpcodeVariant(Opcode opcode, List<String> arguments) {
        List<OpcodeVariant> variants = variantsByOpcode.get(opcode);
        if (variants == null) {
            return null;
        } else if (variants.size() == 1) {
            return variants.get(0);
        } else {
            // Selector keyword position in the list
            int position = opcodeKeywordPosition.get(opcode);

            // We know that variantsByKeyword contains opcode, as variantsByOpcode.get(opcode) isn't null
            return position >= arguments.size() ? null : variantsByKeyword.get(opcode).get(arguments.get(position));
        }
    }

    /**
     * Returns list of argument types based on instruction opcode and instruction variant. The variant of the
     * instruction is determined by inspecting its arguments.
     *
     * @param opcode
     * @param arguments arguments to the instruction
     * @return list of types of given arguments
     */
    protected List<ArgumentType> getArgumentTypes(Opcode opcode, List<String> arguments) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, arguments);
        return opcodeVariant.getArguments().stream().map(NamedArgument::getType).collect(Collectors.toList());
    }

    protected LogicInstruction validate(LogicInstruction instruction) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(instruction.getOpcode(), instruction.getArgs());
        if (opcodeVariant == null) {
            throw new GenerationException("Invalid or version incompatible instruction " + instruction);
        }

        if (instruction.getArgs().size() != opcodeVariant.getArguments().size()) {
            throw new GenerationException("Wrong number of arguments of instruction " + instruction.getOpcode()
                    + " (expected " + opcodeVariant.getArguments().size() + "). " + instruction);
        }

        for (int i = 0; i < instruction.getArgs().size(); i++) {
            String argument = instruction.getArgs().get(i);
            ArgumentType type = opcodeVariant.getArguments().get(i).getType();
            if (!isValid(type, argument)) {
                throw new GenerationException("Argument " + argument + " not compatible with argument type " + type + ". " + instruction);
            }
        }

        return instruction;
    }

    private int getOpcodeVariantSelectorPosition(Opcode opcode, List<OpcodeVariant> opcodeVariants) {
        List<Integer> indexes = opcodeVariants.stream()
                .map(v -> findFirstIndex(v.getArguments(), a -> a.getType().isSelector()))
                .distinct()
                .collect(Collectors.toList());

        if (indexes.size() != 1)  {
            throw new MindcodeException("Cannot determine variant selector position for opcode " + opcode);
        }

        return indexes.get(0);
    }

    private String getOpcodeVariantKeyword(OpcodeVariant opcodeVariant) {
        int position = opcodeKeywordPosition.get(opcodeVariant.getOpcode());
        if (position < 0) {
            return "";      // Single-variant opcode; no keyword
        } else {
            return opcodeVariant.getArguments().get(position).getName();
        }
    }

    private Map<ArgumentType, Set<String>> createAllowedArgumentValuesMap() {
        Map<ArgumentType, Set<String>> map = new HashMap<>();
        for (ArgumentType type : ArgumentType.values()) {
            Set<String> allowedValues = createAllowedValues(type);
            if (!allowedValues.isEmpty()) {
                map.put(type, allowedValues);
            }
        }

        return Map.copyOf(map);
    }

    private Set<String> createAllowedValues(ArgumentType type) {
        if (type.isSelector()) {
            return opcodeVariants.stream()
                    .flatMap(v -> v.getArguments().stream())
                    .filter(v -> v.getType() == type)
                    .map(NamedArgument::getName)
                    .collect(Collectors.toUnmodifiableSet());
        } else {
            // Select only compatible keywords and put them into a set
            return type.getAllowedValues().stream()
                    .filter(v -> v.versions.contains(processorVersion))
                    .flatMap(v -> v.values.stream())
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    // These structures are static for now. Can be made version dependent if needed in the future.
    private static final Map<String, String> UNARY_OPERATORS = Map.of(
            "not",  "not",
            "!",    "not"
    );

    private static final Map<String, String> BINARY_OPERATORS = createBinaryOperatorsMap();

    private static Map<String, String> createBinaryOperatorsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("+",    "add");
        map.put("-",    "sub");
        map.put("*",    "mul");
        map.put("/",    "div");
        map.put("\\",   "idiv");
        map.put("==",   "equal");
        map.put("!=",   "notEqual");
        map.put("<",    "lessThan");
        map.put("<=",   "lessThanEq");
        map.put(">=",   "greaterThanEq");
        map.put(">",    "greaterThan");
        map.put("===",  "strictEqual");
        map.put("**",   "pow");
        map.put("||",   "or");
        map.put("or",   "or");
        map.put("&&",   "land");  // logical-and
        map.put("and",  "land");  // logical-and
        map.put("%",    "mod");
        map.put("<<",   "shl");
        map.put(">>",   "shr");
        map.put("&",    "and");
        map.put("|",    "or");
        map.put("^",    "xor");
        return Map.copyOf(map);
    }

    private static final Map<String, String> INVERSES = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan"
    );

    private static final Set<String> CONSTANT_NAMES = Set.of("true", "false", "null");

    private static final Set<String> BLOCK_NAMES = Set.of("arc", "bank", "battery", "cell", "center", "centrifuge",
            "compressor", "conduit", "container", "conveyor", "crucible", "cultivator", "cyclone", "diode",
            "disassembler", "display", "distributor", "dome", "door", "drill", "driver", "duo", "extractor", "factory",
            "foreshadow", "foundation", "fuse", "gate", "generator", "hail", "incinerator", "junction", "kiln", "lancer",
            "meltdown", "melter", "mender", "message", "mine", "mixer", "node", "nucleus", "panel", "parallax", "point",
            "press", "processor", "projector", "pulverizer", "reactor", "reconstructor", "ripple", "router", "salvo",
            "scatter", "scorch", "segment", "separator", "shard", "smelter", "sorter", "spectre", "swarmer", "switch",
            "tank", "tower", "tsunami", "unloader", "vault", "wall", "wave", "weaver");

    @Override
    public String translateUnaryOpToCode(String op) {
        String translated = UNARY_OPERATORS.get(op);
        if (translated == null) {
            throw new GenerationException("Failed to translate unary operator to Mindustry representation: [" + op + "] is not handled");
        } else {
            return translated;
        }
    }

    @Override
    public String translateBinaryOpToCode(String op) {
        String translated = BINARY_OPERATORS.get(op);
        if (translated == null) {
            throw new GenerationException("Failed to translate binary operator to Mindustry representation: [" + op + "] is not handled");
        } else {
            return translated;
        }
    }

    @Override
    public boolean hasInverse(String comparison) {
        return INVERSES.containsKey(comparison);
    }

    @Override
    public String getInverse(String comparison) {
        return INVERSES.get(comparison);
    }

    @Override
    public Set<String> getConstantNames() {
        return CONSTANT_NAMES;
    }

    @Override
    public Set<String> getBlockNames() {
        return BLOCK_NAMES;
    }
}
