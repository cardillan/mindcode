package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.MindcodeException;

public class InvalidInstructionException extends MindcodeException {
    InvalidInstructionException(String message) {
        super(message);
    }
}
