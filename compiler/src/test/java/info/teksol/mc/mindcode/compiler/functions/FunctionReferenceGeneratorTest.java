package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper.FunctionSample;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.opcodes.*;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition.W;
import static info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition.WORLD_PROCESSOR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class FunctionReferenceGeneratorTest extends AbstractFunctionMapperTest {

    private static final String SYNTAX_REL_PATH = ".." + File.separatorChar + "doc" + File.separatorChar + "syntax" + File.separatorChar;

    private static final String PREAMBLE = """
            This document contains function reference for all built-in Mindcode functions. Functions are grouped by the
            instruction they encapsulate, so that functions with similar logic are listed together. The Mindcode source
            listed in the **Function call** column is compiled to the instruction in the **Generated instruction**
            column.
            
            In some cases, a single instruction can be generated in more than one way (e.g. the `radar` instruction,
            which can be written as a `turret.radar` function, or as a `radar` function which takes `turret` as a parameter).
            Both ways are identical. Additionally, some functions have output parameters, which are marked by the 'out' modifier.
            Output parameters are optional, and you may omit them if you don't need the value they return. Mindcode allows
            you to omit all optional argument, but in this case the entire instruction will be considered useless
            and may be removed by the optimizer.
            
            Instruction names in this documentation are present as they appear in Mindustry user interface. Examples of
            generated code use mlog opcodes.
            """;

    private static final String[] navigation = {
            "Extending Mindcode", "SYNTAX-EXTENSIONS.markdown",
            "Function reference for Mindustry Logic 6.0", "FUNCTIONS-60.markdown",
            "Function reference for Mindustry Logic 7.0", "FUNCTIONS-70.markdown",
            "Function reference for Mindustry Logic 7.1", "FUNCTIONS-71.markdown",
            "Function reference for Mindustry Logic 8.0", "FUNCTIONS-80.markdown",
            "System Library", "SYSTEM-LIBRARY.markdown",
    };

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

    private static final Set<Opcode> RELEASED_OPCODES =
            MindustryOpcodeVariants.getSpecificOpcodeVariants(ProcessorVersion.V7A, W)
                    .stream()
                    .map(OpcodeVariant::opcode)
                    .collect(Collectors.toSet());

    @Test
    void createFunctionReferenceForV6() throws IOException {
        createFunctionReference(ProcessorVersion.V6);
    }

    @Test
    void createFunctionReferenceForV7() throws IOException {
        createFunctionReference(ProcessorVersion.V7);
    }

    @Test
    void createFunctionReferenceForV7A() throws IOException {
        createFunctionReference(ProcessorVersion.V7A);
    }

    @Test
    void createFunctionReferenceForV8A() throws IOException {
        createFunctionReference(ProcessorVersion.V8A);
    }

    private void createFunctionReference(ProcessorVersion version) throws IOException {
        assertTrue(new File(SYNTAX_REL_PATH + "SYNTAX.markdown").isFile());
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(version, W);
        FunctionMapper mapper = createFunctionMapper(processor);
        List<FunctionSample> samples = assertDoesNotThrow(mapper::generateSamples);

        try (final PrintWriter w = new PrintWriter(SYNTAX_REL_PATH + "FUNCTIONS-" + version.major + version.minor + ".markdown", StandardCharsets.UTF_8)) {
            w.println("# Function reference for Mindustry Logic " + version.major + "." + version.minor);
            w.println();
            w.print(PREAMBLE.replaceAll("\n", System.lineSeparator()));
            w.println();
            w.println("# Index");
            w.println();

            for (ProcessorEdition edition : ProcessorEdition.values()) {
                w.println("* " + edition.getTitle());

                for (Opcode opcode : Opcode.values()) {
                    // Does this opcode exist in edition?
                    if (samples.stream().noneMatch(v -> v.edition() == edition && v.instruction().getOpcode() == opcode)) {
                        continue;
                    }
                    w.println("  * [Instruction `" + instructionName(opcode) + "`](#instruction-" + linkify(opcode) + ")");
                }
            }

            for (ProcessorEdition edition : ProcessorEdition.values()) {
                boolean first = true;

                for (Opcode opcode : Opcode.values()) {
                    // Does this opcode exist in edition?
                    if (samples.stream().noneMatch(v -> v.edition() == edition && v.instruction().getOpcode() == opcode)) {
                        continue;
                    }

                    if (first) {
                        w.println();
                        w.println("# " + edition.getTitle());
                        w.println();
                        if (edition == WORLD_PROCESSOR) {
                            w.println("These instructions are only available to the World Processor,");
                            w.println("which can be placed in custom-created levels in Mindustry 7 or higher.");
                            w.println();
                        }
                        first = false;
                    }

                    printOpcode(processor, w, opcode, samples);
                }
            }

            int prev = 2 * version.ordinal();
            int next = prev + 4;
            w.println();
            w.println("---");
            w.println();

            w.printf("[« Previous: %s](%s) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: %s »](%s)",
                    navigation[prev], navigation[prev + 1], navigation[next], navigation[next + 1]);
            w.println();
        }
    }

    private void printOpcode(InstructionProcessor processor, PrintWriter w, Opcode opcode, List<FunctionSample> samples) {
        if (samples.stream().noneMatch(v -> v.instruction().getOpcode() == opcode)) {
            return;
        }

        w.println();
        w.println("## Instruction `" + instructionName(opcode) + "`");
        w.println();
        w.println(opcode.getDescription());
        w.println();
        w.println("[Yruei's documentation](" + yrueiDocsLink(opcode) + ")");
        w.println();
        String padding1 = String.join("", Collections.nCopies(80, "&nbsp;"));
        String padding2 = String.join("", Collections.nCopies(50, "&nbsp;"));
        w.println("|Function&nbsp;call" + padding1 + "|Generated&nbsp;instruction" + padding2 + "|");
        w.println("|-------------|---------------------|");

        samples.stream()
                .filter(v -> v.instruction().getOpcode() == opcode)
                .sorted(Comparator.comparingInt(FunctionSample::order))
                .forEach(s -> printSample(processor, w, s));
    }

    private void printSample(InstructionProcessor processor, PrintWriter w, FunctionSample sample) {
        w.print("|`");
        w.print(sample.functionCall());
        w.print("`");
        if (!sample.note().isEmpty()) {
            w.print("<br/>");
            w.print(sample.note().replace('\'', '`'));
        } else {
            sample.instruction().getTypedArguments().forEach(arg -> printPossibleValues(processor, w, arg));
        }
        w.print("|`");
        w.print(LogicInstructionPrinter.toString(processor, sample.instruction()));
        w.println("`|");
    }

    private void printPossibleValues(InstructionProcessor processor, PrintWriter w, TypedArgument arg) {
        Collection<String> values = arg.type().getVersionKeywords(processor.getProcessorVersion());
        if (arg.type().isKeyword()) {
            w.print("<br/>`");
            w.print(arg.argument().toMlog());
            w.print("` - one of `:");
            w.print(String.join("`, `:", values));
            w.print("`.");
        } else if (!arg.type().isSelector() && !values.isEmpty()) {
            w.print("<br/>`");
            w.print(arg.argument().toMlog());
            w.print("` - accepts `");
            w.print(String.join("`, `", values));
            w.print("`.");
        }
    }

    private static String instructionName(Opcode opcode) {
        String s = opcode.getName();
        StringBuilder result = new StringBuilder(s.length() + 3);

        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append(' ');
            }

            result.append(c);
        }

        return result.toString();
    }

    private static String linkify(Opcode opcode) {
        return instructionName(opcode).toLowerCase().replace(' ', '-');
    }

    private static String yrueiDocsLink(Opcode opcode) {
        return "https://yrueii.github.io/MlogDocs/#" + (RELEASED_OPCODES.contains(opcode) ? linkify(opcode) : "bleeding-edge");
    }
}
