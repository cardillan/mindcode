package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.FinalCodeOutput;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.PRINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class MindcodeCompilerTest extends AbstractCodeGeneratorTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.ALL;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setFinalCodeOutput(FinalCodeOutput.PLAIN);
    }

    @Test
    void realLifeScript1() {
        assertGeneratesMessages(expectedMessages()
                        .add("List of uninitialized variables: MIN_TO_MAX, SHOOT.").repeat(2),
                """
                        target = uradar(enemy, ground, any, health, MIN_TO_MAX);
                        if target != null then
                            approach(target.@x, target.@y, 10);
                            if within(target.@x, target.@y, 10) then
                                target(target.@x, target.@y, SHOOT);
                            end;
                        end;
                        """
        );
    }

    @Test
    void realLifeScript2() {
        assertGeneratesMessage(
                "List of unused variables: building, x, y.",
                """
                        ulocate(ore, @surge-alloy, out x, out y);
                        ulocate(building, core, ENEMY, out x, out y, out building);
                        ulocate(spawn, out x, out y, out building);
                        ulocate(damaged, out x, out y, out building);
                        """
        );
    }

    @Test
    void realLifeScript3() {
        assertGeneratesMessage(
                "List of uninitialized variables: core.",
                "itemDrop(core, @unit.@totalItems);"
        );
    }

    @Test
    void realLifeScript4() {
        assertCompiles("""
                leader = getlink(0);
                count = 1;
                while count < @links do
                    turret = getlink(count);
                    turret.shoot(leader.@shootX, leader.@shootY, leader.@shooting);
                    count = count + 1;
                end;
                """);
    }

    @Test
    void compilesExternalLibrary() {
        assertCompilesTo("""
                        require math;
                        print(PI);
                        """,
                createInstruction(PRINT, "3.141592653589793")
        );
    }

    @Test
    void compilesUnaryMinus() {
        assertCompilesTo("""
                        const a = 0xabcdefabcdefabc;
                        print(-(a + 1));
                        """,
                createInstruction(PRINT, "-773738404492802700")
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

        ListMessageLogger messageLogger = new ListMessageLogger();
        compile(expectedMessages().setLogger(messageLogger), inputFiles, compiler -> {
            assertEquals("""
                            jump 2 always 0 0
                            print "This is a parameter"
                            set value true
                            jump 0 equal value false
                            print "Hello"
                            """,
                    compiler.getOutput());

            assertEquals("Hello", compiler.getTextBuffer().getFormattedOutput());

            int index = CollectionUtils.indexOf(messageLogger.getMessages(),
                    m -> m.message().contains("Final code before resolving virtual instructions"));
            assertTrue(index >= 0, "Failed to locate code output in the log.");

            MindcodeMessage message = messageLogger.getMessages().get(index + 1);

            // Indenting is crucial here
            assertEquals("""
                            label __start__
                        0:  remark "This is a parameter"
                        2:  set value true
                        3:  jump __start__ equal value false
                        4:  print "Hello"
                    """, message.message());
        });
    }

    @Test
    public void compilesMultipleFiles() {
        InputFiles inputFiles = InputFiles.create();
        InputFile file1 = inputFiles.registerFile(Path.of("file1.mnd"), "print(\"File1\");");
        InputFile file2 = inputFiles.registerFile(Path.of("file2.mnd"), "print(\"File2\");");

        ListMessageLogger messageLogger = new ListMessageLogger();
        compile(expectedMessages().setLogger(messageLogger), inputFiles, compiler -> {
            assertEquals("""
                            print "File2File1"
                            """,
                    compiler.getOutput());

            assertEquals("File2File1", compiler.getTextBuffer().getFormattedOutput());

            int index = CollectionUtils.indexOf(messageLogger.getMessages(),
                    m -> m.message().contains("Final code before resolving virtual instructions"));
            assertTrue(index >= 0, "Failed to locate code output in the log.");

            MindcodeMessage message = messageLogger.getMessages().get(index + 1);

            // Indenting is crucial here
            assertEquals("""
                        0:  print "File2File1"
                    """, message.message());
        });
    }
}