package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstArrayAccessVisitor;
import info.teksol.mc.generated.ast.visitors.AstBuiltInIdentifierVisitor;
import info.teksol.mc.generated.ast.visitors.AstIdentifierVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstArrayAccess;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBuiltInIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ExternalVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class IdentifiersBuilder extends AbstractBuilder implements
        AstArrayAccessVisitor<ValueStore>,
        AstBuiltInIdentifierVisitor<ValueStore>,
        AstIdentifierVisitor<ValueStore>
{
    private static final Set<ArgumentType> memoryExpressionTypes = Set.of(
            GLOBAL_VARIABLE,
            LOCAL_VARIABLE,
            PARAMETER,
            BLOCK);

    public IdentifiersBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitArrayAccess(AstArrayAccess node) {
        LogicVariable memory = resolveMemory(node);
        LogicValue index = defensiveCopy(evaluate(node.getIndex()), TMP_VARIABLE);
        return new ExternalVariable(node.getArray().sourcePosition(), memory, index, unprotectedTemp());
    }

    @Override
    public ValueStore visitBuiltInIdentifier(AstBuiltInIdentifier node) {
        if (LVar.forName(node.getName()) == null) {
            warn(node, "Built-in variable '%s' not recognized.", node.getName());
        }
        return LogicBuiltIn.create(processor, node.sourcePosition(), node.getName());
    }

    @Override
    public ValueStore visitIdentifier(AstIdentifier identifier) {
        return variables.resolveVariable(identifier, isLocalContext(), allowUndeclaredLinks());
    }

    private LogicVariable resolveMemory(AstArrayAccess node) {
        ValueStore memory = evaluate(node.getArray());
        if (memory instanceof LogicVariable variable && (memoryExpressionTypes.contains(variable.getType()) || variable.isMainVariable())) {
            if (variable instanceof LogicParameter parameter && !memoryExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getArray(), "Cannot use value assigned to parameter '%s' to access external memory.", parameter.getName());
            }
            return variable;
        } else {
            error(node.getArray(), "'%s' is not an external memory.", node.getArray().getName());
            return LogicVariable.INVALID;
        }
    }
}
