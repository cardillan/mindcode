package info.teksol.mc.mindcode;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import info.teksol.mc.profile.options.Target;
import info.teksol.mc.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
public class SystemLibraryTest {
    public static final String LIBRARY_DIRECTORY = "src/main/resources/library";

    // The path to the tests directory needs to be different, otherwise production code would try to load system
    // libraries from there during unit tests
    public static final String LIBRARY_TESTS_DIRECTORY = "src/test/resources/library/tests";
    public static final String LIBRARY_OUTPUTS_DIRECTORY = "src/test/resources/library/outputs";

    private CompilerProfile createCompilerProfile() {
        return CompilerProfile.fullOptimizations(false, false)
                .setTarget(new Target(ProcessorVersion.MAX, ProcessorType.W))
                .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                .setAutoPrintflush(false)
                .setSyntacticMode(SyntacticMode.STRICT)
                .setOptimizationPasses(25)
                .setPrintStackTrace(true)
                .setPrintCodeSize(false)
                .setRun(true);
    }

    @Test
    void testOneLibrary() throws IOException {
        testLibrary("arrays", OptimizationLevel.NONE);
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
                        .filter(s -> !"compatibility".equals(s))
                        .flatMap(name -> levels.stream().map(
                                level -> DynamicTest.dynamicTest(name + ":" + level,
                                        Path.of(LIBRARY_DIRECTORY, name + ".mnd").toUri(),
                                        () -> testLibrary(name, level))
                        ))
        );
    }

    private void testLibrary(String libraryName, OptimizationLevel level) throws IOException {
        ListMessageLogger messageConsumer = new ListMessageLogger();
        InputFiles inputFiles = InputFiles.create();
        CompilerProfile profile = createCompilerProfile().setAllOptimizationLevels(level).setRemarks(Remarks.ACTIVE);
        MindcodeCompiler compiler = new MindcodeCompiler(messageConsumer, profile, inputFiles);

        Path templateFile = Path.of(LIBRARY_TESTS_DIRECTORY, libraryName + ".txt");
        Path testFile = Path.of(LIBRARY_TESTS_DIRECTORY, libraryName + ".mnd");

        boolean executableTest;
        InputFile inputFile;
        if (testFile.toFile().exists()) {
            // Manually written testing code
            executableTest = true;
            inputFile = inputFiles.registerSource( Files.readString(testFile));
        } else if (templateFile.toFile().exists()) {
            // Generate test code from a template
            executableTest = true;
            inputFile = inputFiles.registerSource(generateCodeFromTemplate(Files.readString(templateFile)));
        } else {
            // Generate test code from a library source
            // No reason to execute it at all, just verify it compiles
            executableTest = false;
            inputFile = inputFiles.registerSource(generateCodeFromSource(libraryName));
        }

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + ".mnd"),
                StringUtils.normalizeLineEndings(inputFile.getCode()), StandardCharsets.UTF_8);

        profile.setRun(executableTest);
        compiler.compile(inputFile);

        String errorsAndWarnings = messageConsumer.getMessages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .filter(s -> !s.equals("The limit of 1000 executable instructions has been exceeded."))
                .collect(Collectors.joining("\n"));

        assertTrue(errorsAndWarnings.isEmpty(), "Unexpected error or warning messages were generated:\n" + errorsAndWarnings);

        String messages = messageConsumer.getMessages().stream()
                .filter(MindcodeMessage::isStable)
                .map(m -> m.formatMessage(sp -> sp.formatForIde(FileReferences.WINDOWS_URI)))
                .collect(Collectors.joining("\n"));
        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + "-" + level.name().toLowerCase() + ".log"),
                StringUtils.normalizeLineEndings(messages), StandardCharsets.UTF_8);

        Files.writeString(Path.of(LIBRARY_OUTPUTS_DIRECTORY, libraryName + "-" + level.name().toLowerCase() + ".mlog"),
                StringUtils.normalizeLineEndings(compiler.getOutput()), StandardCharsets.UTF_8);

        System.out.println(compiler.getTextBufferOutput());

        if (executableTest) {
            assertFalse(compiler.getAssertions().isEmpty(), "No assertions were executed by the unit test.");
            if (compiler.hasRuntimeError()) {
                fail("Runtime error occurred while executing compiled code.");
            }
        }

        compiler.getAssertions().forEach(this::assertSuccess);
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
                code.append("assertPrints(")
                        .append(expected).append(", ")
                        .append(actual).append(", ")
                        .append('"').append(actual.replace('"','\'')).append('"')
                        .append(");\n");
            } else {
                code.append(line).append("\n");
            }
        });

        return code.toString();
    }

    private String loadSystemLibrary(String libraryName) throws IOException {
        try (InputStream resource = MindcodeCompiler.class.getResourceAsStream("/library/" + libraryName + ".mnd")) {
            if (resource == null) {
                throw new IllegalStateException("System library " + libraryName + " not found.");
            }
            try (final InputStreamReader reader = new InputStreamReader(resource)) {
                final StringWriter out = new StringWriter();
                reader.transferTo(out);
                return out.toString();
            }
        }
    }

    private String generateCodeFromSource(String library) throws IOException {
        String code = loadSystemLibrary(library);
        String initializations = "require " + library + ";\n\nbegin\n";

        List<String> lines = code.lines()
                .map(line -> line.startsWith("inline ") ? line.substring("inline ".length()) : line)
                .toList();

        String variables = lines.stream()
                .filter(line -> line.startsWith("def ") || line.startsWith("void "))
                .flatMap(SystemLibraryTest::extractVariables)
                .distinct()
                .sorted()
                .map(s -> "    var " + s + " = null;")
                .collect(Collectors.joining("\n"));

        String functionCalls = lines.stream()
                .filter(line -> line.startsWith("def "))
                .map(line -> "    println(" + line.substring(4) + ");")
                .collect(Collectors.joining("\n"));

        String procedureCalls = lines.stream()
                .filter(line -> line.startsWith("void "))
                .map(line -> "    " + line.substring(5) + ";")
                .collect(Collectors.joining("\n"));

        return initializations + "\n" + variables + "\n\n" + functionCalls + "\n\n" + procedureCalls + "end;";
    }

    private static Stream<String> extractVariables(String declaration) {
        int start = declaration.indexOf('(');
        int end = declaration.indexOf(')');
        return start < end - 1
                ? Arrays.stream(declaration.substring(start + 1, end).split(",")).map(String::trim)
                : Stream.empty();
    }

    void assertSuccess(Assertion assertion) {
        assertEquals(assertion.expected(), assertion.actual(), "Test " + assertion.title() + " failed");
    }
}
