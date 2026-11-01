package io.github.cardillan.mindcode.exttest;

import org.jspecify.annotations.NullMarked;

import java.io.PrintWriter;

@NullMarked
public interface ExecutionFramework {
    void process(PrintWriter writer);
}
