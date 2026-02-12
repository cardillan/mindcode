package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstLoopStatementVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLoopStatement;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
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
