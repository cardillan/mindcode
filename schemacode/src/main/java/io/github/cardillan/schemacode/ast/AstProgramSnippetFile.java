package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.InputFile;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.messages.SourcePositionTranslator;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NullMarked
public record AstProgramSnippetFile(SourcePosition sourcePosition, AstText fileName) implements AstProgramSnippet {

    public AstProgramSnippetFile(AstText fileName) {
        this(SourcePosition.EMPTY, fileName);
    }

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
                builder.getInputFiles().registerFile(path, source, SourcePositionTranslator.EMPTY);
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
