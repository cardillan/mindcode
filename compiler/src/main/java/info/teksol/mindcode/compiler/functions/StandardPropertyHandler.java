package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.LogicNull.NULL;

class StandardPropertyHandler extends AbstractPropertyHandler {
    private final boolean hasResult;

    StandardPropertyHandler(BaseFunctionMapper functionMapper,
            String name, OpcodeVariant opcodeVariant, int numArgs, boolean hasResult) {
        super(functionMapper, name, opcodeVariant, numArgs);
        this.hasResult = hasResult;
    }

    @Override
    public LogicValue handleProperty(AstNode node, Consumer<LogicInstruction> program, LogicValue target, List<LogicValue> fnArgs) {
        if (!checkArguments(node, fnArgs)) {
            return NULL;
        }

        LogicValue tmp = hasResult ? functionMapper.instructionProcessor.nextTemp() : NULL;
        List<LogicArgument> ixArgs = new ArrayList<>();
        int argIndex = 0;

        for (NamedParameter a : opcodeVariant.namedParameters()) {
            if (a.type().isGlobal() && !fnArgs.get(argIndex).isGlobalVariable()) {
                error(node, "Using argument '%s' in a call to '%s' not allowed (a global variable is required).",
                        fnArgs.get(argIndex).toMlog(), name);
                return NULL;
            }

            if (a.type() == InstructionParameterType.RESULT) {
                ixArgs.add(tmp);
            } else if (a.type() == InstructionParameterType.BLOCK) {
                ixArgs.add(target);
            } else if (a.type().isSelector() && !a.type().isFunctionName()) {
                // Selector that IS NOT a function name is taken from the argument list
                ixArgs.add(BaseFunctionMapper.toKeyword(fnArgs.get(argIndex++)));
            } else if (a.type().isSelector()) {
                // Selector that IS a function name isn't in an argument list and must be filled in
                ixArgs.add(LogicKeyword.create(a.name()));
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
                    LogicArgument argument = fnArgs.get(argIndex++);
                    if (argument.getType() == ArgumentType.BLOCK) {
                        error(node, "Using argument '%s' in a call to '%s' not allowed (name reserved for linked blocks).", argument.toMlog(), name);
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
    public String generateCall(List<NamedParameter> arguments) {
        StringBuilder str = new StringBuilder();
        NamedParameter result = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.RESULT);
        if (result != null) {
            str.append(result.name()).append(" = ");
        }

        NamedParameter block = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.BLOCK);
        Objects.requireNonNull(block);
        str.append(block.name()).append('.');

        List<String> strArguments = arguments.stream()
                .filter(a -> !a.type().isUnused() && !a.type().isFunctionName())
                .map(NamedParameter::name)
                .collect(Collectors.toList());

        str.append(getName()).append("(").append(String.join(", ", strArguments)).append(")");
        return str.toString();
    }

    @Override
    public String generateSecondaryCall(List<NamedParameter> arguments) {
        List<NamedParameter> args = new ArrayList<>(arguments);
        NamedParameter blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.type() == InstructionParameterType.BLOCK);
        CollectionUtils.removeFirstMatching(args, a -> a.type().isSelector());
        if (args.size() == 1 && args.get(0).type() == InstructionParameterType.INPUT) {
            return blockArgument.name() + "." + getName() + " = " + args.get(0).name();
        } else {
            return null;
        }
    }
}
