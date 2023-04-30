package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.logic.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.teksol.util.CollectionUtils.findFirstIndex;

public class BaseInstructionProcessor implements InstructionProcessor {
    private final Consumer<CompilerMessage> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<LogicParameter, Set<String>> validArgumentValues;
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
    public LogicLabel nextLabel() {
        return LogicLabel.symbolic(getLabelPrefix() + labelIndex++);
    }

    @Override
    public LogicVariable nextTemp() {
        return LogicVariable.temporary(getTempPrefix() + tmpIndex++);
    }

    @Override
    public LogicVariable nextReturnValue() {
        return LogicVariable.retval( getRetValPrefix() + tmpIndex++);
    }

    @Override
    public String nextLocalPrefix() {
        return getLocalPrefix() + functionIndex++;
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
    public CallInstruction createCallStackless(LogicAddress address) {
        return (CallInstruction) createInstruction(Opcode.CALL, address);
    }

    @Override
    public CallRecInstruction createCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr) {
        return (CallRecInstruction) createInstruction(Opcode.CALLREC, stack, callAddr, retAddr);
    }

    @Override
    public EndInstruction createEnd() {
        return (EndInstruction) createInstruction(Opcode.END);
    }

    @Override
    public GotoInstruction createGoto(LogicVariable address) {
        return (GotoInstruction) createInstruction(Opcode.GOTO, address);
    }

    @Override
    public JumpInstruction createJump(LogicLabel label, Condition condition, LogicValue x, LogicValue y) {
        return (JumpInstruction) createInstruction(Opcode.JUMP, label, condition, x, y);
    }

    @Override
    public JumpInstruction createJumpUnconditional(LogicLabel label) {
        return (JumpInstruction) createInstruction(Opcode.JUMP, label, Condition.ALWAYS);
    }

    @Override
    public LabelInstruction createLabel(LogicLabel label) {
        return (LabelInstruction) createInstruction(Opcode.LABEL, label);
    }

