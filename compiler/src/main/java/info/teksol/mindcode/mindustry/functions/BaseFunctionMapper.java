package info.teksol.mindcode.mindustry.functions;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static info.teksol.util.CollectionUtils.findFirstIndex;


public class BaseFunctionMapper implements FunctionMapper {
    private final InstructionProcessor instructionProcessor;
    private final Consumer<String> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;
    private final List<SampleGenerator> sampleGenerators;

    BaseFunctionMapper(InstructionProcessor InstructionProcessor, Consumer<String> messageConsumer) {
        this.instructionProcessor = InstructionProcessor;
        this.messageConsumer = messageConsumer;

        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        propertyMap = buildPropertyMap();
        functionMap = buildFunctionMap();
        
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
        return handler == null ? null : handler.handleFunction(pipeline, params);
    }

    @Override
    public List<FunctionSample> generateSamples() {
        return sampleGenerators.stream().map(
                s -> new FunctionSample(
                        instructionProcessor.getOpcodeVariants().indexOf(s.getOpcodeVariant()),
                        s.getName(),
                        s.generateSampleCall(),
                        s.generateSampleInstruction(),
                        s.getOpcodeVariant().getEdition(),
                        s.getNote()
                )
        ).collect(Collectors.toList());
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private interface SampleGenerator {
        String getName();
        OpcodeVariant getOpcodeVariant();
        String generateSampleCall();
        LogicInstruction generateSampleInstruction();

        default String getNote() {
            return "";
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

    //
    // Property handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, PropertyHandler> buildPropertyMap() {
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
        int selectorIndex = findFirstIndex(arguments, a -> a.getType().isSelector());
        int blockIndex = findFirstIndex(arguments, a -> a.getType() == ArgumentType.BLOCK);
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int results = (int) arguments.stream().map(NamedArgument::getType).filter(a -> a == ArgumentType.RESULT).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();

        // As of now, we only support instructions with selector and block at the front
        if (selectorIndex == 0 && blockIndex == 1 && outputs == 0 && unused == 0) {
            // Number of function parameters: subtract two for selector and block (aka property and target)
            int numArgs = arguments.size() - 2;
            return new SimplePropertyHandler(arguments.get(0).getName(), opcodeVariant, numArgs);
        } else {
            Optional<NamedArgument> selector = arguments.stream().filter(a -> a.getType().isFunctionName()).findFirst();
            String name = selector.isPresent() ? selector.get().getName() : opcodeVariant.getOpcode().toString();
            return new ComplexPropertyHandler(name, opcodeVariant, outputs, results > 0);
        }
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

    private class SimplePropertyHandler extends AbstractPropertyHandler {
        SimplePropertyHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
            super(name, opcodeVariant, numArgs);
        }

        @Override
        public String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params) {
            checkArguments(params);
            List<String> args = new ArrayList<>(params);
            args.add(0, name);
            args.add(1, target);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return "null";
        }

        @Override
        public String generateSampleCall() {
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument blockArgument = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.BLOCK);
            CollectionUtils.removeFirstMatching(arguments, a -> a.getType().isSelector());
            return blockArgument.getName() + "." + getName() + "(" + joinNamedArguments(arguments) + ")";
        }
    }

    // More than one return value, or unused/const parameters: parameter mapping based on NamedArgument list
    private class ComplexPropertyHandler extends AbstractPropertyHandler  {
        private final boolean hasResult;

