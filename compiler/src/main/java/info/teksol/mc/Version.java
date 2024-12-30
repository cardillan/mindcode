package info.teksol.mc;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@NullMarked
public class Version {

    public static String getVersion() {
        try (InputStream in = Version.class.getResourceAsStream("mindcode.properties")) {
            if (in == null) {
                return "unknown";
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("mindcode.version");
        } catch (IOException e) {
            throw new RuntimeException("Error obtaining version information", e);
        }
    }
}
