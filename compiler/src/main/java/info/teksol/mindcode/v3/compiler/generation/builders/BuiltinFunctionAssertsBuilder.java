package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class BuiltinFunctionAssertsBuilder extends AbstractFunctionBuilder {

    public BuiltinFunctionAssertsBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public ValueStore handleAssertEquals(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);

        if (validateStandardFunctionArguments(call, arguments, 3)) {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            LogicValue expected = evaluate(call.getArgument(0)).getValue(assembler);
            LogicValue actual = evaluate(call.getArgument(1)).getValue(assembler);
            LogicValue title = evaluate(call.getArgument(2)).getValue(assembler);
            assembler.createInstruction(Opcode.ASSERT_EQUALS, expected, actual, title);
        }

        assembler.clearSubcontextType();
        return LogicVoid.VOID;
    }

    public ValueStore handleAssertPrints(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        if (call.getArguments().size() != 3) {
            // Validate all arguments we got and produce messages
            List<FunctionArgument> arguments = processArguments(call);
            validateStandardFunctionArguments(call, arguments, 3);
        } else {
            assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
            // We must not evaluate the second argument until necessary
            LogicValue expected = evaluate(call.getArgument(0)).getValue(assembler);
            LogicValue title = evaluate(call.getArgument(2)).getValue(assembler);
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            assembler.createInstruction(Opcode.ASSERT_FLUSH);
            compile(call.getArgument(1));   // Just print, not interested in result
            assembler.createInstruction(Opcode.ASSERT_PRINTS, expected, title);
        }

        assembler.clearSubcontextType();
        return LogicVoid.VOID;
    }
}
