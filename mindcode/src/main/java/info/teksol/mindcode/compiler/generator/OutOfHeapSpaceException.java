package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ParsingException;

public class OutOfHeapSpaceException extends ParsingException {
    OutOfHeapSpaceException(String message) {
        super(message);
    }
}
