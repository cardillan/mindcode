package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public class ConsoleMessageLogger extends ListMessageLogger {
    private final PositionFormatter positionFormatter;
    private final boolean preserveStdin;
    private final boolean fullConsole;

    private ConsoleMessageLogger(PositionFormatter positionFormatter, boolean preserveStdin, boolean fullConsole) {
        this.positionFormatter = positionFormatter;
        this.preserveStdin = preserveStdin;
        this.fullConsole = fullConsole;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        if (!fullConsole && message.logFile()) {
            super.addMessage(message);
        }

        if (fullConsole || message.console()) {
            if (message.isErrorOrWarning() || preserveStdin) {
                System.err.println(message.formatMessage(positionFormatter));
            } else {
                System.out.println(message.formatMessage(positionFormatter));
            }
        }
    }

    static boolean isStdInOut(File file) {
        return file.getPath().equals("-");
    }

    public static ConsoleMessageLogger create(PositionFormatter positionFormatter, boolean preserveStdin, boolean fullConsole) {
        return new ConsoleMessageLogger(positionFormatter, preserveStdin, fullConsole);
    }

    public static ConsoleMessageLogger create(PositionFormatter positionFormatter, File outputFile, File logFile) {
        return create(positionFormatter, isStdInOut(outputFile), isStdInOut(logFile));
    }
}
