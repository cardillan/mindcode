package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AstSchematicBuilderTest extends AbstractSchematicsTest {

    protected AstDefinitions definitionWithBlocks(AstBlock... blocks) {
        return new AstDefinitions(List.of(new AstSchematic(List.of(), List.of(), List.of(blocks)))).withEmptyPosition();
    }

    @Test
    public void basicParseDoesNotThrow() {
        assertDoesNotThrow(
                () -> parseSchematics(
                        InputFiles.fromSource("""
                                schematic
                                    target = 7.1
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematic(
                                List.of(
                                        new AstSchemaAttribute("name", AstStringLiteral.fromText("On/off switch")),
                                        new AstSchemaAttribute("description", AstStringLiteral.fromText("Description")),
                                        new AstSchemaAttribute("label", AstStringLiteral.fromText("label1")),
                                        new AstSchemaAttribute("label", AstStringLiteral.fromText("label2")),
                                        new AstSchemaAttribute("dimensions", new AstCoordinates(2, 1))
                                ),
                                List.of(),
                                List.of(
                                        new AstBlock(
                                                List.of("switch1"),
                                                new AstSchemaBlock("@switch"),
                                                new AstCoordinates(0, 0),
                                                new AstDirection("south"),
                                                null
                                        ),
                                        new AstBlock(
                                                List.of(),
                                                new AstSchemaBlock("@micro-processor"),
                                                new AstCoordinates(1, 0),
                                                new AstDirection("south"),
                                                null
                                        )
                                )
                        )
                ));

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesTextBlock() {
        AstDefinitions actual = createDefinitions("""
                value = '''
                    text
                    block
                    '''
                """);

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstStringConstant("value", new AstStringBlock("text\nblock\n", 4)
                        )
                )
        );

        assertAstEquals(expected, actual);
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematic(
                                List.of(
                                        new AstSchemaAttribute("name", AstStringLiteral.fromText("Name"))
                                ),
                                List.of(),
                                List.of()
                        )
                ));

        assertAstEquals(expected, actual);
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematic(
                                List.of(
                                        new AstSchemaAttribute("name", new AstStringRef("str_Name"))
                                ),
                                List.of(),
                                List.of()
                        ),
                        new AstStringConstant("str_Name", AstStringLiteral.fromText("Name"))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesDimensions() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    dimensions = (4, 5)
                end
                """
        );

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematic(
                                List.of(
                                        new AstSchemaAttribute("dimensions", new AstCoordinates(4, 5))
                                ),
                                List.of(),
                                List.of()
                        )
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesTarget() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    target = 7.1
                end
                """
        );

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematic(
                                List.of(
                                        new AstSchemaAttribute("target", new AstStringLiteral("7.1"))
                                ),
                                List.of(),
                                List.of()
                        )
                )
        );

        assertAstEquals(expected, actual);
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
    public void parsesBlockAtAbsoluteCoordinates() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(0, 0),
                        null,
                        null
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(SourcePosition.EMPTY, 1, 1, true),
                        null,
                        null
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(new Position(1, 1), true, AstLabel.of("block1")),
                        null,
                        null
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of("label1", "label2", "label3"),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(0, 0),
                        null,
                        null
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(List.of(), new AstSchemaBlock("@conveyor"), new AstCoordinates(0, 0), new AstDirection("south"), null),
                new AstBlock(List.of(), new AstSchemaBlock("@conveyor"), new AstCoordinates(1, 0), new AstDirection("north"), null),
                new AstBlock(List.of(), new AstSchemaBlock("@conveyor"), new AstCoordinates(2, 0), new AstDirection("east"), null),
                new AstBlock(List.of(), new AstSchemaBlock("@conveyor"), new AstCoordinates(3, 0), new AstDirection("west"), null)
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesInvalidDirection() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing {'north', 'south', 'east', 'west'} at 'middle'"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstVirtual()
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@power-node"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(new AstConnection(1, 1))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesConnectedToRelative() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @power-node at (0, 0) connected to +(1, 1)
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@power-node"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(new AstConnection(1, 1, true))
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesConnectedToMixed() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @power-node at (0, 0) connected to +(1, 1), (2, 2)
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@power-node"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(
                                new AstConnection(1, 1, true),
                                new AstConnection(2, 2)
                        )
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesConnectedToRelativeTo() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .addRegex("Parse error: extraneous input '\\+' expecting .*"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@sorter"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstItemReference("@coal")
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesItemNonRef() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing TYPE at 'coal'"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@liquid-source"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstLiquidReference("@water")
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesLiquidNonRef() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .add("Parse error: missing TYPE at 'water'"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@message"),
                        new AstCoordinates(0, 0),
                        null,
                        AstStringLiteral.fromText("message")
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@message"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstStringBlock("message1\nmessage2\n", 0)
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesTextConfigurationRef() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @message at (0, 0) text something
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@message"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstStringRef("something")
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesConfigurationEnabled() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0) enabled
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstBoolean(true)
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void parsesConfigurationDisabled() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @switch at (0, 0) disabled
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@switch"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstBoolean(false)
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), AstProgram.EMPTY, Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), AstProgram.EMPTY, Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPattern(AstLabel.of("*-p-*"))),
                                AstProgram.EMPTY, Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPattern(AstLabel.of("cell1"))),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesProcessorVirtualLinkReference() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .addRegex("Parse error: extraneous input 'virtual' expecting .*"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection("cell1"), "cell2", false)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection("cell1"), "cell2", true)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(-1, -1, true), "cell1", true)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1), null, false)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1, true), null, false)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1), "switch1", false)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1, true), "switch1", false)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1), "switch1", true)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(
                                List.of(new AstLinkPos(new AstConnection(1, 1, true), "switch1", true)),
                                AstProgram.EMPTY,
                                Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
    }

    @Test
    public void refusesProcessorLinkPositionAbsoluteUnnamedVirtual() {
        parseSchematicsExpectingMessages(
                ExpectedMessages.create()
                        .addRegex("Parse error: extraneous input 'virtual' expecting .*"),
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
                        .addRegex("Parse error: extraneous input 'virtual' expecting .*"),
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(
                                new AstLinkPattern(AstLabel.of("p1-*")),
                                new AstLinkPattern(AstLabel.of("switch1")),
                                new AstLinkPos(new AstConnection("cell1"), "cell2", false),
                                new AstLinkPos(new AstConnection(1, 1), null, false),
                                new AstLinkPos(new AstConnection(2, 2, true), "message1", false),
                                new AstLinkPos(new AstConnection(-1, -1, true), "display1", true)
                        ), AstProgram.EMPTY, Language.NONE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetText(AstStringLiteral.fromText("program"))
                                ), Language.MLOG, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetText(new AstStringRef("mlog_program"))
                                ), Language.MLOG, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetFile(AstStringLiteral.fromText("file"))
                                ), Language.MLOG, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetFile(new AstStringRef("my_file"))
                                ), Language.MLOG, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetText(AstStringLiteral.fromText("program"))
                                ), Language.MINDCODE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetText(new AstStringRef("mindcode_program"))
                                ), Language.MINDCODE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetFile(AstStringLiteral.fromText("file"))
                                ), Language.MINDCODE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetFile(new AstStringRef("my_file"))
                                ), Language.MINDCODE, List.of())
                )
        );

        assertAstEquals(expected, actual);
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
                new AstBlock(
                        List.of(),
                        new AstSchemaBlock("@micro-processor"),
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(),
                                new AstProgram(
                                        new AstProgramSnippetText(AstStringLiteral.fromText("program")),
                                        new AstProgramSnippetFile(AstStringLiteral.fromText("file")),
                                        new AstProgramSnippetText(new AstStringRef("my_program")),
                                        new AstProgramSnippetFile(new AstStringRef("my_file"))
                                ), Language.MINDCODE, List.of())
                )
        );

        assertAstEquals(expected, actual);
    }
}
