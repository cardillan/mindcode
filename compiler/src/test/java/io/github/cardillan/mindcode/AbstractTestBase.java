package io.github.cardillan.mindcode;

import io.github.cardillan.mindcode.messages.AbstractMessageEmitter;
import io.github.cardillan.mindcode.messages.ExpectedMessages;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public abstract class AbstractTestBase extends AbstractMessageEmitter {

    public AbstractTestBase() {
        super(ExpectedMessages.throwOnMessage());
    }

    protected static String uriString(File file, int line) {
        URI uri = file.toURI().normalize();
        return (System.getProperty("os.name").toLowerCase().startsWith("win")
                ? uri.toString().replaceAll("file:/", "file:///") : uri.toString())
                + ":" + line;
    }

    protected static String uriString(Path path, int line) {
        URI uri = path.toUri().normalize();
        return (System.getProperty("os.name").toLowerCase().startsWith("win")
                ? uri.toString().replaceAll("file:/", "file:///") : uri.toString())
                + ":" + line;
    }
}
