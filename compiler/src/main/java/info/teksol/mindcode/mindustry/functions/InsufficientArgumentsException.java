package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.generator.GenerationException;

public class InsufficientArgumentsException extends GenerationException {
    InsufficientArgumentsException(String message) {
        super(message);
    }
}
