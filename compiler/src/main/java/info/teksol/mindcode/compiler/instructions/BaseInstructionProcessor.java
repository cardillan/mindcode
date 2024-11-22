package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.mimex.BlockType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.Opcode.*;
import static info.teksol.mindcode.logic.Operation.ADD;
import static info.teksol.util.CollectionUtils.findFirstIndex;

public class BaseInstructionProcessor extends AbstractMessageEmitter implements InstructionProcessor {
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final List<OpcodeVariant> opcodeVariants;
    private final Map<Opcode, List<OpcodeVariant>> variantsByOpcode;
    private final Map<Opcode, Map<String, OpcodeVariant>> variantsByKeyword;
    private final Map<Opcode, Integer> opcodeKeywordPosition;
    private final Map<InstructionParameterType, Collection<String>> validArgumentValues;
    protected final boolean mlog8;
    private int tmpIndex = 0;
    private int labelIndex = 0;
    private int functionIndex = 0;

    // Protected to allow a subclass to use this constructor in unit tests
    protected BaseInstructionProcessor(Consumer<MindcodeMessage> messageConsumer, ProcessorVersion processorVersion,
            ProcessorEdition processorEdition, List<OpcodeVariant> opcodeVariants) {
        super(messageConsumer);
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        this.opcodeVariants = opcodeVariants;
        this.mlog8 = processorVersion.matches(ProcessorVersion.V8A, ProcessorVersion.MAX);
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
    public CallRecInstruction createCallRecursive(AstContext astContext, LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr, LogicVariable returnValue) {
        return (CallRecInstruction) createInstruction(astContext, CALLREC, stack, callAddr, retAddr, returnValue);
    }

    @Override
    public CallInstruction createCallStackless(AstContext astContext, LogicAddress address, LogicVariable returnValue) {
        return (CallInstruction) createInstruction(astContext, CALL, address, returnValue);
    }

    @Override
    public EndInstruction createEnd(AstContext astContext) {
        return (EndInstruction) createInstruction(astContext, END);
    }

    @Override
    public FormatInstruction createFormat(AstContext astContext, LogicValue what) {
        return (FormatInstruction) createInstruction(astContext, FORMAT, what);
    }

    @Override
    public GetlinkInstruction createGetLink(AstContext astContext, LogicVariable result, LogicValue index) {
        return (GetlinkInstruction) createInstruction(astContext, GETLINK, result, index);
    }

    @Override
    public GotoInstruction createGoto(AstContext astContext, LogicVariable address, LogicLabel marker) {
        return (GotoInstruction) createInstruction(astContext, GOTO, address, marker);
    }

    @Override
    public GotoLabelInstruction createGotoLabel(AstContext astContext, LogicLabel label, LogicLabel marker) {
        return (GotoLabelInstruction) createInstruction(astContext, GOTOLABEL, label, marker);
    }

    public GotoOffsetInstruction createGotoOffset(AstContext astContext, LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        return (GotoOffsetInstruction) createInstruction(astContext, GOTOOFFSET, target, value, offset, marker);
    }

    @Override
    public JumpInstruction createJump(AstContext astContext, LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        return (JumpInstruction) createInstruction(astContext, JUMP, target, condition, x, y);
    }

    @Override
    public JumpInstruction createJumpUnconditional(AstContext astContext, LogicLabel target) {
        return (JumpInstruction) createInstruction(astContext, JUMP, target, Condition.ALWAYS);
    }

    @Override
    public LabelInstruction createLabel(AstContext astContext, LogicLabel label) {
        return (LabelInstruction) createInstruction(astContext, LABEL, label);
    }

    @Override
    public LookupInstruction createLookup(AstContext astContext, LogicKeyword type, LogicVariable result, LogicValue index) {
        return (LookupInstruction) createInstruction(astContext, LOOKUP, type, result, index);
    }

    @Override
    public NoOpInstruction createNoOp(AstContext astContext) {
        return (NoOpInstruction) createInstruction(astContext, NOOP);
    }

    @Override
    public OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first) {
        return (OpInstruction) createInstruction(astContext, OP, operation, target, first);
    }

