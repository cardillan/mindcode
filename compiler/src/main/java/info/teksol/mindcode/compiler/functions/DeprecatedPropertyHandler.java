package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class DeprecatedPropertyHandler extends AbstractMessageEmitter implements PropertyHandler {
    private final BaseFunctionMapper functionMapper;
    private final PropertyHandler replacement;
    private final String deprecated;
    private boolean warningEmitted = false;

    DeprecatedPropertyHandler(BaseFunctionMapper functionMapper, String deprecated, PropertyHandler replacement) {
        super(functionMapper.getMessageConsumer());
        this.functionMapper = functionMapper;
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
    public String decompile(MlogInstruction instruction) {
        return null;
    }

    @Override
    public String generateSampleCall() {
        List<NamedParameter> arguments = new ArrayList<>(getOpcodeVariant().namedParameters());
        NamedParameter blockArgument = CollectionUtils.removeFirstMatching(arguments, a -> a.type() == InstructionParameterType.BLOCK);
        CollectionUtils.removeFirstMatching(arguments, a -> a.type().isSelector());
        return blockArgument.name() + "." + deprecated + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }

    @Override
    public String generateSecondarySampleCall() {
        List<NamedParameter> args = new ArrayList<>(getOpcodeVariant().namedParameters());
        NamedParameter blockArgument = CollectionUtils.removeFirstMatching(args, a -> a.type() == InstructionParameterType.BLOCK);
        CollectionUtils.removeFirstMatching(args, a -> a.type().isSelector());
        if (args.size() == 1 && args.getFirst().type() == InstructionParameterType.INPUT) {
            return blockArgument.name() + "." + deprecated + " = " + args.getFirst().name();
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
    public LogicValue handleProperty(AstNode node, Consumer<LogicInstruction> program, LogicValue target, List<LogicFunctionArgument> arguments) {
        if (!warningEmitted) {
            functionMapper.getMessageConsumer().accept(CompilerMessage.warn(node.inputPosition(),
                    "Function '%s' is no longer supported in Mindustry Logic version %s; using '%s' instead.",
                    deprecated, functionMapper.processorVersion, replacement.getName()));
            warningEmitted = true;
        }
        return replacement.handleProperty(node, program, target, arguments);
    }
}
