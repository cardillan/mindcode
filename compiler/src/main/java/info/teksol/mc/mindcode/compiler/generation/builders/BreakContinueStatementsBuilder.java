package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstBreakStatementVisitor;
import info.teksol.mc.generated.ast.visitors.AstContinueStatementVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BreakContinueStatementsBuilder extends AbstractLoopBuilder implements
        AstBreakStatementVisitor<ValueStore>,
        AstContinueStatementVisitor<ValueStore>
{
    public BreakContinueStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitBreakStatement(AstBreakStatement node) {
        final LogicLabel target = getBreakLabel(node.sourcePosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitContinueStatement(AstContinueStatement node) {
        final LogicLabel target = getContinueLabel(node.sourcePosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }
}
