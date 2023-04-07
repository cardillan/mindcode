package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeException;

public class InvalidInstructionException extends MindcodeException {
    InvalidInstructionException(String message) {
        super(message);
    }
}
