package info.teksol.mindcode.compiler;

public interface Compiler<T> {

    CompilerOutput<T> compile(String sourceCode);

}
