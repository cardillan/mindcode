package info.teksol.schemacode.schema;

import info.teksol.mindcode.mimex.Icons;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.config.BooleanConfiguration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.config.ProcessorConfiguration;
import info.teksol.schemacode.config.ProcessorConfiguration.Link;
import info.teksol.schemacode.config.TextConfiguration;
import info.teksol.schemacode.mimex.BlockType;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Item;
import info.teksol.schemacode.mindustry.Liquid;
import info.teksol.schemacode.mindustry.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchematicsBuilderTest extends AbstractSchematicsTest {

    public static final Position P0_0 = Position.ORIGIN;
    public static final Position P1_0 = new Position(1, 0);
    public static final Position P2_0 = new Position(2, 0);
    public static final Position P3_0 = new Position(3, 0);


    @Test
    void unwrapsStringBlock() {
        String expected = """
                This is first line.
                This is second line.""";

        String original = """
                This is
                first line.
                                
                This is
                second line.""";

        String actual = SchematicsBuilder.unwrap(original);
        assertEquals(expected, actual);
    }

    @Test
    void unwrapsStringBlockRemovingEmptyLines() {
        String expected = """
                This is first line.
                This is second line.""";

        String original = """
                                
                This is
                first line.
                                
                                
                This is
                second line.
                                
                """;

        String actual = SchematicsBuilder.unwrap(original);
        assertEquals(expected, actual);
    }

    @Test
    void refusesMissingSchematics() {
        buildSchematicsExpectingMessage("""
                        constant = "foo"
                        """,
                "No schematic defined.");
    }

    @Test
    void refusesMultipleSchematics() {
        buildSchematicsExpectingMessage("""
                        schematic
                            name = "First"
                        end
                                        
                        schematic
                            name = "Second"
                        end
                        """,
                "More than one schematic defined.");
    }

    @Test
    void refusesConstantRedefinitions() {
        buildSchematicsExpectingMessage("""
                        schematic
                            name = "Schematics"
                        end
                                        
                        constant = "foo"
                        constant = "bar"
                        """,
                "Identifier 'constant' defined more than once.");
    }

    @Test
    void refusesUndefinedConstants() {
        buildSchematicsExpectingMessage("""
                        schematic
                            name = foo
                        end
                        """,
                "Undefined identifier 'foo'.");
    }

    @Test
    void refusesMultipleBlocksAsPosition() {
        buildSchematicsExpectingMessage("""
                        schematic
                            @switch at (0, 0)
                            @switch at (0, 0)
                        end
                        """,
                "Multiple blocks at position \\(\\s*0,\\s*0\\).");
    }

    @Test
    void refusesReusedBlockLabels() {
        buildSchematicsExpectingMessage("""
                        schematic
                            switch1: @switch at (0, 0)
                            switch1: @switch at (1, 0)
                        end
                        """,
                "Multiple definitions of block label 'switch1'.");
    }

    @Test
    void refusesCircularPositionDefinition() {
        buildSchematicsExpectingMessage("""
                        schematic
                        switch1:
                            @switch at (0, 0)
                        switch2:
                            @switch at switch3 + (1, 0)
                        switch3:
                            @switch at switch2 - (1, 0)
                        end
                        """,
                "Circular definition of block 'switch2' position.");
    }

    @Test
    void refusesUnknownBlockReference() {
        buildSchematicsExpectingMessage("""
                        schematic
                        switch1:
                            @switch at (0, 0)
                        switch2:
                            @switch at switch3 + (1, 0)
                        end
                        """,
                "Unknown block name 'switch3'.");
    }

    @Test
    void buildsSchematicsWithName() {
        Schematics actual = buildSchematics("""
                schematic
                    name = "Name"
                end
                """);

        Schematics expected = new Schematics("Name", "", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithIndirectName() {
        Schematics actual = buildSchematics("""
                schematic
                    name = my_name
                end
                                
                my_name = "Name"
                """);

        Schematics expected = new Schematics("Name", "", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }


    @Test
    void refuseMultipleNames() {
        buildSchematicsExpectingMessage("""
                schematic
                    name = "Name"
                    name = "Another"
                end
                """,
                "Multiple definitions of attribute 'name'.");
    }

    @Test
    void buildsSchematicsWithDescription() {
        Schematics actual = buildSchematics("""
                schematic
                    description = "Description"
                end
                """);

        Schematics expected = new Schematics("", "Description", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithTextTag() {
        Schematics actual = buildSchematics("""
                schematic
                    tag = "tag"
                end
                """);

        Schematics expected = new Schematics("", "", List.of("tag"), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithIconTag() {
        Schematics actual = buildSchematics("""
                schematic
                    tag = ITEM-COAL
                end
                """);

        Schematics expected = new Schematics("", "", List.of(Icons.translateIcon("ITEM-COAL")), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMergedTags() {
        Schematics actual = buildSchematics("""
                schematic
                    tag = "tag"
                    tag = "tag"
                end
                """);

        Schematics expected = new Schematics("", "", List.of("tag"), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithDimensions() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (5, 7)
                    @message at (4, 6)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 5, 7,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), new Position(4, 6), Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainMessage() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainSwitch() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @switch at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@switch"), P0_0, Direction.EAST, BooleanConfiguration.FALSE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainNode() {
        Schematics actual = buildSchematics("""
                schematic
                    @power-node at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@power-node"), P0_0, Direction.EAST, PositionArray.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainBridge() {
        Schematics actual = buildSchematics("""
                schematic
                    @bridge-conveyor at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@bridge-conveyor"), P0_0, Direction.EAST, Position.INVALID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainUnloader() {
        Schematics actual = buildSchematics("""
                schematic
                    @unloader at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@unloader"), P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainLiquidSource() {
        Schematics actual = buildSchematics("""
                schematic
                    @liquid-source at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@liquid-source"), P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainMicroProcessor() {
        Schematics actual = buildSchematics("""
                schematic
                    @micro-processor at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@micro-processor"), P0_0, Direction.EAST, new ProcessorConfiguration())
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithBlockLabel() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                message1:
                    @message at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of("message1"), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMultipleBlockLabel() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                message1, message2:
                    @message at (0, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of("message1", "message2"), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithRelativeBlockPosition() {
        Schematics actual = buildSchematics("""
                schematic
                    @message at (0, 0)
                    @message at + (1, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 2, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        new Block(List.of(), BlockType.forName("@message"), P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithRelativeToBlockPosition() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (2, 1)
                message1:
                    @message at (0, 0)
                    @message at message1 + (1, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 2, 1,
                List.of(
                        new Block(List.of("message1"), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        new Block(List.of(), BlockType.forName("@message"), P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithAllDirections() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (4, 1)
                    @message at  (0, 0) facing east
                    @message at +(1, 0) facing west
                    @message at +(1, 0) facing north
                    @message at +(1, 0) facing south
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 4, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        new Block(List.of(), BlockType.forName("@message"), P1_0, Direction.WEST, TextConfiguration.EMPTY),
                        new Block(List.of(), BlockType.forName("@message"), P2_0, Direction.NORTH, TextConfiguration.EMPTY),
                        new Block(List.of(), BlockType.forName("@message"), P3_0, Direction.SOUTH, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMessageText() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) text "This is a message"
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), P0_0, Direction.EAST, new TextConfiguration("This is a message"))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMessageTextIndirect() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) text msg
                end
                
                msg = '''
                    This is a message
                    '''
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@message"), P0_0, Direction.EAST, new TextConfiguration("This is a message\n"))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithSwitchEnabled() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @switch at (0, 0) enabled
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@switch"), P0_0, Direction.EAST, BooleanConfiguration.TRUE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithNodeConnections() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @power-node at (0, 0) connected to (1, 0), +(2, 0), +(3, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@power-node"), P0_0, Direction.EAST, new PositionArray(P1_0, P2_0, P3_0))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithBridgeConnection() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @bridge-conveyor at (0, 0) connected to +(1, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@bridge-conveyor"), P0_0, Direction.EAST, P1_0)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithNamedBridgeConnection() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (2, 1)
                    @bridge-conveyor at (0, 0) connected to bridge1
                bridge1:
                    @bridge-conveyor at (1, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 2, 1,
                List.of(
                        new Block(List.of(),          BlockType.forName("@bridge-conveyor"), P0_0, Direction.EAST, P1_0),
                        new Block(List.of("bridge1"), BlockType.forName("@bridge-conveyor"), P1_0, Direction.EAST, Position.INVALID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithUnloaderCoal() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @unloader at (0, 0) item @coal
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@unloader"), P0_0, Direction.EAST, Item.COAL)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithLiquidSourceCryofluid() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @liquid-source at (0, 0) liquid @cryofluid
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@liquid-source"), P0_0, Direction.EAST, Liquid.CRYOFLUID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMicroProcessorLinks() {
        Schematics actual = buildSchematics("""
                schematic
                    @micro-processor at (0, 0) processor
                        links
                            (1, 0) as switch1
                            message1 as message1
                            p-message2
                        end
                        mlog = ""
                    end
                switch1:
                    @switch at +(1, 0)
                message1:
                    @message at +(1, 0)
                p-message2:
                    @message at +(1, 0)
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 4, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@micro-processor"), P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(
                                                new Link("switch1", 1, 0),
                                                new Link("message1", 2, 0),
                                                new Link("message2", 3, 0)
                                        ),
                                        ""
                                )
                        ),
                        new Block(List.of("switch1"), BlockType.forName("@switch"), P1_0, Direction.EAST, BooleanConfiguration.FALSE),
                        new Block(List.of("message1"), BlockType.forName("@message"), P2_0, Direction.EAST, TextConfiguration.EMPTY),
                        new Block(List.of("p-message2"), BlockType.forName("@message"), P3_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void refusesWrongLinkNames() {
        buildSchematicsExpectingMessage("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                    links
                        switch1 as message1
                        message1 as switch1
                    end
                    mlog = ""
                end
                switch1:
                    @switch at +(1, 0)
                message1:
                    @message at +(1, 0)
                end
                """,
                "Incompatible link name 'message1' for block type '@switch'.");
    }

    @Test
    void buildsSchematicsWithMicroProcessorMlog() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mlog = "print @this"
                    end
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@micro-processor"), P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(),
                                        "print @this"
                                )
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMicroProcessorMindcode() {
        Schematics actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mindcode = "print(@this)"
                    end
                end
                """);

        Schematics expected = new Schematics("", "", List.of(), 1, 1,
                List.of(
                        new Block(List.of(), BlockType.forName("@micro-processor"), P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(),
                                        "print @this\nend\n"
                                )
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void refusesUnknownBlocks() {
        buildSchematicsExpectingMessage("""
                schematic
                    dimensions = (1, 1)
                    @fluffyBunny at (0, 0)
                end
                """,
                "Unknown block type '@fluffyBunny'.");
    }

    @Test
    void refusesWrongConfiguration() {
        buildSchematicsExpectingMessage("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) enabled
                end
                """,
                "Unexpected configuration type for block @message at \\(\\s*0,\\s*0\\): expected TEXT, found BOOLEAN.");
    }
}
