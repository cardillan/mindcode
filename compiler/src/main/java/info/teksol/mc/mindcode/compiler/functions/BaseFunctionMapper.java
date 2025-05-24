package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.*;
import info.teksol.mc.util.Tuple2Nullable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class BaseFunctionMapper extends AbstractMessageEmitter implements FunctionMapper {
    private final AstContext staticAstContext = AstContext.createStaticRootNode();
    final InstructionProcessor processor;
    final @Nullable CodeAssembler assembler;
    final ProcessorVersion processorVersion;
    final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;
    private final List<SampleGenerator> sampleGenerators;

    private final Map<Opcode, List<SampleGenerator>> opcodeSampleGenerators;

    public BaseFunctionMapper(MessageConsumer messageConsumer, InstructionProcessor processor, @Nullable CodeAssembler assembler) {
        super(messageConsumer);
        this.processor = processor;
        this.assembler = assembler;
        processorVersion = processor.getProcessorVersion();
        processorEdition = processor.getProcessorEdition();
        propertyMap = createPropertyMap();
        functionMap = createFunctionMap();

        sampleGenerators = new ArrayList<>();
        propertyMap.values().forEach(p -> p.register(sampleGenerators::add));
        functionMap.values().forEach(f -> f.register(sampleGenerators::add));
        sampleGenerators.sort(Comparator.comparing(SampleGenerator::getName));

        opcodeSampleGenerators = sampleGenerators.stream()
                .collect(Collectors.groupingBy(s -> s.getOpcodeVariant().opcode()));
    }

    public BaseFunctionMapper(FunctionMapperContext context) {
        this(context.messageConsumer(), context.instructionProcessor(), context.assembler());
    }

    @Override
    public @Nullable ValueStore handleProperty(AstFunctionCall call, ValueStore target, List<FunctionArgument> arguments) {
        PropertyHandler handler = propertyMap.get(call.getFunctionName());
        return handler == null ? null : handler.handleProperty(call, target, arguments);
    }

    @Override
    public @Nullable ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments) {
        FunctionHandler handler = functionMap.get(call.getFunctionName());
        if (handler == null) return null;
        assert assembler != null;
        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        return handler.handleFunction(call, arguments);
    }

    public @Nullable String decompile(MlogInstruction instruction) {
        List<SampleGenerator> generators = opcodeSampleGenerators.getOrDefault(instruction.getOpcode(), List.of());
        for (SampleGenerator generator : generators) {
            // Force function call syntax for these instructions
            if (instruction.getArgs().size() == 1 && generator instanceof StandardPropertyHandler) continue;

            String code = generator.decompile(instruction);
            if (code != null) {
                return code;
            }
        }

        return null;
    }

    @Override
    public List<FunctionSample> generateSamples() {
        return Stream.concat(
                sampleGenerators.stream().map(
                        s -> new FunctionSample(
                                processor.getOpcodeVariants().indexOf(s.getOpcodeVariant()),
                                s.getName(),
                                s.generateSampleCall(),
                                s.generateSampleInstruction(),
                                s.getOpcodeVariant().edition(),
                                s.getNote()
                        )
                ),
                sampleGenerators.stream()
                        .map(s -> Tuple2Nullable.<SampleGenerator, @Nullable String>of(s, s.generateSecondarySampleCall()))
                        .filter(t -> t.e2() != null)
                        .map(t -> new FunctionSample(
                                        processor.getOpcodeVariants().indexOf(Objects.requireNonNull(t.e1()).getOpcodeVariant()),
                                        t.e1().getName(),
                                        t.e2(),
                                        t.e1().generateSampleInstruction(),
                                        t.e1().getOpcodeVariant().edition(),
                                        t.e1().getNote()
                                )
                        )
        ).collect(Collectors.toList());
    }

    LogicInstruction createSampleInstruction(Opcode opcode, List<LogicArgument> args) {
        return processor.createInstructionUnchecked(staticAstContext, opcode, args);
    }

    static String joinNamedArguments(List<NamedParameter> arguments) {
        return arguments.stream().map(NamedParameter::name).collect(Collectors.joining(", "));
    }

    //
    // Property handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, PropertyHandler> createPropertyMap() {
        Map<String, PropertyHandler> map = processor.getOpcodeVariants().stream()
                .filter(v -> v.functionMapping() == FunctionMapping.PROP || v.functionMapping() == FunctionMapping.BOTH)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createPropertyHandler)
                .collect(Collectors.toMap(PropertyHandler::getName, f -> f));

        // V6 backwards compatibility
        if (map.containsKey("config")) {
            map.put("configure", new DeprecatedPropertyHandler(this, "configure", map.get("config")));
        }

        return map;
    }

    private PropertyHandler createPropertyHandler(OpcodeVariant opcodeVariant) {
        List<NamedParameter> arguments = opcodeVariant.namedParameters();
        Optional<NamedParameter> selector = arguments.stream().filter(a -> a.type().isFunctionName()).findFirst();
        final int outputs = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedParameter::type).filter(a -> a == InstructionParameterType.RESULT).count();
        final int unused  = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Subtract one more for target
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0) - unused - 1;
        String name = selector.map(NamedParameter::name).orElseGet(() -> opcodeVariant.opcode().toString());
        return new StandardPropertyHandler(this, name, opcodeVariant, numArgs, results > 0);
    }

    //
    // Function handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, FunctionHandler> createFunctionMap() {
        Map<String, List<FunctionHandler>> functionGroups = processor.getOpcodeVariants().stream()
                .filter(v -> v.functionMapping() == FunctionMapping.FUNC || v.functionMapping() == FunctionMapping.BOTH)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createFunctionHandler)
                .collect(Collectors.groupingBy(FunctionHandler::getName));

        // Create MultiplexedFunctionHandler for functions with identical names
        return functionGroups.values().stream()
                .map(this::collapseFunctions)
                .collect(Collectors.toMap(FunctionHandler::getName, f -> f));
    }

    private FunctionHandler createFunctionHandler(OpcodeVariant opcodeVariant) {
        final Opcode opcode = opcodeVariant.opcode();

        // Handle special cases
        // Note: the print() function is handled by the compiler to cover the formating variant,
        // but it is included here to handle documentation and decompilation.
        switch (opcode) {
            case FORMAT, PRINT, PRINTCHAR:
                return new VariableArityFunctionHandler(this, opcode.toString(), opcodeVariant);

            case UBIND:
                return new UbindFunctionHandler(this, opcode.toString(), opcodeVariant);

            case MESSAGE:
                if (processorVersion.ordinal() >= ProcessorVersion.V8A.ordinal()) {
                    return new MessageFunctionHandler(this, opcode.toString(), opcodeVariant);
                }
                break;
        }

        List<NamedParameter> arguments = opcodeVariant.namedParameters();
        NamedParameter selector = arguments.stream().filter(a -> a.type().isFunctionName()).findFirst().orElse(null);
        String name = functionName(opcodeVariant, selector);
        final int outputs = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType.RESULT::equals).count();
        final int unused  = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0 && opcodeVariant.opcode() != Opcode.SYNC) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Number of function parameters: selectors and results are not in the parameter list
        int numArgs = arguments.size() - results - (selector == null ? 0 : 1) - unused;

        // Optional parameters are output arguments at the end of the argument list
        int optional = 0;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            InstructionParameterType type = arguments.get(i).type();
            if (type.isInput() || !type.isOutput() && !type.isUnused()) {
                break;
            }
            // Result and unused do not count, as they're not in the parameter list.
            if (type.isOutput() && type != InstructionParameterType.RESULT && !type.isUnused()) {
                optional++;
            }
        }

        int minArgs = numArgs - optional;
        return new StandardFunctionHandler(this, name, opcodeVariant, minArgs, numArgs, results > 0);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private String functionName(OpcodeVariant opcodeVariant, @Nullable NamedParameter selector) {
        return switch (opcodeVariant.opcode()) {
            case DRAW   -> switch (Objects.requireNonNull(selector).name()) {
                    case "print" -> "drawPrint";
                    default      -> selector.name();
            };
            case STOP   -> "stopProcessor";
            case STATUS -> switch (opcodeVariant.namedParameters().getFirst().name()) {
                    case "true"  -> "clearStatus";
                    case "false" -> "applyStatus";
                    default      -> throw new MindcodeInternalError("Opcode variant " + opcodeVariant + " not mapped to a function.");
                };
            default     -> selector == null ? opcodeVariant.opcode().toString() : selector.name();
        };
    }

    private FunctionHandler collapseFunctions(List<FunctionHandler> functions) {
        if (functions.size() == 1) {
            return functions.getFirst();
        } else {
            if (functions.stream().anyMatch(fn -> !(fn instanceof SelectorFunction))) {
                throw new InvalidMetadataException("Function name collision; " + functions.getFirst().getName() + " maps to:"
                        + System.lineSeparator()
                        + functions.stream().map(f -> f.getOpcodeVariant().toString()).collect(Collectors.joining(System.lineSeparator())));
            }

            Map<String, FunctionHandler> keywordMap = functions.stream()
                    .collect(Collectors.toMap(f -> ((SelectorFunction) f).getSelector().name(), f -> f));

            String name = functions.getFirst().getName();
            OpcodeVariant opcodeVariant = functions.getFirst().getOpcodeVariant();
            return new MultiplexedFunctionHandler(this, keywordMap, name, opcodeVariant);
        }
    }

}
