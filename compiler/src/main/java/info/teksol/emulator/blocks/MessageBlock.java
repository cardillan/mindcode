package info.teksol.emulator.blocks;

import info.teksol.emulator.processor.TextBuffer;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.mindcode.mimex.MindustryContent;

public class MessageBlock extends MindustryBlock {
    private final StringBuilder contents = new StringBuilder();

    public MessageBlock(String name, MindustryContent type) {
        super(name, type);
    }

    public void printflush(TextBuffer textBuffer) {
        textBuffer.printflush(contents);
    }

    public static MessageBlock createMessage() {
        return new MessageBlock("message", BlockType.forName("@message"));
    }
}
