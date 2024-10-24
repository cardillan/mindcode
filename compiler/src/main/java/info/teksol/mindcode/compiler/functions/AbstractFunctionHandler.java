package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.List;

abstract class AbstractFunctionHandler extends AbstractHandler implements FunctionHandler {
    protected final String name;
    protected final int minArgs;
    protected final int numArgs;

    AbstractFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant, int numArgs) {
        this(functionMapper, name, opcodeVariant, numArgs, numArgs);
    }

    AbstractFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant, int minArgs, int numArgs) {
        super(functionMapper, opcodeVariant);
        if (minArgs > numArgs) {
            throw new InvalidMetadataException("Minimum number of arguments greater than total.");
        }
        this.name = name;
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

    protected boolean checkArguments(FunctionCall call, List<LogicValue> arguments) {
        if (arguments.size() < minArgs || arguments.size() > numArgs) {
            String args = (minArgs == numArgs) ? String.valueOf(numArgs) : minArgs + " to " + numArgs;
            error(call, "Function '%s': wrong number of arguments (expected %s, found %d)", name, args, arguments.size());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", numArgs=" + numArgs + '}';
    }
}