        ComplexPropertyHandler(String name, OpcodeVariant opcodeVariant, int numArgs, boolean hasResult) {
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
                    args.add(target);
                } else if (a.getType().isSelector()  && !a.getType().isFunctionName()) {
                    // Selector that IS NOT a function name is taken from the parameter list
                    args.add(params.get(paramIndex++));
                } else if (a.getType().isSelector() || a.getType().isUnused()) {
                    // Selector that IS a function name isn't in a parameter list and must be filled in
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    args.add(a.getType().isOutput() ? instructionProcessor.nextTemp() : a.getName());
                } else {
                    // Optional arguments are always output; generate temporary variable for them
                    args.add(params.get(paramIndex++));
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
                messageConsumer.accept("Function '" + deprecated + "' is no longer supported in Mindustry Logic version " +
                        processorVersion + "; using '" + replacement.getName() + "' instead.");
                warningEmitted = true;
            }
            return replacement.handleProperty(pipeline, target, params);
        }
    }

    //
    // Function handlers
    //
    ///////////////////////////////////////////////////////////////

    private Map<String, FunctionHandler> buildFunctionMap() {
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
        final int inputs  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isInput).count();
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();
        final int results = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType.RESULT::equals).count();
        final int selectorIndex = findFirstIndex(arguments, a -> a.getType().isFunctionName());

        if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Number of function parameters: selectors and results are not in parameter list
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0);

        if (unused == 0 && selectorIndex <= 0 && results == 0 && inputs == numArgs) {
            // Selector, if preset, is the first argument; all other arguments are input
            return selector.isEmpty()
                    ? new ZeroResultsFunctionHandler(name, opcodeVariant, numArgs)
                    : new ZeroResultsSelectorFunctionHandler(name, opcodeVariant, selector.get().getName(), numArgs);
        } else if (unused == 0 && selectorIndex <= 0 && outputs == 1 && inputs == numArgs) {
            // Selector, if preset, is the first argument. Then exactly one output argument - it must be of type RESULT (checked above)

            int resultIndex = findFirstIndex(arguments, a -> a.getType().isOutput());
            return selector.isEmpty()
                    ? new SingleResultFunctionHandler(name, opcodeVariant, numArgs, resultIndex)
                    : new SingleResultSelectorFunctionHandler(name, opcodeVariant, selector.get().getName(), numArgs, resultIndex);
        } else {
            if (results > 1) {
                throw new InvalidMetadataException("more than one RESULT arguments in opcode " + opcodeVariant);
            }

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

            numArgs -= unused;
            int minArgs = numArgs - optional;
            return new ComplexFunctionHandler(name, opcodeVariant, minArgs, numArgs, results > 0);
        }
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
                throw new InvalidMetadataException("Function name collision; " + functions.get(0).getName() + " maps to:\n"
                        + functions.stream().map(f -> f.getOpcodeVariant().toString()).collect(Collectors.joining("\n")));
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
        private final String name;
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

    // No return value: all parameters passed in in given order
    private class ZeroResultsFunctionHandler extends AbstractFunctionHandler {
        public ZeroResultsFunctionHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
            super(name, opcodeVariant, numArgs);
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), params));
            return "null";
        }

        @Override
        public String generateSampleCall() {
            return getName() + "(" + joinNamedArguments(getOpcodeVariant().getArguments()) + ")";
        }
    }

    // No return value: selector plus all parameters passed in in given order
    private class ZeroResultsSelectorFunctionHandler extends AbstractFunctionHandler implements SelectorFunction {
        private final String keyword;

        ZeroResultsSelectorFunctionHandler(String name, OpcodeVariant opcodeVariant, String keyword, int numArgs) {
            super(name, opcodeVariant, numArgs);
            this.keyword = keyword;
        }

        @Override
        public String getKeyword() {
            return keyword;
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            List<String> args = new ArrayList<>(params);
            args.add(0, keyword);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return "null";
        }

        @Override
        public String generateSampleCall() {
            return getName() + "(" + joinNamedArguments(getOpcodeVariant().getArguments(), a -> !a.getType().isSelector()) + ")";
        }
    }

    // Single return value: all other parameters passed in in given order, result inserted into arg list at proper position
    private class SingleResultFunctionHandler extends AbstractFunctionHandler {
        private final int resultPosition;

        SingleResultFunctionHandler(String name, OpcodeVariant opcodeVariant, int numArgs, int resultPosition) {
            super(name, opcodeVariant, numArgs);
            this.resultPosition = resultPosition;
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            String tmp = instructionProcessor.nextTemp();
            List<String> args = new ArrayList<>(params);
            args.add(resultPosition, tmp);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument result = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.RESULT);
            return result.getName() + " = " + getName() + "(" + joinNamedArguments(arguments) + ")";
        }
    }

    // Single return value: selector plus all other parameters passed in in given order, result inserted into arg list at proper position
    private class SingleResultSelectorFunctionHandler extends AbstractFunctionHandler implements SelectorFunction {
        private final String keyword;
        private final int resultPosition;

        SingleResultSelectorFunctionHandler(String name, OpcodeVariant opcodeVariant, String keyword, int numArgs, int resultPosition) {
            super(name, opcodeVariant, numArgs);
            this.keyword = keyword;
            this.resultPosition = resultPosition;
        }

        @Override
        public String getKeyword() {
            return keyword;
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            String tmp = instructionProcessor.nextTemp();
            List<String> args = new ArrayList<>(params);
            args.add(0, keyword);
            args.add(resultPosition, tmp);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            List<NamedArgument> arguments = new ArrayList<>(getOpcodeVariant().getArguments());
            NamedArgument result = CollectionUtils.removeFirstMatching(arguments, a -> a.getType() == ArgumentType.RESULT);
            CollectionUtils.removeFirstMatching(arguments, a -> a.getType().isSelector());
            return result.getName() + " = " + getName() + "(" + joinNamedArguments(arguments) + ")";
        }
    }

    // More than one return value, or unused/const parameters: parameter mapping based on NamedArgument list
    private class ComplexFunctionHandler extends AbstractFunctionHandler implements SelectorFunction {
        private final Optional<String> keyword;
        private final boolean hasResult;

        ComplexFunctionHandler(String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs, boolean hasResult) {
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
                    args.add(params.get(paramIndex++));
                } else if (a.getType().isSelector() || a.getType().isUnused()) {
                    // Selector that IS a function name isn't in a parameter list and must be filled in
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    args.add(a.getType().isOutput() ? instructionProcessor.nextTemp() : a.getName());
                } else {
                    // Optional arguments are always output; generate temporary variable for them
                    args.add(paramIndex < params.size() ? params.get(paramIndex++) : instructionProcessor.nextTemp());
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

    private static String joinNamedArguments(List<NamedArgument> arguments) {
        return arguments.stream().map(NamedArgument::getName).collect(Collectors.joining(", "));
    }

    private static String joinNamedArguments(List<NamedArgument> arguments, Predicate<NamedArgument> filter) {
        return arguments.stream().filter(filter).map(NamedArgument::getName).collect(Collectors.joining(", "));
    }
}
