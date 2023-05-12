package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.schema.Schematics;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchematicsIOTest {

    @Test
    public void testDecodeLabels() {
        String strLabels = """
                [Test,"a,b,c","a,/"b/",/",c/",//,/"///",//,x"]"""
                .replace('/', '\\');

        List<String> list = SchematicsIO.decodeLabels(strLabels);

        assertEquals(
                List.of("Test", "a,b,c", "a,\"b\",\",c\",\\,\"\\\",\\,x"),
                list);
    }

    @Test
    public void testEncodeLabelsSimple() {
        List<String> labels = List.of("foo", "bar", "baz");
        String encoded = SchematicsIO.encodeLabels(labels);

        assertEquals("[foo,bar,baz]", encoded);
    }

    @Test
    public void testEncodeLabelsEscaped() {
        List<String> labels = List.of("a,b,c", "\"bar\"", "[baz]");
        String encoded = SchematicsIO.encodeLabels(labels);

        assertEquals("[\"a,b,c\",\"\\\"bar\\\"\",\"[baz]\"]", encoded);
    }

    @TestFactory
    List<DynamicTest> readsSchematics() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/test/resources/schematics";
        final File[] files = new File(dirname).listFiles();
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one schematic; found none");
        Arrays.sort(files);

        for (final File file : files) {
            result.add(DynamicTest.dynamicTest(file.getName(), null, () -> readSchema(file)));
        }

        return result;
    }

    private void readSchema(File schema) {
        assertDoesNotThrow(() -> SchematicsIO.read(new FileInputStream(schema)));
    }

    @TestFactory
    List<DynamicTest> rewriteSchematics() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/test/resources/schematics";
        final File[] files = new File(dirname).listFiles();
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one schematic; found none");
        Arrays.sort(files);

        for (final File file : files) {
            result.add(DynamicTest.dynamicTest(file.getName(), null, () -> rewriteSchema(file)));
        }

        return result;
    }

    private void rewriteSchema(File schema) throws IOException {
        // Reads schematics and writes it again, expects to obtain identical result.
        // Comparison done on uncompressed arrays (although processor configuration is compressed anyway)
        // The schematics were obtained from Mindustry itself.
        byte[] contents = Files.readAllBytes(schema.toPath());
        Schematics schematics = assertDoesNotThrow(() -> SchematicsIO.read(new ByteArrayInputStream(contents)));
        byte[] rewritten = assertDoesNotThrow(() -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematics, output);
            return output.toByteArray();
        });

        String expected = streamToString(SchematicsIO.skipHeader(new ByteArrayInputStream(contents)).getT1());
        String actual = streamToString(SchematicsIO.skipHeader(new ByteArrayInputStream(rewritten)).getT1());

        assertEquals(expected, actual);
    }

    private static final String HEX = "0123456789ABCDEF";

    private String streamToString(DataInputStream stream) throws IOException {
        byte[] bytes = stream.readAllBytes();
        StringBuilder sbr = new StringBuilder(bytes.length * 4);
        int index = 0;
        for (byte b : bytes) {
            if (index > 0) {
                sbr.append(switch (index % 16) {
                    case 0 -> "\n";
                    case 8 -> " | ";
                    default -> " ";
                });
            }
            appendByte(sbr, b);
            index++;
        }

        return sbr.toString();
    }

    private void appendByte(StringBuilder sbr, byte b) {
        int val = 0xff & ((int) b);
        sbr.append(HEX.charAt(val >> 4));
        sbr.append(HEX.charAt(val & 15));
    }

    @Test
    public void correctlyFormatsBytes() {
        assertDoesNotThrow(() -> {
            StringBuilder sbr = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                byte b = (byte) i;
                sbr.setLength(0);
                appendByte(sbr, b);

                String correct = String.format("%02X", i);

                if (!sbr.toString().equals(correct)) {
                    throw new RuntimeException("Invalid encoding at " + i);
                }
            }
        });
    }
}