    @Override
    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first) {
        return (OpInstruction) createInstruction(Opcode.OP, operation, target, first);
    }

    @Override
    public OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return (OpInstruction) createInstruction(Opcode.OP, operation, target, first, second);
    }

    @Override
    public PopInstruction createPop(LogicVariable memory, LogicVariable value) {
        return (PopInstruction) createInstruction(Opcode.POP, memory, value);
    }

    @Override
    public PrintInstruction createPrint(LogicValue what) {
        return (PrintInstruction) createInstruction(Opcode.PRINT, what);
    }

    @Override
    public PrintflushInstruction createPrintflush(LogicVariable messageBlock) {
        return (PrintflushInstruction) createInstruction(Opcode.PRINTFLUSH, messageBlock);
    }

    @Override
    public PushInstruction createPush(LogicVariable memory, LogicVariable value) {
        return (PushInstruction) createInstruction(Opcode.PUSH, memory, value);
    }

    @Override
    public ReadInstruction createRead(LogicVariable result, LogicVariable memory, LogicValue index) {
        return (ReadInstruction) createInstruction(Opcode.READ, result, memory, index);
    }

    @Override
    public ReturnInstruction createReturn(LogicVariable stack) {
        return (ReturnInstruction) createInstruction(Opcode.RETURN, stack);
    }

    @Override
    public SensorInstruction createSensor(LogicVariable result, LogicValue target, LogicValue property) {
        return (SensorInstruction) createInstruction(Opcode.SENSOR, result, target, property);
    }

    @Override
    public SetInstruction createSet(LogicVariable target, LogicValue value) {
        return (SetInstruction) createInstruction(Opcode.SET, target, value);
    }

    @Override
    public SetAddressInstruction createSetAddress(LogicVariable variable, LogicLabel address) {
        return (SetAddressInstruction) createInstruction(Opcode.SETADDR, variable, address);
    }

    @Override
    public StopInstruction createStop() {
        return (StopInstruction) createInstruction(Opcode.STOP);
    }

    @Override
    public WriteInstruction createWrite(LogicValue value, LogicVariable memory, LogicValue index) {
        return (WriteInstruction) createInstruction(Opcode.WRITE, value, memory, index);
    }

    @Override
    public WriteInstruction createWriteAddress(LogicAddress value, LogicVariable memory, LogicValue index) {
        return (WriteInstruction) createInstruction(Opcode.WRITE, value, memory, index);
    }


    @Override
    public LogicInstruction createInstruction(Opcode opcode, LogicArgument... arguments) {
        return createInstruction(opcode, List.of(arguments));
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments) {
        return validate(createInstructionUnchecked(opcode, arguments));
    }

    @Override
    public LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant) {
        return createInstruction(opcodeVariant.getOpcode(),
                opcodeVariant.getNamedParameters().stream()
                        .map(NamedParameter::name)
                        .map(BaseArgument::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LogicInstruction createInstructionUnchecked(Opcode opcode, List<LogicArgument> arguments) {
        List<LogicParameter> params = getParameters(opcode, arguments);

        return switch (opcode) {
            case CALL       -> new CallInstruction(arguments, params);
            case CALLREC    -> new CallRecInstruction(arguments, params);
            case END        -> new EndInstruction();
            case GOTO       -> new GotoInstruction(arguments, params);
            case JUMP       -> new JumpInstruction(arguments, params);
            case LABEL      -> new LabelInstruction(arguments, params);
            case OP         -> new OpInstruction(arguments, params);
            case PACKCOLOR  -> new PackColorInstruction(arguments, params);
            case POP        -> new PopInstruction(arguments, params);
            case PRINT      -> new PrintInstruction(arguments, params);
            case PRINTFLUSH -> new PrintflushInstruction(arguments, params);
            case PUSH       -> new PushInstruction(arguments, params);
            case READ       -> new ReadInstruction(arguments, params);
            case RETURN     -> new ReturnInstruction(arguments, params);
            case SENSOR     -> new SensorInstruction(arguments, params);
            case SET        -> new SetInstruction(arguments, params);
            case SETADDR    -> new SetAddressInstruction(arguments, params);
            case STOP       -> new StopInstruction();
            case WRITE      -> new WriteInstruction(arguments, params);
            default         -> new BaseInstruction(opcode, arguments, params);
        };
    }

    @Override
    public void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        switch (instruction) {
            case LabelInstruction ix -> {}       // Do nothing

            case PushInstruction ix -> {
                consumer.accept(createWrite(ix.getVariable(), ix.getMemory(), stackPointer()));
                consumer.accept(createOp(Operation.ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
            }

            case PopInstruction ix -> {
                consumer.accept(createOp(Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(ix.getVariable(), ix.getMemory(), stackPointer()));
            }

            case CallRecInstruction ix -> {
                consumer.accept(createInstruction(Opcode.WRITE, ix.getRetAddr(), ix.getStack(), stackPointer()));
                consumer.accept(createOp(Operation.ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createInstruction(Opcode.SET, LogicBuiltIn.COUNTER, ix.getCallAddr()));
            }

            case ReturnInstruction ix -> {
                LogicVariable retAddr = nextTemp();
                consumer.accept(createOp(Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(retAddr, ix.getStack(), stackPointer()));
                consumer.accept(createInstruction(Opcode.SET, LogicBuiltIn.COUNTER, retAddr));
            }

            case CallInstruction ix       -> consumer.accept(createJumpUnconditional(ix.getCallAddr()));
            case GotoInstruction ix       -> consumer.accept(createInstruction(Opcode.SET, LogicBuiltIn.COUNTER, ix.getIndirectAddress()));
            case SetAddressInstruction ix -> consumer.accept(createInstruction(Opcode.SET, ix.getTarget(), ix.getLabel()));

            default                       -> consumer.accept(instruction);
        }
    }

    public LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, LogicArgument arg) {
        if (instruction.getArg(argIndex).equals(arg)) {
            return instruction;
        }
        else {
            List<LogicArgument> newArgs = new ArrayList<>(instruction.getArgs());
            newArgs.set(argIndex, arg);
            return createInstruction(instruction.getOpcode(), newArgs);
        }
    }

    @Override
    public LogicInstruction replaceAllArgs(LogicInstruction instruction, LogicArgument oldArg, LogicArgument newArg) {
        List<LogicArgument> args = instruction.getArgs().stream()
                .map(arg -> arg.equals(oldArg) ? newArg : arg)
                .toList();
        return replaceArgs(instruction, args);
    }

    @Override
    public LogicInstruction replaceArgs(LogicInstruction instruction, List<LogicArgument> newArgs) {
        return instruction.getMarker() != null
                ? createInstruction(instruction.getOpcode(), newArgs).withMarker(instruction.getMarker())
                : createInstruction(instruction.getOpcode(), newArgs);
    }

    private List<LogicParameter> getArgumentTypes(LogicInstruction instruction) {
        return getParameters(instruction.getOpcode(), instruction.getArgs());
    }

    @Override
    public int getPrintArgumentCount(LogicInstruction instruction) {
        // Maximum over all existing opcode variants, plus additional opcode-specific unused arguments
        // TODO precompute and cache per opcode
        return variantsByOpcode.get(instruction.getOpcode()).stream()
                .mapToInt(v -> v.getNamedParameters().size()).max().orElse(0)
                + instruction.getOpcode().getAdditionalPrintArguments();
    }


    /**
     * Returns true if the given value is allowed to be used in place of the given argument.
     * For input and output arguments, anything is permissible at the moment (it could be a variable name, a literal,
     * or in some cases a @constant), but it might be possible to implement more specific checks in the future.
     * For other arguments, only concrete, version-specific values are permissible.
     *
     * @param type type of the argument
     * @param value value assigned to the argument
     * @return true if the value is valid for given argument type
     */
    private boolean isValid(LogicParameter type, LogicArgument value) {
        if (type.restrictValues()) {
            Set<String> values = validArgumentValues.get(type);
            return values.contains(value.toMlog());
        } else {
            return true;
        }
    }

    protected OpcodeVariant getOpcodeVariant(Opcode opcode, List<LogicArgument> arguments) {
        List<OpcodeVariant> variants = variantsByOpcode.get(opcode);
        if (variants == null) {
            return null;
        } else if (variants.size() == 1) {
            return variants.get(0);
        } else {
            // Selector keyword position in the list
            int position = opcodeKeywordPosition.get(opcode);

            // We know that variantsByKeyword contains opcode, as variantsByOpcode.get(opcode) isn't null
            return position >= arguments.size() ? null : variantsByKeyword.get(opcode).get(arguments.get(position).toMlog());
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
    protected List<LogicParameter> getParameters(Opcode opcode, List<LogicArgument> arguments) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, arguments);
        return opcodeVariant == null ? null : opcodeVariant.getParameterTypes();
    }

    protected LogicInstruction validate(LogicInstruction instruction) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(instruction.getOpcode(), instruction.getArgs());
        if (opcodeVariant == null) {
            throw new GenerationException("Invalid or version incompatible instruction " + instruction);
        }

        if (instruction.getArgs().size() != opcodeVariant.getNamedParameters().size()) {
            throw new GenerationException("Wrong number of arguments of instruction " + instruction.getOpcode()
                    + " (expected " + opcodeVariant.getNamedParameters().size() + "). " + instruction);
        }

        for (int i = 0; i < instruction.getArgs().size(); i++) {
            NamedParameter namedParameter = opcodeVariant.getNamedParameters().get(i);
            LogicArgument argument = instruction.getArgs().get(i);
            LogicParameter type = namedParameter.type();
            if (!isValid(type, argument)) {
                throw new GenerationException("Argument " + argument + " for parameter '" + namedParameter.name()
                        + "' not compatible with argument type " + type + ". " + instruction);
            }
        }

        if (instruction.getParams() == null) {
            throw new GenerationException("Instruction created without valid parameters: " + instruction);
        }

        return instruction;
    }

    private int getOpcodeVariantSelectorPosition(Opcode opcode, List<OpcodeVariant> opcodeVariants) {
        List<Integer> indexes = opcodeVariants.stream()
                .map(v -> findFirstIndex(v.getNamedParameters(), a -> a.type().isSelector()))
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
            return opcodeVariant.getNamedParameters().get(position).name();
        }
    }

    private Map<LogicParameter, Set<String>> createAllowedArgumentValuesMap() {
        Map<LogicParameter, Set<String>> map = new HashMap<>();
        for (LogicParameter type : LogicParameter.values()) {
            Set<String> allowedValues = createAllowedValues(type);
            if (!allowedValues.isEmpty()) {
                map.put(type, allowedValues);
            }
        }

        return Map.copyOf(map);
    }

    private Set<String> createAllowedValues(LogicParameter type) {
        if (type.isSelector()) {
            return opcodeVariants.stream()
                    .flatMap(v -> v.getNamedParameters().stream())
                    .filter(v -> v.type() == type)
                    .map(NamedParameter::name)
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

    private static final Set<String> BLOCK_NAMES = Set.of("arc", "bank", "battery", "cell", "center", "centrifuge",
            "compressor", "conduit", "container", "conveyor", "crucible", "cultivator", "cyclone", "diode",
            "disassembler", "display", "distributor", "dome", "door", "drill", "driver", "duo", "extractor", "factory",
            "foreshadow", "foundation", "fuse", "gate", "generator", "hail", "incinerator", "junction", "kiln", "lancer",
            "meltdown", "melter", "mender", "message", "mine", "mixer", "node", "nucleus", "panel", "parallax", "point",
            "press", "processor", "projector", "pulverizer", "reactor", "reconstructor", "ripple", "router", "salvo",
            "scatter", "scorch", "segment", "separator", "shard", "smelter", "sorter", "spectre", "swarmer", "switch",
            "tank", "tower", "tsunami", "unloader", "vault", "wall", "wave", "weaver");

    @Override
    public boolean isBlockName(String identifier) {
        Matcher matcher = BLOCK_NAME_PATTERN.matcher(identifier);
        return matcher.find() && BLOCK_NAMES.contains(matcher.group(1));
    }

    @Override
    public boolean isGlobalName(String identifier) {
        char ch = identifier.charAt(0);
        // If it doesn't start with one of those characters, it isn't an identifier
        return (ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                && identifier.equals(identifier.toUpperCase());
    }

    private LogicVariable stackPointer() {
        return LogicVariable.STACK_POINTER;
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
            // Use float representation, as too high a precision might get too high exponents
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