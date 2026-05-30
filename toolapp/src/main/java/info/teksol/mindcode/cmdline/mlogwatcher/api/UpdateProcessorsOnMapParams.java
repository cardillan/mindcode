package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProcessorsOnMapParams implements Params {
    public static final String VERSION_SELECTION_EXACT = "exact";
    public static final String VERSION_SELECTION_COMPATIBLE = "compatible";
    public static final String VERSION_SELECTION_ANY = "any";

    private String code;

    @JsonProperty("program_id")
    private ProgramId programId;

    @JsonProperty("variable_name")
    private String variableName;

    @JsonProperty("version_selection")
    private String versionSelection;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProgramId getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramId programId) {
        this.programId = programId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVersionSelection() {
        return versionSelection;
    }

    public void setVersionSelection(String versionSelection) {
        this.versionSelection = versionSelection;
    }
}
