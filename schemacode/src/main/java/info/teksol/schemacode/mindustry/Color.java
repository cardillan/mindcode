package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.IntConfiguration;
import info.teksol.schemacode.schema.Block;

public record Color(int red, int green, int blue, int alpha) implements Configuration {

    public static final Color WHITE = new Color(255, 255, 255, 255);

    @Override
    public Configuration encode(Block block) {
        return new IntConfiguration(encode());
    }

    public int encode() {
        return (red << 24) | (green << 16) | (blue << 8) | alpha;
    }

    public static Color decode(int value) {
        int red   = ((value & 0xff000000) >>> 24);
        int green = ((value & 0x00ff0000) >>> 16);
        int blue  = ((value & 0x0000ff00) >>> 8);
        int alpha = ((value & 0x000000ff));
        return new Color(red, green, blue, alpha);
    }
}
