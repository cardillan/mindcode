package info.teksol.mindcode.cmdline.mlogwatcher;

public class TextResult implements Results {
    private String text;

    public TextResult() {
    }

    public TextResult(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
