package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.StackAllocation;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.*;
import java.util.stream.Stream;

/**
 * Holds information about calls between individual user-defined functions. Information is gathered by inspecting
 * function definitions, particularly calls made to other user-defined functions. Information is held by instances
 * of the inner Function class.
 */
public final class CallGraph {
    private final FunctionDefinitions functionDefinitions;
    private final List<LogicFunction> functions;
    private final StackAllocation allocatedStack;
    private final Set<String> syncedVariables = new HashSet<>();

    CallGraph(FunctionDefinitions functionDefinitions, StackAllocation allocatedStack) {
        this.functionDefinitions = Objects.requireNonNull(functionDefinitions);
        this.allocatedStack = allocatedStack;
        functions = List.copyOf(functionDefinitions.getFunctions());
    }

    public static CallGraph createEmpty() {
        return new CallGraph(new FunctionDefinitions(mindcodeMessage -> {}),  null);
    }

    /**
     * Returns the instance of StackAllocation found in the program, regardless of where in the program it was declared.
     *
     * @return instance of StackAllocation or null if it wasn't declared
     */
    public StackAllocation getAllocatedStack() {
        return allocatedStack;
    }

    /**
     * Returns the representation of the main function, that is the main program body.
     *
     * @return instance representing main body
     */
    public LogicFunction getMain() {
        return functionDefinitions.getMain();
    }

    /**
     * @return list of all existing functions
     */
    public List<LogicFunction> getFunctions() {
        return functions;
    }

    /**
     * Finds the closes matching function to given function call. The closest matching function is the one
     * giving the best match score. If a function matching the call exactly exists, it will be chosen.
     *
     * @param call function call to match
     * @return function matching the given call best
     */
    public Optional<LogicFunction> getFunction(FunctionCall call) {
        return functionDefinitions.getFunction(call);
    }

    /**
     * Returns true if at least one user-defined function is recursive. Declared but unused functions aren't
     * considered (a function is unused if it isn't reachable from the main body). When there is at least one
     * recursive function, a stack needs to be set up.
     *
     * @return true if there's a recursive function
     */
    public boolean containsRecursiveFunction() {
        return functions.stream().filter(LogicFunction::isUsed).anyMatch(LogicFunction::isRecursive);
    }

    /**
     * Creates a stream of active, recursive functions.
     *
     * @return a stream of active, recursive functions.
     */
    public Stream<LogicFunction> recursiveFunctions() {
        return functions.stream().filter(LogicFunction::isUsed).filter(LogicFunction::isRecursive);
    }

    void addSyncedVariables(Set<String> syncedVariables) {
        this.syncedVariables.addAll(syncedVariables);
    }

    public Set<String> getSyncedVariables() {
        return syncedVariables;
    }

    /**
     * Inspects the structure of function calls and sets function properties accordingly. Assigns a function prefix
     * and labels to recursive and stackless functions.
     *
     * @param instructionProcessor processor used to obtain function prefixes and labels
     */
    void buildCallGraph(InstructionProcessor instructionProcessor) {
        visitFunction(functionDefinitions.getMain(), 0);

        // 1st level of indirect calls
        // Filtering out null values: call map may contain built-in and unknown functions
        functions.forEach(outer ->
                outer.getCallCardinality().keySet().forEach(inner ->
                        outer.addIndirectCalls(inner.getDirectCalls())
                )
        );

        // 2nd and other levels of indirection
        // Must end eventually, as there's a finite number of functions to add
        while (propagateIndirectCalls()) ;

        functions.stream()
                .filter(f -> f.isUsed() && !f.isInline())
                .forEach(f -> setupOutOfLineFunction(instructionProcessor, f));
    }

    private void setupOutOfLineFunction(InstructionProcessor instructionProcessor, LogicFunction function) {
        function.setLabel(instructionProcessor.nextLabel());
        function.setPrefix(instructionProcessor.nextFunctionPrefix());
        function.createParameters();
    }

    private boolean propagateIndirectCalls() {
        // Returns true if at least one function was modified
        return functions.stream()
                .flatMapToInt(outer -> outer.getDirectCalls().stream()
                        .mapToInt(inner -> outer.addIndirectCalls(inner.getIndirectCalls()) ? 1 : 0)
                ).sum() > 0;    // sum to force visiting all items in the stream
    }

    private final List<LogicFunction> callStack = new ArrayList<>();

    private void visitFunction(LogicFunction function, int count) {
        function.markUsage(count);

        int index = callStack.indexOf(function);
        callStack.add(function);
        if (index >= 0) {
            // Detected a cycle in the call graph starting at index. Mark all calls on the cycle as recursive, stop DFS
            // We know function is now at the beginning and also at the end of the cycle in callStack
            LogicFunction caller = function;
            for (LogicFunction nextCallee : callStack.subList(index + 1, callStack.size())) {
                caller.addRecursiveCall(nextCallee);
                caller = nextCallee;
            }
        } else {
            // Visit all children
            function.getCallCardinality().forEach(this::visitFunction);
        }
        callStack.remove(callStack.size() - 1);
    }

}
