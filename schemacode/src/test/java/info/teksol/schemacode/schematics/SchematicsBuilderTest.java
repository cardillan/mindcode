package info.teksol.schemacode.schematics;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.config.BooleanConfiguration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.config.TextConfiguration;
import info.teksol.schemacode.mindustry.*;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;
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
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("No schematic defined."),
                """
                        constant = "foo"
                        """
        );
    }

    @Test
    void refusesMultipleSchematics() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("More than one schematic defined."),
                """
                        schematic
                            name = "First"
                        end
                        
                        schematic
                            name = "Second"
                        end
                        """
        );
    }

    @Test
    void refusesConstantRedefinitions() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .add("Identifier 'constant' already defined."),
                """
                        schematic
                            name = "Schematics"
                        end
                        
                        constant = "foo"
                        constant = "bar"
                        """
        );
    }

    @Test
    void refusesUndefinedConstants() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Undefined identifier 'foo'."),
                """
                        schematic
                            name = foo
                        end
                        """
        );
    }

    @Test
    void refusesReusedBlockLabels() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Multiple definitions of block label 'switch1'."),
                """
                        schematic
                            switch1: @switch at (0, 0)
                            switch1: @switch at (1, 0)
                        end
                        """
        );
    }

    @Test
    void refusesCircularPositionDefinition() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Overlapping blocks: #\\d '@switch' at \\(0, 0\\) and #\\d '@switch' at \\(0, 0\\).").repeat(2)
                        .addRegex("Circular definition of block 'switch2' position.")
                        .addRegex("Circular definition of block 'switch3' position."),
                """
                        schematic
                        switch1:
                            @switch at (0, 0)
                        switch2:
                            @switch at switch3 + (1, 0)
                        switch3:
                            @switch at switch2 - (1, 0)
                        end
                        """
        );
    }

    @Test
    void refusesUnknownBlockReference() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .add("Unknown block name 'switch3'.").atLeast(1)
                        .add("Overlapping blocks: #0 '@switch' at (0, 0) and #1 '@switch' at (0, 0)."),
                """
                        schematic
                        switch1:
                            @switch at (0, 0)
                        switch2:
                            @switch at switch3 + (1, 0)
                        end
                        """
        );
    }

    @Test
    void buildsSchematicsWithName() {
        Schematic actual = buildSchematics("""
                schematic
                    name = "Name"
                end
                """);

        Schematic expected = new Schematic("Name", "", List.of(), 0, 0, List.of());

        assertAstEquals(expected, actual);
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

        assertAstEquals(expected, actual);
    }


    @Test
    void refuseMultipleNames() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .add("Attribute 'name' is already defined."),
                """
                        schematic
                            name = "Name"
                            name = "Another"
                        end
                        """
        );
    }

    @Test
    void buildsSchematicsWithDescription() {
        Schematic actual = buildSchematics("""
                schematic
                    description = "Description"
                end
                """);

        Schematic expected = new Schematic("", "Description", List.of(), 0, 0, List.of());

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithTextTag() {
        Schematic actual = buildSchematics("""
                schematic
                    tag = "tag"
                end
                """);

        Schematic expected = new Schematic("", "", List.of("tag"), 0, 0, List.of());

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithIconTag() {
        Schematic actual = buildSchematics("""
                schematic
                    tag = ITEM-COAL
                end
                """);

        Schematic expected = new Schematic("", "",
                List.of(SchematicsMetadata.metadata.getIcons().translateIcon("ITEM-COAL")), 0, 0, List.of());

        assertAstEquals(expected, actual);
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

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(pos(4, 5), "@message", p(4, 6), Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@switch", P0_0, Direction.EAST, BooleanConfiguration.FALSE)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@power-node", P0_0, Direction.EAST, PositionArray.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@bridge-conveyor", P0_0, Direction.EAST, PositionArray.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@unloader", P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@liquid-source", P0_0, Direction.EAST, EmptyConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@micro-processor", P0_0, Direction.EAST, ProcessorConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 1), List.of("message1"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 1), List.of("message1", "message2"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(pos(3, 5), "@message", P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 1), List.of("message1"), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(pos(5, 5), "@message", P1_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@message", P0_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(pos(4, 5), "@message", P1_0, Direction.WEST, TextConfiguration.EMPTY),
                        block(pos(5, 5), "@message", P2_0, Direction.NORTH, TextConfiguration.EMPTY),
                        block(pos(6, 5), "@message", P3_0, Direction.SOUTH, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@message", P0_0, Direction.EAST, new TextConfiguration("This is a message"))
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@message", P0_0, Direction.EAST, new TextConfiguration("This is a message\n"))
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@switch", P0_0, Direction.EAST, BooleanConfiguration.TRUE)
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(2, 5), "@power-node", P0_0, Direction.EAST, pa(P1_0)),
                        block(pos(3, 5), "@power-node", P1_0, Direction.EAST, pa(P0_0))
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@bridge-conveyor", P0_0, Direction.EAST, pa(P1_0))
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), List.of(), "@bridge-conveyor", P0_0, Direction.EAST, pa(P1_0)),
                        block(pos(4, 1), List.of("bridge1"), "@bridge-conveyor", P1_0, Direction.EAST, pa())
                )
        );

        assertAstEquals(expected, actual);
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
                        block(pos(3, 5), "@unloader", P0_0, Direction.EAST, ItemConfiguration.forName("@coal"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void refusesSchematicsWithInvalidItemConfiguration() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@unloader' at \\(\\s*0,\\s*0\\): unknown or unsupported item '@fluffyBunny'\\."),
                """
                        schematic
                            @unloader at (0, 0) item @fluffyBunny
                        end
                        """
        );
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
                        block(pos(3, 5), "@liquid-source", P0_0, Direction.EAST, LiquidConfiguration.forName("@cryofluid"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void refusesSchematicsWithInvalidLiquidConfiguration() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@liquid-source' at \\(\\s*0,\\s*0\\): unknown or unsupported liquid '@fluffyBunny'\\."),
                """
                        schematic
                            @liquid-source at (0, 0) liquid @fluffyBunny
                        end
                        """
        );
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
                        block(pos(2, 5), "@micro-processor", P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(
                                                new Link("switch1", 1, 0),
                                                new Link("message1", 2, 0),
                                                new Link("message2", 3, 0)
                                        ),
                                        ""
                                )
                        ),
                        block(pos(10, 1), List.of("switch1"), "@switch", P1_0, Direction.EAST, BooleanConfiguration.FALSE),
                        block(pos(12, 1), List.of("message1"), "@message", P2_0, Direction.EAST, TextConfiguration.EMPTY),
                        block(pos(14, 1), List.of("p-message2"), "@message", P3_0, Direction.EAST, TextConfiguration.EMPTY)
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void refusesWrongLinkNames() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .add("Incompatible link name 'message1' for block type '@switch'.")
                        .add("Incompatible link name 'switch1' for block type '@message'."),
                """
                        schematic
                            dimensions = (3, 1)
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
                        """
        );
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
                        block(pos(3, 5), "@micro-processor", P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(),
                                        "print @this"
                                )
                        )
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithMicroProcessorMindcode() {
        Schematic actual = buildSchematics("""
                schematic
                    dimensions = (1, 1)
                    @micro-processor at (0, 0) processor
                        mindcode = "print(@this); printflush(message1);"
                    end
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block(pos(3, 5), "@micro-processor", P0_0, Direction.EAST,
                                new ProcessorConfiguration(
                                        List.of(),
                                        "print @this\nprintflush message1\n"
                                )
                        )
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithAirFactoryFlare() {
        Schematic actual = buildSchematics("""
                schematic
                    @air-factory at (0, 0) unit @flare
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 3, 3,
                List.of(
                        block(pos(2, 5), "@air-factory", P0_0, Direction.EAST, new UnitPlan("@flare"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithAirFactoryMono() {
        Schematic actual = buildSchematics("""
                schematic
                    @air-factory at (0, 0) unit @mono
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 3, 3,
                List.of(
                        block(pos(2, 5), "@air-factory", P0_0, Direction.EAST, new UnitPlan("@mono"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void refusesUnsupportedUnitConfiguration() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@air-factory' at \\(\\s*0,\\s*0\\): unknown or unsupported unit type '@poly'\\."),
                """
                        schematic
                            @air-factory at (0, 0) unit @poly
                        end
                        """
        );
    }

    @Test
    void buildsIlluminatorWithColor() {
        Schematic actual = buildSchematics("""
                schematic
                    @illuminator at (0, 0) color rgba(255, 0, 0, 127)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 1, 1,
                List.of(
                        block(pos(2, 5), "@illuminator", P0_0, Direction.EAST, new Color(255, 0, 0, 127))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void refusesWrongColorValueRed() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@illuminator' at \\(\\s*0,\\s*0\\): value 256 of color component 'red' outside valid range <0, 255>\\."),
                """
                        schematic
                            @illuminator at (0, 0) color rgba(256, 0, 0, 127)
                        end
                        """
        );
    }

    @Test
    void refusesWrongColorValueGreen() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@illuminator' at \\(\\s*0,\\s*0\\): value -1 of color component 'green' outside valid range <0, 255>\\."),
                """
                        schematic
                            @illuminator at (0, 0) color rgba(255, -1, 0, 127)
                        end
                        """
        );
    }

    @Test
    void refusesWrongColorValueBlue() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@illuminator' at \\(\\s*0,\\s*0\\): value 99999 of color component 'blue' outside valid range <0, 255>\\."),
                """
                        schematic
                            @illuminator at (0, 0) color rgba(255, 0, 99999, 127)
                        end
                        """
        );
    }

    @Test
    void refusesWrongColorValueAlpha() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@illuminator' at \\(\\s*0,\\s*0\\): value 256 of color component 'alpha' outside valid range <0, 255>\\."),
                """
                        schematic
                            @illuminator at (0, 0) color rgba(255, 0, 0, 256)
                        end
                        """
        );
    }

    @Test
    void refusesUnknownBlocks() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .add("Unknown block type '@fluffyBunny'.")
                        .add("Actual schematic dimensions ( 0,  0) are smaller than specified dimensions ( 1,  1)."),
                """
                        schematic
                            dimensions = (1, 1)
                            @fluffyBunny at (0, 0)
                        end
                        """
        );
    }

    @Test
    void refusesWrongConfiguration() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Unexpected configuration type for block '@message' at \\(\\s*0,\\s*0\\): expected TEXT, found BOOLEAN."),
                """
                        schematic
                            dimensions = (1, 1)
                            @message at (0, 0) enabled
                        end
                        """
        );
    }

    @Test
    void buildsSchematicsWithUnitConfiguration() {
        Schematic actual = buildSchematics("""
                schematic
                    @payload-source at (0, 0) unit @mega
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 5,
                List.of(
                        block(pos(2, 5), "@payload-source", P0_0, Direction.EAST, UnitConfiguration.forName("@mega"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithBlockConfiguration() {
        Schematic actual = buildSchematics("""
                schematic
                    @payload-source at (0, 0) block @vault
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 5,
                List.of(
                        block(pos(2, 5), "@payload-source", P0_0, Direction.EAST, BlockConfiguration.forName("@vault"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    void buildsSchematicsWithUnitCommandConfiguration() {
        Schematic actual = buildSchematics("""
                schematic
                    @multiplicative-reconstructor at (0, 0) command @repair
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 5, 5,
                List.of(
                        block(pos(2, 5), "@multiplicative-reconstructor", P0_0, Direction.EAST, UnitCommandConfiguration.forName("@repair"))
                )
        );

        assertAstEquals(expected, actual);
    }
}
