package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
class MindustryMetadataTest {
    public static final String MIMEX_BASE_DIRECTORY = "src/main/resources/mimex/";

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

        for (int i = 1; i < metadata.size(); i++) {
            Map<String, T> currentList = createList(contentExtractor.apply(metadata.get(i)));
            stableList.keySet().retainAll(currentList.keySet());
            for (T c : currentList.values()) {
                T d = stableList.get(c.name());
                if (d == null) {
                    stableList.put(c.name(), c);
                } else if (d.logicId() != c.logicId()) {
                    stableList.remove(c.name());
                }
            }
        }

        stableList.values().stream().sorted().map(MindustryContent::name).forEach(stableIds::add);
    }

    private void findStableValues(List<String> stableIds, List<MindustryMetadata> metadata) {
        Map<String, LVar> stableList = createLVarList(metadata.getFirst().getLVarMap());

        for (int i = 1; i < metadata.size(); i++) {
            Map<String, LVar> currentList = createLVarList(metadata.get(i).getLVarMap());
            for (LVar c : currentList.values()) {
                LVar d = stableList.get(c.name());
                if (d == null) {
                    stableList.put(c.name(), c);
                } else if (d.numericValue() != c.numericValue()) {
                    stableList.remove(c.name());
                }
            }
        }

        stableList.values().stream().sorted().map(MindustryContent::name).forEach(stableIds::add);
    }

    private <T extends MindustryContent> Map<String, T> createList(Map<String, T> content) {
        return content.values().stream()
                .filter(c -> c.logicId() >= 0)
                .collect(Collectors.toMap(MindustryContent::name, Function.identity()));
    }

    private Map<String, LVar> createLVarList(Map<String, LVar> content) {
        return content.values().stream()
                .filter(LVar::isNumericConstant)
                .filter(c -> !c.name().endsWith("Count"))
                .collect(Collectors.toMap(MindustryContent::name, Function.identity()));
    }
}