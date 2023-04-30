package info.teksol.mindcode.processor;

public class ExecutionException extends RuntimeException {
    private final ProcessorFlag flag;

    public ExecutionException(ProcessorFlag flag, String message) {
        super(message);
        this.flag = flag;
    }

    public ProcessorFlag getFlag() {
        return flag;
    }
}
