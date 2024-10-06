package info.teksol.emulator.blocks.graphics;

import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.mindcode.compiler.instructions.DrawInstruction;
import info.teksol.mindcode.mimex.BlockType;

import java.util.ArrayList;
import java.util.List;

public class LogicDisplay extends MindustryBlock {
    private final int sizeX;
    private final int sizeY;

    private final List<byte[]> snapshots = new ArrayList<>();

    public LogicDisplay(String name, BlockType type, int sizeX, int sizeY) {
        super(name, type);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public LogicDisplay(String name, BlockType type, int size) {
        this(name, type, size, size);
    }

    public void drawflush(GraphicsBuffer graphicsBuffer) {
        // Apply the contents of the graphics buffer onto the image
        graphicsBuffer.getDrawInstructions().forEach(instruction -> draw(graphicsBuffer, instruction));
        graphicsBuffer.clear();

        // TODO Take a snapshot and store it in the output list
    }

    private void draw(GraphicsBuffer graphicsBuffer, DrawInstruction instruction) {
        // TODO Execute the instruction
        // TODO Use the transformation matrix from GraphicsBuffer
    }

    public List<byte[]> getRenderedImages() {
        return snapshots;
    }

    public static LogicDisplay createLogicDisplay() {
        return new LogicDisplay("display", BlockType.forName("@logic-display"), 80);
    }

    public static LogicDisplay createLargeLogicDisplay() {
        return new LogicDisplay("display", BlockType.forName("@large-logic-display"), 176);
    }

    public static LogicDisplay createLogicDisplay(boolean large) {
        return large ? createLargeLogicDisplay() : createLogicDisplay();
    }
}
