package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.MindcodeInternalError;

public class InvalidMetadataException extends MindcodeInternalError {
    InvalidMetadataException(String message) {
        super(message);
    }
}
