package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Response {
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";

    public static final String ERR_INVALID_ARGUMENTS = "invalid_arguments";
    public static final String ERR_INVALID_PROGRAM_ID = "invalid_program_id";
    public static final String ERR_NO_PROCESSOR_ATTACHED = "no_processor_attached";
    public static final String ERR_NO_ACTIVE_MAP = "no_active_map";
    public static final String ERR_NO_PROCESSORS_FOUND = "no_processors_found";
    public static final String ERR_SCHEMATIC_IMPORT_FAILED = "schematic_import_failed";
    public static final String ERR_UNKNOWN_METHOD = "unknown_method";
    public static final String ERR_INTERNAL_ERROR = "internal_error";

    public static final String RESULT_TYPE_TEXT = "text_result";
    public static final String RESULT_TYPE_PROCESSOR_UPDATE = "processor_update_result";

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
            ),
            @JsonSubTypes.Type(
                    value = ProcessorUpdateResults.class,
                    name = RESULT_TYPE_PROCESSOR_UPDATE
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

    public Response(String status) {
        this(status, 0, RESULT_TYPE_TEXT, null);
    }

    public Response(String status, Results result) {
        this(status, 0, RESULT_TYPE_TEXT, result);
    }

    public Response(String status, String result) {
        this(status, 0, RESULT_TYPE_TEXT, new TextResult(result));
    }

    public static Response success() {
        return new Response(STATUS_SUCCESS);
    }

    public static Response success(String result) {
        return new Response(STATUS_SUCCESS, result);
    }

    public static Response success(Results result) {
        return new Response(STATUS_SUCCESS, result);
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
