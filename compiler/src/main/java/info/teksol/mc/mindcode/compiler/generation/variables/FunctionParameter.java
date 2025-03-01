package info.teksol.mc.mindcode.compiler.generation.variables;

public interface FunctionParameter extends ValueStore {
    // Variable name
    String getName();

    boolean isInput();

    boolean isOutput();
}
