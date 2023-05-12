package info.teksol.schemacode.config;

import info.teksol.schemacode.IOConsumer;
import info.teksol.schemacode.IOSupplier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record Array<E>(Class<E> dataClass, List<E> array) implements Configuration {

    @Override
    public String toString() {
        return dataClass.getSimpleName() + "[" + array.size() + "]";
    }

    public int size() {
        return array.size();
    }

    public void store(IOConsumer<E> consumer) throws IOException {
        for (E item : array) {
            consumer.accept(item);
        }
    }

    public static <T> Array<T> create(Class<T> dataClass, int length, IOSupplier<T> supplier) throws IOException {
        List<T> array = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            array.add(supplier.get());
        }
        return new Array<>(dataClass, array);
    }
}
