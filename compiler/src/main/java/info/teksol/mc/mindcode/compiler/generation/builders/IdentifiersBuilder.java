package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstArrayAccessVisitor;
import info.teksol.mc.generated.ast.visitors.AstBuiltInIdentifierVisitor;
import info.teksol.mc.generated.ast.visitors.AstIdentifierVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstArrayAccess;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBuiltInIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ExternalVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import org.jspecify.annotations.NullMarked;

import java.util.List;
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
        ValueStore valueStore = evaluate(node.getArray());
        return switch (valueStore) {
            case LogicVariable memory   -> memoryArrayAccess(node, memory);
            case ArrayStore<?> array    -> storeArrayAccess(node, array);
            default -> throw new MindcodeInternalError("Unhandled valueStore type: " + valueStore);
        };
    }

    @Override
    public ValueStore visitBuiltInIdentifier(AstBuiltInIdentifier node) {
        if (LVar.forName(node.getName()) == null) {
            warn(node, WARN.BUILT_IN_VARIABLE_NOT_RECOGNIZED, node.getName());
        }
        return LogicBuiltIn.create(processor, node.sourcePosition(), node.getName());
    }

    @Override
    public ValueStore visitIdentifier(AstIdentifier identifier) {
        return variables.resolveVariable(identifier, isLocalContext(), allowUndeclaredLinks())
                .withSourcePosition(identifier.sourcePosition());
    }

    private ValueStore memoryArrayAccess(AstArrayAccess node, LogicVariable memory) {
        LogicValue index = assembler.defensiveCopy(evaluate(node.getIndex()), TMP_VARIABLE);
        return new ExternalVariable(node.getArray().sourcePosition(), resolveMemory(node, memory), index, assembler.unprotectedTemp());
    }

    // TODO TYPES: type checking of the memory variable
    private LogicVariable resolveMemory(AstArrayAccess node, LogicVariable memory) {
        if (memoryExpressionTypes.contains(memory.getType()) || memory.isMainVariable()) {
            if (memory instanceof LogicParameter parameter && !memoryExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getArray(), ERR.ARRAY_PARAMETER_NOT_MEMORY, parameter.getName());
            }
            return memory;
        } else {
            error(node.getArray(), ERR.ARRAY_EXPRESSION_NOT_MEMORY, node.getArray().getName());
            return LogicVariable.INVALID;
        }
    }

    private ValueStore storeArrayAccess(AstArrayAccess node, ArrayStore<?> array) {
        ValueStore index = evaluate(node.getIndex());

        if (index instanceof LogicNumber number) {
            List<? extends ValueStore> elements = array.getElements();

            if (!number.isLong()) {
                assembler.error(number.sourcePosition(), ERR.ARRAY_NON_INTEGER_INDEX);
                return LogicVariable.INVALID;
            } else if (number.getIntValue() < 0 || number.getIntValue() >= elements.size()) {
                assembler.error(number.sourcePosition(), ERR.ARRAY_INDEX_OUT_OF_BOUNDS, elements.size() - 1);
                return LogicVariable.INVALID;
            } else {
                return elements.get(number.getIntValue());
            }
        }

        // The ArrayStore implementation is responsible for making a defensive copy of the index if needed
        return array.getElement(assembler, node, index);
    }
}
