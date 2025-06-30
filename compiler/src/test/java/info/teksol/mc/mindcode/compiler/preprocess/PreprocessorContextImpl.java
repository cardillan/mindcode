package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PreprocessorContextImpl extends AbstractMessageEmitter implements PreprocessorContext {
    private final DirectiveProcessor directiveProcessor;
    private final CompilerProfile compilerProfile;

    PreprocessorContextImpl(MessageConsumer messageConsumer, CompilerProfile compilerProfile) {
        super(messageConsumer);
        this.directiveProcessor = new DirectiveProcessor(messageConsumer);
        this.compilerProfile = compilerProfile;
    }

    @Override
    public DirectiveProcessor directiveProcessor() {
        return directiveProcessor;
    }

    @Override
    public CompilerProfile compilerProfile() {
        return compilerProfile;
    }
}
