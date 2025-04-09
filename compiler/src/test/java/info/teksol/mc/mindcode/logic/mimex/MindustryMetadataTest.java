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

        // Crate a list of metadata
        String last = ProcessorVersion.V6.mimexVersion;
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

        Assertions.assertFalse(stableIds.isEmpty(), "No stable IDs found");

        // If there were multiple items with the same name, we could get different IDs for them
        // When this test fails, add filter for non-duplicate items
        Map<String, Long> frequencies = stableIds.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        Assertions.assertEquals(stableIds.size(), frequencies.size(), "Duplicate stable IDs found");

        try {
            Path file = Path.of(MIMEX_BASE_DIRECTORY, "stable-builtins.txt");
            Files.writeString(file, String.join("\n", stableIds));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void findStableIds(List<String> stableIds, List<MindustryMetadata> metadata,
            Function<MindustryMetadata, Map<String, ? extends MindustryContent>> contentExtractor) {

        Map<String, MindustryContent> stableList = createList(contentExtractor.apply(metadata.getFirst()));

        for (int i = 1; i < metadata.size(); i++) {
            Map<String, MindustryContent> currentList = createList(contentExtractor.apply(metadata.get(i)));
            stableList.keySet().retainAll(currentList.keySet());
            for (MindustryContent c : currentList.values()) {
                MindustryContent d = stableList.get(c.name());
                if (d != null && d.logicId() != c.logicId()) {
                    stableList.remove(c.name());
                }
            }
        }

        stableList.values().stream().sorted().map(MindustryContent::name).forEach(stableIds::add);
    }

    private Map<String, MindustryContent> createList(Map<String, ? extends MindustryContent> content) {
        return content.values().stream()
                .filter(c -> c.logicId() >= 0)
                .collect(Collectors.toMap(MindustryContent::name, Function.identity()));
    }
}