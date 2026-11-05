package io.github.cardillan.mindcode.toolapp.updates;

import dev.dirs.ProjectDirectories;
import io.github.cardillan.mindcode.Version;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.prefs.Preferences;

@NullMarked
public class UpdateChecker {
    private static final String URL = "https://raw.githubusercontent.com/cardillan/mindcode/refs/heads/main/toolapp/releases.json";

    private static final Preferences PREFS = Preferences.userNodeForPackage(UpdateChecker.class);
    private static final String KEY_NEXT_UPDATE = "next_update";

    /// @return the message to display to the user
    public static String checkForUpdates() {
        ProjectDirectories dirs = ProjectDirectories.from(null, "cardillan", "mindcode");
        String cacheDir = dirs.cacheDir;
        if (cacheDir == null || cacheDir.isEmpty() || !createDirectory(cacheDir)) return "";

        Path cachedFilePath = Path.of(cacheDir, "releases.json");
        String message = generateMessage(cachedFilePath);

        long nextUpdateCheckTime = PREFS.getLong(KEY_NEXT_UPDATE, 0);
        if (System.currentTimeMillis() > nextUpdateCheckTime) {
            // Check again in two hours
            PREFS.putLong(KEY_NEXT_UPDATE, System.currentTimeMillis() + 2 * 3_600_000);

            Thread thread = new Thread(() -> {
                HttpURLConnection connection = null;

                try {
                    connection = (HttpURLConnection) URI.create(URL).toURL().openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3_000);
                    connection.setReadTimeout(5_000);

                    int status = connection.getResponseCode();

                    if (status == HttpURLConnection.HTTP_OK) {
                        try (InputStream in = connection.getInputStream()) {
                            Files.createDirectories(cachedFilePath.getParent());
                            Files.copy(in, cachedFilePath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        // Success: reschedule the next attempt at twelve hours from now
                        PREFS.putLong(KEY_NEXT_UPDATE, System.currentTimeMillis() + 12 * 3_600_000);
                    }
                } catch (IOException e) {
                    // Update check failed or timed out.
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }, "update-checker");

            thread.setDaemon(true);
            thread.start();
        }

        return message;
    }

    private static boolean createDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String generateMessage(Path cachedFilePath) {
        try {
            Releases releases = Releases.read(cachedFilePath);

            Release latest = releases.latest();
            if (latest != null) {
                Version current = Version.getVersionObject();
                return releases.stream()
                        .filter(r -> r.version().compareTo(current) > 0)
                        .map(Release::importance)
                        .max(Comparator.naturalOrder())
                        .map(importance -> createMessage(importance, current, latest.version()))
                        .orElse("");
            }
        } catch (IOException ignore) {
        }

        return "";
    }

    private static String createMessage(ReleaseImportance importance, Version current, Version latest) {
        String message = switch (importance) {
            case OPTIONAL -> """
                    A new version of Mindcode is available: %s (current: %s).
                    """;
            case RECOMMENDED -> """
                    A new version of Mindcode is available: %s (current: %s).
                    
                    Updating is recommended. This release includes improvements and bug fixes.
                    """;
            case CRITICAL -> """
                    IMPORTANT: A critical Mindcode update is available!
                    
                    Current version: %2$s
                    Latest version:  %1$s
                    
                    This update fixes a critical issue. Please upgrade as soon as possible.
                    """;
        };
        return message.formatted(latest, current);
    }
}
