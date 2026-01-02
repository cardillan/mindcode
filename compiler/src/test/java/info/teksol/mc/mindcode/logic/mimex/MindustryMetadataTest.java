package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
class MindustryMetadataTest {
    public static final String MIMEX_BASE_DIRECTORY = "src/main/resources" + MindustryMetadata.MIMEX_DATA;

    @Test
    void createStableIdList() {
        List<MindustryMetadata> metadataList = new ArrayList<>();

        // Create a list of metadata
        String last = "";
        for (ProcessorVersion version : ProcessorVersion.values()) {
            if (!version.mimexVersion.equals(last)) {
                last = version.mimexVersion;
                metadataList.add(MindustryMetadata.forVersion(version));
            }
        }

        List<String> stableIds = new ArrayList<>();
        findStableIds(stableIds, metadataList, MindustryMetadata::getBlockMap);
        findStableIds(stableIds, metadataList, MindustryMetadata::getUnitMap);
        findStableIds(stableIds, metadataList, MindustryMetadata::getItemMap);
        findStableIds(stableIds, metadataList, MindustryMetadata::getLiquidMap);
        findStableIds(stableIds, metadataList, MindustryMetadata::getTeamMap);
        findStableValues(stableIds, metadataList);

        Assertions.assertFalse(stableIds.isEmpty(), "No stable IDs found");

        // If there were multiple items with the same name, we could get different IDs for them
        // When this test fails, add filter for non-duplicate items
        Map<String, Long> frequencies = stableIds.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        Assertions.assertEquals(stableIds.size(), frequencies.size(), "Duplicate stable IDs found");

        try {
            Path file = Path.of(MIMEX_BASE_DIRECTORY, "stable-builtins.txt");
            Files.writeString(file, String.join(System.lineSeparator(), stableIds));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends MindustryContent> void findStableIds(List<String> stableIds, List<MindustryMetadata> metadata,
            Function<MindustryMetadata, Map<String, T>> contentExtractor) {

        Map<String, T> stableList = createList(contentExtractor.apply(metadata.getFirst()));
        Set<String> unstableList = new HashSet<>();

        for (int i = 1; i < metadata.size(); i++) {
            Map<String, T> currentList = createList(contentExtractor.apply(metadata.get(i)));
            stableList.keySet().stream().filter(k -> !currentList.containsKey(k)).forEach(unstableList::add);
            stableList.keySet().retainAll(currentList.keySet());
            for (T c : currentList.values()) {
                T d = stableList.get(c.name());
                if (d == null) {
                    stableList.put(c.name(), c);
                } else if (d.logicId() != c.logicId()) {
                    unstableList.add(c.name());
                }
            }
        }

        stableList.keySet().removeAll(unstableList);
        stableList.values().stream().sorted().map(MindustryContent::name).forEach(stableIds::add);
    }

    private void findStableValues(List<String> stableIds, List<MindustryMetadata> metadata) {
        Map<String, LVariable> stableList = createLVarList(metadata.getFirst().getLVarMap());
        Set<String> unstableList = new HashSet<>();

        for (int i = 1; i < metadata.size(); i++) {
            Map<String, LVariable> currentList = createLVarList(metadata.get(i).getLVarMap());
            stableList.keySet().stream().filter(k -> !currentList.containsKey(k)).forEach(unstableList::add);
            for (LVariable c : currentList.values()) {
                LVariable d = stableList.get(c.name());
                if (d == null) {
                    stableList.put(c.name(), c);
                } else if (d.numericValue() != c.numericValue()) {
                    unstableList.add(c.name());
                }
            }
        }

        stableList.keySet().removeAll(unstableList);
        stableList.values().stream().sorted().map(MindustryContent::name).forEach(stableIds::add);
    }

    private <T extends MindustryContent> Map<String, T> createList(Map<String, T> content) {
        return content.values().stream()
                .filter(c -> c.logicId() >= 0)
                .collect(Collectors.toMap(MindustryContent::name, Function.identity()));
    }

    private static final Set<String> UNSTABLE_COUNTS = Set.of("@itemCount", "@liquidCount", "@unitCount", "@blockCount");

    private Map<String, LVariable> createLVarList(Map<String, LVariable> content) {
        return content.values().stream()
                .filter(LVariable::isNumericConstant)
                .filter(c -> c.name().startsWith("@"))
                .filter(c -> !UNSTABLE_COUNTS.contains(c.name()))
                .collect(Collectors.toMap(MindustryContent::name, Function.identity()));
    }
}
