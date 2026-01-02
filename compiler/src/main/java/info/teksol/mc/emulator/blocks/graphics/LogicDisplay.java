package info.teksol.mc.emulator.blocks.graphics;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class LogicDisplay extends MindustryBuilding {
    private final TransformationMatrix transformationMatrix = new TransformationMatrix();

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
        graphicsBuffer.getCommands().forEach(command -> draw(graphicsBuffer, command));
        graphicsBuffer.clear();

        // TODO Take a snapshot and store it in the output list
    }

    private void draw(GraphicsBuffer graphicsBuffer, GraphicsCommand command) {
        // TODO Execute the command
    }

    public List<byte[]> getRenderedImages() {
        return snapshots;
    }

    public static LogicDisplay createLogicDisplay(MindustryMetadata metadata) {
        return new LogicDisplay("display", metadata.getExistingBlock("@logic-display"), 80);
    }

    public static LogicDisplay createLargeLogicDisplay(MindustryMetadata metadata) {
        return new LogicDisplay("display", metadata.getExistingBlock("@large-logic-display"), 176);
    }

    public static LogicDisplay createLogicDisplay(MindustryMetadata metadata, boolean large) {
        return large ? createLargeLogicDisplay(metadata) : createLogicDisplay(metadata);
    }
}
