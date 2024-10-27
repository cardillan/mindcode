package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mindcode.logic.LogicNull.NULL;
import static info.teksol.mindcode.logic.LogicVoid.VOID;

class StandardPropertyHandler extends AbstractPropertyHandler {
    private final boolean hasResult;

    StandardPropertyHandler(BaseFunctionMapper functionMapper,
            String name, OpcodeVariant opcodeVariant, int numArgs, boolean hasResult) {
        super(functionMapper, name, opcodeVariant, numArgs);
        this.hasResult = hasResult;
    }

    @Override
    public LogicValue handleProperty(AstNode node, Consumer<LogicInstruction> program, LogicValue target, List<LogicFunctionArgument> fnArgs) {
        if (!validateArguments(node, fnArgs)) {
            return NULL;
        }

        LogicValue tmp = hasResult ? functionMapper.instructionProcessor.nextTemp() : VOID;
        List<LogicArgument> ixArgs = new ArrayList<>();
        int argIndex = 0;

        for (NamedParameter a : opcodeVariant.namedParameters()) {
            if (a.type().isGlobal() && !fnArgs.get(argIndex).value().isGlobalVariable()) {
                error(node, "Using argument '%s' in a call to '%s' not allowed (a global variable is required).",
                        fnArgs.get(argIndex).value().toMlog(), name);
                return NULL;
            }

            if (a.type() == InstructionParameterType.RESULT) {
                ixArgs.add(tmp);
            } else if (a.type() == InstructionParameterType.BLOCK) {
                ixArgs.add(target);
            } else if (a.type().isSelector() && !a.type().isFunctionName()) {
                // Selector that IS NOT a function name is taken from the argument list
                ixArgs.add(BaseFunctionMapper.toKeyword(requireNoModifiers(a, fnArgs.get(argIndex++))));
            } else if (a.type().isSelector()) {
                // Selector that IS a function name isn't in an argument list and must be filled in
                ixArgs.add(LogicKeyword.create(a.name()));
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
                        error(node, "Using argument '%s' in a call to '%s' not allowed (name reserved for linked blocks).",
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

    @Override
    protected String generateCall(List<NamedParameter> arguments) {
        NamedParameter block = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.BLOCK);
        Objects.requireNonNull(block);
        String methodCall = super.generateCall(arguments);
        return methodCall.contains(" = ")
                ? methodCall.replace(" = ", " = " + block.name() + '.')
                : block.name() + '.' + methodCall;
    }

    @Override
    public String generateSecondaryCall(List<NamedParameter> arguments, boolean markOptional) {
        List<NamedParameter> args = new ArrayList<>(arguments);
        NamedParameter block = CollectionUtils.removeFirstMatching(args, a -> a.type() == InstructionParameterType.BLOCK);
        Objects.requireNonNull(block);
        CollectionUtils.removeFirstMatching(args, a -> a.type().isSelector());
        if (args.size() == 1 && args.get(0).type() == InstructionParameterType.INPUT) {
            return block.name() + "." + getName() + " = " + args.get(0).name();
        } else {
            return null;
        }
    }
}
