package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static info.teksol.mc.mindcode.compiler.Modifier.*;

/// This class resolves source code identifiers into variables represented by the ValueStore interface
/// and tracks temporary variables used within AST nodes for stack management.
///
/// Types of source code identifiers:
/// - processor variables
///   - global
///   - main/local
///   - function parameters
/// - external (memory-backed) variables
///
/// Features to be implemented:
/// - variable declarations
/// - namespaces
/// - arrays, records and other complex types
@NullMarked
public class Variables extends AbstractMessageEmitter {
    private final Set<AstMindcodeNode> reportedErrors = Collections.newSetFromMap(new IdentityHashMap<>());

    private final InstructionProcessor processor;
    private final NameCreator nameCreator;
    private final Map<String, ValueStore> globalVariables;
    private final Map<String, StructuredValueStore> structuredVariables = new HashMap<>();
    private final Deque<FunctionContext> contextStack = new ArrayDeque<>();
    private FunctionContext functionContext = new GlobalContext();

    private HeapTracker heapTracker;

    public Variables(VariablesContext context) {
        super(context.messageConsumer());
        processor = context.instructionProcessor();
        nameCreator = context.nameCreator();
        heapTracker = HeapTracker.createDefaultTracker(context);
        globalVariables = context.metadata().getIcons().createIconMapAsValueStore();
        GlobalCompilerProfile profile = context.globalCompilerProfile();
        putVariable("__MINDUSTRY_VERSION__", LogicString.create(profile.getProcessorVersion().mimexVersion));
        putVariable("__TARGET_MAJOR__", LogicNumber.create(profile.getProcessorVersion().major));
        putVariable("__TARGET_MINOR__", LogicNumber.create(profile.getProcessorVersion().minor));
        putVariable("__PROCESSOR_EDITION__", LogicString.create(profile.getProcessorEdition().editionName()));
    }

    public NameCreator nameCreator() {
        return nameCreator;
    }

    public List<LogicVariable> getVolatileVariables() {
        return globalVariables.values().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(LogicVariable::isVolatile).toList();
    }

    public void setHeapTracker(HeapTracker heapTracker) {
        this.heapTracker = heapTracker;
    }

    public boolean isVarargParameter(@Nullable AstExpression expression) {
        return expression instanceof AstIdentifier id && currentFunction().isVarargs(id.getName());
    }

    public MindcodeFunction currentFunction() {
        return functionContext.function();
    }

    public Collection<ValueStore> getActiveVariables() {
        return functionContext.getActiveVariables();
    }

    public LoopStack getLoopStack() {
        return functionContext.loopStack();
    }

    public List<FunctionArgument> getVarargs() {
        return functionContext.getVarargs();
    }

    private boolean isGlobalVariable(AstIdentifier identifier) {
        return identifier.isExternal() || processor.isGlobalName(identifier.getName());
    }

    private boolean isLinkedVariable(AstIdentifier identifier) {
        return processor.isBlockName(identifier.getName());
    }

    private @Nullable ValueStore putVariable(String name, ValueStore variable) {
        return globalVariables.put(name, variable);
    }

    private void putVariableIfAbsent(String name, ValueStore variable) {
        globalVariables.putIfAbsent(name, variable);
    }

    private void putStructuredVariable(String name, StructuredValueStore variable) {
        structuredVariables.put(name, variable);
    }

