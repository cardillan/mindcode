package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

class InvalidHeapAllocationException extends ParsingException {
    InvalidHeapAllocationException(String message) {
        super(message);
    }
}
