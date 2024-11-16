package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
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
import java.util.ArrayList;
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

    private CompilerProfile createCompilerProfile() {
        return new CompilerProfile(false, OptimizationLevel.ADVANCED)
                .setProcessorVersion(ProcessorVersion.MAX)
                .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                .setPrintStackTrace(true)
                .setRun(true);
    }

    private MindcodeCompiler createCompiler(CompilerProfile profile, InputFiles inputFiles) {
        return new MindcodeCompiler(profile, inputFiles);
    }

    private MindcodeCompiler createCompiler(InputFiles inputFiles) {
        return createCompiler(createCompilerProfile(), inputFiles);
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

        CompilerOutput<String> result = createCompiler(inputFiles).compile();

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
        InputFiles inputFiles = InputFiles.create();
        InputFile file1 = inputFiles.registerFile(Path.of("file1.mnd"), "print(\"File1\");");
        InputFile file2 = inputFiles.registerFile(Path.of("file2.mnd"), "print(\"File2\");");

        CompilerOutput<String> result = createCompiler(inputFiles).compile();

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

        return DynamicContainer.dynamicContainer("System library tests",
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
        InputFile inputFile;
        InputFiles inputFiles = InputFiles.create();
        CompilerProfile profile = createCompilerProfile().setAllOptimizationLevels(level).setRemarks(Remarks.ACTIVE);
        MindcodeCompiler compiler = createCompiler(profile, inputFiles);

        Path templateFile = Path.of(LIBRARY_TESTS_DIRECTORY, filename + ".txt");
        Path testFile = Path.of(LIBRARY_TESTS_DIRECTORY, filename + ".mnd");
        if (templateFile.toFile().exists()) {
            // Create test code from template
            inputFile = inputFiles.registerSource(processTemplate(Files.readString(templateFile)));
        } else if (testFile.toFile().exists()) {
            // Explicitly written testing code
            inputFile = inputFiles.registerSource( Files.readString(testFile));
        } else {
            // Create the test code automatically
            InputFile source = compiler.loadSystemLibrary(filename);

            String initializations = "require " + filename + ";\n";

            List<String> lines = source.getCode().lines()
                    .map(line -> line.startsWith("inline ") ? line.substring("inline ".length()) : line)
                    .toList();

            String variables = lines.stream()
                    .filter(line -> line.startsWith("def ") || line.startsWith("void "))
                    .flatMap(MindcodeCompilerTest::extractVariables)
                    .distinct()
                    .sorted()
                    .map(s -> s + " = null;")
                    .collect(Collectors.joining("\n"));

            String functionCalls = lines.stream()
                    .filter(line -> line.startsWith("def "))
                    .map(line -> "println(" + line.substring(4) + ");")
                    .collect(Collectors.joining("\n"));

            String procedureCalls = lines.stream()
                    .filter(line -> line.startsWith("void "))
                    .map(line -> line.substring(5) + ";")
                    .collect(Collectors.joining("\n"));

            // We know there must be a variable names display
            String code = initializations + "\n" + variables + "\n\n" + functionCalls + "\n" + procedureCalls;
            inputFile = inputFiles.registerSource(code);
        }

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, filename + ".mnd"),
                normalizeLineEndings(inputFile.getCode()), StandardCharsets.UTF_8);

        CompilerOutput<String> result = compiler.compile(List.of(inputFile));

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

        //if (result.hasProgramOutput()) System.out.println(result.getProgramOutput());

        extractTestResults(result.getProgramOutput()).forEach(TestResult::assertValid);

        if (!errorsAndWarnings.isEmpty()) {
            fail("Unexpected error or warning messages were generated:\n" + errorsAndWarnings);
        }
    }

    private String processTemplate(String template) {
        StringBuilder code = new StringBuilder();

        template.lines().forEach(line -> {
            int index1 = line.indexOf("::");
            int index2 = line.indexOf(":*");
            if (index1 >= 0) {
                String actual = line.substring(0, index1).trim();
                String expected = line.substring(index1 + 2).trim();
                code.append("assertEquals(")
                        .append(expected).append(", ")
                        .append(actual).append(", ")
                        .append('"').append(actual.replace('"','\'')).append('"')
                        .append(");\n");
            } else if (index2 >= 0) {
                String actual = line.substring(0, index2).trim();
                String expected = line.substring(index2 + 2).trim();
                code.append("assertWillPrint(")
                        .append(expected).append(", ")
                        .append('"').append(actual.replace('"','\'')).append('"')
                        .append(");")
                        .append(actual).append("; println();\n");
            } else {
                code.append(line).append("\n");
            }
        });

        return code.toString();
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
        compileLibrary("math", OptimizationLevel.ADVANCED);
    }
}
