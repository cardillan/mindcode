package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpgradeAllProcessorsOnMapParams implements Params {

    private String code;

    @JsonProperty("program_id")
    private ProgramId programId;

    @JsonProperty("variable_name")
    private String variableName;

    public String getCode() {
        byte[] bytes = Base64.getDecoder().decode(code);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void setCode(String code) {
        byte[] bytes = code.getBytes(StandardCharsets.UTF_8);
        this.code = Base64.getEncoder().encodeToString(bytes);
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
}
