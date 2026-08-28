package info.teksol.mc.common;

public class Globals {
    public static final int MAX_INSTRUCTIONS = 1000;
    public static final int MAX_JUMPS = 500;
    public static final int MAX_MLOG_BYTE_LENGTH = 1024 * 100;
    public static final int MAX_PROCESSOR_CFG_SIZE = 16_000;
    public static final int CFG_SIZE_SAFETY_MARGIN = 1_000;

    public static final int MAX_INTERNAL_ARRAY_SIZE = 1000;
    public static final int MAX_EXTERNAL_ARRAY_SIZE = 2048;

    // Arrays larger than this are always copied using loops
    public static final int DIRECT_ARRAY_COPY_SIZE_LIMIT = 25;
}
