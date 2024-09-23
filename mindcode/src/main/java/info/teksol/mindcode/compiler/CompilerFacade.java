package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.OptimizationLevel;

public class CompilerFacade {
    public static CompilerOutput<String> compile(boolean webApplication, String sourceCode, OptimizationLevel optimizationLevel,
                                                 boolean run) {
        CompilerProfile compilerProfile = new CompilerProfile(webApplication, optimizationLevel);
        compilerProfile.setRun(run);
        return compile(sourceCode, compilerProfile);
    }

    public static CompilerOutput<String> compile(String sourceCode, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(sourceCode);
    }
}
