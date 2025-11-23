package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractLoopBuilder extends AbstractCodeBuilder {
    protected static final int LOOP_REPETITIONS = 25;             // Estimated number of repetitions for normal loops

    protected AbstractLoopBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    protected LoopLabels enterLoop(AstLabeledStatement loopNode) {
        final LogicLabel continueLabel = assembler.nextLabel();
        final LogicLabel breakLabel = assembler.nextLabel();
        return variables.getLoopStack().enterLoop(loopNode.getLabel(), breakLabel, continueLabel);
    }

    protected LogicLabel getBreakLabel(AstBreakStatement loopNode) {
        return variables.getLoopStack().getBreakLabel(loopNode);
    }

    protected LogicLabel getContinueLabel(AstContinueStatement loopNode) {
        return variables.getLoopStack().getContinueLabel(loopNode);
    }

    protected void exitLoop(LoopLabels loopLabels) {
        variables.getLoopStack().exitLoop(loopLabels);
    }

    protected void allowContinue(boolean allowed) {
        variables.getLoopStack().allowContinue(allowed);
    }
}
