package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.Globals;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.IntermediateValue;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.opcodes.KeywordCategory;
import info.teksol.mc.profile.SyntacticMode;
import info.teksol.mc.profile.options.Target;
import info.teksol.mc.util.StringUtils;
import info.teksol.mc.util.Tuple2;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.Modifier.*;
import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.*;

@NullMarked
public class DeclarationsBuilder extends AbstractCodeBuilder implements
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

    private static final Set<ArgumentType> blockExpressionTypes = Set.of(
            GLOBAL_VARIABLE,
            PROGRAM_PARAMETER,
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
                if (context.stackTracker().externalStack()) {
                    error(node, ERR.ALLOCATION_MULTIPLE_STACK);
                } else {
                    final Allocation allocation = resolveExternalStorage(node);
                    context.stackTracker().setStackMemory(allocation.memory, allocation.start, allocation.end);
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
        } else if (category == KeywordCategory.color) {
            for (AstMindcodeNode element : directive.getElements()) {
                if (element instanceof AstIdentifier name) {
                    processor.addColorName(name.getName());
                    processor.addBuiltin("@" + name.getName());
                } else {
                    error(element, ERR.DECLARE_COLOR_NAME_EXPECTED);
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
        // Function declarations are processed out of line
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitModuleDeclaration(AstModuleDeclaration node) {
        if (node.getProfile().getSyntacticMode() != SyntacticMode.STRICT) {
            error(node, ERR.MODULE_STRICT_MODE_REQUIRED);
        }

        Target moduleTarget = node.getProfile().getTarget();
        Target globalTarget = context.globalCompilerProfile().getTarget();
        if (!moduleTarget.isCompatibleWith(globalTarget)) {
            error(node, ERR.MODULE_TARGET_INCOMPATIBLE, moduleTarget.targetName(), globalTarget.targetName());
        }

        // Module declarations are processed out of line
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitParameter(AstParameter node) {
        if (processor.isBlockName(node.getParameterName())) {
            error(node.getName(), ERR.VARIABLE_NAME_RESERVED_FOR_LINKS, node.getParameterName());
        }

        ValueStore valueStore = processInLocalScope(() -> evaluate(node.getValue()));
        LogicValue parameterValue;
        if (valueStore instanceof IntermediateValue value) {
            error(node, ERR.LITERAL_NO_VALID_REPRESENTATION_PARAM,
                    node.getParameterName(), value.getLiteral());
            parameterValue = LogicNull.NULL;
        } else if (valueStore instanceof LogicValue value && value.isConstantValue()) {
            parameterValue = value;
        } else {
            error(node.getValue(), ERR.EXPRESSION_NOT_CONSTANT_PARAM, node.getParameterName());
            parameterValue = LogicNull.NULL;
        }

        ValueStore parameter = variables.createProgramParameter(node, parameterValue);
        parameter.setValue(assembler, parameterValue);

        variables.removeUnresolvedGlobal(node.getParameterName());
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireFile(AstRequireFile node) {
        if (!node.getProcessors().isEmpty()) {
            initializeRemoteProcessors(node);
        }

        // The 'require' directive has no value
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitRequireLibrary(AstRequireLibrary node) {
        if (!node.getProcessors().isEmpty()) {
            initializeRemoteProcessors(node);
        }

        // The 'require' directive has no value
        return LogicVoid.VOID;
    }

    // Note: remote modules are not processed by code generator. Any variables declared `export` encountered here
    //       are compiled as part of a remote processor code and are created as volatile variables.
    @Override
    public ValueStore visitVariablesDeclaration(AstVariablesDeclaration node) {
        Modifiers modifiers = getEffectiveModifiers(node);

        for (AstVariableSpecification specification : node.getVariables()) {
            if (specification.getIdentifier().isIntrinsic()) {
                // Report error and do nothing
                error(specification.getIdentifier(), ERR.VARIABLE_INTRINSIC_IDENTIFIER, specification.getName());
            } else if (specification.isArray()) {
                Consumer<AstVariableModifier> validator = modifiers.contains(LINKED)
                        ? this::validateLinkedArrayModifiers : this::validateRegularArrayModifiers;
                node.getModifiers().forEach(validator);

                processArrayDeclaration(specification, modifiers, false);
            } else {
                processVariableDeclaration(specification, modifiers);
            }

            variables.removeUnresolvedGlobal(specification.getName());
        }

        return LogicVoid.VOID;
    }

    private void initializeRemoteProcessors(AstRequire node) {
        verifyMinimalRemoteTarget(node);

        // Assign remote function indexes
        AstModule module = getModule(node);
        callGraph.assignRemoteFunctionIndexes(f -> f.getModule() == module);

        List<LogicVariable> processors = node.getProcessors().stream().map(this::evaluateProcessor).toList();

        // Remote signature
        String remoteSignature = createRemoteSignature(module);

        int index = 0;
        for (AstIdentifier identifier : node.getProcessors()) {
            LogicVariable processor = processors.get(index);

            // Processor members
            Map<String, ValueStore> members = callGraph.getFunctions().stream()
                    .filter(f -> f.getModule() == module)
                    .collect(Collectors.toMap(MindcodeFunction::getName, f -> createFunctionOutputs(f, processor)));
            createRemoteVariables(module, processor, node.getProcessors().size() > 1, index > 0, members);

            StructuredValueStore processorStructure = new StructuredValueStore(identifier.sourcePosition(), processor, identifier.getName(), members);
            variables.registerStructuredVariable(identifier, processorStructure);

            // Generate guard code for the processor
            if (processor.getType() == BLOCK) {
                LogicString initializedName = LogicString.create(nameCreator.remoteSignature());
                LogicVariable tmp = assembler.unprotectedTemp();
                LogicLabel label = assembler.createNextLabel();
                assembler.createRead(tmp, processor, initializedName);
                assembler.createJump(label, Condition.NOT_EQUAL, tmp, LogicString.create(remoteSignature));
            }

            index++;
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

    private void createRemoteVariables(AstModule module, LogicVariable processor, boolean shared,
            boolean suppressMessages, Map<String, ValueStore> structureMembers) {
        module.getChildren().stream()
                .filter(AstVariablesDeclaration.class::isInstance)
                .map(AstVariablesDeclaration.class::cast)
                .filter(n -> n.getModifiers().stream().anyMatch(m -> m.getModifier() == EXPORT
                        || (m.getModifier() == REMOTE && m.getParametrization() == null)))  // DEPRECATED
                .forEach(n -> visitRemoteVariablesDeclaration(module, n, processor, shared, suppressMessages, structureMembers));
    }

    private void visitRemoteVariablesDeclaration(AstModule module, AstVariablesDeclaration node, LogicVariable processor,
            boolean shared, boolean suppressMessages, Map<String, ValueStore> structureMembers) {
        runWithMessageFilter(_ -> !suppressMessages, () -> {
            Modifiers modifiers = getEffectiveModifiers(node);

            for (AstVariableSpecification specification : node.getVariables()) {
                AstIdentifier identifier = specification.getIdentifier();
                String name = identifier.getName();

                if (specification.getIdentifier().isIntrinsic()) {
                    // Report error and do nothing
                    error(specification.getIdentifier(), ERR.VARIABLE_INTRINSIC_IDENTIFIER, specification.getName());
                } else if (specification.isArray()) {
                    node.getModifiers().forEach(this::validateRemoteArrayModifiers);
                    int arraySize = processArrayDeclaration(specification, modifiers, true);

                    ArrayNameCreator arrayNameCreator = variables.processArrayMlogModifier(modifiers, arraySize, nameCreator);
                    InternalArray array = InternalArray.create(this.processor, arrayNameCreator, null, identifier,
                            0, arraySize, true, true, processor, shared);
                    structureMembers.put(name, array);
                } else {
                    RemoteVariable variable = new RemoteVariable(identifier.sourcePosition(), processor, name,
                            nameCreator.remote(identifier), assembler.nextTemp(), false, false, false);

                    structureMembers.put(name, variable);
                }
            }
        });
    }

    private StructuredValueStore createFunctionOutputs(MindcodeFunction function, LogicVariable processor) {
        List<FunctionParameter> parameters = function.createRemoteParameters(assembler, processor);
        Map<String, ValueStore> members = parameters.stream()
                .filter(FunctionParameter::isOutput)
                .collect(Collectors.toMap(FunctionParameter::getName, p -> p));
        return new StructuredValueStore(function.getSourcePosition(), null, function.getName(), members);
    }

    private void validateLocalContextModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case EXPORT, EXTERNAL, GUARDED, LINKED, MLOG, REMOTE, VOLATILE -> error(element,
                    ERR.MODIFIER_REQUIRES_GLOBAL_SCOPE, element.getModifier().keyword());
        }
    }

    private boolean containsRange(List<AstExpression> expressions) {
        return expressions.stream().anyMatch(AstRange.class::isInstance);
    }

    private int getDeclaredArraySize(AstVariableSpecification specification, Modifiers modifiers) {
        AstExpression arraySize = specification.getArraySize();
        if (arraySize == null) return 0;
        int maxSize = modifiers.contains(EXTERNAL) ? Globals.MAX_EXTERNAL_ARRAY_SIZE : Globals.MAX_INTERNAL_ARRAY_SIZE;

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
        return -1;
    }

    private ValueStore createLinkedVariable(AstIdentifier identifier, Modifiers modifiers) {
        LogicVariable variable = variables.createLinkedVariable(identifier, modifiers, identifier);
        generateLinkGuard(variable, modifiers.contains(GUARDED));
        return variable;
    }

    private List<ValueStore> processInitialValuesLinked(AstVariableSpecification specification, Modifiers modifiers) {
        if (specification.getExpressions().isEmpty()) {
            error(specification, ERR.ARRAY_LINKED_NOT_INITIALIZED);
            return List.of();
        }

        if (containsRange(specification.getExpressions())) {
            if (specification.getExpressions().size() != 1 || !(specification.getExpressions().getFirst() instanceof AstRange range)) {
                throw new MindcodeInternalError("A single range is expected.");
            }

            // We know all the identifiers in the list are valid
            return createLinkedIdentifiersList(this, range).stream()
                    .map(identifier -> createLinkedVariable(identifier, modifiers)).toList();
        } else {
            return specification.getExpressions().stream()
                    .map(node -> {
                        if (getLinkIdentifier(this, node) instanceof AstIdentifier identifier) {
                            return createLinkedVariable(identifier, modifiers);
                        } else {
                            processInLocalScope(() -> evaluate(node));  // To report errors in expressions
                            return LogicVariable.INVALID;
                        }
                    }).toList();
        }
    }

    private List<ValueStore> processInitialArrayValuesConst(AstVariableSpecification specification) {
        if (specification.getExpressions().isEmpty()) {
            error(specification, ERR.ARRAY_CONST_NOT_INITIALIZED);
            return List.of();
        } else if (containsRange(specification.getExpressions())) {
            error(specification.getExpressions().getFirst(), ERR.ARRAY_INIT_INVALID_RANGE);
            return List.of();
        }

        return specification.getExpressions().stream()
                .map(node -> {
                    ValueStore value = processInLocalScope(() -> evaluate(node));
                    if (value.isConstantValue() && value.isMlogRepresentable()) {
                        return value;
                    } else {
                        error(node, ERR.ARRAY_CONST_NOT_CONSTANT);
                        return LogicVariable.INVALID;
                    }
                }).toList();
    }

    private List<ValueStore> processInitialValuesExpr(AstVariableSpecification specification) {
        if (specification.getExpressions().isEmpty()) {
            return List.of();
        } else if (containsRange(specification.getExpressions())) {
            error(specification.getExpressions().getFirst(), ERR.ARRAY_INIT_INVALID_RANGE);
            return List.of();
        }

        return specification.getExpressions().stream()
                .map(node -> processInLocalScope(() -> evaluate(node))).toList();
    }

    private int processArrayDeclaration(AstVariableSpecification specification, Modifiers modifiers, boolean remoteDeclaration) {
        int declaredSize = getDeclaredArraySize(specification, modifiers);

        List<ValueStore> initialValues =
                remoteDeclaration ? Collections.nCopies(specification.getExpressions().size(), LogicVariable.INVALID)
                        : modifiers.getMain() == LINKED ? processInitialValuesLinked(specification, modifiers)
                        : modifiers.getMain() == CONST ? processInitialArrayValuesConst(specification)
                        : processInitialValuesExpr(specification);

        int arraySize;
        if (declaredSize < 0) {
            // Error in declaration, already reported
            arraySize = 1;
        } else if (declaredSize == 0) {
            // No size in declaration
            if (initialValues.isEmpty()) {
                error(specification, ERR.ARRAY_SIZE_NOT_SPECIFIED);
                arraySize = 1;
            } else {
                int maxSize = modifiers.contains(EXTERNAL) ? Globals.MAX_EXTERNAL_ARRAY_SIZE : Globals.MAX_INTERNAL_ARRAY_SIZE;
                if (initialValues.size() > maxSize) {
                    error(specification, ERR.ARRAY_SIZE_OUTSIDE_RANGE, maxSize);
                    arraySize = maxSize;
                } else {
                    arraySize = initialValues.size();
                }
            }
        } else {
            if (!initialValues.isEmpty() && initialValues.size() != declaredSize) {
                error(specification, ERR.ARRAY_SIZE_MISMATCH);
            }
            arraySize = declaredSize;
        }

        // Skip actual array creation for remote declarations
        if (!remoteDeclaration) {
            ArrayStore array = variables.createArray(isLocalContext(), specification.getIdentifier(), modifiers, arraySize, initialValues);

            if (array.valid() && !modifiers.contains(CONST) && !modifiers.contains(LINKED)) {
                for (int i = 0; i < initialValues.size(); i++) {
                    array.getElements().get(i).setValue(assembler, initialValues.get(i).getValue(assembler));
                }

                if (modifiers.containsAny(EXPORT, VOLATILE)) {
                    array.getElements().stream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEach(context::addForcedVariable);
                }
            }
        }

        return arraySize;
    }

    private void validateRemoteArrayModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case CACHED, GUARDED, LINKED, NOINIT -> error(element,
                    ERR.REMOTE_ARRAY_UNSUPPORTED_MODIFIER, element.getModifier().keyword());
        }
    }

    private void validateRegularArrayModifiers(AstVariableModifier element) {
        switch (element.getModifier()) {
            case CACHED, GUARDED, LINKED, NOINIT -> error(element,
                    ERR.ARRAY_UNSUPPORTED_MODIFIER, element.getModifier().keyword());
        }
    }

    private void validateLinkedArrayModifiers(AstVariableModifier element) {
        if (element.getModifier() == CACHED) {
            error(element, ERR.ARRAY_UNSUPPORTED_MODIFIER, element.getModifier().keyword());
        }
    }

    private void processVariableDeclaration(AstVariableSpecification specification, Modifiers modifiers) {
        ValueStore variable = createVariable(specification, modifiers);

        if (modifiers.getMain() == LINKED) {
            // Linked variables are initialized at creation
            return;
        }

        if (variable instanceof LogicVariable logicVariable && modifiers.containsAny(EXPORT, VOLATILE)) {
            // Ensure to explicitly create all remote variables
            context.addForcedVariable(logicVariable);
        }

        if (specification.getExpressions().isEmpty()) {
            if (!modifiers.contains(NOINIT)) {
                // Initializes external cached variables by reading the value from the memory block
                variable.initialize(assembler);
            }
        } else {
            if (specification.getExpressions().size() != 1) {
                // Shouldn't happen
                throw new MindcodeInternalError("Unexpected number of expressions: " + specification.getExpressions().size());
            }

            if (modifiers.contains(NOINIT)) {
                error(specification, ERR.VARIABLE_NOINIT_CANNOT_BE_INITIALIZED);
            }

            AstExpression expression = specification.getExpressions().getFirst();

            // AstVariableDeclaration node doesn't enter the local scope, so that the identifier can be
            // resolved in the scope containing the node. However, the expression needs to be evaluated
            // in the local scope, as all executable code must be placed there.
            ValueStore valueStore = processInLocalScope(() -> evaluate(expression));

            if (modifiers.contains(CONST)) {
                if (valueStore.isConstantValue()) {
                    variables.createConstant(isLocalContext(), specification, valueStore);
                } else {
                    error(expression, ERR.EXPRESSION_NOT_CONSTANT_CONST, specification.getName());
                    variables.createConstant(isLocalContext(), specification, LogicNull.NULL);
                }
            } else {
                // Produces a warning when the variable is a linked block
                ValueStore target = resolveLValue(specification.getIdentifier(), variable);
                target.setValue(assembler, valueStore.getValue(assembler));
            }
        }
    }

    private ValueStore createVariable(AstVariableSpecification specification, Modifiers modifiers) {
        return switch (modifiers.getMain()) {
            // No variable is created for CONST
            case CONST -> LogicVoid.VOID;
            case LINKED -> createLinkedVariable(specification, modifiers);
            case EXTERNAL -> variables.createExternalVariable(specification.getIdentifier(), modifiers);

            // Local variables need to be created within the parent node, as the current node is the
            // AstVariablesDeclaration node. If the variable was created within the current node, it
            // would fall out of scope when AstVariablesDeclaration node processing finishes.
            case NONE, EXPORT, REMOTE ->
                    variables.createVariable(isLocalContext(), specification.getIdentifier(), VariableScope.PARENT_NODE, modifiers);

            default -> {
                throw new MindcodeInternalError("Unhandled combination of modifiers: " + modifiers);
            }
        };
    }

    private LogicVariable createLinkedVariable(AstVariableSpecification specification, Modifiers modifiers) {
        LogicVariable variable;

        if (specification.getExpressions().isEmpty()) {
            AstIdentifier identifier = specification.getIdentifier();
            variable = variables.createLinkedVariable(identifier, modifiers, identifier);
        } else if (specification.getExpressions().size() == 1) {
            if (specification.getExpressions().getFirst() instanceof AstIdentifier linkedTo) {
                // Is it symbolic?
                if (modifiers.getParameters(LINKED) instanceof String blockName) {
                    // Verify the name is compatible
                    String baseName = BlockType.getBaseLinkName(blockName);
                    if (!linkedTo.getName().startsWith(baseName) || !linkedTo.getName().substring(baseName.length()).matches("[1-9]\\d{0,8}")) {
                        error(linkedTo, ERR.LITERAL_LINK_TYPE_MISMATCH, linkedTo.getName(), blockName);
                    }
                    variable = variables.createLinkedVariable(specification.getIdentifier(), modifiers,
                            variables.hasSchematicLinks() ? specification.getIdentifier() : linkedTo);
                } else {
                    variable = variables.createLinkedVariable(specification.getIdentifier(), modifiers, linkedTo);
                }
            } else {
                error(specification.getExpressions().getFirst(), ERR.IDENTIFIER_EXPECTED);
                compile(specification.getExpressions().getFirst());
                variable = LogicVariable.INVALID;
            }
        } else {
            throw new MindcodeInternalError("Unexpected number of expressions: " + specification.getExpressions().size());
        }

        generateLinkGuard(variable, modifiers.contains(GUARDED));
        return variable;
    }

    private Modifiers getEffectiveModifiers(AstVariablesDeclaration node) {
        Map<Modifier, ModifierParametrization<?>> modifiers = new EnumMap<>(Modifier.class);

        EnumSet<Modifier> primaryModifiers = getPrimarySet();
        Modifier firstPrimary = NONE;
        for (AstVariableModifier astModifier : node.getModifiers()) {
            Modifier modifier = astModifier.getModifier();

            if (firstPrimary == NONE && primaryModifiers.contains(modifier)) {
                firstPrimary = modifier;
            }

            if (modifiers.containsKey(modifier)) {
                error(astModifier, ERR.VARIABLE_REPEATED_MODIFIER, modifier.keyword());
            } else {
                modifiers.put(modifier, this.processInLocalScope(() -> createParametrization(astModifier)));
            }
        }

        if (firstPrimary == REMOTE || firstPrimary == EXPORT) {
            verifyMinimalRemoteTarget(modifiers.get(firstPrimary).node());
        }

        primaryModifiers.retainAll(modifiers.keySet());
        if (primaryModifiers.size() > 1) {
            List<String> parts = primaryModifiers.stream().map(s -> "'" + s.keyword() + "'").toList();
            String keywords = StringUtils.joinUsingAnd(parts);

            for (Modifier modifier : primaryModifiers) {
                error(modifiers.get(modifier).node(), ERR.VARIABLE_INCOMPATIBLE_MODIFIERS, keywords);
            }
        }

        final Modifier mainModifier = firstPrimary;
        if (mainModifier == NONE) {
            modifiers.keySet().stream()
                    .filter(m -> !primaryModifiers.contains(m))
                    .filter(m -> !m.getRequirements().contains(mainModifier))
                    .forEach(m -> error(modifiers.get(m).node(), ERR.VARIABLE_MISSING_MODIFIER, m.keyword(),
                            StringUtils.joinUsingOr(m.getRequirements().stream().filter(k -> k != NONE)
                                    .map(k -> "'" + k.keyword() + "'").toList())));
        } else {
            modifiers.keySet().stream()
                    .filter(m -> !primaryModifiers.contains(m))
                    .filter(m -> !m.getRequirements().contains(mainModifier))
                    .forEach(m -> error(modifiers.get(m).node(), ERR.VARIABLE_INCOMPATIBLE_MODIFIER,
                            m.keyword(), mainModifier.keyword()));
        }

        if (firstPrimary == NONE && modifiers.containsKey(GUARDED)) {
            firstPrimary = LINKED;
            modifiers.put(LINKED, modifiers.get(GUARDED));
        }

        if (isLocalContext()) {
            node.getModifiers().forEach(this::validateLocalContextModifiers);
        }

        if (modifiers.containsKey(MLOG) && node.getVariables().size() > 1) {
            error(node.getVariables().get(1), ERR.VARIABLE_MULTIPLE_SPECIFICATIONS_MLOG);
        }

        return new Modifiers(firstPrimary, modifiers);
    }

    private ModifierParametrization<?> createParametrization(AstVariableModifier modifier) {
        return switch (modifier.getParametrization()) {
            case ExternalStorage externalStorage -> new ModifierParametrization<>(modifier,
                    resolveExternalStorage(externalStorage).createTracker(context));

            case AstRemoteParameters param -> new ModifierParametrization<>(modifier,
                    resolveProcessor(param));

            case AstMlogParameters param -> new ModifierParametrization<>(modifier,
                    new MlogSpecification(resolveMlogNames(param.getMlogNames())));

            case AstLinkedParameters param -> new ModifierParametrization<>(modifier,
                    resolveSymbolicLinkType(param.getType()));

            case null -> new ModifierParametrization<>(modifier, null);

            default -> throw new MindcodeInternalError("Unhandled parametrization: " + modifier.getParametrization());
        };
    }

    private @Nullable String resolveSymbolicLinkType(@Nullable AstBuiltInIdentifier identifier) {
        if (identifier == null) return null;

        String typeName = identifier.getName();
        String blockName = BlockType.getBaseLinkName(typeName);
        if (!processor.isBaseBlockName(blockName)) {
            if (processor.isValidBuiltIn(typeName)) {
                error(identifier, ERR.INVALID_LINKED_TYPE_SPEC, typeName);
            } else {
                warn(identifier, WARN.LINKED_UNKNOWN_TYPE_SPEC, typeName);
            }
        }

        return typeName;
    }

    private List<LogicArgument> resolveMlogNames(List<AstExpression> mlogNames) {
        List<LogicArgument> arguments = new ArrayList<>(mlogNames.size());
        for (AstExpression mlogName : mlogNames) {
            switch (evaluate(mlogName)) {
                case LogicString str -> arguments.add(str);
                case LogicKeyword kw -> arguments.add(kw);
                default -> {
                    error(mlogName, ERR.CONSTANT_STRING_OR_KEYWORD_REQUIRED);
                    arguments.add(LogicString.create(mlogName.sourcePosition(), ""));
                }
            }
        }
        return arguments;
    }

    private void generateLinkGuard(LogicVariable variable, boolean guarded) {
        if (variable.getType() == BLOCK && guardedBlockNames.add(variable.toMlog()) && guarded) {
            LogicLabel label = assembler.nextLabel();
            assembler.createLabel(label);
            assembler.createJump(label, Condition.EQUAL, variable, LogicNull.NULL);
        }
    }

    private LogicVariable resolveMemory(ExternalStorage node) {
        ValueStore memory = evaluate(node.getMemory());
        if (memory instanceof LogicVariable variable && (blockExpressionTypes.contains(variable.getType()) || variable.isMainVariable())) {
            if (variable instanceof LogicParameter parameter && !blockExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getMemory(), ERR.EXT_STORAGE_INVALID_MEMORY_VALUE, parameter.getName());
            }
            return variable;
        } else {
            error(node.getMemory(), ERR.EXT_STORAGE_INVALID_MEMORY, node.getMemory().getName());
            return LogicVariable.INVALID;
        }
    }

    private LogicVariable resolveProcessor(AstRemoteParameters node) {
        ValueStore memory = evaluate(node.getProcessor());
        if (memory instanceof LogicVariable variable && (blockExpressionTypes.contains(variable.getType()) || variable.isMainVariable())) {
            if (variable instanceof LogicParameter parameter && !blockExpressionTypes.contains(parameter.getValue().getType())) {
                error(node.getProcessor(), ERR.REMOTE_STORAGE_INVALID_PROCESSOR_VALUE, parameter.getName());
            }
            return variable;
        } else {
            error(node.getProcessor(), ERR.REMOTE_STORAGE_INVALID_PROCESSOR, node.getProcessor().getName());
            return LogicVariable.INVALID;
        }
    }

    private Allocation resolveExternalStorage(ExternalStorage externalStorage) {
        LogicVariable memory = resolveMemory(externalStorage);
        int defaultEndValue = memory.getType() == BLOCK
                && processor.isBlockName(memory.getName())
                && memory.getName().startsWith("bank") ? 511 : 63;
        int startHeapIndex = getIndex(externalStorage, true, 0);
        int endHeapIndex = getIndex(externalStorage, false,
                externalStorage.getStartIndex() == null ? defaultEndValue : startHeapIndex) + 1;

        if (startHeapIndex >= endHeapIndex) {
            error(externalStorage, ERR.EXT_STORAGE_INVALID_RANGE);
        }

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

    private record Allocation(LogicVariable memory, int start, int end) {
        public HeapTracker createTracker(CodeGeneratorContext context) {
            return HeapTracker.createTracker(context, memory, start, end);
        }
    }

    private static @Nullable AstIdentifier getLinkIdentifier(CompilerMessageEmitter messageEmitter, AstExpression value) {
        if (value instanceof AstIdentifier identifier) return identifier;
        messageEmitter.error(value, ERR.LINK_EXPECTED);
        return null;
    }

    private static @Nullable Tuple2<String, Integer> extractIndex(CompilerMessageEmitter messageEmitter, @Nullable AstIdentifier identifier) {
        if (identifier == null) return null;

        String name = identifier.getName();
        int index = name.length();
        while (index > 0 && Character.isDigit(name.charAt(index - 1))) index--;

        if (index > 0 && index < name.length()) {
            String base = name.substring(0, index);
            String number = name.substring(index);

            if (!number.startsWith("0")) {
                try {
                    // All is okay here: the parsed number can't be zero or negative
                    return new Tuple2<>(base, Integer.parseInt(number));
                } catch (NumberFormatException ex) {
                    // Ignore - will be reported as the link error
                }
            }
        }

        messageEmitter.error(identifier, ERR.LINK_EXPECTED);
        return null;
    }

    public static List<AstIdentifier> createLinkedIdentifiersList(CompilerMessageEmitter messageEmitter, AstRange range) {
        AstIdentifier firstId = getLinkIdentifier(messageEmitter, range.getFirstValue());
        AstIdentifier lastId = getLinkIdentifier(messageEmitter, range.getLastValue());
        Tuple2<String, Integer> first = extractIndex(messageEmitter, firstId);
        Tuple2<String, Integer> last = extractIndex(messageEmitter, lastId);
        if (first == null || last == null) return List.of();  // Error already reported

        if (first.e1().equals(last.e1())) {
            String base = first.e1();
            int start = first.e2();
            int end = last.e2() - (range.isExclusive() ? 1 : 0);
            if (start <= end) {
                List<AstIdentifier> list = new ArrayList<>(end - start + 1);
                for (int i = start; i <= end; i++) {
                    list.add((i < end ? firstId : lastId).withName(base + i));
                }
                return list;
            }
        }

        messageEmitter.error(range, ERR.ARRAY_LINKED_INVALID_RANGE);
        return List.of();
    }
}
