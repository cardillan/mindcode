package info.teksol.mindcode.logic;

import java.util.List;

public class OpcodeVariant {
    private final ProcessorVersion versionFrom;
    private final ProcessorVersion versionTo;
    private final ProcessorEdition edition;
    private final FunctionMapping functionMapping;
    private final Opcode opcode;
    private final List<NamedParameter> namedParameters;
    private final List<LogicParameter> parameterTypes;

    public OpcodeVariant(ProcessorVersion versionFrom, ProcessorVersion versionTo, ProcessorEdition edition,
            FunctionMapping functionMapping, Opcode opcode, NamedParameter... namedParameters) {
        this.versionFrom = versionFrom;
        this.versionTo = versionTo;
        this.edition = edition;
        this.functionMapping = functionMapping;
        this.opcode = opcode;
        this.namedParameters = List.of(namedParameters);
        this.parameterTypes = this.namedParameters.stream().map(NamedParameter::type).toList();
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

    public List<NamedParameter> getNamedParameters() {
        return namedParameters;
    }

    public List<LogicParameter> getParameterTypes() {
        return parameterTypes;
    }

    public FunctionMapping getFunctionMapping() {
        return functionMapping;
    }
    public enum FunctionMapping {
        NONE,       // No mapping
        FUNC,       // Mapping to function
        PROP,       // Mapping to property access - block.method(arguments)
        BOTH       // Mapping to both a function and a property access
    }

    @Override
    public String toString() {
        return "OpcodeVariant{" + "versionFrom=" + versionFrom + ", versionTo=" + versionTo + ", edition=" + edition
                + ", functionMapping=" + functionMapping + ", opcode=" + opcode + ", arguments=" + namedParameters + '}';
    }
}
