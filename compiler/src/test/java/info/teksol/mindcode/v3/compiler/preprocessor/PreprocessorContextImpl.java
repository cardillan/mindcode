package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class PreprocessorContextImpl implements PreprocessorContext {
    private final MessageConsumer messageConsumer;
    private final CompilerProfile compilerProfile;
    private @Nullable AstAllocation stackAllocation;
    private @Nullable AstAllocation heapAllocation;

    PreprocessorContextImpl(MessageConsumer messageConsumer,
            CompilerProfile compilerProfile) {
        this.messageConsumer = messageConsumer;
        this.compilerProfile = compilerProfile;
    }

    @Override
    public MessageConsumer messageConsumer() {
        return messageConsumer;
    }

    @Override
    public CompilerProfile compilerProfile() {
        return compilerProfile;
    }

    @Override
    public void setStackAllocation(AstAllocation stackAllocation) {
        this.stackAllocation = stackAllocation;
    }

    @Override
    public void setHeapAllocation(AstAllocation heapAllocation) {
        this.heapAllocation = heapAllocation;
    }

    @Override
    public @Nullable AstAllocation stackAllocation() {
        return stackAllocation;
    }

    @Override
    public @Nullable AstAllocation heapAllocation() {
        return heapAllocation;
    }
}
