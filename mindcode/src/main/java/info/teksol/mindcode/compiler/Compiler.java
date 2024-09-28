package info.teksol.mindcode.compiler;

import java.util.List;

public interface Compiler<T> {

    CompilerOutput<T> compile(List<SourceFile> sourceFiles);

}
