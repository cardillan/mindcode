package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstWhileLoopStatementVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstWhileLoopStatement;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

@NullMarked
public class WhileLoopStatementsBuilder extends AbstractLoopBuilder implements AstWhileLoopStatementVisitor<NodeValue> {

    public WhileLoopStatementsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
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
        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Loop body
        codeBuilder.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        codeBuilder.createLabel(beginLabel);
        visitStatements(node.getBody());

        // Condition
        // TODO Continue label should probably go into condition context?
        codeBuilder.createLabel(loopLabels.continueLabel());
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        final NodeValue condition = visit(node.getCondition());
        codeBuilder.createJump(beginLabel, Condition.NOT_EQUAL, condition.getValue(codeBuilder), FALSE);

        // Exit
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        codeBuilder.createLabel(loopLabels.doneLabel());
        codeBuilder.clearSubcontextType();
        exitLoop(node);
    }

    private void createWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        // TODO Place continue label instead of beginLabel, drop beginLabel
        codeBuilder.createLabel(beginLabel);
        final NodeValue condition = visit(node.getCondition());
        codeBuilder.createJump(loopLabels.doneLabel(), Condition.EQUAL, condition.getValue(codeBuilder), FALSE);

        // Loop body
        codeBuilder.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visitStatements(node.getBody());

        // Flow control
        codeBuilder.createLabel(loopLabels.continueLabel());
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        codeBuilder.createJumpUnconditional(beginLabel);

        // Exit
        codeBuilder.createLabel(loopLabels.doneLabel());
        codeBuilder.clearSubcontextType();
        exitLoop(node);
    }
}