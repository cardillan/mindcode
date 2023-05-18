package info.teksol.schemacode.schema;

import info.teksol.mindcode.mimex.Icons;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.config.BooleanConfiguration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.config.ProcessorConfiguration;
import info.teksol.schemacode.config.ProcessorConfiguration.Link;
import info.teksol.schemacode.config.TextConfiguration;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Item;
import info.teksol.schemacode.mindustry.Liquid;
import info.teksol.schemacode.mindustry.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchematicsBuilderTest extends AbstractSchematicsTest {
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
        buildSchematicsExpectingError("""
                        constant = "foo"
                        """,
                "No schematic defined.");
    }

    @Test
    void refusesMultipleSchematics() {
        buildSchematicsExpectingError("""
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
        buildSchematicsExpectingError("""
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
        buildSchematicsExpectingError("""
                        schematic
                            name = foo
                        end
                        """,
                "Undefined identifier 'foo'.");
    }

    @Test
    void refusesReusedBlockLabels() {
        buildSchematicsExpectingError("""
                        schematic
                            switch1: @switch at (0, 0)
                            switch1: @switch at (1, 0)
                        end
                        """,
                "Multiple definitions of block label 'switch1'.");
    }

    @Test
    void refusesCircularPositionDefinition() {
        buildSchematicsExpectingError("""
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
        buildSchematicsExpectingError("""
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
        Schematic actual = buildSchematics("""
                schematic
                    name = "Name"
                end
                """);

        Schematic expected = new Schematic("Name", "", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithIndirectName() {
        Schematic actual = buildSchematics("""
                schematic
                    name = my_name
                end
                                
                my_name = "Name"
                """);

        Schematic expected = new Schematic("Name", "", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }


    @Test
    void refuseMultipleNames() {
        buildSchematicsExpectingError("""
                schematic
                    name = "Name"
                    name = "Another"
                end
                """,
                "Multiple definitions of attribute 'name'.");
    }

    @Test
    void buildsSchematicsWithDescription() {
        Schematic actual = buildSchematics("""
                schematic
                    description = "Description"
                end
                """);

        Schematic expected = new Schematic("", "Description", List.of(), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithTextTag() {
        Schematic actual = buildSchematics("""
                schematic
                    tag = "tag"
                end
                """);

        Schematic expected = new Schematic("", "", List.of("tag"), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithIconTag() {
        Schematic actual = buildSchematics("""
                schematic
                    tag = ITEM-COAL
                end
                """);

        Schematic expected = new Schematic("", "", List.of(Icons.translateIcon("ITEM-COAL")), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMergedTags() {
        Schematic actual = buildSchematics("""
                schematic
                    tag = "tag"
                    tag = "tag"
                end
                """);

        Schematic expected = new Schematic("", "", List.of("tag"), 0, 0, List.of());

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithDimensions() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (5, 7)
                    @message at (0, 0)
                    @message at (4, 6)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 7,
                List.of(
                        block("@message", P0_0,    Direction.EAST, TextConfiguration.EMPTY),
                        block("@message", p(4, 6), Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPositiveOrigin() {
        Schematic actual = buildSchematics("""
                schematic
                    @message at (4, 6)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@message", P0_0,    Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithNegativeOrigin() {
        Schematic actual = buildSchematics("""
                schematic
                    @message at (-7, -5)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@message", P0_0,    Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainMessage() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainSwitch() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @switch at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@switch", P0_0, Direction.EAST, BooleanConfiguration.FALSE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainNode() {
        Schematic actual = buildSchematics("""
                schematic
                    @power-node at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@power-node", P0_0, Direction.EAST, PositionArray.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainBridge() {
        Schematic actual = buildSchematics("""
                schematic
                    @bridge-conveyor at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@bridge-conveyor", P0_0, Direction.EAST, Position.INVALID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainUnloader() {
        Schematic actual = buildSchematics("""
                schematic
                    @unloader at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@unloader", P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainLiquidSource() {
        Schematic actual = buildSchematics("""
                schematic
                    @liquid-source at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@liquid-source", P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithPlainMicroProcessor() {
        Schematic actual = buildSchematics("""
                schematic
                    @micro-processor at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@micro-processor", P0_0, Direction.EAST, new ProcessorConfiguration())
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithBlockLabel() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                message1:
                    @message at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block(List.of("message1"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMultipleBlockLabel() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                message1, message2:
                    @message at (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block(List.of("message1", "message2"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithRelativeBlockPosition() {
        Schematic actual = buildSchematics("""
                schematic
                    @message at (0, 0)
                    @message at + (1, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 2, 1,
                List.of(
                        block("@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block("@message", P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithRelativeToBlockPosition() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (2, 1)
                message1:
                    @message at (0, 0)
                    @message at message1 + (1, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 2, 1,
                List.of(
                        block(List.of("message1"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block("@message", P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithAllDirections() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (4, 1)
                    @message at  (0, 0) facing east
                    @message at +(1, 0) facing west
                    @message at +(1, 0) facing north
                    @message at +(1, 0) facing south
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 4, 1,
                List.of(
                        block("@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block("@message", P1_0, Direction.WEST, TextConfiguration.EMPTY),
                        block("@message", P2_0, Direction.NORTH, TextConfiguration.EMPTY),
                        block("@message", P3_0, Direction.SOUTH, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMessageText() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) text "This is a message"
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@message", P0_0, Direction.EAST, new TextConfiguration("This is a message"))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMessageTextIndirect() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) text msg
                end
                
                msg = '''
                    This is a message
                    '''
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@message", P0_0, Direction.EAST, new TextConfiguration("This is a message\n"))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithSwitchEnabled() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @switch at (0, 0) enabled
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@switch", P0_0, Direction.EAST, BooleanConfiguration.TRUE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithNodeConnections() {
        Schematic actual = buildSchematics("""
                schematic
                    @power-node at (0, 0) connected to (1, 0)
                    @power-node at (1, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 2, 1,
                List.of(
                        block("@power-node", P0_0, Direction.EAST, pa(P1_0)),
                        block("@power-node", P1_0, Direction.EAST, pa(P0_0))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithBridgeConnection() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @bridge-conveyor at (0, 0) connected to +(1, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@bridge-conveyor", P0_0, Direction.EAST, P1_0)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithNamedBridgeConnection() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (2, 1)
                    @bridge-conveyor at (0, 0) connected to bridge1
                bridge1:
                    @bridge-conveyor at (1, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 2, 1,
                List.of(
                        block(List.of(),          "@bridge-conveyor", P0_0, Direction.EAST, P1_0),
                        block(List.of("bridge1"), "@bridge-conveyor", P1_0, Direction.EAST, Position.INVALID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithUnloaderCoal() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @unloader at (0, 0) item @coal
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@unloader", P0_0, Direction.EAST, Item.COAL)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithLiquidSourceCryofluid() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @liquid-source at (0, 0) liquid @cryofluid
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@liquid-source", P0_0, Direction.EAST, Liquid.CRYOFLUID)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMicroProcessorLinks() {
        Schematic actual = buildSchematics("""
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

        Schematic expected = new Schematic("", "", List.of(), 4, 1,
                List.of(
                        block("@micro-processor", P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(
                                                new Link("switch1", 1, 0),
                                                new Link("message1", 2, 0),
                                                new Link("message2", 3, 0)
                                        ),
                                        ""
                                )
                        ),
                        block(List.of("switch1"), "@switch", P1_0, Direction.EAST, BooleanConfiguration.FALSE),
                        block(List.of("message1"), "@message", P2_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(List.of("p-message2"), "@message", P3_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void refusesWrongLinkNames() {
        buildSchematicsExpectingError("""
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
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mlog = "print @this"
                    end
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@micro-processor", P0_0, Direction.EAST,
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
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mindcode = "print(@this)"
                    end
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block("@micro-processor", P0_0, Direction.EAST,
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
        buildSchematicsExpectingError("""
                schematic
                    dimensions = (1, 1)
                    @fluffyBunny at (0, 0)
                end
                """,
                "Unknown block type '@fluffyBunny'.");
    }

    @Test
    void refusesWrongConfiguration() {
        buildSchematicsExpectingError("""
                schematic
                    dimensions = (1, 1)
                    @message at (0, 0) enabled
                end
                """,
                "Unexpected configuration type for block @message at \\(\\s*0,\\s*0\\): expected TEXT, found BOOLEAN.");
    }
}
