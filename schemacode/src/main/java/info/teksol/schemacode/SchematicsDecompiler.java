package info.teksol.schemacode;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.Decompiler;
import info.teksol.schemacode.schematics.Schematic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class SchematicsDecompiler {

    public static CompilerOutput<String> decompile(MessageConsumer messageConsumer, String encodedSchematics) {
        if (encodedSchematics.isBlank()) {
            return CompilerOutput.empty();
        }

        final byte[] binary;
        try {
            binary = Base64.getDecoder().decode(encodedSchematics.trim());
        } catch (IllegalArgumentException e) {
            messageConsumer.addMessage(ToolMessage.error("Error decoding schematics string: " + e.getMessage()));
            return CompilerOutput.empty();
        }

        try (InputStream is = new ByteArrayInputStream(binary)) {
            Schematic schematic = SchematicsIO.read("", is);
            Decompiler decompiler = new Decompiler(schematic);
            decompiler.setRelativePositions(false);
            decompiler.setRelativeConnections(true);
            decompiler.setRelativeLinks(true);
            String schemaDefinition = decompiler.buildCode();
            return new CompilerOutput<>(schemaDefinition, "");
        } catch (Exception e) {
            messageConsumer.addMessage(ToolMessage.error("Error decoding schematics: " + e.getMessage()));
            return CompilerOutput.empty();
        }
    }
}
