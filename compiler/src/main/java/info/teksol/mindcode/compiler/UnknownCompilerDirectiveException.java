package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeException;

public class UnknownCompilerDirectiveException extends MindcodeException {
    public UnknownCompilerDirectiveException(String message) {
        super(message);
    }
}
