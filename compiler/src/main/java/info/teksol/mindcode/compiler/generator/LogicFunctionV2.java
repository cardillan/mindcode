package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.IntRange;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// Just "Function" would be preferred, but that conflicts with java.util.function.Function
public class LogicFunctionV2 implements LogicFunction {
    private static final AtomicInteger functionIds = new AtomicInteger();

    // Function id
    private final int id = functionIds.getAndIncrement();

    // Function properties
    private final FunctionDeclaration declaration;
    private final IntRange parameterCount;
    private Map<String, FunctionParameter> parameterMap = Map.of();
    private List<LogicVariable> parameters = List.of();
    private LogicLabel label;
    private String prefix;
    private int useCount = 0;
    private boolean inlined = false;

    // All calls in this function, including unresolved ones
    private final List<FunctionCall> functionCalls = new ArrayList<>();

    // Information about user-defined functions called from this function
    private final Map<LogicFunction, Integer> callCardinality = new HashMap<>();
    private final Set<LogicFunction> directCalls = new HashSet<>();
    private final Set<LogicFunction> recursiveCalls = new HashSet<>();
    private final Set<LogicFunction> indirectCalls = new HashSet<>();

    LogicFunctionV2(FunctionDeclaration declaration) {
        this.declaration = declaration;
        parameterCount = declaration.getParameterCount();
    }

    void addCall(FunctionCall call) {
        functionCalls.add(call);
    }

    void initializeCalls(FunctionDefinitions functions) {
        List<LogicFunctionV2> calls = functionCalls.stream().map(functions::getExactMatch).filter(Objects::nonNull).toList();
        directCalls.addAll(calls);

        Map<LogicFunctionV2, Long> cardinality = calls.stream().collect(Collectors.groupingBy(f -> f, Collectors.counting()));
        cardinality.forEach((function, count) -> callCardinality.put(function, count.intValue()));
    }

    /**
     * @return true if this is the main function
     */
    @Override
    public boolean isMain() {
        return declaration.getName().isEmpty();
    }

    /**
     * @return true if this function should be inlined
     */
    @Override
    public boolean isInline() {
        // Automatically inline all non-recursive functions called just once
        return !isRecursive() && !declaration.isNoinline() && (inlined || declaration.isInline() || getUseCount() == 1);
    }

    @Override
    public boolean isNoinline() {
        return declaration.isNoinline();
    }

    public boolean isVarArgs() {
        List<FunctionParameter> parameters = getDeclaredParameters();
        return !parameters.isEmpty() && parameters.getLast().isVarArgs();
    }

    @Override
    public boolean isVoid() {
        return declaration.isProcedure();
    }

    public FunctionDeclaration getDeclaration() {
        return declaration;
    }

    /**
     * @return the body of the function from function declaration
     */
    public AstNode getBody() {
        return declaration.getBody();
    }

    /**
     * @return name of the function
     */
    public String getName() {
        return declaration.getName();
    }

    /**
     * @return list of parameters of the function
     */
    public List<FunctionParameter> getDeclaredParameters() {
        return declaration.getParams();
    }

    public FunctionParameter getDeclaredParameter(int index) {
        return declaration.getParams().get(index);
    }

    public FunctionParameter getDeclaredParameter(String name) {
        return parameterMap.get(name);
    }

    /**
     * @return list of parameters of the function as LogicVariable
     */
    @Override
    public List<LogicVariable> getParameters() {
        return parameters;
    }

    /**
     * @return function parameter at given index
     */
    @Override
    public LogicVariable getParameter(int index) {
        return parameters.get(index);
    }

    @Override
    public boolean isInputFunctionParameter(LogicVariable variable) {
        return variable.getType() == ArgumentType.LOCAL_VARIABLE
                && prefix.equals(variable.getFunctionPrefix())
                && parameterMap.containsKey(variable.getName())
                && parameterMap.get(variable.getName()).isInput();
    }

    @Override
    public boolean isOutputFunctionParameter(LogicVariable variable) {
        return variable.getType() == ArgumentType.LOCAL_VARIABLE
                && prefix.equals(variable.getFunctionPrefix())
                && parameterMap.containsKey(variable.getName())
                && parameterMap.get(variable.getName()).isOutput();
    }

    @Override
    public boolean isNotOutputFunctionParameter(LogicVariable variable) {
        return !isOutputFunctionParameter(variable);
    }

