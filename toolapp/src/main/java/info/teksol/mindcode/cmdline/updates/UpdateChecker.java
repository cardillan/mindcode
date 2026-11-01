package info.teksol.mindcode.cmdline.updates;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class UpdateChecker {
    private static final String URL = "https://raw.githubusercontent.com/cardillan/mindcode/refs/heads/devel/toolapp/releases.json";

    public static void checkForUpdates() {
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
                        byte[] data = in.readAllBytes();
                        //processReleases(data);
                    }
                } else if (status == 403 || status == 429) {
                    String remaining = connection.getHeaderField("X-RateLimit-Remaining");
                    String reset = connection.getHeaderField("X-RateLimit-Reset");
                    String retryAfter = connection.getHeaderField("Retry-After");

                    // Log rate-limit information if available.
                } else {
                    // Other HTTP error.
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
}
