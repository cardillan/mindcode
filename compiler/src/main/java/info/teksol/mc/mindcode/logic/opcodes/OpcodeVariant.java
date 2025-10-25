package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Stream;

@NullMarked
public record OpcodeVariant(
        ProcessorVersion versionFrom,
        ProcessorVersion versionTo,
        ProcessorEdition edition,
        FunctionMapping functionMapping,
        boolean virtual,
        Opcode opcode,
        List<NamedParameter> namedParameters,
        List<InstructionParameterType> parameterTypes) {

    public OpcodeVariant(ProcessorVersion versionFrom, ProcessorVersion versionTo, ProcessorEdition edition,
            FunctionMapping functionMapping, Opcode opcode, NamedParameter... namedParameters) {
        this(versionFrom, versionTo, edition, functionMapping, false, opcode, List.of(namedParameters),
                Stream.of(namedParameters).map(NamedParameter::type).toList());
    }

    public static OpcodeVariant virtual(ProcessorVersion versionFrom, ProcessorVersion versionTo, ProcessorEdition edition,
            FunctionMapping functionMapping, Opcode opcode, NamedParameter... namedParameters) {
        return new OpcodeVariant(versionFrom, versionTo, edition, functionMapping, true, opcode, List.of(namedParameters),
                Stream.of(namedParameters).map(NamedParameter::type).toList());
    }

    public boolean isAvailableIn(ProcessorVersion version, ProcessorEdition edition) {
        return versionFrom.ordinal() <= version.ordinal() && version.ordinal() <= versionTo.ordinal()
                && edition.ordinal() >= this.edition.ordinal();
    }
}
