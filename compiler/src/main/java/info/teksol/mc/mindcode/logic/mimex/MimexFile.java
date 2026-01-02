package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@NullMarked
public class MimexFile {
    private final String fileName;
    private final List<String> header;
    private final List<Record> records;

    public static MimexFile load(String directory, String fileName) {
        String resourceName = directory + "/" + fileName;
        List<String> lines;

        try (InputStream input = MindustryMetadata.class.getResourceAsStream(resourceName)) {
            if (input == null) {
                return new MimexFile(fileName, List.of(), List.of());
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                lines = reader.lines()
                        .filter(l -> !l.startsWith("//") && !l.isBlank())
                        .toList();
                List<String> header = List.of(lines.getFirst().split(";"));
                List<List<String>> records = lines.stream().skip(1).map(l -> List.of(l.split(";"))).toList();
                return new MimexFile(fileName, header, records);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + resourceName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + resourceName, e);
        }
    }

    private MimexFile(String fileName, List<String> header, List<List<String>> records) {
        this.fileName = fileName;
        this.header = header;
        this.records = records.stream().map(Record::new).toList();
    }

    public String getFileName() {
        return fileName;
    }

    public int getColumnIndex(String columnName) {
        int index = header.indexOf(columnName);
        if (index < 0 && !header.isEmpty()) {
            throw new IllegalStateException("Cannot locate column " + columnName + " in " + String.join(";", header));
        }
        return index;
    }

    public List<Record> getRecords() {
        return records;
    }

    public int getSize() {
        return records.size();
    }

    public Record getRecord(int index) {
        return records.get(index);
    }

    public Optional<Record> findRecord(String name) {
        int columnIndex = getColumnIndex("name");
        return columnIndex < 0 ? Optional.empty()
                : records.stream().filter(r -> r.getAttribute(columnIndex).equals(name)).findFirst();
    }

    public Optional<String> findAttribute(String name, String attribute) {
        return findAttribute("name", name, attribute);
    }

    public Optional<String> findAttribute(String idColumnName, String id, String attribute) {
        int columnIndex = getColumnIndex(idColumnName);
        return columnIndex < 0 ? Optional.empty() : records.stream()
                .filter(r -> r.getAttribute(columnIndex).equals(id))
                .findFirst()
                .flatMap(r -> r.getAttribute(attribute));
    }

    public class Record {
        private final List<String> values;

        private Record(List<String> values) {
            this.values = values;
        }

        public String getAttribute(int columnIndex) {
            return values.get(columnIndex);
        }

        public Optional<String> getAttribute(String attribute) {
            int columnIndex = getColumnIndex(attribute);
            return columnIndex < 0 ? Optional.empty() : Optional.of(values.get(columnIndex));
        }
    }
}
