package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.GenerationException;

public class InvalidMetadataException extends GenerationException {
    InvalidMetadataException(String message) {
        super(message);
    }
}
