package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.generated.ast.visitors.AstWhileLoopStatementVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstWhileLoopStatement;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

@NullMarked
public class WhileLoopStatementsHandler extends BaseLoopHandler implements AstWhileLoopStatementVisitor<NodeValue> {

    public WhileLoopStatementsHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitWhileLoopStatement(AstWhileLoopStatement node) {
        if (node.isEntryCondition()) {
            createWhileLoop(node);
        } else {
            createDoWhileLoop(node);
        }

        return LogicVoid.VOID;
    }

    private void createDoWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = processor.nextLabel();
        LoopLabels loopLabels = enterLoop(node.inputPosition(), node);

        // Loop body
        codeBuilder.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        codeBuilder.createLabel(beginLabel);
        node.getBody().forEach(this::visit);

        // Condition
        // TODO Continue label should probably go into condition context?
        codeBuilder.createLabel(loopLabels.continueLabel());
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        final NodeValue cond = visit(node.getCondition());
        codeBuilder.createJump(beginLabel, Condition.NOT_EQUAL, cond.getValue(codeBuilder), FALSE);

        // Exit
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        codeBuilder.createLabel(loopLabels.doneLabel());
        codeBuilder.clearSubcontextType();
        exitLoop(loopLabels);
    }

    private void createWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = processor.nextLabel();
        LoopLabels loopLabels = enterLoop(node.inputPosition(), node);

        // Condition
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        // TODO Place continue label instead of beginLabel, drop beginLabel
        codeBuilder.createLabel(beginLabel);
        final NodeValue cond = visit(node.getCondition());
        codeBuilder.createJump(loopLabels .doneLabel(), Condition.EQUAL, cond.getValue(codeBuilder), FALSE);

        // Loop body
        codeBuilder.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        node.getBody().forEach(this::visit);

        // Flow control
        codeBuilder.createLabel(loopLabels.continueLabel());
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        codeBuilder.createJumpUnconditional(beginLabel);

        // Exit
        codeBuilder.createLabel(loopLabels.doneLabel());
        codeBuilder.clearSubcontextType();
        exitLoop(loopLabels);
    }
}
