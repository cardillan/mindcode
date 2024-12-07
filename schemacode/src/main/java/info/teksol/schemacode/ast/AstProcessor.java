package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.Language;

import java.util.List;

public record AstProcessor(SourcePosition sourcePosition, List<AstLink> links, AstProgram program, Language language) implements AstConfiguration {
}
