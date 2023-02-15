package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.generator.GenerationException;

public class WrongNumberOfParametersException extends GenerationException {
    WrongNumberOfParametersException(String message) {
        super(message);
    }
}
