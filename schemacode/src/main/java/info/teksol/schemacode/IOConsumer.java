package info.teksol.schemacode;

import java.io.IOException;

public interface IOConsumer<E> {
    void accept(E item) throws IOException;
}
