package info.teksol.mc.mindcode;

import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;

@NullMarked
public class CompatibilityLibraryGeneratorTest {
    public static final String LIBRARY_DIRECTORY = "src/main/resources/library";
    public static final String TARGET_FILE = "compatibility.mnd";

    public static final String MODULE = """
            // Mindcode system library for Mindustry Logic version 8
            // System-generated library for compatibility testing purposes
            //
            //* To use the Compatibility library, use the `require compatibility;` statement.
            
            module compatibility;
            
            linked message1;

            /**
             * This function runs the compatibility test on a Mindustry Logic processor. The compatibility test verifies that the
             * compiler's metadata corresponding to the current target are identical to the actual data in the Mindustry processor.
             * The test needs to be run on a logic processor (a microprocessor can also be used, but it takes a bit longer to finish
             * the test) with a message block linked as `message1`. The result of the test is output on the message block.
             *
             * The function never returns: when the test finishes, the processor loops indefinitely. This test isn't meant to be
             *  incorporated into a larger program. Typically, a program for running the test will look like this:
             *
             * ```
             * // Set the target appropriately
             * #set target = 8;
             *
             * require compatibility;
             *
             * runCompatibilityTest();
             * ```
             *
             * If the test fails, the message will suggest compiler options to use to avoid compatibility issues. By using the
             * suggested compiler options, Mindcode will generate code that should run correctly on the logic processor on which
             * the test was performed, avoiding the compatibility issues.
             *
             * Note: The Mindcode processor emulator currently isn't able to run this function.
             */
            inline void runCompatibilityTest()
                if __TARGET_MAJOR__ == 6 then
                    _compatibilityTest6();
                elsif __TARGET_MAJOR__ == 7 then
                    _compatibilityTest7();
                else
                    _compatibilityTest8();
                end;
            end;
            
            inline void _compatibilityTest6()
            %send;
            
            inline void _compatibilityTest7()
            %send;
            
            inline void _compatibilityTest8()
            %send;
            """;

    public static final String CODE = """
                print("Testing...");
                printflush(message1);
            %s
            %s
                mlog("print", in
                    "[green]No compatibility issues encountered. Mindcode is fully compatible with this Mindustry version.");
                mlog("jump", "finish", "always", 0, 0);
                mlogLabel("general:");
                mlog("print", in
                    "[salmon]Mindcode metadata of stable built-ins are not compatible with this Mindustry version.[]\\n" +
                    "Please report the problem, and use [gold]#set builtin-evaluation = none;[]\\n" +
                    "to avoid incompatibility issues.");
                mlog("jump", "finish", "always", 0, 0);
                mlogLabel("specific:");
                mlog("print", in
                    "[salmon]Mindcode metadata of unstable built-ins are not compatible with this Mindustry version.[]\\n" +
                    "Please report the problem, and use [gold]#set builtin-evaluation = compatible;[]\\n" +
                    "to avoid incompatibility issues.");
                mlogLabel("finish:");
                printflush(message1);
                do while true;
            """;

    @Test
    public void generateCompatibilityLibrary() throws IOException {
        String text = String.format(MODULE,
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V6)),
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V7)),
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V8A))
        );

        Files.writeString(Path.of(LIBRARY_DIRECTORY, TARGET_FILE), StringUtils.normalizeLineEndings(text));
    }

    private String formatMethod(MindustryMetadata metadata) {
        return String.format(CODE, generateStableBuiltinTest(metadata), generateUnstableBuiltinTest(metadata));
    }

    private boolean isStableBuiltin(MindustryMetadata metadata, LVar var) {
        return var.isNumericConstant() && metadata.isStableBuiltin(var.name());
    }

    private boolean isUnStableBuiltin(MindustryMetadata metadata, LVar var) {
        return var.isNumericConstant() && !metadata.isStableBuiltin(var.name());
    }

    private static final String BUILTIN_TEST = """
                mlog("jump", "general", "notEqual", "%s", %s);
            """;

    private static final String BUILTIN_TEST_STRICT = """
                mlog("op", "strictEqual", "result", "%s", %s);
                mlog("jump", "general", "equal", "result", "false");
            """;

    private static final String BUILTIN_TEST_SPECIFIC = """
                mlog("jump", "specific", "notEqual", "%s", %s);
            """;

    private static final String BUILTIN_TEST_SPECIFIC_STRICT = """
                mlog("op", "strictEqual", "result", "%s", %s);
                mlog("jump", "general", "equal", "result", "false");
            """;

    private final double MAX_COLOR = Color.parseColor("%FFFFFFFF");

    private boolean isIntegerValue(double value) {
        return value == (long) value;
    }

    private boolean isColorValue(double value) {
        return value <= MAX_COLOR;
    }

    private String formatValue(double value) {
        return isColorValue(value) ? Color.unpack(value) :
                value == (long)value ? Long.toString((long)value) : Double.toString(value);
    }

    private String generateStableBuiltinTest(MindustryMetadata metadata) {
        StringBuilder sb = new StringBuilder();

        metadata.getAllLVars().stream().filter(var -> isStableBuiltin(metadata, var))
                .map(var -> String.format(isIntegerValue(var.numericValue()) ? BUILTIN_TEST : BUILTIN_TEST_STRICT,
                        var.name(), formatValue(var.numericValue())))
                .forEach(sb::append);

        appendMindustryContent(sb, metadata, metadata.getAllBlocks(), true);
        appendMindustryContent(sb, metadata, metadata.getAllUnits(), true);
        appendMindustryContent(sb, metadata, metadata.getAllItems(), true);
        appendMindustryContent(sb, metadata, metadata.getAllLiquids(), true);
        return sb.toString();
    }

    private String generateUnstableBuiltinTest(MindustryMetadata metadata) {
        StringBuilder sb = new StringBuilder();

        metadata.getAllLVars().stream().filter(var -> isUnStableBuiltin(metadata, var) )
                .map(var -> String.format(isIntegerValue(var.numericValue()) ? BUILTIN_TEST_SPECIFIC : BUILTIN_TEST_SPECIFIC_STRICT,
                        var.name(), formatValue(var.numericValue())))
                .forEach(sb::append);

        appendMindustryContent(sb, metadata, metadata.getAllBlocks(), false);
        appendMindustryContent(sb, metadata, metadata.getAllUnits(), false);
        appendMindustryContent(sb, metadata, metadata.getAllItems(), false);
        appendMindustryContent(sb, metadata, metadata.getAllLiquids(), false);
        return sb.toString();
    }

    private static final String CONTENT_TEST = """
                mlog("sensor", "id", "%s", "@id");
                mlog("jump", "%s", "notEqual", "id", %d);
            """;

    private void appendMindustryContent(StringBuilder sb, MindustryMetadata metadata, Collection<? extends MindustryContent> content, boolean stable) {
        String label = stable ? "general" : "specific";
        content.stream().filter(o -> o.logicId() >= 0 && !o.legacy() && metadata.isStableBuiltin(o.name()) == stable)
                .sorted(Comparator.comparingInt(MindustryContent::logicId))
                .map(o -> String.format(CONTENT_TEST, o.name(), label, o.logicId()))
                .forEach(sb::append);
    }
}
