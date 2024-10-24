package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.LogicNull.NULL;

class StandardFunctionHandler extends AbstractFunctionHandler implements SelectorFunction {
    private final BaseFunctionMapper functionMapper;
    private final String keyword;
    private final boolean hasResult;

    StandardFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs, boolean hasResult) {
        super(functionMapper, name, opcodeVariant, minArgs, numArgs);
        this.functionMapper = functionMapper;
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
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicValue> fnArgs) {
        if (!checkArguments(call, fnArgs)) return NULL;

        LogicValue tmp = hasResult ? functionMapper.instructionProcessor.nextTemp() : NULL;
        // Need to support all kinds of arguments here, including keywords
        List<LogicArgument> ixArgs = new ArrayList<>();
        int argIndex = 0;

        for (NamedParameter a : opcodeVariant.namedParameters()) {
            if (a.type().isGlobal() && !fnArgs.get(argIndex).isGlobalVariable()) {
                error(call, "Using argument '%s' in a call to '%s' not allowed (a global variable is required).",
                        fnArgs.get(argIndex).toMlog(), name);
                return NULL;
            }

            if (a.type() == InstructionParameterType.RESULT) {
                ixArgs.add(tmp);
            } else if (a.type().isSelector() && !a.type().isFunctionName()) {
                // Selector that IS NOT a function name is taken from the argument list
                ixArgs.add(BaseFunctionMapper.toKeyword(fnArgs.get(argIndex++)));
            } else if (a.type().isSelector()) {
                // Selector that IS a function name isn't in an argument list and must be filled in
                // Perhaps we might store the Operation into the NamedParameter directly to avoid lookup
                ixArgs.add(getOpcode() == Opcode.OP ? Operation.fromMlog(a.name()) : LogicKeyword.create(a.name()));
            } else if (a.type().isUnused()) {
                // Unused inputs must be filled with defaults
                // Generate new temporary variable for unused outputs (will be replaced by the temp optimizer)
                ixArgs.add(a.type().isOutput() ? functionMapper.instructionProcessor.nextTemp() : LogicKeyword.create(a.name()));
            } else if (a.type().isInput()) {
                // Input argument - take it as it is
                ixArgs.add(fnArgs.get(argIndex++));
            } else if (a.type().isOutput()) {
                if (argIndex >= fnArgs.size()) {
                    // Optional arguments are always output; generate temporary variable for them
                    ixArgs.add(functionMapper.instructionProcessor.nextTemp());
                } else {
                    // Block name cannot be used as output argument
                    LogicValue argument = fnArgs.get(argIndex++);
                    if (argument.getType() == ArgumentType.BLOCK) {
                        error(call, "Using argument '%s' in a call to '%s' not allowed (name reserved for linked blocks).",
                                argument.toMlog(), name);
                        return NULL;
                    }
                    ixArgs.add(argument);
                }
            } else {
                ixArgs.add(BaseFunctionMapper.toKeyword(fnArgs.get(argIndex++)));
            }
        }

        program.accept(functionMapper.instructionProcessor.createInstruction(functionMapper.astContextSupplier.get(), getOpcode(), ixArgs));
        return tmp;
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments, boolean markOptional) {
        StringBuilder str = new StringBuilder();
        NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.RESULT);
        if (result != null) {
            str.append(result.name()).append(" = ");
        }

        List<String> strArguments = arguments.stream()
                .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                .map(NamedParameter::name)
                .collect(Collectors.toList());

        // Mark optional arguments
        if (markOptional) {
            strArguments.subList(minArgs, numArgs).replaceAll(s -> s + "?");
        }
        str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
        return str.toString();
    }
}