    @Override
    public OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return (OpInstruction) createInstruction(astContext, OP, operation, target, first, second);
    }

    @Override
    public PopInstruction createPop(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return (PopInstruction) createInstruction(astContext, POP, memory, value);
    }

    @Override
    public PrintInstruction createPrint(AstContext astContext, LogicValue what) {
        return (PrintInstruction) createInstruction(astContext, PRINT, what);
    }

    @Override
    public PrintflushInstruction createPrintflush(AstContext astContext, LogicVariable messageBlock) {
        return (PrintflushInstruction) createInstruction(astContext, PRINTFLUSH, messageBlock);
    }

    @Override
    public PushInstruction createPush(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return (PushInstruction) createInstruction(astContext, PUSH, memory, value);
    }

    @Override
    public ReadInstruction createRead(AstContext astContext, LogicVariable result, LogicVariable memory, LogicValue index) {
        return (ReadInstruction) createInstruction(astContext, READ, result, memory, index);
    }

    @Override
    public RemarkInstruction createRemark(AstContext astContext, LogicValue what) {
        return (RemarkInstruction) createInstruction(astContext, REMARK, what);
    }

    @Override
    public ReturnInstruction createReturn(AstContext astContext, LogicVariable stack) {
        return (ReturnInstruction) createInstruction(astContext, RETURN, stack);
    }

    @Override
    public SensorInstruction createSensor(AstContext astContext, LogicVariable result, LogicValue target, LogicValue property) {
        return (SensorInstruction) createInstruction(astContext, SENSOR, result, target, property);
    }

    @Override
    public SetInstruction createSet(AstContext astContext, LogicVariable target, LogicValue value) {
        return (SetInstruction) createInstruction(astContext, SET, target, value);
    }

    @Override
    public SetAddressInstruction createSetAddress(AstContext astContext, LogicVariable variable, LogicLabel address) {
        return (SetAddressInstruction) createInstruction(astContext, SETADDR, variable, address);
    }

    @Override
    public StopInstruction createStop(AstContext astContext) {
        return (StopInstruction) createInstruction(astContext, STOP);
    }

    @Override
    public WriteInstruction createWrite(AstContext astContext, LogicValue value, LogicVariable memory, LogicValue index) {
        return (WriteInstruction) createInstruction(astContext, WRITE, value, memory, index);
    }


    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, LogicArgument... arguments) {
        return createInstruction(astContext, opcode, List.of(arguments));
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
    public LogicInstruction normalizeInstruction(LogicInstruction instruction) {
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
        if (outputs.size() == 1 && outputs.get(0) == InstructionParameterType.RESULT) {
            return new BaseResultInstruction(astContext, opcode, arguments, params);
        } else {
            return new BaseInstruction(astContext, opcode, arguments, params);
        }
    }

    @Override
    public void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        AstContext astContext = instruction.getAstContext();

        if (instruction instanceof NoOpInstruction || instruction instanceof GotoLabelInstruction || instruction instanceof LabelInstruction) {
            return;
        }

        if (instruction instanceof PushInstruction ix) {
            consumer.accept(createWrite(astContext, ix.getVariable(), ix.getMemory(), stackPointer()));
            consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
            return;
        }
        if (instruction instanceof PopInstruction ix) {
            consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
            consumer.accept(createRead(astContext, ix.getVariable(), ix.getMemory(), stackPointer()));
            return;
        }
        if (instruction instanceof CallRecInstruction ix) {
            consumer.accept(createInstruction(astContext, WRITE, ix.getRetAddr(), ix.getStack(), stackPointer()));
            consumer.accept(createOp(astContext, ADD, stackPointer(), stackPointer(), LogicNumber.ONE));
            consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, ix.getCallAddr()));
            return;
        }
        if (instruction instanceof ReturnInstruction ix) {
            LogicVariable retAddr = nextTemp();
            consumer.accept(createOp(astContext, Operation.SUB, stackPointer(), stackPointer(), LogicNumber.ONE));
            consumer.accept(createRead(astContext, retAddr, ix.getStack(), stackPointer()));
            consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, retAddr));
            return;
        }
        if (instruction instanceof CallInstruction ix) {
            consumer.accept(createJumpUnconditional(astContext, ix.getCallAddr()));
            return;
        }
        if (instruction instanceof SetAddressInstruction ix) {
            consumer.accept(createInstruction(astContext, SET, ix.getResult(), ix.getLabel()));
            return;
        }
        if (instruction instanceof GotoInstruction ix) {
            consumer.accept(createInstruction(astContext, SET, LogicBuiltIn.COUNTER, ix.getIndirectAddress()));
            return;
        }

        // Note: GotoOffsetInstruction is handled by LabelResolver, as the actual label value needs to be known
        consumer.accept(instruction);
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
    public List<InstructionParameterType> getParameters(Opcode opcode, List<? extends LogicArgument> arguments) {
        OpcodeVariant opcodeVariant = getOpcodeVariant(opcode, arguments);
        return opcodeVariant == null ? null : opcodeVariant.parameterTypes();
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
                error(instruction.getAstContext().node(),
                        "Invalid value '%s' for parameter '%s': allowed values are '%s'.", argument.toMlog(),
                        namedParameter.name(), String.join("', '", validArgumentValues.get(type)));
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
                .map(v -> findFirstIndex(v.namedParameters(), a -> a.type().isSelector()))
                .distinct()
                .toList();

        if (indexes.size() != 1)  {
            throw new MindcodeInternalError("Cannot determine variant selector position for opcode " + opcode);
        }

        return indexes.get(0);
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

        return Map.copyOf(map);
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

    // 20 digits precision
    protected static final MathContext CONVERSION_CONTEXT = new MathContext(17, RoundingMode.HALF_UP);

    protected Optional<String> mlogFormat(double value, String literal) {
        if (mlog8) {
            return mlogFormat8(value, literal);
        }

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

                String mantissa =  literalFloat.substring(0, dot) + literalFloat.substring(dot + 1, exp);
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
                        : mantissa.substring(0, lastValidDigit + 1) + literalFloat.charAt(exp) + exponent;

                double reFloat = Double.parseDouble(String.valueOf(valueFloat));
                double absDiff = Math.abs(reFloat - value);
                double relDiff = absDiff / value;
                if (relDiff > 1e-9) {
                    // This warning doesn't come with an input position
                    // This will no longer happen in ML8 - won't fix.
                    messageConsumer.accept(CompilerMessage.warn(InputPosition.EMPTY,
                            "Loss of precision while creating mlog literals (original value %s, encoded value %s)",
                            literal, mlog));
                }

                return Optional.of(mlog);
            } else {
                return Optional.of(literalFloat);
            }
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> mlogFormat8(double value, String literal) {
        if (Double.isFinite(value)) {
            double abs = Math.abs(value);

            // Can it be represented as a double at all?
            if (!Double.isFinite(abs)) {
                return Optional.empty();
            }

            // Is it zero?
            if (abs <= Double.MIN_NORMAL) {
                return Optional.of("0");
            }

            if (1e-20 <= abs && abs < Long.MAX_VALUE) {
                // Fits into a long, Mindustry can convert it using decimal notation
                // Hopefully more readable in mlog
                BigDecimal decimal = new BigDecimal(literal, CONVERSION_CONTEXT);
                return Optional.of(decimal.stripTrailingZeros().toPlainString());
            }

            // Cannot avoid exponent. Format it so that Mindustry understands it.
            String literalDouble = Double.toString(value);
            int dot = literalDouble.indexOf('.');
            int exp = literalDouble.indexOf('E');

            if (dot >= 0 && exp >= 0) {
                if (dot >= exp) {
                    return Optional.empty(); // Not possible, but hey
                }

                int exponent = Integer.parseInt(literalDouble.substring(exp + 1)) ;
                String mantissa =  literalDouble.substring(0, dot) + literalDouble.substring(dot + 1, exp);
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
                        : mantissa.substring(0, lastValidDigit + 1) + literalDouble.charAt(exp) + exponent;

                return Optional.of(mlog);
            } else {
                return Optional.of(literalDouble);
            }
        } else {
            return Optional.empty();
        }
    }
}
