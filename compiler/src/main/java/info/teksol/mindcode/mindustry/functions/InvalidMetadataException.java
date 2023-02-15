package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.generator.GenerationException;

public class InvalidMetadataException extends GenerationException {
    InvalidMetadataException(String message) {
        super(message);
    }
}
