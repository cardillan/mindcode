package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class GetlinkInstruction extends BaseResultInstruction {

    GetlinkInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.GETLINK, args, params);
    }

    protected GetlinkInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public GetlinkInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new GetlinkInstruction(this, astContext);
    }

    @Override
    public GetlinkInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new GetlinkInstruction(astContext, List.of(result, getIndex()), getArgumentTypes()).copyInfo(this);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(1);
    }
}
