package info.teksol.mindcode.webapp;

import info.teksol.mc.common.TextFilePosition;
import info.teksol.mc.messages.MindcodeMessage;

public final class WebappMessage {
    private final String prefix;
    private final boolean position;
    private final TextFilePosition start;
    private final TextFilePosition end;
    private final String message;

    public WebappMessage(String prefix, boolean position, TextFilePosition start, TextFilePosition end, String message) {
        this.prefix = prefix;
        this.position = position;
        this.start = start;
        this.end = end;
        this.message = message;
    }

    public boolean hasPosition() {
        return position;
    }

    public int getStartLine() {
        return start.line();
    }

    public int getStartColumn() {
        return start.column();
    }
    
    public int getEndLine() {
        return end.line();
    }

    public int getEndColumn() {
        return end.column();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage() {
        return message;
    }

    public String getPosition() {
        return position ? prefix + " at line " + getStartLine() + ", column " + getStartColumn() : "";
    }

    public static WebappMessage transform(MindcodeMessage msg) {
        if (msg.sourcePosition().isEmpty()) {
            return new WebappMessage("", false, null, null, msg.message());
        } else if (msg.sourcePosition().isLibrary()) {
            String position = " at " + msg.sourcePosition().getDistinctPath() + ":" + msg.sourcePosition().line() + ":" + msg.sourcePosition().column() + ": ";
            return new WebappMessage("", false, null, null, msg.level().getTitle() + position + msg.message());
        } else {
            return new WebappMessage(msg.level().getTitle(), true, msg.sourcePosition().start(), msg.sourcePosition().end(), msg.message());
        }
    }
}
