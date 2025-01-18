package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Just "Function" would be preferred, but that conflicts with java.util.function.Function
@NullMarked
public class MindcodeFunction {
    private static final AtomicInteger functionIds = new AtomicInteger();

    // Function id
    private final int id = functionIds.getAndIncrement();

    // Function properties
    private final AstFunctionDeclaration declaration;
    private final IntRange parameterCount;
    private Map<String, AstFunctionParameter> parameterMap = Map.of();

    // TODO parameters should be ValueStores; add copyFrom method to ValueStore
    private List<LogicVariable> parameters = List.of();
    private @Nullable LogicLabel label;
    private String prefix = "";
    private int placementCount = 0;
    private boolean inlined = false;

    // All calls in this function, including unresolved ones
    private final List<AstFunctionCall> functionCalls = new ArrayList<>();

    // Information about user-defined functions called from this function
    private final Map<MindcodeFunction, Integer> callCardinality = new HashMap<>();
    private final Set<MindcodeFunction> directCalls = new HashSet<>();
    private final Set<MindcodeFunction> recursiveCalls = new HashSet<>();
    private final Set<MindcodeFunction> indirectCalls = new HashSet<>();

    MindcodeFunction(AstFunctionDeclaration declaration) {
        this.declaration = Objects.requireNonNull(declaration);
        parameterCount = declaration.getParameterCount();
    }

    private MindcodeFunction(MindcodeFunction other, String prefix) {
        this.prefix = prefix;

        declaration = other.declaration;
        parameterCount = other.parameterCount;
        placementCount = other.placementCount;
        inlined = true;

        functionCalls.addAll(other.functionCalls);
        callCardinality.putAll(other.callCardinality);
        directCalls.addAll(other.directCalls);
        recursiveCalls.addAll(other.recursiveCalls);
        indirectCalls.addAll(other.indirectCalls);

        createParameters();
    }

    /// Prepares an inlined function for call (by setting up a prefix for it)
    public MindcodeFunction prepareInlinedForCall(String prefix) {
        return new MindcodeFunction(this, prefix);
    }

    void addCall(AstFunctionCall call) {
        functionCalls.add(call);
    }

    void initializeCalls(FunctionDefinitions functions) {
        List<MindcodeFunction> calls = functionCalls.stream().flatMap(call -> getPossibleCalls(functions, call)).toList();
        directCalls.addAll(calls);

        Map<MindcodeFunction, Long> cardinality = calls.stream().collect(Collectors.groupingBy(f -> f, Collectors.counting()));
        cardinality.forEach((function, count) -> callCardinality.put(function, count.intValue()));
    }

    /// Creates a list of possible calls from this function
    Stream<MindcodeFunction> getPossibleCalls(FunctionDefinitions functions, AstFunctionCall call) {
        // When creating the function call graph, we can't match functions exactly from within the
        return isVarargs()
                ? functions.getFunctions(call.getFunctionName()).stream()
                : Stream.of(call).map(c -> functions.getExactMatch(c, -1)).filter(Objects::nonNull);
    }

    /// @return true if this is the main function
    public boolean isMain() {
        return declaration.getName().isEmpty();
    }

    /// @return true if this function should be inlined
    public boolean isInline() {
        // Automatically inline all non-recursive functions called just once
        return !isRecursive() && !declaration.isNoinline() && (inlined || declaration.isInline() || getPlacementCount() == 1);
    }

    public boolean isNoinline() {
        return declaration.isNoinline();
    }

    public boolean isVarargs() {
        List<AstFunctionParameter> parameters = getDeclaredParameters();
        return !parameters.isEmpty() && parameters.getLast().isVarargs();
    }

    public boolean isVarargs(String parameterName) {
        List<AstFunctionParameter> parameters = getDeclaredParameters();
        return !parameters.isEmpty() && parameters.getLast().isVarargs()
               && parameters.getLast().getName().equals(parameterName);
    }

    public boolean isVoid() {
        return declaration.getDataType() == DataType.VOID;
    }

    public AstFunctionDeclaration getDeclaration() {
        return declaration;
    }

    /// @return the body of the function from function declaration
    public List<AstMindcodeNode> getBody() {
        return declaration.getBody();
    }

    /// @return name of the function
    public String getName() {
        return declaration.getName();
    }

    /// @return list of parameters of the function
    public List<AstFunctionParameter> getDeclaredParameters() {
        return declaration.getParameters();
    }

    public AstFunctionParameter getDeclaredParameter(int index) {
        return declaration.getParameter(index);
    }

    public AstFunctionParameter getDeclaredParameter(String name) {
        return parameterMap.get(name);
    }

    /// @return list of parameters of the function as LogicVariable
    public List<LogicVariable> getParameters() {
        return parameters;
    }

    /// @return function parameter at given index
    public LogicVariable getParameter(int index) {
        return parameters.get(index);
    }

    public boolean isInputFunctionParameter(LogicVariable variable) {
        return variable.getType() == ArgumentType.LOCAL_VARIABLE
                && prefix.equals(variable.getFunctionPrefix())
                && parameterMap.containsKey(variable.getName())
                && parameterMap.get(variable.getName()).isInput();
    }

