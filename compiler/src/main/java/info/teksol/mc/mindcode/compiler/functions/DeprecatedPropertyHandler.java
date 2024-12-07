package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
class DeprecatedPropertyHandler extends AbstractMessageEmitter implements PropertyHandler {
    private final BaseFunctionMapper functionMapper;
    private final PropertyHandler replacement;
    private final String deprecated;
    private boolean warningEmitted = false;

    DeprecatedPropertyHandler(BaseFunctionMapper functionMapper, String deprecated, PropertyHandler replacement) {
        super(functionMapper.messageConsumer());
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
    public @Nullable String decompile(MlogInstruction instruction) {
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
    public LogicValue handleProperty(AstFunctionCall node, ValueStore target, List<FunctionArgument> arguments) {
        if (!warningEmitted) {
            functionMapper.messageConsumer().accept(CompilerMessage.warn(node.sourcePosition(),
                    "Function '%s' is no longer supported in Mindustry Logic version %s; using '%s' instead.",
                    deprecated, functionMapper.processorVersion, replacement.getName()));
            warningEmitted = true;
        }
        return replacement.handleProperty(node, target, arguments);
    }
}
