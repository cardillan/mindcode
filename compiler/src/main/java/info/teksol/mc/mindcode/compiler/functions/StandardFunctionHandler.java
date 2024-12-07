package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class StandardFunctionHandler extends AbstractHandler implements FunctionHandler, SelectorFunction {
    private final @Nullable NamedParameter selector;

    StandardFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs, boolean hasResult) {
        super(functionMapper, name, opcodeVariant, minArgs, numArgs, hasResult);
        this.selector = opcodeVariant.namedParameters().stream().filter(a -> a.type().isSelector()).findFirst().orElse(null);
    }

    @Override
    public NamedParameter getSelector() {
        if (selector == null) {
            throw new InvalidMetadataException("No keyword selector for function " + getName());
        }
        return selector;
    }
}
