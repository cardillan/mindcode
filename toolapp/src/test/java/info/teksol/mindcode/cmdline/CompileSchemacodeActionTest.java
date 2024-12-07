package info.teksol.mindcode.cmdline;

import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.FinalCodeOutput;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompileSchemacodeActionTest extends AbstractCommandLineTest {

    @Test
    public void inputArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut());
        assertEquals(Action.COMPILE_SCHEMA, Action.fromShortcut(arguments.get("action")));
        assertEquals(new File("-"), arguments.get("input"));
    }

    @Test
    public void inputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf");
        assertEquals(new File("input.sdf"), arguments.get("input"));
    }

    @Test
    public void outputArgumentDefaultDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(new File("-"), output);
    }

    @Test
    public void outputArgumentDefaultFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(new File("input.msch"), output);
    }

    @Test
    public void outputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf output.msch");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".msch");
        assertEquals(output, arguments.get("output"));
    }

    @Test
    public void logArgumentNone() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf output.msch");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("-"), output);
    }

    @Test
    public void logArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf output.msch -l");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("input.log"), output);
    }

    @Test
    public void logArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " input.sdf output.msch -l log.log");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("log.log"), output);
    }

    @Test
    public void optimizationArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -o basic");
        assertEquals(OptimizationLevel.BASIC, arguments.get("optimization"));
    }

    @Test
    public void specificOptimizationArguments() throws ArgumentParserException {
        List<String> expected = Collections.nCopies(Optimization.LIST.size(), OptimizationLevel.NONE.name());
        List<String> actual = new ArrayList<>();
        for (Optimization optimization : Optimization.LIST) {
            String cmdLine = Action.COMPILE_SCHEMA.getShortcut() + " --" + optimization.getOptionName() + " none";
            Namespace arguments = parseCommandLine(cmdLine);
            actual.add(arguments.<OptimizationLevel>get(optimization.name()).name());
        }
        assertEquals(String.join("\n", expected), String.join("\n", actual));
    }

    @Test
    public void targetArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -t 6");
        assertEquals("6", arguments.get("target"));
    }

    @Test
    public void parseTreeArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -p 1");
        assertEquals(Integer.valueOf(1), arguments.get("parse_tree"));
    }

    @Test
    public void debugMessagesArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -d 3");
        assertEquals(Integer.valueOf(3), arguments.get("debug_messages"));
    }

    @Test
    public void finalCodeOutputDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -u");
        assertEquals(FinalCodeOutput.PLAIN, arguments.get("print_unresolved"));
    }

    @Test
    public void finalCodeOutputFlat() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -u flat_ast");
        assertEquals(FinalCodeOutput.FLAT_AST, arguments.get("print_unresolved"));
    }

    @Test
    public void stacktraceArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -s");
        assertEquals(true, arguments.get("stacktrace"));
    }

    @Test
    public void createsCompilerProfile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut() + " -t 6 -o none -p 1 -d 3 -u source -s -g size");
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);

        assertEquals(ProcessorEdition.STANDARD_PROCESSOR, actual.getProcessorEdition());
        assertEquals(ProcessorVersion.V6, actual.getProcessorVersion());
        assertEquals(OptimizationLevel.NONE, actual.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION));
        assertEquals(1, actual.getParseTreeLevel());
        assertEquals(3, actual.getDebugLevel());
        assertEquals(GenerationGoal.SIZE, actual.getGoal());
        assertEquals(FinalCodeOutput.SOURCE, actual.getFinalCodeOutput());
        assertTrue(actual.isPrintStackTrace());
    }

    @Test
    public void createsCompilerProfileDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_SCHEMA.getShortcut());
        CompilerProfile expected = CompilerProfile.fullOptimizations(false);
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);

        assertEquals(expected.getProcessorEdition(), actual.getProcessorEdition());
        assertEquals(expected.getProcessorVersion(), actual.getProcessorVersion());
        assertEquals(expected.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION), actual.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION));
        assertEquals(expected.getParseTreeLevel(), actual.getParseTreeLevel());
        assertEquals(expected.getDebugLevel(), actual.getDebugLevel());
        assertEquals(expected.getGoal(), actual.getGoal());
        assertEquals(expected.getFinalCodeOutput(), actual.getFinalCodeOutput());
        assertEquals(expected.isPrintStackTrace(), actual.isPrintStackTrace());
    }
}