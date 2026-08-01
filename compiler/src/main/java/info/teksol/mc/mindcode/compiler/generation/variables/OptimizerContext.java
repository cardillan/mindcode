package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
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
