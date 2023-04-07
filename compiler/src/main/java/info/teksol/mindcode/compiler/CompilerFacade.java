package info.teksol.mindcode.compiler;

public class CompilerFacade {
    public static CompilerOutput compile(String sourceCode, boolean enableOptimization) {
        return compile(sourceCode, enableOptimization ? CompilerProfile.standardOptimizations() : CompilerProfile.noOptimizations());
    }
    
    public static CompilerOutput compile(String sourceCode, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(sourceCode);
    }
}
