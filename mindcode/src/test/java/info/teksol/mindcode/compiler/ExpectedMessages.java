package info.teksol.mindcode.compiler;

import com.ibm.icu.impl.Assert;
import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.MindcodeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

public class ExpectedMessages implements Consumer<MindcodeMessage> {
    private final List<MessageMatcher> matchers;
    private final boolean throwOnError;

    private ExpectedMessages(boolean throwOnError) {
        this.throwOnError = throwOnError;
        matchers = new ArrayList<>();
    }

    private ExpectedMessages(List<MessageMatcher> matchers, boolean throwOnError) {
        this.matchers = matchers;
        this.throwOnError = throwOnError;
    }

    public ExpectedMessages add(String message) {
        matchers.add(new TextMessageMatcher(message));
        return this;
    }

    public ExpectedMessages add(int line, int column, String message) {
        matchers.add(new PositionalMessageMatcher(line, column, message));
        return this;
    }

    @Override
    public void accept(MindcodeMessage msg) {
        for (MessageMatcher matcher : matchers) {
            if (matcher.matches(msg)) {
                return;
            }
        }

        String errorMessage = "Unexpected message: " + msg.formatMessage(
                pos -> "line " + pos.line() + ", column " + pos.charPositionInLine() + ", message");

        if (throwOnError) {
            throw new UnexpectedMessageException(errorMessage);
        } else {
            fail(errorMessage);
        }
    }

    public void validate() {
        matchers.forEach(MessageMatcher::validate);
    }

    public void validate(Consumer<ExpectedMessages> action) {
        action.accept(this);
        validate();
    }

    public void validate(List<MindcodeMessage> messages) {
        messages.forEach(this);
        validate();
    }

    private static void refuseMessages(MindcodeMessage message) {
        fail("Unexpected message: " + message);
    }

    public static Consumer<MindcodeMessage> refuseAll() {
        return ExpectedMessages::refuseMessages;
    }

    public static ExpectedMessages none() {
        return new ExpectedMessages(List.of(), false);
    }

    public static ExpectedMessages none(boolean throwOnError) {
        return new ExpectedMessages(List.of(), throwOnError);
    }

    public static ExpectedMessages create() {
        return new ExpectedMessages(false);
    }

    public static ExpectedMessages create(boolean throwOnError) {
        return new ExpectedMessages(throwOnError);
    }

    private interface MessageMatcher {
        boolean matches(MindcodeMessage msg);
        void validate();
    }

    private static class TextMessageMatcher implements MessageMatcher {
        private final String message;
        private boolean matched;

        public TextMessageMatcher(String message) {
            this.message = message.trim();
        }

        @Override
        public boolean matches(MindcodeMessage msg) {
            if (!matched && message.equals(msg.message().trim())) {
                matched = true;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void validate() {
            if (!matched) {
                Assert.fail("Expected message not generated: " + message);
            }
        }
    }

    private static class PositionalMessageMatcher implements MessageMatcher {
        private final int line;
        private final int column;
        private final String message;
        private boolean matched;

        public PositionalMessageMatcher(int line, int column, String message) {
            this.line = line;
            this.column = column;
            this.message = message;
        }

        @Override
        public boolean matches(MindcodeMessage msg) {
            if (!matched && msg instanceof CompilerMessage m) {
                if (line == m.inputPosition().line() && column == m.inputPosition().charPositionInLine() && message.equals(m.message())) {
                    matched = true;
                    return true;
                }
            }

            return false;
        }

        @Override
        public void validate() {
            if (!matched) {
                Assert.fail("Expected message not generated: line " + line + ", column " + column + ", message "  + message);
            }
        }
    }
}
