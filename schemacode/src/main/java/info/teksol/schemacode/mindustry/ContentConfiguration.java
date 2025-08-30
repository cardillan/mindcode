package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.schemacode.config.Configuration;

public interface ContentConfiguration extends Configuration {
    ContentType getContentType();
    String getContentName();
    int getId();
}
