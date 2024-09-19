package info.teksol.mindcode.logic;

import java.util.List;
import java.util.stream.Stream;

public record OpcodeVariant(
        ProcessorVersion versionFrom,
        ProcessorVersion versionTo,
        ProcessorEdition edition,
        FunctionMapping functionMapping,
        Opcode opcode,
        List<NamedParameter> namedParameters,
        List<InstructionParameterType> parameterTypes) {

    public OpcodeVariant(ProcessorVersion versionFrom, ProcessorVersion versionTo, ProcessorEdition edition,
            FunctionMapping functionMapping, Opcode opcode, NamedParameter... namedParameters) {
        this(versionFrom, versionTo, edition, functionMapping, opcode, List.of(namedParameters),
                Stream.of(namedParameters).map(NamedParameter::type).toList());
    }

    public boolean isAvailableIn(ProcessorVersion version, ProcessorEdition edition) {
        return versionFrom.ordinal() <= version.ordinal() && version.ordinal() <= versionTo.ordinal()
                && edition.ordinal() >= this.edition.ordinal();
    }
}
