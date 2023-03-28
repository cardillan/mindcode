package info.teksol.mindcode.processor;

/**
 * Mindustry processor operation.
 */
public interface Operation {
    void execute(Variable result, Variable a, Variable b);
}
