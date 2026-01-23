package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConsoleMessageLogger extends ListMessageLogger {
    private final PositionFormatter positionFormatter;
    private final boolean preserveStdin;

    public ConsoleMessageLogger(PositionFormatter positionFormatter, boolean preserveStdin) {
        this.positionFormatter = positionFormatter;
        this.preserveStdin = preserveStdin;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        if (message.logFile()) {
            super.addMessage(message);
        }

        if (message.console()) {
            if (message.isErrorOrWarning() || preserveStdin) {
                System.err.println(message.formatMessage(positionFormatter));
            } else {
                System.out.println(message.formatMessage(positionFormatter));
            }
        }
    }
}
