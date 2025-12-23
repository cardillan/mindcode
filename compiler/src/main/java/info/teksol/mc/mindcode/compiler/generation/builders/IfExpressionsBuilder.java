package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstOperatorTernaryVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorTernary;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
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
