package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstArrayAccessVisitor;
import info.teksol.generated.ast.visitors.AstBuiltInIdentifierVisitor;
import info.teksol.generated.ast.visitors.AstIdentifierVisitor;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicBuiltIn;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstArrayAccess;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstBuiltInIdentifier;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.ExternalVariable;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static info.teksol.mindcode.logic.ArgumentType.*;

@NullMarked
public class IdentifiersBuilder extends AbstractBuilder implements
        AstArrayAccessVisitor<NodeValue>,
        AstBuiltInIdentifierVisitor<NodeValue>,
        AstIdentifierVisitor<NodeValue>
{
    private static final Set<ArgumentType> memoryExpressionTypes = Set.of(
            GLOBAL_VARIABLE,
            LOCAL_VARIABLE,
            PARAMETER,
            BLOCK);

    public IdentifiersBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitArrayAccess(AstArrayAccess node) {
        LogicVariable memory = resolveMemory(node);
        NodeValue index = visit(node.getIndex());
        return new ExternalVariable(memory, index.getValue(codeBuilder), standaloneTemp());
    }

    @Override
    public NodeValue visitBuiltInIdentifier(AstBuiltInIdentifier node) {
        return LogicBuiltIn.create(node.getName());
    }

    @Override
    public NodeValue visitIdentifier(AstIdentifier identifier) {
        return variables.resolveVariable(identifier);
    }

    private LogicVariable resolveMemory(AstArrayAccess node) {
        NodeValue memory = visit(node.getArray());
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
