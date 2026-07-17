package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.profile.DirectiveProcessor;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PreprocessorContextImpl extends AbstractMessageEmitter implements PreprocessorContext {
    private final GlobalCompilerProfile globalCompilerProfile;
    private final DirectiveProcessor directiveProcessor;

    PreprocessorContextImpl(MessageConsumer messageConsumer, GlobalCompilerProfile globalCompilerProfile) {
        super(messageConsumer);
        this.globalCompilerProfile = globalCompilerProfile;
        this.directiveProcessor = new DirectiveProcessor(messageConsumer);
    }

    @Override
    public GlobalCompilerProfile globalCompilerProfile() {
        return globalCompilerProfile;
    }

    @Override
    public DirectiveProcessor directiveProcessor() {
        return directiveProcessor;
    }
}
