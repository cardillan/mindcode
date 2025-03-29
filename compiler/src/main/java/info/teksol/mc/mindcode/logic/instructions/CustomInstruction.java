package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class CustomInstruction extends AbstractInstruction {
    private final boolean safe;
    private final boolean text;
    private final String opcode;

    public CustomInstruction(AstContext astContext, boolean safe, boolean text, String opcode, List<LogicArgument> args,
            @Nullable List<InstructionParameterType> params) {
        super(astContext, args, params);
        this.safe = safe;
        this.text = text;
        this.opcode = Objects.requireNonNull(opcode);
    }

    protected CustomInstruction(CustomInstruction other, AstContext astContext) {
        super(other, astContext);
        this.safe = other.safe;
        this.text = other.text;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomInstruction that = (CustomInstruction) o;
        return Objects.equals(opcode, that.opcode) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcode, args);
    }
}
