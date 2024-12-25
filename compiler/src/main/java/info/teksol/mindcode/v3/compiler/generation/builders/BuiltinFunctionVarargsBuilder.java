package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.LogicNull;
import info.teksol.mindcode.logic.LogicNumber;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class BuiltinFunctionVarargsBuilder extends AbstractFunctionBuilder {

    public BuiltinFunctionVarargsBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public NodeValue handleLength(AstFunctionCall call) {
        if (validateStandardFunctionArguments(call, 3)) {
            assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
            List<FunctionArgument> arguments = processArguments(call.getArguments());
            assembler.clearSubcontextType();
            return LogicNumber.get(arguments.size());
        } else {
            return LogicNumber.ZERO;
        }
    }

    public NodeValue handleMinMax(AstFunctionCall call) {
        validateStandardFunctionArguments(call.getArguments());
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        if (arguments.size() < 2) {
            error(call, "Not enough arguments to the '%s' function (expected 2 or more, found %d).",
                    call.getFunctionName(), call.getArguments().size());

            return LogicNull.NULL;
        }

        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        LogicVariable result = nextNodeResultTemp();
        Operation op = Operation.fromMlog(call.getFunctionName());
        assembler.createOp(op, result, arguments.get(0).getValue(assembler), arguments.get(1).getValue(assembler));
        for (int i = 2; i < arguments.size(); i++) {
            assembler.createOp(op, result, result, arguments.get(i).getValue(assembler));
        }
        assembler.clearSubcontextType();
        return result;
    }
}
