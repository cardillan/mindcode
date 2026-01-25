package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Response {
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";

    public static final String ERR_INVALID_ARGUMENTS = "invalid_arguments";
    public static final String ERR_NO_PROCESSOR_ATTACHED = "no_processor_attached";
    public static final String ERR_SCHEMATIC_IMPORT_FAILED = "schematic_import_failed";
    public static final String ERR_UNKNOWN_METHOD = "unknown_method";

    public static final String RESULT_TYPE_TEXT = "text_result";

    private String status;

    @JsonProperty("invocation_id")
    private int invocationId;

    @JsonProperty("result_type")
    private String resultType;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "result_type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(
                    value = TextResult.class,
                    name = RESULT_TYPE_TEXT
            )
    })
    private Results result;

    public Response() {
    }

    public Response(String status, int invocationId, String resultType, Results result) {
        this.status = status;
        this.invocationId = invocationId;
        this.resultType = resultType;
        this.result = result;
    }

    public Response(String status, String result) {
        this(status, 0, RESULT_TYPE_TEXT, new TextResult(result));
    }

    public static Response success() {
        return new Response(STATUS_SUCCESS, null);
    }

    public static Response error(String result) {
        return new Response(STATUS_ERROR, result);
    }

    // getters & setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(int invocationId) {
        this.invocationId = invocationId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @SuppressWarnings("unchecked")
    public <T extends Results> T getResult() {
        return (T) result;
    }

    public void setResult(Results result) {
        this.result = result;
    }
}
