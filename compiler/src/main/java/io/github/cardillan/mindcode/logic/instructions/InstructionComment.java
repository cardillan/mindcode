package io.github.cardillan.mindcode.logic.instructions;

public record InstructionComment(String whiteSpace, String comment) {
    public static final InstructionComment EMPTY = new InstructionComment("", "");

    public String fullComment() {
        return whiteSpace + comment;
    }
}