    @Override
    public IntRange getParameterCount() {
        return parameterCount;
    }

    @Override
    public int getStandardParameterCount() {
        return declaration.getParams().size() - (isVarArgs() ? 1 : 0);
    }

    @Override
    public Set<LogicFunction> getDirectCalls() {
        return directCalls;
    }

    @Override
    public Set<LogicFunction> getIndirectCalls() {
        return indirectCalls;
    }

    public Map<LogicFunction, Integer> getCallCardinality() {
        return callCardinality;
    }

    /**
     * @return true if the function is used (is reachable from main program body)
     */
    @Override
    public boolean isUsed() {
        return useCount > 0;
    }

    /**
     * @return the number of places the function is called from
     */
    @Override
    public int getUseCount() {
        return useCount;
    }

    /**
     * @return true if the function is recursive
     */
    @Override
    public boolean isRecursive() {
        return !recursiveCalls.isEmpty();
    }

    /**
     * Determines whether the call to calledFunction from this function is (potentially) recursive, that is
     * it can lead to another invocation of this function. If it is, the local context of the function needs
     * to be saved and restored using stack.
     *
     * @param calledFunction name of the function being called from this function
     * @return true if this function call might be recursive
     */
    public boolean isRecursiveCall(LogicFunction calledFunction) {
        return recursiveCalls.contains(calledFunction);
    }

    public boolean exactMatch(FunctionCall call) {
        if (!parameterCount.contains(call.getArguments().size())) {
            return false;
        }

        boolean isVarArgs = isVarArgs();
        int size = getStandardParameterCount();
        for (int i = 0; i < call.getArguments().size(); i++) {
            FunctionArgument argument = call.getArgument(i);
            if (i < size) {
                if (!declaration.getParams().get(i).matches(argument)) return false;
            } else if (!isVarArgs || !argument.hasExpression()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether the called function is called more than once from this function. If it is, the
     * returned value from each call needs to be stored in a unique variable, otherwise subsequent calls
     * might overwrite the returned value from previous call.
     *
     * @param calledFunction name of the function being called from this function
     * @return true if the function is called more than once from this function
     */
    public boolean isRepeatedCall(LogicFunction calledFunction) {
        return indirectCalls.contains(calledFunction)
                ? getCallCardinality().getOrDefault(calledFunction, 0) > 0
                : getCallCardinality().getOrDefault(calledFunction, 0) > 1;
    }

    /**
     * @return the label allocated for the beginning of this function
     */
    @Override
    public LogicLabel getLabel() {
        return label;
    }

    /**
     * @return the local prefix allocated for local variables of this function
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    public boolean addIndirectCalls(Set<LogicFunction> calls) {
        return indirectCalls.addAll(calls);
    }

    // Mark the call from this function to called function as recursive. Used when cycle in the call graph
    // was detected.
    public void addRecursiveCall(LogicFunction calledFunction) {
        recursiveCalls.add(calledFunction);
    }

    // Increase usage count of this function
    @Override
    public void markUsage(int count) {
        useCount += count;
    }

    public void setLabel(LogicLabel label) {
        this.label = label;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void setInlined() {
        inlined = true;
    }

    void createParameters() {
        parameterMap = getDeclaredParameters().stream().collect(
                Collectors.toMap(FunctionParameter::getName, v -> v));

        parameters = getDeclaredParameters().stream()
                .map(p -> LogicVariable.local(getName(), prefix, p)).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicFunctionV2 function = (LogicFunctionV2) o;
        return id == function.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public InputPosition getInputPosition() {
        return declaration != null ? declaration.inputPosition() : null;
    }

    @Override
    public String format() {
        StringBuilder sbr = new StringBuilder(getName().isEmpty()? "<main>" : getName()).append("(");
        for (int i = 0; i < declaration.getParams().size(); i++) {
            if (i > 0) sbr.append(", ");
            appendParameter(sbr, declaration.getParams().get(i));
        }
        return sbr.append(")").toString();

    }

    private void appendParameter(StringBuilder sbr, FunctionParameter parameter) {
        if (parameter.hasInModifier()) sbr.append("in ");
        if (parameter.hasOutModifier()) sbr.append("out ");
        sbr.append(parameter.getName());
        if (parameter.isVarArgs()) sbr.append("...");
    }

    @Override
    public String toString() {
        return "Logic function #" + id + ": " + format();
    }

}