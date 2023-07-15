package info.teksol.mindcode.compiler;

public class CompilerFacade {
    public static CompilerOutput<String> compile(boolean webApplication, String sourceCode, boolean enableOptimization) {
        return compile(sourceCode, enableOptimization
                ? CompilerProfile.standardOptimizations(webApplication)
                : CompilerProfile.noOptimizations(webApplication)
        );
    }
    
    public static CompilerOutput<String> compile(String sourceCode, CompilerProfile profile) {
        MindcodeCompiler compiler = new MindcodeCompiler(profile);
        return compiler.compile(sourceCode);
    }
}
