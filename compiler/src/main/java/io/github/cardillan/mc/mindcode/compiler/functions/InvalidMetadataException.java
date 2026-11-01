package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidMetadataException extends MindcodeInternalError {
    InvalidMetadataException(String message) {
        super(message);
    }
}
