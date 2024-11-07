package info.teksol.schemacode.schematics;

import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.ProcessorConfiguration;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockPositionMapTest extends AbstractSchematicsTest {

    @Test
    void refusesOverlappingBlocks1x1() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Overlapping blocks: #0 '@message' at \\(0, 0\\) and #1 '@switch' at \\(0, 0\\)\\."),
                """
                        schematic
                            @message at (0, 0)
                            @switch  at (0, 0)
                        end
                        """
        );
    }

    @Test
    void refusesOverlappingBlocks2x2() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Overlapping blocks: #0 '@power-node-large' at \\(0, 0\\) - \\(1, 1\\) and #1 '@kiln' at \\(1, 1\\) - \\(2, 2\\)\\."),
                """
                        schematic
                            @power-node-large at (0, 0)
                            @kiln  at (1, 1)
                        end
                        """
        );
    }

    @Test
    void refusesOverlappingBlocks3x3() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Overlapping blocks: #0 '@battery-large' at \\(0, 0\\) - \\(2, 2\\) and #1 '@switch' at \\(2, 2\\)\\."),
                """
                        schematic
                            @battery-large at (0, 0)
                            @switch  at (2, 2)
                        end
                        """
                );
    }

    @Test
    void supportsLinkingNodesByAnyTile() {
        Schematic actual = buildSchematics("""
                schematic
                    @power-node     at (0, 0) connected to (4, 2)
                    @battery-large  at (2, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 3,
                List.of(
                        block(pos(2, 5), "@power-node", P0_0, Direction.EAST, pa(P2_0)),
                        block(pos(3, 5), "@battery-large", P2_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void supportsLinkingProcessorsByAnyTile() {
        Schematic actual = buildSchematics("""
                schematic
                    @micro-processor at (0, 0) processor
                        links (4, 2) as battery1 end
                    end
                    @battery-large   at (2, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 3,
                List.of(
                        block(pos(2, 5), "@micro-processor", P0_0, Direction.EAST,
                                new ProcessorConfiguration(List.of(new Link("battery1", 2, 0)), "")
                        ),
                        block(pos(5, 5), "@battery-large", P2_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }
}