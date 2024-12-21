package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.MessageConsumer;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PreprocessorContextImpl implements PreprocessorContext {
    private final MessageConsumer messageConsumer;
    private final CompilerProfile compilerProfile;

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
}
