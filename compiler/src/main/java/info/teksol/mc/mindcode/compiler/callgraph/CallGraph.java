package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/// Holds information about calls between individual user-defined functions. Information is gathered by inspecting
/// function definitions, particularly calls made to other user-defined functions. Information is held by instances
/// of the inner Function class.
@NullMarked
public final class CallGraph {
    private final FunctionDefinitions functionDefinitions;
    private final List<MindcodeFunction> functions;

    CallGraph(FunctionDefinitions functionDefinitions) {
        this.functionDefinitions = Objects.requireNonNull(functionDefinitions);
        this.functions = List.copyOf(functionDefinitions.getFunctions());
    }

    public static CallGraph createEmpty() {
        return new CallGraph(new FunctionDefinitions(mindcodeMessage -> {}));
    }

    /// Returns the representation of the main function, that is the main program body.
    ///
    /// @return instance representing main body
    public MindcodeFunction getMain() {
        return functionDefinitions.getMain();
    }

    /// @return list of all existing functions
    public List<MindcodeFunction> getFunctions() {
        return functions;
    }

    public List<MindcodeFunction> getExactMatches(AstFunctionCall call, List<FunctionArgument> arguments) {
        return functionDefinitions.getExactMatches(call, arguments.size());
    }

    public List<MindcodeFunction> getLooseMatches(AstFunctionCall call) {
        return functionDefinitions.getLooseMatches(call);
    }

    /// Returns true if at least one user-defined function is recursive. Declared but unused functions aren't
    /// considered (a function is unused if it isn't reachable from the main body). When there is at least one
    /// recursive function, a stack needs to be set up.
    ///
    /// @return true if there's a recursive function
    public boolean containsRecursiveFunction() {
        return functions.stream().filter(MindcodeFunction::isUsed).anyMatch(MindcodeFunction::isRecursive);
    }

    /// @return a stream of active, recursive functions.
    public Stream<MindcodeFunction> recursiveFunctions() {
        return functions.stream().filter(MindcodeFunction::isUsed).filter(MindcodeFunction::isRecursive);
    }
}
