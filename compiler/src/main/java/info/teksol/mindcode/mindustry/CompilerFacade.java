package info.teksol.mindcode.mindustry;

public class CompilerFacade {
    public static CompilerOutput compile(String sourceCode, boolean enableOptimization) {
        return compile(sourceCode, enableOptimization ? CompilerProfile.fullOptimizations() : CompilerProfile.noOptimizations());
    }
    
    public static CompilerOutput compile(String sourceCode, CompilerProfile profile) {
        return CompilerFactory.createCompiler(profile).compile(sourceCode);
    }
}
