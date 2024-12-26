package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
class StandardPropertyHandler extends AbstractHandler implements PropertyHandler {

    StandardPropertyHandler(BaseFunctionMapper functionMapper,
            String name, OpcodeVariant opcodeVariant, int numArgs, boolean hasResult) {
        super(functionMapper, name, opcodeVariant, numArgs, hasResult);
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
        if (args.size() == 1 && args.getFirst().type() == InstructionParameterType.INPUT) {
            return block.name() + "." + getName() + " = " + args.getFirst().name();
        } else {
            return null;
        }
    }
}
