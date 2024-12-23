package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.HeapTracker;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static info.teksol.mindcode.logic.ArgumentType.*;

@NullMarked
public class DeclarationsBuilder extends AbstractBuilder implements
        AstAllocationsVisitor<NodeValue>,
        AstAllocationVisitor<NodeValue>,
        AstConstantVisitor<NodeValue>,
        AstDirectiveSetVisitor<NodeValue>,
        AstDocCommentVisitor<NodeValue>,
        AstFunctionDeclarationVisitor<NodeValue>,
        AstParameterVisitor<NodeValue>,
        AstRequireFileVisitor<NodeValue>,
        AstRequireLibraryVisitor<NodeValue>
{
    private static final Set<ArgumentType> constantExpressionTypes = Set.of(
            NULL_LITERAL,
            BOOLEAN_LITERAL,
            NUMERIC_LITERAL,
            STRING_LITERAL,
            BUILT_IN,
            BLOCK);

    private static final Set<ArgumentType> memoryExpressionTypes = Set.of(
            GLOBAL_VARIABLE,
            PARAMETER,
            BLOCK);

    public DeclarationsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitAllocations(AstAllocations node) {
        visitStatements(node.getAllocations());
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitAllocation(AstAllocation node) {
        if (assembler.getAstContext().parent().contextType() != AstContextType.ROOT) {
            error(node, "Heap/stack allocation must be declared at the topmost level.");
            return LogicVoid.VOID;
        }

        switch (node.getType()) {
            case STACK -> {
                if (context.stackAllocation() != null) {
                    error(node, "Multiple stack allocation declarations.");
                } else {
                    final Allocation allocation = resolveAllocation(node);
                    context.setStackAllocation(node);
                    if (callGraph.containsRecursiveFunction()) {
                        assembler.createSet(LogicVariable.STACK_POINTER, LogicNumber.get(allocation.start));
                    }
                }
            }
            case HEAP -> {
                if (context.heapAllocation() != null) {
                    error(node, "Multiple heap allocation declarations.");
                } else {
                    final Allocation allocation = resolveAllocation(node);
                    context.setHeapAllocation(node);
                    variables.setHeapTracker(allocation.createTracker(context));
                }
            }
            default -> throw new MindcodeInternalError("Unknown allocation type: " + node.getType());
        }

        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitConstant(AstConstant node) {
        NodeValue nodeValue = visit(node.getValue());
        if (nodeValue instanceof LogicValue value && isNonvolatileConstant(value)) {
            variables.createConstant(node, value);
        } else {
            error(node.getValue(), "Value assigned to constant '%s' is not a constant expression.", node.getConstantName());
            variables.createConstant(node, LogicNull.NULL);
        }
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitDirectiveSet(AstDirectiveSet node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitDocComment(AstDocComment node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitFunctionDeclaration(AstFunctionDeclaration node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitParameter(AstParameter node) {
        NodeValue nodeValue = visit(node.getValue());
        LogicValue parameterValue;
        if (nodeValue instanceof LogicValue value && isNonvolatileConstant(value)) {
            parameterValue = value;
        } else {
            error(node.getValue(), "Value assigned to parameter '%s' is not a constant expression.", node.getParameterName());
            parameterValue = LogicNull.NULL;
        }

        NodeValue parameter = variables.createParameter(node, parameterValue);
        parameter.setValue(assembler, parameterValue);
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitRequireFile(AstRequireFile node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitRequireLibrary(AstRequireLibrary node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    private LogicVariable resolveMemory(AstAllocation node) {
        NodeValue memory = visit(node.getMemory());
        if (memory instanceof LogicVariable variable && (memoryExpressionTypes.contains(variable.getType()) || variable.isMainVariable())) {
            if (variable instanceof LogicParameter parameter && !memoryExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getMemory(), "Cannot use value assigned to parameter '%s' as a memory for heap or stack.", parameter.getName());
            }
            return variable;
        } else {
            error(node.getMemory(), "Cannot use '%s' as a memory for heap or stack.", node.getMemory().getName());
            return LogicVariable.INVALID;
        }
    }

    private Allocation resolveAllocation(AstAllocation node) {
        LogicVariable memory = resolveMemory(node);
        int defaultEndValue = memory.getType() == BLOCK
                              && processor.isBlockName(memory.getName())
                              && memory.getName().startsWith("bank") ? 512 : 64;
        int startHeapIndex = getIndex(node, true, 0);
        int endHeapIndex = getIndex(node, false, defaultEndValue) + 1;

        if (startHeapIndex >= endHeapIndex) {
            error(node, "Empty or invalid heap/stack memory range.");
        }

        return new Allocation(memory, startHeapIndex, endHeapIndex);
    }

    private int getIndex(AstAllocation node, boolean first, int defaultValue) {
        AstRange range = node.getRange();
        if (range != null) {
            AstMindcodeNode element = first ? range.getFirstValue() : range.getLastValue();
            int correction = !first && range.isExclusive() ? -1 : 0;
            NodeValue value = visit(element);
            if (!(value instanceof LogicNumber number)) {
                error(element, "Heap/stack declaration must specify constant range.");
            } else if (!number.isInteger()) {
                error(element, "Heap/stack declaration must specify integer range.");
            } else if (number.getIntValue() < 0 || number.getIntValue() + correction >= 512) {
                error(element, "Heap/stack memory index out of range (0 .. 512).");
            } else {
                return number.getIntValue() + correction;
            }
        }
        return defaultValue;
    }

    public boolean isNonvolatileConstant(LogicValue value) {
        return constantExpressionTypes.contains(value.getType()) && !value.isVolatile();
    }

    private record Allocation(LogicVariable memory, int start, int end) {
        public HeapTracker createTracker(CodeGeneratorContext context) {
            return HeapTracker.createTracker(context, memory, start, end);
        }
    }
}
