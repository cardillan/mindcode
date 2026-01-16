package info.teksol.mc.mindcode.logic.opcodes;

import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
class MindustryOpcodeVariantsTest {

    /// Ensures that existing instruction variants correspond to the metadata: checks that the selectors in
    /// variable opcodes are matched 1:1 to the list of possible selector values in the metadata.
    @Test void verifyAllOpcodesArePresent() {
        List<String> errors = new ArrayList<>();
        for (ProcessorVersion processorVersion : ProcessorVersion.values()) {
            if (processorVersion != ProcessorVersion.MAX) verifyAllOpcodesArePresent(errors, processorVersion);
        }
        Assertions.assertTrue(errors.isEmpty(), "\n" + String.join("\n", errors));
    }

    void verifyAllOpcodesArePresent(List<String> errors, ProcessorVersion processorVersion) {
        Map<Opcode, List<OpcodeVariant>> opcodes = MindustryOpcodeVariants.getSpecificOpcodeVariants(processorVersion, ProcessorType.W)
                .stream()
                .collect(Collectors.groupingBy(OpcodeVariant::opcode, Collectors.toList()));

        for (List<OpcodeVariant> variants : opcodes.values()) {
            verifyAllOpcodesArePresent(errors, processorVersion, variants);
        }
    }

    void verifyAllOpcodesArePresent(List<String> errors, ProcessorVersion processorVersion, List<OpcodeVariant> variants) {
        OpcodeVariant opcode = variants.getFirst();
        int index = CollectionUtils.indexOf(opcode.parameterTypes(), InstructionParameterType::isSelector);
        if (opcode.opcode().isVirtual() || index < 0) return;

        InstructionParameterType parameterType = opcode.parameterTypes().get(index);
        Set<String> keywords = new HashSet<>(parameterType.getVersionKeywords(processorVersion));

        // Test that instruction opcodes exists for all known keywords
        for (String k : keywords) {
            String keyword = k.substring(k.startsWith("@") ? 1 : 0);
            long count = variants.stream().filter(v -> v.namedParameters().get(index).name().equals(keyword)).count();
            if (count == 0) {
                errors.add("Missing opcode variant for processor version " + processorVersion + ", opcode '" + opcode.opcode() + "' and selector '" + keyword + "'");
            } else if (count > 1) {
                errors.add("Multiple opcode variants for processor version " + processorVersion + ", opcode '" + opcode.opcode() + "' and selector '" + keyword + "'");
            }
        }

        // Test that there aren't unknown instruction opcodes
        for (OpcodeVariant variant : variants) {
            if (variant.virtual()) continue;

            String keyword = variant.namedParameters().get(index).name();
            if (!keywords.contains(keyword) && !keywords.contains("@" + keyword)) {
                errors.add("Unknown opcode variant for processor version " + processorVersion + ", opcode '" + opcode.opcode() + "' and selector '" + keyword + "'");
            }
        }
    }
}
