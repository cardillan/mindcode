package info.teksol.mc.mindcode;

import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.mimex.LVariable;
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
            //* A special-purpose library for testing Mindcode's compatibility with a specific Mindustry version.
            
            module compatibility;
            
            linked message1;
            
            noinit var init;
            volatile var counterNull;
            
            /**
             * This function runs the compatibility test on a Mindustry Logic processor. The compatibility test verifies that the
             * compiler's metadata corresponding to the current target are identical to the actual data in the Mindustry processor.
             * The test needs to be run on a logic processor (a microprocessor can also be used, but it takes a few seconds to finish
             * the test) with a message block linked as `message1`. The result of the test is output on the message block.
             *
             * If the test fails, the message will suggest compiler options to use to avoid compatibility issues. By using the
             * suggested compiler options, Mindcode will generate code that should run correctly on the logic processor on which
             * the test was performed, avoiding the compatibility issues.
             *
             * Additionally, the procedure also tests whether assigning `null` to the `@counter` variable is ignored by the processor,
             * and outputs a message accordingly. Again, a message suggesting a compiler option to use is displayed if the test fails.
             *
             * The function never returns: when the test finishes, the processor loops indefinitely. This test isn't meant to be
             * incorporated into a larger program. Typically, a program for running the test will look like this:
             *
             * ```
             * // Set the target appropriately
             * #set target = 8;
             *
             * require compatibility;
             *
             * runCompatibilityTest();
             * ```
             */
            inline void runCompatibilityTest()
                counterNull = __MINDUSTRY_VERSION__ == "v149"
                    ? "[salmon]set @counter null IS NOT a noop[]\\nYour selected compilation target already handles this.\\n\\n"
                    : "[salmon]set @counter null IS NOT a noop[]\\nUse [gold]#set null-counter-is-noop = false;[] in your programs.\\n\\n";

                if not init then
                    init = true;
                    mlog("set", "@counter", "null");
                    mlog("read", "@counter", "@this", in ".counterNull");
                    counterNull = "[green]set @counter null IS a noop[]\\n\\n";
                end;
            
                if __MINDUSTRY_VERSION__ == "v126.2" then
                    _compatibilityTest6();
                elsif __MINDUSTRY_VERSION__ == "v146" then
                    _compatibilityTest7();
                elsif __MINDUSTRY_VERSION__ == "v149" then
                    _compatibilityTest80();
                else
                    _compatibilityTest81();
                end;
            end;
            
            const _MESSAGE_OK = "[green]No compatibility issues encountered. Mindcode is fully compatible with this Mindustry version.";
            
            const _MESSAGE_GENERAL =
                "[salmon]Mindcode metadata of stable built-ins are not compatible with this Mindustry version.[]\\n" +
                "Please report the problem, and use [gold]#set builtin-evaluation = none;[]\\n" +
                "to avoid incompatibility issues.";
            
            const _MESSAGE_SPECIFIC =
                "[salmon]Mindcode metadata of unstable built-ins are not compatible with this Mindustry version.[]\\n" +
                "Please report the problem, and use [gold]#set builtin-evaluation = compatible;[]\\n" +
                "to avoid incompatibility issues.";
            
            const _MESSAGE_NO_SELECT =
                "[salmon]Cannot evaluate processor compatibility - the [gold]select[] instruction is missing.[]\\n" +
                "Use the correct target for this Mindustry version when compiling your code.";

            inline void _compatibilityTest6()
            %send;
            
            inline void _compatibilityTest7()
            %send;
            
            inline void _compatibilityTest80()
            %send;
            
            inline void _compatibilityTest81()
            %send;
            """;

    public static final String CODE = """
                mlog {
                    print "Testing..."
                    printflush message1
                    print $counterNull;
            
            %s
            %s
                    print $_MESSAGE_OK
                    jump finish always 0 0
                general:
                    print $_MESSAGE_GENERAL
                    jump finish always 0 0
                specific:
                    print $_MESSAGE_SPECIFIC
                finish:
                    printflush message1;
                }
                do while true;
            """;

    public static final String CODE_SELECT = """
                mlog {
                    print "Testing..."
                    printflush message1
                    print $counterNull;
            
                    set e1 ""
                    set e2 ""
                    set msg $_MESSAGE_NO_SELECT

            %s
            %s
                    select msg equal e2 "" $_MESSAGE_OK $_MESSAGE_SPECIFIC
                    select msg equal e1 "" msg $_MESSAGE_GENERAL
                    print msg
                    print "\\n[grey]"
                    print e1
                    print e2
                    printflush message1;
                }
                do while true;
            """;

    @Test
    public void generateCompatibilityLibrary() throws IOException {
        String text = String.format(MODULE,
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V6)),
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V7)),
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V8A)),
                formatMethod(MindustryMetadata.forVersion(ProcessorVersion.V8B))
        );

        Files.writeString(Path.of(LIBRARY_DIRECTORY, TARGET_FILE), StringUtils.normalizeLineEndings(text));
    }

    private String formatMethod(MindustryMetadata metadata) {
        String template = metadata.getProcessorVersion().atLeast(ProcessorVersion.V8B) ? CODE_SELECT : CODE;
        return String.format(template, generateStableBuiltinTest(metadata), generateUnstableBuiltinTest(metadata));
    }

    private boolean isStableBuiltin(MindustryMetadata metadata, LVariable var) {
        return var.isNumericConstant() && metadata.isStableBuiltin(var.name());
    }

    private boolean isUnStableBuiltin(MindustryMetadata metadata, LVariable var) {
        return var.isNumericConstant() && !metadata.isStableBuiltin(var.name());
    }

    private static final String BUILTIN_TEST_GENERAL = """
                    jump general notEqual %s %s
            """;

    private static final String BUILTIN_TEST_GENERAL_STRICT = """
                    op strictEqual result %s %s
                    jump general equal result false
            """;

    private static final String BUILTIN_TEST_SPECIFIC = """
                    jump specific notEqual %s %s
            """;

    private static final String BUILTIN_TEST_SPECIFIC_STRICT = """
                    op strictEqual result %s %s
                    jump specific equal result false
            """;

    private static final String BUILTIN_TEST_GENERAL_STRICT_SELECT = """
                    select e1 strictEqual %s %s e1 "\\n%s"
            """;

    private static final String BUILTIN_TEST_SPECIFIC_STRICT_SELECT = """
                    select e2 strictEqual %s %s e2 "\\n%s"
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
                value == (long) value ? Long.toString((long) value) : Double.toString(value);
    }

    private String generateStableBuiltinTest(MindustryMetadata metadata) {
        boolean select = metadata.getProcessorVersion().atLeast(ProcessorVersion.V8B);
        StringBuilder sb = new StringBuilder();

        metadata.getAllLVars().stream().filter(var -> isStableBuiltin(metadata, var))
                .map(var -> String.format(select
                                ? BUILTIN_TEST_GENERAL_STRICT_SELECT
                                : isIntegerValue(var.numericValue()) ? BUILTIN_TEST_GENERAL : BUILTIN_TEST_GENERAL_STRICT,
                        var.name(), formatValue(var.numericValue()), var.name()))
                .forEach(sb::append);

        appendMindustryContent(sb, metadata, metadata.getAllBlocks(), true);
        appendMindustryContent(sb, metadata, metadata.getAllUnits(), true);
        appendMindustryContent(sb, metadata, metadata.getAllItems(), true);
        appendMindustryContent(sb, metadata, metadata.getAllLiquids(), true);
        return sb.toString();
    }

    private String generateUnstableBuiltinTest(MindustryMetadata metadata) {
        boolean select = metadata.getProcessorVersion().atLeast(ProcessorVersion.V8B);
        StringBuilder sb = new StringBuilder();

        metadata.getAllLVars().stream().filter(var -> isUnStableBuiltin(metadata, var))
                .map(var -> String.format(select
                                ? BUILTIN_TEST_SPECIFIC_STRICT_SELECT
                                : isIntegerValue(var.numericValue()) ? BUILTIN_TEST_SPECIFIC : BUILTIN_TEST_SPECIFIC_STRICT,
                        var.name(), formatValue(var.numericValue()), var.name()))
                .forEach(sb::append);

        appendMindustryContent(sb, metadata, metadata.getAllBlocks(), false);
        appendMindustryContent(sb, metadata, metadata.getAllUnits(), false);
        appendMindustryContent(sb, metadata, metadata.getAllItems(), false);
        appendMindustryContent(sb, metadata, metadata.getAllLiquids(), false);
        return sb.toString();
    }

    private static final String CONTENT_TEST = """
                    sensor id %s @id
                    jump %s notEqual id %d
            """;

    private static final String CONTENT_TEST_SELECT = """
                    sensor id %s @id
                    select %s equal id %d %s "\\n%s"
            """;

    private void appendMindustryContent(StringBuilder sb, MindustryMetadata metadata, Collection<? extends MindustryContent> content, boolean stable) {
        if (metadata.getProcessorVersion().atLeast(ProcessorVersion.V8B)) {
            String var = stable ? "e1" : "e2";
            content.stream().filter(o -> o.logicId() >= 0 && !o.legacy() && metadata.isStableBuiltin(o.name()) == stable)
                    .sorted(Comparator.comparingInt(MindustryContent::logicId))
                    .map(o -> String.format(CONTENT_TEST_SELECT, o.name(), var, o.logicId(), var, o.name()))
                    .forEach(sb::append);
        } else {
            String label = stable ? "general" : "specific";
            content.stream().filter(o -> o.logicId() >= 0 && !o.legacy() && metadata.isStableBuiltin(o.name()) == stable)
                    .sorted(Comparator.comparingInt(MindustryContent::logicId))
                    .map(o -> String.format(CONTENT_TEST, o.name(), label, o.logicId()))
                    .forEach(sb::append);
        }
    }
}
