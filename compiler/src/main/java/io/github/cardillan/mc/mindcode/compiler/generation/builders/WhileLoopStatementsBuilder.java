package io.github.cardillan.mc.mindcode.compiler.generation.builders;

import io.github.cardillan.mc.generated.ast.visitors.AstWhileLoopStatementVisitor;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstWhileLoopStatement;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WhileLoopStatementsBuilder extends AbstractLoopBuilder implements AstWhileLoopStatementVisitor<ValueStore> {

    public WhileLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitWhileLoopStatement(AstWhileLoopStatement node) {
        if (node.isEntryCondition()) {
            createWhileLoop(node);
        } else {
            createDoWhileLoop(node);
        }

        return LogicVoid.VOID;
    }

    private void createDoWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node, "do");

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of the loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        evaluateInvertedCondition(node.getCondition(), beginLabel, loopLabels.breakLabel());

        // Exit
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1);
        assembler.createLabel(loopLabels.breakLabel());
        assembler.clearSubcontextType();
        exitLoop(loopLabels);
    }

    private void createWhileLoop(AstWhileLoopStatement node) {
        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node, "while");

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        evaluateCondition(node.getCondition(), loopLabels.breakLabel(), AstContextType.SCBE_COND);

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visitBody(node.getBody());

        // Continue label:
        // The label needs to be part of the loop body so that it gets copied on loop unrolling.
        // The label cannot be placed in front of the entry condition, as the entry condition might
        // get moved to the end of the loop.
        assembler.createLabel(loopLabels.continueLabel());

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.breakLabel());
        assembler.clearSubcontextType();
        exitLoop(loopLabels);
    }
}
