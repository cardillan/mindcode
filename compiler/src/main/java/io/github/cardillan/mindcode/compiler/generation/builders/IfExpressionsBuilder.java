package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstOperatorTernary;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.generation.AbstractCodeBuilder;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.generated.ast.visitors.AstOperatorTernaryVisitor;
import io.github.cardillan.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IfExpressionsBuilder extends AbstractCodeBuilder implements
        AstOperatorTernaryVisitor<ValueStore>
{
    public IfExpressionsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitOperatorTernary(AstOperatorTernary node) {
        final LogicLabel falseLabel = assembler.nextLabel();
        final LogicLabel endLabel = assembler.nextLabel();

        assembler.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        evaluateCondition(node.getCondition(), falseLabel, AstContextType.SCBE_COND);

        final LogicVariable tmp = assembler.nextNodeResultTemp();

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final ValueStore trueBranch = evaluate(node.getTrueBranch());
        assembler.createSet(tmp, handleVoid(trueBranch.getValue(assembler)));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createJumpUnconditional(endLabel);

        assembler.createLabel(falseLabel);
        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        final ValueStore falseBranch = evaluate(node.getFalseBranch());
        assembler.createSet(tmp, handleVoid(falseBranch.getValue(assembler)));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createLabel(endLabel);

        assembler.clearSubcontextType();
        return tmp;
    }

    // Some constructs may produce VOID, but we want if statement branches to default to null,
    // because VOID gets mishandled in optimization
    // TODO Remove after introducing types, as `if` expressions will get a type, possibly VOID
    private LogicValue handleVoid(LogicValue value) {
        return value == LogicVoid.VOID ? LogicNull.NULL : value;
    }
}
