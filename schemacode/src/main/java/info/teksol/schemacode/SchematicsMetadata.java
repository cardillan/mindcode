package info.teksol.schemacode;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;

public class SchematicsMetadata {
    // Always use the latest metadata here - certainly for loading schematics.
    // We might want target a specific version when creating schematics one day, in which case it would be
    // necessary to use the correct metadata version when writing the schematics.
    public static final MindustryMetadata metadata = MindustryMetadata.getLatest();
}
