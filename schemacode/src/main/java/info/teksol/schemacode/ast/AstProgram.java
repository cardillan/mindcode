package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record AstProgram(SourcePosition sourcePosition, List<AstProgramSnippet> snippets) implements AstSchemaItem {
    public static AstProgram EMPTY = new AstProgram(SourcePosition.EMPTY, List.of());

    public AstProgram(SourcePosition sourcePosition, AstProgramSnippet... snippets) {
        this(sourcePosition, List.of(snippets));
    }

    public String getProgramText(SchematicsBuilder builder) {
        return snippets.stream()
                .map(s -> s.getProgramText(builder))
                .collect(Collectors.joining("\n"));
    }

    public String getProgramId(SchematicsBuilder builder) {
        return snippets.stream()
                .map(s -> s.getProgramId(builder))
                .collect(Collectors.joining(", "));
    }

    public SourcePositionTranslator createPositionTranslator(SchematicsBuilder builder) {
        return MultipartPositionTranslator.createTranslator(builder, snippets);
    }

    @Override
    public AstProgram withEmptyPosition() {
        return new AstProgram(SourcePosition.EMPTY, erasePositions(snippets));
    }
}
