package info.teksol.mindcode.webapp;

import info.teksol.mindcode.MindcodeMessage;

public final class WebappMessage {
    private final String prefix;
    private final boolean position;
    private final int line;
    private final int charPositionInLine;
    private final String message;

    public WebappMessage(String prefix, boolean position, int line, int charPositionInLine, String message) {
        this.prefix = prefix;
        this.position = position;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = message;
    }

    public boolean hasPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage() {
        return message;
    }

    public String getPosition() {
        return position ? prefix + " at line " + line + ", column " + charPositionInLine : "";
    }

    public static WebappMessage transform(MindcodeMessage msg) {
        if (msg.inputPosition().isEmpty()) {
            return new WebappMessage("", false, -1, -1, msg.message());
        } else if (msg.inputPosition().inputFile().isLibrary()) {
            String position = " at " + msg.inputPosition().inputFile().getDistinctPath() + ":" + msg.inputPosition().line() + ":" + msg.inputPosition().column() + ": ";
            return new WebappMessage("", false, -1, -1, msg.level().getTitle() + position + msg.message());
        } else {
            return new WebappMessage(msg.level().getTitle(), true, msg.inputPosition().line(), msg.inputPosition().column(), msg.message());
        }
    }
}
