package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstBreakStatementVisitor;
import info.teksol.generated.ast.visitors.AstContinueStatementVisitor;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BreakContinueStatementsBuilder extends AbstractLoopBuilder implements
        AstBreakStatementVisitor<NodeValue>,
        AstContinueStatementVisitor<NodeValue>
{
    public BreakContinueStatementsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitBreakStatement(AstBreakStatement node) {
        final LogicLabel target = getBreakLabel(node.inputPosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitContinueStatement(AstContinueStatement node) {
        final LogicLabel target = getContinueLabel(node.inputPosition(), node);
        assembler.createJumpUnconditional(target);
        return LogicVoid.VOID;
    }
}
