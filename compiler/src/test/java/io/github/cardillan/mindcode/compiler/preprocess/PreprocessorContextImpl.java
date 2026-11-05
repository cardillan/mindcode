package io.github.cardillan.mindcode.compiler.preprocess;

import io.github.cardillan.mindcode.messages.AbstractMessageEmitter;
import io.github.cardillan.mindcode.messages.MessageConsumer;
import io.github.cardillan.mindcode.profile.DirectiveProcessor;
import io.github.cardillan.mindcode.profile.GlobalCompilerProfile;
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
