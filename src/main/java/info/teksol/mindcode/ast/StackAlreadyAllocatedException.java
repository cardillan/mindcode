package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

class StackAlreadyAllocatedException extends ParsingException {
    StackAlreadyAllocatedException(String message) {
        super(message);
    }
}
