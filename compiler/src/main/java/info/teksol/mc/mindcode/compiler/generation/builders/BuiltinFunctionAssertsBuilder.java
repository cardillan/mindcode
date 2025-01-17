package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

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
            LogicValue expected = arguments.get(0).getValue(assembler);
            LogicValue actual = arguments.get(1).getValue(assembler);
            LogicValue title = arguments.get(2).getValue(assembler);
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
            LogicValue expected = convertArgument(call.getArgument(0)).validateAsInput(messageConsumer).getValue(assembler);
            LogicValue title = convertArgument(call.getArgument(2)).validateAsInput(messageConsumer).getValue(assembler);
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            assembler.createInstruction(Opcode.ASSERT_FLUSH);
            validateAndCompile(call.getArgument(1));           // Just print, not interested in result
            assembler.createInstruction(Opcode.ASSERT_PRINTS, expected, title);
        }

        assembler.clearSubcontextType();
        return LogicVoid.VOID;
    }

    private void validateAndCompile(AstFunctionArgument argument) {
        if (!argument.hasExpression()) {
            messageConsumer.addMessage(CompilerMessage.error(argument.sourcePosition(),
                    "Parameter corresponding to this argument isn't optional, a value must be provided."));
        }
        if (argument.hasOutModifier()) {
            messageConsumer.addMessage(CompilerMessage.error(argument.sourcePosition(),
                    "Parameter corresponding to this argument isn't output, 'out' modifier cannot be used."));
        }
        compile(Objects.requireNonNull(argument.getExpression()));
    }
}
