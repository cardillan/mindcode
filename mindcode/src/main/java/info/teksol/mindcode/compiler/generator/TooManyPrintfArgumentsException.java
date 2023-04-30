package info.teksol.mindcode.compiler.generator;

public class TooManyPrintfArgumentsException extends GenerationException {
    TooManyPrintfArgumentsException(String message) {
        super(message);
    }
}
