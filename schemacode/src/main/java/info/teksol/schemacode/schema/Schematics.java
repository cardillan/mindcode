package info.teksol.schemacode.schema;

import java.util.List;

public record Schematics(String name, String description, List<String> labels, int width, int height, List<Block> blocks) {
}
