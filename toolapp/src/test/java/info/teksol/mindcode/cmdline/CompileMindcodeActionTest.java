package info.teksol.mindcode.cmdline;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
public class CompileMindcodeActionTest extends AbstractCommandLineTest {

    public CompileMindcodeActionTest() {
        super(Action.COMPILE_MINDCODE);
    }

    @Nested
    class CompileMindcodeActionParserTest {

        @Nested
        class ClipboardArgumentsTest {
            @Test
            public void clipboardArgument() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -c");
                assertTrue(arguments.getBoolean("clipboard"));
            }

            @Test
            public void noClipboardArgument() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog");
                assertFalse(arguments.getBoolean("clipboard"));
            }
        }

        @Nested
        class WatcherArgumentsTest {
            @Test
            public void watcherArgument() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -w --watcher-port 1234 --watcher-timeout 2000");
                assertTrue(arguments.getBoolean("watcher"));
                assertEquals(1234, arguments.getInt("watcher_port"));
                assertEquals(2000, arguments.getInt("watcher_timeout"));
            }

            @Test
            public void noWatcherArgument() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog");
                assertFalse(arguments.getBoolean("watcher"));
            }
        }

        @Nested
        class InputArgumentsTest {
            @Test
            public void inputArgumentDefault() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("");
                assertEquals(action, Action.fromShortcut(arguments.get("action")));
                assertEquals(new File("-"), arguments.get("input"));
            }

            @Test
            public void inputArgumentFile() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd");
                assertEquals(new File("input.mnd"), arguments.get("input"));
            }

            @Test
            public void excerptArgument() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog --excerpt 3:5-7:4");
                assertEquals(new ExcerptSpecification(3, 5, 7, 4), arguments.get("excerpt"));
            }
        }

        @Nested
        class OutputArgumentsTest {

            @Test
            public void outputArgumentDefaultDefault() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("-");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(new File("-"), output);
            }

            @Test
            public void outputArgumentDefaultFile() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(new File("input.mlog"), output);
            }

            @Test
            public void outputArgumentFile() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(output, arguments.get("output"));
            }
        }

        @Nested
        class OutputDirectoryArgumentsTest {

            @Test
            public void outputArgumentDefaultDefault(@TempDir Path tempDir) throws ArgumentParserException {
                String tempDirPath = tempDir.toAbsolutePath().toString();
                Namespace arguments = parseCommandLine("--output-directory " + tempDirPath + " -");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(new File("-"), output);
            }

            @Test
            public void outputArgumentDefaultFile(@TempDir Path tempDir) throws ArgumentParserException {
                String tempDirPath = tempDir.toAbsolutePath().toString();
                Namespace arguments = parseCommandLine("--output-directory " + tempDirPath + " input.mnd");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(new File(tempDirPath, "input.mlog"), output);
            }

            @Test
            public void outputArgumentFile(@TempDir Path tempDir) throws ArgumentParserException {
                String tempDirPath = tempDir.toAbsolutePath().toString();
                Namespace arguments = parseCommandLine("--output-directory " + tempDirPath + " input.mnd -o output.mlog");
                File output = resolveOutputFile(arguments, ".mlog");
                assertEquals(new File(tempDirPath, "output.mlog"), output);
            }

            @Test
            public void logArgumentDefault(@TempDir Path tempDir) throws ArgumentParserException {
                String tempDirPath = tempDir.toAbsolutePath().toString();
                Namespace arguments = parseCommandLine("--output-directory " + tempDirPath + " input.mnd -o output.mlog -l");
                File output = resolveLogFile(arguments, ".log");
                assertEquals(new File(tempDirPath, "input.log"), output);
            }

            @Test
            public void logArgumentFile(@TempDir Path tempDir) throws ArgumentParserException {
                String tempDirPath = tempDir.toAbsolutePath().toString();
                Namespace arguments = parseCommandLine("--output-directory " + tempDirPath + " input.mnd -o output.mlog -l log.log");
                File output = resolveLogFile(arguments, ".log");
                assertEquals(new File(tempDirPath, "log.log"), output);
            }
        }

        @Nested
        class LogArgumentsTest {
            @Test
            public void logArgumentNone() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog");
                File output = resolveLogFile(arguments, ".log");
                assertEquals(new File("-"), output);
            }

            @Test
            public void logArgumentDefault() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -l");
                File output = resolveLogFile(arguments, ".log");
                assertEquals(new File("input.log"), output);
            }

            @Test
            public void logArgumentFile() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -l log.log");
                File output = resolveLogFile(arguments, ".log");
                assertEquals(new File("log.log"), output);
            }
        }

        @Nested
        class AppendArgumentsTest {
            @Test
            public void appendNotGiven() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog");
                assertNull(arguments.<List<File>>get("append"));
            }

            @Test
            public void appendArgumentFileRequired() {
                assertThrows(ArgumentParserException.class,
                        () -> parseCommandLine("input.mnd output.mlog -a"));
            }

            @Test
            public void appendArgumentOneFile() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -a file1.mnd");
                assertEquals(List.of(new File("file1.mnd")), arguments.get("append"));
            }

            @Test
            public void appendArgumentTwoFiles() throws ArgumentParserException {
                Namespace arguments = parseCommandLine("input.mnd -o output.mlog -a file1.mnd file2.mnd");
                assertEquals(List.of(new File("file1.mnd"), new File("file2.mnd")), arguments.get("append"));
            }
        }
    }

    @Nested
    class CompileMindcodeActionCompilerProfileTest {

        @Nested
        class RunArgumentTest {
            @Test
            public void longArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertFalse(profile.isRun());
            }

            @Test
            public void longArgumentPresent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--run");
                assertTrue(profile.isRun());
            }

            @Test
            public void longArgumentTrue() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--run true");
                assertTrue(profile.isRun());
            }

            @Test
            public void longArgumentFalse() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--run false");
                assertFalse(profile.isRun());
            }
        }

        @Nested
        class RunStepsArgumentTest {
            @Test
            public void longArgument() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--run-steps 10000");
                assertEquals(10000, profile.getStepLimit());
            }
        }

        @Nested
        class OutputProfilingArgumentTest {
            @Test
            public void longArgumentAbsent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("");
                assertFalse(profile.isOutputProfiling());
            }

            @Test
            public void longArgumentPresent() throws ArgumentParserException {
                CompilerProfile profile = parseToProfile("--output-profiling");
                assertTrue(profile.isOutputProfiling());
            }
        }

        @Nested
        class ExecutionFlagsArgumentsTest {
            @Test
            public void executionFlagsTrue() throws ArgumentParserException {
                String cmdLine = ExecutionFlag.LIST.stream()
                        .filter(ExecutionFlag::isSettable)
                        .map(o -> "--" + o.getOptionName() + " true")
                        .collect(Collectors.joining(" "));

                CompilerProfile profile = parseToProfile(cmdLine);
                Set<ExecutionFlag> expected = ExecutionFlag.LIST.stream().filter(ExecutionFlag::isSettable).collect(Collectors.toSet());
                assertEquals(expected, profile.getExecutionFlags().stream()
                        .filter(ExecutionFlag::isSettable).collect(Collectors.toSet()));
            }

            @Test
            public void executionFlagsFalse() throws ArgumentParserException {
                String cmdLine = ExecutionFlag.LIST.stream()
                        .filter(ExecutionFlag::isSettable)
                        .map(o -> "--" + o.getOptionName() + " false")
                        .collect(Collectors.joining(" "));

                CompilerProfile profile = parseToProfile(cmdLine);
                assertEquals(Set.of(), profile.getExecutionFlags().stream()
                        .filter(ExecutionFlag::isSettable).collect(Collectors.toSet()));
            }
        }
    }

    @Test
    public void createsCompilerProfileDefault() throws ArgumentParserException {
        CompilerProfile expected = CompilerProfile.fullOptimizations(false);
        CompilerProfile actual = parseToProfile("");
        assertEquals(expected, actual);
    }
}
