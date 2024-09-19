package info.teksol.mindcode.mimex;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BlockType(
        String baseName,
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
) implements NumberedConstant {

    static final Map<String, BlockType> NAME_MAP = BlockTypeReader.createFromResource();

    static final Map<Integer, BlockType> ID_MAP = NAME_MAP.values().stream()
            .collect(Collectors.toMap(BlockType::id, block -> block));

    public static BlockType forName(String name) {
        return NAME_MAP.get(name);
    }

    public static BlockType forId(int id) {
        return ID_MAP.get(id);
    }

    public static boolean isNameValid(String name) {
        return NAME_MAP.containsKey(name);
    }

    public String getBaseLinkName() {
        if (baseName.contains("-")) {
            String[] s = baseName.split("-");
            //filter out 'large' and numbers at the end of block names
            if (s.length >= 2 && (s[s.length - 1].equals("large") || s[s.length - 1].matches("\\d+"))) {
                return s[s.length - 2];
            } else {
                return s[s.length - 1];
            }
        }
        return baseName;
    }

    public static Set<String> getBaseLinkNames() {
        return NAME_MAP.values().stream()
                .filter(b -> !b.visibility.equals("hidden"))
                .map(BlockType::getBaseLinkName)
                .collect(Collectors.toSet());
    }
}
