package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.*;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.util.Utf8Utils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
import static info.teksol.mc.mindcode.logic.arguments.Operation.STRICT_NOT_EQUAL;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static info.teksol.mc.util.CollectionUtils.indexOf;

@NullMarked
public abstract class BaseInstructionProcessor extends AbstractMessageEmitter implements InstructionProcessor {
    private final ProcessorVersion processorVersion;
    private final ProcessorType processorType;
    private final NameCreator nameCreator;
    private final LStrings strings;
    private @Nullable MindustryMetadata metadata;
    private final boolean instructionValidation;
    private final boolean noArgumentPadding;
    private final boolean encodeZeroCharacters;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<InstructionParameterType, Collection<String>> validArgumentValues;
    private final Set<String> additionalBuiltins = new HashSet<>();
    private int tmpIndex = 0;
    private int labelIndex = 0;
    private int markerIndex = 0;

    private @Nullable SideEffects sideEffects;

    record InstructionProcessorParameters(
            MessageConsumer messageConsumer,
            ProcessorVersion version,
            ProcessorType type,
            NameCreator nameCreator,
            boolean instructionValidation,
            boolean noArgumentPadding,
            boolean encodeZeroCharacters,
            List<OpcodeVariant> opcodeVariants) {

        public InstructionProcessorParameters(MessageConsumer messageConsumer, ProcessorVersion version, ProcessorType type,
                NameCreator nameCreator, boolean instructionValidation, boolean noArgumentPadding, boolean encodeZeroCharacters) {
            this(messageConsumer, version, type, nameCreator, instructionValidation, noArgumentPadding,
                    encodeZeroCharacters, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, type));
        }
    }

    BaseInstructionProcessor(InstructionProcessorParameters parameters) {
        super(parameters.messageConsumer);
        this.processorVersion = parameters.version;
        this.processorType = parameters.type;
        this.strings = LStrings.create(processorVersion);
        this.nameCreator = parameters.nameCreator;
        this.instructionValidation = parameters.instructionValidation;
        this.noArgumentPadding = parameters.noArgumentPadding;
        this.encodeZeroCharacters = parameters.encodeZeroCharacters;
        this.opcodeVariants = parameters.opcodeVariants;
        variantsByOpcode = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::opcode));
        opcodeKeywordPosition = variantsByOpcode.keySet().stream().collect(Collectors.toMap(k -> k,
                k -> getOpcodeVariantSelectorPosition(k, Objects.requireNonNull(variantsByOpcode.get(k)))));

        variantsByKeyword = opcodeVariants.stream().collect(Collectors.groupingBy(OpcodeVariant::opcode,
                Collectors.toMap(this::getOpcodeVariantKeyword, v -> v)));

        validArgumentValues = createAllowedArgumentValuesMap();
    }

    @Override
    public MindustryMetadata getMetadata() {
        if (metadata == null) {
            metadata = MindustryMetadata.forVersion(processorVersion);
        }
        return metadata;
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
        return LogicVariable.temporary(nameCreator.temp(tmpIndex++));
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
    public ProcessorType getProcessorType() {
        return processorType;
    }

    @Override
    public List<OpcodeVariant> getOpcodeVariants() {
        return opcodeVariants;
    }

    private static final AstContext sampleContext = AstContext.createRootNode(
            CompilerProfile.fullOptimizations(false));

    @Override
    public BaseInstructionProcessor withSideEffects(SideEffects sideEffects) {
        this.sideEffects = sideEffects;
        return this;
    }

    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        return validate(createInstructionUnchecked(astContext, opcode, arguments));
    }

    @Override
    public LogicInstruction createInstructionUnchecked(AstContext astContext, Opcode opcode, List<LogicArgument> args) {
        List<InstructionParameterType> params = getParameters(opcode, args);

        LogicInstruction instruction = switch (opcode) {
            case CALL        -> new CallInstruction(astContext, args, params);
            case CALLREC     -> new CallRecInstruction(astContext, args, params);
            case COMMENT     -> new CommentInstruction(astContext, args, params);
            case CONTROL     -> new ControlInstruction(astContext, args, params);
            case DRAW        -> new DrawInstruction(astContext, args, params);
            case DRAWFLUSH   -> new DrawflushInstruction(astContext, args, params);
            case END         -> new EndInstruction(astContext);
            case FORMAT      -> new FormatInstruction(astContext, args, params);
            case GETLINK     -> new GetlinkInstruction(astContext, args, params);
            case JUMP        -> new JumpInstruction(astContext, args, params);
            case LABEL       -> new LabelInstruction(astContext, args, params);
            case LOOKUP      -> new LookupInstruction(astContext, args, params);
            case MULTICALL   -> new MultiCallInstruction(astContext, args, params);
            case MULTIJUMP   -> new MultiJumpInstruction(astContext, args, params);
            case MULTILABEL  -> new MultiLabelInstruction(astContext, args, params);
            case EMPTY       -> new EmptyInstruction(astContext);
            case OP          -> new OpInstruction(astContext, args, params);
            case PACKCOLOR   -> new PackColorInstruction(astContext, args, params);
            case POP         -> new PopInstruction(astContext, args, params);
            case PRINT       -> new PrintInstruction(astContext, args, params);
            case PRINTCHAR   -> new PrintCharInstruction(astContext, args, params);
            case PRINTFLUSH  -> new PrintflushInstruction(astContext, args, params);
            case PUSH        -> new PushInstruction(astContext, args, params);
            case READ        -> new ReadInstruction(astContext, args, params);
            case READARR     -> new ReadArrInstruction(astContext, args, params);
            case REMARK      -> new RemarkInstruction(astContext, args, params);
            case RETURN      -> new ReturnInstruction(astContext, args, params);
            case RETURNREC   -> new ReturnRecInstruction(astContext, args, params);
            case SELECT      -> new SelectInstruction(astContext, args, params);
            case SENSOR      -> new SensorInstruction(astContext, args, params);
            case SET         -> new SetInstruction(astContext, args, params);
            case SETADDR     -> new SetAddressInstruction(astContext, args, params);
            case STOP        -> new StopInstruction(astContext);
            case UNPACKCOLOR -> new UnpackColorInstruction(astContext, args, params);
            case WAIT        -> new WaitInstruction(astContext, args, params);
            case WRITE       -> new WriteInstruction(astContext, args, params);
            case WRITEARR    -> new WriteArrInstruction(astContext, args, params);
            default          ->  createGenericInstruction(astContext, opcode, args, params);
        };

        if (sideEffects != null) {
            instruction.setSideEffects(sideEffects);
            sideEffects = null;
        }

        return instruction;
    }

    @Override
    public void setupArrayAccessInstruction(ArrayAccessInstruction instruction) {
        switch (instruction.getArray().getArrayStore().getArrayType()) {
            case EXTERNAL -> instruction.setArrayOrganization(ArrayOrganization.EXTERNAL, ArrayConstruction.REGULAR);
            case CONSTANT -> instruction.setArrayOrganization(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR)
                    .setArrayFolded(isSupported(SELECT));

            default -> {
                if (instruction.getArray().getArrayStore() instanceof InternalArray array && array.getLookupType() != LogicKeyword.INVALID) {
                    instruction.setArrayOrganization(ArrayOrganization.LOOKUP, ArrayConstruction.COMPACT)
                            .setArrayLookupType(array.getLookupType());
                } else if (getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
                    instruction.setArrayOrganization(ArrayOrganization.INTERNAL, ArrayConstruction.COMPACT)
                            .setArrayFolded(isSupported(SELECT));
                } else {
                    instruction.setArrayOrganization(ArrayOrganization.INTERNAL, ArrayConstruction.REGULAR);
                }
            }
        }
    }

    private LogicInstruction createGenericInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments,
            @Nullable List<InstructionParameterType> params) {
        List<InstructionParameterType> outputs = params == null ? List.of()
                : params.stream().filter(InstructionParameterType::isOutput).toList();

        if (outputs.size() == 1 && outputs.getFirst() == InstructionParameterType.RESULT) {
            return new BaseResultInstruction(astContext, opcode, arguments, params);
        } else {
            return new BaseInstruction(astContext, opcode, arguments, params);
        }
    }

    private @Nullable LogicVariable stackPointer;
    private LogicVariable stackPointer() {
        if (stackPointer == null) {
            stackPointer = LogicVariable.preserved(nameCreator.stackPointer());
        }
        return stackPointer;
    }

    @Override
    public void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        AstContext astContext = instruction.getAstContext();
        GlobalCompilerProfile profile = astContext.getGlobalProfile();

        switch (instruction) {
            case EmptyInstruction ix -> { }
            case JumpInstruction ix -> {
                consumer.accept(ix.getCondition() == Condition.STRICT_NOT_EQUAL
                        ? createSelect(astContext, LogicBuiltIn.COUNTER, Condition.STRICT_EQUAL,
                        ix.getX(), ix.getY(), LogicBuiltIn.COUNTER, ix.getTarget()).copyComment(ix)
                        : ix);
            }
            case OpInstruction ix -> {
                consumer.accept(ix.getOperation() == STRICT_NOT_EQUAL
                        ? createSelect(astContext, ix.getResult(), Condition.STRICT_EQUAL,
                        ix.getX(), ix.getY(), LogicBoolean.FALSE, LogicBoolean.TRUE).copyComment(ix)
                        : ix);
            }
            case SelectInstruction ix -> {
                consumer.accept(ix.getCondition() == Condition.STRICT_NOT_EQUAL
                        ? createSelect(astContext, ix.getResult(), Condition.STRICT_EQUAL,
                        ix.getX(), ix.getY(), ix.getFalseValue(), ix.getTrueValue()).copyComment(ix)
                        : ix);
            }
            case MultiLabelInstruction ix -> { }
            case LabelInstruction ix -> { }
            case PushInstruction ix -> {
                consumer.accept(createWrite(astContext, ix.getVariable(), ix.getMemory(), stackPointer()));
                consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE).copyComment(ix));
            }
            case PopInstruction ix -> {
                consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(astContext, ix.getVariable(), ix.getMemory(), stackPointer()).copyComment(ix));
            }
            case CallRecInstruction ix -> {
                if (profile.isSymbolicLabels()) {
                    LogicVariable returnAddress = nextTemp();
                    consumer.accept(createOp(astContext, ADD, returnAddress, LogicBuiltIn.COUNTER, LogicNumber.THREE));
                    consumer.accept(createInstruction(astContext, WRITE, returnAddress, ix.getStack(), stackPointer()));
                } else {
                    consumer.accept(createInstruction(astContext, WRITE, ix.getRetAddr(), ix.getStack(), stackPointer()));
                }
                consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createJumpUnconditional(astContext, ix.getCallAddr()).copyComment(ix));
            }
            case ReturnRecInstruction ix -> {
                LogicVariable retAddr = nextTemp();
                consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
                consumer.accept(createRead(astContext, retAddr, ix.getStack(), stackPointer()));
                consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, retAddr).copyComment(ix));
            }
            case CallInstruction ix -> {
                if (profile.isSymbolicLabels()) {
                    consumer.accept(createOp(astContext, ADD, ix.getReturnAddr(), LogicBuiltIn.COUNTER, LogicNumber.ONE));
                }
                consumer.accept(createJumpUnconditional(astContext, ix.getCallAddr()).copyComment(ix));
            }
            case SetAddressInstruction ix -> {
                consumer.accept(createInstruction(astContext, SET, ix.getResult(), ix.getLabel()).copyComment(ix));
            }
            case ReturnInstruction ix -> {
                consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, ix.getIndirectAddress()).copyComment(ix));
            }

            // Note: MULTIJUMP / MULTICALL Instructions are handled by LabelResolver, as the actual label value needs to be known
            default -> {
                consumer.accept(instruction);
            }
        }
    }

    @Override
    public <T extends LogicInstruction> T copy(T instruction) {
        return replaceArgs(instruction, instruction.getArgs());
    }

    @Override
    public <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs) {
        LogicInstruction result;
        if (instruction instanceof CustomInstruction ix) {
            result = new CustomInstruction(ix.getAstContext(), ix.isSafe(), ix.isText(), ix.isLabel(), ix.getMlogOpcode(), newArgs, ix.getArgumentTypes());
        } else {
            result = createInstruction(instruction.getAstContext(), instruction.getOpcode(), newArgs);
        }
        return result.copyInfo(instruction);
    }

    @Override
    public <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg) {
        List<LogicArgument> args = instruction.getArgs().stream()
                .map(arg -> arg.equals(oldArg) ? newArg : arg)
                .toList();
        return replaceArgs(instruction, args);
    }

    @Override
    public <T extends LogicInstruction> T replaceLabels(T instruction, Map<LogicLabel, LogicLabel> labelMap) {
        Function<LogicArgument, LogicArgument> mapper =
                arg -> arg instanceof LogicLabel label ? labelMap.getOrDefault(label, label) : arg;
        T ix = replaceArgs(instruction, instruction.getArgs().stream().map(mapper).toList());
        ix.remapInfoLabels(labelMap);
        return ix;
    }

    private final EnumMap<Opcode, Integer> instructionSizes = new EnumMap<>(Opcode.class);

    @Override
    public int getPrintArgumentCount(LogicInstruction instruction) {
        if (instruction instanceof CustomInstruction || noArgumentPadding) {
            return instruction.getArgs().size();
        } else {
            return instructionSizes.computeIfAbsent(instruction.getOpcode(), this::computePrintArgumentCount);
        }
    }

    private int computePrintArgumentCount(Opcode opcode) {
        // Maximum over all existing opcode variants, plus additional opcode-specific unused arguments
        return Objects.requireNonNull(variantsByOpcode.get(opcode)).stream()
                .mapToInt(v -> v.namedParameters().size()).max().orElseThrow()
                + opcode.getAdditionalPrintArguments();
    }

    private static final EnumSet<Opcode> DETERMINISTIC_OPCODES = EnumSet.of(OP, SELECT, SENSOR, SET, PACKCOLOR,
            UNPACKCOLOR, LOOKUP, EMPTY, SETADDR);
    private static final Set<String> CONSTANT_PROPERTIES = Set.of("@size", "@speed", "@type", "@id");

    @Override
    public boolean isDeterministic(LogicInstruction instruction) {
        return DETERMINISTIC_OPCODES.contains(instruction.getOpcode())
               && hasDeterministicArguments(instruction)
               && hasNonvolatileArguments(instruction);
    }

    private boolean hasDeterministicArguments(LogicInstruction instruction) {
        return switch (instruction) {
            case OpInstruction ix -> ix.getOperation().isDeterministic();
            case SensorInstruction ix -> CONSTANT_PROPERTIES.contains(ix.getProperty().toMlog());
            default -> true;
        };
    }

    private boolean hasNonvolatileArguments(LogicInstruction instruction) {
        return instruction.inputArgumentsStream().noneMatch(v -> v.getType() == ArgumentType.BLOCK || v.isVolatile());
    }

    @Override
    public boolean isSupported(Opcode opcode) {
        return variantsByOpcode.containsKey(opcode);
    }

    @Override
    public boolean isSupported(Opcode opcode, List<LogicArgument> arguments) {
        return getOpcodeVariant(opcode, arguments) != null;
    }

    /// Returns true if the given value is allowed to be used in place of the given argument.
    /// For input and output arguments, anything is permissible at the moment (it could be a variable name, a literal,
    /// or in some cases a @constant), but it might be possible to implement more specific checks in the future.
    /// For other arguments, only concrete, version-specific values are permissible.
    ///
    /// @param type type of the argument
    /// @param value value assigned to the argument
    /// @return true if the value is valid for given argument type
    private boolean isValid(InstructionParameterType type, LogicArgument value) {
        if (type.restrictValues()) {
            Collection<String> values = getParameterValues(type);
            return values.contains(value.toMlog());
        } else {
            return true;
        }
    }

    protected @Nullable OpcodeVariant getOpcodeVariant(Opcode opcode, List<? extends LogicArgument> arguments) {
        List<OpcodeVariant> variants = variantsByOpcode.get(opcode);
        if (variants == null) {
            return null;
        } else if (variants.size() == 1) {
            return variants.getFirst();
        } else {
            // Selector keyword position in the list
            int position = Objects.requireNonNull(opcodeKeywordPosition.get(opcode));

            // We know that variantsByKeyword contains opcode, as variantsByOpcode.get(opcode) isn't null
            return position >= arguments.size() ? null
                    : Objects.requireNonNull(variantsByKeyword.get(opcode)).get(arguments.get(position).toMlog());
        }
    }

    /// Returns list of argument types based on instruction opcode and instruction variant. The variant of the
    /// instruction is determined by inspecting its arguments.
    ///
    /// @param opcode opcode of the instruction
    /// @param args arguments to the instruction
    /// @return list of types of given arguments
    public @Nullable List<InstructionParameterType> getParameters(Opcode opcode, List<? extends LogicArgument> args) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, args);
        return opcodeVariant == null ? null : opcodeVariant.parameterTypes();
    }

    public Collection<String> getParameterValues(InstructionParameterType type) {
        return Objects.requireNonNull(validArgumentValues.get(type));
    }

    @Override
    public void addBuiltin(String name) {
        if (!name.startsWith("@")) {
            throw new IllegalArgumentException("Built-in name must start with '@'");
        }
        additionalBuiltins.add(name);
    }

    @Override
    public boolean addKeyword(KeywordCategory keywordCategory, String keyword) {
        if (keyword.startsWith(":")) {
            throw new IllegalArgumentException("Keyword must not start with ':'");
        }
        Collection<String> values = validArgumentValues.get(InstructionParameterType.forCategory(keywordCategory));
        if (values != null) values.add(keyword);
        return values != null;
    }

    protected <T extends LogicInstruction> T validate(T instruction) {
        if (!instructionValidation) return instruction;

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
                        String.join("', '", getParameterValues(type)));
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
        int position = Objects.requireNonNull(opcodeKeywordPosition.get(opcodeVariant.opcode()));
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
            return new LinkedHashSet<>(type.getVersionKeywords(processorVersion));
        }
    }

    private static final Pattern BLOCK_NAME_PATTERN = Pattern.compile("^([a-zA-Z][a-zA-Z_]*)[1-9]\\d*$");

    private @Nullable Set<String> blockNames;

    @Override
    public boolean isBlockName(String identifier) {
        if (blockNames == null) {
            blockNames = new HashSet<>(BlockType.getBaseLinkNames(getMetadata()));
        }
        Matcher matcher = BLOCK_NAME_PATTERN.matcher(identifier);
        return matcher.find() && blockNames.contains(matcher.group(1));
    }

    public void addBlockName(String identifier) {
        if (blockNames == null) {
            blockNames = new HashSet<>(BlockType.getBaseLinkNames(getMetadata()));
        }
        blockNames.add(identifier);
    }

    @Override
    public boolean isGlobalName(String identifier) {
        char ch = identifier.charAt(0);
        // If it doesn't start with one of those characters, it isn't an identifier
        return (ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                && identifier.equals(identifier.toUpperCase());
    }

    @Override
    public boolean isValidBuiltIn(String builtin) {
        return getMetadata().isValidBuiltIn(builtin) || additionalBuiltins.contains(builtin);
    }

    private static final Set<String> VOLATILE_NAMES = Set.of("@counter", "@time", "@tick", "@second",
            "@minute", "@waveNumber", "@waveTime", "@unit", "@links");

    @Override
    public boolean isVolatileBuiltIn(String builtin) {
        return VOLATILE_NAMES.contains(builtin);
    }

    @Override
    public Optional<String> mlogRewrite(SourcePosition sourcePosition, String literal, boolean allowPrecisionLoss) {
        try {
            return mlogFormat(sourcePosition, Double.parseDouble(literal), literal, allowPrecisionLoss);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private static final double COLOR_LITERAL_MAX_VALUE = Color.parseColor("%FFFFFFFF");

    @Override
    public Optional<LogicLiteral> createLiteral(SourcePosition sourcePosition, double value, boolean allowPrecisionLoss) {
        if (isSupported(PACKCOLOR) && value > 0 && value <= COLOR_LITERAL_MAX_VALUE) {
            return Optional.of(LogicColor.create(sourcePosition, Color.unpack(value)));
        }
        return mlogFormat(sourcePosition, value, String.valueOf(value), allowPrecisionLoss)
                .map(literal -> LogicNumber.create(this, sourcePosition, literal));
    }

    protected abstract Optional<String> mlogFormat(SourcePosition sourcePosition, double value, String literal, boolean allowPrecisionLoss);

    // 17 Digit precision - enough for 2^54. Larger numbers will lose precision anyway
    protected static final MathContext CONVERSION_CONTEXT = new MathContext(17, RoundingMode.HALF_UP);

    protected Optional<String> mlogFormatWithoutExponent(double value, String literal) {
        double absoluteValue = Math.abs(value);
        if (absoluteValue == 0.0) {
            return Optional.of("0");
        } else if (1e-20 <= absoluteValue && absoluteValue <= Long.MAX_VALUE) {
            long longValue = (long) value;
            if (longValue == value && !isValidIntegerLiteral(longValue)) {
                return Optional.of("");
            }

            // Fits into a long, Mindustry can convert it using double precision.
            // NOTE: Some precision is lost for numbers above 2^53, since double can only store 53 digits
            //       of the mantissa.
            BigDecimal decimal = new BigDecimal(literal, CONVERSION_CONTEXT);
            return Optional.of(decimal.stripTrailingZeros().toPlainString());
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> mlogFormatWithExponent(SourcePosition sourcePosition, double value, String literal,
            String originalLiteral, boolean floatPrecision, boolean allowPrecisionLoss) {
        int dot = literal.indexOf('.');
        int exp = literal.indexOf('E');

        if (dot >= exp) {
            // Three possible cases:
            // 1. Neither a dot, nor an exp: this should have been handled by mlogFormatWithoutExponent.
            // 2. We have a dot, but not an exp: this should have been handled by mlogFormatWithoutExponent.
            // 3. The dot comes after the exp: a malformed literal (can't happen).
            // We should never end up here.
            throw new MindcodeInternalError("Error formatting numeric constant for mlog: " + literal);
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

        double rewritten = parseNumber(mlog);
        double absDiff = Math.abs(rewritten - value);
        double relDiff = Math.abs(absDiff / value);

        if (floatPrecision) {
            if (relDiff > 1e-9) {
                if (!allowPrecisionLoss) {
                    return Optional.empty();
                }

                messageConsumer.accept(CompilerMessage.warn(sourcePosition, WARN.LITERAL_LOSS_OF_PRECISION,
                        originalLiteral, rewritten));
            }
        } else if (relDiff > 1e-15 && Math.abs(value) >= Double.MIN_NORMAL) {
            // We don't care about precision loss of subnormal numbers.
            // Note that packed colors are subnormal, but these should be rewritten as color literals.
            throw new MindcodeInternalError("Unexpected precision loss formatting literal: " + literal);
        }

        return Optional.of(mlog);
    }


    @Override
    public double parseNumber(String literal) {
        double value = strings.parseDouble(literal, Double.NaN);
        if (Double.isNaN(value)) {
            throw new MindcodeInternalError("Cannot parse numeric literal: " + literal);
        }
        return value;
    }

    @Override
    public boolean canEncode(int character) {
        return character == 0 ? encodeZeroCharacters : Utf8Utils.canEncode(character);
    }
}
