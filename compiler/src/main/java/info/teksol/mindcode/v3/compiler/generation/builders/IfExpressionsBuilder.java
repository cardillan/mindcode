package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstIfExpressionVisitor;
import info.teksol.generated.ast.visitors.AstOperatorTernaryVisitor;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

@NullMarked
public class IfExpressionsBuilder extends AbstractBuilder implements
        AstIfExpressionVisitor<NodeValue>,
        AstOperatorTernaryVisitor<NodeValue>
{
    public IfExpressionsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public NodeValue visitIfExpression(AstIfExpression node) {
        // TODO In the past, elsif branches were emulated as nested else if expressions.
        //      Code optimizers might therefore have problems if we encoded elsif branches more effectively.
        //      Change the implementation to a more efficient one and update the optimizers.
        return visitOperatorTernary(repackageIfExpression(node));
    }

    @Override
    public NodeValue visitOperatorTernary(AstOperatorTernary node) {
        assembler.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        final NodeValue condition = variables.excludeVariablesFromTracking(() -> evaluate(node.getCondition()));

        final LogicVariable tmp = nextNodeResultTemp();
        final LogicLabel elseBranch = nextLabel();
        final LogicLabel endBranch = nextLabel();

        assembler.createJump(elseBranch, Condition.EQUAL, condition.getValue(assembler), FALSE);

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final NodeValue trueBranch = evaluate(node.getTrueBranch());
        assembler.createSet(tmp, trueBranch.getValue(assembler));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createJumpUnconditional(endBranch);
        assembler.createLabel(elseBranch);

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final NodeValue falseBranch = evaluate(node.getFalseBranch());
        assembler.createSet(tmp, falseBranch.getValue(assembler));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createLabel(endBranch);

        assembler.clearSubcontextType();
        return tmp;
    }

    private AstOperatorTernary repackageIfExpression(AstIfExpression node) {
        if (node.getIfBranches().isEmpty()) {
            throw new MindcodeInternalError("Invalid If expression structure (missing if branch).");
        }

        AstIfBranch last = node.getIfBranches().getLast();
        AstOperatorTernary result = new AstOperatorTernary(last.inputPosition(), last.getCondition(),
                repackageBody(last.getCondition().inputPosition(), last.getBody()),
                repackageBody(last.getCondition().inputPosition(), node.getElseBranch()));

        for (int i = node.getIfBranches().size() - 2; i >= 0; i--) {
            final AstIfBranch branch = node.getIfBranches().get(i);
            result = new AstOperatorTernary(branch.inputPosition(), branch.getCondition(),
                    repackageBody(branch.getCondition().inputPosition(), branch.getBody()),
                    result);
        }

        return result;
    }

    private AstExpression repackageBody(InputPosition position, List<AstMindcodeNode> expressions) {
        return (expressions.size() == 1 && expressions.getFirst() instanceof AstExpression exp)
                ? exp : new AstCodeBlock(expressions.isEmpty() ? position : expressions.getFirst().inputPosition(), expressions);
    }

}
