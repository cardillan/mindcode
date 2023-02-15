package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
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
import java.util.stream.Collectors;

import static info.teksol.util.CollectionUtils.*;

public class BaseFunctionMapper implements FunctionMapper {
    private final InstructionProcessor instructionProcessor;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final Map<String, FunctionHandler> functionMap;

    public BaseFunctionMapper(InstructionProcessor InstructionProcessor) {
        this.instructionProcessor = InstructionProcessor;
        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        functionMap = buildFunctionMap();
    }

    @Override
    public String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params) {

        FunctionHandler handler = functionMap.get(functionName);
        return handler == null ? null : handler.handleFunction(pipeline, params);
    }

    private Map<String, FunctionHandler> buildFunctionMap() {
        Map<String, List<FunctionHandler>> functionGroups = instructionProcessor.getOpcodeVariants().stream()
                .filter(v -> v.getFunctionMapping() == OpcodeVariant.FunctionMapping.FUNC)
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
            case PRINT:     return new PrintFunctionHandler(opcode.toString(), opcode, 1);
            case STOP:      return new ZeroResultsFunctionHandler("stopProcessor", opcode, 0);
        }

        List<NamedArgument> arguments = opcodeVariant.getArguments();
        Optional<NamedArgument> selector = arguments.stream().filter(a -> a.getType().isFunctionName()).findFirst();
        String name = selector.isPresent() ? selector.get().getName() : opcode.toString();
        final int inputs  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isInput).count();
        final int outputs = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isOutput).count();
        final int unused  = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType::isUnused).count();
        final int results = (int) arguments.stream().map(NamedArgument::getType).filter(ArgumentType.RESULT::equals).count();

        if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Number of function parameters: selectors and results are not in parameter list
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0);

        if (unused == 0 && results == 0 && inputs == numArgs) {
            // All arguments are input
            return selector.isEmpty()
                    ? new ZeroResultsFunctionHandler(name, opcode, numArgs)
                    : new ZeroResultsSelectorFunctionHandler(name, opcode, selector.get().getName(), numArgs);
        } else if (unused == 0 && outputs == 1 && inputs == numArgs) {
            int resultIndex = findFirstIndex(arguments, a -> a.getType().isOutput());
            return selector.isEmpty()
                    ? new SingleResultFunctionHandler(name, opcode, numArgs, resultIndex)
                    : new SingleResultSelectorFunctionHandler(name, opcode, selector.get().getName(), numArgs, resultIndex);
        } else {
            if (results > 1) {
                throw new InvalidMetadataException("Too many RESULT arguments in opcode " + opcodeVariant);
            }

            int optional = 0;
            for (int i = arguments.size() - 1; i >= 0; i--) {
                ArgumentType type = arguments.get(i).getType();
                if (!type.isOutput() || !type.isUnused()) {
                    break;
                }
                if (type.isOutput() && type != ArgumentType.RESULT) {
                    optional++;
                }
            }

            // Unused arguments and result aren't passed as parameters to the function
            numArgs -= unused;
            int minArgs = numArgs - optional;
            return new ComplexFunctionHandler(name, opcode, minArgs, numArgs, results > 0, arguments);
        }
    }

    private FunctionHandler collapseFunctions(List<FunctionHandler> functions) {
        if (functions.size() == 1) {
            return functions.get(0);
        } else {
            if (functions.stream().anyMatch(fn -> !(fn instanceof SelectorFunction))) {
                throw new InvalidMetadataException("Multiplexed function " + functions.get(0).getName() + " has a non-selector based variant.");
            }
            
            Map<String, FunctionHandler> keywordMap = functions.stream()
                    .collect(Collectors.toMap(f -> ((SelectorFunction) f).getKeyword(), f -> f));
            
            String name = functions.get(0).getName();
            Opcode opcode = functions.get(0).opcode;
            return new MultiplexedFunctionHandler(keywordMap, name, opcode);
        }
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private interface SelectorFunction {
        String getKeyword();
    }

    private abstract class FunctionHandler {
        private final int minArgs;
        private final int numArgs;
        private final String name;
        private final Opcode opcode;

        public FunctionHandler(String name, Opcode opcode, int numArgs) {
            this(name, opcode, numArgs, numArgs);
        }

        public FunctionHandler(String name, Opcode opcode, int minArgs, int numArgs) {
            if (minArgs > numArgs) {
                throw new InvalidMetadataException("Minimum number of arguments greater than total.");
            }
            this.name = name;
            this.opcode = opcode;
            this.minArgs = minArgs;
            this.numArgs = numArgs;
        }

        public String getName() {
            return name;
        }

        public Opcode getOpcode() {
            return opcode;
        }

        protected void checkArguments(List<String> params) {
            if (params.size() < minArgs || params.size() > numArgs) {
                String args = (minArgs == numArgs) ? String.valueOf(numArgs) : minArgs + " to " + numArgs;
                throw new WrongNumberOfParametersException("Function '" + name + "': wrong number of parameters (expected "
                        + args + ", found " + params.size() + ")");
            }
        }

        protected abstract String handleFunction(LogicInstructionPipeline pipeline, List<String> params);
    }

    // No return value: all parameters passed in in given order
    private class ZeroResultsFunctionHandler extends FunctionHandler {
        public ZeroResultsFunctionHandler(String name, Opcode opcode, int numArgs) {
            super(name, opcode, numArgs);
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), params));
            return "null";
        }
    }

    // No return value: all parameters passed in in given order
    private class ZeroResultsSelectorFunctionHandler extends FunctionHandler implements SelectorFunction {
        private final String keyword;

        public ZeroResultsSelectorFunctionHandler(String name, Opcode opcode, String keyword, int numArgs) {
            super(name, opcode, numArgs);
            this.keyword = keyword;
        }

        @Override
        public String getKeyword() {
            return keyword;
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            List<String> args = new ArrayList<>(params);
            args.add(0, keyword);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return "null";
        }
    }

    // Single return value: all other parameters passed in in given order, result inserted into arg list at proper position
    private class SingleResultFunctionHandler extends FunctionHandler {
        private final int resultPosition;

        private SingleResultFunctionHandler(String name, Opcode opcode, int numArgs, int resultPosition) {
            super(name, opcode, numArgs);
            this.resultPosition = resultPosition;
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);
            String tmp = instructionProcessor.nextTemp();
            List<String> args = new ArrayList<>(params);
            args.add(resultPosition, tmp);
            pipeline.emit(instructionProcessor.createInstruction(getOpcode(), args));
            return tmp;
        }
    }

    // Single return value: all other parameters passed in in given order, result inserted into arg list at proper position
    private class SingleResultSelectorFunctionHandler extends FunctionHandler implements SelectorFunction {
        private final String keyword;
        private final int resultPosition;

        private SingleResultSelectorFunctionHandler(String name, Opcode opcode, String keyword, int numArgs, int resultPosition) {
            super(name, opcode, numArgs);
            this.keyword = keyword;
            this.resultPosition = resultPosition;
        }

        @Override
        public String getKeyword() {
            return keyword;
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
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
    private class ComplexFunctionHandler extends FunctionHandler implements SelectorFunction {
        private final Optional<String> keyword;
        private final boolean hasResult;
        private final List<NamedArgument> instructionArgs;

        public ComplexFunctionHandler(String name, Opcode opcode, int minArgs, int numArgs, boolean hasResult, List<NamedArgument> instructionArgs) {
            super(name, opcode, minArgs, numArgs);
            this.keyword = instructionArgs.stream().filter(a -> a.getType().isSelector()).map(NamedArgument::getName).findFirst();
            this.hasResult = hasResult;
            this.instructionArgs = instructionArgs;
        }

        @Override
        public String getKeyword() {
            if (keyword.isEmpty()) {
                throw new InvalidMetadataException("No keyword selector for function " + getName());
            }
            return keyword.get();
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            checkArguments(params);

            String tmp = hasResult ? instructionProcessor.nextTemp() : "null";
            List<String> args = new ArrayList<>();
            int paramIndex = 0;

            for (NamedArgument a : instructionArgs) {
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
    private class MultiplexedFunctionHandler extends FunctionHandler {
        private final Map<String, FunctionHandler> functions;

        public MultiplexedFunctionHandler(Map<String, FunctionHandler> functions, String name, Opcode opcode) {
            super(name, opcode, 0);
            this.functions = functions;
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            FunctionHandler handler = functions.get(params.get(0));
            if (handler == null) {
                throw new UnhandledFunctionVariantException("Unhandled type of " + getOpcode() + " in " + params);
            }
            return handler.handleFunction(pipeline, params);
        }
    }

    // Handles the print function
    private class PrintFunctionHandler extends FunctionHandler {
        public PrintFunctionHandler(String name, Opcode opcode, int numArgs) {
            super(name, opcode, numArgs);
        }

        @Override
        protected String handleFunction(LogicInstructionPipeline pipeline, List<String> params) {
            params.forEach((param) -> pipeline.emit(createInstruction(Opcode.PRINT, param)));
            return params.get(params.size() - 1);
        }
    }
}
