package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.OptimizationLevel;

public class CompilerFacade {
    public static CompilerOutput<String> compile(boolean webApplication, String sourceCode, OptimizationLevel optimizationLevel) {
        return compile(sourceCode, new CompilerProfile(webApplication, optimizationLevel));
    }

    public static CompilerOutput<String> compile(String sourceCode, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(sourceCode);
    }
}
