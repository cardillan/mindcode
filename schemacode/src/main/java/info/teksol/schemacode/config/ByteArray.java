package info.teksol.schemacode.config;

public record ByteArray(byte[] bytes) implements Configuration {

    public int size() {
        return bytes.length;
    }
}
