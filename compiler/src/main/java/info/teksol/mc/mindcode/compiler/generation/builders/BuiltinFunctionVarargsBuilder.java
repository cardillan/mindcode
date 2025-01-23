package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class BuiltinFunctionVarargsBuilder extends AbstractFunctionBuilder {

    public BuiltinFunctionVarargsBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public ValueStore handleLength(AstFunctionCall call) {
        if (call.getArguments().size() != 1) {
            error(call, "Function '%s': wrong number of arguments (expected 1, found %d).",
                    call.getFunctionName(), call.getArguments().size());
            return LogicNumber.ZERO;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);
        assembler.clearSubcontextType();
        return LogicNumber.create(arguments.size());
    }

    public ValueStore handleMinMax(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        LogicVariable result = nextNodeResultTemp();
        if (arguments.size() < 2) {
            error(call, "Not enough arguments to the '%s' function (expected 2 or more, found %d).",
                    call.getFunctionName(), call.getArguments().size());
        } else {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            Operation op = Operation.fromMlog(call.getFunctionName());
            assert op != null;
            assembler.createOp(op, result, arguments.get(0).getValue(assembler), arguments.get(1).getValue(assembler));
            for (int i = 2; i < arguments.size(); i++) {
                assembler.createOp(op, result, result, arguments.get(i).getValue(assembler));
            }
        }
        assembler.clearSubcontextType();
        return result;
    }
}
