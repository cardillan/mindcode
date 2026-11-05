package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class FormatInstruction extends BaseInstruction {

    FormatInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.FORMAT, args, params);
    }

    protected FormatInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public FormatInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new FormatInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
