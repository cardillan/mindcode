package io.github.cardillan.schemacode;

import java.io.IOException;

public interface IOConsumer<E> {
    void accept(E item) throws IOException;
}
