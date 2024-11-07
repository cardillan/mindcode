package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.v3.InputFiles;

import java.util.List;

public class CompilerFacade {
    public static CompilerOutput<String> compile(boolean webApplication, String sourceCode,
            OptimizationLevel optimizationLevel, boolean run) {
        CompilerProfile compilerProfile = new CompilerProfile(webApplication, optimizationLevel);
        compilerProfile.setRun(run);
        return compile(InputFiles.fromSource(sourceCode), compilerProfile);
    }

    public static CompilerOutput<String> compile(InputFiles inputFiles, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(inputFiles);
    }

    public static CompilerOutput<String> compile(InputFiles inputFiles, InputFiles.InputFile fileToCompile, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(inputFiles, List.of(fileToCompile));
    }
}
