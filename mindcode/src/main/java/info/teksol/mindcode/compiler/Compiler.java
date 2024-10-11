package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputFile;

import java.util.List;

public interface Compiler<T> {

    CompilerOutput<T> compile(List<InputFile> inputFiles);

}
