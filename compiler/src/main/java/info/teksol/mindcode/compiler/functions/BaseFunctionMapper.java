package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseFunctionMapper extends AbstractMessageEmitter implements FunctionMapper {
    private final AstContext staticAstContext = AstContext.createStaticRootNode();
    final Supplier<AstContext> astContextSupplier;
    final InstructionProcessor instructionProcessor;
    final ProcessorVersion processorVersion;
    final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;
    private final List<SampleGenerator> sampleGenerators;

    private final Map<Opcode, List<SampleGenerator>> opcodeSampleGenerators;

    BaseFunctionMapper(InstructionProcessor InstructionProcessor, Supplier<AstContext> astContextSupplier,
            Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        this.astContextSupplier = astContextSupplier;
        this.instructionProcessor = InstructionProcessor;

        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        propertyMap = createPropertyMap();
        functionMap = createFunctionMap();

        sampleGenerators = new ArrayList<>();
        propertyMap.values().forEach(p -> p.register(sampleGenerators::add));
        functionMap.values().forEach(f -> f.register(sampleGenerators::add));
        sampleGenerators.sort(Comparator.comparing(SampleGenerator::getName));

        opcodeSampleGenerators = sampleGenerators.stream()
                .collect(Collectors.groupingBy(s -> s.getOpcodeVariant().opcode()));
    }

    @Override
    public LogicValue handleProperty(AstNode node, Consumer<LogicInstruction> program, String propertyName, LogicValue target,
            List<LogicFunctionArgument> arguments) {
        PropertyHandler handler = propertyMap.get(propertyName);
        return handler == null ? null : handler.handleProperty(node, program, target, arguments);
    }

    @Override
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, String functionName, List<LogicFunctionArgument> arguments) {
        FunctionHandler handler = functionMap.get(functionName);
        return handler == null ? null : handler.handleFunction(call, program, arguments);
    }

    public String decompile(MlogInstruction instruction) {
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
                                instructionProcessor.getOpcodeVariants().indexOf(s.getOpcodeVariant()),
                                s.getName(),
                                s.generateSampleCall(),
                                s.generateSampleInstruction(),
                                s.getOpcodeVariant().edition(),
                                s.getNote()
                        )
                ),
                sampleGenerators.stream().filter(s -> s.generateSecondarySampleCall() != null).map(
                        s -> new FunctionSample(
                                instructionProcessor.getOpcodeVariants().indexOf(s.getOpcodeVariant()),
                                s.getName(),
                                s.generateSecondarySampleCall(),
                                s.generateSampleInstruction(),
                                s.getOpcodeVariant().edition(),
                                s.getNote()
                        )
                )
        ).collect(Collectors.toList());
    }

    LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return instructionProcessor.createInstruction(astContextSupplier.get(), opcode, args);
    }

    LogicInstruction createSampleInstruction(Opcode opcode, List<LogicArgument> args) {
        return instructionProcessor.createInstructionUnchecked(staticAstContext, opcode, args);
    }

    static LogicKeyword toKeyword(LogicValue arg) {
        // Syntactically, instruction keywords are just identifiers.
        // To convert it to keyword, we use the plain variable name.
        return switch (arg) {
            case LogicVariable lv -> LogicKeyword.create(lv.getName());
            case LogicBoolean lb  -> LogicKeyword.create(lb.toMlog()); // Handles the case where true/false are used as a selector or keyword
            default -> throw new MindcodeInternalError("Unexpected type of argument " + arg);
        };
    }

    static LogicKeyword toKeywordOptional(LogicValue arg) {
        return switch (arg) {
            case LogicVariable lv -> LogicKeyword.create(lv.getName());
            case LogicBoolean lb  -> LogicKeyword.create(lb.toMlog()); // Handles the case where true/false are used as a selector or keyword
            default               -> LogicKeyword.create(""); // A keyword that cannot exist
        };
    }

    static String joinNamedArguments(List<NamedParameter> arguments) {
        return arguments.stream().map(NamedParameter::name).collect(Collectors.joining(", "));
    }

    //
    // Property handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, PropertyHandler> createPropertyMap() {
        Map<String, PropertyHandler> map = instructionProcessor.getOpcodeVariants().stream()
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
        String name = selector.isPresent() ? selector.get().name() : opcodeVariant.opcode().toString();
        return new StandardPropertyHandler(this, name, opcodeVariant, numArgs, results > 0);
    }

    //
    // Function handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, FunctionHandler> createFunctionMap() {
        Map<String, List<FunctionHandler>> functionGroups = instructionProcessor.getOpcodeVariants().stream()
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
        switch(opcode) {
            case FORMAT, PRINT: return new VariableArityFunctionHandler(this, opcode.toString(), opcodeVariant);
            case UBIND:         return new UbindFunctionHandler(this, opcode.toString(), opcodeVariant);
            case MESSAGE:
                if (processorVersion.ordinal() >= ProcessorVersion.V8A.ordinal()) {
                    return new MessageFunctionHandler(this, opcode.toString(), opcodeVariant);
                }
        }

        List<NamedParameter> arguments = opcodeVariant.namedParameters();
        Optional<NamedParameter> selector = arguments.stream().filter(a -> a.type().isFunctionName()).findFirst();
        String name = functionName(opcodeVariant, selector.orElse(null));
        final int outputs = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType.RESULT::equals).count();
        final int unused  = (int) arguments.stream().map(NamedParameter::type).filter(InstructionParameterType::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0 && opcodeVariant.opcode() != Opcode.SYNC) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Number of function parameters: selectors and results are not in parameter list
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0) - unused;

        // Optional parameters are output arguments at the end of the argument list
        int optional = 0;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            InstructionParameterType type = arguments.get(i).type();
            if (!type.isOutput() && !type.isUnused()) {
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

    private String functionName(OpcodeVariant opcodeVariant, NamedParameter selector) {
        return switch (opcodeVariant.opcode()) {
            case DRAW   -> switch (selector.name()) {
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
                    .collect(Collectors.toMap(f -> ((SelectorFunction) f).getKeyword(), f -> f));

            String name = functions.getFirst().getName();
            OpcodeVariant opcodeVariant = functions.getFirst().getOpcodeVariant();
            return new MultiplexedFunctionHandler(this, keywordMap, name, opcodeVariant);
        }
    }

}
