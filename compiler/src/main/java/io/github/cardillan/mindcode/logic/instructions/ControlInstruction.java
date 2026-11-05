package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicKeyword;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ControlInstruction extends BaseInstruction {

    ControlInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.CONTROL, args, params);
    }

    protected ControlInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ControlInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ControlInstruction(this, astContext);
    }

    public final LogicKeyword getProperty() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getObject() {
        return (LogicValue) getArg(1);
    }

    // Property types

    public final LogicValue getValue() {
        return (LogicValue) getArg(2);
    }
}
