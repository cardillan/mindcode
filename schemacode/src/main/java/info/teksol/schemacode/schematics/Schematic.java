package info.teksol.schemacode.schematics;

import java.util.List;

public record Schematic(String name, String description, List<String> labels, int width, int height, List<Block> blocks) {
}
