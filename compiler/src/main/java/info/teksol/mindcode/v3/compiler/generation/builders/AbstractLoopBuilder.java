package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractLoopBuilder extends AbstractBuilder {
    protected static final int LOOP_REPETITIONS = 25;             // Estimated number of repetitions for normal loops

    protected AbstractLoopBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    protected LoopLabels enterLoop(AstLabeledStatement loopNode) {
        final LogicLabel continueLabel = nextLabel();
        final LogicLabel doneLabel = nextLabel();
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        codeBuilder.getLoopStack().enterLoop(loopNode.inputPosition(), loopLabel, doneLabel, continueLabel);
        return new LoopLabels(loopLabel, continueLabel, doneLabel);
    }

    protected LogicLabel getBreakLabel(InputPosition position, AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        return codeBuilder.getLoopStack().getBreakLabel(position, loopLabel);
    }

    protected LogicLabel getContinueLabel(InputPosition position, AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        return codeBuilder.getLoopStack().getContinueLabel(position, loopLabel);
    }

    protected void exitLoop(AstLabeledStatement loopNode) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        codeBuilder.getLoopStack().exitLoop(loopLabel);
    }

    protected record LoopLabels(String loopLabel, LogicLabel continueLabel, LogicLabel doneLabel) {}
}