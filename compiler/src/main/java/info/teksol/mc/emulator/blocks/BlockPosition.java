package info.teksol.mc.emulator.blocks;

public record BlockPosition(int x, int y) {
    public static final BlockPosition ZERO_POSITION = new BlockPosition(0, 0);
}
