package info.teksol.mindcode.mimex;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record BlockType(
        String name,
        int id,
        String visibility,
        String implementation,
        int size,
        boolean hasPower,
        boolean configurable,
        String category,
        float range,
        int maxNodes,
        boolean rotate,
        List<String> unitPlans
) implements MindustryContent {

    public static int count() {
        return MindustryContents.BLOCK_MAP.size();
    }

    public static BlockType forName(String name) {
        return MindustryContents.BLOCK_MAP.get(name);
    }

    public static BlockType forId(int id) {
        return MindustryContents.BLOCK_ID_MAP.get(id);
    }

    public static boolean isNameValid(String name) {
        return MindustryContents.BLOCK_MAP.containsKey(name);
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

    public static Set<String> getBaseLinkNames() {
        return MindustryContents.BLOCK_MAP.values().stream()
                .filter(b -> !b.visibility.equals("hidden"))
                .map(BlockType::getBaseLinkName)
                .collect(Collectors.toSet());
    }
}
