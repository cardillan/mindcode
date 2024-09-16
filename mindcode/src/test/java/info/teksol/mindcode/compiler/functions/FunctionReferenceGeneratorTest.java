package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.functions.FunctionMapper.FunctionSample;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static info.teksol.mindcode.logic.ProcessorEdition.W;
import static info.teksol.mindcode.logic.ProcessorEdition.WORLD_PROCESSOR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionReferenceGeneratorTest {

    private static final String SYNTAX_REL_PATH = ".." + File.separatorChar + "doc" + File.separatorChar + "syntax" + File.separatorChar;

    private static final String PREAMBLE = """
            This document contains function reference for all built-in Mindcode functions. Functions are grouped by the
            instruction they encapsulate, so that functions with similar logic are listed together. The Mindcode source
            listed in the **Function call** column is compiled to the instruction in the **Generated instruction**
            column.
                        
            In some cases, a single instruction can be generated in more than one way (e.g. the `radar` instruction,
            which can be written as a `turret.radar` function, or as a `radar` function which takes `turret` as a parameter).
            Both ways are identical. Additionally, some functions have optional parameters, which are marked by
            a question mark (e.g. `building?`). Only output parameters are optional, and you may omit them if you don't
            need the value they return. When omitted, the optional parameter is replaced by an unused temporary variable.
            Mindcode allows you to omit all optional argument, but in this case the entire instruction will be considered
            useless and may be removed by the optimizer.
            """;

    private static final String[] navigation = {
            "Code optimization", "SYNTAX-6-OPTIMIZATIONS.markdown",
            "Function reference for Mindustry Logic 6", "FUNCTIONS_V6.markdown",
            "Function reference for Mindustry Logic 7", "FUNCTIONS_V7.markdown",
            "Function reference for Mindustry Logic 7A", "FUNCTIONS_V7A.markdown",
            "Schemacode", "SCHEMACODE.markdown",
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

    private void createFunctionReference(ProcessorVersion version) throws IOException {
        assertTrue(new File(SYNTAX_REL_PATH + "SYNTAX.markdown").isFile());
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(version, W);
        FunctionMapper mapper = FunctionMapperFactory.getFunctionMapper(processor, () -> STATIC_AST_CONTEXT, s -> {});
        List<FunctionSample> samples = assertDoesNotThrow(mapper::generateSamples);

        try (final PrintWriter w = new PrintWriter(SYNTAX_REL_PATH + "FUNCTIONS_" + version + ".markdown", StandardCharsets.UTF_8)) {
            w.println("# Function reference for Mindustry " + version);
            w.println();
            w.print(PREAMBLE.replaceAll("\n", System.lineSeparator()));
            w.println();

            for (ProcessorEdition edition : ProcessorEdition.values()) {
                boolean first = true;

                for (Opcode opcode : Opcode.values()) {
                    // Does this opcode exist in edition?
                    if (processor.getOpcodeVariants().stream().noneMatch(v -> v.edition() == edition && v.opcode() == opcode)) {
                        continue;
                    }

                    if (first) {
                        w.println("# " + edition.getTitle());
                        w.println();
                        if (edition == WORLD_PROCESSOR) {
                            w.println("These instructions are only available to the World Processor,");
                            w.println("which can be placed in custom-created levels in Mindustry 7.");
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

            w.printf("[« Previous: %s](%s) &nbsp; | &nbsp; [Next: %s »](%s)",
                    navigation[prev], navigation[prev + 1], navigation[next], navigation[next + 1]);
            w.println();
        }
    }

    private void printOpcode(InstructionProcessor processor, PrintWriter w, Opcode opcode,
            List<FunctionSample> list) {

        if (list.stream().noneMatch(v -> v.instruction().getOpcode() == opcode)) {
            return;
        }

        w.println();
        w.println("## Instruction `" + opcode.toString() + "`");
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
        }
        w.print("|`");
        w.print(LogicInstructionPrinter.toString(processor, sample.instruction()));
        w.println("`|");
    }
}
