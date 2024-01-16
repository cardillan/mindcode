package info.teksol.mindcode.mimex;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BlockType(
        String name,
        String implementation,
        int size,
        boolean hasPower,
        boolean configurable,
        String category,
        float range,
        int maxNodes,
        boolean rotate,
        List<String> unitPlans
        ) {

    private static final Map<String, BlockType> NAME_MAP = BlockTypeReader.createFromResource();

    public static BlockType forName(String name) {
        return NAME_MAP.get(name);
    }

    public static boolean isNameValid(String name) {
        return NAME_MAP.containsKey(name);
    }

    public String getBaseLinkName() {
        String strippedTypeName = name.substring(1);
        if (strippedTypeName.contains("-")) {
            String[] s = strippedTypeName.split("-");
            //filter out 'large' and numbers at the end of block names
            if (s.length >= 2 && (s[s.length - 1].equals("large") || s[s.length - 1].matches("\\d+"))) {
                return s[s.length - 2];
            } else {
                return s[s.length - 1];
            }
        }
        return strippedTypeName;
    }

    public static Set<String> getBaseLinkNames() {
        return NAME_MAP.values().stream()
                .map(BlockType::getBaseLinkName)
                .collect(Collectors.toSet());
    }
}
