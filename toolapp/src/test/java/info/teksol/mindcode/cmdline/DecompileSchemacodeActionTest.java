package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.cmdline.Main.Action;
import info.teksol.schemacode.schematics.BlockOrder;
import info.teksol.schemacode.schematics.Decompiler;
import info.teksol.schemacode.schematics.DirectionLevel;
import info.teksol.schemacode.schematics.Schematic;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class DecompileSchemacodeActionTest {
    private final Action action = Action.DECOMPILE_SCHEMA;

    protected Namespace parseCommandLine(String commandLine) throws ArgumentParserException {
        ArgumentParser parser = Main.createArgumentParser(Arguments.fileType(), 79);
        String args = action.getShortcut() + " file " + commandLine;
        return parser.parseArgs(args.split("\\s+"));
    }

    protected Decompiler parseToDecompiler(String commandLine) throws ArgumentParserException {
        Namespace arguments = parseCommandLine(commandLine);
        Decompiler decompiler = new Decompiler(Schematic.empty());
        ((DecompileSchemacodeAction) action.getHandler()).configureDecompiler(decompiler, arguments);
        return decompiler;
    }

    @Test
    public void recognizesRelativePositionsShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-p");
        assertTrue(decompiler.isRelativePositions());
    }

    @Test
    public void recognizesRelativePositionsLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--relative-positions");
        assertTrue(decompiler.isRelativePositions());
    }

    @Test
    public void recognizesAbsolutePositionsShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-P");
        assertFalse(decompiler.isRelativePositions());
    }

    @Test
    public void recognizesAbsolutePositionsLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--absolute-positions");
        assertFalse(decompiler.isRelativePositions());
    }

    @Test
    public void recognizesRelativeConnectionsShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-c");
        assertTrue(decompiler.isRelativeConnections());
    }

    @Test
    public void recognizesRelativeConnectionsLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--relative-connections");
        assertTrue(decompiler.isRelativeConnections());
    }

    @Test
    public void recognizesAbsoluteConnectionsShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-C");
        assertFalse(decompiler.isRelativeConnections());
    }

    @Test
    public void recognizesAbsoluteConnectionsLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--absolute-connections");
        assertFalse(decompiler.isRelativeConnections());
    }

    @Test
    public void recognizesRelativeLinksShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-l");
        assertTrue(decompiler.isRelativeLinks());
    }

    @Test
    public void recognizesRelativeLinksLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--relative-links");
        assertTrue(decompiler.isRelativeLinks());
    }

    @Test
    public void recognizesAbsoluteLinksShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-L");
        assertFalse(decompiler.isRelativeLinks());
    }

    @Test
    public void recognizesAbsoluteLinksLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--absolute-links");
        assertFalse(decompiler.isRelativeLinks());
    }

    @Test
    public void recognizesSortOrderOriginalShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-s original");
        assertEquals(BlockOrder.ORIGINAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesSortOrderHorizontalShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-s horizontal");
        assertEquals(BlockOrder.HORIZONTAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesSortOrderVerticalShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-s vertical");
        assertEquals(BlockOrder.VERTICAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesSortOrderOriginalLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--sort-order original");
        assertEquals(BlockOrder.ORIGINAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesSortOrderHorizontalLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--sort-order horizontal");
        assertEquals(BlockOrder.HORIZONTAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesSortOrderVerticalLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--sort-order vertical");
        assertEquals(BlockOrder.VERTICAL, decompiler.getBlockOrder());
    }

    @Test
    public void recognizesDirectionRotatableShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-d rotatable");
        assertEquals(DirectionLevel.ROTATABLE, decompiler.getDirectionLevel());
    }

    @Test
    public void recognizesDirectionNonDefaultShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-d non-default");
        assertEquals(DirectionLevel.NON_DEFAULT, decompiler.getDirectionLevel());
    }

    @Test
    public void recognizesDirectionAllShort() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("-d all");
        assertEquals(DirectionLevel.ALL, decompiler.getDirectionLevel());
    }

    @Test
    public void recognizesDirectionRotatableLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--direction rotatable");
        assertEquals(DirectionLevel.ROTATABLE, decompiler.getDirectionLevel());
    }

    @Test
    public void recognizesDirectionNonDefaultLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--direction non-default");
        assertEquals(DirectionLevel.NON_DEFAULT, decompiler.getDirectionLevel());
    }

    @Test
    public void recognizesDirectionAllLong() throws ArgumentParserException {
        Decompiler decompiler = parseToDecompiler("--direction all");
        assertEquals(DirectionLevel.ALL, decompiler.getDirectionLevel());
    }
}