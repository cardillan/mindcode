package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class WriteArrInstruction extends BaseInstruction implements ArrayAccessInstruction {

    WriteArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITEARR, args, params);
        createSideEffects();
    }

    protected WriteArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        createSideEffects();
    }

    private void createSideEffects() {
        if (astContext.matches(AstSubcontextType.MOCK)) return;

        List<LogicVariable> elements = getArray().getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();

        List<LogicVariable> reads = MindcodeCompiler.getContext().compilerProfile().isSymbolicLabels()
                ? List.of(getArray().writeVal, getArray().writeInd)
                : List.of(getArray().writeVal);

        setSideEffects(SideEffects.of(reads, List.of(), elements));
    }

    @Override
    public WriteArrInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new WriteArrInstruction(this, astContext);
    }

    @Override
    public String getJumpTableId() {
        return getArray().getWriteJumpTableId();
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
