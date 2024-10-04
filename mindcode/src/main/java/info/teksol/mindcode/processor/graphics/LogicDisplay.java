package info.teksol.mindcode.processor.graphics;

import info.teksol.mindcode.compiler.instructions.DrawInstruction;
import info.teksol.mindcode.processor.MindustryObject;

import java.util.ArrayList;
import java.util.List;

public class LogicDisplay extends MindustryObject {
    private final int sizeX;
    private final int sizeY;

    private final List<byte[]> snapshots = new ArrayList<>();

    public LogicDisplay(String name, String value, int sizeX, int sizeY) {
        super(name, value);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public LogicDisplay(String name, String value, int size) {
        super(name, value);
        this.sizeX = size;
        this.sizeY = size;
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

    public static LogicDisplay createLogicDisplay(String name) {
        return new LogicDisplay(name, "display", 80);
    }

    public static LogicDisplay createLargeLogicDisplay(String name) {
        return new LogicDisplay(name, "display", 176);
    }

    public static LogicDisplay createLogicDisplay(String name, boolean large) {
        return new LogicDisplay(name, "display", large ? 176 : 80);
    }
}
