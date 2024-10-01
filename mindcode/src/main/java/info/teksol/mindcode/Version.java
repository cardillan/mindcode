package info.teksol.mindcode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

    public static String getVersion() {
        try (InputStream in = Version.class.getResourceAsStream("mindcode.properties")) {
            if (in == null) {
                return "unknown";
            }
            final Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("mindcode.version");
        } catch (IOException e) {
            throw new RuntimeException("Error obtaining version information", e);
        }
    }
}
