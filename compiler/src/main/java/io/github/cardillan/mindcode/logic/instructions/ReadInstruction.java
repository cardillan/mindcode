package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ReadInstruction extends BaseResultInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READ, args, params);
    }

    protected ReadInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReadInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReadInstruction(this, astContext);
    }

    @Override
    public ReadInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadInstruction(astContext, List.of(result, getMemory(), getIndex()), getArgumentTypes()).copyInfo(this);
    }

    public final LogicValue getMemory() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
