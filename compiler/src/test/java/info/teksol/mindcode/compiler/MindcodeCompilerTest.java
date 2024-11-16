package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MindcodeCompilerTest {

    private CompilerProfile createCompilerProfile() {
        return new CompilerProfile(false, OptimizationLevel.ADVANCED)
                .setProcessorVersion(ProcessorVersion.MAX)
                .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                .setPrintStackTrace(true)
                .setRun(true);
    }

    private MindcodeCompiler createCompiler(InputFiles inputFiles) {
        return new MindcodeCompiler(createCompilerProfile(), inputFiles);
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

    private record TestResult(String title, String expected, String actual) {
        void assertValid() {
            assertEquals(expected, actual,"Test " + title + " failed");
        }
    }

}
