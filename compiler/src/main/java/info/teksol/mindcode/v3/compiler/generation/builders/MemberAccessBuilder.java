package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstMemberAccessVisitor;
import info.teksol.generated.ast.visitors.AstPropertyAccessVisitor;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMemberAccess;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstPropertyAccess;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.Property;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MemberAccessBuilder extends AbstractBuilder implements
        AstMemberAccessVisitor<ValueStore>,
        AstPropertyAccessVisitor<ValueStore>
{

    public MemberAccessBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitMemberAccess(AstMemberAccess node) {
        ValueStore object = evaluate(node.getObject());

        if (object instanceof LogicVariable target) {
            return new Property(target, node.getMember().getName(), unprotectedTemp());
        } else {
            return LogicVariable.INVALID;
        }
    }

    @Override
    public ValueStore visitPropertyAccess(AstPropertyAccess node) {
        ValueStore target = evaluate(node.getObject());
        final LogicVariable resultVar = nextNodeResultTemp();

        assembler.createSensor(resultVar, target.getValue(assembler),
                evaluate(node.getProperty()).getValue(assembler));

        return resultVar;
    }
}
