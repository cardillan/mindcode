package io.github.cardillan.schemacode.config;

public record ByteArray(byte[] bytes) implements Configuration {

    public int size() {
        return bytes.length;
    }
}
