package info.teksol.mindcode.mindustry.logic;

import java.util.List;

public class OpcodeVariant {
    private final ProcessorVersion versionFrom;
    private final ProcessorVersion versionTo;
    private final ProcessorEdition edition;
    private final FunctionMapping functionMapping;
    private final Opcode opcode;
    private final List<NamedArgument> arguments;

    OpcodeVariant(ProcessorVersion versionFrom, ProcessorVersion versionTo, ProcessorEdition edition,
            FunctionMapping functionMapping, Opcode opcode, NamedArgument... arguments) {
        this.versionFrom = versionFrom;
        this.versionTo = versionTo;
        this.edition = edition;
        this.functionMapping = functionMapping;
        this.opcode = opcode;
        this.arguments = List.of(arguments);
    }

    public boolean isAvailableIn(ProcessorVersion version, ProcessorEdition edition) {
        return versionFrom.ordinal() <= version.ordinal() && version.ordinal() <= versionTo.ordinal()
                && edition.ordinal() >= this.edition.ordinal();
    }

    public ProcessorVersion getVersionFrom() {
        return versionFrom;
    }

    public ProcessorVersion getVersionTo() {
        return versionTo;
    }

    public ProcessorEdition getEdition() {
        return edition;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public List<NamedArgument> getArguments() {
        return arguments;
    }

    public FunctionMapping getFunctionMapping() {
        return functionMapping;
    }
    public static enum FunctionMapping {
        NONE,       // No mapping
        FUNC,       // Mapping to function
        PROP;       // Mapping to property access (block.method(arguments)
    }
}
