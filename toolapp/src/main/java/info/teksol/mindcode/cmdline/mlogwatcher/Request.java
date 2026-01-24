package info.teksol.mindcode.cmdline.mlogwatcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Request {

    private String method;

    @JsonProperty("method_version")
    private int methodVersion = 1;

    @JsonProperty("invocation_id")
    private int invocationId;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "method",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(
                    value = UpdateSelectedProcessorParams.class,
                    name = "update_selected_processor"
            )
    })
    private Params params;

    // getters & setters

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getMethodVersion() {
        return methodVersion;
    }

    public void setMethodVersion(int methodVersion) {
        this.methodVersion = methodVersion;
    }

    public int getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(int invocationId) {
        this.invocationId = invocationId;
    }

    @SuppressWarnings("unchecked")
    public <T extends Params> T getParams() {
        return (T) params;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
