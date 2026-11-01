package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class CustomInstruction extends AbstractInstruction {
    private final boolean safe;
    private final boolean text;
    private final boolean label;
    private final String opcode;

    public CustomInstruction(AstContext astContext, boolean safe, boolean text, boolean label, String opcode, List<LogicArgument> args,
            @Nullable List<InstructionParameterType> params) {
        super(astContext, args, params);
        this.safe = safe;
        this.text = text;
        this.label = label;
        this.opcode = Objects.requireNonNull(opcode);
    }

    protected CustomInstruction(CustomInstruction other, AstContext astContext) {
        super(other, astContext);
        this.safe = other.safe;
        this.text = other.text;
        this.label = other.label;
        this.opcode = other.opcode;
    }

    @Override
    public CustomInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new CustomInstruction(this, astContext);
    }

    @Override
    public Opcode getOpcode() {
        return Opcode.CUSTOM;
    }

    @Override
    public String getMlogOpcode() {
        return opcode;
    }

    @Override
    public boolean isSafe() {
        return safe;
    }

    public boolean isText() {
        return text;
    }

    public boolean isLabel() {
        return label;
    }

    @Override
    public boolean isReal() {
        return !label;
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return label ? 0 : 1;
    }
}
