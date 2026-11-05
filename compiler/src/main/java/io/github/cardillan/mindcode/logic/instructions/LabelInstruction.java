package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class LabelInstruction extends BaseInstruction implements LabeledInstruction {

    LabelInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.LABEL, args, params);
    }

    protected LabelInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public LabelInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new LabelInstruction(this, astContext);
    }

    @Override
    public LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }

    public LabelInstruction withLabel(LogicLabel label) {
        return getLabel() == label ? this
                : new LabelInstruction(getAstContext(), List.of(label), getArgumentTypes()).copyInfo(this);
    }
}
