package info.teksol.mindcode.v3.compiler.callgraph;

import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/// Holds information about calls between individual user-defined functions. Information is gathered by inspecting
/// function definitions, particularly calls made to other user-defined functions. Information is held by instances
/// of the inner Function class.
@NullMarked
public final class CallGraph {
    private final FunctionDefinitions functionDefinitions;
    private final List<LogicFunction> functions;
    private final Set<String> syncedVariables;

    CallGraph(FunctionDefinitions functionDefinitions, Set<String> syncedVariables) {
        this.functionDefinitions = Objects.requireNonNull(functionDefinitions);
        this.functions = List.copyOf(functionDefinitions.getFunctions());
        this.syncedVariables = Set.copyOf(syncedVariables);
    }

    public static CallGraph createEmpty() {
        return new CallGraph(new FunctionDefinitions(mindcodeMessage -> {}), Set.of());
    }

    /// Returns the representation of the main function, that is the main program body.
    ///
    /// @return instance representing main body
    public LogicFunction getMain() {
        return functionDefinitions.getMain();
    }

    /// @return list of all existing functions
    public List<LogicFunction> getFunctions() {
        return functions;
    }

    public List<LogicFunction> getExactMatches(AstFunctionCall call) {
        return functionDefinitions.getExactMatches(call);
    }

    public List<LogicFunction> getLooseMatches(AstFunctionCall call) {
        return functionDefinitions.getLooseMatches(call);
    }

    /// Returns true if at least one user-defined function is recursive. Declared but unused functions aren't
    /// considered (a function is unused if it isn't reachable from the main body). When there is at least one
    /// recursive function, a stack needs to be set up.
    ///
    /// @return true if there's a recursive function
    public boolean containsRecursiveFunction() {
        return functions.stream().filter(LogicFunction::isUsed).anyMatch(LogicFunction::isRecursive);
    }

    /// @return a stream of active, recursive functions.
    public Stream<LogicFunction> recursiveFunctions() {
        return functions.stream().filter(LogicFunction::isUsed).filter(LogicFunction::isRecursive);
    }

    public Set<String> getSyncedVariables() {
        return syncedVariables;
    }
}
