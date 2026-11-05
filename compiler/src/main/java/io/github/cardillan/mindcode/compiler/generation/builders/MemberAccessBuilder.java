package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstMemberAccess;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstPropertyAccess;
import io.github.cardillan.mindcode.compiler.generation.AbstractCodeBuilder;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.variables.Property;
import io.github.cardillan.mindcode.compiler.generation.variables.StructuredValueStore;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.generated.ast.visitors.AstMemberAccessVisitor;
import io.github.cardillan.mindcode.generated.ast.visitors.AstPropertyAccessVisitor;
import io.github.cardillan.mindcode.logic.arguments.ArgumentType;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import io.github.cardillan.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mindcode.messages.ERR;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MemberAccessBuilder extends AbstractCodeBuilder implements
        AstMemberAccessVisitor<ValueStore>,
        AstPropertyAccessVisitor<ValueStore> {

    public MemberAccessBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitMemberAccess(AstMemberAccess node) {
        ValueStore target = resolveTarget(node.getObject(), true, false, ERR.CANNOT_INVOKE_PROPERTIES);

        if (target instanceof LogicVariable variable && (variable.isGlobalVariable() || variable.getType() == ArgumentType.BLOCK)) {
            StructuredValueStore structuredVariable = variables.findStructuredVariable(variable.getName());
            if (structuredVariable != null) {
                target = structuredVariable;
            }
        }

        String name = node.getMember().getName();
        if (target instanceof StructuredValueStore store) {
            ValueStore member = store.getMember(name);
            if (member != null) return member;

            if (store.getObject() == null) {
                error(node.getMember(), ERR.REMOTE_UNKNOWN_PARAMETER, store.getName(), name);
                return LogicVariable.INVALID;
            } else {
                target = store.getObject();
            }
        }

        if (validateProperty(name)) {
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
        ValueStore target = resolveTarget(node.getObject(), false, true, ERR.CANNOT_INVOKE_PROPERTIES);
        final LogicVariable resultVar = assembler.nextNodeResultTemp();
        assembler.createSensor(resultVar, target.getValue(assembler), evaluate(node.getProperty()).getValue(assembler));
        return resultVar;
    }
}
