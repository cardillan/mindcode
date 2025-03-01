package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.evaluator.IntermediateValue;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class DeclarationsBuilder extends AbstractBuilder implements
        AstAllocationVisitor<ValueStore>,
        AstAllocationsVisitor<ValueStore>,
        AstConstantVisitor<ValueStore>,
        AstDirectiveSetVisitor<ValueStore>,
        AstDocCommentVisitor<ValueStore>,
        AstFunctionDeclarationVisitor<ValueStore>,
        AstModuleDeclarationVisitor<ValueStore>,
        AstParameterVisitor<ValueStore>,
        AstRequireFileVisitor<ValueStore>,
        AstRequireLibraryVisitor<ValueStore>,
        AstVariablesDeclarationVisitor<ValueStore> {
    public static final int MAX_INTERNAL_ARRAY_SIZE = 250;
    public static final int MAX_EXTERNAL_ARRAY_SIZE = 2048;

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

    private static final Object EMPTY_PARAMETRIZATION = new Object();

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
                    error(node, ERR.ALLOCATION_MULTIPLE_STACK);
                } else {
                    final Allocation allocation = resolveExternalStorage(node);
                    context.stackTracker().setStackMemory(allocation.memory);
                    if (callGraph.containsRecursiveFunction()) {
                        assembler.createSet(LogicVariable.STACK_POINTER,
                                LogicNumber.create(node.sourcePosition(), allocation.start));
                    }
                }
            }
            case HEAP -> {
                if (context.heapAllocation() != null) {
                    error(node, ERR.ALLOCATION_MULTIPLE_HEAP);
                } else {
                    final Allocation allocation = resolveExternalStorage(node);
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
        if (valueStore instanceof LogicValue value && isNonvolatileConstant(value) || valueStore instanceof FormattableContent) {
            variables.createConstant(node, valueStore);
        } else {
            error(node.getValue(), ERR.EXPRESSION_NOT_CONSTANT_CONST, node.getConstantName());
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
    public ValueStore visitModuleDeclaration(AstModuleDeclaration node) {
        // Module declarations are processed out of line
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitParameter(AstParameter node) {
        if (processor.isBlockName(node.getParameterName())) {
            error(node.getName(), ERR.VARIABLE_NAME_RESERVED_FOR_LINKS, node.getParameterName());
        }

        ValueStore valueStore = evaluate(node.getValue());
        LogicValue parameterValue;
        if (valueStore instanceof IntermediateValue value) {
            error(node, ERR.LITERAL_NO_VALID_REPRESENTATION_PARAM,
                    node.getParameterName(), value.getLiteral());
            parameterValue = LogicNull.NULL;
        } else if (valueStore instanceof LogicValue value && isNonvolatileConstant(value)) {
            parameterValue = value;
        } else {
            error(node.getValue(), ERR.EXPRESSION_NOT_CONSTANT_PARAM, node.getParameterName());
            parameterValue = LogicNull.NULL;
        }

        ValueStore parameter = variables.createParameter(node, parameterValue);
        parameter.setValue(assembler, parameterValue);
        return LogicVoid.VOID;
    }

    private void initializeRemoteProcessor(AstRequire node) {
        assert node.getProcessor() != null;
        LogicVariable processor = (LogicVariable) evaluate(node.getProcessor());

        // Generate guard code for processor
        LogicString mainProcessorMlog = LogicVariable.MAIN_PROCESSOR.getMlogString();
        LogicVariable tmp = assembler.unprotectedTemp();
        LogicLabel label = assembler.createNextLabel();
        assembler.createRead(tmp, processor, mainProcessorMlog);
        assembler.createJump(label, Condition.EQUAL, tmp, LogicNull.NULL);

        assembler.createWrite(LogicBuiltIn.THIS, processor, mainProcessorMlog);

        List<LogicVariable> variables = new ArrayList<>();

        // Initialize function addresses and function parameters
        // Create function output variables
        callGraph.getFunctions().stream()
                .filter(f -> f.getModule().getRemoteProcessor() == node.getProcessor() && f.isUsed())
                .forEach(function -> {
                    function.createRemoteParameters(assembler, processor);
                    LogicVariable v = LogicVariable.fnAddress(function);
                    assembler.createRead(v, processor, v.getMlogString());
                    variables.add(LogicVariable.fnFinished(function));
                    if (!function.isVoid()) {
                        variables.add(LogicVariable.fnRetVal(function));
                    }
                    function.getLocalParameters().stream()
                            .filter(FunctionParameter::isOutput)
                            .map(LogicVariable.class::cast)
                            .forEach(variables::add);
                });

        assembler.createVariables(variables);
    }

    @Override
    public ValueStore visitRequireFile(AstRequireFile node) {
        // When a processor is part of the declaration, evaluate it.
        // It creates the variable representing the processor, if not already present.
        if (node.getProcessor() != null) {
            initializeRemoteProcessor(node);
        }

        // Requires have no value
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireLibrary(AstRequireLibrary node) {
        // When a processor is part of the declaration, evaluate it.
        // It creates the variable representing the processor, if not already present.
        if (node.getProcessor() != null) {
            initializeRemoteProcessor(node);
        }

        // Requires have no value
        return LogicVoid.VOID;
    }

    // Note: remote modules are not processed by code generator. Variables declared `remote` are created
    //       as volatile variables.
    @Override
    public ValueStore visitVariablesDeclaration(AstVariablesDeclaration node) {
        Map<Modifier, Object> modifiers = getEffectiveModifiers(node);

        if (isLocalContext()) {
            node.getModifiers().forEach(this::verifyLocalContextModifiers);
        }

        for (AstVariableSpecification specification : node.getVariables()) {
            if (specification.isArray()) {
                node.getModifiers().forEach(this::verifyArrayModifiers);
                if (isLocalContext()) {
                    error(specification, ERR.ARRAY_LOCAL);
                }

                processArray(modifiers, specification);
            } else {
                processVariable(modifiers, specification);
            }
        }

        return LogicVoid.VOID;
    }

    private void verifyLocalContextModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case EXTERNAL -> error(element, ERR.SCOPE_EXTERNAL_NOT_GLOBAL);
            case LINKED -> error(element, ERR.SCOPE_LINKED_NOT_GLOBAL);
            case NOINIT -> error(element, ERR.VARIABLE_LOCAL_CANNOT_BE_NOINIT);
            case REMOTE -> error(element, ERR.VARIABLE_LOCAL_CANNOT_BE_REMOTE);
            case VOLATILE -> error(element, ERR.VARIABLE_LOCAL_CANNOT_BE_VOLATILE);
        }
    }

    private void processArray(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        int declaredSize = getDeclaredArraySize(modifiers, specification);
        int initialSize = specification.getExpressions().size();

        if (declaredSize > 0 && initialSize > 0) {
            if (initialSize != declaredSize) {
                error(specification, ERR.ARRAY_SIZE_MISMATCH);
            }
            declaredSize = initialSize;
        } else if (declaredSize < 0 && initialSize == 0) {
            error(specification, ERR.ARRAY_SIZE_NOT_SPECIFIED);
        }

        if (declaredSize <= 0) {
            declaredSize = initialSize;
        }

        ArrayStore<?> array = variables.createArray(specification.getIdentifier(), declaredSize, modifiers);

        for (int i = 0; i < initialSize; i++) {
            AstExpression initExpression = specification.getExpressions().get(i);
            ValueStore initValue = processInLocalScope(() -> evaluate(initExpression));
            array.getElements().get(i).setValue(assembler, initValue.getValue(assembler));
        }
    }

    private void verifyArrayModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case CACHED -> error(element, ERR.ARRAY_CACHED);
            case LINKED -> error(element, ERR.ARRAY_LINKED);
            case NOINIT -> error(element, ERR.ARRAY_NOINIT);
            case REMOTE -> error(element, ERR.ARRAY_REMOTE);
            case VOLATILE -> error(element, ERR.ARRAY_VOLATILE);
        }
    }

    private int getDeclaredArraySize(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        if (specification.getArraySize() == null) return -1;
        int maxSize = modifiers.containsKey(Modifier.EXTERNAL) ? MAX_EXTERNAL_ARRAY_SIZE : MAX_INTERNAL_ARRAY_SIZE;

        ValueStore size = processInLocalScope(() -> evaluate(specification.getArraySize()));
        if (!(size instanceof LogicNumber number)) {
            error(specification.getArraySize(), ERR.ARRAY_MUTABLE_SIZE);
        } else if (!number.isInteger()) {
            error(specification.getArraySize(), ERR.ARRAY_NON_INTEGER_SIZE);
        } else {
            int value = number.getIntValue();
            if (value > 0 && value <= maxSize) {
                return value;
            }

            error(specification.getArraySize(), ERR.ARRAY_SIZE_OUTSIDE_RANGE, maxSize);
        }

        // Error
        return 0;
    }

    private void processVariable(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        ValueStore variable = createVariable(modifiers, specification);

        if (modifiers.containsKey(Modifier.LINKED)) {
            // Linked variables are initialized at creation
            return;
        }

        if (specification.getExpressions().isEmpty()) {
            if (modifiers.containsKey(Modifier.REMOTE)) {
                error(specification, ERR.VARIABLE_REMOTE_MUST_BE_INITIALIZED);
            }

            if (!modifiers.containsKey(Modifier.NOINIT)) {
                // Initializes external cached variables by reading the value from memory block
                variable.initialize(assembler);
            }
        } else {
            if (specification.getExpressions().size() != 1) {
                // Shouldn't happen
                throw new MindcodeInternalError("Unexpected number of expressions: " + specification.getExpressions().size());
            }

            if (modifiers.containsKey(Modifier.NOINIT)) {
                error(specification, ERR.VARIABLE_NOINIT_CANNOT_BE_INITIALIZED);
            }

            // AstVariableDeclaration node doesn't enter the local scope, so that the identifier can be
            // resolved in the scope containing the node. However, the expression needs to be evaluated
            // in local scope, as all executable code must be placed there.
            ValueStore value = processInLocalScope(() -> evaluate(specification.getExpressions().getFirst()));
            // Produces warning when the variable is a linked block
            ValueStore target = resolveLValue(specification.getIdentifier(), variable);
            target.setValue(assembler, value.getValue(assembler));
        }
    }

    private ValueStore createVariable(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        if (modifiers.containsKey(Modifier.EXTERNAL) || modifiers.containsKey(Modifier.CACHED)) {
            return variables.createExternalVariable(specification.getIdentifier(), modifiers);
        } else if (modifiers.containsKey(Modifier.LINKED)) {
            return createLinkedVariable(modifiers, specification);
        } else if (modifiers.isEmpty() || modifiers.containsKey(Modifier.NOINIT)
                   || modifiers.containsKey(Modifier.VOLATILE) || modifiers.containsKey(Modifier.REMOTE)) {
            // Local variables need to be created within the parent node, as the current node is the
            // AstVariablesDeclaration node. If the variable was created within the current node, it
            // would fall out of scope when AstVariablesDeclaration node processing finishes.
            return variables.createVariable(isLocalContext(), specification.getIdentifier(), VariableScope.PARENT_NODE, modifiers);
        } else {
            throw new MindcodeInternalError("Unhandled combination of modifiers: " + modifiers);
        }
    }

    private LogicVariable createLinkedVariable(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        LogicVariable variable;

        if (specification.getExpressions().isEmpty()) {
            variable = variables.createLinkedVariable(specification.getIdentifier(), specification.getIdentifier());
        } else if (specification.getExpressions().size() == 1) {
            if (specification.getExpressions().getFirst() instanceof AstIdentifier linkedTo) {
                variable = variables.createLinkedVariable(specification.getIdentifier(), linkedTo);
            } else {
                error(specification.getExpressions().getFirst(), ERR.IDENTIFIER_EXPECTED);
                compile(specification.getExpressions().getFirst());
                variable = LogicVariable.INVALID;
            }
        } else {
            throw new MindcodeInternalError("Unexpected number of expressions: " + specification.getExpressions().size());
        }

        generateLinkGuard(variable, modifiers.containsKey(Modifier.NOINIT));
        return variable;
    }

    private Map<Modifier, Object> getEffectiveModifiers(AstVariablesDeclaration node) {
        Map<Modifier, Object> modifiers = new EnumMap<>(Modifier.class);

        for (AstVariableModifier astModifier : node.getModifiers()) {
            Modifier modifier = astModifier.getModifier();

            if (modifiers.containsKey(modifier)) {
                error(astModifier, ERR.VARIABLE_REPEATED_MODIFIER, modifier.name().toLowerCase());
            } else if (modifier.isCompatibleWith(modifiers.keySet())) {
                modifiers.put(modifier, createParametrization(astModifier.getParametrization()));
            } else {
                error(astModifier, ERR.VARIABLE_INCOMPATIBLE_MODIFIER, modifier.name().toLowerCase());
            }
        }

        if (modifiers.containsKey(Modifier.CACHED) && !modifiers.containsKey(Modifier.EXTERNAL)) {
            error(modifierElement(node, Modifier.CACHED), ERR.VARIABLE_MISSING_MODIFIER,
                    Modifier.CACHED.name().toLowerCase(), Modifier.EXTERNAL.name().toLowerCase());
        }

        return modifiers;
    }

    private Object createParametrization(@Nullable AstMindcodeNode node) {
        if (node instanceof ExternalStorage externalStorage) {
            return resolveExternalStorage(externalStorage).createTracker(context);
        } else {
            return EMPTY_PARAMETRIZATION;
        }
    }

    private SourceElement modifierElement(AstVariablesDeclaration node, Modifier modifier) {
        return node.getModifiers().stream()
                .filter(m -> m.getModifier() == modifier)
                .findFirst()
                .map(SourceElement.class::cast)
                .orElse(node);
    }

    private void generateLinkGuard(LogicVariable variable, boolean noinit) {
        if (variable.getType() == BLOCK && profile.isLinkedBlockGuards() && guardedBlockNames.add(variable.toMlog()) && !noinit) {
            LogicLabel label = assembler.nextLabel();
            assembler.createLabel(label);
            assembler.createJump(label, Condition.EQUAL, variable, LogicNull.NULL);
        }
    }

    private LogicVariable resolveMemory(ExternalStorage node) {
        ValueStore memory = evaluate(node.getMemory());
        if (memory instanceof LogicVariable variable && (memoryExpressionTypes.contains(variable.getType()) || variable.isMainVariable())) {
            if (variable instanceof LogicParameter parameter && !memoryExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getMemory(), ERR.EXT_STORAGE_INVALID_MEMORY_VALUE, parameter.getName());
            }
            return variable;
        } else {
            error(node.getMemory(), ERR.EXT_STORAGE_INVALID_MEMORY, node.getMemory().getName());
            return LogicVariable.INVALID;
        }
    }

    private Allocation resolveExternalStorage(ExternalStorage externalStorage) {
        LogicVariable memory = resolveMemory(externalStorage);
        int defaultEndValue = memory.getType() == BLOCK
                              && processor.isBlockName(memory.getName())
                              && memory.getName().startsWith("bank") ? 511 : 63;
        int startHeapIndex = getIndex(externalStorage, true, 0);
        int endHeapIndex = getIndex(externalStorage, false, defaultEndValue) + 1;

        if (startHeapIndex >= endHeapIndex) {
            error(externalStorage, ERR.EXT_STORAGE_INVALID_RANGE);
        }

        generateLinkGuard(memory, false);
        return new Allocation(memory, startHeapIndex, endHeapIndex);
    }

    private int getIndex(ExternalStorage node, boolean first, int defaultValue) {
        AstRange range = node.getRange();
        AstExpression startIndex = node.getStartIndex();
        if (startIndex != null) {
            if (!first) return defaultValue;

            ValueStore value = evaluate(startIndex);
            if (!(value instanceof LogicNumber number)) {
                error(startIndex, ERR.EXT_STORAGE_MUTABLE_INDEX);
            } else if (!number.isInteger()) {
                error(startIndex, ERR.EXT_STORAGE_NON_INTEGER_INDEX);
//            } else if (number.getIntValue() < 0 || number.getIntValue() >= 512) {
//                error(element, ERR.EXT_STORAGE_OUTSIDE_RANGE);
            } else {
                return number.getIntValue();
            }
        } else if (range != null) {
            AstMindcodeNode element = first ? range.getFirstValue() : range.getLastValue();
            int correction = !first && range.isExclusive() ? -1 : 0;
            ValueStore value = evaluate(element);
            if (!(value instanceof LogicNumber number)) {
                error(element, ERR.EXT_STORAGE_MUTABLE_RANGE);
            } else if (!number.isInteger()) {
                error(element, ERR.EXT_STORAGE_NON_INTEGER_RANGE);
//            } else if (number.getIntValue() < 0 || number.getIntValue() + correction >= 512) {
//                error(element, ERR.EXT_STORAGE_OUTSIDE_RANGE);
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
