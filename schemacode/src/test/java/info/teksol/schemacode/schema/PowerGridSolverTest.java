package info.teksol.schemacode.schema;

import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerGridSolverTest extends AbstractSchematicsTest {

    @Test
    void warnsAboutNonexistentLinkedBlock() {
        buildSchematicsExpectingWarning("""
                schematic
                    @power-node at (0, 0) connected to (1, 1)
                end
                """,
                "Block '@power-node' at \\(\\s*0,\\s*0\\) has a connection to a nonexistent block at \\(\\s*1,\\s*1\\)\\.");
    }

    @Test
    void refusesLinksToSelf() {
        buildSchematicsExpectingError("""
                schematic
                    @power-node at (0, 0) connected to (0, 0)
                end
                """,
                "Block '@power-node' at \\(\\s*0,\\s*0\\) has a connection to self\\.");
    }

    @Test
    void refusesLinksToNoPoweredBlocks() {
        buildSchematicsExpectingError("""
                schematic
                    @power-node at (0, 0) connected to (1, 1)
                    @conveyor at (1, 1)
                end
                """,
                "Block '@power-node' at \\(\\s*0,\\s*0\\) has an invalid connection to a non-powered block '@conveyor' at \\(\\s*1,\\s*1\\)\\.");
    }

    @Test
    void buildsInRangeLinks() {
        Schematic actual = buildSchematics("""
                schematic
                    @power-source        at ( 0,  0)
                    @power-node          at ( 0,  6) connected to (0, 0)
                    @power-node          at ( 1,  6) connected to (0, 0)
                    @power-node          at ( 2,  6) connected to (0, 0)
                    @power-node          at ( 3,  5) connected to (0, 0)
                    @power-node          at ( 4,  5) connected to (0, 0)
                    @power-node          at ( 5,  3) connected to (0, 0)
                    @power-node          at ( 5,  4) connected to (0, 0)
                    @power-node          at ( 6,  0) connected to (0, 0)
                    @power-node          at ( 6,  1) connected to (0, 0)
                    @power-node          at ( 6,  2) connected to (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 7, 7,
                List.of(
                        block("@power-source", P0_0, Direction.EAST, pa(
                                p(0, 6), p(1, 6), p(2, 6), p(3, 5), p(4, 5), p(5, 3), p(5, 4), p(6, 0), p(6, 1), p(6, 2))
                        ),
                        block("@power-node", p(0, 6), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(1, 6), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(2, 6), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(3, 5), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(4, 5), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(5, 3), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(5, 4), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(6, 0), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(6, 1), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(6, 2), Direction.EAST, pa(P0_0))
                )
        );

        assertEquals(expected, actual);
    }
    
    @Test
    void buildsInRangeLinksLarge() {
        Schematic actual = buildSchematics("""
                schematic
                    @power-node-large    at ( 0,  0)
                    @power-node          at ( 1, 15)  connected to (0, 0)
                    @power-node          at ( 6, 15)  connected to (0, 0)
                    @power-node          at ( 7, 14)  connected to (0, 0)
                    @power-node          at ( 8, 14)  connected to (0, 0)
                    @power-node          at ( 9, 13)  connected to (0, 0)
                    @power-node          at (10, 12)  connected to (0, 0)
                    @power-node          at (11, 12)  connected to (0, 0)
                    @power-node          at (12, 11)  connected to (0, 0)
                    @power-node          at (12, 10)  connected to (0, 0)
                    @power-node          at (13,  9)  connected to (0, 0)
                    @power-node          at (14,  8)  connected to (0, 0)
                    @power-node          at (14,  7)  connected to (0, 0)
                    @power-node          at (15,  6)  connected to (0, 0)
                    @power-node          at (15,  1)  connected to (0, 0)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 16, 16,
                List.of(
                        block("@power-node-large", P0_0, Direction.EAST, pa(
                                p( 1, 15), p( 6, 15), p( 7, 14), p( 8, 14), p( 9, 13), p(10, 12), p(11, 12),
                                p(12, 11), p(12, 10), p(13,  9), p(14,  8), p(14,  7), p(15,  6), p(15,  1))
                        ),
                        block("@power-node", p( 1, 15), Direction.EAST, pa(P0_0)),
                        block("@power-node", p( 6, 15), Direction.EAST, pa(P0_0)),
                        block("@power-node", p( 7, 14), Direction.EAST, pa(P0_0)),
                        block("@power-node", p( 8, 14), Direction.EAST, pa(P0_0)),
                        block("@power-node", p( 9, 13), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(10, 12), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(11, 12), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(12, 11), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(12, 10), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(13,  9), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(14,  8), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(14,  7), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(15,  6), Direction.EAST, pa(P0_0)),
                        block("@power-node", p(15,  1), Direction.EAST, pa(P0_0))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsInRangeSurgeTower1() {
        Schematic actual = buildSchematics("""
                schematic
                    @surge-tower at ( 0,  0) connected to (29, 29)
                    @surge-tower at (29, 29)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 31, 31,
                List.of(
                        block("@surge-tower", P0_0, Direction.EAST, pa(p(29, 29))),
                        block("@surge-tower", p(29, 29), Direction.EAST, pa(P0_0))
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    void buildsInRangeSurgeTower2() {
        Schematic actual = buildSchematics("""
                schematic
                    @surge-tower at ( 0,  0) connected to (27, 31)
                    @surge-tower at (27, 31)
                end
                """);

        Schematic expected = new Schematic("", "", List.of(), 29, 33,
                List.of(
                        block("@surge-tower", P0_0, Direction.EAST, pa(p(27, 31))),
                        block("@surge-tower", p(27, 31), Direction.EAST, pa(P0_0))
                )
        );

        assertEquals(expected, actual);
    }

    @TestFactory
    List<DynamicTest> refusesOutOfRangeLinks() {
        final List<DynamicTest> result = new ArrayList<>();
        List<Position> positions = List.of(p(0, 7), p(1, 7), p(2, 7), p(3, 6), p(3, 7), p(4, 6),
                p(5, 5), p(6, 3), p(7, 0), p(7, 1), p(7, 2), p(7, 3));

        for (final Position position : positions) {
            result.add(DynamicTest.dynamicTest(position.toString(), null, () -> refusesOutOfRangeLink(position)));
        }

        return result;
    }

    private void refusesOutOfRangeLink(Position position) {
        buildSchematicsExpectingError("""
                schematic
                    @power-node at (0, 0)
                    @power-node at %s connected to (0, 0)
                end
                """.formatted(position.toStringAbsolute()),
                "Block '@power-node' at \\(\\s*\\d+,\\s*\\d+\\) has an out-of-range connection to block '@power-node' at \\(\\s*0,\\s*0\\)\\.");
    }

    @TestFactory
    List<DynamicTest> refusesOutOfRangeLinksLarge() {
        // Tests *some* of closest out-of-range links
        final List<DynamicTest> result = new ArrayList<>();
        List<Position> positions = List.of(
                p(1, 16), p(7, 15), p(9, 14), p(10, 13), p(12, 12), p(13, 11),
                p(13, 10), p(14, 9), p(15, 8), p(16, 7), p(16, 6), p(16, 1));

        for (final Position position : positions) {
            result.add(DynamicTest.dynamicTest(position.toString(), null, () -> refusesOutOfRangeLinkLarge(position)));
        }

        return result;
    }

    private void refusesOutOfRangeLinkLarge(Position position) {
        buildSchematicsExpectingError("""
                schematic
                    @power-node-large at (0, 0)
                    @power-node at %s connected to (0, 0)
                end
                """.formatted(position.toStringAbsolute()),
                "Block '@power-node' at \\(\\s*\\d+,\\s*\\d+\\) has an out-of-range connection to block '@power-node-large' at \\(\\s*0,\\s*0\\)\\.");
    }

    @Test
    void warnsAboutMultiplyLinkedBlock() {
        buildSchematicsExpectingWarning("""
                schematic
                    @power-node at (0, 0) connected to (1, 1), (1, 1)
                    @power-node at (1, 1)
                end
                """,
                "Block '@power-node' at \\(\\s*0,\\s*0\\) has multiple connections to block '@power-node' at \\(\\s*1,\\s*1\\)\\.");
    }

    @Test
    void refusesTooManyLinks() {
        buildSchematicsExpectingError("""
                schematic
                    @power-node          at ( 0,  0)
                    @power-node          at ( 0,  6) connected to (0, 0)
                    @power-node          at ( 1,  6) connected to (0, 0)
                    @power-node          at ( 2,  6) connected to (0, 0)
                    @power-node          at ( 3,  5) connected to (0, 0)
                    @power-node          at ( 4,  5) connected to (0, 0)
                    @power-node          at ( 5,  3) connected to (0, 0)
                    @power-node          at ( 5,  4) connected to (0, 0)
                    @power-node          at ( 6,  0) connected to (0, 0)
                    @power-node          at ( 6,  1) connected to (0, 0)
                    @power-node          at ( 6,  2) connected to (0, 0)
                    @power-node          at ( 1,  1) connected to (0, 0)
                end
                """,
                "Block '@power-node' at \\(\\s*0,\\s*0\\) has more than 10 connection\\(s\\)\\.");
    }
}
