package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.generated.ast.visitors.AstIfExpressionVisitor;
import info.teksol.generated.ast.visitors.AstOperatorTernaryVisitor;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

@NullMarked
public class IfExpressionsHandler extends BaseHandler implements
        AstIfExpressionVisitor<NodeValue>,
        AstOperatorTernaryVisitor<NodeValue>
{
    public IfExpressionsHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context, mainNodeVisitor);
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
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        final NodeValue condition = variables.excludeVariablesFromNode(() -> visit(node.getCondition()));

        final LogicVariable tmp = nextNodeResultTemp();
        final LogicLabel elseBranch = processor.nextLabel();
        final LogicLabel endBranch = processor.nextLabel();

        codeBuilder.createJump(elseBranch, Condition.EQUAL, condition.getValue(codeBuilder), FALSE);

        codeBuilder.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final NodeValue trueBranch = visit(node.getTrueBranch());
        codeBuilder.createSet(tmp, trueBranch.getValue(codeBuilder));
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        codeBuilder.createJumpUnconditional(endBranch);
        codeBuilder.createLabel(elseBranch);

        codeBuilder.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final NodeValue falseBranch = visit(node.getFalseBranch());
        codeBuilder.createSet(tmp, falseBranch.getValue(codeBuilder));
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        codeBuilder.createLabel(endBranch);

        codeBuilder.clearSubcontextType();
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
