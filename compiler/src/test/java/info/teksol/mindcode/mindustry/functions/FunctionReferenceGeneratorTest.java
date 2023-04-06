package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPrinter;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.ProcessorEdition.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionReferenceGeneratorTest {

    private static final List<String> PREAMBLE = List.of(
            "This document contains function reference for all built-in Mindcode functions.",
            "Functions are grouped by the instruction they encapsulate, so that functions with similar logic are listed together.",
            "The Mindcode source listed in the **Function call** column is compiled to the instruction in the **Generated instruction** column.",
            System.lineSeparator(),
            System.lineSeparator(),
            "In some cases, a single instruction can be generated in more than one way",
            "(e.g. the `radar` instruction, which can be written as a `turret.radar` function, or as a `radar` function which takes `turret` as a parameter).",
            "Both ways are identical.",
            "Additionally, some functions have optional parameters, which are marked by a question mark (e.g. `building?`).",
            "Only output parameters are optional, and you may omit them if you don't need the value they return.",
            "When omitted, the optional parameter is replaced by an unused temporary variable.",
            "Mindcode allows you to omit all optional argument, but in this case the entire instruction will be considered useless",
            "and may be removed by the optimizer.");

    @Test
    void createFunctionReferenceForV6() throws FileNotFoundException {
        createFunctionReference(ProcessorVersion.V6);
    }

    @Test
    void createFunctionReferenceForV7() throws FileNotFoundException {
        createFunctionReference(ProcessorVersion.V7);
    }

    private void createFunctionReference(ProcessorVersion version) throws FileNotFoundException {
        assertTrue(new File(".." + File.separatorChar + "SYNTAX.markdown").isFile());
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(version, W);
        FunctionMapper mapper = FunctionMapperFactory.getFunctionMapper(processor, s -> {});
        List<FunctionMapper.FunctionSample> samples = assertDoesNotThrow(mapper::generateSamples);

        try (final PrintWriter w = new PrintWriter(".." + File.separatorChar + "FUNCTIONS_" + version + ".markdown")) {
            w.println("# Function reference for Mindustry " + version);
            w.println();
            PREAMBLE.forEach(s -> { w.print(s); w.print(" "); });
            w.println();

            for (ProcessorEdition edition : ProcessorEdition.values()) {
                boolean first = true;

                for (Opcode opcode : Opcode.values()) {
                    // Does this opcode exist in edition?
                    if (processor.getOpcodeVariants().stream().noneMatch(v -> v.getEdition() == edition && v.getOpcode() == opcode)) {
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
        }
    }

    private void printOpcode(InstructionProcessor processor, PrintWriter w, Opcode opcode,
            List<FunctionMapper.FunctionSample> list) {

        if (list.stream().noneMatch(v -> v.instruction.getOpcode() == opcode)) {
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
                .filter(v -> v.instruction.getOpcode() == opcode)
                .sorted(Comparator.comparingInt(v -> v.order))
                .forEach(s -> printSample(processor, w, s));
    }

    private void printSample(InstructionProcessor processor, PrintWriter w, FunctionMapper.FunctionSample sample) {
        w.print("|`");
        w.print(sample.functionCall);
        w.print("`");
        if (!sample.note.isEmpty()) {
            w.print("<br/>");
            w.print(sample.note.replace('\'', '`'));
        }
        w.print("|`");
        w.print(LogicInstructionPrinter.toString(processor, sample.instruction));
        w.println("`|");
    }
}
