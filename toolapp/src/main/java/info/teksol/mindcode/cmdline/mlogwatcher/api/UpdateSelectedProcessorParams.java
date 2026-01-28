package info.teksol.mindcode.cmdline.mlogwatcher.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpdateSelectedProcessorParams implements Params {

    private String code;

    public String getCode() {
        byte[] bytes = Base64.getDecoder().decode(code);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void setCode(String code) {
        byte[] bytes = code.getBytes(StandardCharsets.UTF_8);
        this.code = Base64.getEncoder().encodeToString(bytes);
    }
}
