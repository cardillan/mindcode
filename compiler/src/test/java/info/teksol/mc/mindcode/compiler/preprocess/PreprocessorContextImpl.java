package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.profile.CompilerProfile;
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
