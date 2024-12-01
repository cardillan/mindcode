package info.teksol.util;

import com.ibm.icu.impl.Assert;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Unit test helper class to manage error/warning messages generated by compiler.
 * A properly configured instance of this class is passed into the compiler as
 * a message consumer to assert that all expected messages were generated and
 * no unexpected messages appeared.
 * <p>
 * The expected messages are matched against the incoming messages in the order in which they
 * were configured. A single incoming message shouldn't be matched by more than one rule.
 */
public class ExpectedMessages implements Consumer<MindcodeMessage> {
    private final List<MatchCounter> matchers;
    private final List<String> messages = new ArrayList<>();
    private boolean accumulateErrors = false;

    private ExpectedMessages() {
        matchers = new ArrayList<>();
    }

    private ExpectedMessages(List<MatchCounter> matchers) {
        this.matchers = matchers;
    }

    /**
     * @return an instance which doesn't accept any message and generates Assert.fail()
     */
    public static ExpectedMessages none() {
        return new ExpectedMessages(List.of());
    }

    /**
     * @return an instance which doesn't accept any message and generates Assert.fail()
     */
    public static ExpectedMessages throwOnMessage() {
        return new ExpectedMessages(List.of()) {
            @Override
            public void accept(MindcodeMessage msg) {
                throw new UnexpectedMessageException("Unexpected message encountered: " + msg);
            }
        };
    }

    /**
     * Creates a new, empty instance. Messages to be recognized need to be added to the instance
     * using fluent interface.
     *
     * @return an instance which fails on unexpected or missing messages
     */
    public static ExpectedMessages create() {
        return new ExpectedMessages();
    }

    /**
     * Adds a message at certain level or lower.
     *
     * @param message message to be expected (trimmed for comparison)
     * @return this instance
     */
    public ExpectedMessages addLevelsUpTo(MessageLevel level) {
        matchers.add(new MatchCounter(new LevelMessageMatcher(level)));
        return this;
    }

    /**
     * Adds a new expected message.
     *
     * @param message message to be expected (trimmed for comparison)
     * @return this instance
     */
    public ExpectedMessages add(String message) {
        matchers.add(new MatchCounter(new TextMessageMatcher(message)));
        return this;
    }

    /**
     * Adds a new expected positional message. A message will only be matched when generated
     * at given position.
     *
     * @param line    expected line number
     * @param column  expected column number
     * @param message message to be expected (trimmed for comparison)
     * @return this instance
     */
    public ExpectedMessages add(int line, int column, String message) {
        matchers.add(new MatchCounter(new PositionalMessageMatcher(line, column, new TextMessageMatcher(message))));
        return this;
    }

    /**
     * Adds a new expected message.
     *
     * @param pattern regex pattern to be expected
     * @return this instance
     */
    public ExpectedMessages addRegex(@Language("RegExp") String pattern) {
        matchers.add(new MatchCounter(new RegexMessageMatcher(pattern)));
        return this;
    }

    /**
     * Adds a new expected positional message. A message will only be matched when generated
     * at given position.
     *
     * @param line    expected line number
     * @param column  expected column number
     * @param pattern regex pattern to be expected
     * @return this instance
     */
    public ExpectedMessages addRegex(int line, int column,  @Language("RegExp") String pattern) {
        matchers.add(new MatchCounter(new PositionalMessageMatcher(line, column, new RegexMessageMatcher(pattern))));
        return this;
    }

    /**
     * Sets the minimum and maximum repeat count for the last added message.
     *
     * @param minimumCount minimum expected number of repetitions
     * @param maximumCount maximum expected number of repetitions
     * @return this instance
     */
    public ExpectedMessages between(int minimumCount, int maximumCount) {
        if (matchers.isEmpty()) {
            throw new IllegalStateException("No matchers found");
        }
        if (minimumCount > maximumCount) {
            throw new IllegalArgumentException("Minimum count cannot be greater than maximum count");
        }
        matchers.getLast().setLimits(minimumCount, maximumCount);
        return this;
    }

    /**
     * Sets the minimum repeat count for the last added message.  The message must appear at least
     * the given number of times, but may appear more times.
     *
     * @param minimumCount minimum expected number of repetitions, must be at least 1.
     * @return this instance
     */
    public ExpectedMessages atLeast(int minimumCount) {
        if (minimumCount < 1) {
            throw new IllegalArgumentException("Minimum count must be at least 1");
        }
        return between(minimumCount, Integer.MAX_VALUE);
    }

    /**
     * Sets the maximum repeat count for the last added message. The message may appear at most
     * the given number of times. It may not appear at all.
     *
     * @param maximumCount maximum expected number of repetitions
     * @return this instance
     */
    public ExpectedMessages atMost(int maximumCount) {
        if (maximumCount < 1) {
            throw new IllegalArgumentException("Maximum count must be at least 1");
        }
        return between(0, maximumCount);
    }

    private static String formatPosition(InputPosition pos) {
        return "line " + pos.line() + ", column " + pos.column() + ", message";
    }

