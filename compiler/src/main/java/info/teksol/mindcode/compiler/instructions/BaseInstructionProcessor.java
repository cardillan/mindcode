package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.ExpressionEvaluator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.teksol.util.CollectionUtils.findFirstIndex;

public class BaseInstructionProcessor implements InstructionProcessor {
    private final Consumer<CompilerMessage> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<ArgumentType, Set<String>> validArgumentValues;
    private final Map<String, String> functionByPrefix = new HashMap<>();
    private int tmpIndex = 0;
    private int labelIndex = 0;
    private int functionIndex = 0;

    // Protected to allow a subclass to use this constructor in unit tests
    protected BaseInstructionProcessor(Consumer<CompilerMessage> messageConsumer, ProcessorVersion processorVersion,
                                       ProcessorEdition processorEdition, List<OpcodeVariant> opcodeVariants) {
        this.messageConsumer = messageConsumer;
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
    public String nextReturnValue() {
        return getRetValPrefix() + tmpIndex++;
    }

    @Override
    public String nextLocalPrefix(String functionName) {
        String prefix = getLocalPrefix() + functionIndex++;
        functionByPrefix.put(prefix, functionName);
        return prefix;
    }

    @Override
    public Tuple2<String, String> parseVariable(String id) {
        if (id.startsWith(getLocalPrefix())) {
            int index = id.indexOf('_', getLocalPrefix().length());
            return new Tuple2<>(functionByPrefix.get(id.substring(0, index)), id.substring(index + 1));
        } else {
            return new Tuple2<>(null, id);
        }
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
    public LogicInstruction createInstruction(Opcode opcode, String... arguments) {
        return createInstruction(null, opcode, List.of(arguments));
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<String> arguments) {
        return createInstruction(null, opcode, arguments);
    }

    @Override
    public LogicInstruction createInstruction(String marker, Opcode opcode, String... arguments) {
        return createInstruction(marker, opcode, List.of(arguments));
    }

    @Override
    public LogicInstruction createInstruction(String marker, Opcode opcode, List<String> arguments) {
        return validate(createInstructionUnchecked(marker, opcode, arguments));
    }

    @Override
    public LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant) {
        return createInstruction(opcodeVariant.getOpcode(),
                opcodeVariant.getArguments().stream().map(NamedArgument::getName).collect(Collectors.toList())
        );
    }

    @Override
    public LogicInstruction createInstructionUnchecked(String marker, Opcode opcode, List<String> arguments) {
        return switch (opcode) {
            case CALL       -> new CallInstruction(marker, opcode, arguments);
            case END        -> new EndInstruction(marker, opcode, arguments);
            case GOTO       -> new GotoInstruction(marker, opcode, arguments);
            case JUMP       -> new JumpInstruction(marker, opcode, arguments);
            case LABEL      -> new LabelInstruction(marker, opcode, arguments);
            case OP         -> new OpInstruction(marker, opcode, arguments);
            case PACKCOLOR  -> new PackColorInstruction(marker, opcode, arguments);
            case POP        -> new PopInstruction(marker, opcode, arguments);
            case PRINT      -> new PrintInstruction(marker, opcode, arguments);
            case PRINTFLUSH -> new PrintflushInstruction(marker, opcode, arguments);
            case PUSH       -> new PushInstruction(marker, opcode, arguments);
            case READ       -> new ReadInstruction(marker, opcode, arguments);
            case RETURN     -> new ReturnInstruction(marker, opcode, arguments);
            case SET        -> new SetInstruction(marker, opcode, arguments);
            case STOP       -> new StopInstruction(marker, opcode, arguments);
            case WRITE      -> new WriteInstruction(marker, opcode, arguments);
            default         -> new BaseInstruction(marker, opcode, arguments);
        };
    }

    @Override
    public List<LogicInstruction> resolve(LogicInstruction virtualInstruction) {
        return switch (virtualInstruction) {
            case LabelInstruction ix -> List.of();

            case PushInstruction ix -> List.of(
                    createInstruction(Opcode.WRITE, ix.getValue(), ix.getMemory(), getStackPointer()),
                    createInstruction(Opcode.OP, "add", getStackPointer(), getStackPointer(), "1"));

            case PopInstruction ix -> List.of(
                    createInstruction(Opcode.OP, "sub", getStackPointer(), getStackPointer(), "1"),
                    createInstruction(Opcode.READ, ix.getValue(), ix.getMemory(), getStackPointer())
            );

            case CallInstruction ix -> List.of(
                    createInstruction(Opcode.WRITE, ix.getReturn(), ix.getMemory(), getStackPointer()),
                    createInstruction(Opcode.OP, "add", getStackPointer(), getStackPointer(), "1"),
                    createInstruction(Opcode.SET, "@counter", ix.getTarget())
            );

            case ReturnInstruction ix -> {
                String retAddr = nextTemp();
                yield List.of(
                        createInstruction(Opcode.OP, "sub", getStackPointer(), getStackPointer(), "1"),
                        createInstruction(Opcode.READ, retAddr, ix.getMemory(), getStackPointer()),
                        createInstruction(Opcode.SET, "@counter", retAddr)
                );
            }

            case GotoInstruction ix -> List.of(
                    createInstruction(Opcode.SET, "@counter", ix.getLabel())
            );

            default ->
                    throw new GenerationException("Don't know how to resolve virtual instruction " + virtualInstruction);
        };
    }

    @Override
    public int getRealSize(LogicInstruction instruction) {
        return instruction.getOpcode().getSize();
    }

    @Override
    public boolean isTemporary(String varRef) {
        return varRef.startsWith(getTempPrefix());
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
                .toList();
    }

    @Override
    public List<String> getOutputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isOutput())
                .map(TypedArgument::getValue)
                .toList();
    }

    @Override
    public List<String> getInputOutputValues(LogicInstruction instruction) {
        return getTypedArguments(instruction)
                .filter(a -> a.getArgumentType().isInputOrOutput())
                .map(TypedArgument::getValue)
                .toList();
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
     * @param opcode opcode of the instruction
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
                .toList();

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

    private static final Pattern BLOCK_NAME_PATTERN = Pattern.compile("^([a-zA-Z][a-zA-Z_]*)[1-9]\\d*$");

    private static final Set<String> VOLATILE_NAMES = Set.of("@counter", "@time", "@tick", "@second", "@minute",
            "@waveNumber", "@waveTime");

    @Override
    public boolean isVolatile(String identifier) {
        return VOLATILE_NAMES.contains(identifier);
    }

    @Override
    public boolean isBlockName(String identifier) {
        Matcher matcher = BLOCK_NAME_PATTERN.matcher(identifier);
        return matcher.find() && getBlockNames().contains(matcher.group(1));
    }

    @Override
    public boolean isGlobalName(String identifier) {
        char ch = identifier.charAt(0);
        // If it doesn't start with one of those characters, it isn't an identifier
        return (ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                && identifier.equals(identifier.toUpperCase());
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
    public String translateOpToCode(String op) {
        String translated = ExpressionEvaluator.translateOperator(op);
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

    public Optional<String> mlogRewrite(String literal) {
        try {
            return mlogFormat(Double.parseDouble(literal), literal);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> mlogFormat(double value) {
        return mlogFormat(value, String.valueOf(value));
    }

    // 20 digits precision
    private static final MathContext CONVERSION_CONTEXT = new MathContext(17, RoundingMode.HALF_UP);
    private static final BigDecimal LOSS_THRESHOLD = new BigDecimal("1e-8");

    private Optional<String> mlogFormat(double value, String literal) {
        if (Double.isFinite(value)) {
            double abs = Math.abs(value);

            // Is it zero?
            if (abs <= Double.MIN_NORMAL) {
                return Optional.of("0");
            }

            if (1e-20 <= abs && abs < Long.MAX_VALUE) {
                // Fits into a long, Mindustry can convert it using double precision.
                BigDecimal decimal = new BigDecimal(literal, CONVERSION_CONTEXT);
                return Optional.of(decimal.stripTrailingZeros().toPlainString());
            }

            // Can it be represented as a float at all?
            if ((float) abs == 0f || !Float.isFinite((float) abs)) {
                return Optional.empty();
            }

            // Cannot avoid exponent. Format it so that Mindustry understands it.
            // Use float representation, as too high a precision might create too high exponents
            float valueFloat = (float) value;
            String literalFloat = Float.toString(valueFloat);
            int dot = literalFloat.indexOf('.');
            int exp = literalFloat.indexOf('E');

            if (dot >= 0 && exp >= 0) {
                if (dot >= exp) {
                    return Optional.empty(); // Not possible, but hey
                }

                int exponent = Integer.parseInt(literalFloat.substring(exp + 1)) ;
                if (Math.abs(exponent) > 38) {
                    // Even float precision would be compromised at this point
                    return Optional.empty();
                }

                String mantisa =  literalFloat.substring(0, dot) + literalFloat.substring(dot + 1, exp);
                exponent -= (exp - dot - 1);

                int lastValidDigit = mantisa.length() - 1;
                while (mantisa.charAt(lastValidDigit) == '0') {
                    if (--lastValidDigit < 0) {
                        return Optional.of("0");
                    }
                    exponent++;
                }

                String mlog = exponent == 0
                        ? mantisa.substring(0, lastValidDigit + 1)
                        : mantisa.substring(0, lastValidDigit + 1) + literalFloat.charAt(exp) + exponent;

                double refloat = Double.parseDouble(String.valueOf(valueFloat));
                double absDiff = Math.abs(refloat - value);
                double relDiff = absDiff / value;
                if (relDiff > 1e-9) {
                    messageConsumer.accept(CompilerMessage.warn("Loss of precision while creating mlog literals (original value "+ literal + ", encoded value " + mlog + ")"));
                }

                return Optional.of(mlog);
            } else {
                return Optional.of(literalFloat);
            }
        } else {
            return Optional.empty();
        }
    }
}
