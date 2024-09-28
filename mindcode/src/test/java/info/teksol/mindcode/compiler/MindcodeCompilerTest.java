package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MindcodeCompilerTest {

    MindcodeCompiler compiler = new MindcodeCompiler(
            new CompilerProfile(false, OptimizationLevel.ADVANCED)
                    .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                    .setRun(true)
    );

    @Test
    public void producesAllOutputs() {
        CompilerOutput<String> result = compiler.compile(SourceFile.code("""
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

        assertEquals("Hello", result.textBuffer());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        CompilerMessage message = result.messages().get(index + 1);

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
        SourceFile file1 = new SourceFile("file1.mnd", "print(\"File1\")");
        SourceFile file2 = new SourceFile("file2.mnd", "print(\"File2\")");

        CompilerOutput<String> result = compiler.compile(List.of(file1, file2));

        assertEquals("""
                print "File1File2"
                """,
                result.output());

        assertEquals("File1File2", result.textBuffer());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        CompilerMessage message = result.messages().get(index + 1);

        // Indenting is crucial here
        assertEquals("""
                    0:  print "File1File2"
                """, message.message());
    }
}
