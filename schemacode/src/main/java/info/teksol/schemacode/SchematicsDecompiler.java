package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schema.Decompiler;
import info.teksol.schemacode.schema.Schematic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class SchematicsDecompiler {

    public static CompilerOutput<String> decompile(String encodedSchematics) {
        if (encodedSchematics.isBlank()) {
            return new CompilerOutput<>("", List.of());
        }

        byte[] binary = Base64.getDecoder().decode(encodedSchematics);

        try (InputStream is = new ByteArrayInputStream(binary)) {
            Schematic schematic = SchematicsIO.read(is);
            Decompiler decompiler = new Decompiler(schematic);
            decompiler.setRelativePositions(false);
            decompiler.setRelativeConnections(true);
            decompiler.setRelativeLinks(true);
            String schemaDefinition = decompiler.buildCode();
            return new CompilerOutput<>(schemaDefinition, List.of());
        } catch (IOException e) {
            return new CompilerOutput<>("", List.of(SchemacodeMessage.error(e.toString())));
        }
    }
}