    public boolean isOutputFunctionParameter(LogicVariable variable) {
        return variable.getType() == ArgumentType.LOCAL_VARIABLE
                && prefix.equals(variable.getFunctionPrefix())
                && parameterMap.containsKey(variable.getName())
                && parameterMap.get(variable.getName()).isOutput();
    }

    public boolean isNotOutputFunctionParameter(LogicVariable variable) {
        return !isOutputFunctionParameter(variable);
    }

    public boolean isVarargFunctionParameter(LogicVariable variable) {
        return variable.getType() == ArgumentType.LOCAL_VARIABLE
               && prefix.equals(variable.getFunctionPrefix())
               && parameterMap.containsKey(variable.getName())
               && parameterMap.get(variable.getName()).isVarargs();
    }

    public IntRange getParameterCount() {
        return parameterCount;
    }

    public int getStandardParameterCount() {
        return declaration.getParameters().size() - (isVarargs() ? 1 : 0);
    }

    public Set<MindcodeFunction> getDirectCalls() {
        return directCalls;
    }

    public Set<MindcodeFunction> getIndirectCalls() {
        return indirectCalls;
    }

    Map<MindcodeFunction, Integer> getCallCardinality() {
        return callCardinality;
    }

    /// @return true if the function is used (is reachable from main program body)
    public boolean isUsed() {
        return placementCount > 0;
    }

    /// @return the number of places the function is called from
    public int getPlacementCount() {
        return placementCount;
    }

    /// @return true if the function is recursive
    public boolean isRecursive() {
        return !recursiveCalls.isEmpty();
    }

    /// Determines whether the call to calledFunction from this function is (potentially) recursive, that is
    /// it can lead to another invocation of this function. If it is, the local context of the function needs
    /// to be saved and restored using stack.
    ///
    /// @param calledFunction name of the function being called from this function
    /// @return true if this function call might be recursive
    public boolean isRecursiveCall(MindcodeFunction calledFunction) {
        return recursiveCalls.contains(calledFunction);
    }

    /// Determines whether this function call exactly matches this function.
    ///
    /// @param call function call to inspect
    /// @return true if the call is an exact match to this function
    public boolean exactMatch(AstFunctionCall call, int callArgumentCount) {
        int arguments = callArgumentCount < 0 ? call.getArguments().size() : callArgumentCount;

        if (!parameterCount.contains(arguments)) {
            return false;
        }

        int limit = Math.min(arguments, call.getArguments().size());
        boolean isVarArgs = isVarargs();
        int size = getStandardParameterCount();
        for (int i = 0; i < limit; i++) {
            AstFunctionArgument argument = call.getArgument(i);
            if (i < size) {
                if (!declaration.getParameter(i).matches(argument)) return false;
            } else if (!isVarArgs || !argument.hasExpression()) {
                return false;
            }
        }

        return true;
    }

    /// Determines whether the called function is called more than once from this function. If it is, the
    /// returned value from each call needs to be stored in a unique variable, otherwise subsequent calls
    /// might overwrite the returned value from previous call.
    ///
    /// @param calledFunction function being called from this function
    /// @return true if the function is called more than once from this function
    public boolean isRepeatedCall(MindcodeFunction calledFunction) {
        return indirectCalls.contains(calledFunction)
                ? getCallCardinality().getOrDefault(calledFunction, 0) > 0
                : getCallCardinality().getOrDefault(calledFunction, 0) > 1;
    }

    /// @return the label allocated for the beginning of this function (null for inline functions)
    public @Nullable LogicLabel getLabel() {
        return label;
    }

    /// @return the local prefix allocated for local variables of this function
    public String getPrefix() {
        return prefix;
    }

    boolean addIndirectCalls(Set<MindcodeFunction> calls) {
        return indirectCalls.addAll(calls);
    }

    // Mark the call from this function to called function as recursive. Used when cycle in the call graph
    // was detected.
    void addRecursiveCall(MindcodeFunction calledFunction) {
        recursiveCalls.add(calledFunction);
    }

    public void updatePlacement(int count) {
        placementCount += count;
    }

    public void setLabel(LogicLabel label) {
        this.label = label;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setInlined() {
        inlined = true;
    }

    void createParameters() {
        parameterMap = getDeclaredParameters().stream().collect(
               Collectors.toMap(AstFunctionParameter::getName, v -> v));

        parameters = getDeclaredParameters().stream()
                .map(p -> LogicVariable.parameter(p, getName(), prefix)).toList();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MindcodeFunction function = (MindcodeFunction) o;
        return id == function.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public SourcePosition getSourcePosition() {
        return declaration.sourcePosition();
    }

    public String format() {
        StringBuilder sbr = new StringBuilder(getName().isEmpty()? "<main>" : getName()).append("(");
        for (int i = 0; i < declaration.getParameters().size(); i++) {
            if (i > 0) sbr.append(", ");
            appendParameter(sbr, declaration.getParameter(i));
        }
        return sbr.append(")").toString();

    }
    private void appendParameter(StringBuilder sbr, AstFunctionParameter parameter) {
        if (parameter.hasInModifier()) sbr.append("in ");
        if (parameter.hasOutModifier()) sbr.append("out ");
        sbr.append(parameter.getName());
        if (parameter.isVarargs()) sbr.append("...");
    }

    @Override
    public String toString() {
        return "Logic function #" + id + ": " + format();
    }

}
