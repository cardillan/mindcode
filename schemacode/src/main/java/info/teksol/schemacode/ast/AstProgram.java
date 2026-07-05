package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record AstProgram(SourcePosition sourcePosition, List<AstProgramSnippet> snippets) implements AstSchemaItem {
    public static AstProgram EMPTY = new AstProgram(SourcePosition.EMPTY, List.of());

    public AstProgram(List<AstProgramSnippet> snippets) {
        this(SourcePosition.EMPTY, snippets);
    }

    public AstProgram(AstProgramSnippet... snippets) {
        this(List.of(snippets));
    }

    public String getProgramText(SchematicsBuilder builder, String prologueName) {
        return withPrologue(builder, prologueName).stream()
                .map(s -> s.getProgramText(builder))
                .collect(Collectors.joining("\n"));
    }

    public String getProgramId(SchematicsBuilder builder, String prologueName) {
        return withPrologue(builder, prologueName).stream()
                .map(s -> s.getProgramId(builder))
                .collect(Collectors.joining(", "));
    }

    public SourcePositionTranslator createPositionTranslator(SchematicsBuilder builder, String prologueName) {
        return MultipartPositionTranslator.createTranslator(builder, withPrologue(builder, prologueName));
    }

    @Override
    public AstProgram withEmptyPosition() {
        return new AstProgram(SourcePosition.EMPTY, erasePositions(snippets));
    }

    private List<AstProgramSnippet> withPrologue(SchematicsBuilder builder, String prologueName) {
        AstProgramSnippet prologue = builder.getAttribute(prologueName, AstProgramSnippet.class);
        if (prologue == null) return snippets;

        ArrayList<AstProgramSnippet> result = new ArrayList<>(snippets.size() + 1);
        result.add(prologue);
        result.addAll(snippets);
        return result;
    }
}
