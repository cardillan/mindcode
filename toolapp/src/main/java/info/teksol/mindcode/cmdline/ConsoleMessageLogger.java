package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConsoleMessageLogger extends ListMessageLogger {
    private final PositionFormatter positionFormatter;
    private final MessageLevel levelLimit;
    private final boolean preserveStdin;

    public ConsoleMessageLogger(PositionFormatter positionFormatter, MessageLevel levelLimit, boolean preserveStdin) {
        super();
        this.positionFormatter = positionFormatter;
        this.levelLimit = levelLimit;
        this.preserveStdin = preserveStdin;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        super.addMessage(message);
        if (levelLimit.weakerOrEqual(message.level())) {
            if (message.isErrorOrWarning() || preserveStdin) {
                System.err.println(message.formatMessage(positionFormatter));
            } else {
                System.out.println(message.formatMessage(positionFormatter));
            }
        }
    }
}
