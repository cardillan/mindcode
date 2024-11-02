package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.StackAllocation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Holds information about calls between individual user-defined functions. Information is gathered by inspecting
 * function definitions, particularly calls made to other user-defined functions. Information is held by instances
 * of the inner Function class.
 */
public final class CallGraph {
    private final FunctionDefinitions functionDefinitions;
    private final List<LogicFunction> functions;
    private final Set<String> syncedVariables;
    private final StackAllocation allocatedStack;

    CallGraph(FunctionDefinitions functionDefinitions, Set<String> syncedVariables, StackAllocation allocatedStack) {
        this.functionDefinitions = Objects.requireNonNull(functionDefinitions);
        this.functions = List.copyOf(functionDefinitions.getFunctions());
        this.syncedVariables = Set.copyOf(syncedVariables);
        this.allocatedStack = allocatedStack;
    }

    public static CallGraph createEmpty() {
        return new CallGraph(new FunctionDefinitions(mindcodeMessage -> {}), Set.of(), null);
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

    public Set<String> getSyncedVariables() {
        return syncedVariables;
    }
}
