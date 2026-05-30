package info.teksol.mindcode.cmdline.mlogwatcher.api;

public class ProcessorExtractResults implements Results {

    private String code;

    public ProcessorExtractResults() {
    }

    public ProcessorExtractResults(String code) {
        setCode(code);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
