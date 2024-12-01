package info.teksol.schemacode.ast;

import info.teksol.mindcode.v3.InputFiles;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Language;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstSchematicBuilderTest extends AbstractSchematicsTest {

    protected AstDefinitions definitionWithBlocks(AstBlock... blocks) {
        return new AstDefinitions(pos(1, 1), List.of(new AstSchematic(pos(1, 1), List.of(), List.of(blocks))));
    }

    @Test
    public void basicParseDoesNotThrow() {
        assertDoesNotThrow(
                () -> parseSchematics(
                        InputFiles.fromSource("""
                                schematic
                                    name = "Reactor Control"
                                    dimensions = (16, 11)
                                    @bridge-conveyor      at ( 6,  0) facing north
                                    @bridge-conveyor      at (10,  0) facing south
                                    @plastanium-wall      at (11,  0) facing south
                                end
                                """)
                )
        );
    }

    @Test
    public void parsesBasicSchematics() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    name = "On/off switch"
                    description = "Description"
                    tag = "label1"
                    tag = "label2"
                    dimensions = (2, 1)
                switch1:
                    @switch               at ( 0,  0) facing south
                    @micro-processor      at ( 1,  0) facing south
                end
                """
        );

        AstDefinitions expected = new AstDefinitions(pos(1, 1),
                List.of(
                        new AstSchematic(pos(1, 1),
                                List.of(
                                        new AstSchemaAttribute(pos(2, 5), "name", AstStringLiteral.fromText(pos(2, 13), "On/off switch")),
                                        new AstSchemaAttribute(pos(3, 5), "description", AstStringLiteral.fromText(pos(3, 20), "Description")),
                                        new AstSchemaAttribute(pos(4, 5), "label", AstStringLiteral.fromText(pos(4, 12), "label1")),
                                        new AstSchemaAttribute(pos(5, 5), "label", AstStringLiteral.fromText(pos(5, 12), "label2")),
                                        new AstSchemaAttribute(pos(6, 5), "dimensions", new AstCoordinates(pos(6, 18), 2, 1))
                                ),
                                List.of(
                                        new AstBlock(pos(7, 1),
                                                List.of("switch1"),
                                                "@switch",
                                                new AstCoordinates(pos(8, 30), 0, 0),
                                                new AstDirection(pos(8, 39), "south"),
                                                null
                                        ),
                                        new AstBlock(pos(9, 5),
                                                List.of(),
                                                "@micro-processor",
                                                new AstCoordinates(pos(9, 30), 1, 0),
                                                new AstDirection(pos(9, 39), "south"),
                                                null
                                        )
                                )
                        )
                ));

        assertEquals(expected, actual);
    }

    @Test
    public void parsesTextBlock() {
        AstDefinitions actual = createDefinitions("""
                value = '''
                    text
                    block
                    '''
                """);

        AstDefinitions expected = new AstDefinitions(pos(1, 1),
                List.of(
                        new AstStringConstant(pos(1, 1), "value", new AstStringBlock(pos(2, 1), "text\nblock\n", 4)
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void stripsTextBlockIndent() {
        AstDefinitions parsed = createDefinitions("""
                value = '''
                        text
                        block
                        '''
                """);

        assertTrue(parsed.definitions().getFirst() instanceof AstStringConstant stringConstant
                && stringConstant.name().equals("value")
                && stringConstant.value() instanceof AstStringBlock block
                && block.text().equals("text\nblock\n")
        );
    }


    @Test
    public void extractsDescription() {
        AstDefinitions definitions = createDefinitions("""
                schematic
                    description = '''
                        Description'''
                end
                """
        );

        assertTrue(definitions.definitions().getFirst() instanceof AstSchematic schematics
                && schematics.attributes().getFirst().attribute().equals("description")
                && schematics.attributes().getFirst().value() instanceof AstStringBlock stringBlock
                && stringBlock.text().equals("Description"));
    }

    @Test
    public void parsesNameLiteral() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    name = "Name"
                end
                """
        );

        AstDefinitions expected = new AstDefinitions(pos(1, 1),
                List.of(
                        new AstSchematic(pos(1, 1),
                                List.of(
                                        new AstSchemaAttribute(pos(2, 5), "name", AstStringLiteral.fromText(pos(2, 13), "Name"))
                                ),
                                List.of()
                        )
                ));

        assertEquals(expected, actual);
    }

    @Test
    public void parsesNameRef() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    name = str_Name
                end
                
                str_Name = "Name"
                """
        );

        AstDefinitions expected = new AstDefinitions(pos(1, 1),
                List.of(
                        new AstSchematic(pos(1, 1),
                                List.of(
                                        new AstSchemaAttribute(pos(2, 5), "name", new AstStringRef(pos(2, 12), "str_Name"))
                                ),
                                List.of()
                        ),
                        new AstStringConstant(pos(5, 1), "str_Name", AstStringLiteral.fromText(pos(5, 13), "Name"))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesDimensions() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    dimensions = (4, 5)
                end
                """
        );

        AstDefinitions expected = new AstDefinitions(pos(1, 1),
                List.of(
                        new AstSchematic(pos(1, 1),
                                List.of(
                                        new AstSchemaAttribute(pos(2, 5), "dimensions", new AstCoordinates(pos(2, 18), 4, 5))
                                ),
                                List.of()
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesRelativeDimensions() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: extraneous input '+' expecting '('"),
                """
                        schematic
                            name = "Reactor Control"
                            dimensions = +(16, 11)
                        end
                        """
        );
    }

    @Test
    public void refusesNonRefBlock() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: 'at': mismatched input 'at' expecting {':', ','}"),
                """
                        schematic
                            conveyor at (0, 0)
                        end
                        """
        );
    }

    @Test
    public void parsesBlockAtAbsoluteCoordinates() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 16), 0, 0),
                        null,
                        null
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesBlockAtRelativeCoordinates() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at +(1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 17), 1, 1, true),
                        null,
                        null
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesBlockAtRelativeToCoordinates() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at block1 + (1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 25), new Position(1, 1), true, "block1"),
                        null,
                        null
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void requiresPosition() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: 'facing': mismatched input 'facing' expecting 'at'"),
                """
                        schematic
                            label1: @switch facing south
                        end
                        """
        );
    }

    @Test
    public void parsesBlockWithLabels() {
        AstDefinitions actual = createDefinitions("""
                schematic
                label1, label2, label3:
                    @switch at (0,  0)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 1),
                        List.of("label1", "label2", "label3"),
                        "@switch",
                        new AstCoordinates(pos(3, 16), 0, 0),
                        null,
                        null
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesAllDirections() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @conveyor at ( 0,  0) facing south
                    @conveyor at ( 1,  0) facing north
                    @conveyor at ( 2,  0) facing east
                    @conveyor at ( 3,  0) facing west
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5), List.of(), "@conveyor", new AstCoordinates(pos(2, 18), 0, 0), new AstDirection(pos(2, 27), "south"), null),
                new AstBlock(pos(3, 5), List.of(), "@conveyor", new AstCoordinates(pos(3, 18), 1, 0), new AstDirection(pos(3, 27), "north"), null),
                new AstBlock(pos(4, 5), List.of(), "@conveyor", new AstCoordinates(pos(4, 18), 2, 0), new AstDirection(pos(4, 27), "east"), null),
                new AstBlock(pos(5, 5), List.of(), "@conveyor", new AstCoordinates(pos(5, 18), 3, 0), new AstDirection(pos(5, 27), "west"), null)
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesInvalidDirection() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing {'north', 'south', 'east', 'west'} at 'middle'")
                        .add("Parse error: 'end': mismatched input 'end' expecting {':', ','}"),
                """
                        schematic
                            @conveyor at ( 0,  0) facing middle
                        end
                        """
        );
    }

    @Test
    public void parsesVirtualConfiguration() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0) virtual
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 16), 0, 0),
                        null,
                        new AstVirtual(pos(2, 23))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesConnectedToAbsolute() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @power-node at (0, 0) connected to (1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@power-node",
                        new AstCoordinates(pos(2, 20), 0, 0),
                        null,
                        new AstConnections(pos(2, 27), new AstConnection(pos(2, 40), 1, 1))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesConnectedToRelative() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @power-node at (0, 0) connected to +(1, 1)
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@power-node",
                        new AstCoordinates(pos(2, 20), 0, 0),
                        null,
                        new AstConnections(pos(2, 27), new AstConnection(pos(2, 40), 1, 1, true))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesConnectedToMixed() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @power-node at (0, 0) connected to +(1, 1), (2, 2)
                end
                """);

        AstConnections xxx = new AstConnections(pos(2, 27),
                new AstConnection(pos(2, 41), 1, 1, true));

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@power-node",
                        new AstCoordinates(pos(2, 20), 0, 0),
                        null,
                        new AstConnections(pos(2, 27),
                                new AstConnection(pos(2, 40), 1, 1, true),
                                new AstConnection(pos(2, 49), 2, 2)
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesConnectedToRelativeTo() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: extraneous input '+' expecting {'description', 'dimensions', 'end', 'name', 'tag', Id, Ref}"),
                """
                        schematic
                            @power-node at (0, 0) connected to block1 + (1, 1)
                        end
                        """
        );
    }

    @Test
    public void parsesItemRef() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @sorter at (0, 0) item @coal
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@sorter",
                        new AstCoordinates(pos(2, 16), 0, 0),
                        null,
                        new AstItemReference(pos(2, 23), "@coal")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesItemNonRef() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing Ref at 'coal'")
                        .add("Parse error: 'end': mismatched input 'end' expecting {':', ','}"),
                """
                        schematic
                            @sorter at (0, 0) item coal
                        end
                        """
        );
    }

    @Test
    public void parsesLiquidRef() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @liquid-source at (0, 0) liquid @water
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@liquid-source",
                        new AstCoordinates(pos(2, 23), 0, 0),
                        null,
                        new AstLiquidReference(pos(2, 30), "@water")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesLiquidNonRef() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing Ref at 'water'")
                        .add("Parse error: 'end': mismatched input 'end' expecting {':', ','}"),
                """
                        schematic
                            @liquid-source at (0, 0) liquid water
                        end
                        """
        );
    }

    @Test
    public void parsesTextConfigurationLiteral() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @message at (0, 0) text "message"
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@message",
                        new AstCoordinates(pos(2, 17), 0, 0),
                        null,
                        AstStringLiteral.fromText(pos(2, 30), "message")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesTextConfigurationBlock() {
        // Note: the schemacode block literal is intentionally not indented
        AstDefinitions actual = createDefinitions("""
                schematic
                    @message at (0, 0) text '''
                message1
                message2
                '''
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@message",
                        new AstCoordinates(pos(2, 17), 0, 0),
                        null,
                        new AstStringBlock(pos(3, 1), "message1\nmessage2\n", 0)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesTextConfigurationRef() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @message at (0, 0) text something
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@message",
                        new AstCoordinates(pos(2, 17), 0, 0),
                        null,
                        new AstStringRef(pos(2, 29), "something")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesConfigurationEnabled() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0) enabled
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 16), 0, 0),
                        null,
                        new AstBoolean(pos(2, 23), true)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesConfigurationDisabled() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0) disabled
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@switch",
                        new AstCoordinates(pos(2, 16), 0, 0),
                        null,
                        new AstBoolean(pos(2, 23), false)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesEmptyProcessor() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorEmptyLinks() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPattern() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links *-p-* end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(new AstLinkPattern(pos(3, 15), "*-p-*")), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkReference() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links cell1 end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), "cell1"), null, false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesProcessorVirtualLinkReference() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: extraneous input 'virtual' expecting {'end', '-', '+', '(', Id, Pattern}"),
                """
                        schematic
                            @micro-processor at (0, 0) processor
                                links * virtual end
                            end
                        end
                        """
        );
    }

    @Test
    public void parsesProcessorLinkNamedReference() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links cell1 as cell2 end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), "cell1"), "cell2", false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkNamedReferenceVirtual() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links cell1 as cell2 virtual end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), "cell1"), "cell2", true)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkReferenceVirtual() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links -(1, 1) as cell1 virtual end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), -1, -1, true), "cell1", true)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionAbsolute() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links (1, 1) end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1), null, false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionRelative() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links +(1, 1) end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1, true), null, false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionAbsoluteNamed() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links (1, 1) as switch1 end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1), "switch1", false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionRelativeNamed() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links +(1, 1) as switch1 end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1, true), "switch1", false)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionAbsoluteNamedVirtual() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links (1, 1) as switch1 virtual end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1), "switch1", true)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkPositionRelativeNamedVirtual() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links +(1, 1) as switch1 virtual end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32),
                                List.of(new AstLinkPos(pos(3, 15), new AstConnection(pos(3, 15), 1, 1, true), "switch1", true)),
                                null,
                                Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesProcessorLinkPositionAbsoluteUnnamedVirtual() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: extraneous input 'virtual' expecting {'end', '-', '+', '(', Id, Pattern}"),
                """
                        schematic
                            @micro-processor at (0, 0) processor
                                links (1, 1) virtual end
                            end
                        end
                        """
        );
    }

    @Test
    public void refusesProcessorLinkPositionRelativeUnnamedVirtual() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: extraneous input 'virtual' expecting {'end', '-', '+', '(', Id, Pattern}"),
                """
                        schematic
                            @micro-processor at (0, 0) processor
                                links +(1, 1) virtual end
                            end
                        end
                        """
        );
    }

    @Test
    public void parsesProcessorLinks() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links
                            p1-*
                            switch1
                            cell1 as cell2
                            (1, 1)
                            +(2, 2) as message1
                            -(1, 1) as display1 virtual
                         end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(
                                new AstLinkPattern(pos(4, 13), "p1-*"),
                                new AstLinkPos(pos(5, 13), new AstConnection(pos(5, 13), "switch1"), null, false),
                                new AstLinkPos(pos(6, 13), new AstConnection(pos(6, 13), "cell1"), "cell2", false),
                                new AstLinkPos(pos(7, 13), new AstConnection(pos(7, 13), 1, 1), null, false),
                                new AstLinkPos(pos(8, 13), new AstConnection(pos(8, 13), 2, 2, true), "message1", false),
                                new AstLinkPos(pos(9, 13), new AstConnection(pos(9, 13), -1, -1, true), "display1", true)
                        ), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMlogInline() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mlog = "program"
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 16),
                                        new AstProgramSnippetText(pos(3, 16), AstStringLiteral.fromText(pos(3, 17), "program"))
                                ), Language.MLOG)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMlogIndirect() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mlog = mlog_program
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 16),
                                        new AstProgramSnippetText(pos(3, 16), new AstStringRef(pos(3, 16), "mlog_program"))
                                ), Language.MLOG)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMlogFileInline() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mlog = file "file"
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 16),
                                        new AstProgramSnippetFile(pos(3, 16), AstStringLiteral.fromText(pos(3, 22), "file"))
                                ), Language.MLOG)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMlogFileIndirect() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mlog = file my_file
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 16),
                                        new AstProgramSnippetFile(pos(3, 16), new AstStringRef(pos(3, 21), "my_file"))
                                ), Language.MLOG)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMindcodeInline() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mindcode = "program"
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 20),
                                        new AstProgramSnippetText(pos(3, 20), AstStringLiteral.fromText(pos(3, 21), "program"))
                                ), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMindcodeIndirect() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mindcode = mindcode_program
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 20),
                                        new AstProgramSnippetText(pos(3, 20), new AstStringRef(pos(3, 20), "mindcode_program"))
                                ), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMindcodeFileInline() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mindcode = file "file"
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 20),
                                        new AstProgramSnippetFile(pos(3, 20), AstStringLiteral.fromText(pos(3, 26), "file"))
                                ), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorCodeMindcodeFileIndirect() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mindcode = file my_file
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 20),
                                        new AstProgramSnippetFile(pos(3, 20), new AstStringRef(pos(3, 25), "my_file"))
                                ), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }


    @Test
    public void parsesProcessorCodeMindcodeMultipleSnippets() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        mindcode = "program" + file "file" + my_program + file my_file
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(pos(2, 5),
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(pos(2, 25), 0, 0),
                        null,
                        new AstProcessor(pos(2, 32), List.of(),
                                new AstProgram(pos(3, 20),
                                        new AstProgramSnippetText(pos(3, 20), AstStringLiteral.fromText(pos(3, 21), "program")),
                                        new AstProgramSnippetFile(pos(3, 32), AstStringLiteral.fromText(pos(3, 38), "file")),
                                        new AstProgramSnippetText(pos(3, 46), new AstStringRef(pos(3, 46), "my_program")),
                                        new AstProgramSnippetFile(pos(3, 59), new AstStringRef(pos(3, 64), "my_file"))
                                ), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesUnknownConfiguration() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: 'end': mismatched input 'end' expecting {':', ','}"),
                """
                        schematic
                            @switch at (0, 0) fluffyBunny
                        end
                        """
        );
    }
}
