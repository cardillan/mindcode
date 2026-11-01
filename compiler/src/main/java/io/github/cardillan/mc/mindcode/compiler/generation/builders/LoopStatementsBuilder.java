package io.github.cardillan.mc.mindcode.compiler.generation.builders;

import io.github.cardillan.mc.generated.ast.visitors.AstLoopStatementVisitor;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstLoopStatement;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LoopStatementsBuilder extends AbstractLoopBuilder implements AstLoopStatementVisitor<ValueStore> {

    public LoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitLoopStatement(AstLoopStatement node) {
        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node, "loop");

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, LOOP_REPETITIONS);
        assembler.createLabel(beginLabel);
        visitBody(node.getBody());

        // Unfortunately, this needs to be here due to DFO. Jump Threading will fix it.
        assembler.createLabel(loopLabels.continueLabel());

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
