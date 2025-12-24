package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public record BlockType(
        String contentName,
        String name,
        int id,
        int logicId,
        String visibility,
        String implementation,
        boolean legacy,
        int size,
        boolean hasPower,
        boolean configurable,
        String category,
        float range,
        int maxNodes,
        boolean rotate,
        List<String> unitPlans
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.BLOCK;
    }

    public String getBaseLinkName() {
        String name = this.name.substring(1);
        if (name.contains("-")) {
            String[] s = name.split("-");
            //filter out 'large' and numbers at the end of block names
            if (s.length >= 2 && (s[s.length - 1].equals("large") || s[s.length - 1].matches("\\d+"))) {
                return s[s.length - 2];
            } else {
                return s[s.length - 1];
            }
        }
        return name;
    }

    public static Set<String> getBaseLinkNames(MindustryMetadata metadata) {
        return metadata.getBlockMap().values().stream()
                .filter(b -> !b.visibility.equals("hidden"))
                .map(BlockType::getBaseLinkName)
                .collect(Collectors.toSet());
    }
}
