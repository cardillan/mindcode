package info.teksol.mindcode.compiler;

import info.teksol.mindcode.v3.InputFiles;

import java.util.List;

public interface Compiler<T> {

    CompilerOutput<T> compile(InputFiles inputFiles);

    CompilerOutput<String> compile(InputFiles inputFiles, List<InputFiles.InputFile> filesToCompile);
}
