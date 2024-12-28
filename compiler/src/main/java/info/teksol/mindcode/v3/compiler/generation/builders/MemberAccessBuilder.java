package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstMemberAccessVisitor;
import info.teksol.generated.ast.visitors.AstPropertyAccessVisitor;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;
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
        AstPropertyAccessVisitor<ValueStore> {

    public MemberAccessBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitMemberAccess(AstMemberAccess node) {
        LogicVariable target = resolveAnyVariable(node.getObject(), "Cannot invoke properties on this expression.");
        String propertyName = node.getMember().getName();
        if (validateProperty(propertyName)) {
            return new Property(target, propertyName, unprotectedTemp());
        } else {
            error(node.getMember(), "Unknown property '%s'.", propertyName);
            return LogicVariable.INVALID;
        }
    }

    private boolean validateProperty(String propertyName) {
        return processor.getOpcodeVariants().stream().anyMatch(v -> isPropertyOpcode(v, propertyName));
    }

    private boolean isPropertyOpcode(OpcodeVariant variant, String propertyName) {
        return variant.opcode() == Opcode.CONTROL
               && variant.namedParameters().getFirst().name().equals(propertyName)
               && variant.namedParameters().size() == 3;
    }

    @Override
    public ValueStore visitPropertyAccess(AstPropertyAccess node) {
        LogicVariable target = resolveAnyVariable(node.getObject(), "Cannot invoke properties on this expression.");
        final LogicVariable resultVar = nextNodeResultTemp();
        assembler.createSensor(resultVar, target, evaluate(node.getProperty()).getValue(assembler));
        return resultVar;
    }
}
