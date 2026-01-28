package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogicProcessor {
    public static final String UPDATED = "updated";
    public static final String INCOMPATIBLE_VERSION = "incompatible_version";
    public static final String MISSING_PROGRAM_ID = "missing_program_id";

    // Position on the map
    private float x;

    private float y;

    private String type;

    @JsonProperty("program_id")
    private ProgramId programId;

    private String status;

    public LogicProcessor() {
    }

    public LogicProcessor(float x, float y, String type, ProgramId programId, String status) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.programId = programId;
        this.status = status;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProgramId getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramId programId) {
        this.programId = programId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
