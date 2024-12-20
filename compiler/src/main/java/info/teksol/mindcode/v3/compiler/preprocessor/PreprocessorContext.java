package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.CompilerContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface PreprocessorContext extends CompilerContext {
    CompilerProfile compilerProfile();

    void setStackAllocation(AstAllocation stackAllocation);
    void setHeapAllocation(AstAllocation heapAllocation);

    @Nullable AstAllocation stackAllocation();
    @Nullable AstAllocation heapAllocation();
}
