package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstIteratedForLoopStatementVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIteratedForLoopStatement;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

@NullMarked
public class IteratedForLoopStatementsBuilder extends AbstractLoopBuilder implements AstIteratedForLoopStatementVisitor<NodeValue> {

    public IteratedForLoopStatementsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitIteratedForLoopStatement(AstIteratedForLoopStatement node) {
        // Initialization
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);
        visitStatements(node.getInitialize());

        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        if (node.getCondition() != null) {
            final NodeValue condition = visit(node.getCondition());
            assembler.createJump(loopLabels.doneLabel(), Condition.EQUAL, condition.getValue(assembler), FALSE);
        }

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visitStatements(node.getBody());

        // Update
        assembler.createLabel(loopLabels.continueLabel());
        assembler.setSubcontextType(AstSubcontextType.UPDATE, LOOP_REPETITIONS);
        visitStatements(node.getUpdate());

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.doneLabel());
        assembler.clearSubcontextType();
        exitLoop(node);

        return LogicVoid.VOID;
    }
}
