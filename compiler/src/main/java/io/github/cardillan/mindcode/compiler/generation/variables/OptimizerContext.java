package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.compiler.CompilerContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.callgraph.CallGraph;
import io.github.cardillan.mindcode.compiler.generation.StackTracker;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface OptimizerContext extends CompilerContext {
    void addDiagnosticData(Object data);
    <T> void addDiagnosticData(Class<T> dataClass, List<T> data);
    <T> List<T> getDiagnosticData(Class<T> type);

    AstContext rootAstContext();
    NameCreator nameCreator();
    StackTracker stackTracker();
    CallGraph callGraph();
}
