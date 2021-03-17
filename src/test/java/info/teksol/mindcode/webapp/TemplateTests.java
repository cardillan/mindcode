package info.teksol.mindcode.webapp;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateTests {
    @TestFactory
    List<DynamicTest> templatesAreValid() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/main/resources/templates";
        final File[] files = new File(dirname).listFiles();
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one template; found none");
        Arrays.sort(files);

        final VelocityEngine engine = createEngine();

        final File root = new File("src/main/resources");
        for (final File template : files) {
            result.add(DynamicTest.dynamicTest(template.getName(), null, () -> {
                final String templateName = template.getPath().replace(
                        root.getPath() + "/",
                        ""
                );
                assertDoesNotThrow(() -> engine.getTemplate(templateName));
            }));
        }

        return result;
    }

    VelocityEngine createEngine() {
        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.ENCODING_DEFAULT, "UTF-8");
        velocityEngine.setProperty("resource.loaders", "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.classpath.cache", true);
        velocityEngine.setProperty("resource.loader.classpath.modificationCheckInterval", -1);
        velocityEngine.init();
        return velocityEngine;
    }
}
