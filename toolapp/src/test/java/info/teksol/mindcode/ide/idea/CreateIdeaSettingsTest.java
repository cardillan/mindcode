package info.teksol.mindcode.ide.idea;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.FunctionMapping;
import info.teksol.mc.mindcode.logic.opcodes.MindustryOpcodeVariants;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.schemacode.grammar.SchemacodeLexer;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateIdeaSettingsTest {
    private static final String TEMPLATES = "src/test/resources/ide/idea";
    private static final String SETTINGS = "../support/idea";

    private final List<String> opcodes = Stream.of(Opcode.values()).filter(o -> !o.isVirtual()).map(Opcode::getOpcode).toList();
    private final List<String> mlog = getMlogKeywords();
    private final List<String> mindcode = getKeywords(MindcodeLexer.VOCABULARY);
    private final List<String> schemacode = getKeywords(SchemacodeLexer.VOCABULARY);
    private final List<String> combined = Stream.concat(mindcode.stream(), schemacode.stream())
            .distinct().sorted().toList();
    private final List<String> builtins = MindustryMetadata.getLatest().getAllLVars().stream().map(LVar::name).distinct().sorted().toList();

    private final String mlogOpcodes = String.join(";", opcodes);
    private final String pureMlogKeywords = String.join(";", mlog);
    private final String mlogKeywords = mlog.stream().map(":"::concat).collect(Collectors.joining(";"));
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

    private static List<String> getMlogKeywords() {
        return MindustryOpcodeVariants.getAllOpcodeVariants().stream()
                .filter(o -> o.functionMapping() != FunctionMapping.NONE)
                .flatMap(opcodeVariant -> opcodeVariant.namedParameters().stream())
                .filter(p -> (p.type().isKeyword() || p.type().isSelector()) && !p.type().isFunctionName())
                .flatMap(p -> p.type().getAllowedValues().isEmpty() ? Stream.of(p.name())
                        : p.type().getAllowedValues().stream().flatMap(v -> v.values.stream()))
                .sorted()
                .distinct()
                .toList();
    }

    @Test
    void createIdeaSettings() throws IOException {
        createZipFile("settings-filetypes.zip",
                List.of(
                        loadFile("IntelliJ IDEA Global Settings"),
                        loadFile("installed.txt"),
                        loadFile("filetypes/Mindcode.xml"),
                        loadFile("filetypes/mlog.xml"),
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
        boolean modified = false;

        // Do not modify the files if the content didn't change
        if (zipFile.exists()) {
            try (FileSystem zipfs = FileSystems.newFileSystem(zipFile.toPath())) {
                for (FileEntry entry : entries) {
                    Path pathInZipfile = zipfs.getPath("/" + entry.fileName);
                    if (!Files.exists(pathInZipfile) || !Files.readString(pathInZipfile).equals(entry.contents)) {
                        modified = true;
                        break;
                    }
                }
            }
        } else {
            modified = true;
        }

        if (modified) {
            try (ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for (FileEntry entry : entries) {
                    addFile(zipOutStream, entry.fileName, entry.contents);
                }
            }
        }
    }

    private void addFile(ZipOutputStream zipOutStream, String fileName, String contents) throws IOException {
        zipOutStream.putNextEntry(new ZipEntry(fileName));
        byte[] bytes = contents.getBytes(StandardCharsets.UTF_8);
        zipOutStream.write(bytes);
    }

    private FileEntry loadFile(String fileName) throws IOException {
        return loadFile(fileName, fileName);
    }

    private FileEntry loadFile(String dirFileName, String zipFileName) throws IOException {
        String contents = Files.readString(Path.of(TEMPLATES, dirFileName));
        return new FileEntry(zipFileName, replacePatterns(contents));
    }

    private String replacePatterns(String contents) {
        return contents
                .replace("${mlog-opcodes}", mlogOpcodes)
                .replace("${pure-mlog-keywords}", pureMlogKeywords)
                .replace("${mlog-keywords}", mlogKeywords)
                .replace("${mindcode-keywords}", mindcodeKeywords)
                .replace("${schemacode-keywords}", schemacodeKeywords)
                .replace("${combined-keywords}", combinedKeywords)
                .replace("${mindustry-builtins}", builtinsKeywords);
    }

    private record FileEntry(String fileName, String contents) {
    }
}