    /**
     * Sets the exact repeat count for the last added message.  The message must appear exactly
     * the given number of times.
     *
     * @param times the expected number of repetitions
     * @return this instance
     */
    public ExpectedMessages repeat(int times) {
        return between(times, times);
    }

    /**
     * Marks the message as ignored. The message may appear arbitrary number of times,
     * or it might not appear at all.
     * <p>
     * Marking a message as ignored suppresses outputting detected matches to stdout.
     *
     * @return this instance
     */
    public ExpectedMessages ignored() {
        matchers.getLast().setIgnored();
        return between(0, Integer.MAX_VALUE);
    }

    /**
     * Marks the message as forbidden. Not configuring the message at all would have the same effect.
     * Use this method to explicitly express certain message must not appear.
     *
     * @return this instance
     */
    public ExpectedMessages forbidden() {
        return between(0, 0);
    }

    @Override
    public void accept(MindcodeMessage msg) {
        for (MessageMatcher matcher : matchers) {
            if (matcher.matches(msg)) {
                return;
            }
        }

        fail("Unexpected message: " + msg.formatMessage(ExpectedMessages::formatPosition));
    }

    /**
     * Verifies that all expected messages were encountered. Needs to be called at the end
     * of the test to make sure the conditions were met.
     */
    private void validate(Runnable operation) {
        try {
            accumulateErrors = true;
            operation.run();
            accumulateErrors = false;
            if (!messages.isEmpty()) {
                Assert.fail(String.join("\n", messages));
            }
        } finally {
            accumulateErrors = false;
            messages.clear();
        }
    }


    /**
     * Verifies that all expected messages were encountered. Needs to be called at the end
     * of the test to make sure the conditions were met.
     */
    public void validate() {
        validate(() -> matchers.forEach(MatchCounter::validate));
    }

    /**
     * Executes the given action and then validates that all expected messages were generated.
     */
    public void validate(Consumer<ExpectedMessages> action) {
        validate(() -> {
            action.accept(this);
            validate();
        });
    }

    /**
     * Validates the given list of messages against the rules in this instance.
     */
    public void validate(List<MindcodeMessage> messages) {
        validate(() -> {
            messages.forEach(this);
            validate();
        });
    }

    private void fail(String errorMessage) {
        if (accumulateErrors) {
            messages.add(errorMessage);
        } else {
            Assert.fail(errorMessage);
        }
    }


    private class MatchCounter implements MessageMatcher {
        private final MessageMatcher matcher;
        private int minimumCount = 1;
        private int maximumCount = 1;
        private int matchCount = 0;
        private boolean ignored;

        private MatchCounter(MessageMatcher matcher) {
            this.matcher = matcher;
        }

        private void setLimits(int minimumCount, int maximumCount) {
            this.minimumCount = minimumCount;
            this.maximumCount = maximumCount;
        }

        private void setIgnored() {
            ignored = true;
        }

        @Override
        public boolean matches(MindcodeMessage msg) {
            if (matchCount < maximumCount && matcher.matches(msg)) {
                if (!ignored) {
                    System.out.println("Matched message " + msg.formatMessage(ExpectedMessages::formatPosition));
                }
                matchCount++;
                return true;
            } else {
                return false;
            }
        }

        private void validate() {
            if (matchCount < minimumCount) {
                if (minimumCount == 1) {
                    fail("Expected message not generated: " + matcher.message());
                } else {
                    fail("Message expected at least " + minimumCount + " times, but matched " + matchCount + " times: " + matcher.message());
                }
            }
        }

        @Override
        public String message() {
            return "";
        }
    }


    private interface MessageMatcher {
        boolean matches(MindcodeMessage msg);

        String message();
    }

    private record LevelMessageMatcher(MessageLevel level) implements MessageMatcher {
        @Override
        public boolean matches(MindcodeMessage msg) {
            return level.weakerOrEqual((msg.level()));
        }

        @Override
        public String message() {
            return "Messages at level " + level + " or weaker";
        }
    }

    private record TextMessageMatcher(String message) implements MessageMatcher {
        private TextMessageMatcher(String message) {
            this.message = message.trim();
        }

        @Override
        public boolean matches(MindcodeMessage msg) {
            return message.equals(msg.message().trim());
        }
    }

    private record RegexMessageMatcher(Pattern pattern) implements MessageMatcher {
        private RegexMessageMatcher(String pattern) {
            this(Pattern.compile(pattern));
        }

        @Override
        public boolean matches(MindcodeMessage msg) {
            return pattern.matcher(msg.message().trim()).matches();
        }

        @Override
        public String message() {
            return pattern().pattern();
        }
    }

    private record PositionalMessageMatcher(int line, int column, MessageMatcher matcher) implements MessageMatcher {
        @Override
        public boolean matches(MindcodeMessage msg) {
            return line == msg.inputPosition().line() && column == msg.inputPosition().column() && matcher.matches(msg);
        }

        @Override
        public String message() {
            return "line " + line + ", column " + column + ", message " + matcher.message();
        }
    }
}
