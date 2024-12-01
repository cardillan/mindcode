package info.teksol.schemacode.ast;

import info.teksol.schemacode.config.Configuration;

public interface AstContentsReference extends AstConfiguration {

    String getConfigurationText();

    Configuration getConfiguration();
}
