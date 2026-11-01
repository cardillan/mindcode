package io.github.cardillan.schemacode.mindustry;

import io.github.cardillan.mc.mindcode.logic.mimex.BlockType;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class ImplementationTest {

    private final MindustryMetadata metadata = MindustryMetadata.forVersion(ProcessorVersion.MAX);

    @Test
    void ensureAllImplementationsExist() {
        List<BlockType> blocks = metadata.getAllBlocks()
                .stream()
                .filter(b -> Implementation.fromBlockType(b) == null)
                .toList();

        blocks.forEach(b -> System.out.println("Block type " + b.name() + ": missing implementation " + b.implementation()));
        assertTrue(blocks.isEmpty());
    }
}
