package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicFunctionArgument> fnArgs) {
        if (!validateArguments(call, fnArgs)) return NULL;

        LogicValue tmp = hasResult ? functionMapper.instructionProcessor.nextTemp() : NULL;
        // Need to support all kinds of arguments here, including keywords
        List<LogicArgument> ixArgs = new ArrayList<>();
        int argIndex = 0;

        for (NamedParameter a : opcodeVariant.namedParameters()) {
            if (a.type().isGlobal() && !fnArgs.get(argIndex).value().isGlobalVariable()) {
                error(call, "Using argument '%s' in a call to '%s' not allowed (a global variable is required).",
                        fnArgs.get(argIndex).value().toMlog(), name);
                return NULL;
            }

            if (a.type() == InstructionParameterType.RESULT) {
                ixArgs.add(tmp);
            } else if (a.type().isSelector() && !a.type().isFunctionName()) {
                // Selector that IS NOT a function name is taken from the argument list
                ixArgs.add(BaseFunctionMapper.toKeyword(requireNoModifiers(a, fnArgs.get(argIndex++))));
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
                ixArgs.add(requireNoOutModifier(a, fnArgs.get(argIndex++)));
            } else if (a.type().isOutput()) {
                if (argIndex >= fnArgs.size()) {
                    // Optional arguments are always output; generate temporary variable for them
                    ixArgs.add(functionMapper.instructionProcessor.nextTemp());
                } else {
                    // Block name cannot be used as output argument
                    LogicFunctionArgument argument = fnArgs.get(argIndex++);
                    if (argument.value().getType() == ArgumentType.BLOCK) {
                        error(call, "Using argument '%s' in a call to '%s' not allowed (name reserved for linked blocks).",
                                argument.value().toMlog(), name);
                        return NULL;
                    }
                    ixArgs.add(requireOutModifier(a, argument));
                }
            } else {
                // Nether input nor output???
                ixArgs.add(BaseFunctionMapper.toKeyword(requireNoModifiers(a, fnArgs.get(argIndex++))));
            }
        }

        program.accept(functionMapper.instructionProcessor.createInstruction(
                functionMapper.astContextSupplier.get(), getOpcode(), ixArgs));

        return tmp;
    }
}
