package info.teksol.mindcode.cmdline;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    public void excerptArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog --excerpt 3:5-7:4");
        assertEquals(new ExcerptSpecification(3,5,7,4), arguments.get("excerpt"));
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
    public void appendNotGiven() throws ArgumentParserException {
        Namespace arguments =parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog");
        assertNull(arguments.<List<File>>get("append"));
    }

    @Test
    public void appendArgumentFileRequired() {
        assertThrows(ArgumentParserException.class,
                () -> parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -a"));
    }

    @Test
    public void appendArgumentOneFile() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -a file1.mnd");
        assertEquals(List.of(new File("file1.mnd")), arguments.get("append"));
    }

    @Test
    public void appendArgumentTwoFiles() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -a file1.mnd file2.mnd");
        assertEquals(List.of(new File("file1.mnd"), new File("file2.mnd")), arguments.get("append"));
    }

    @Test
    public void watcherArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -w --watcher-port 1234 --watcher-timeout 2000");
        assertTrue(arguments.getBoolean("watcher"));
        assertEquals(1234, arguments.getInt("watcher_port"));
        assertEquals(2000, arguments.getInt("watcher_timeout"));
    }

    @Test
    public void noWatcherArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog");
        assertFalse(arguments.getBoolean("watcher"));
    }

    @Test
    public void clipboardArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog -c");
        assertTrue(arguments.getBoolean("clipboard"));
    }

    @Test
    public void noClipboardArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " input.mnd output.mlog");
        assertFalse(arguments.getBoolean("clipboard"));
    }


    @Test
    public void optimizationArgument() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -o basic");
        assertEquals(OptimizationLevel.BASIC, arguments.get("optimization"));
    }

    @Test
    public void specificOptimizationArguments() throws ArgumentParserException {
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
    public void targetArgument6() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -t 6");
        assertEquals("6", arguments.get("target"));
    }

    @Test
    public void targetArgument70w() throws ArgumentParserException {
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() + " -t 7.0w");
        assertEquals("7.0w", arguments.get("target"));
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

    @Test
    public void executionFlags() throws ArgumentParserException {
        List<Boolean> expected = new ArrayList<>();
        List<Boolean> actual = new ArrayList<>();
        boolean value = true;
        for (ExecutionFlag flag : ExecutionFlag.LIST) {
            if (flag.isSettable()) {
                value = !value;
                String cmdLine = Action.COMPILE_MINDCODE.getShortcut() + " --" + flag.getOptionName() + " " + value;
                Namespace arguments = parseCommandLine(cmdLine);
                expected.add(value);
                actual.add(arguments.<Boolean>get(flag.name()));
            }
        }
        assertEquals(
                expected.stream().map(Object::toString).collect(Collectors.joining("\n")),
                actual.stream().map(Object::toString).collect(Collectors.joining("\n")));
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
        Namespace arguments = parseCommandLine(Action.COMPILE_MINDCODE.getShortcut() +
                " -t 7.0w -o none -p 1 -d 3 -u source -s -g size -r active -e 100 --run --run-steps 100" +
                " --sort-variables ALL --no-signature --trace-execution true");  //  -m restricted
        CompilerProfile actual = ActionHandler.createCompilerProfile(arguments);

        assertEquals(ProcessorEdition.WORLD_PROCESSOR, actual.getProcessorEdition());
        assertEquals(ProcessorVersion.V7, actual.getProcessorVersion());
        assertEquals(OptimizationLevel.NONE, actual.getOptimizationLevel(Optimization.DATA_FLOW_OPTIMIZATION));
        assertEquals(1, actual.getParseTreeLevel());
        assertEquals(3, actual.getDebugLevel());
        assertEquals(100, actual.getOptimizationPasses());
        assertEquals(GenerationGoal.SIZE, actual.getGoal());
        assertEquals(Remarks.ACTIVE, actual.getRemarks());
        assertEquals(List.of(SortCategory.ALL), actual.getSortVariables());
        assertFalse(actual.isSignature());
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
        assertEquals(expected.isSignature(), actual.isSignature());
        //assertEquals(expected.getMemoryModel(), actual.getMemoryModel());
        assertEquals(expected.getFinalCodeOutput(), actual.getFinalCodeOutput());
        assertEquals(expected.isPrintStackTrace(), actual.isPrintStackTrace());

        assertEquals(expected.isRun(), actual.isRun());
        assertEquals(expected.getStepLimit(), actual.getStepLimit());

    }
}