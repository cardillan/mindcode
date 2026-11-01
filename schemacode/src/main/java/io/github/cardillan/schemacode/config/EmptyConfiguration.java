package io.github.cardillan.schemacode.config;

import io.github.cardillan.schemacode.mindustry.ConfigurationType;

public enum EmptyConfiguration implements Configuration {
    EMPTY;

    @Override
    public <T extends Configuration> T as(Class<T> type) {
        return ConfigurationType.createEmpty(type);
    }
}
