package info.teksol.mindcode.logic;

public enum ProcessorEdition {
    STANDARD_PROCESSOR      ("Micro Processor, Logic Processor and Hyper Processor"),
    WORLD_PROCESSOR         ("World processor"),
    ;

    private final String title;

    ProcessorEdition(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static final ProcessorEdition S = ProcessorEdition.STANDARD_PROCESSOR;
    public static final ProcessorEdition W = ProcessorEdition.WORLD_PROCESSOR;
}
