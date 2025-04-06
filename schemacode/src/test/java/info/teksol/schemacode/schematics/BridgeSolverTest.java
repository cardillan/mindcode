package info.teksol.schemacode.schematics;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.AbstractSchematicsTest;
import info.teksol.schemacode.SchematicsMetadata;
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
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@%s' at \\(\\s*0,\\s*0\\) has a connection to self\\.".formatted(blockType)),
                """
                        schematic
                            @%s at (0, 0) connected to (0, 0)
                        end
                        """.formatted(blockType)
        );
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
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@%s' at \\(\\s*0,\\s*0\\) has a connection leading to \\(\\s*5,\\s*5\\), which is neither horizontal nor vertical\\.".formatted(blockType)),
                """
                        schematic
                            @%1$s at (0, 0) connected to (5, 5)
                            @%1$s at (5, 5)
                        end
                        """.formatted(blockType)
        );
    }

    @TestFactory
    public List<DynamicTest> refusesOutOfRangeConnections() {
        final List<DynamicTest> result = new ArrayList<>();
        List<BlockType> blockTypes = List.of(
                SchematicsMetadata.metadata.getExistingBlock("@bridge-conduit"),
                SchematicsMetadata.metadata.getExistingBlock("@bridge-conveyor"),
                SchematicsMetadata.metadata.getExistingBlock("@phase-conduit"),
                SchematicsMetadata.metadata.getExistingBlock("@phase-conveyor"));

        for (final BlockType blockType : blockTypes) {
            result.add(DynamicTest.dynamicTest(blockType.name(), null, () -> refusesOutOfRangeConnection(blockType)));
        }

        return result;
    }

    private void refusesOutOfRangeConnection(BlockType blockType) {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '%s' at \\(\\s*0,\\s*\\d+\\) has an out-of-range connection to \\(\\s*0,\\s*0\\)\\.".formatted(blockType.name())),
                """
                        schematic
                            %s at (0, 0)
                            %s at (0, %d) connected to (0, 0)
                        end
                        """.formatted(blockType.name(), blockType.name(), (int) (blockType.range() + 1.1))
        );
    }

    @Test
    public void refusesMassDriverOutOfRangeConnection() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@mass-driver' at \\(\\s*\\d+,\\s*\\d+\\) has an out-of-range connection to \\(\\s*0,\\s*0\\)\\."),
                """
                        schematic
                            @mass-driver at (0, 0)
                            @mass-driver at (39, 39) connected to (0, 0)
                        end
                        """
        );
    }

    @Test
    public void refusesBackConnections() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Two '@bridge-conveyor' blocks at \\(\\s*0,\\s*0\\) and \\(\\s*2,\\s*0\\) connect to each other\\."),
                """
                        schematic
                            @bridge-conveyor at (0, 0) connected to (2, 0)
                            @bridge-conveyor at (2, 0) connected to (0, 0)
                        end
                        """
        );
    }

    @Test
    public void refusesTooManyConnections() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@bridge-conveyor' at \\(\\s*0,\\s*0\\) has more than one connection\\."),
                """
                        schematic
                            @bridge-conveyor at (0, 0) connected to (1, 0), (2, 0)
                            @bridge-conveyor at (1, 0)
                            @bridge-conveyor at (2, 0)
                        end
                        """
        );
    }

    @Test
    public void refusesConnectionsToDifferentType() {
        assertGeneratesErrors(
                ExpectedMessages.create()
                        .addRegex("Block '@bridge-conveyor' at \\(\\s*0,\\s*0\\) has a connection leading to a different block type '@phase-conveyor' at \\(\\s*1,\\s*0\\)\\."),
                """
                        schematic
                            @bridge-conveyor at (0, 0) connected to (1, 0)
                            @phase-conveyor  at (1, 0)
                        end
                        """
        );
    }
}
