package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.util.CollectionUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MindcodeCompilerTest {
    public static final String LIBRARY_DIRECTORY = "src/main/resources/library";

    // The path to the tests directory needs to be different, otherwise production code would try to load system
    // libraries from there during unit tests
    public static final String LIBRARY_TESTS_DIRECTORY = "src/test/resources/library/tests";
    public static final String LIBRARY_OUTPUTS_DIRECTORY = "src/test/resources/library/outputs";

    private MindcodeCompiler createCompiler(InputFiles inputFiles, boolean run) {
        return new MindcodeCompiler(
                new CompilerProfile(false, OptimizationLevel.ADVANCED)
                        .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                        .setRun(run),
                inputFiles
        );
    }

    @Test
    public void producesAllOutputs() {
        InputFiles inputFiles = InputFiles.fromSource("""
                remark("This is a parameter");
                param value = true;
                if value then
                    print("Hello");
                end;
                """);

        CompilerOutput<String> result = createCompiler(inputFiles, true).compile();

        assertEquals("""
                jump 2 always 0 0
                print "This is a parameter"
                set value true
                jump 0 equal value false
                print "Hello"
                """,
                result.output());

        assertEquals("Hello", result.textBuffer());

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
        InputFiles inputFiles = InputFiles.create();
        InputFile file1 = inputFiles.registerFile(Path.of("file1.mnd"), "print(\"File1\");");
        InputFile file2 = inputFiles.registerFile(Path.of("file2.mnd"), "print(\"File2\");");

        CompilerOutput<String> result = createCompiler(inputFiles, true).compile();

        assertEquals("""
                print "File2File1"
                """,
                result.output());

        assertEquals("File2File1", result.textBuffer());

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

        return DynamicContainer.dynamicContainer("Optimization tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(f -> f.replace(".mnd", ""))
                        .map(f -> DynamicTest.dynamicTest(f, null, () -> compileLibrary(f)))
        );
    }

    //@Test
    void compileLibrary() throws IOException {
        compileLibrary("blocks");
    }

    private void compileLibrary(String filename) throws IOException {
        InputFile inputFile;
        InputFiles inputFiles = InputFiles.create();
        MindcodeCompiler compiler = createCompiler(inputFiles, false);

        Path testFile = Path.of(LIBRARY_TESTS_DIRECTORY, filename + ".mnd");
        if (testFile.toFile().exists()) {
            // Explicitly written testing code
            inputFile = inputFiles.registerSource( Files.readString(testFile));
        } else {
            // Create the test code automatically
            InputFile source = compiler.loadSystemLibrary(filename);

            String initializations = """
                    #set target = ML8A;
                    require %s;
                    SYS_MESSAGE = null;
                    """.formatted(filename);

            String variables = source.getCode().lines()
                    .filter(line -> line.startsWith("def ") || line.startsWith("void "))
                    .flatMap(MindcodeCompilerTest::extractVariables)
                    .distinct()
                    .sorted()
                    .map(s -> s + " = null;")
                    .collect(Collectors.joining("\n"));

            String functionCalls = source.getCode().lines()
                    .filter(line -> line.startsWith("def "))
                    .map(line -> "println(" + line.substring(4) + ");")
                    .collect(Collectors.joining("\n"));

            String procedureCalls = source.getCode().lines()
                    .filter(line -> line.startsWith("void "))
                    .map(line -> line.substring(5) + ";")
                    .collect(Collectors.joining("\n"));

            // We know there must be a variable names display
            String code = initializations + "\n" + variables + "\n\n" + functionCalls + "\n" + procedureCalls;
            inputFile = inputFiles.registerSource(code);
        }

        CompilerOutput<String> result = compiler.compile(List.of(inputFile));

        String errorsAndWarnings = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .filter(message -> !"List of unused variables: SYS_MESSAGE.".equals(message.trim()))
                .collect(Collectors.joining("\n"));

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + ".mnd"),
                normalizeLineEndings(inputFile.getCode()), StandardCharsets.UTF_8);

        String messages = result.messages().stream()
                .filter(m -> !m.message().startsWith("\nPerformance: parsed"))
                .map(m -> m.formatMessage(InputPosition::formatForIde))
                .collect(Collectors.joining("\n"));
        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + ".log"),
                normalizeLineEndings(messages), StandardCharsets.UTF_8);

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + ".mlog"),
                normalizeLineEndings(result.output()), StandardCharsets.UTF_8);

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
}
