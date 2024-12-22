package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BaseLoopHandler extends BaseHandler {
    protected static final int LOOP_REPETITIONS = 25;             // Estimated number of repetitions for normal loops

    public BaseLoopHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    protected LoopLabels enterLoop(InputPosition position, AstLabeledStatement loopNode) {
        final LogicLabel continueLabel = processor.nextLabel();
        final LogicLabel doneLabel = processor.nextLabel();
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();
        codeBuilder.getLoopStack().enterLoop(position, loopLabel, doneLabel, continueLabel);
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

    protected void exitLoop(LoopLabels loopLabels) {
        codeBuilder.getLoopStack().exitLoop(loopLabels.loopLabel);
    }

    protected record LoopLabels(String loopLabel, LogicLabel continueLabel, LogicLabel doneLabel) {}
}
