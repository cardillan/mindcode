package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.generator.GenerationException;

public class UnhandledFunctionVariantException extends GenerationException {
    UnhandledFunctionVariantException(String message) {
        super(message);
    }
}
