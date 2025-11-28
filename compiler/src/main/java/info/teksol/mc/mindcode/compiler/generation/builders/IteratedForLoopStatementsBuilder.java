package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstIteratedForLoopStatementVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIteratedForLoopStatement;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IteratedForLoopStatementsBuilder extends AbstractLoopBuilder implements AstIteratedForLoopStatementVisitor<ValueStore> {

    public IteratedForLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitIteratedForLoopStatement(AstIteratedForLoopStatement node) {
        // Initialization
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);
        visitBody(node.getInitialize());

        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        if (node.getCondition() != null) {
            evaluateCondition(node.getCondition(), loopLabels.breakLabel(), AstContextType.SCBE_COND);
        }

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of the loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Update
        assembler.setSubcontextType(AstSubcontextType.UPDATE, LOOP_REPETITIONS);
        visitBody(node.getUpdate());

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, LOOP_REPETITIONS);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.breakLabel());
        assembler.clearSubcontextType();
        exitLoop(loopLabels);

        return LogicVoid.VOID;
    }
}
