package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.logic.arguments.LogicNull;
import io.github.cardillan.mindcode.logic.opcodes.NamedParameter;
import io.github.cardillan.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mindcode.messages.ERR;
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
        if (arguments.isEmpty()) {
            error(call, ERR.FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, 0);
            return LogicNull.NULL;
        }

        String keyword = validateKeyword(namedParameter, arguments.getFirst(), false).getKeyword();
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
    protected String generateCall(List<NamedParameter> arguments, boolean useKeywordPrefixes) {
        throw new UnsupportedOperationException("Not supported for MultiplexedFunctionHandler");
    }
}
