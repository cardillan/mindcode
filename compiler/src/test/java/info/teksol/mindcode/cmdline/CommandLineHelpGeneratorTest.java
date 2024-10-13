package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(99)
public class CommandLineHelpGeneratorTest {
    private static final String PREFIX = "#generate:";
    private static final String SOURCE_FILE = "src/test/resources/templates/TOOLS-CMDLINE_template.markdown";
    private static final String TARGET_FILE = "../doc/syntax/TOOLS-CMDLINE.markdown";

    private static final ArgumentParser parser = Main.createArgumentParser(Arguments.fileType(), 120);

    @Test
    void createCommandLineHelp() throws IOException {
        assertTrue(new File(TARGET_FILE).isFile());
        Path path = Path.of(SOURCE_FILE);

        try (final PrintWriter w = new PrintWriter(TARGET_FILE, StandardCharsets.UTF_8); Stream<String> lineStream = Files.lines(path)) {
            lineStream.forEach(l -> processLine(w, l));
        }
    }

    private void processLine(PrintWriter writer, String line) {
        if (line.startsWith(PREFIX)) {
            String name = line.substring(PREFIX.length());
            if (name.equals("GENERAL")) {
                parser.printHelp(writer);
            } else {
                Action action = Action.valueOf(name);
                Main.ACTION_PARSERS.get(action).printHelp(writer);
            }
        } else {
            writer.println(line);
        }
    }
}
