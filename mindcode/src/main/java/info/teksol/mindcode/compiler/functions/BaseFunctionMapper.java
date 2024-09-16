package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseFunctionMapper implements FunctionMapper {
    private final AstContext staticAstContext = AstContext.createStaticRootNode();
    private final Supplier<AstContext> astContextSupplier;
    private final InstructionProcessor instructionProcessor;
    private final Consumer<CompilerMessage> messageConsumer;
    private final ProcessorVersion processorVersion;
    private final ProcessorEdition processorEdition;
    private final Map<String, PropertyHandler> propertyMap;
    private final Map<String, FunctionHandler> functionMap;
    private final List<SampleGenerator> sampleGenerators;
    private final Map<String, BuiltInFunctionHandler> builtInFunctionMap;

    BaseFunctionMapper(InstructionProcessor InstructionProcessor, Supplier<AstContext> astContextSupplier,
            Consumer<CompilerMessage> messageConsumer) {
        this.astContextSupplier = astContextSupplier;
        this.instructionProcessor = InstructionProcessor;
        this.messageConsumer = messageConsumer;

        processorVersion = instructionProcessor.getProcessorVersion();
        processorEdition = instructionProcessor.getProcessorEdition();
        propertyMap = createPropertyMap();
        functionMap = createFunctionMap();
        builtInFunctionMap = createBuiltInFunctionMap();
        
        sampleGenerators = new ArrayList<>();
        propertyMap.values().forEach(p -> p.register(sampleGenerators::add));
        functionMap.values().forEach(f -> f.register(sampleGenerators::add));
        sampleGenerators.sort(Comparator.comparing(SampleGenerator::getName));
    }

    @Override
    public LogicValue handleProperty(Consumer<LogicInstruction> program, String propertyName, LogicValue target,
            List<LogicValue> arguments) {
        PropertyHandler handler = propertyMap.get(propertyName);
        return handler == null ? null : handler.handleProperty(program, target, arguments);
    }

    @Override
    public LogicValue handleFunction(Consumer<LogicInstruction> program, String functionName, List<LogicValue> arguments) {
        FunctionHandler handler = functionMap.get(functionName);
        BuiltInFunctionHandler builtInHandler = builtInFunctionMap.get(functionName);
        return handler == null
                ? builtInHandler == null ? null : builtInHandler.handleFunction(program, arguments)
                : handler.handleFunction(program, arguments);
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

    private LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return instructionProcessor.createInstruction(astContextSupplier.get(), opcode, args);
    }

    private LogicKeyword toKeyword(LogicValue arg) {
        // Syntactically, instruction keywords are just identifiers.
        // To convert it to keyword, we use the plain variable name.
        if (arg instanceof LogicVariable lv) {
            return LogicKeyword.create(lv.getName());
        } else {
            throw new MindcodeInternalError("Unexpected type of argument " + arg);
        }
    }

    private LogicKeyword toKeywordOptional(LogicValue arg) {
        if (arg instanceof LogicVariable lv) {
            return LogicKeyword.create(lv.getName());
        } else {
            return LogicKeyword.create("");        // A keyword that cannot exist
        }
    }

    private static String joinNamedArguments(List<NamedParameter> arguments) {
        return arguments.stream().map(NamedParameter::name).collect(Collectors.joining(", "));
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
        LogicValue handleProperty(Consumer<LogicInstruction> program, LogicValue target, List<LogicValue> arguments);

        default Opcode getOpcode() {
            return getOpcodeVariant().opcode();
        }
    }

    private interface FunctionHandler extends SampleGenerator {
        LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments);

        default Opcode getOpcode() {
            return getOpcodeVariant().opcode();
        }
    }

    private interface SelectorFunction extends FunctionHandler {
        String getKeyword();
    }

    private interface BuiltInFunctionHandler {
        LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments);
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
            map.put("configure", new DeprecatedPropertyHandler("configure", map.get("config")));
        }

        return map;
    }

    private PropertyHandler createPropertyHandler(OpcodeVariant opcodeVariant) {
        List<NamedParameter> arguments = opcodeVariant.namedParameters();
        Optional<NamedParameter> selector = arguments.stream().filter(a -> a.type().isFunctionName()).findFirst();
        final int outputs = (int) arguments.stream().map(NamedParameter::type).filter(LogicParameter::isOutput).count();
        final int results = (int) arguments.stream().map(NamedParameter::type).filter(a -> a == LogicParameter.RESULT).count();
        final int unused  = (int) arguments.stream().map(NamedParameter::type).filter(LogicParameter::isUnused).count();

        if (results > 1) {
            throw new InvalidMetadataException("More than one RESULT arguments in opcode " + opcodeVariant);
        } else if (outputs == 1 && results == 0) {
            // If there is exactly one output, it must be marked as a result
            throw new InvalidMetadataException("Output argument not marked as RESULT in opcode " + opcodeVariant);
        }

        // Subtract one more for target
        int numArgs = arguments.size() - results - (selector.isPresent() ? 1 : 0) - unused - 1;
        String name = selector.isPresent() ? selector.get().name() : opcodeVariant.opcode().toString();
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

        protected void checkArguments(List<LogicValue> arguments) {
            if (arguments.size() != numArgs) {
                throw new MindcodeException("Function '" + name + "': wrong number of arguments (expected "
                        + numArgs + ", found " + arguments.size() + ")");
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
        public LogicValue handleProperty(Consumer<LogicInstruction> program, LogicValue target, List<LogicValue> fnArgs) {
            checkArguments(fnArgs);

            LogicValue tmp = hasResult ? instructionProcessor.nextTemp() : LogicNull.NULL;
            List<LogicArgument> ixArgs = new ArrayList<>();
            int argIndex = 0;

            for (NamedParameter a : opcodeVariant.namedParameters()) {
                if (a.type().isGlobal() && !fnArgs.get(argIndex).isGlobalVariable()) {
                    throw new MindcodeException("Using argument '" + fnArgs.get(argIndex).toMlog() + "' in a call to '" + name
                            + "' not allowed (a global variable is required)");
                }

                if (a.type() == LogicParameter.RESULT) {
                    ixArgs.add(tmp);
                } else if (a.type() == LogicParameter.BLOCK) {
                    ixArgs.add(target);
                } else if (a.type().isSelector()  && !a.type().isFunctionName()) {
                    // Selector that IS NOT a function name is taken from the argument list
                    ixArgs.add(toKeyword(fnArgs.get(argIndex++)));
                } else if (a.type().isSelector()) {
                    // Selector that IS a function name isn't in an argument list and must be filled in
                    ixArgs.add(LogicKeyword.create(a.name()));
                } else if (a.type().isUnused()) {
                    // Unused inputs must be filled with defaults
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    ixArgs.add(a.type().isOutput() ? instructionProcessor.nextTemp() : LogicKeyword.create(a.name()));
                } else if (a.type().isInput()) {
                    // Input argument - take it as it is
                    ixArgs.add(fnArgs.get(argIndex++));
                } else if (a.type().isOutput()) {
                    if (argIndex >= fnArgs.size()) {
                        // Optional arguments are always output; generate temporary variable for them
                        ixArgs.add(instructionProcessor.nextTemp());
                    } else {
                        // Block name cannot be used as output argument
                        LogicArgument argument = fnArgs.get(argIndex++);
                        if (argument.getType() == ArgumentType.BLOCK) {
                            throw new MindcodeException("Using argument '" + argument.toMlog() + "' in a call to '" + name
                                    + "' not allowed (name reserved for linked blocks)");
                        }
                        ixArgs.add(argument);
                    }
                } else {
                    ixArgs.add(toKeyword(fnArgs.get(argIndex++)));
                }
            }

            program.accept(instructionProcessor.createInstruction(astContextSupplier.get(), getOpcode(), ixArgs));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            StringBuilder str = new StringBuilder();
            List<NamedParameter> arguments = new ArrayList<>(getOpcodeVariant().namedParameters());
            NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == LogicParameter.RESULT);
            if (result != null) {
                str.append(result.name()).append(" = ");
            }

            NamedParameter block = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == LogicParameter.BLOCK);
            str.append(block.name()).append('.');

            List<String> strArguments = arguments.stream()
                    .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                    .map(NamedParameter::name)
                    .collect(Collectors.toList());

            str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
            return str.toString();
        }

        @Override
        public String generateSecondarySampleCall() {
            List<NamedParameter> args = new ArrayList<>(getOpcodeVariant().namedParameters());
            NamedParameter blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.type() == LogicParameter.BLOCK);
            CollectionUtils.removeFirstMatching(args, a -> a.type().isSelector());
            if (args.size() == 1 && args.get(0).type() == LogicParameter.INPUT) {
                return blockArgument.name() + "." + getName() + " = " + args.get(0).name();
            } else {
                return null;
            }
        }

        @Override
        public LogicInstruction generateSampleInstruction() {
            AtomicInteger counter = new AtomicInteger();
            String tmpPrefix = instructionProcessor.getTempPrefix();
            List<LogicArgument> arguments = getOpcodeVariant().namedParameters().stream()
                    .map(a -> a.type() == LogicParameter.UNUSED_OUTPUT ? tmpPrefix + counter.getAndIncrement() : a.name())
                    .map(BaseArgument::new)
                    .collect(Collectors.toList());
            return instructionProcessor.createInstructionUnchecked(staticAstContext, getOpcode(), arguments);
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
            List<NamedParameter> arguments = new ArrayList<>(getOpcodeVariant().namedParameters());
            NamedParameter blockArgument = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == LogicParameter.BLOCK);
            CollectionUtils.removeFirstMatching(arguments, a -> a.type().isSelector());
            return blockArgument.name() + "." + deprecated + "(" + joinNamedArguments(arguments) + ")";
        }

        @Override
        public String generateSecondarySampleCall() {
            List<NamedParameter> args = new ArrayList<>(getOpcodeVariant().namedParameters());
            NamedParameter blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.type() == LogicParameter.BLOCK);
            CollectionUtils.removeFirstMatching(args, a -> a.type().isSelector());
            if (args.size() == 1 && args.get(0).type() == LogicParameter.INPUT) {
                return blockArgument.name() + "." + deprecated + " = " + args.get(0).name();
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
        public LogicValue handleProperty(Consumer<LogicInstruction> program, LogicValue target, List<LogicValue> arguments) {
            if (!warningEmitted) {
                messageConsumer.accept(MindcodeMessage.warn(
                        "Function '" + deprecated + "' is no longer supported in Mindustry Logic version " +
                        processorVersion + "; using '" + replacement.getName() + "' instead."));
                warningEmitted = true;
            }
            return replacement.handleProperty(program, target, arguments);
        }
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
        switch(opcode) {
            case PRINT:     return new PrintFunctionHandler(opcode.toString(), opcodeVariant);
            case UBIND:     return new UbindFunctionHandler(opcode.toString(), opcodeVariant);
        }

        List<NamedParameter> arguments = opcodeVariant.namedParameters();
        Optional<NamedParameter> selector = arguments.stream().filter(a -> a.type().isFunctionName()).findFirst();
        String name = functionName(opcodeVariant, selector.orElse(null));
        final int outputs = (int) arguments.stream().map(NamedParameter::type).filter(LogicParameter::isOutput).count();
        final int results = (int) arguments.stream().map(NamedParameter::type).filter(LogicParameter.RESULT::equals).count();
        final int unused  = (int) arguments.stream().map(NamedParameter::type).filter(LogicParameter::isUnused).count();

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
            LogicParameter type = arguments.get(i).type();
            if (!type.isOutput() && !type.isUnused()) {
                break;
            }
            // Result and unused do not count, as they're not in the parameter list.
            if (type.isOutput() && type != LogicParameter.RESULT && !type.isUnused()) {
                optional++;
            }
        }

        int minArgs = numArgs - optional;
        return new StandardFunctionHandler(name, opcodeVariant, minArgs, numArgs, results > 0);
    }

    private String functionName(OpcodeVariant opcodeVariant, NamedParameter selector) {
        return switch (opcodeVariant.opcode()) {
            case STOP   -> "stopProcessor";
            case STATUS -> switch (opcodeVariant.namedParameters().get(0).name()) {
                    case "true"  -> "clearStatus";
                    case "false" -> "applyStatus";
                    default      -> throw new MindcodeInternalError("Opcode variant " + opcodeVariant + " not mapped to a function.");
                };
            default     -> selector == null ? opcodeVariant.opcode().toString() : selector.name();
        };
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

        protected void checkArguments(List<LogicValue> arguments) {
            if (arguments.size() < minArgs || arguments.size() > numArgs) {
                String args = (minArgs == numArgs) ? String.valueOf(numArgs) : minArgs + " to " + numArgs;
                throw new MindcodeException("Function '" + name + "': wrong number of arguments (expected "
                        + args + ", found " + arguments.size() + ")");
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
        private final String keyword;
        private final boolean hasResult;

        StandardFunctionHandler(String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs, boolean hasResult) {
            super(name, opcodeVariant, minArgs, numArgs);
            this.keyword = opcodeVariant.namedParameters().stream().filter(a -> a.type().isSelector())
                    .map(NamedParameter::name).findFirst().orElse(null);
            this.hasResult = hasResult;
        }

        @Override
        public String getKeyword() {
            if (keyword == null) {
                throw new InvalidMetadataException("No keyword selector for function " + getName());
            }
            return keyword;
        }

        @Override
        public LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> fnArgs) {
            checkArguments(fnArgs);

            LogicValue tmp = hasResult ? instructionProcessor.nextTemp() : LogicNull.NULL;
            // Need to support all kinds of arguments here, including keywords
            List<LogicArgument> ixArgs = new ArrayList<>();
            int argIndex = 0;

            for (NamedParameter a : opcodeVariant.namedParameters()) {
                if (a.type().isGlobal() && !fnArgs.get(argIndex).isGlobalVariable()) {
                    throw new MindcodeException("Using argument '" + fnArgs.get(argIndex).toMlog() + "' in a call to '" + name
                            + "' not allowed (a global variable is required)");
                }

                if (a.type() == LogicParameter.RESULT) {
                    ixArgs.add(tmp);
                } else if (a.type().isSelector() && !a.type().isFunctionName()) {
                    // Selector that IS NOT a function name is taken from the argument list
                    ixArgs.add(toKeyword(fnArgs.get(argIndex++)));
                } else if (a.type().isSelector()) {
                    // Selector that IS a function name isn't in an argument list and must be filled in
                    // Perhaps we might store the Operation into the NamedParameter directly to avoid lookup
                    ixArgs.add(getOpcode() == Opcode.OP ? Operation.fromMlog(a.name()) : LogicKeyword.create(a.name()));
                } else if (a.type().isUnused()) {
                    // Pass in zero for unused inputs
                    // Generate new temporary variable for unused outputs (may not be necessary)
                    ixArgs.add(a.type().isOutput() ? instructionProcessor.nextTemp() : LogicKeyword.create(a.name()));
                } else if (a.type().isInput()) {
                    // Input argument - take it as it is
                    ixArgs.add(fnArgs.get(argIndex++));
                } else if (a.type().isOutput()) {
                    if (argIndex >= fnArgs.size()) {
                        // Optional arguments are always output; generate temporary variable for them
                        ixArgs.add(instructionProcessor.nextTemp());
                    } else {
                        // Block name cannot be used as output argument
                        LogicValue argument = fnArgs.get(argIndex++);
                        if (argument.getType() == ArgumentType.BLOCK) {
                            throw new MindcodeException("Using argument '" + argument.toMlog() + "' in a call to '" + name
                                    + "' not allowed (name reserved for linked blocks)");
                        }
                        ixArgs.add(argument);
                    }
                } else {
                    ixArgs.add(toKeyword(fnArgs.get(argIndex++)));
                }
            }

            program.accept(instructionProcessor.createInstruction(astContextSupplier.get(), getOpcode(), ixArgs));
            return tmp;
        }

        @Override
        public String generateSampleCall() {
            StringBuilder str = new StringBuilder();
            List<NamedParameter> arguments = new ArrayList<>(getOpcodeVariant().namedParameters());
            NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == LogicParameter.RESULT);
            if (result != null) {
                str.append(result.name()).append(" = ");
            }

            List<String> strArguments = arguments.stream()
                    .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                    .map(NamedParameter::name)
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
            List<LogicArgument> arguments = getOpcodeVariant().namedParameters().stream()
                    .map(a -> a.type() == LogicParameter.UNUSED_OUTPUT ? tmpPrefix + counter.getAndIncrement() : a.name())
                    .map(BaseArgument::new)
                    .collect(Collectors.toList());
            return instructionProcessor.createInstructionUnchecked(staticAstContext, getOpcode(), arguments);
        }
    }

    // Chooses a function handler based on the first argument value
    private class MultiplexedFunctionHandler extends AbstractFunctionHandler {
        private final Map<String, FunctionHandler> functions;

        MultiplexedFunctionHandler(Map<String, FunctionHandler> functions, String name, OpcodeVariant opcodeVariant) {
            super(name, opcodeVariant, 0);
            this.functions = functions;
        }

        @Override
        public LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments) {
            // toKeywordOptional handles the case of somebody passing in a number as the first argument of e.g. ulocate.
            FunctionHandler handler = functions.get(toKeywordOptional(arguments.get(0)).getKeyword());
            if (handler == null) {
                throw new MindcodeInternalError("Unhandled type of " + getOpcode() + " in " + arguments);
            }
            return handler.handleFunction(program, arguments);
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
        PrintFunctionHandler(String name, OpcodeVariant opcodeVariant) {
            super(name, opcodeVariant, 1);
        }

        @Override
        public LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments) {
            arguments.forEach(arg -> program.accept(createInstruction(Opcode.PRINT, arg)));
            return arguments.get(arguments.size() - 1);
        }

        @Override
        public String generateSampleCall() {
            return getName() + "(" + joinNamedArguments(getOpcodeVariant().namedParameters()) + ")";
        }
    }
    
    private class UbindFunctionHandler extends AbstractFunctionHandler {
        UbindFunctionHandler(String name, OpcodeVariant opcodeVariant) {
            super(name, opcodeVariant, 1);
        }

        @Override
        public LogicValue handleFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments) {
            checkArguments(arguments);
            program.accept(createInstruction(Opcode.UBIND, arguments.get(0)));
            return LogicBuiltIn.UNIT;
        }

        @Override
        public String generateSampleCall() {
            return "unit = " + getName() + "(" + joinNamedArguments(getOpcodeVariant().namedParameters()) + ")";
        }
    }

    private Map<String, BuiltInFunctionHandler> createBuiltInFunctionMap() {
        Map<String, BuiltInFunctionHandler> map = new HashMap<>();
        map.put("println", this::handlePrintlnFunction);
        return map;
    }
    
    private LogicValue handlePrintlnFunction(Consumer<LogicInstruction> program, List<LogicValue> arguments) {
        arguments.forEach(arg -> program.accept(createInstruction(Opcode.PRINT, arg)));
        program.accept(createInstruction(Opcode.PRINT, LogicString.NEW_LINE));
        return arguments.isEmpty() ? LogicNull.NULL : arguments.get(arguments.size() - 1);
    }
}
