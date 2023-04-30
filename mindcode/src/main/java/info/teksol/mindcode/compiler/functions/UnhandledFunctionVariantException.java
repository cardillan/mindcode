package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.GenerationException;

public class UnhandledFunctionVariantException extends GenerationException {
    UnhandledFunctionVariantException(String message) {
        super(message);
    }
}
