package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.cmdline.Main.Action;
import info.teksol.mindcode.compiler.*;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompileMindcodeActionTest extends AbstractCommandLineTest {

    @Test
    public void inputArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut());
        assertEquals(Action.COMPILE_MINDCODE, Action.fromShortcut(arguments.get("action")));
        assertEquals(new File("-"), arguments.get("input"));
    }

    @Test
    public void inputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd");
        assertEquals(new File("input.mnd"), arguments.get("input"));
    }

    @Test
    public void outputArgumentDefaultDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        assertEquals(new File("-"), output);
    }

    @Test
    public void outputArgumentDefaultFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        assertEquals(new File("input.mlog"), output);
    }

    @Test
    public void outputArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        assertEquals(output, arguments.get("output"));
    }

    @Test
    public void logArgumentNone() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("-"), output);
    }

    @Test
    public void logArgumentDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -l");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("input.log"), output);
    }

    @Test
    public void logArgumentFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -l log.log");
        File output = ActionHandler.resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        assertEquals(new File("log.log"), output);
    }

    @Test
    public void optimizationArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -o basic");
        assertEquals(OptimizationLevel.BASIC, arguments.get("optimization"));
    }

    @Test
    public void specificOptimizationArguments() throws ArgumentParserException {
        @SuppressWarnings("unchecked")
        List<String> expected = Collections.nCopies(Optimization.LIST.size(), OptimizationLevel.NONE.name());
        List<String> actual = new ArrayList<>();
        for (Optimization optimization : Optimization.LIST) {
            String cmdLine = Action.COMPILE_MINDCODE.getShortcut() + " --" + optimization.getOptionName() + " none";
            Namespace arguments = parseCommandLine(cmdLine);
            actual.add(arguments.<OptimizationLevel>get(optimization.name()).name());
        }
        assertEquals(String.join("\n", expected), String.join("\n", actual));
    }

    @Test
    public void targetArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -t 6");
        assertEquals("6", arguments.get("target"));
    }

    @Test
    public void parseTreeArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -p 1");
        assertEquals(Integer.valueOf(1), arguments.get("parse_tree"));
    }

    @Test
    public void debugMessagesArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -d 3");
        assertEquals(Integer.valueOf(3), arguments.get("debug_messages"));
    }

    @Test
    public void finalCodeOutputDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -u");
        assertEquals(FinalCodeOutput.PLAIN, arguments.get("print_unresolved"));
    }

    @Test
    public void finalCodeOutputFlat() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -u flat_ast");
        assertEquals(FinalCodeOutput.FLAT_AST, arguments.get("print_unresolved"));
    }

    @Test
    public void stacktraceArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -s");
        assertEquals(true, arguments.get("stacktrace"));
    }

    @Test
    public void goalArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -g auto");
        assertEquals(GenerationGoal.AUTO, arguments.get("goal"));
    }

    @Test
    public void remarksArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -r active");
        assertEquals(Remarks.ACTIVE, arguments.get("remarks"));
    }

    @Test
    public void sortVariablesArgumentMissing() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut());
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);
        assertEquals(List.of(), actual.getSortVariables());
    }

    @Test
    public void sortVariablesArgumentEmpty() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " --sort-variables");
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);
        assertEquals(List.of(SortCategory.values()), actual.getSortVariables());
    }

    @Test
    public void sortVariablesArgumentSpecific() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " --sort-variables PARAMS GLOBALS");
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);
        assertEquals(List.of(SortCategory.PARAMS, SortCategory.GLOBALS), actual.getSortVariables());
    }

    /*
    @Test
    public void memoryModelArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -m restricted");
        assertEquals(MemoryModel.RESTRICTED, arguments.get("memory_model"));
    }
    */

    @Test
    public void createsCompilerProfile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -t 6 -o none -p 1 -d 3 -u source -s -g size -r active -e 100 --run --run-steps 100 --sort-variables ALL");  //  -m restricted
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);

        assertEquals(ProcessorEdition.STANDARD_PROCESSOR, actual.getProcessorEdition());
        assertEquals(ProcessorVersion.V6, actual.getProcessorVersion());
        assertEquals(OptimizationLevel.NONE, actual.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION));
        assertEquals(1, actual.getParseTreeLevel());
        assertEquals(3, actual.getDebugLevel());
        assertEquals(100, actual.getOptimizationPasses());
        assertEquals(GenerationGoal.SIZE, actual.getGoal());
        assertEquals(Remarks.ACTIVE, actual.getRemarks());
        assertEquals(List.of(SortCategory.ALL), actual.getSortVariables());
        //assertEquals(MemoryModel.RESTRICTED, actual.getMemoryModel());
        assertEquals(FinalCodeOutput.SOURCE, actual.getFinalCodeOutput());
        assertTrue(actual.isPrintStackTrace());
        assertTrue(actual.isRun());
        assertEquals(100, actual.getStepLimit());
    }

    @Test
    public void createsCompilerProfileDefault() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut());
        CompilerProfile expected = CompilerProfile.fullOptimizations(false);
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);

        assertEquals(expected.getProcessorEdition(), actual.getProcessorEdition());
        assertEquals(expected.getProcessorVersion(), actual.getProcessorVersion());
        assertEquals(expected.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION), actual.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION));
        assertEquals(expected.getParseTreeLevel(), actual.getParseTreeLevel());
        assertEquals(expected.getDebugLevel(), actual.getDebugLevel());
        assertEquals(CompilerProfile.DEFAULT_INSTRUCTIONS, actual.getInstructionLimit());
        assertEquals(CompilerProfile.DEFAULT_CMDLINE_PASSES, actual.getOptimizationPasses());
        assertEquals(expected.getGoal(), actual.getGoal());
        assertEquals(expected.getRemarks(), actual.getRemarks());
        assertEquals(expected.getSortVariables(), actual.getSortVariables());
        //assertEquals(expected.getMemoryModel(), actual.getMemoryModel());
        assertEquals(expected.getFinalCodeOutput(), actual.getFinalCodeOutput());
        assertEquals(expected.isPrintStackTrace(), actual.isPrintStackTrace());

        assertEquals(expected.isRun(), actual.isRun());
        assertEquals(expected.getStepLimit(), actual.getStepLimit());

    }
}