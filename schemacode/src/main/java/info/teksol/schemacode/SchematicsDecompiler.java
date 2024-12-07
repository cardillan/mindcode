package info.teksol.schemacode;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.Decompiler;
import info.teksol.schemacode.schematics.Schematic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class SchematicsDecompiler {

    public static CompilerOutput<String> decompile(String encodedSchematics) {
        if (encodedSchematics.isBlank()) {
            return new CompilerOutput<>("", List.of());
        }

        final byte[] binary;
        try {
            binary = Base64.getDecoder().decode(encodedSchematics.trim());
        } catch (IllegalArgumentException e) {
            return new CompilerOutput<>("", List.of(ToolMessage.error("Error decoding schematics string: " + e.getMessage())));
        }

        try (InputStream is = new ByteArrayInputStream(binary)) {
            Schematic schematic = SchematicsIO.read(is);
            Decompiler decompiler = new Decompiler(schematic);
            decompiler.setRelativePositions(false);
            decompiler.setRelativeConnections(true);
            decompiler.setRelativeLinks(true);
            String schemaDefinition = decompiler.buildCode();
            return new CompilerOutput<>(schemaDefinition, List.of());
        } catch (Exception e) {
            return new CompilerOutput<>("", List.of(ToolMessage.error(e.toString())));
        }
    }
}
