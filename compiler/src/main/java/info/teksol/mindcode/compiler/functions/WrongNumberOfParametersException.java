package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.GenerationException;

public class WrongNumberOfParametersException extends GenerationException {
    public WrongNumberOfParametersException(String message) {
        super(message);
    }
}
