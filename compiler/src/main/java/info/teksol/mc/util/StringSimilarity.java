package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class StringSimilarity {

    // Mindcode specific equivalences between keywords
    private static final List<Set<String>> equivalences = List.of(
            Set.of("false", "off", "none", "no"),
            Set.of("true", "on", "full", "yes"),
            Set.of("aggressive", "advanced")
    );

    // Implementation of the Levenshtein Edit Distance
    // See https://rosettacode.org/wiki/Levenshtein_distance#Java
    private static int editDistance0(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }

            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    // Computes word-based edit distance with fuzzy matching
    private static double editDistance(String existingValue, String userValue) {
        // Split to words
        String[] words1 = userValue.split("[ _-]");
        String[] words2 = existingValue.split("[ _-]");

        // Penalize differences in word count
        double sum = Math.abs(words1.length - words2.length) / 2.0;

        for (String word1 : words1) {
            String bestWord = "";
            int min = Integer.MAX_VALUE;
            for (String word2 : words2) {
                int distance = editDistance0(word1, word2);
                if (distance < min) {
                    min = distance;
                    bestWord = word2;
                }
            }
            sum += min / (double) Math.max(word1.length(), bestWord.length());
        }

        return sum;
    }

    private static Set<String> findAlternatives(String value) {
        for (Set<String> set : equivalences) {
            if (set.contains(value)) return set;
        }
        return Set.of(value);
    }

    private static WordDistance editDistanceWithAlternatives(String existingWord, String userValue) {
        Set<String> alternatives = findAlternatives(existingWord);
        double value = alternatives.stream()
                .mapToDouble(a -> editDistance(a, userValue))
                .min().orElse(Double.MAX_VALUE);
        return new WordDistance(existingWord, value);
    }

    public static Optional<String> findBestAlternative(String value, Collection<String> allowedValues) {
        return allowedValues.stream()
                .map(a -> editDistanceWithAlternatives(a, value))
                .min(Comparator.comparingDouble(WordDistance::distance))
                .filter(d -> d.distance < 1)
                .map(WordDistance::word);
    }

    public static Optional<String> findBestAlternative(String value, String... allowedValues) {
        return findBestAlternative(value, Arrays.asList(allowedValues));
    }

    private record WordDistance(String word, double distance) {}

    public static void main(String[] args) {
        String option = "large-scale";
        List<String> values = List.of(
                "large_scale_operation",
                "large",
                "scale",
                "operation",

                "syntax",
                "target");

        values.forEach(v -> System.out.println(v + ": " + editDistance(v, option)));
    }
}
