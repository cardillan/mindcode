package info.teksol.schemacode.schema;

import java.util.List;

public record Schematic(String name, String description, List<String> labels, int width, int height, List<Block> blocks) {
}
