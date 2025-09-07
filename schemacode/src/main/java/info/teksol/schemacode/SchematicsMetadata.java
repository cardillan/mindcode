package info.teksol.schemacode;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;

public class SchematicsMetadata {
    // Always use the latest metadata here - certainly for loading schematics.
    // We might want to target a specific version when creating schematics one day, in which case it would be
    // necessary to use the correct metadata version when writing the schematics.
    private static final ThreadLocal<MindustryMetadata> metadata = ThreadLocal.withInitial(MindustryMetadata::getLatest);

    public static MindustryMetadata getMetadata() {
        return metadata.get();
    }

    public static void setMetadata(MindustryMetadata newMetadata) {
        metadata.set(newMetadata);
    }
}
