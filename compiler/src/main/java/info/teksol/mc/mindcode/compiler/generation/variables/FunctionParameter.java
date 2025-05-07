package info.teksol.mc.mindcode.compiler.generation.variables;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FunctionParameter extends ValueStore {
    // Variable name
    String getName();

    boolean isInput();

    boolean isOutput();
}
