package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.CompilerMessage;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.ArgumentType;
import info.teksol.mindcode.mindustry.logic.NamedArgument;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.OpcodeVariant;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseFunctionMapper implements FunctionMapper {
    private final InstructionProcessor instructionProcessor;
    private final Consumer<CompilerMessage> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;
    private final List<SampleGenerator> sampleGenerators;
    private final Map<String, BuiltInFunctionHandler> builtInFunctionMap;

    BaseFunctionMapper(InstructionProcessor InstructionProcessor, Consumer<CompilerMessage> messageConsumer) {
        this.instructionProcessor = InstructionProcessor;
        this.messageConsumer = messageConsumer;

        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        propertyMap = createPropertyMap();
        functionMap = createFunctionMap();
        builtInFunctionMap = createBuiltInFunctionMap();
        
        sampleGenerators = new ArrayList();
        propertyMap.values().forEach(p -> p.register(sampleGenerators::add));
        functionMap.values().forEach(f -> f.register(sampleGenerators::add));
        sampleGenerators.sort(Comparator.comparing(SampleGenerator::getName));
    }

    @Override
    public String handleProperty(LogicInstructionPipeline pipeline, String propertyName, String target, List<String> params) {
        PropertyHandler handler = propertyMap.get(propertyName);
        return handler == null ? null : handler.handleProperty(pipeline, target, params);
    }

    @Override
    public String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params) {
        FunctionHandler handler = functionMap.get(functionName);
        BuiltInFunctionHandler builtInHandler = builtInFunctionMap.get(functionName);
        return handler == null
                ? builtInHandler == null ? null : builtInHandler.handleFunction(pipeline, params)
                : handler.handleFunction(pipeline, params);
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
                                s.getOpcodeVariant().getEdition(),
                                s.getNote()
                        )
                ),
                sampleGenerators.stream().filter(s -> s.generateSecondarySampleCall() != null).map(
                        s -> new FunctionSample(
                                instructionProcessor.getOpcodeVariants().indexOf(s.getOpcodeVariant()),
                                s.getName(),
                                s.generateSecondarySampleCall(),
                                s.generateSampleInstruction(),
                                s.getOpcodeVariant().getEdition(),
                                s.getNote()
                        )
                )
        ).collect(Collectors.toList());
    }

    private LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private String stripLocalPrefix(String value) {
        // The argument is not a variable. If there was a local prefix added, we need to strip it.
        if (value.startsWith(instructionProcessor.getLocalPrefix())) {
            int startIndex = value.indexOf('_', instructionProcessor.getLocalPrefix().length());
            return startIndex >= 0 ? value.substring(startIndex + 1) : value;
        } else {
            return value;
        }
    }

    private static String joinNamedArguments(List<NamedArgument> arguments) {
        return arguments.stream().map(NamedArgument::getName).collect(Collectors.joining(", "));
    }

    private interface SampleGenerator {
        String getName();
        OpcodeVariant getOpcodeVariant();
        String generateSampleCall();
        LogicInstruction generateSampleInstruction();

        default String getNote() {
            return "";
        }

        default String generateSecondarySampleCall() {
            return null;
        }

        default void register(Consumer<SampleGenerator> registry) {
            registry.accept(this);
        }
    }
    
    private interface PropertyHandler extends SampleGenerator {
        String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params);

        default Opcode getOpcode() {
            return getOpcodeVariant().getOpcode();
        }
    }

    private interface FunctionHandler extends SampleGenerator {
        String handleFunction(LogicInstructionPipeline pipeline, List<String> params);

        default Opcode getOpcode() {
            return getOpcodeVariant().getOpcode();
        }
    }

    private interface SelectorFunction extends FunctionHandler {
        String getKeyword();
    }

    private interface BuiltInFunctionHandler {
        String handleFunction(LogicInstructionPipeline pipeline, List<String> params);
    }

    //
    // Property handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, PropertyHandler> createPropertyMap() {
        Map<String, PropertyHandler> map = instructionProcessor.getOpcodeVariants().stream()
                .filter(v -> v.getFunctionMapping() == OpcodeVariant.FunctionMapping.PROP || v.getFunctionMapping() == OpcodeVariant.FunctionMapping.BOTH)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createPropertyHandler)
                .collect(Collectors.toMap(PropertyHandler::getName, f -> f));

        // V6 backwards compatibility
        if (map.containsKey("config")) {
            map.put("configure", new DeprecatedPropertyHandler("configure", map.get("config")));
        }

        return map;
    }

    private PropertyHandler createPropertyHandler(OpcodeVariant opcodeVariant) {
        List<NamedArgument> arguments = opcodeVariant.getArguments();
        Optional<NamedArgument> selector = arguments.stream().filter(a -> a.getType().isFunctionName()).findFirst();
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedArgument::getType).filter(a -> a == ArgumentType.RESULT).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Subtract one more for target
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0) - unused - 1;
        String name = selector.isPresent() ? selector.get().getName() : opcodeVariant.getOpcode().toString();
        return new StandardPropertyHandler(name, opcodeVariant, numArgs, results > 0);
    }

    private abstract class AbstractPropertyHandler implements PropertyHandler {
        protected final OpcodeVariant opcodeVariant;
        protected final String name;
        protected final int numArgs;

        AbstractPropertyHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
            this.name = name;
            this.opcodeVariant = opcodeVariant;
            this.numArgs = numArgs;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public OpcodeVariant getOpcodeVariant() {
            return opcodeVariant;
        }

        protected void checkArguments(List<String> params) {
            if (params.size() != numArgs) {
                throw new WrongNumberOfParametersException("Function '" + name + "': wrong number of parameters (expected "
                        + numArgs + ", found " + params.size() + ")");
            }
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            return instructionProcessor.fromOpcodeVariant(getOpcodeVariant());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", numArgs=" + numArgs + '}';
        }
    }

    private class StandardPropertyHandler extends AbstractPropertyHandler  {
        private final boolean hasResult;

        StandardPropertyHandler(String name, OpcodeVariant opcodeVariant, int numArgs, boolean hasResult) {
            super(name, opcodeVariant, numArgs);
            this.hasResult = hasResult;
        }

        @Override
        public String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params) {
            checkArguments(params);

            String tmp = hasResult ? instructionProcessor.nextTemp() : "null";
            List<String> args = new ArrayList<>();
            int paramIndex = 0;

            for (NamedArgument a : opcodeVariant.getArguments()) {
                if (a.getType() == ArgumentType.RESULT) {
                    args.add(tmp);
                } else if (a.getType() == ArgumentType.BLOCK) {
                    // No stripping: it is either a block name - already unprefixed, or a regular variable.
                    args.add(target);
                } else if (a.getType().isSelector()  && !a.getType().isFunctionName()) {
                    // Selector that IS NOT a function name is taken from the parameter list
                    args.add(stripLocalPrefix(params.get(paramIndex++)));
                } else if (a.getType().isSelector() || a.getType().isUnused()) {
                    // Selector that IS a function name isn't in a parameter list and must be filled in
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    args.add(a.getType().isOutput() ? instructionProcessor.nextTemp() : a.getName());
                } else if (a.getType().isInput()) {
                    // Input argument - take it as it is
                    args.add(params.get(paramIndex++));
                } else if (a.getType().isOutput()) {
                    if (paramIndex >= params.size()) {
                        // Optional arguments are always output; generate temporary variable for them
                        args.add(instructionProcessor.nextTemp());
                    } else {
                        // Block name cannot be used as output argument
                        String argument = params.get(paramIndex++);
                        if (instructionProcessor.isBlockName(argument)) {
                            throw new GenerationException("Using variable " + argument + " in function " + name
                                    + " not allowed (name reserved for linked blocks)");
                        }
                        args.add(argument);
                    }
                } else {
                    // Not a variable - strip prefix
                    args.add(stripLocalPrefix(params.get(paramIndex++)));
                }
            }

            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            StringBuilder str = new StringBuilder();
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument result = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.RESULT);
            if (result != null) {
                str.append(result.getName()).append(" = ");
            }

            NamedArgument block = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.BLOCK);
            str.append(block.getName()).append('.');


            List<String> strArguments = arguments.stream()
                    .filter(a -> !a.getType().isUnused() && !a.getType().isFunctionName())
                    .map(NamedArgument::getName)
                    .collect(Collectors.toList());

            str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
            return str.toString();
        }

        @Override
        public String generateSecondarySampleCall() {
            List<NamedArgument> args = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.getType() == ArgumentType.BLOCK);
            CollectionUtils.removeFirstMatching(args, a -> a.getType().isSelector());
            if (args.size() == 1 && args.get(0).getType() == ArgumentType.INPUT) {
                return blockArgument.getName() + "." + getName() + " = " + args.get(0).getName();
            } else {
                return null;
            }
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            AtomicInteger counter = new AtomicInteger();
            String tmpPrefix = instructionProcessor.getTempPrefix();
            List<String> arguments = getOpcodeVariant().getArguments().stream()
                    .map(a -> a.getType() == ArgumentType.UNUSED_OUTPUT ? tmpPrefix + counter.getAndIncrement() : a.getName())
                    .collect(Collectors.toList());
            return instructionProcessor.createInstructionUnchecked(getOpcode(), arguments);
        }
    }

    private class DeprecatedPropertyHandler implements PropertyHandler {
        private final PropertyHandler replacement;
        private final String deprecated;
        private boolean warningEmitted = false;

        DeprecatedPropertyHandler(String deprecated, PropertyHandler replacement) {
            this.deprecated = deprecated;
            this.replacement = replacement;
        }

        @Override
        public String getName() {
            return replacement.getName();
        }

        @Override
        public OpcodeVariant getOpcodeVariant() {
            return replacement.getOpcodeVariant();
        }

        @Override
        public String generateSampleCall() {
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument blockArgument = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.BLOCK);
            CollectionUtils.removeFirstMatching(arguments, a -> a.getType().isSelector());
            return blockArgument.getName() + "." + deprecated + "(" + joinNamedArguments(arguments) + ")";
        }

        @Override
        public String generateSecondarySampleCall() {
            List<NamedArgument> args = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.getType() == ArgumentType.BLOCK);
            CollectionUtils.removeFirstMatching(args, a -> a.getType().isSelector());
            if (args.size() == 1 && args.get(0).getType() == ArgumentType.INPUT) {
                return blockArgument.getName() + "." + deprecated + " = " + args.get(0).getName();
            } else {
                return null;
            }
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            return replacement.generateSampleInstruction();
        }

        @Override
        public String getNote() {
            return "Deprecated. Use '" + replacement.getName() + "' instead.";
        }

        @Override
        public String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params) {
            if (!warningEmitted) {
                messageConsumer.accept(CompilerMessage.warn(
                        "Function '" + deprecated + "' is no longer supported in Mindustry Logic version " +
                        processorVersion + "; using '" + replacement.getName() + "' instead."));
                warningEmitted = true;
            }
            return replacement.handleProperty(pipeline, target, params);
        }
    }

    //
    // Function handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, FunctionHandler> createFunctionMap() {
        Map<String, List<FunctionHandler>> functionGroups = instructionProcessor.getOpcodeVariants().stream()
                .filter(v -> v.getFunctionMapping() == OpcodeVariant.FunctionMapping.FUNC || v.getFunctionMapping() == OpcodeVariant.FunctionMapping.BOTH)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createFunctionHandler)
                .collect(Collectors.groupingBy(FunctionHandler::getName));

        // Create MultiplexedFunctionHandler for functions with identical names
        return functionGroups.values().stream()
                .map(this::collapseFunctions)
                .collect(Collectors.toMap(FunctionHandler::getName, f -> f));
    }

    private FunctionHandler createFunctionHandler(OpcodeVariant opcodeVariant) {
        final Opcode opcode = opcodeVariant.getOpcode();

        // Handle special cases
        switch(opcode) {
            case PRINT:     return new PrintFunctionHandler(opcode.toString(), opcodeVariant, 1);
        }

        List<NamedArgument> arguments = opcodeVariant.getArguments();
        Optional<NamedArgument> selector = arguments.stream().filter(a -> a.getType().isFunctionName()).findFirst();
        String name = functionName(opcodeVariant, selector);
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType.RESULT::equals).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Number of function parameters: selectors and results are not in parameter list
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0) - unused;

        // Optional parameters are output arguments at the end of the argument list
        int optional = 0;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            ArgumentType type = arguments.get(i).getType();
            if (!type.isOutput() && !type.isUnused()) {
                break;
            }
            // Result and unused do not count, as they're not in the parameter list.
            if (type.isOutput() && type != ArgumentType.RESULT && !type.isUnused()) {
                optional++;
            }
        }

        int minArgs = numArgs - optional;
        return new StandardFunctionHandler(name, opcodeVariant, minArgs, numArgs, results > 0);
    }

    private String functionName(OpcodeVariant opcodeVariant, Optional<NamedArgument> selector) {
        switch (opcodeVariant.getOpcode()) {
            case STOP:
                return "stopProcessor";

            case STATUS:
                switch (opcodeVariant.getArguments().get(0).getName()) {
                    case "true":    return "clearStatus";
                    case "false":   return "applyStatus";
                    default:        throw new GenerationException("Don't know function name of " + opcodeVariant);
                }

            default:
                return selector.isPresent() ? selector.get().getName() : opcodeVariant.getOpcode().toString();
        }
    }

    private FunctionHandler collapseFunctions(List<FunctionHandler> functions) {
        if (functions.size() == 1) {
            return functions.get(0);
        } else {
            if (functions.stream().anyMatch(fn -> !(fn instanceof SelectorFunction))) {
                throw new InvalidMetadataException("Function name collision; " + functions.get(0).getName() + " maps to:"
                        + System.lineSeparator()
                        + functions.stream().map(f -> f.getOpcodeVariant().toString()).collect(Collectors.joining(System.lineSeparator())));
            }

            Map<String, FunctionHandler> keywordMap = functions.stream()
                    .collect(Collectors.toMap(f -> ((SelectorFunction) f).getKeyword(), f -> f));

            String name = functions.get(0).getName();
            OpcodeVariant opcodeVariant = functions.get(0).getOpcodeVariant();
            return new MultiplexedFunctionHandler(keywordMap, name, opcodeVariant);
        }
    }

    private abstract class AbstractFunctionHandler implements FunctionHandler {
        protected final OpcodeVariant opcodeVariant;
        protected final String name;
        protected final int minArgs;
        protected final int numArgs;

        AbstractFunctionHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
            this(name, opcodeVariant, numArgs, numArgs);
        }

        AbstractFunctionHandler(String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs) {
            if (minArgs > numArgs) {
                throw new InvalidMetadataException("Minimum number of arguments greater than total.");
            }
            this.name = name;
            this.opcodeVariant = opcodeVariant;
            this.minArgs = minArgs;
            this.numArgs = numArgs;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public OpcodeVariant getOpcodeVariant() {
            return opcodeVariant;
        }

        protected void checkArguments(List<String> params) {
            if (params.size() < minArgs || params.size() > numArgs) {
                String args = (minArgs == numArgs) ? String.valueOf(numArgs) : minArgs + " to " + numArgs;
                throw new WrongNumberOfParametersException("Function '" + name + "': wrong number of parameters (expected "
                        + args + ", found " + params.size() + ")");
            }
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            return instructionProcessor.fromOpcodeVariant(getOpcodeVariant());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", numArgs=" + numArgs + '}';
        }
    }

    private class StandardFunctionHandler extends AbstractFunctionHandler implements SelectorFunction {
        private final Optional<String> keyword;
        private final boolean hasResult;

        StandardFunctionHandler(String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs, boolean hasResult) {
            super(name, opcodeVariant, minArgs, numArgs);
            this.keyword = opcodeVariant.getArguments().stream().filter(a -> a.getType().isSelector()).map(NamedArgument::getName).findFirst();
            this.hasResult = hasResult;
        }

        @Override
        public String getKeyword() {
            if (keyword.isEmpty()) {
                throw new InvalidMetadataException("No keyword selector for function " + getName());
            }
            return keyword.get();
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);

            String tmp = hasResult ? instructionProcessor.nextTemp() : "null";
            List<String> args = new ArrayList<>();
            int paramIndex = 0;

            for (NamedArgument a : opcodeVariant.getArguments()) {
                if (a.getType() == ArgumentType.RESULT) {
                    args.add(tmp);
                } else if (a.getType().isSelector()  && !a.getType().isFunctionName()) {
                    // Selector that IS NOT a function name is taken from the parameter list
                    args.add(stripLocalPrefix(params.get(paramIndex++)));
                } else if (a.getType().isSelector() || a.getType().isUnused()) {
                    // Selector that IS a function name isn't in a parameter list and must be filled in
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    args.add(a.getType().isOutput() ? instructionProcessor.nextTemp() : a.getName());
                } else if (a.getType().isInput()) {
                    // Input argument - take it as it is
                    args.add(params.get(paramIndex++));
                } else if (a.getType().isOutput()) {
                    if (paramIndex >= params.size()) {
                        // Optional arguments are always output; generate temporary variable for them
                        args.add(instructionProcessor.nextTemp());
                    } else {
                        // Block name cannot be used as output argument
                        String argument = params.get(paramIndex++);
                        if (instructionProcessor.isBlockName(argument)) {
                            throw new GenerationException("Using variable " + argument + " in function " + name
                                    + " not allowed (name reserved for linked blocks)");
                        }
                        args.add(argument);
                    }
                } else {
                    // Not a variable - strip prefix
                    args.add(stripLocalPrefix(params.get(paramIndex++)));
                }
            }

            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            StringBuilder str = new StringBuilder();
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument result = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.RESULT);
            if (result != null) {
                str.append(result.getName()).append(" = ");
            }

            List<String> strArguments = arguments.stream()
                    .filter(a -> !a.getType().isUnused() && !a.getType().isFunctionName())
                    .map(NamedArgument::getName)
                    .collect(Collectors.toList());

            // Mark optional arguments
            strArguments.subList(minArgs, numArgs).replaceAll(s -> s + "?");
            str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
            return str.toString();
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            AtomicInteger counter = new AtomicInteger();
            String tmpPrefix = instructionProcessor.getTempPrefix();
            List<String> arguments = getOpcodeVariant().getArguments().stream()
                    .map(a -> a.getType() == ArgumentType.UNUSED_OUTPUT ? tmpPrefix + counter.getAndIncrement() : a.getName())
                    .collect(Collectors.toList());
            return instructionProcessor.createInstructionUnchecked(getOpcode(), arguments);
        }
    }

    // Chooses a function handler based on the first parameter value
    private class MultiplexedFunctionHandler extends AbstractFunctionHandler {
        private final Map<String, FunctionHandler> functions;

        MultiplexedFunctionHandler(Map<String, FunctionHandler> functions, String name, OpcodeVariant opcodeVariant) {
            super(name, opcodeVariant, 0);
            this.functions = functions;
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            FunctionHandler handler = functions.get(params.get(0));
            if (handler == null) {
                throw new UnhandledFunctionVariantException("Unhandled type of " + getOpcode() + " in " + params);
            }
            return handler.handleFunction(pipeline, params);
        }

        @Override
        public void register(Consumer<SampleGenerator> registry) {
            functions.values().forEach(f -> f.register(registry));
        }

        @Override
        public String generateSampleCall() {
            throw new UnsupportedOperationException("Not supported for MultiplexedFunctionHandler");
        }
    }

    // Handles the print function
    private class PrintFunctionHandler extends AbstractFunctionHandler {
        PrintFunctionHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
            super(name, opcodeVariant, numArgs);
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            params.forEach((param) -> pipeline.emit(createInstruction(Opcode.PRINT, param)));
            return params.get(params.size() - 1);
        }

        @Override
        public String generateSampleCall() {
            return getName() + "(" + joinNamedArguments(getOpcodeVariant().getArguments()) + ")";
        }
    }
    
    private Map<String, BuiltInFunctionHandler> createBuiltInFunctionMap() {
        Map<String, BuiltInFunctionHandler> map = new HashMap<>();
        map.put("println", this::handlePrintlnFunction);
        return map;
    }
    
    private String handlePrintlnFunction(LogicInstructionPipeline pipeline, List<String> params) {
        params.forEach((param) -> pipeline.emit(createInstruction(Opcode.PRINT, param)));
        pipeline.emit(createInstruction(Opcode.PRINT, "\"\\n\""));
        return params.isEmpty() ? "null" : params.get(params.size() - 1);
    }
}
