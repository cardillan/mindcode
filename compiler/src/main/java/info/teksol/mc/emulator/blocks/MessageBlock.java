package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.TextBuffer;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageBlock extends MindustryBuilding {
    private final StringBuilder message = new StringBuilder();

    public MessageBlock(String name, BlockType type, BlockPosition position) {
        super(name, type, position);
    }

    public void printflush(TextBuffer textBuffer) {
        textBuffer.printflush(message);
    }

    public StringBuilder getMessage() {
        return message;
    }

    public static MessageBlock createMessage(MindustryMetadata metadata, BlockPosition position) {
        return new MessageBlock("message", metadata.getExistingBlock("@message"), position);
    }
}
