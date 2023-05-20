package info.teksol.mindcode.cmdline;

import edu.emory.mathcs.backport.java.util.Collections;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompileSchemacodeActionTest extends AbstractCommandLineTest {

    @Test
    public void inputArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs");
        assertEquals(new File("-"), arguments.get("input"));
    }

    @Test
    public void inputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf");
        assertEquals(new File("input.sdf"), arguments.get("input"));
    }

    @Test
    public void outputArgumentDefaultDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(new File("-"), output);
    }

    @Test
    public void outputArgumentDefaultFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(new File("input.msch"), output);
    }

    @Test
    public void outputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf output.msch");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(output, arguments.get("output"));
    }

    @Test
    public void logArgumentNone() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf output.msch");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("-"), output);
    }

    @Test
    public void logArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf output.msch -l");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("input.log"), output);
    }

    @Test
    public void logArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs input.sdf output.msch -l log.log");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("log.log"), output);
    }

    @Test
    public void optimizationArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -o basic");
        assertEquals(OptimizationLevel.BASIC, arguments.get("optimization"));
    }

    @Test
    public void specificOptimizationArguments() throws ArgumentParserException {
        @SuppressWarnings("unchecked")
        List<String> expected = Collections.nCopies(Optimization.LIST.size(), OptimizationLevel.OFF.name());
        List<String> actual = new ArrayList<>();
        for (Optimization optimization : Optimization.LIST) {
            String cmdLine = "cs --" + optimization.getOptionName() + " off";
            Namespace arguments = parseCommandLine(cmdLine);
            actual.add(arguments.<OptimizationLevel>get(optimization.name()).name());
        }
        assertEquals(String.join("\n", expected), String.join("\n", actual));
    }

    @Test
    public void targetArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -t 6");
        assertEquals("6", arguments.get("target"));
    }

    @Test
    public void parseTreeArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -p 1");
        assertEquals(Integer.valueOf(1), arguments.get("parse_tree"));
    }

    @Test
    public void debugMessagesArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -d 3");
        assertEquals(Integer.valueOf(3), arguments.get("debug_messages"));
    }

    @Test
    public void printVirtualArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -r");
        assertEquals(true, arguments.get("print_virtual"));
    }

    @Test
    public void stacktraceArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -s");
        assertEquals(true, arguments.get("stacktrace"));
    }

    @Test
    public void createsCompilerProfile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine("cs -t 6 -o off -p 1 -d 3 -r -s");
        CompilerProfile compilerProfile = ActionHandler.createCompilerProfile(arguments);

        assertEquals(ProcessorEdition.STANDARD_PROCESSOR, compilerProfile.getProcessorEdition());
        assertEquals(ProcessorVersion.V6, compilerProfile.getProcessorVersion());
        assertEquals(OptimizationLevel.OFF, compilerProfile.getOptimizationLevel(Optimization.RETURN_VALUE_OPTIMIZATION));
        assertEquals(1, compilerProfile.getParseTreeLevel());
        assertEquals(3, compilerProfile.getDebugLevel());
        assertTrue(compilerProfile.isPrintFinalCode());
        assertTrue(compilerProfile.isPrintStackTrace());
    }
}