package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;

public class InvalidMetadataException extends MindcodeInternalError {
    InvalidMetadataException(String message) {
        super(message);
    }
}
