package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PreprocessorContextImpl extends AbstractMessageEmitter implements PreprocessorContext {
    private final DirectiveProcessor directiveProcessor;

    PreprocessorContextImpl(MessageConsumer messageConsumer) {
        super(messageConsumer);
        this.directiveProcessor = new DirectiveProcessor(messageConsumer);
    }

    @Override
    public DirectiveProcessor directiveProcessor() {
        return directiveProcessor;
    }
}
