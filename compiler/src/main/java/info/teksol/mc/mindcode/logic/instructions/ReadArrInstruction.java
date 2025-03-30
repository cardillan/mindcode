package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public class ReadArrInstruction extends BaseResultInstruction implements ArrayAccessInstruction {

    ReadArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READARR, args, params);
        createSideEffects();
    }

    protected ReadArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        createSideEffects();
    }

    private void createSideEffects() {
        if (astContext.matches(AstSubcontextType.MOCK)) return;

        List<LogicVariable> elements = getArray().getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));

        if (MindcodeCompiler.getContext().compilerProfile().isSymbolicLabels()) {
            elements.add(getArray().readInd);
        }

        setSideEffects(SideEffects.of(List.copyOf(elements), List.of(), List.of(getArray().readVal)));
    }


    @Override
    public ReadArrInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReadArrInstruction(this, astContext);
    }

    @Override
    public String getJumpTableId() {
        return getArray().getReadJumpTableId();
    }

    @Override
    public ReadArrInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadArrInstruction(astContext, List.of(result, getArray(), getIndex()), getArgumentTypes());
    }
}
