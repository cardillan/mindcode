package info.teksol.schemacode.ast;

import info.teksol.schemacode.schematics.SchematicsBuilder;

public interface AstText extends AstConfiguration {

    String getText(SchematicsBuilder builder);
}