package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.evaluator.IntermediateValue;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.HeapTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.VariableScope;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class DeclarationsBuilder extends AbstractBuilder implements
        AstAllocationVisitor<ValueStore>,
        AstAllocationsVisitor<ValueStore>,
        AstConstantVisitor<ValueStore>,
        AstDirectiveSetVisitor<ValueStore>,
        AstDocCommentVisitor<ValueStore>,
        AstFunctionDeclarationVisitor<ValueStore>,
        AstParameterVisitor<ValueStore>,
        AstRequireFileVisitor<ValueStore>,
        AstRequireLibraryVisitor<ValueStore>,
        AstVariablesDeclarationVisitor<ValueStore>
{
    private static final Set<ArgumentType> constantExpressionTypes = Set.of(
            NULL_LITERAL,
            BOOLEAN_LITERAL,
            COLOR_LITERAL,
            NUMERIC_LITERAL,
            STRING_LITERAL,
            BUILT_IN,
            BLOCK);

    private static final Set<ArgumentType> memoryExpressionTypes = Set.of(
            GLOBAL_VARIABLE,
            PARAMETER,
            BLOCK);

    private final Set<String> guardedBlockNames = new HashSet<>();

    public DeclarationsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitAllocations(AstAllocations node) {
        visitBody(node.getAllocations());
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitAllocation(AstAllocation node) {
        switch (node.getType()) {
            case STACK -> {
                if (context.stackTracker().isValid()) {
                    error(node, "Multiple stack allocation declarations.");
                } else {
                    final Allocation allocation = resolveAllocation(node);
                    context.stackTracker().setStackMemory(allocation.memory);
                    if (callGraph.containsRecursiveFunction()) {
                        assembler.createSet(LogicVariable.STACK_POINTER, LogicNumber.create(allocation.start));
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
    public ValueStore visitConstant(AstConstant node) {
        ValueStore valueStore = evaluate(node.getValue());
//        if (valueStore instanceof LogicNumber number && !processor.hasMlogRepresentation(number.getDoubleValue())) {
//            error(node, "Value assigned to constant '%s' (%s) does not have a valid mlog representation.",
//                    node.getConstantName(), number.toMlog());
//            variables.createConstant(node, LogicNull.NULL);
//        } else
        if (valueStore instanceof LogicValue value && isNonvolatileConstant(value) || valueStore instanceof FormattableContent) {
            variables.createConstant(node, valueStore);
        } else {
            error(node.getValue(), "Value assigned to constant '%s' is not a constant expression.", node.getConstantName());
            variables.createConstant(node, LogicNull.NULL);
        }
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitDirectiveSet(AstDirectiveSet node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitDocComment(AstDocComment node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitFunctionDeclaration(AstFunctionDeclaration node) {
        // Function declarations are processed out of line
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitParameter(AstParameter node) {
        if (processor.isBlockName(node.getParameterName())) {
            error(node.getName(), "Name '%s' is reserved for linked blocks.", node.getParameterName());
        }

        ValueStore valueStore = evaluate(node.getValue());
        LogicValue parameterValue;
        if (valueStore instanceof IntermediateValue value) {
            error(node, "Value assigned to parameter '%s' (%s) does not have a valid mlog representation.",
                    node.getParameterName(), value.getLiteral());
            parameterValue = LogicNull.NULL;
        }  else if (valueStore instanceof LogicValue value && isNonvolatileConstant(value)) {
            parameterValue = value;
        } else {
            error(node.getValue(), "Value assigned to parameter '%s' is not a constant expression.", node.getParameterName());
            parameterValue = LogicNull.NULL;
        }

        ValueStore parameter = variables.createParameter(node, parameterValue);
        parameter.setValue(assembler, parameterValue);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireFile(AstRequireFile node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireLibrary(AstRequireLibrary node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitVariablesDeclaration(AstVariablesDeclaration node) {
        Set<Modifier> modifiers = getEffectiveModifiers(node);

        if (isLocalContext()) {
            if (modifiers.contains(Modifier.EXTERNAL)) {
                error(node, "External variables must be declared in the global scope.");
            }
            if (modifiers.contains(Modifier.LINKED)) {
                error(node, "Linked blocks must be declared in the global scope.");
            }
            if (modifiers.contains(Modifier.NOINIT)) {
                error(node, "Local variable cannot be declared 'uninitialized'.");
            }
            if (modifiers.contains(Modifier.VOLATILE)) {
                error(node, "Local variable cannot be declared 'volatile'.");
            }
        }

        for (AstVariableSpecification specification : node.getVariables()) {
            ValueStore variable = createVariable(modifiers, specification);

            // LINKED variables initializations are handled separately
            if (specification.getExpression() != null && !modifiers.contains(Modifier.LINKED)) {
                if (modifiers.contains(Modifier.NOINIT)) {
                    error(node, "Variable declared as 'noinit' cannot be initialized.");
                }

                // AstVariableDeclaration node doesn't enter the local scope, so that the identifier can be
                // resolved in the scope containing the node. However, the expression needs to be evaluated
                // in local scope, as all executable code must be placed there.
                ValueStore value = processInLocalScope( () -> evaluate(specification.getExpression()));
                // Produces warning when the variable is a linked block
                ValueStore target = resolveLValue(specification.getIdentifier(), variable);
                target.setValue(assembler, value.getValue(assembler));
            } else if (!modifiers.contains(Modifier.NOINIT)) {
                variable.initialize(assembler);
            }
        }

        return LogicVoid.VOID;
    }

    private ValueStore createVariable(Set<Modifier> modifiers, AstVariableSpecification specification) {
        if (modifiers.contains(Modifier.EXTERNAL) || modifiers.contains(Modifier.CACHED)) {
            return variables.createExternalVariable(specification.getIdentifier(), modifiers);
        } else if (modifiers.contains(Modifier.LINKED)) {
            return createLinkedVariable(modifiers, specification);
        } else if (modifiers.isEmpty() || modifiers.contains(Modifier.NOINIT) || modifiers.contains(Modifier.VOLATILE)) {
            // Local variables need to be created within the parent node, as the current node is the
            // AstVariablesDeclaration node. If the variable was created within the current node, it
            // would fall out of scope when AstVariablesDeclaration node processing finishes.
            return variables.createVariable(isLocalContext(), specification.getIdentifier(), VariableScope.PARENT_NODE, modifiers);
        } else {
            throw new MindcodeInternalError("Unhandled combination of modifiers: " + modifiers);
        }
    }

    private ValueStore createLinkedVariable(Set<Modifier> modifiers, AstVariableSpecification specification) {
        LogicVariable variable = switch (specification.getExpression()) {
            case AstIdentifier linkedTo -> variables.createLinkedVariable(specification.getIdentifier(), linkedTo);
            case null -> variables.createLinkedVariable(specification.getIdentifier(), specification.getIdentifier());
            default -> {
                error(specification.getExpression(), "Identifier expected.");
                compile(specification.getExpression());
                yield LogicVariable.INVALID;
            }
        };

        generateLinkGuard(variable, modifiers.contains(Modifier.NOINIT));
        return variable;
    }

    private Set<Modifier> getEffectiveModifiers(AstVariablesDeclaration node) {
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

        for (AstVariableModifier astModifier : node.getModifiers()) {
            Modifier modifier = astModifier.getModifier();

            if (modifiers.contains(modifier)) {
                error(astModifier, "Repeated modifier '%s'.", modifier.name().toLowerCase());
            } else if (modifier.isCompatibleWith(modifiers)) {
                modifiers.add(modifier);
            } else {
                error(astModifier, "Modifier '%s' is incompatible with previous modifiers.",
                        modifier.name().toLowerCase());
            }
        }

        if (modifiers.contains(Modifier.CACHED) && !modifiers.contains(Modifier.EXTERNAL)) {
            error("Modifier '%s' used without '%s'.", Modifier.CACHED.name().toLowerCase(), Modifier.EXTERNAL.name().toLowerCase());
        }

        return modifiers;
    }

    private void generateLinkGuard(LogicVariable variable, boolean noinit) {
        if (variable.getType() == BLOCK && profile.isLinkedBlockGuards() && guardedBlockNames.add(variable.toMlog()) && !noinit) {
            LogicLabel label = nextLabel();
            assembler.createLabel(label);
            assembler.createJump(label,Condition.EQUAL, variable, LogicNull.NULL);
        }
    }

    private LogicVariable resolveMemory(AstAllocation node) {
        ValueStore memory = evaluate(node.getMemory());
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

        generateLinkGuard(memory, false);
        return new Allocation(memory, startHeapIndex, endHeapIndex);
    }

    private int getIndex(AstAllocation node, boolean first, int defaultValue) {
        AstRange range = node.getRange();
        if (range != null) {
            AstMindcodeNode element = first ? range.getFirstValue() : range.getLastValue();
            int correction = !first && range.isExclusive() ? -1 : 0;
            ValueStore value = evaluate(element);
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

    private boolean isNonvolatileConstant(LogicValue value) {
        return constantExpressionTypes.contains(value.getType()) && !value.isVolatile();
    }

    private record Allocation(LogicVariable memory, int start, int end) {
        public HeapTracker createTracker(CodeGeneratorContext context) {
            return HeapTracker.createTracker(context, memory, start, end);
        }
    }
}
