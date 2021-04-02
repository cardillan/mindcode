package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

class HeapAlreadyAllocatedException extends ParsingException {
    HeapAlreadyAllocatedException(String message) {
        super(message);
    }
}
