package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstBreakStatement;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstContinueStatement;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.generated.ast.visitors.AstBreakStatementVisitor;
import io.github.cardillan.mindcode.generated.ast.visitors.AstContinueStatementVisitor;
import io.github.cardillan.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mindcode.logic.arguments.LogicVoid;
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
        LogicLabel target = getBreakLabel(node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitContinueStatement(AstContinueStatement node) {
        LogicLabel target = getContinueLabel(node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }
}
