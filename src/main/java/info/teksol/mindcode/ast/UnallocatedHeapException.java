package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

public class UnallocatedHeapException extends ParsingException {
    UnallocatedHeapException(String message) {
        super(message);
    }

}
