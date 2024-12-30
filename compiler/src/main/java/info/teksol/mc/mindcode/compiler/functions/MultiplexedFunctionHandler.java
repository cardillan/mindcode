package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Chooses a function handler based on the first argument value
@NullMarked
class MultiplexedFunctionHandler extends AbstractHandler implements FunctionHandler {
    private final NamedParameter namedParameter;
    private final Map<String, FunctionHandler> functions;

    MultiplexedFunctionHandler(BaseFunctionMapper functionMapper, Map<String, FunctionHandler> functions,
            String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 0, false);
        this.functions = functions;
        namedParameter = opcodeVariant.namedParameters().stream().filter(p -> p.type().isSelector())
                .findFirst().orElseThrow();
    }

    @Override
    public ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments) {
        String keyword = validateKeyword(namedParameter, arguments.getFirst()).getKeyword();
        if (keyword.isEmpty()) {
            // Keyword was not recognized and the error has already been reported
            return LogicNull.NULL;
        }

        FunctionHandler handler = functions.get(keyword);
        if (handler == null) {
            throw new MindcodeInternalError("No function for keyword " + keyword);
        }

        return handler.handleFunction(call, arguments);
    }

    @Override
    public void register(Consumer<SampleGenerator> registry) {
        functions.values().forEach(f -> f.register(registry));
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments) {
        throw new UnsupportedOperationException("Not supported for MultiplexedFunctionHandler");
    }
}
