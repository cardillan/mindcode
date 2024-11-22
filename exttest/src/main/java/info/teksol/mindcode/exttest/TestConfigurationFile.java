package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.v3.InputFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestConfigurationFile {
    private final Properties properties;

    private TestConfigurationFile(String propertiesFileName) throws IOException {
        FileInputStream configFileReader = new FileInputStream(propertiesFileName);
        properties = new Properties();
        properties.load(configFileReader);
    }

    public static TestConfiguration loadConfiguration(String propertiesFileName) throws IOException {
        TestConfigurationFile config = new TestConfigurationFile(propertiesFileName);
        InputFiles inputFiles = InputFiles.create();
        // TODO: We pretend the source file is a library to have it parsed just once and cached.
        //       Implement a proper caching mechanism for these tests.
        inputFiles.registerLibraryFile(Path.of("__tmp"), Files.readString(Path.of(config.getSourceFileName())));
        return new TestConfiguration(
                config.getSourceFileName(),
                inputFiles,
                config.getParallelism(),
                config.getOptimizationLevels(),
                config.getSampleCount());
    }

    private String requireProperty(String propertyName)  {
        String value = properties.getProperty(propertyName);
        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + propertyName);
        }
        return value;
    }

    private String getSourceFileName() {
        return requireProperty("source");
    }

    private int getParallelism() {
        return Integer.parseInt(requireProperty("parallelism"));
    }

    private int getSampleCount() {
        String samples = properties.getProperty("samples");
        if (samples == null) {
            return -1;
        } else {
            return Integer.parseInt(samples);
        }
    }

    private Map<Optimization, List<OptimizationLevel>> getOptimizationLevels() {
        Map<Optimization, List<OptimizationLevel>> result = new HashMap<>();
        for (Optimization optimization : Optimization.values()) {
            String settings = properties.getProperty(optimization.getOptionName());
            if (settings == null) {
                result.put(optimization, List.of(OptimizationLevel.NONE));
            } else {
                result.put(optimization, parseLevels(settings));
            }
        }

        return result;
    }

    private List<OptimizationLevel> parseLevels(String settings) {
        List<OptimizationLevel> list = Stream.of(settings.split(","))
                .map(String::trim)
                .map(OptimizationLevel::byName)
                .collect(Collectors.toCollection(ArrayList::new));

        if (list.contains(null)) {
            throw new IllegalArgumentException("Cannot parse optimization levels '" + settings + "'.");
        }

        return List.copyOf(list);
    }
}
