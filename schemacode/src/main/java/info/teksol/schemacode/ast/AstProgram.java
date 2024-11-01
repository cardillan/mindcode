package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPositionTranslator;
import info.teksol.schemacode.schematics.SchematicsBuilder;

import java.util.List;
import java.util.stream.Collectors;

public record AstProgram(List<AstProgramSnippet> snippets) implements AstSchemaItem {

    public AstProgram(AstProgramSnippet... snippets) {
        this(List.of(snippets));
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

    public InputPositionTranslator createPositionTranslator(SchematicsBuilder builder) {
        return MultipartPositionTranslator.createTranslator(builder, snippets);
    }
}
