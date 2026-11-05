package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidMetadataException extends MindcodeInternalError {
    InvalidMetadataException(String message) {
        super(message);
    }
}
