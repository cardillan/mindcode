package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.visitors.AstIfExpressionVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorTernaryVisitor;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.LogicBoolean.FALSE;

@NullMarked
public class IfExpressionsBuilder extends AbstractBuilder implements
        AstIfExpressionVisitor<ValueStore>,
        AstOperatorTernaryVisitor<ValueStore>
{
    public IfExpressionsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitIfExpression(AstIfExpression node) {
        // TODO In the past, elsif branches were emulated as nested else if expressions.
        //      Code optimizers might therefore have problems if we encoded elsif branches more effectively.
        //      Change the implementation to a more efficient one and update the optimizers.
        return visitOperatorTernary(repackageIfExpression(node));
    }

    @Override
    public ValueStore visitOperatorTernary(AstOperatorTernary node) {
        assembler.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        final ValueStore condition = variables.excludeVariablesFromTracking(() -> evaluate(node.getCondition()));

        final LogicVariable tmp = nextNodeResultTemp();
        final LogicLabel elseBranch = nextLabel();
        final LogicLabel endBranch = nextLabel();

        assembler.createJump(elseBranch, Condition.EQUAL, condition.getValue(assembler), FALSE);

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final ValueStore trueBranch = evaluate(node.getTrueBranch());
        assembler.createSet(tmp, handleVoid(trueBranch.getValue(assembler)));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createJumpUnconditional(endBranch);
        assembler.createLabel(elseBranch);

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final ValueStore falseBranch = evaluate(node.getFalseBranch());
        assembler.createSet(tmp, handleVoid(falseBranch.getValue(assembler)));
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
        AstOperatorTernary result = new AstOperatorTernary(last.sourcePosition(), last.getCondition(),
                repackageBody(last.getCondition().sourcePosition(), last.getBody()),
                repackageBody(last.getCondition().sourcePosition(), node.getElseBranch()));

        for (int i = node.getIfBranches().size() - 2; i >= 0; i--) {
            final AstIfBranch branch = node.getIfBranches().get(i);
            result = new AstOperatorTernary(branch.sourcePosition(), branch.getCondition(),
                    repackageBody(branch.getCondition().sourcePosition(), branch.getBody()),
                    result);
        }

        return result;
    }

    private AstMindcodeNode repackageBody(SourcePosition position, List<AstMindcodeNode> expressions) {
        return (expressions.size() == 1 && expressions.getFirst() instanceof AstExpression exp)
                ? exp : new AstStatementList(expressions.isEmpty() ? position : expressions.getFirst().sourcePosition(), expressions);
    }

    // Some constructs may produce VOID, but we want if statement branches to default to null,
    // because VOID gets mishandled in optimization
    // TODO Remove after introducing types, as if expressions will get a type, possibly VOID
    private LogicValue handleVoid(LogicValue value) {
        return value == LogicVoid.VOID ? LogicNull.NULL : value;
    }
}
