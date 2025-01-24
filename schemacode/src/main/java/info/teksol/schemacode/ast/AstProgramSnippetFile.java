package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NullMarked
public record AstProgramSnippetFile(SourcePosition sourcePosition, AstText fileName) implements AstProgramSnippet {

    @Override
    public String getProgramId(SchematicsBuilder builder) {
        return "file " + fileName.getText(builder);
    }

    @Override
    public String getProgramText(SchematicsBuilder builder) {
        if (builder.externalFilesAllowed()) {
            Path path = builder.getBasePath().resolve(fileName.getText(builder));
            try {
                String source = Files.readString(path);
                builder.getInputFiles().registerFile(path, source);
                return source;
            } catch (IOException ex) {
                builder.error(this, "Error reading file '%s'.", path.toString());
                return "";
            }
        } else {
            builder.error("Loading code from external file not supported in web application.");
            return "";
        }
    }

    @Override
    public SourcePosition getSourcePosition(SchematicsBuilder builder) {
        if (builder.externalFilesAllowed()) {
            Path path = builder.getBasePath().resolve(fileName.getText(builder));
            InputFile inputFile = builder.getInputFiles().getInputFile(path);
            return new SourcePosition(inputFile, 1, 1);
        } else {
            builder.error("Loading code from external file not supported in web application.");
            return SourcePosition.EMPTY;
        }
    }

    @Override
    public AstProgramSnippetFile withEmptyPosition() {
        return new AstProgramSnippetFile(SourcePosition.EMPTY, erasePosition(fileName));
    }
}
