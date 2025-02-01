package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstArrayAccessVisitor;
import info.teksol.mc.generated.ast.visitors.AstBuiltInIdentifierVisitor;
import info.teksol.mc.generated.ast.visitors.AstIdentifierVisitor;
import info.teksol.mc.generated.ast.visitors.AstSubarrayVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ExternalArray;
import info.teksol.mc.mindcode.compiler.generation.variables.ExternalVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class IdentifiersBuilder extends AbstractBuilder implements
        AstArrayAccessVisitor<ValueStore>,
        AstBuiltInIdentifierVisitor<ValueStore>,
        AstIdentifierVisitor<ValueStore>,
        AstSubarrayVisitor<ValueStore>
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

    @Override
    public ValueStore visitSubarray(AstSubarray node) {
        ValueStore valueStore = evaluate(node.getArray());
        return switch (valueStore) {
            case LogicVariable memory   -> memorySubarrayAccess(node, memory);
            case ArrayStore<?> array    -> storeSubarrayAccess(node, array);
            default -> throw new MindcodeInternalError("Unhandled valueStore type: " + valueStore);
        };
    }

    private ValueStore memoryArrayAccess(AstArrayAccess node, LogicVariable memory) {
        LogicValue index = assembler.defensiveCopy(evaluate(node.getIndex()), TMP_VARIABLE);
        return new ExternalVariable(node.getArray().sourcePosition(), resolveMemory(node, memory), index, assembler.unprotectedTemp());
    }

    private ValueStore memorySubarrayAccess(AstSubarray node, LogicVariable memory) {
        IntRange range = parseSubarrayRange(node, DeclarationsBuilder.MAX_EXTERNAL_ARRAY_SIZE);
        if (range == null) {
            return LogicVariable.INVALID;
        }

        List<ExternalVariable> elements = IntStream.rangeClosed(range.min(), range.max())
                .mapToObj(index -> new ExternalVariable(node.sourcePosition(), memory,
                        LogicNumber.create(index), processor.nextTemp())).toList();

        return new ExternalArray(node.sourcePosition(), memory.getName(), memory, range.min(), elements);
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

    private ValueStore storeSubarrayAccess(AstSubarray node, ArrayStore<?> array) {
        IntRange range = parseSubarrayRange(node, array.getSize());
        return range == null ? LogicVariable.INVALID : array.subarray(node.sourcePosition(), range.min(), range.max() + 1);
    }

    private @Nullable IntRange parseSubarrayRange(AstSubarray node, int size) {
        int start = getIndex(node.getRange(), true);
        int end = getIndex(node.getRange(), false);
        if (start == Integer.MAX_VALUE || end == Integer.MAX_VALUE) {
            return null;
        }

        boolean error = false;
        if (start > end) {
            error(node, ERR.SUBARRAY_INVALID_RANGE);
            error = true;
        }
        if (start < 0 || start >= size) {
            error(node.getRange().getFirstValue(), ERR.SUBARRAY_INDEX_OUT_OF_BOUNDS, start, 0, size - 1);
            error = true;
        }
        if (end < 0 || end >= size) {
            error(node.getRange().getLastValue(), ERR.SUBARRAY_INDEX_OUT_OF_BOUNDS, end, 0, size - 1);
            error = true;
        }

        return error ? null : new IntRange(start, end);
    }

    private int getIndex(AstRange range, boolean first) {
        AstMindcodeNode element = first ? range.getFirstValue() : range.getLastValue();
        int correction = !first && range.isExclusive() ? -1 : 0;
        ValueStore value = evaluate(element);
        if (!(value instanceof LogicNumber number)) {
            error(element, ERR.SUBARRAY_MUTABLE_RANGE);
        } else if (!number.isInteger()) {
            error(element, ERR.SUBARRAY_NON_INTEGER_RANGE);
        } else {
            return number.getIntValue() + correction;
        }
        return Integer.MAX_VALUE;
    }
}
