package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper.FunctionSample;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            """;

    private static final String[] navigation = {
            "Code optimization", "SYNTAX-6-OPTIMIZATIONS.markdown",
            "Function reference for Mindustry Logic 6.0", "FUNCTIONS-60.markdown",
            "Function reference for Mindustry Logic 7.0", "FUNCTIONS-70.markdown",
            "Function reference for Mindustry Logic 7.1", "FUNCTIONS-71.markdown",
            "Function reference for Mindustry Logic 8.0", "FUNCTIONS-80.markdown",
            "System Library", "SYSTEM-LIBRARY.markdown",
    };

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

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
                    if (opcode.isVirtual() || processor.getOpcodeVariants().stream().noneMatch(v -> v.edition() == edition && v.opcode() == opcode)) {
                        continue;
                    }
                    w.println("  * [Instruction `" + opcode + "`](#instruction-" + opcode + ")");
                }
            }

            for (ProcessorEdition edition : ProcessorEdition.values()) {
                boolean first = true;

                for (Opcode opcode : Opcode.values()) {
                    // Does this opcode exist in edition?
                    if (processor.getOpcodeVariants().stream().noneMatch(v -> v.edition() == edition && v.opcode() == opcode)) {
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

    private void printOpcode(InstructionProcessor processor, PrintWriter w, Opcode opcode, List<FunctionSample> list) {
        if (list.stream().noneMatch(v -> v.instruction().getOpcode() == opcode)) {
            return;
        }

        w.println();
        w.println("## Instruction `" + opcode + "`");
        w.println();
        w.println(opcode.getDescription());
        w.println();
        String padding1 = String.join("", Collections.nCopies(80, "&nbsp;"));
        String padding2 = String.join("", Collections.nCopies(50, "&nbsp;"));
        w.println("|Function&nbsp;call" + padding1 + "|Generated&nbsp;instruction" + padding2 + "|");
        w.println("|-------------|---------------------|");

        list.stream()
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
            sample.instruction().getTypedArguments().stream()
                    .filter(arg -> arg.type().isKeyword())
                    .forEach(arg -> {
                        w.print("<br/>`");
                        w.print(arg.argument().toMlog());
                        w.print("` - one of `:");
                        w.print(String.join("`, `:", processor.getParameterValues(arg.type())));
                        w.print("`.");
                    });
        }
        w.print("|`");
        w.print(LogicInstructionPrinter.toString(processor, sample.instruction()));
        w.println("`|");
    }
}
