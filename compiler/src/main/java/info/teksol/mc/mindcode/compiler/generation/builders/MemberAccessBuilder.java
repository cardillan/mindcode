package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstMemberAccessVisitor;
import info.teksol.mc.generated.ast.visitors.AstPropertyAccessVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMemberAccess;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstPropertyAccess;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Property;
import info.teksol.mc.mindcode.compiler.generation.variables.StructuredValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
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
        ValueStore target = resolveTarget(node.getObject(), true, ERR.CANNOT_INVOKE_PROPERTIES);
        String name = node.getMember().getName();
        if (target instanceof StructuredValueStore store) {
            ValueStore member = store.getMember(name);
            if (member == null) {
                error(node.getMember(), ERR.REMOTE_UNKNOWN_PARAMETER, store.getName(), name);
                return LogicVariable.INVALID;
            } else {
                return member;
            }
        } else if (validateProperty(name)) {
            return new Property(node.sourcePosition(),
                    assembler.defensiveCopy(target, ArgumentType.TMP_VARIABLE),
                    name,
                    assembler.unprotectedTemp());
        } else {
            error(node.getMember(), ERR.PROPERTY_UNKNOWN, name);
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
        ValueStore target = resolveTarget(node.getObject(), false, ERR.CANNOT_INVOKE_PROPERTIES);
        final LogicVariable resultVar = assembler.nextNodeResultTemp();
        assembler.createSensor(resultVar, target.getValue(assembler), evaluate(node.getProperty()).getValue(assembler));
        return resultVar;
    }
}
