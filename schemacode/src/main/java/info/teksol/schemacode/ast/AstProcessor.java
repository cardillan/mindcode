package info.teksol.schemacode.ast;

import info.teksol.schemacode.schematics.Language;

import java.util.List;

public record AstProcessor(List<AstLink> links, AstProgram program, Language language) implements AstConfiguration {
}
