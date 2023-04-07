package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ParsingException;

class InvalidMemoryAllocationException extends ParsingException {
    InvalidMemoryAllocationException(String message) {
        super(message);
    }
}
