package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface OptimizerContext extends CompilerContext {
    void addDiagnosticData(Object data);
    <T> List<T> getDiagnosticData(Class<T> type);
}