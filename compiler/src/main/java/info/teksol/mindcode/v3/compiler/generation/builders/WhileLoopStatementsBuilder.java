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

    public WhileLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
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
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        visitBody(node.getBody());

        // Condition
        // TODO Continue label should probably go into condition context?
        assembler.createLabel(loopLabels.continueLabel());
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        final NodeValue condition = evaluate(node.getCondition());
        assembler.createJump(beginLabel, Condition.NOT_EQUAL, condition.getValue(assembler), FALSE);

        // Exit
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createLabel(loopLabels.doneLabel());
        assembler.clearSubcontextType();
        exitLoop(node);
    }

    private void createWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        // TODO Place continue label instead of beginLabel, drop beginLabel
        assembler.createLabel(beginLabel);
        final NodeValue condition = evaluate(node.getCondition());
        assembler.createJump(loopLabels.doneLabel(), Condition.EQUAL, condition.getValue(assembler), FALSE);

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visitBody(node.getBody());

        // Flow control
        assembler.createLabel(loopLabels.continueLabel());
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.doneLabel());
        assembler.clearSubcontextType();
        exitLoop(node);
    }
}
