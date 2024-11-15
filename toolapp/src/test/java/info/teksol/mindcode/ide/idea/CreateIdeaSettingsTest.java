package info.teksol.mindcode.ide.idea;

import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.mimex.LVar;
import info.teksol.schemacode.grammar.SchemacodeLexer;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateIdeaSettingsTest {
    private static final String TEMPLATES = "src/test/resources/ide/idea";
    private static final String SETTINGS = "../support/idea";

    private final List<String> mindcode = getKeywords(MindcodeLexer.VOCABULARY);
    private final List<String> schemacode = getKeywords(SchemacodeLexer.VOCABULARY);
    private final List<String> combined = Stream.concat(mindcode.stream(), schemacode.stream())
            .distinct().sorted().toList();
    private final List<String> builtins = LVar.allVars().stream().map(LVar::name).distinct().sorted().toList();

    private final String mindcodeKeywords = String.join(";", mindcode);
    private final String schemacodeKeywords = String.join(";", schemacode);
    private final String combinedKeywords = String.join(";", combined);
    private final String builtinsKeywords = String.join(";", builtins);

    private List<String> getKeywords(Vocabulary vocabulary) {
        return IntStream.range(0, vocabulary.getMaxTokenType())
                .mapToObj(vocabulary::getLiteralName)
                .filter(l -> l != null && l.matches("'#?\\w+'"))
                .map(l -> l.substring(1, l.length() - 1))
                .filter(k -> !k.equals("elif") && !k.equals("elseif"))
                .toList();
    }

    @Test
    void createIdeaSettings() throws IOException {
        createZipFile("settings-filetypes.zip",
                List.of(
                        loadFile("IntelliJ IDEA Global Settings"),
                        loadFile("installed.txt"),
                        loadFile("filetypes/Mindcode.xml"),
                        loadFile("filetypes/Schema Definition File.xml")
                )
        );

        createZipFile("settings-tools-linux.zip",
                List.of(
                        loadFile("IntelliJ IDEA Global Settings"),
                        loadFile("installed.txt"),
                        loadFile("tools-linux/External Tools.xml", "tools/External Tools.xml")
                )
        );

        createZipFile("settings-tools-windows.zip",
                List.of(
                        loadFile("IntelliJ IDEA Global Settings"),
                        loadFile("installed.txt"),
                        loadFile("tools-windows/External Tools.xml", "tools/External Tools.xml")
                )
        );
    }

    private void createZipFile(String fileName, List<FileEntry> entries) throws IOException {
        File zipFile = new File(SETTINGS, fileName);
        try (ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (FileEntry entry : entries) {
                addFile(zipOutStream, entry.fileName, entry.contents);
            }
        }
    }

    private void addFile(ZipOutputStream zipOutStream, String fileName, String contents) throws IOException {
        zipOutStream.putNextEntry(new ZipEntry(fileName));
        byte[] bytes = replacePatterns(contents).getBytes(StandardCharsets.UTF_8);
        zipOutStream.write(bytes);
    }

    private FileEntry loadFile(String fileName) throws IOException {
        String contents = Files.readString(Path.of(TEMPLATES, fileName));
        return new FileEntry(fileName, contents);
    }

    private FileEntry loadFile(String dirFileName, String zipFileName) throws IOException {
        String contents = Files.readString(Path.of(TEMPLATES, dirFileName));
        return new FileEntry(zipFileName, contents);
    }

    private String replacePatterns(String contents) {
        return contents
                .replace("${mindcode-keywords}", mindcodeKeywords)
                .replace("${schemacode-keywords}", schemacodeKeywords)
                .replace("${combined-keywords}", combinedKeywords)
                .replace("${mindustry-builtins}", builtinsKeywords);
    }

    private record FileEntry(String fileName, String contents) {
    }
}
