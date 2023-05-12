package info.teksol.schemacode;

import java.io.IOException;

public interface IOSupplier<T> {
    T get() throws IOException;
}
