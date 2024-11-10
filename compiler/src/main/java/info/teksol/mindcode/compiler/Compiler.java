package info.teksol.mindcode.compiler;

import info.teksol.mindcode.v3.InputFile;

import java.util.List;

public interface Compiler<T> {

    CompilerOutput<T> compile();

    CompilerOutput<String> compile(List<InputFile> filesToCompile);
}
