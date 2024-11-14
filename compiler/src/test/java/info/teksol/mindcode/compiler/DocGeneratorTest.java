package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.FunctionParameter;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Order(5)
public class DocGeneratorTest extends AbstractGeneratorTest {
    private static final String PREFIX = "#generate";
    private static final String SOURCE_FILE = "src/test/resources/library/doc/SYSTEM-LIBRARY_template.markdown";
    private static final String TARGET_FILE = "../doc/syntax/SYSTEM-LIBRARY.markdown";
    private static final String LIBRARY_DIRECTORY = "src/main/resources/library";

    private PrintWriter writer;

    @Test
    void createCommandLineHelp() throws IOException {
        Path path = Path.of(SOURCE_FILE);

        try (final PrintWriter w = new PrintWriter(TARGET_FILE, StandardCharsets.UTF_8); Stream<String> lineStream = Files.lines(path)) {
            writer = w;
            lineStream.forEach(this::processLine);
            writer = null;
        }
    }

    private void processLine(String line)  {
        if (line.startsWith(PREFIX)) {
            processAllLibraries();
        } else {
            writer.println(line);
        }
    }

    private void processAllLibraries() {
        try (Stream<Path> stream = Files.list(Paths.get(LIBRARY_DIRECTORY))) {
            List<Path> files = stream
                    .filter(f -> f.toString().endsWith(".mnd"))
                    .filter(f -> !f.toString().contains("unittests.mnd"))
                    .toList();
            assertFalse(files.isEmpty(), "Expected to find at least one script in " + LIBRARY_DIRECTORY + "; found none");
            for (Path file : files) {
                processLibrary(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<FunctionDeclaration> functions = new ArrayList<>();

    private void processLibrary(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        String libraryName = fileName.substring(0, fileName.lastIndexOf("."));
        String code = Files.readString(file);

        // Parse and process the module
        functions.clear();
        AstNode node = translateToAst(ExpectedMessages.create().addLevelsUpTo(MessageLevel.WARNING).ignored(),
                code, new ArrayList<>());
        processNode(node);

        writer.println();
        writer.println("## " + firstUpperCase(libraryName) +  " library");
        writer.println();
        processModuleDoc(code);

        functions.stream().filter(function -> !function.getName().startsWith("_")).forEach(this::processFunction);
    }

    private String firstUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void processModuleDoc(String code) {
        for (String line : code.lines().toList()) {
            if (!line.startsWith("//")) {
                return;
            } else if (line.startsWith("//*")) {
                writer.println(line.substring(line.startsWith("//* ") ? 4 : 3).trim());
            }
        }
    }

    private void processNode(AstNode node) {
        if (node instanceof FunctionDeclaration fd) {
            functions.add(fd);
        }
        node.getChildren().forEach(this::processNode);
    }

    private void processFunction(FunctionDeclaration declaration) {
        writer.println();
        writer.println("### " + declaration.getName());
        writer.println();
        writeFunction(declaration);

        if (declaration.getCodeDoc() != null) {
            writer.println();
            writeCodeDoc(declaration.getCodeDoc());
        }
    }

    private void writeFunction(FunctionDeclaration declaration) {
        writer.print("**Definition:** `");
        if (declaration.isInline()) writer.print("inline ");
        if (declaration.isNoinline()) writer.print("noinline ");
        writer.print(declaration.isProcedure() ? "void " : "def ");
        writer.print(declaration.getName());
        writer.print("(");
        boolean first = true;
        for (FunctionParameter parameter : declaration.getParams()) {
            if (first) {
                first = false;
            } else {
                writer.print(", ");
            }
            if (parameter.hasInModifier()) writer.print("in ");
            if (parameter.hasOutModifier()) writer.print("out ");
            writer.print(parameter.getName().substring(parameter.getName().charAt(0) == '_' ? 1 : 0));
            if (parameter.isVarArgs()) writer.print("...");
        }
        writer.println(")`");
    }

    private void writeCodeDoc(String codeDoc) {
        if (!codeDoc.contains("\n")) {
            writer.println(codeDoc.trim());
        } else {
            codeDoc.lines().forEach(this::writeLine);
        }
    }

    private void writeLine(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("*")) {
            writer.println(trimmed.substring(trimmed.startsWith("* ") ? 2 : 1));
        } else if (!trimmed.isEmpty()) {
            writer.println(line);
        }
    }
}
