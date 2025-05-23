package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.IntermediateValue;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.KeywordCategory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class DeclarationsBuilder extends AbstractBuilder implements
        AstAllocationVisitor<ValueStore>,
        AstAllocationsVisitor<ValueStore>,
        AstDirectiveSetVisitor<ValueStore>,
        AstDirectiveDeclareVisitor<ValueStore>,
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
            NAMED_COLOR_LITERAL,
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
    public ValueStore visitDirectiveSet(AstDirectiveSet node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitDirectiveDeclare(AstDirectiveDeclare directive) {
        String categoryName = directive.getCategory().getName();
        KeywordCategory category = KeywordCategory.byName(categoryName);
        if (category == null) {
            error(directive.getCategory(), ERR.DECLARE_UNKNOWN_CATEGORY, categoryName);
        } else if (category == KeywordCategory.linkedBlock) {
            for (AstMindcodeNode element : directive.getElements()) {
                if (element instanceof AstIdentifier identifier) {
                    processor.addBlockName(identifier.getName());
                } else {
                    error(element, ERR.DECLARE_BLOCK_NAME_EXPECTED);
                }
            }
        } else if (category == KeywordCategory.builtin) {
            for (AstMindcodeNode element : directive.getElements()) {
                if (element instanceof AstBuiltInIdentifier builtIn) {
                    processor.addBuiltin(builtIn.getName());
                } else {
                    error(element, ERR.DECLARE_BUILTIN_EXPECTED);
                }
            }
        } else {
            boolean reported = false;
            for (AstMindcodeNode element : directive.getElements()) {
                if (element instanceof AstKeyword keyword) {
                    if (!processor.addKeyword(category, keyword.getKeyword()) && !reported) {
                        error(directive.getCategory(), ERR.DECLARE_UNSUPPORTED_CATEGORY, categoryName);
                        reported = true;
                    }
                } else {
                    error(element, ERR.DECLARE_KEYWORD_EXPECTED);
                }
            }
        }

        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitDocComment(AstDocComment node) {
        // Ignored - processed elsewhere
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitFunctionDeclaration(AstFunctionDeclaration node) {
        if (node.isRemote()) {
            verifyMinimalRemoteTarget(node);
        }

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

    @Override
    public ValueStore visitRequireFile(AstRequireFile node) {
        if (!node.getProcessors().isEmpty()) {
            initializeRemoteProcessors(node);
        }

        // Requires have no value
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireLibrary(AstRequireLibrary node) {
        if (!node.getProcessors().isEmpty()) {
            initializeRemoteProcessors(node);
        }

        // Requires have no value
        return LogicVoid.VOID;
    }

    // Note: remote modules are not processed by code generator. Any variables declared `remote` encountered here
    //       are compiled as part of a remote processor code and are created as volatile variables.
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

                processArray(modifiers, specification, true, null);
            } else {
                processVariable(modifiers, specification);
            }
        }

        return LogicVoid.VOID;
    }

    private void initializeRemoteProcessors(AstRequire node) {
        verifyMinimalRemoteTarget(node);
        boolean reportRemoteErrors = true;

        // Assign remote function indexes
        AstModule module = getModule(node);
        callGraph.assignRemoteFunctionIndexes(f -> f.getModule() == module);

        List<LogicVariable> processors = node.getProcessors().stream().map(this::evaluateProcessor).toList();

        // If there's exactly one remote processor, import functions and variables to local namespace
        if (processors.size() == 1 && processors.getFirst().getType() == BLOCK) {
            createRemoteVariables(module, processors.getFirst(), true, null);
            reportRemoteErrors = false;

            // Initialize function parameters
            // Create function output variables
            callGraph.getFunctions().stream()
                    .filter(f -> f.getModule() == module && f.isUsed())
                    .forEach(function -> {
                        function.setupRemoteParameters(assembler, processors.getFirst());
                        Map<String, ValueStore> members = function.getParameters()
                                .stream()
                                .filter(FunctionParameter::isOutput)
                                .collect(Collectors.toMap(FunctionParameter::getName, p -> p));
                        StructuredValueStore variable = new StructuredValueStore(function.getSourcePosition(), function.getName(), members);
                        variables.registerRemoteCallStore(function.getDeclaration().getIdentifier(), variable);
                    });
        }

        // Remote signature
        String remoteSignature = createRemoteSignature(module);

        int index = 0;
        for (AstIdentifier identifier : node.getProcessors()) {
            LogicVariable processor = processors.get(index++);

            // Processor members
            Map<String, ValueStore> members = callGraph.getFunctions().stream()
                    .filter(f -> f.getModule() == module)
                    .collect(Collectors.toMap(MindcodeFunction::getName, f -> createFunctionOutputs(f, processor)));
            createRemoteVariables(module, processor, reportRemoteErrors, members);
            reportRemoteErrors = false;

            StructuredValueStore processorStructure = new StructuredValueStore(identifier.sourcePosition(), identifier.getName(), members);
            variables.registerStructuredVariable(identifier, processorStructure);

            // Generate guard code for processor
            if (processor.getType() == BLOCK) {
                LogicString initializedName = LogicVariable.REMOTE_SIGNATURE.getMlogString();
                LogicVariable tmp = assembler.unprotectedTemp();
                LogicLabel label = assembler.createNextLabel();
                assembler.createRead(tmp, processor, initializedName);
                assembler.createJump(label, Condition.NOT_EQUAL, tmp, LogicString.create(remoteSignature));
            }
        }
    }

    private LogicVariable evaluateProcessor(AstIdentifier identifier) {
        ValueStore value = processInLocalScope(() -> evaluate(identifier));
        if (value instanceof LogicVariable proc) {
            if (proc.isGlobalVariable() || proc.getType() == BLOCK) {
                return proc;
            }
            error(identifier, ERR.REMOTE_PROCESSOR_NOT_GLOBAL);
        } else {
            error(identifier, ERR.IDENTIFIER_EXPECTED);
        }

        return LogicVariable.INVALID;
    }

    private void createRemoteVariables(AstModule module, LogicVariable processor, boolean reportArrayErrors,
            @Nullable Map<String, ValueStore> structureMembers) {
        module.getChildren().stream()
                .filter(AstVariablesDeclaration.class::isInstance)
                .map(AstVariablesDeclaration.class::cast)
                .filter(n -> n.getModifiers().stream().anyMatch(m -> m.getModifier() == Modifier.REMOTE))
                .forEach(n -> visitRemoteVariablesDeclaration(module, n, processor, reportArrayErrors, structureMembers));
    }

    public void visitRemoteVariablesDeclaration(AstModule module, AstVariablesDeclaration node,
            LogicVariable processor, boolean reportArrayErrors, @Nullable Map<String, ValueStore> structureMembers) {
        Map<Modifier, Object> modifiers = getEffectiveModifiers(node);

        if (isLocalContext()) {
            node.getModifiers().forEach(this::verifyLocalContextModifiers);
        }

        for (AstVariableSpecification specification : node.getVariables()) {
            AstIdentifier identifier = specification.getIdentifier();
            String name = identifier.getName();

            if (specification.isArray()) {
                node.getModifiers().forEach(this::verifyArrayModifiers);
                if (isLocalContext()) {
                    error(specification, ERR.ARRAY_LOCAL);
                }

                if (structureMembers != null) {
                    int arraySize = getArraySize(modifiers, specification, reportArrayErrors);
                    InternalArray array = InternalArray.create(identifier, arraySize, true, processor);
                    structureMembers.put(name, array);
                } else {
                    processArray(modifiers, specification, reportArrayErrors, processor);
                }
            } else {
                RemoteVariable variable = new RemoteVariable(identifier.sourcePosition(), processor, name,
                        LogicVariable.global(identifier).getMlogString(), assembler.nextTemp(), false, false);

                if (structureMembers != null) {
                    structureMembers.put(name, variable);
                } else {
                    variables.registerRemoteVariable(identifier, variable);
                }
            }
        }
    }

    private StructuredValueStore createFunctionOutputs(MindcodeFunction function, LogicVariable processor) {
        List<FunctionParameter> parameters = function.createRemoteParameters(assembler, processor);
        Map<String, ValueStore> members = parameters.stream()
                .filter(FunctionParameter::isOutput)
                .collect(Collectors.toMap(FunctionParameter::getName, p -> p));
        return new StructuredValueStore(function.getSourcePosition(), function.getName(), members);
    }

    private void verifyLocalContextModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case EXTERNAL -> error(element, ERR.SCOPE_EXTERNAL_NOT_GLOBAL);
            case LINKED -> error(element, ERR.SCOPE_LINKED_NOT_GLOBAL);
            case REMOTE -> error(element, ERR.VARIABLE_LOCAL_CANNOT_BE_REMOTE);
            case VOLATILE -> error(element, ERR.VARIABLE_LOCAL_CANNOT_BE_VOLATILE);
        }
    }

    private int getArraySize(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification, boolean reportArrayErrors) {
        int declaredSize = getDeclaredArraySize(modifiers, specification);
        int initialSize = specification.getExpressions().size();

        if (declaredSize > 0 && initialSize > 0) {
            if (reportArrayErrors && initialSize != declaredSize) {
                error(specification, ERR.ARRAY_SIZE_MISMATCH);
            }
            declaredSize = initialSize;
        } else if (reportArrayErrors && declaredSize < 0 && initialSize == 0) {
            error(specification, ERR.ARRAY_SIZE_NOT_SPECIFIED);
        }

        if (declaredSize <= 0) {
            declaredSize = initialSize;
        }

        return declaredSize;
    }

    private void processArray(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification,
            boolean reportArrayErrors, @Nullable LogicVariable processor) {
        int declaredSize = getArraySize(modifiers, specification, reportArrayErrors);
        int initialSize = specification.getExpressions().size();

        List<ValueStore> initialValues = specification.getExpressions().stream()
                .map(node -> processInLocalScope(() -> evaluate(node)))
                .toList();

        if (modifiers.containsKey(Modifier.CONST)) {
            if (initialValues.isEmpty()) {
                error(specification, ERR.ARRAY_CONST_NOT_INITIALIZED);
            }

            initialValues.stream().filter(v -> !(v instanceof LogicValue value && isNonvolatileConstant(value)))
                    .forEach(v -> error(v, ERR.ARRAY_CONST_NOT_CONSTANT));
        }

        ArrayStore array = variables.createArray(specification.getIdentifier(), declaredSize, modifiers, initialValues, processor);

        if (!modifiers.containsKey(Modifier.CONST)) {
            for (int i = 0; i < initialSize; i++) {
                array.getElements().get(i).setValue(assembler, initialValues.get(i).getValue(assembler));
            }

            if (modifiers.containsKey(Modifier.REMOTE) && processor == null) {
                array.getElements().stream()
                        .filter(LogicVariable.class::isInstance)
                        .map(LogicVariable.class::cast)
                        .forEach(context::addRemoteVariable);
            }
        }
    }

    private void verifyArrayModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case CACHED -> error(element, ERR.ARRAY_CACHED);
            case LINKED -> error(element, ERR.ARRAY_LINKED);
            case NOINIT -> error(element, ERR.ARRAY_NOINIT);
        }
    }

    private int getDeclaredArraySize(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        AstExpression arraySize = specification.getArraySize();
        if (arraySize == null) return -1;
        int maxSize = modifiers.containsKey(Modifier.EXTERNAL) ? MAX_EXTERNAL_ARRAY_SIZE : MAX_INTERNAL_ARRAY_SIZE;

        ValueStore size = processInLocalScope(() -> evaluate(arraySize));
        if (!(size instanceof LogicReadable number && number.isNumericConstant())) {
            error(arraySize, ERR.ARRAY_MUTABLE_SIZE);
        } else if (!number.isInteger()) {
            error(arraySize, ERR.ARRAY_NON_INTEGER_SIZE);
        } else {
            int value = number.getIntValue();
            if (value > 0 && value <= maxSize) {
                return value;
            }

            error(arraySize, ERR.ARRAY_SIZE_OUTSIDE_RANGE, maxSize);
        }

        // Error
        return 0;
    }

    private void processVariable(Map<Modifier, @Nullable Object> modifiers, AstVariableSpecification specification) {
        ValueStore variable = modifiers.containsKey(Modifier.CONST) ? LogicVoid.VOID : createVariable(modifiers, specification);

        if (modifiers.containsKey(Modifier.LINKED)) {
            // Linked variables are initialized at creation
            return;
        }

        if (specification.getExpressions().isEmpty()) {
            if (modifiers.containsKey(Modifier.REMOTE)) {
                context.addRemoteVariable((LogicVariable) variable);
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

            AstExpression expression = specification.getExpressions().getFirst();

            // AstVariableDeclaration node doesn't enter the local scope, so that the identifier can be
            // resolved in the scope containing the node. However, the expression needs to be evaluated
            // in local scope, as all executable code must be placed there.
            ValueStore valueStore = processInLocalScope(() -> evaluate(expression));

            if (modifiers.containsKey(Modifier.CONST)) {
                if (valueStore instanceof LogicValue value && isNonvolatileConstant(value) || valueStore instanceof FormattableContent
                        || valueStore instanceof LogicKeyword) {
                    variables.createConstant(specification, valueStore);
                } else {
                    error(expression, ERR.EXPRESSION_NOT_CONSTANT_CONST, specification.getName());
                    variables.createConstant(specification, LogicNull.NULL);
                }
            } else {
                // Produces warning when the variable is a linked block
                ValueStore target = resolveLValue(specification.getIdentifier(), variable);
                target.setValue(assembler, valueStore.getValue(assembler));
            }
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
                if (modifier == Modifier.REMOTE) {
                    verifyMinimalRemoteTarget(astModifier);
                }
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
            if (!(value instanceof LogicReadable number && number.isNumericConstant())) {
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
            if (!(value instanceof LogicReadable number && number.isNumericConstant())) {
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
