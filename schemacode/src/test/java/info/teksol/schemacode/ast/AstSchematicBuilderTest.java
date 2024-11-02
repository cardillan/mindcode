package info.teksol.schemacode.ast;

import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Language;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.InputPosition.EMPTY;
import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class AstSchematicBuilderTest extends AbstractSchematicsTest {

    protected AstDefinitions definitionWithBlocks(AstBlock... blocks) {
        return new AstDefinitions(EMPTY, List.of(new AstSchematic(EMPTY, List.of(), List.of(blocks))));
    }

    @Test
    public void basicParseDoesNotThrow() {
        assertDoesNotThrow(() -> parseSchematics("""
                schematic
                    name = "Reactor Control"
                    dimensions = (16, 11)
                    @bridge-conveyor      at ( 6,  0) facing north
                    @bridge-conveyor      at (10,  0) facing south
                    @plastanium-wall      at (11,  0) facing south
                end
                """)
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

        AstDefinitions expected = new AstDefinitions(EMPTY,
                List.of(
                        new AstSchematic(EMPTY,
                                List.of(
                                        new AstSchemaAttribute(EMPTY, "name", AstStringLiteral.fromText("On/off switch", 2, 13)),
                                        new AstSchemaAttribute(EMPTY, "description", AstStringLiteral.fromText("Description", 3, 20)),
                                        new AstSchemaAttribute(EMPTY, "label", AstStringLiteral.fromText("label1", 4, 12)),
                                        new AstSchemaAttribute(EMPTY, "label", AstStringLiteral.fromText("label2", 5, 12)),
                                        new AstSchemaAttribute(EMPTY, "dimensions", new AstCoordinates(EMPTY, 2, 1))
                                ),
                                List.of(
                                        new AstBlock(EMPTY,
                                                List.of("switch1"),
                                                "@switch",
                                                new AstCoordinates(EMPTY, 0, 0),
                                                new AstDirection(EMPTY, "south"),
                                                null
                                        ),
                                        new AstBlock(EMPTY,
                                                List.of(),
                                                "@micro-processor",
                                                new AstCoordinates(EMPTY, 1, 0),
                                                new AstDirection(EMPTY, "south"),
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

        AstDefinitions expected = new AstDefinitions(EMPTY,
                List.of(
                        new AstStringConstant(EMPTY, "value", new AstStringBlock(EMPTY, "text\nblock\n", 4)
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

        assertTrue(parsed.definitions().get(0) instanceof AstStringConstant stringConstant
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

        assertTrue(definitions.definitions().get(0) instanceof AstSchematic schematics
                && schematics.attributes().get(0).attribute().equals("description")
                && schematics.attributes().get(0).value() instanceof AstStringBlock stringBlock
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

        AstDefinitions expected = new AstDefinitions(EMPTY,
                List.of(
                        new AstSchematic(EMPTY,
                                List.of(
                                        new AstSchemaAttribute(EMPTY, "name", AstStringLiteral.fromText("Name", 2, 13))
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

        AstDefinitions expected = new AstDefinitions(EMPTY,
                List.of(
                        new AstSchematic(EMPTY,
                                List.of(
                                        new AstSchemaAttribute(EMPTY, "name", new AstStringRef(EMPTY, "str_Name"))
                                ),
                                List.of()
                        ),
                        new AstStringConstant(EMPTY, "str_Name", AstStringLiteral.fromText("Name", 5, 13))
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

        AstDefinitions expected = new AstDefinitions(EMPTY,
                List.of(
                        new AstSchematic(EMPTY,
                                List.of(
                                        new AstSchemaAttribute(EMPTY, "dimensions", new AstCoordinates(EMPTY, 4, 5))
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
                    @switch               at ( 0,  0)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, 0, 0),
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
                    @switch               at +(1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, 1, 1, true),
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
                    @switch               at block1 + (1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, new Position(1, 1), true, "block1"),
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
                    @switch               at ( 0,  0)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of("label1", "label2", "label3"),
                        "@switch",
                        new AstCoordinates(EMPTY, 0, 0),
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
                new AstBlock(EMPTY, List.of(), "@conveyor", new AstCoordinates(EMPTY, 0, 0), new AstDirection(EMPTY, "south"), null),
                new AstBlock(EMPTY, List.of(), "@conveyor", new AstCoordinates(EMPTY, 1, 0), new AstDirection(EMPTY, "north"), null),
                new AstBlock(EMPTY, List.of(), "@conveyor", new AstCoordinates(EMPTY, 2, 0), new AstDirection(EMPTY, "east"), null),
                new AstBlock(EMPTY, List.of(), "@conveyor", new AstCoordinates(EMPTY, 3, 0), new AstDirection(EMPTY, "west"), null)
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
                    @switch               at ( 0,  0) virtual
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstVirtual(EMPTY)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@power-node",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstConnections(EMPTY, new AstConnection(EMPTY, 1, 1))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@power-node",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstConnections(EMPTY, new AstConnection(EMPTY, 1, 1, true))
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

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(EMPTY,
                        List.of(),
                        "@power-node",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstConnections(EMPTY,
                                new AstConnection(EMPTY, 1, 1, true),
                                new AstConnection(EMPTY, 2, 2)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@sorter",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstItemReference(EMPTY, "@coal")
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@liquid-source",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstLiquidReference(EMPTY, "@water")
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@message",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        AstStringLiteral.fromText("message", 2, 30)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@message",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstStringBlock(EMPTY, "message1\nmessage2\n", 0)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@message",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstStringRef(EMPTY, "something")
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstBoolean(EMPTY, true)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@switch",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstBoolean(EMPTY, false)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPattern(EMPTY, "*-p-*")), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, "cell1"), null, false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, "cell1"), "cell2", false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, "cell1"), "cell2", true)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY,
                                List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, -1, -1, true), "cell1", true)),
                                null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1), null, false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1, true), null, false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1), "switch1", false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1, true), "switch1", false)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1), "switch1", true)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1, true), "switch1", true)), null, Language.NONE)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(
                                new AstLinkPattern(EMPTY, "p1-*"),
                                new AstLinkPos(EMPTY, new AstConnection(EMPTY, "switch1"), null, false),
                                new AstLinkPos(EMPTY, new AstConnection(EMPTY, "cell1"), "cell2", false),
                                new AstLinkPos(EMPTY, new AstConnection(EMPTY, 1, 1), null, false),
                                new AstLinkPos(EMPTY, new AstConnection(EMPTY, 2, 2, true), "message1", false),
                                new AstLinkPos(EMPTY, new AstConnection(EMPTY, -1, -1, true), "display1", true)
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetText(EMPTY, AstStringLiteral.fromText("program", 3, 17))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetText(EMPTY, new AstStringRef(EMPTY, "mlog_program"))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetFile(EMPTY, AstStringLiteral.fromText("file", 3, 22))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetFile(EMPTY, new AstStringRef(EMPTY, "my_file"))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetText(EMPTY, AstStringLiteral.fromText("program", 3, 21))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetText(EMPTY, new AstStringRef(EMPTY, "mindcode_program"))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetFile(EMPTY, AstStringLiteral.fromText("file", 3, 26))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetFile(EMPTY, new AstStringRef(EMPTY, "my_file"))
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
                new AstBlock(EMPTY,
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(EMPTY, 0, 0),
                        null,
                        new AstProcessor(EMPTY, List.of(),
                                new AstProgram(EMPTY,
                                        new AstProgramSnippetText(EMPTY, AstStringLiteral.fromText("program", 3, 21)),
                                        new AstProgramSnippetFile(EMPTY, AstStringLiteral.fromText("file", 3, 38)),
                                        new AstProgramSnippetText(EMPTY, new AstStringRef(EMPTY, "my_program")),
                                        new AstProgramSnippetFile(EMPTY, new AstStringRef(EMPTY, "my_file"))
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
