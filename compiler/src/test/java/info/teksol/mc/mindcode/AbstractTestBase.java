package info.teksol.mc.mindcode;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ExpectedMessages;

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

    protected static String uriString(Path path) {
        URI uri = path.toUri().normalize();
        return (System.getProperty("os.name").toLowerCase().startsWith("win")
                ? uri.toString().replaceAll("file:/", "file:///") : uri.toString());
    }
}
