package io.github.cardillan.schemacode.mindustry;

import io.github.cardillan.mc.mindcode.logic.mimex.ContentType;
import io.github.cardillan.schemacode.config.Configuration;

public interface ContentConfiguration extends Configuration {
    ContentType getContentType();
    String getContentName();
    int getId();
}
