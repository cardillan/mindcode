package info.teksol.schemacode.ast;

import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstSchematicsBuilderTest extends AbstractSchematicsTest {

    protected AstDefinitions definitionWithBlocks(AstBlock... blocks) {
        return new AstDefinitions(List.of(new AstSchematics(List.of(), List.of(blocks))));
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematics(
                                List.of(
                                        new AstSchemaAttribute("name", AstStringLiteral.fromText("On/off switch")),
                                        new AstSchemaAttribute("description", AstStringLiteral.fromText("Description")),
                                        new AstSchemaAttribute("label", AstStringLiteral.fromText("label1")),
                                        new AstSchemaAttribute("label", AstStringLiteral.fromText("label2")),
                                        new AstSchemaAttribute("dimensions", new AstCoordinates(2, 1))
                                ),
                                List.of(
                                        new AstBlock(
                                                List.of("switch1"),
                                                "@switch",
                                                new AstCoordinates(0, 0),
                                                new AstDirection("south"),
                                                null
                                        ),
                                        new AstBlock(
                                                List.of(),
                                                "@micro-processor",
                                                new AstCoordinates(1, 0),
                                                new AstDirection("south"),
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstStringConstant("value", AstStringBlock.fromText("text\nblock\n"))
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
                && block.getValue().equals("text\nblock\n")
        );
    }


    @Test
    public void exctractsDescription() {
        AstDefinitions definitions = createDefinitions("""
                schematic
                    description = '''
                        Description'''
                end
                """
        );

        assertTrue(definitions.definitions().get(0) instanceof AstSchematics schematics
                && schematics.attributes().get(0).attribute().equals("description")
                && schematics.attributes().get(0).value() instanceof AstStringBlock stringBlock
                && stringBlock.getValue().equals("Description"));
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
                        new AstSchematics(
                                List.of(
                                        new AstSchemaAttribute("name", AstStringLiteral.fromText("Name"))
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematics(
                                List.of(
                                        new AstSchemaAttribute("name", new AstStringRef("str_Name"))
                                ),
                                List.of()
                        ),
                        new AstStringConstant("str_Name", AstStringLiteral.fromText("Name"))
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

        AstDefinitions expected = new AstDefinitions(
                List.of(
                        new AstSchematics(
                                List.of(
                                        new AstSchemaAttribute("dimensions", new AstCoordinates(4, 5))
                                ),
                                List.of()
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesRelativeDimensions() {
        parseSchematicsExpectingMessage("""
                schematic
                    name = "Reactor Control"
                    dimensions = +(16, 11)
                end
                """,
                "Syntax error: .* extraneous input '.+' expecting '\\('");
    }

    @Test
    public void refusesNonRefBlock() {
        parseSchematicsExpectingMessage("""
                schematic
                    conveyor at (0, 0)
                end
                """,
                "Syntax error: .* mismatched input '.+' expecting \\{':', ','}");
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
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(0, 0),
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
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(1, 1, true),
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
                    @switch               at block + (1, 1)
                end
                """
        );

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(new Position(1, 1), true, "block"),
                        null,
                        null
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void requiresPosition() {
        parseSchematicsExpectingMessage("""
                schematic
                    label1: @switch facing south
                end
                """,
                "Syntax error: .* mismatched input '.+' expecting 'at'");
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
                new AstBlock(
                        List.of("label1", "label2", "label3"),
                        "@switch",
                        new AstCoordinates(0, 0),
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
                new AstBlock(List.of(), "@conveyor", new AstCoordinates(0, 0), new AstDirection("south"),null),
                new AstBlock(List.of(), "@conveyor", new AstCoordinates(1, 0), new AstDirection("north"),null),
                new AstBlock(List.of(), "@conveyor", new AstCoordinates(2, 0), new AstDirection("east"),null),
                new AstBlock(List.of(), "@conveyor", new AstCoordinates(3, 0), new AstDirection("west"),null)
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesInvalidDirection() {
        parseSchematicsExpectingMessage("""
                schematic
                    @conveyor at ( 0,  0) facing middle
                end
                """,
                ("Syntax error: .* missing \\{'north', 'south', 'east', 'west'} at 'middle'"));
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
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(0, 0),
                        null,
                        AstVirtual.VIRTUAL
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
                new AstBlock(
                        List.of(),
                        "@power-node",
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(new AstConnection(new AstCoordinates(1, 1)))
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
                new AstBlock(
                        List.of(),
                        "@power-node",
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(new AstConnection(new AstCoordinates(1, 1, true)))
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
                new AstBlock(
                        List.of(),
                        "@power-node",
                        new AstCoordinates(0, 0),
                        null,
                        new AstConnections(
                                new AstConnection(new AstCoordinates(1, 1, true)),
                                new AstConnection(new AstCoordinates(2, 2))
                        )
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesConnectedToRelativeTo() {
        parseSchematicsExpectingMessage("""
                schematic
                    @power-node at (0, 0) connected to block + (1, 1)
                end
                """,
                "Syntax error: .* extraneous input '.+' expecting \\{'description', 'dimensions', 'end', 'name', 'tag', Id, Ref}");
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
                        "@sorter",
                        new AstCoordinates(0, 0),
                        null,
                        new AstItemReference("@coal")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesItemNonRef() {
        parseSchematicsExpectingMessage("""
                schematic
                    @sorter at (0, 0) item coal
                end
                """,
                "Syntax error: .* missing Ref at 'coal'");
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
                        "@liquid-source",
                        new AstCoordinates(0, 0),
                        null,
                        new AstLiquidReference("@water")
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesLiquidNonRef() {
        parseSchematicsExpectingMessage("""
                schematic
                    @liquid-source at (0, 0) liquid water
                end
                """,
                "Syntax error: .* missing Ref at 'water'");
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
                        "@message",
                        new AstCoordinates(0, 0),
                        null,
                        AstStringLiteral.fromText("message")
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
                new AstBlock(
                        List.of(),
                        "@message",
                        new AstCoordinates(0, 0),
                        null,
                        AstStringBlock.fromText("message1\nmessage2\n")
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
                new AstBlock(
                        List.of(),
                        "@message",
                        new AstCoordinates(0, 0),
                        null,
                        new AstStringRef("something")
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
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(0, 0),
                        null,
                        new AstBoolean(true)
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
                new AstBlock(
                        List.of(),
                        "@switch",
                        new AstCoordinates(0, 0),
                        null,
                        new AstBoolean(false)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPattern("*-p-*")), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkRef("cell1", null, false)), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesProcessorVirtualLinkReference() {
        parseSchematicsExpectingMessage("""
                schematic
                    @micro-processor at (0, 0) processor
                        links * virtual end
                    end
                end
                """,
                "Syntax error: .* extraneous input 'virtual' expecting \\{'end', '-', '\\+', '\\(', Id, Pattern}");
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
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkRef("cell1", "cell2", false)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkRef("cell1", "cell2", true)), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void parsesProcessorLinkReferenceVirtual() {
        AstDefinitions actual = createDefinitions("""
                schematic
                    @micro-processor at (0, 0) processor
                        links cell1 virtual end
                    end
                end
                """);

        AstDefinitions expected = definitionWithBlocks(
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkRef("cell1", null, true)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1)), null, false)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1, true)), null, false)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1)), "switch1", false)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1, true)), "switch1", false)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1)), "switch1", true)), null, Language.NONE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(new AstLinkPos(new AstConnection(new AstCoordinates(1, 1, true)), "switch1", true)), null, Language.NONE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesProcessorLinkPositionAbsoluteUnnamedVirtual() {
        parseSchematicsExpectingMessage("""
                schematic
                    @micro-processor at (0, 0) processor
                        links (1, 1) virtual end
                    end
                end
                """,
                "Syntax error: .* extraneous input 'virtual' expecting \\{'end', '-', '\\+', '\\(', Id, Pattern}");
    }

    @Test
    public void refusesProcessorLinkPositionRelativeUnnamedVirtual() {
        parseSchematicsExpectingMessage("""
                schematic
                    @micro-processor at (0, 0) processor
                        links +(1, 1) virtual end
                    end
                end
                """,
                "Syntax error: .* extraneous input 'virtual' expecting \\{'end', '-', '\\+', '\\(', Id, Pattern}");
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
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(
                                new AstLinkPattern("p1-*"),
                                new AstLinkRef("switch1", null, false),
                                new AstLinkRef("cell1", "cell2", false),
                                new AstLinkPos(new AstConnection(new AstCoordinates(1, 1)), null, false),
                                new AstLinkPos(new AstConnection(new AstCoordinates(2, 2, true)), "message1", false),
                                new AstLinkPos(new AstConnection(new AstCoordinates(-1, -1, true)), "display1", true)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramText(AstStringLiteral.fromText("program")), Language.MLOG)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramText(new AstStringRef("mlog_program")), Language.MLOG)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramFile(AstStringLiteral.fromText("file")), Language.MLOG)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramFile(new AstStringRef("my_file")), Language.MLOG)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramText(AstStringLiteral.fromText("program")), Language.MINDCODE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramText(new AstStringRef("mindcode_program")), Language.MINDCODE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramFile(AstStringLiteral.fromText("file")), Language.MINDCODE)
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
                new AstBlock(
                        List.of(),
                        "@micro-processor",
                        new AstCoordinates(0, 0),
                        null,
                        new AstProcessor(List.of(), new AstProgramFile(new AstStringRef("my_file")), Language.MINDCODE)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void refusesUnknownConfiguration() {
        parseSchematicsExpectingMessage("""
                schematic
                    @switch at (0, 0) fluffyBunny
                end
                """,
                "Syntax error: .* mismatched input 'end' expecting \\{':', ','}");
    }
}
