package io.github.cardillan.schemacode.ast;

import io.github.cardillan.schemacode.config.Configuration;

public interface AstContentsReference extends AstConfiguration {

    String getConfigurationText();

    Configuration getConfiguration();
}
