package info.teksol.mindcode.compiler;

import info.teksol.emulator.processor.ExecutionFlag;
import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.util.CollectionUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class MindcodeCompilerTest {
    public static final String LIBRARY_DIRECTORY = "src/main/resources/library";

    // The path to the tests directory needs to be different, otherwise production code would try to load system
    // libraries from there during unit tests
    public static final String LIBRARY_TESTS_DIRECTORY = "src/test/resources/library/tests";
    public static final String LIBRARY_OUTPUTS_DIRECTORY = "src/test/resources/library/outputs";

    private CompilerProfile createCompilerProfile() {
        return new CompilerProfile(false, OptimizationLevel.ADVANCED)
                .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                .setPrintStackTrace(true)
                .setRun(true);
    }

    private MindcodeCompiler createCompiler() {
        return new MindcodeCompiler(createCompilerProfile());
    }

    private MindcodeCompiler createCompiler(CompilerProfile profile) {
        return new MindcodeCompiler(profile);
    }

    @Test
    public void producesAllOutputs() {
        CompilerOutput<String> result = createCompiler().compile(InputFile.createSourceFiles("""
                remark("This is a parameter");
                param value = true;
                if value then
                    print("Hello");
                end;
                """));

        assertEquals("""
                jump 2 always 0 0
                print "This is a parameter"
                set value true
                jump 0 equal value false
                print "Hello"
                """,
                result.output());

        assertEquals("Hello", result.getProgramOutput());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        MindcodeMessage message = result.messages().get(index + 1);

        // Indenting is crucial here
        assertEquals("""
                        label __start__
                    0:  remark "This is a parameter"
                    2:  set value true
                    3:  jump __start__ equal value false
                    4:  print "Hello"
                """, message.message());
    }

    @Test
    public void handlesMultipleFiles() {
        InputFile file1 = new InputFile("file1.mnd", "file1.mnd", "print(\"File1\");");
        InputFile file2 = new InputFile("file2.mnd", "file2.mnd", "print(\"File2\");");

        CompilerOutput<String> result = createCompiler().compile(List.of(file1, file2));

        assertEquals("""
                print "File2File1"
                """,
                result.output());

        assertEquals("File2File1", result.getProgramOutput());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        MindcodeMessage message = result.messages().get(index + 1);

        // Indenting is crucial here
        assertEquals("""
                    0:  print "File2File1"
                """, message.message());
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode compilesSystemLibraries() {
        final File[] files = new File(LIBRARY_DIRECTORY).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + LIBRARY_DIRECTORY + "; found none");
        List<OptimizationLevel> levels = List.of(OptimizationLevel.values());

        return DynamicContainer.dynamicContainer("Optimization tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(f -> f.replace(".mnd", ""))
                        .flatMap(name -> levels.stream().map(
                                level -> DynamicTest.dynamicTest(name + ":" + level,
                                        null, () -> compileLibrary(name, level))
                        ))
        );
    }

    private void compileLibrary(String filename, OptimizationLevel level) throws IOException {
        Path testFile = Path.of(LIBRARY_TESTS_DIRECTORY, filename + ".mnd");
        String code;
        if (testFile.toFile().exists()) {
            // Separate testing code
            code = Files.readString(testFile);
        } else {
            // Create the test code automatically
            InputFile source = MindcodeCompiler.loadLibraryFromResource(filename);

            String initializations = """
                    #set target = ML8A;
                    require %s;
                    """.formatted(filename);

            String variables = source.code().lines()
                    .filter(line -> line.startsWith("def ") || line.startsWith("void "))
                    .flatMap(MindcodeCompilerTest::extractVariables)
                    .distinct()
                    .sorted()
                    .map(s -> s + " = null;")
                    .collect(Collectors.joining("\n"));

            String functionCalls = source.code().lines()
                    .filter(line -> line.startsWith("def "))
                    .map(line -> "println(" + line.substring(4) + ");")
                    .collect(Collectors.joining("\n"));

            String procedureCalls = source.code().lines()
                    .filter(line -> line.startsWith("void "))
                    .map(line -> line.substring(5) + ";")
                    .collect(Collectors.joining("\n"));

            // We know there must be a variable names display
            code = initializations + "\n" + variables + "\n\n" + functionCalls + "\n" + procedureCalls;

            Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + ".mnd"),
                    normalizeLineEndings(code), StandardCharsets.UTF_8);
        }

        CompilerProfile profile = createCompilerProfile()
                .setAllOptimizationLevels(level)
                .setRemarks(Remarks.ACTIVE)
                .clearExecutionFlags(ExecutionFlag.ERR_UNSUPPORTED_OPCODE);

        CompilerOutput<String> result = createCompiler(profile).compile(InputFile.createSourceFiles(code));

        String errorsAndWarnings = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String messages = result.messages().stream()
                .filter(m -> !m.message().startsWith("\nPerformance: parsed"))
                .map(m -> m.formatMessage(InputPosition::formatForIde))
                .collect(Collectors.joining("\n"));
        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + "-" + level.name().toLowerCase() + ".log"),
                normalizeLineEndings(messages), StandardCharsets.UTF_8);

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + "-" + level.name().toLowerCase() + ".mlog"),
                normalizeLineEndings(result.output()), StandardCharsets.UTF_8);

        if (result.hasProgramOutput()) {
            System.out.println(result.getProgramOutput());
        }

        extractTestResults(result.getProgramOutput()).forEach(TestResult::assertValid);

        if (!errorsAndWarnings.isEmpty()) {
            fail("Unexpected error or warning messages were generated:\n" + errorsAndWarnings);
        }
    }

    private String normalizeLineEndings(String string) {
        return string.replaceAll("\\R", System.lineSeparator());

    }

    private static Stream<String> extractVariables(String declaration) {
        int start = declaration.indexOf('(');
        int end = declaration.indexOf(')');
        return start < end - 1
                ? Arrays.stream(declaration.substring(start + 1, end).split(",")).map(String::trim)
                : Stream.empty();
    }

    private record TestResult(String title, String expected, String actual) {
        void assertValid() {
            assertEquals(expected, actual,"Test " + title + " failed");
        }
    }

    private List<TestResult> extractTestResults(String output) {
        List<TestResult> results = new ArrayList<>();
        String title = null;
        String expected = null;
        String actual = null;

        for (String line : output.split("\n")) {
            if (line.startsWith("T:")) {
                title = line.substring(2);
            } else if (line.startsWith("E:")) {
                expected = line.substring(2);
            } else if (line.startsWith("A:")) {
                actual = line.substring(2);

                if (title == null || expected == null) {
                    throw new MindcodeInternalError("Unexpected structure of test output.");
                } else {
                    results.add(new TestResult(title, expected, actual));
                    title = null;
                    expected = null;
                    actual = null;
                }
            }
        }

        return results;
    }

    @Test
    void compileLibrary() throws IOException {
        compileLibrary("printing", OptimizationLevel.ADVANCED);
    }
}
