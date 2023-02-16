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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.util.CollectionUtils.*;

public class BaseFunctionMapper implements FunctionMapper {
    private final InstructionProcessor instructionProcessor;
    private final Consumer<String> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;

    BaseFunctionMapper(InstructionProcessor InstructionProcessor, Consumer<String> messageConsumer) {
        this.instructionProcessor = InstructionProcessor;
        this.messageConsumer = messageConsumer;

        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        propertyMap = buildPropertyMap();
        functionMap = buildFunctionMap();
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

    private Map<String, PropertyHandler> buildPropertyMap() {
        Map<String, PropertyHandler> map = instructionProcessor.getOpcodeVariants().stream()
                .filter(v -> v.getFunctionMapping() == OpcodeVariant.FunctionMapping.PROP)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createPropertyHandler)
                .collect(Collectors.toMap(BasicPropertyHandler::getName, f -> f));

        // V6 backwards compatibility
        if (map.containsKey("config")) {
            map.put("configure", new DeprecatedPropertyHandler("configure", map.get("config")));
        }

        return map;
    }

    private Map<String, FunctionHandler> buildFunctionMap() {
        Map<String, List<AbstractFunctionHandler>> functionGroups = instructionProcessor.getOpcodeVariants().stream()
                .filter(v -> v.getFunctionMapping() == OpcodeVariant.FunctionMapping.FUNC)
                .filter(v -> v.isAvailableIn(processorVersion, processorEdition))
                .map(this::createFunctionHandler)
                .collect(Collectors.groupingBy(AbstractFunctionHandler::getName));

        // Create MultiplexedFunctionHandler for functions with identical names
        return functionGroups.values().stream()
                .map(this::collapseFunctions)
                .collect(Collectors.toMap(AbstractFunctionHandler::getName, f -> f));
    }

    private BasicPropertyHandler createPropertyHandler(OpcodeVariant opcodeVariant) {
        List<NamedArgument> arguments = opcodeVariant.getArguments();
        int selectorIndex = findFirstIndex(arguments, a -> a.getType().isSelector());
        int blockIndex = findFirstIndex(arguments, a -> a.getType() == ArgumentType.BLOCK);
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();

        // As of now, we only support instructions with selector and block at the front
        if (selectorIndex != 0 || blockIndex != 1 || outputs != 0 || unused != 0) {
            throw new InvalidMetadataException("Unsupported property configuration in " + opcodeVariant);
        }

        // Number of function parameters: subtract two for selector and block (aka property and target)
        int numArgs = arguments.size() - 2;

        return new BasicPropertyHandler(arguments.get(0).getName(), opcodeVariant, numArgs);
    }

    private AbstractFunctionHandler createFunctionHandler(OpcodeVariant opcodeVariant) {
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
            // Result doesn't count, as it is not in the parameter list.
            int optional = 0;
            for (int i = arguments.size() - 1; i >= 0; i--) {
                ArgumentType type = arguments.get(i).getType();
                if (!type.isOutput() && !type.isUnused()) {
                    break;
                }
                if (type.isOutput() && type != ArgumentType.RESULT) {
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

    private AbstractFunctionHandler collapseFunctions(List<AbstractFunctionHandler> functions) {
        if (functions.size() == 1) {
            return functions.get(0);
        } else {
            if (functions.stream().anyMatch(fn -> !(fn instanceof SelectorFunction))) {
                throw new InvalidMetadataException("Function name collision; " + functions.get(0).getName() + " maps to:\n"
                        + functions.stream().map(f -> f.opcodeVariant.toString()).collect(Collectors.joining("\n")));
            }
            
            Map<String, AbstractFunctionHandler> keywordMap = functions.stream()
                    .collect(Collectors.toMap(f -> ((SelectorFunction) f).getKeyword(), f -> f));
            
            String name = functions.get(0).getName();
            OpcodeVariant opcodeVariant = functions.get(0).opcodeVariant;
            return new MultiplexedFunctionHandler(keywordMap, name, opcodeVariant);
        }
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private interface PropertyHandler {
        String getName();
        OpcodeVariant getOpcodeVariant();
        String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params);

        default Opcode getOpcode() {
            return getOpcodeVariant().getOpcode();
        }
    }

    private class BasicPropertyHandler implements PropertyHandler {
        protected final OpcodeVariant opcodeVariant;
        private final String name;
        private final int numArgs;

        BasicPropertyHandler(String name, OpcodeVariant opcodeVariant, int numArgs) {
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
        public String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params) {
            checkArguments(params);
            List<String> args = new ArrayList<>(params);
            args.add(0, name);
            args.add(1, target);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return "null";
        }


        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", numArgs=" + numArgs + '}';
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
        public String handleProperty(LogicInstructionPipeline pipeline, String target, List<String> params) {
            if (!warningEmitted) {
                messageConsumer.accept("Function '" + deprecated + "' is no longer supported in Mindustry Logic version " +
                        processorVersion + "; using '" + replacement.getName() + "' instead.");
                warningEmitted = true;
            }
            return replacement.handleProperty(pipeline, target, params);
        }
    }

    private interface FunctionHandler {
        String getName();
        OpcodeVariant getOpcodeVariant();
        String handleFunction(LogicInstructionPipeline pipeline, List<String> params);

        default Opcode getOpcode() {
            return getOpcodeVariant().getOpcode();
        }
    }

    private interface SelectorFunction extends FunctionHandler {
        String getKeyword();
    }

    private abstract class AbstractFunctionHandler implements FunctionHandler {
        protected final OpcodeVariant opcodeVariant;
        private final String name;
        private final int minArgs;
        private final int numArgs;

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
    }

    // No return value: all parameters passed in in given order
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
    }

    // Single return value: all other parameters passed in in given order, result inserted into arg list at proper position
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
    }

    // Chooses a function handler based on the first parameter value
    private class MultiplexedFunctionHandler extends AbstractFunctionHandler {
        private final Map<String, AbstractFunctionHandler> functions;

        MultiplexedFunctionHandler(Map<String, AbstractFunctionHandler> functions, String name, OpcodeVariant opcodeVariant) {
            super(name, opcodeVariant, 0);
            this.functions = functions;
        }

        @Override
        public String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            AbstractFunctionHandler handler = functions.get(params.get(0));
            if (handler == null) {
                throw new UnhandledFunctionVariantException("Unhandled type of " + getOpcode() + " in " + params);
            }
            return handler.handleFunction(pipeline, params);
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
    }
}
