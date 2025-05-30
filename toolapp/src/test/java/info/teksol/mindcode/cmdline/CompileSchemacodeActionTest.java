package info.teksol.mindcode.cmdline;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompileSchemacodeActionTest extends AbstractCommandLineTest {

    protected CompileSchemacodeActionTest() {
        super(Action.COMPILE_SCHEMA);
    }

    @Nested
    class CompileSchemacodeActionParserTest {

        @Test
        public void inputArgumentDefault() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("");
            assertEquals(new File("-"), arguments.get("input"));
        }

        @Test
        public void inputArgumentFile() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf");
            assertEquals(new File("input.sdf"), arguments.get("input"));
        }

        @Test
        public void outputArgumentDefaultDefault() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("-");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
            assertEquals(new File("-"), output);
        }

        @Test
        public void outputArgumentDefaultFile() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
            assertEquals(new File("input.msch"), output);
        }

        @Test
        public void outputArgumentFile() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf -o output.msch");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
            assertEquals(output, arguments.get("output"));
        }

        @Test
        public void logArgumentNone() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf -o output.msch");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
            assertEquals(new File("-"), output);
        }

        @Test
        public void logArgumentDefault() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf -o output.msch -l");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
            assertEquals(new File("input.log"), output);
        }

        @Test
        public void logArgumentFile() throws ArgumentParserException {
            Namespace arguments = parseCommandLine("input.sdf -o output.msch -l log.log");
            File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
            assertEquals(new File("log.log"), output);
        }
    }


    @Nested
    class CompileSchemacodeActionCompilerProfileTest {
        @Nested
        class AddTagArgumentTest {
            @Test
            public void addTagArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertEquals(List.of(), profile.getAdditionalTags());
            }

            @Test
            public void addTagArgumentShort() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("-a Cardillan Test");
                assertEquals(List.of("Cardillan", "Test"), profile.getAdditionalTags());
            }

            @Test
            public void addTagArgumentLong() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--add-tag Cardillan Test");
                assertEquals(List.of("Cardillan", "Test"), profile.getAdditionalTags());
            }
        }
    }

    @Test
    public void createsCompilerProfileDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut());
        CompilerProfile expected = CompilerProfile.fullOptimizations(false);
        CompilerProfile actual = new CompileSchemacodeAction().createCompilerProfile(arguments);

        assertEquals(expected.getProcessorEdition(), actual.getProcessorEdition());
        assertEquals(expected.getProcessorVersion(), actual.getProcessorVersion());
        assertEquals(expected.getOptimizationLevels(), actual.getOptimizationLevels());
        assertEquals(expected.getParseTreeLevel(), actual.getParseTreeLevel());
        assertEquals(expected.getDebugLevel(), actual.getDebugLevel());
        assertEquals(expected.getGoal(), actual.getGoal());
        assertEquals(expected.getFinalCodeOutput(), actual.getFinalCodeOutput());
        assertEquals(expected.isPrintStackTrace(), actual.isPrintStackTrace());
    }
}