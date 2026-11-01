package io.github.cardillan.mc.mindcode.compiler.preprocess;

import io.github.cardillan.mc.messages.AbstractMessageEmitter;
import io.github.cardillan.mc.messages.MessageConsumer;
import io.github.cardillan.mc.profile.DirectiveProcessor;
import io.github.cardillan.mc.profile.GlobalCompilerProfile;
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
