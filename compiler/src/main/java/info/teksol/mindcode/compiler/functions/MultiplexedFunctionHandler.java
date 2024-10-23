package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Chooses a function handler based on the first argument value
class MultiplexedFunctionHandler extends AbstractFunctionHandler {
    private final Map<String, FunctionHandler> functions;

    MultiplexedFunctionHandler(BaseFunctionMapper functionMapper, Map<String, FunctionHandler> functions,
            String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 0);
        this.functions = functions;
    }

    @Override
    public LogicValue handleFunction(AstNode node, Consumer<LogicInstruction> program, List<LogicValue> arguments) {
        // toKeywordOptional handles the case of somebody passing in a number as the first argument of e.g. ulocate.
        FunctionHandler handler = functions.get(BaseFunctionMapper.toKeywordOptional(arguments.get(0)).getKeyword());
        if (handler == null) {
            throw new MindcodeInternalError("Unhandled type of " + getOpcode() + " in " + arguments);
        }
        return handler.handleFunction(node, program, arguments);
    }

    @Override
    public void register(Consumer<SampleGenerator> registry) {
        functions.values().forEach(f -> f.register(registry));
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments, boolean markOptional) {
        throw new UnsupportedOperationException("Not supported for MultiplexedFunctionHandler");
    }
}
