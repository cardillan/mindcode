package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.opcodes.*;
import info.teksol.mc.profile.CompilerProfile;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.Operation.ADD;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static info.teksol.mc.util.CollectionUtils.indexOf;

public abstract class BaseInstructionProcessor extends AbstractMessageEmitter implements InstructionProcessor {
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<InstructionParameterType, Collection<String>> validArgumentValues;
    private int tmpIndex = 0;
    private int labelIndex = 0;
    private int markerIndex = 0;
    private int functionIndex = 0;

    static class InstructionProcessorParameters {
        public final MessageConsumer messageConsumer;
        public final ProcessorVersion version;
        public final ProcessorEdition edition;
        public final List<OpcodeVariant> opcodeVariants;

        public InstructionProcessorParameters(MessageConsumer messageConsumer, ProcessorVersion version,
                ProcessorEdition edition, List<OpcodeVariant> opcodeVariants) {
            this.messageConsumer = messageConsumer;
            this.version = version;
            this.edition = edition;
            this.opcodeVariants = opcodeVariants;
        }

        public InstructionProcessorParameters(MessageConsumer messageConsumer, ProcessorVersion version,
                ProcessorEdition edition) {
            this(messageConsumer, version, edition, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, edition));
        }
    }

    BaseInstructionProcessor(InstructionProcessorParameters parameters) {
        super(parameters.messageConsumer);
        this.processorVersion = parameters.version;
        this.processorEdition = parameters.edition;
        this.opcodeVariants = parameters.opcodeVariants;
        variantsByOpcode = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::opcode));
        opcodeKeywordPosition = variantsByOpcode.keySet().stream()
                .collect(Collectors.toMap(k -> k, k -> getOpcodeVariantSelectorPosition(k, variantsByOpcode.get(k))));

        variantsByKeyword = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::opcode,
                Collectors.toMap(this::getOpcodeVariantKeyword, v -> v)));

        validArgumentValues = createAllowedArgumentValuesMap();
    }

    @Override
    public LogicLabel nextLabel() {
        return LogicLabel.symbolic(getLabelPrefix() + labelIndex++);
    }

    @Override
    public LogicLabel nextMarker() {
        return LogicLabel.symbolic("marker" + markerIndex++);
    }

    @Override
    public LogicVariable nextTemp() {
        return LogicVariable.temporary(getTempPrefix() + tmpIndex++);
    }

    @Override
    public String nextFunctionPrefix() {
        return getFunctionPrefix() + functionIndex++;
    }

    @Override
    public LogicVariable unusedVariable() {
        return LogicVariable.unusedVariable();
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

    private static final AstContext sampleContext = AstContext.createRootNode(
            CompilerProfile.standardOptimizations(false));

    @Override
    public LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant) {
        return createInstruction(sampleContext, opcodeVariant.opcode(),
                opcodeVariant.namedParameters().stream()
                        .map(NamedParameter::name)
                        .map(BaseArgument::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        return validate(createInstructionUnchecked(astContext, opcode, arguments));
    }

    @Override
    public LogicInstruction createInstructionUnchecked(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        List<InstructionParameterType> params = getParameters(opcode, arguments);

        return switch (opcode) {
            case CALL       -> new CallInstruction(astContext, arguments, params);
            case CALLREC    -> new CallRecInstruction(astContext, arguments, params);
            case CONTROL    -> new ControlInstruction(astContext, arguments, params);
            case DRAW       -> new DrawInstruction(astContext, arguments, params);
            case DRAWFLUSH  -> new DrawflushInstruction(astContext, arguments, params);
            case END        -> new EndInstruction(astContext);
            case FORMAT     -> new FormatInstruction(astContext, arguments, params);
            case GETLINK    -> new GetlinkInstruction(astContext, arguments, params);
            case GOTO       -> new GotoInstruction(astContext, arguments, params);
            case GOTOLABEL  -> new GotoLabelInstruction(astContext, arguments, params);
            case GOTOOFFSET -> new GotoOffsetInstruction(astContext, arguments, params);
            case JUMP       -> new JumpInstruction(astContext, arguments, params);
            case LABEL      -> new LabelInstruction(astContext, arguments, params);
            case LOOKUP     -> new LookupInstruction(astContext, arguments, params);
            case NOOP       -> new NoOpInstruction(astContext);
            case OP         -> new OpInstruction(astContext, arguments, params);
            case PACKCOLOR  -> new PackColorInstruction(astContext, arguments, params);
            case POP        -> new PopInstruction(astContext, arguments, params);
            case PRINT      -> new PrintInstruction(astContext, arguments, params);
            case PRINTFLUSH -> new PrintflushInstruction(astContext, arguments, params);
            case PUSH       -> new PushInstruction(astContext, arguments, params);
            case READ       -> new ReadInstruction(astContext, arguments, params);
            case REMARK     -> new RemarkInstruction(astContext, arguments, params);
            case RETURN     -> new ReturnInstruction(astContext, arguments, params);
            case SENSOR     -> new SensorInstruction(astContext, arguments, params);
            case SET        -> new SetInstruction(astContext, arguments, params);
            case SETADDR    -> new SetAddressInstruction(astContext, arguments, params);
            case STOP       -> new StopInstruction(astContext);
            case WRITE      -> new WriteInstruction(astContext, arguments, params);
            default         -> createGenericInstruction(astContext, opcode, arguments, params);
        };
    }

    @Override
    public LogicInstruction convertCustomInstruction(LogicInstruction instruction) {
        Opcode opcode;
        if (instruction instanceof CustomInstruction ix && (opcode = Opcode.fromOpcode(ix.getMlogOpcode())) != null) {
            Operation operation;
            if (opcode == OP && (operation = Operation.fromMlog(ix.getArg(0).toMlog())) != null) {
                List<LogicArgument> args = new ArrayList<>(ix.getArgs());
                args.set(0, operation);
                List<InstructionParameterType> params = getParameters(opcode, args);
                return new OpInstruction(ix.astContext, args, params);
            }
            return createInstructionUnchecked(instruction.getAstContext(), Opcode.fromOpcode(ix.getMlogOpcode()), instruction.getArgs());
        }
        return instruction;
    }

    private LogicInstruction createGenericInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments,
            List<InstructionParameterType> params) {
        List<InstructionParameterType> outputs = params.stream().filter(InstructionParameterType::isOutput).toList();
        if (outputs.size() == 1 && outputs.getFirst() == InstructionParameterType.RESULT) {
            return new BaseResultInstruction(astContext, opcode, arguments, params);
        } else {
            return new BaseInstruction(astContext, opcode, arguments, params);
        }
    }

    @Override
    public void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        AstContext astContext = instruction.getAstContext();

        switch (instruction) {
            case NoOpInstruction ix -> { }
            case GotoLabelInstruction ix -> { }
            case LabelInstruction ix -> { }
            case PushInstruction ix -> {
                consumer.accept(createWrite(astContext, ix.getVariable(), ix.getMemory(), stackPointer()));
                consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
            }
            case PopInstruction ix -> {
                consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(astContext, ix.getVariable(), ix.getMemory(), stackPointer()));
            }
            case CallRecInstruction ix -> {
                consumer.accept(createInstruction(astContext, WRITE, ix.getRetAddr(), ix.getStack(), stackPointer()));
                consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, ix.getCallAddr()));
            }
            case ReturnInstruction ix -> {
                LogicVariable retAddr = nextTemp();
                consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(astContext, retAddr, ix.getStack(), stackPointer()));
                consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, retAddr));
            }
            case CallInstruction ix -> {
                consumer.accept(createJumpUnconditional(astContext, ix.getCallAddr()));
            }
            case SetAddressInstruction ix -> {
                consumer.accept(createInstruction(astContext, SET, ix.getResult(), ix.getLabel()));
            }
            case GotoInstruction ix -> {
                consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, ix.getIndirectAddress()));
            }
            default -> {
                // GotoOffsetInstruction is handled by LabelResolver, as the actual label value needs to be known
                consumer.accept(instruction);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs) {
        if (instruction instanceof CustomInstruction ix) {
            return (T) new CustomInstruction(ix.getAstContext(), ix.isSafe(), ix.getMlogOpcode(), newArgs, ix.getArgumentTypes());
        } else {
            return (T) createInstruction(instruction.getAstContext(), instruction.getOpcode(), newArgs);
        }
    }

    @Override
    public <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg) {
        List<LogicArgument> args = instruction.getArgs().stream()
                .map(arg -> arg.equals(oldArg) ? newArg : arg)
                .toList();
        return replaceArgs(instruction, args);
    }

    @Override
    public <T extends LogicInstruction> T replaceAllArgs(T instruction, Map<LogicArgument, LogicArgument> argumentMap) {
        Function<LogicArgument, LogicArgument> mapper = arg -> argumentMap.getOrDefault(arg, arg);
        return replaceArgs(instruction, instruction.getArgs().stream().map(mapper).toList());

    }

    @Override
    public <T extends LogicInstruction> T replaceLabels(T instruction, Map<LogicLabel, LogicLabel> labelMap) {
        Function<LogicArgument, LogicArgument> mapper =
                arg -> arg instanceof LogicLabel label ? labelMap.getOrDefault(label, label) : arg;
        return replaceArgs(instruction, instruction.getArgs().stream().map(mapper).toList());
    }

    @Override
    public int getPrintArgumentCount(LogicInstruction instruction) {
        if (instruction instanceof CustomInstruction ix) {
            return ix.getArgs().size();
        } else {
            // Maximum over all existing opcode variants, plus additional opcode-specific unused arguments
            // TODO precompute and cache per opcode
            return variantsByOpcode.get(instruction.getOpcode()).stream()
                    .mapToInt(v -> v.namedParameters().size()).max().orElse(0)
                    + instruction.getOpcode().getAdditionalPrintArguments();
        }
    }

    @Override
    public boolean isDeterministic(LogicInstruction instruction) {
        return switch (instruction.getOpcode()) {
            case OP -> {
                OpInstruction ix = (OpInstruction) instruction;
                yield ix.getOperation().isDeterministic() && nonvolatileArguments(ix);
            }
            case SENSOR -> {
                // TODO Activate after support for volatile variable declaration
                yield false;
//                SensorInstruction ix = (SensorInstruction) instruction;
//                yield nonvolatileArguments(instruction)
//                        && ix.getProperty() instanceof LogicBuiltIn lbi
//                        && CONSTANT_PROPERTIES.contains(lbi.getName());
            }
            case SET, PACKCOLOR, LOOKUP -> nonvolatileArguments(instruction);
            case NOOP -> true;
            default -> false;
        };
    }

    private static final Set<String> CONSTANT_PROPERTIES = Set.of("size", "speed", "type", "id");

    private boolean nonvolatileArguments(LogicInstruction instruction) {
        // TODO Remove the exception for BLOCK after support for volatile variable declaration
        return instruction.inputArgumentsStream().noneMatch(v -> v.getType() == ArgumentType.BLOCK || v.isVolatile());
    }

    @Override
    public boolean isSafe(LogicInstruction instruction) {
        // TODO More instructions might be safe
        return switch (instruction.getOpcode()) {
            case READ, GETLINK, RADAR, SENSOR, SET, OP, LOOKUP, PACKCOLOR, NOOP -> true;
            default -> false;
        };
    }

    public boolean isSupported(Opcode opcode, List<LogicArgument> arguments) {
        return getOpcodeVariant(opcode, arguments) != null;
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
    private boolean isValid(InstructionParameterType type, LogicArgument value) {
        if (type.restrictValues()) {
            Collection<String> values = validArgumentValues.get(type);
            return values.contains(value.toMlog());
        } else {
            return true;
        }
    }

    protected OpcodeVariant getOpcodeVariant(Opcode opcode, List<? extends LogicArgument> arguments) {
        List<OpcodeVariant> variants = variantsByOpcode.get(opcode);
        if (variants == null) {
            return null;
        } else if (variants.size() == 1) {
            return variants.getFirst();
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
    public List<InstructionParameterType> getParameters(Opcode opcode, List<? extends LogicArgument> arguments) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, arguments);
        return opcodeVariant == null ? null : opcodeVariant.parameterTypes();
    }

    public Collection<String> getParameterValues(InstructionParameterType type) {
        return validArgumentValues.get(type);
    }

    protected <T extends LogicInstruction> T validate(T instruction) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(instruction.getOpcode(), instruction.getArgs());
        if (opcodeVariant == null) {
            throw new MindcodeInternalError("Invalid or version incompatible instruction " + instruction);
        }

        if (instruction.getArgs().size() != opcodeVariant.namedParameters().size()) {
            throw new MindcodeInternalError("Wrong number of arguments of instruction " + instruction.getMlogOpcode()
                    + " (expected " + opcodeVariant.namedParameters().size() + "). " + instruction);
        }

        for (int i = 0; i < instruction.getArgs().size(); i++) {
            NamedParameter namedParameter = opcodeVariant.namedParameters().get(i);
            LogicArgument argument = instruction.getArgs().get(i);
            InstructionParameterType type = namedParameter.type();
            if (!isValid(type, argument)) {
                throw new MindcodeInternalError("Invalid value '%s' for parameter '%s': allowed values are '%s'.",
                        argument.toMlog(), namedParameter.name(),
                        String.join("', '", validArgumentValues.get(type)));
            }
        }

        if (instruction.getArgumentTypes() == null) {
            throw new MindcodeInternalError("Instruction created without valid parameters: " + instruction);
        }

        if (instruction instanceof JumpInstruction && instruction.getAstContext().subcontextType() == AstSubcontextType.BODY) {
            throw new MindcodeInternalError("Jump instruction not allowed in BODY subcontext." + instruction);
        }

        return instruction;
    }

    private int getOpcodeVariantSelectorPosition(Opcode opcode, List<OpcodeVariant> opcodeVariants) {
        List<Integer> indexes = opcodeVariants.stream()
                .map(v -> indexOf(v.namedParameters(), a -> a.type().isSelector()))
                .distinct()
                .toList();

        if (indexes.size() != 1)  {
            throw new MindcodeInternalError("Cannot determine variant selector position for opcode " + opcode);
        }

        return indexes.getFirst();
    }

    private String getOpcodeVariantKeyword(OpcodeVariant opcodeVariant) {
        int position = opcodeKeywordPosition.get(opcodeVariant.opcode());
        if (position < 0) {
            return "";      // Single-variant opcode; no keyword
        } else {
            return opcodeVariant.namedParameters().get(position).name();
        }
    }

    private Map<InstructionParameterType, Collection<String>> createAllowedArgumentValuesMap() {
        Map<InstructionParameterType, Collection<String>> map = new HashMap<>();
        for (InstructionParameterType type : InstructionParameterType.values()) {
            Collection<String> allowedValues = createAllowedValues(type);
            if (!allowedValues.isEmpty()) {
                map.put(type, allowedValues);
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private Collection<String> createAllowedValues(InstructionParameterType type) {
        if (type.isSelector()) {
            return opcodeVariants.stream()
                    .flatMap(v -> v.namedParameters().stream())
                    .filter(v -> v.type() == type)
                    .map(NamedParameter::name)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            // Select only compatible keywords and put them into a set
            return type.getAllowedValues().stream()
                    .filter(v -> v.versions.contains(processorVersion))
                    .flatMap(v -> v.values.stream())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

    private static final Pattern BLOCK_NAME_PATTERN = Pattern.compile("^([a-zA-Z][a-zA-Z_]*)[1-9]\\d*$");

    private static final Set<String> BLOCK_NAMES = BlockType.getBaseLinkNames();

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

    protected abstract Optional<String> mlogFormat(double value, String literal);

    // 17 Digit precision - enough for 2^54. Larger numbers will lose precision anyway
    protected static final MathContext CONVERSION_CONTEXT = new MathContext(17, RoundingMode.HALF_UP);

    protected Optional<String> mlogFormatWithoutExponent(double absoluteValue, String literal) {
        // Is it zero?
        if (absoluteValue <= Double.MIN_NORMAL) {
            return Optional.of("0");
        } else if (1e-20 <= absoluteValue && absoluteValue < Long.MAX_VALUE) {
            // Fits into a long, Mindustry can convert it using double precision.
            // NOTE: Some precision is lost for numbers above 2^53, since double can only store 53 digits
            //       of the mantissa.
            BigDecimal decimal = new BigDecimal(literal, CONVERSION_CONTEXT);
            return Optional.of(decimal.stripTrailingZeros().toPlainString());
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> mlogFormatWithExponent(double value, String literal, boolean floatPrecision) {
        int dot = literal.indexOf('.');
        int exp = literal.indexOf('E');

        if (dot >= exp) {
            // Three possible cases:
            // 1. Neither a dot, nor an exp: this should have been handled by mlogFormatWithoutExponent.
            // 2. We have a dot, but not an exp: this should have been handled by mlogFormatWithoutExponent.
            // 3. The dot comes after the exp: a malformed literal (can't happen).
            // We should never end up here.
            throw new MindcodeInternalError("Error formatting numerical constant for mlog: " + literal);
        }

        if (dot < 0) {
            // We have just the exponent: the literal as is will do.
            // For floats, the check that the value will fit into a float has been made by the caller.
            return Optional.of(literal);
        }

        int exponent = Integer.parseInt(literal.substring(exp + 1)) ;
        if (floatPrecision && Math.abs(exponent) > 38) {
            // Even float precision would be compromised at this point
            return Optional.empty();
        }

        String mantissa = literal.substring(0, dot) + literal.substring(dot + 1, exp);
        exponent -= (exp - dot - 1);

        int lastValidDigit = mantissa.length() - 1;
        while (mantissa.charAt(lastValidDigit) == '0') {
            if (--lastValidDigit < 0) {
                return Optional.of("0");
            }
            exponent++;
        }

        String mlog = exponent == 0
                ? mantissa.substring(0, lastValidDigit + 1)
                : mantissa.substring(0, lastValidDigit + 1) + literal.charAt(exp) + exponent;

        if (floatPrecision) {
            double rewritten = Double.parseDouble(literal);
            double absDiff = Math.abs(rewritten - value);
            double relDiff = Math.abs(absDiff / value);
            if (relDiff > 1e-9) {
                // This warning doesn't come with an input position
                // This will no longer happen in ML8 - won't fix.
                messageConsumer.accept(CompilerMessage.warn(InputPosition.EMPTY,
                        "Loss of precision while creating mlog literals (original value %s, encoded value %s)",
                        literal, mlog));
            }
        }

        return Optional.of(mlog);
    }
}