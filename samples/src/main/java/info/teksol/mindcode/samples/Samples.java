package info.teksol.mindcode.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Samples {

    public static Map<String, Sample> loadMindcodeSamples() {
        final List<String> sampleNames = List.of(
                "control-two-units",
                "relaxed:one-thorium",
                "relaxed:many-thorium",
                "heal-damaged-building",
                "mining-drone",
                "upgrade-conveyors",
                "sum-of-primes"
        );

        return sampleNames.stream().map(Samples::loadMindcodeSample)
                .collect(Collectors.toMap(Sample::name, s -> s));
    }

    public static Map<String, Sample> loadSchemacodeSamples() {
        final List<String> sampleNames = List.of(
                "detector",
                "healing-center",
                "on-off-switch",
                "regulator",
                "slow:overdrive-dome-supply",
                "worker-recall-station",
                "scrap-to-metaglass-2",
                "payload-source",
                "slow:mandelbrot-generator"
        );

        return sampleNames.stream().map(Samples::loadSchemacodeSample)
                .collect(Collectors.toMap(Sample::name, s -> s));
    }

    private static Sample loadMindcodeSample(String sampleName) {
        return loadSample("samples/mindcode/", sampleName, ".mnd");
    }

    private static Sample loadSchemacodeSample(String sampleName) {
        return loadSample("samples/schematics/", sampleName, ".sdf");
    }

    private static Sample loadSample(String path, String sampleName, String extension) {
        boolean slow = sampleName.startsWith("slow:");
        boolean relaxed = sampleName.startsWith("relaxed:");
        String fileName = sampleName.replaceAll(".*:", "");

        URL sample = Samples.class.getClassLoader().getResource(path + fileName + extension);
        if (sample == null) {
            throw new RuntimeException("Cannot locate sample file: " + fileName);
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(sample.openStream()))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return new Sample(fileName, out.toString(), slow, relaxed);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read sample file: " + fileName);
        }
    }
}