package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;
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

public class SystemLibraryTest {
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

    @Test
    void testOneLibrary() throws IOException {
        testLibrary("math", OptimizationLevel.ADVANCED);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode correctlyCompilesSystemLibraries() {
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
                                        null, () -> testLibrary(name, level))
                        ))
        );
    }

    private void testLibrary(String libraryName, OptimizationLevel level) throws IOException {
        InputFiles inputFiles = InputFiles.create();
        CompilerProfile profile = createCompilerProfile().setAllOptimizationLevels(level).setRemarks(Remarks.ACTIVE);
        MindcodeCompiler compiler = createCompiler(profile, inputFiles);

        Path templateFile = Path.of(LIBRARY_TESTS_DIRECTORY, libraryName + ".txt");
        Path testFile = Path.of(LIBRARY_TESTS_DIRECTORY, libraryName + ".mnd");

        InputFile inputFile;
        if (testFile.toFile().exists()) {
            // Manually written testing code
            inputFile = inputFiles.registerSource( Files.readString(testFile));
        } else if (templateFile.toFile().exists()) {
            // Generate test code from template
            inputFile = inputFiles.registerSource(generateCodeFromTemplate(Files.readString(templateFile)));
        } else {
            // Generate test code from library source
            InputFile source = compiler.loadSystemLibrary(libraryName);
            inputFile = inputFiles.registerSource(generateCodeFromSource(libraryName, source.getCode()));
        }

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + ".mnd"),
                normalizeLineEndings(inputFile.getCode()), StandardCharsets.UTF_8);

        CompilerOutput<String> result = compiler.compile(List.of(inputFile));

        String errorsAndWarnings = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String messages = result.messages().stream()
                .filter(MindcodeMessage::isStable)
                .map(m -> m.formatMessage(InputPosition::formatForIde))
                .collect(Collectors.joining("\n"));
        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + "-" + level.name().toLowerCase() + ".log"),
                normalizeLineEndings(messages), StandardCharsets.UTF_8);

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + "-" + level.name().toLowerCase() + ".mlog"),
                normalizeLineEndings(result.output()), StandardCharsets.UTF_8);

        //if (result.hasProgramOutput()) System.out.println(result.getProgramOutput());

        extractTestResults(result.getProgramOutput()).forEach(TestResult::assertValid);

        if (!errorsAndWarnings.isEmpty()) {
            fail("Unexpected error or warning messages were generated:\n" + errorsAndWarnings);
        }
    }

    private String generateCodeFromTemplate(String template) {
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

    private String generateCodeFromSource(String library, String code) {
        String initializations = "require " + library + ";\n";

        List<String> lines = code.lines()
                .map(line -> line.startsWith("inline ") ? line.substring("inline ".length()) : line)
                .toList();

        String variables = lines.stream()
                .filter(line -> line.startsWith("def ") || line.startsWith("void "))
                .flatMap(SystemLibraryTest::extractVariables)
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

        return initializations + "\n" + variables + "\n\n" + functionCalls + "\n\n" + procedureCalls;
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
}
