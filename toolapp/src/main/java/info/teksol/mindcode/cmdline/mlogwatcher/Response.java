package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";

    private String status;

    @JsonProperty("invocation_id")
    private int invocationId;

    private String result;

    public Response() {
    }

    public Response(String status, int invocationId, String result) {
        this.status = status;
        this.invocationId = invocationId;
        this.result = result;
    }

    public Response(String status, String result) {
        this.status = status;
        this.result = result;
    }

    public static Response success(String result) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
