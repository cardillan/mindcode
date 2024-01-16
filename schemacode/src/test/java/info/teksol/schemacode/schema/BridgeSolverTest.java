package info.teksol.schemacode.schema;

import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.AbstractSchematicsTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;

class BridgeSolverTest extends AbstractSchematicsTest {

    @TestFactory
    public List<DynamicTest> refusesConnectionsToSelfFactory() {
        final List<DynamicTest> result = new ArrayList<>();
        List<String> blockTypes = List.of(
                "beam-link",
                "bridge-conduit",
                "bridge-conveyor",
                "large-payload-mass-driver",
                "mass-driver",
                "payload-mass-driver",
                "phase-conduit",
                "phase-conveyor");
        
        for (final String blockType : blockTypes) {
            result.add(DynamicTest.dynamicTest(blockType, null, () -> refusesConnectionToSelf(blockType)));
        }

        return result;
    }

    private void refusesConnectionToSelf(String blockType) {
        buildSchematicsExpectingError("""
                schematic
                    @%s at (0, 0) connected to (0, 0)
                end
                """.formatted(blockType),
                "Block '@%s' at \\(\\s*0,\\s*0\\) has a connection to self\\.".formatted(blockType));
    }

    @TestFactory
    public List<DynamicTest> refusesNonOrthogonalConnections() {
        final List<DynamicTest> result = new ArrayList<>();
        List<String> blockTypes = List.of(
                "bridge-conduit",
                "bridge-conveyor",
                "phase-conduit",
                "phase-conveyor");

        for (final String blockType : blockTypes) {
            result.add(DynamicTest.dynamicTest(blockType, null, () -> refusesNonOrthogonalConnection(blockType)));
        }

        return result;
    }

    private void refusesNonOrthogonalConnection(String blockType) {
        buildSchematicsExpectingError("""
                schematic
                    @%1$s at (0, 0) connected to (5, 5)
                    @%1$s at (5, 5)
                end
                """.formatted(blockType),
                "Block '@%s' at \\(\\s*0,\\s*0\\) has a connection leading to \\(\\s*5,\\s*5\\), which is neither horizontal nor vertical\\.".formatted(blockType));
    }

    @TestFactory
    public List<DynamicTest> refusesOutOfRangeConnections() {
        final List<DynamicTest> result = new ArrayList<>();
        List<BlockType> blockTypes = List.of(
                BlockType.forName("@bridge-conduit"),
                BlockType.forName("@bridge-conveyor"),
                BlockType.forName("@phase-conduit"),
                BlockType.forName("@phase-conveyor"));

        for (final BlockType blockType : blockTypes) {
            result.add(DynamicTest.dynamicTest(blockType.name(), null, () -> refusesOutOfRangeConnection(blockType)));
        }

        return result;
    }

    private void refusesOutOfRangeConnection(BlockType blockType) {
        buildSchematicsExpectingError("""
                schematic
                    %s at (0, 0)
                    %s at (0, %d) connected to (0, 0)
                end
                """.formatted(blockType.name(), blockType.name(), (int) (blockType.range() + 1.1)),
                "Block '%s' at \\(\\s*0,\\s*\\d+\\) has an out-of-range connection to \\(\\s*0,\\s*0\\)\\.".formatted(blockType.name()));
    }

    @Test
    public void refusesMassDriverOutOfRangeConnection() {
        buildSchematicsExpectingError("""
                schematic
                    @mass-driver at (0, 0)
                    @mass-driver at (39, 39) connected to (0, 0)
                end
                """,
                "Block '@mass-driver' at \\(\\s*\\d+,\\s*\\d+\\) has an out-of-range connection to \\(\\s*0,\\s*0\\)\\.");
    }

    @Test
    public void refusesBackConnections() {
        buildSchematicsExpectingError("""
                schematic
                    @bridge-conveyor at (0, 0) connected to (2, 0)
                    @bridge-conveyor at (2, 0) connected to (0, 0)
                end
                """,
                "Two '@bridge-conveyor' blocks at \\(\\s*0,\\s*0\\) and \\(\\s*2,\\s*0\\) connect to each other\\.");
    }

    @Test
    public void refusesTooManyConnections() {
        buildSchematicsExpectingError("""
                schematic
                    @bridge-conveyor at (0, 0) connected to (1, 0), (2, 0)
                    @bridge-conveyor at (1, 0)
                    @bridge-conveyor at (2, 0)
                end
                """,
                "Block '@bridge-conveyor' at \\(\\s*0,\\s*0\\) has more than one connection\\.");
    }

    @Test
    public void refusesConnectionsToDifferentType() {
        buildSchematicsExpectingError("""
                schematic
                    @bridge-conveyor at (0, 0) connected to (1, 0)
                    @phase-conveyor  at (1, 0)
                end
                """,
                "Block '@bridge-conveyor' at \\(\\s*0,\\s*0\\) has a connection leading to a different block type '@phase-conveyor' at \\(\\s*1,\\s*0\\)\\.");
    }
}
