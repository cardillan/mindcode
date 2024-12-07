package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractLoopBuilder extends AbstractBuilder {
    protected static final int LOOP_REPETITIONS = 25;             // Estimated number of repetitions for normal loops

    protected AbstractLoopBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    protected LoopLabels enterLoop(AstLabeledStatement loopNode) {
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        variables.getLoopStack().enterLoop(loopNode.sourcePosition(), loopLabel, doneLabel, continueLabel);
        return new LoopLabels(loopLabel, continueLabel, doneLabel);
    }

    protected LogicLabel getBreakLabel(SourcePosition position, AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        return variables.getLoopStack().getBreakLabel(position, loopLabel);
    }

    protected LogicLabel getContinueLabel(SourcePosition position, AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        return variables.getLoopStack().getContinueLabel(position, loopLabel);
    }

    protected void exitLoop(AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        variables.getLoopStack().exitLoop(loopLabel);
    }

    protected record LoopLabels(String loopLabel, LogicLabel continueLabel, LogicLabel doneLabel) {}
}
