package info.teksol.mc.messages;

public class UnexpectedMessageException extends RuntimeException {
    public UnexpectedMessageException(String message) {
        super(message);
    }
}
