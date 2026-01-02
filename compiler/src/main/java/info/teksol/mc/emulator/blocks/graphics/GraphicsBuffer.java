package info.teksol.mc.emulator.blocks.graphics;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class GraphicsBuffer {
    private final int sizeLimit;
    private final List<GraphicsCommand> commands = new ArrayList<>();

    public GraphicsBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public boolean draw(GraphicsCommand command) {
        if (commands.size() < sizeLimit) {
            commands.add(command);
            return true;
        }
        return false;
    }

    public List<GraphicsCommand> getCommands() {
        return commands;
    }

    public void clear() {
        commands.clear();
    }

    public static final GraphicsBuffer EMPTY = new GraphicsBuffer(0);
}
