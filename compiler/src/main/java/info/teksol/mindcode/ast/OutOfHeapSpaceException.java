package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class OutOfHeapSpaceException extends ParsingException {
    OutOfHeapSpaceException(String message) {
        super(message);
    }
}
