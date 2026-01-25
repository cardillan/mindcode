package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpgradeAllProcessorsOnMapParams implements Params {

    private String code;

    @JsonProperty("program_id")
    private String programId;

    public String getCode() {
        byte[] bytes = Base64.getDecoder().decode(code);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void setCode(String code) {
        byte[] bytes = code.getBytes(StandardCharsets.UTF_8);
        this.code = Base64.getEncoder().encodeToString(bytes);
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }
}
