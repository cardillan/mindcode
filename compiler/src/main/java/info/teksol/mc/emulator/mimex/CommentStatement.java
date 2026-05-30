package info.teksol.mc.emulator.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CommentStatement extends LStatement {
    // Comment, including #
    private final String comment;

    public CommentStatement(String comment) {
        super(false, "", List.of(), List.of());
        this.comment = comment;
    }

    @Override
    public boolean virtual() {
        return true;
    }

    @Override
    public String toString() {
        return comment;
    }
}
