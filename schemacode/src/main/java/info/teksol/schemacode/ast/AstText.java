package info.teksol.schemacode.ast;

import info.teksol.schemacode.schema.SchematicsBuilder;

public interface AstText extends AstConfiguration {

    String getText(SchematicsBuilder builder);
}