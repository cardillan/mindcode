package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstBreakStatementVisitor;
import info.teksol.generated.ast.visitors.AstContinueStatementVisitor;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
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
        final LogicLabel target = getBreakLabel(node.inputPosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitContinueStatement(AstContinueStatement node) {
        final LogicLabel target = getContinueLabel(node.inputPosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }
}