    private ValueStore registerGlobalVariable(AstIdentifier identifier, ValueStore variable) {
        ValueStore existing = putVariable(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of global variable (existing variable: %s, new variable: %s).",
                    existing, variable);
        }
        return variable;
    }

    public void registerRemoteCallStore(AstIdentifier identifier, ValueStore variable) {
        ValueStore existing = putVariable(identifier.getName(), variable);
        if (existing != null) {
            error(existing, ERR.VARIABLE_MULTIPLE_DECLARATIONS_REMOTE, identifier.getName());
        }
    }

    public void registerStructuredVariable(AstIdentifier identifier, StructuredValueStore structure) {
        putStructuredVariable(identifier.getName(), structure);
    }

    public ValueStore registerRemoteVariable(AstIdentifier identifier, ValueStore variable) {
        ValueStore existing = putVariable(identifier.getName(), variable);
        if (existing != null) {
            error(existing, ERR.VARIABLE_MULTIPLE_DECLARATIONS, identifier.getName());
        }
        return variable;
    }

    public void replaceFunctionVariable(AstIdentifier identifier, ValueStore variable) {
        functionContext.replaceFunctionVariable(identifier, variable);
    }

    /// Creates an implicit variable. Used in the relaxed syntax setting only. Analyzes the identifier to create
    ///  the correct variable type and put it into the correct variable list.
    ///
    /// @param identifier variable identifier
    /// @return ValueStore instance representing the variable
    private ValueStore createImplicitVariable(AstIdentifier identifier) {
        if (identifier.isExternal()) {
            return registerGlobalVariable(identifier, heapTracker.createVariable(identifier, Modifiers.EMPTY));
        } else if (isLinkedVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.block(identifier));
        } else if (isGlobalVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.global(identifier, nameCreator.global(identifier.getName())));
        } else {
            return functionContext.registerFunctionVariable(identifier, VariableScope.FUNCTION, false, true);
        }
    }

    /// Registers a constant. Reports possible name clashes.
    ///
    /// @param specification constant declaration to process
    /// @param value compile-time value associated with the specification
    public void createConstant(AstVariableSpecification specification, ValueStore value) {
        verifyGlobalDeclaration(specification, specification.getIdentifier());
        putVariableIfAbsent(specification.getName(), value);
    }

    /// Registers a parameter. Reports possible name clashes.
    ///
    /// Parameter is an mlog variable. The caller must initialize the variable.
    ///
    /// @param parameter parameter declaration to process.
    /// @param value value assigned to the parameter
    /// @return ValueStore instance representing the parameter's variable
    public ValueStore createParameter(AstParameter parameter, LogicValue value) {
        ValueStore result = verifyGlobalDeclaration(parameter, parameter.getName())
                ? LogicParameter.parameter(parameter.getName(), value)
                : LogicParameter.parameter(new AstIdentifier(SourcePosition.EMPTY, "invalid"), value);

        putVariableIfAbsent(parameter.getParameterName(), result);
        return result;
    }

    /// Registers a linked variable (implicitly global). Reports possible name clashes. Warns when the variable
    /// doesn't conform to linked variable names.
    ///
    /// @param identifier variable name
    /// @return ValueStore instance representing the linked variable
    public LogicVariable createLinkedVariable(AstIdentifier identifier, AstIdentifier linkedTo) {
        verifyGlobalDeclaration(identifier, identifier);

        if (!isLinkedVariable(linkedTo)) {
            warn(linkedTo, WARN.LINKED_VARIABLE_NOT_RECOGNIZED, linkedTo.getName());
        }

        LogicVariable result = LogicVariable.block(identifier, linkedTo);
        putVariableIfAbsent(identifier.getName(), result);
        return result;
    }

    private HeapTracker getHeapTracker(Modifiers modifiers) {
        return modifiers.getParameters(EXTERNAL) instanceof HeapTracker externalTracker ? externalTracker : heapTracker;
    }

    /// Registers an external variable in a given scope. Reports possible name clashes.
    ///
    /// @param identifier variable name
    /// @return ValueStore instance representing the created variable
    public ValueStore createExternalVariable(AstIdentifier identifier, Modifiers modifiers) {
        if (!verifyGlobalDeclaration(identifier, identifier)) {
            return LogicVariable.INVALID;
        }

        ValueStore result = getHeapTracker(modifiers).createVariable(identifier, modifiers);
        putVariableIfAbsent(identifier.getName(), result);
        return result;
    }

    /// Registers an array. Scope is always global.
    ///
    /// @return ValueStore instance representing the created variable
    public ArrayStore createArray(AstIdentifier identifier, Modifiers modifiers, int size, List<ValueStore> initialValues) {
        ArrayStore result;

        if (!verifyGlobalDeclaration(identifier, identifier)) {
            result = InternalArray.createInvalid(nameCreator, identifier, size);
        } else if (modifiers.contains(CONST)) {
            result = InternalArray.createConst(nameCreator, identifier, size, initialValues);
        } else if (modifiers.contains(EXTERNAL)) {
            result = getHeapTracker(modifiers).createArray(identifier, size);
        } else {
            boolean declaredRemote = modifiers.containsAny(REMOTE, EXPORT);
            boolean isVolatile = modifiers.contains(VOLATILE) || declaredRemote;
            LogicVariable storageProcessor = modifiers.getParameters(REMOTE) instanceof LogicVariable p ? p : null;
            ArrayNameCreator arrayNameCreator = processArrayMlogModifier(modifiers, size, nameCreator);
            result = InternalArray.create(arrayNameCreator, identifier, size, isVolatile, declaredRemote, storageProcessor, false);
        }

        putVariableIfAbsent(identifier.getName(), result);
        return result;
    }

    /// Registers a standard variable in a given scope. Reports possible name clashes.
    ///
    /// @param local      true if the variable should be registered in a local scope, false for the global scope
    /// @param identifier variable name
    /// @param scope      scope of the variable
    /// @param modifiers  declaration modifiers
    /// @return ValueStore instance representing the created variable
    public ValueStore createVariable(boolean local, AstIdentifier identifier, VariableScope scope, Modifiers modifiers) {
        String name = identifier.getName();

        if (local) {
            if (functionContext.variables().containsKey(name)) {
                error(identifier, ERR.VARIABLE_MULTIPLE_DECLARATIONS, name);
                return Objects.requireNonNull(functionContext.variables().get(identifier.getName()));
            }
            return functionContext.registerFunctionVariable(identifier, scope,
                    modifiers.contains(NOINIT), false);
        } else {
            if (globalVariables.containsKey(name)) {
                error(identifier, ERR.VARIABLE_MULTIPLE_DECLARATIONS, name);
                return Objects.requireNonNull(globalVariables.get(identifier.getName()));
            }

            String mlogName = processVariableMlogModifier(modifiers);

            if (modifiers.getParameters(REMOTE) instanceof LogicVariable storageProcessor) {
                LogicVariable transferVariable = modifiers.contains(CACHED)
                        ? LogicVariable.global(identifier, nameCreator.global(identifier.getName()))
                        : this.processor.nextTemp();
                LogicString remoteName = mlogName == null ? nameCreator.remote(identifier)
                        : LogicString.create(modifiers.getNode(REMOTE).sourcePosition(), mlogName);
                RemoteVariable variable = new RemoteVariable(identifier.sourcePosition(), storageProcessor,
                        name, remoteName, transferVariable, false, false,
                        modifiers.contains(CACHED));

                return registerRemoteVariable(identifier, variable);
            } else  {
                return registerGlobalVariable(identifier,
                        LogicVariable.global(identifier,
                                mlogName == null ? nameCreator.global(identifier.getName()) : mlogName,
                                modifiers.containsAny(EXPORT, VOLATILE),
                                modifiers.contains(NOINIT),
                                modifiers.containsAny(EXPORT, MLOG)
                        )
                );
            }
        }
    }

    private boolean verifyGlobalDeclaration(AstMindcodeNode element, AstIdentifier identifierNode) {
        String identifier = identifierNode.getName();

        if (functionContext.variables().containsKey(identifier) || globalVariables.containsKey(identifier)) {
            error(element, ERR.VARIABLE_MULTIPLE_DECLARATIONS, identifier);
            return false;
        } else {
            return true;
        }
    }

    /// Resolves the variable encountered in the source code.
    ///
    /// Unknown variables handling:
    /// - Relaxed syntax: the variable is created implicitly.
    /// - Strict syntax: an error is reported, and an invalid variable instance is returned.
    ///
    /// @param identifier variable identifier
    /// @param local `true` if a local context is active. When a local context is not active, local function variables
    ///         are excluded
    /// @param allowUndeclaredLinks `true` to automatically declare undeclared linked variables without an error.
    ///         Used by `param` and `allocate` nodes.
    /// @return ValueStore instance containing the variable
    public ValueStore resolveVariable(AstIdentifier identifier, boolean local, boolean allowUndeclaredLinks) {
        // Look for local variables first
        if (local && functionContext.variables().containsKey(identifier.getName())) {
            return Objects.requireNonNull(functionContext.variables().get(identifier.getName()));
        } else if (globalVariables.containsKey(identifier.getName())) {
            return Objects.requireNonNull(globalVariables.get(identifier.getName()));
        }

        if (allowUndeclaredLinks && isLinkedVariable(identifier)) {
            return registerGlobalVariable(identifier, LogicVariable.block(identifier));
        }

        if (reportedErrors.add(identifier)) {
            switch (identifier.getProfile().getSyntacticMode()) {
                case STRICT -> error(identifier, ERR.VARIABLE_NOT_DEFINED, identifier.getName());
                case MIXED  -> warn(identifier, WARN.VARIABLE_NOT_DEFINED, identifier.getName());
            }
        }

        return createImplicitVariable(identifier);
    }

    /// Tries to find a variable among declared variables (local, then global). Returns null when not found.
    ///
    /// @param name name of variable to find
    /// @param allowLocal `true` to search in the local scope first
    /// @return ValueStore instance containing the variable
    public @Nullable ValueStore findVariable(String name, boolean allowLocal) {
        // Look for local variables first
        if (allowLocal && functionContext.variables().containsKey(name)) {
            return Objects.requireNonNull(functionContext.variables().get(name));
        } else if (globalVariables.containsKey(name)) {
            return Objects.requireNonNull(globalVariables.get(name));
        }
        return null;
    }

    public @Nullable StructuredValueStore findStructuredVariable(String name) {
        return structuredVariables.get(name);
    }

    /// Indicates a new function is being processed. Function processing can become nested when
    /// processing inline function calls. In this case, the previous function context is stored on a stack
    /// and restored when exiting the function processing. Non-inlined functions can't be nested.
    public void enterFunction(MindcodeFunction function, List<FunctionArgument> varargs) {
        contextStack.push(functionContext);
        functionContext = function.isRecursive()
                ? new RecursiveContext(messageConsumer, nameCreator, function, varargs)
                : new LocalContext(messageConsumer, nameCreator, function, varargs);
    }

    /// Called when function processing is finished. The previous function context is restored from the stack.
    public void exitFunction(MindcodeFunction function) {
        if (function != functionContext.function()) {
            throw new MindcodeInternalError("Wrong exitFunction order.");
        }
        if (contextStack.isEmpty()) {
            throw new MindcodeInternalError("Function stack is empty.");
        }
        functionContext = contextStack.pop();
    }

    public void enterAstNode() {
        functionContext.enterAstNode();
    }

    public void exitAstNode() {
        functionContext.exitAstNode();
    }

    public void registerNodeVariable(LogicVariable variable) {
        functionContext.registerNodeVariable(variable);
    }

    public void registerParentNodeVariable(LogicVariable variable) {
        functionContext.registerParentNodeVariable(variable);
    }

    /// Encapsulates processing of a given expression by keeping temporary variable(s) created while evaluating
    /// the expression out of the current node context. Suitable when the generated temporary variables are known
    /// not to be used outside the context of the expression. A good example is the condition expression of the
    /// if statement: the condition is evaluated, and the result is used to choose the branch to execute, but
    /// all this happens before either of the branches is executed and the temporary variable holding the condition
    /// value will not be used again.
    ///
    /// Note: `if x = a > b then ... else ... end; print(x);` is not a problem, because x is a user variable and
    /// is registered separately.
    ///
    /// @param <T> type of return value
    /// @param expression expression to evaluate
    /// @return value provided by the expression
    public <T> T excludeVariablesFromTracking(Supplier<T> expression) {
        return functionContext.excludeVariablesFromNode(expression);
    }

    /// Encapsulates processing of a given expression by keeping temporary variable(s) created while evaluating
    /// the expression out of the current node context (see above). To be used on expressions that do not return value.
    public void excludeVariablesFromTracking(Runnable expression) {
        excludeVariablesFromTracking(() -> {
            expression.run();
            return Void.TYPE;
        });
    }

    private @Nullable String processVariableMlogModifier(Modifiers modifiers) {
        if (modifiers.getParameters(MLOG) instanceof MlogSpecification(List<LogicArgument> mlogNames)) {
            if (mlogNames.isEmpty()) {
                throw new MindcodeInternalError("Mising name in mlog specification.");
            }

            if (mlogNames.size() > 1) {
                if (mlogNames.get(1) instanceof SourceElement element && !element.sourcePosition().isEmpty()) {
                    error(element, ERR.MODIFIER_MLOG_TOO_MAY_VALUES);
                } else {
                    error(modifiers.getNode(MLOG), ERR.MODIFIER_MLOG_TOO_MAY_VALUES);
                }
            }

            switch (mlogNames.getFirst()) {
                case LogicString s -> { return s.getValue();}
                case LogicKeyword kw -> {
                    error(modifiers.getNode(MLOG), ERR.INVALID_MLOG_KEYWORD);
                    return null;
                }
                case LogicVariable l -> { return null; }
                default -> throw new MindcodeInternalError("Unexpected mlog name " + mlogNames.getFirst());
            }
        } else if (modifiers.contains(MLOG)) {
            throw new MindcodeInternalError("Unexpected modifier parametrization: " + modifiers.getParameters(MLOG));
        } else {
            return null;
        }
    }

    public ArrayNameCreator processArrayMlogModifier(Modifiers modifiers, int arraySize, NameCreator standardNameCreator) {
        if (!modifiers.contains(MLOG)) return standardNameCreator;

        if (modifiers.getParameters(MLOG) instanceof MlogSpecification(List<LogicArgument> mlogNames)) {
            if (mlogNames.isEmpty()) {
                throw new MindcodeInternalError("Mising name in mlog specification.");
            }
            SourceElement sourceElement = modifiers.getNode(MLOG);

            if (mlogNames.getFirst() instanceof LogicKeyword keyword) {
                if (mlogNames.size() > 1) {
                    error(sourceElement, ERR.MODIFIER_MLOG_TOO_MAY_VALUES);
                }
                if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
                    error(sourceElement, ERR.LOOKUP_REQUIRES_TARGET_8);
                }

                Map<Integer, ? extends MindustryContent> lookupMap = processor.getMetadata().getLookupMap(keyword.getKeyword());

                if (lookupMap == null) {
                    error(sourceElement, ERR.MODIFIER_MLOG_UNKNOWN_LOOKUP_TYPE, keyword.getKeyword());
                    return standardNameCreator;
                }

                if (lookupMap.size() < arraySize) {
                    error(sourceElement, ERR.MODIFIER_MLOG_LOOKUP_TOO_SMALL, keyword.getKeyword(),
                            lookupMap.size(), arraySize);
                    return standardNameCreator;
                }

                return new ArrayNameCreator() {
                    @Override
                    public String arrayBase(String processorName, String arrayName) {
                        return standardNameCreator.arrayBase(processorName, arrayName);
                    }

                    @Override
                    public String arrayElement(String arrayName, int index) {
                        return lookupMap.get(index).contentName();
                    }

                    @Override
                    public String remoteArrayElement(String arrayName, int index) {
                        return arrayElement(arrayName, index);
                    }

                    @Override
                    public LogicKeyword arrayLookupType() {
                        return keyword;
                    }
                };
            } else {
                if (mlogNames.size() != arraySize) {
                    error(sourceElement, ERR.MODIFIER_MLOG_SIZE_MISMATCH, mlogNames.size(), arraySize);
                }

                mlogNames.stream().filter(LogicKeyword.class::isInstance)
                        .forEach(_ -> error(sourceElement, ERR.INVALID_MLOG_KEYWORD));

                return new ArrayNameCreator() {
                    @Override
                    public String arrayBase(String processorName, String arrayName) {
                        return standardNameCreator.arrayBase(processorName, arrayName);
                    }

                    @Override
                    public String arrayElement(String arrayName, int index) {
                        return index >= mlogNames.size() ? "invalid"
                                : mlogNames.get(index) instanceof LogicString str ? str.getValue()
                                : standardNameCreator.arrayElement(arrayName, index);
                    }

                    @Override
                    public String remoteArrayElement(String arrayName, int index) {
                        return arrayElement(arrayName, index);
                    }
                };
            }
        } else {
            throw new MindcodeInternalError("Unexpected modifier parametrization: " + modifiers.getParameters(MLOG));
        }
    }
}
