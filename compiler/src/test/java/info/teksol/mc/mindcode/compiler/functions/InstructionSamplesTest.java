package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.GenericArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition.W;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// This test produces files containing permutations of instructions and their arguments allowed by metadata.
/// The produced files (`Instruction_Samples_*.txt`) should then be pasted into a Mindustry processor editor
/// (world processor when available, otherwise a standard processor) and then copied back into a different text file.
/// There should be no differences between the file generated by this code and the file obtained from Mindustry
/// Processor editor. Any difference means the metadata in this project are flawed.
public class InstructionSamplesTest {

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

    @Test
    void createInstructionSamplesForV6() throws IOException {
        createInstructionSamples(ProcessorVersion.V6);
    }

    @Test
    void createInstructionSamplesForV7() throws IOException {
        createInstructionSamples(ProcessorVersion.V7);
    }

    @Test
    void createInstructionSamplesForV7A() throws IOException {
        createInstructionSamples(ProcessorVersion.V7A);
    }

    @Test
    void createInstructionSamplesForV8A() throws IOException {
        createInstructionSamples(ProcessorVersion.V8A);
    }

    private void createInstructionSamples(ProcessorVersion version) throws IOException {
        assertTrue(new File(".." + File.separatorChar + "README.markdown").isFile());
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(version, W);
        List<LogicInstruction> instructions = processor.getOpcodeVariants().stream()
                .filter(v -> !v.opcode().isVirtual())
                .flatMap(v -> createOpcodeSamples(processor, v).stream())
                .collect(Collectors.toList());

        File file = new File(".." + File.separatorChar + "Instruction_Samples_" + version + ".txt");
        try (final PrintWriter w = new PrintWriter(file, StandardCharsets.UTF_8)) {
            w.print(LogicInstructionPrinter.toString(processor, instructions));
        }
    }

    private List<LogicInstruction> createOpcodeSamples(InstructionProcessor processor, OpcodeVariant opcodeVariant) {
        if (opcodeVariant.opcode() == Opcode.LABEL) {
            return List.of();
        }

        ProcessorVersion processorVersion = processor.getProcessorVersion();
        List<LogicInstruction> result = new ArrayList<>();
        List<List<String>> combinations = new ArrayList<>();

        for (NamedParameter arg : opcodeVariant.namedParameters()) {
            if (arg.type() == InstructionParameterType.LABEL) {
                combinations.add(List.of("0"));
            } else if (arg.type().isKeyword()) {
                combinations.add(arg.type().getAllowedValues().stream()
                        .filter(v -> v.versions.contains(processorVersion))
                        .flatMap(v -> v.values.stream())
                        .sorted()
                        .toList());
            } else {
                combinations.add(List.of(arg.name()));
            }
        }
        int variants = combinations.stream()
                .mapToInt(List::size)
                .max().orElse(1);

        for (int i = 0; i < variants; i++) {
            final int index = i;
            List<LogicArgument> params = combinations.stream()
                    .map(l -> l.get(index % l.size()))
                    .map(GenericArgument::new)
                    .collect(Collectors.toList());

            result.add(processor.createInstruction(STATIC_AST_CONTEXT, opcodeVariant.opcode(), params));
        }

        return result;
    }
}
