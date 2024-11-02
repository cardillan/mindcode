package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.Language;

import java.util.List;

public record AstProcessor(InputPosition inputPosition, List<AstLink> links, AstProgram program, Language language) implements AstConfiguration {
}
