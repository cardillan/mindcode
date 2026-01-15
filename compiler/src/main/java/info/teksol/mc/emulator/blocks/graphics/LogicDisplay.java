package info.teksol.mc.emulator.blocks.graphics;

import info.teksol.mc.emulator.blocks.BlockPosition;
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

    public LogicDisplay(String name, BlockType type, BlockPosition position, int sizeX, int sizeY) {
        super(name, type, position);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public LogicDisplay(String name, BlockType type, BlockPosition position, int size) {
        this(name, type, position, size, size);
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

    public static LogicDisplay createLogicDisplay(MindustryMetadata metadata, BlockPosition position) {
        return new LogicDisplay("display", metadata.getExistingBlock("@logic-display"), position, 80);
    }

    public static LogicDisplay createLargeLogicDisplay(MindustryMetadata metadata, BlockPosition position) {
        return new LogicDisplay("display", metadata.getExistingBlock("@large-logic-display"), position, 176);
    }

    public static LogicDisplay createLogicDisplay(MindustryMetadata metadata, boolean large, BlockPosition position) {
        return large ? createLargeLogicDisplay(metadata, position) : createLogicDisplay(metadata, position);
    }
}
