package info.teksol.mindcode.v3;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class hold all input files used in a compilation.
 */
public class InputFiles {
    private static final Path EMPTY_PATH = Path.of("");

    /**
     * Base path of the project. Path of the source file, or empty path when compiling a String.
     * Always absolute.
     */
    private final Path basePath;

    /**
     * All registered input files
     */
    private final List<InputFile> inputFiles = new ArrayList<>();

    /**
     * Registered files residing on the file system.
     */
    private final Map<Path, InputFile> standardFiles = new HashMap<>();

    /**
     * Registered virtual files
     */
    private final Map<Path, InputFile> virtualFiles = new HashMap<>();

    /**
     * Contents of files for later retrieval.
     */
    private final List<String> fileContents = new ArrayList<>();

    private InputFiles(Path basePath) {
        this.basePath = basePath;
    }

    public static InputFiles create() {
        return new InputFiles(EMPTY_PATH);
    }

    public static InputFiles create(Path basePath) {
        return new InputFiles(basePath);
    }

    public static InputFiles fromSource(String source) {
        InputFiles inputFiles = new InputFiles(EMPTY_PATH);
        inputFiles.registerVirtualFile(EMPTY_PATH, source);
        return inputFiles;
    }

    public InputFile registerFile(Path path, String code) {
        return register(standardFiles, path, true, code);
    }

    public InputFile registerVirtualFile(Path path, String source) {
        return register(virtualFiles, path, false, source);
    }

    public InputFile registerSource(String source) {
        InputFile inputFile = new InputFile(fileContents.size(), false, EMPTY_PATH);
        inputFiles.add(inputFile);
        fileContents.add(source);
        return inputFile;
    }

    public Path getBasePath() {
        return basePath;
    }

    public InputFile getMainInputFile() {
        return inputFiles.get(0);
    }

    public InputFile getInputFile(Path path) {
        return standardFiles.get(path.toAbsolutePath().normalize());
    }

    private InputFile register(Map<Path, InputFile> registry, Path path, boolean fileSystem, String source) {
        Path absolute = path.toAbsolutePath().normalize();
        if (registry.containsKey(absolute)) {
            InputFile existing = registry.get(absolute);
            if (existing.getCode().equals(source)) {
                return existing;
            }

            throw new IllegalArgumentException("File already registered with different content: " + absolute);
        }

        InputFile inputFile = new InputFile(fileContents.size(), fileSystem, path);
        inputFiles.add(inputFile);
        fileContents.add(source);
        registry.put(absolute, inputFile);
        return inputFile;
    }

    public List<InputFile> getInputFiles() {
        return inputFiles;
    }

    /**
     * Represents an input file
     */
    public class InputFile {
        /** Id of the file for storing in file position */
        private final int id;

        /** Indicates whether this is a physical file residing on a file system */
        private final boolean fileSystem;

        /** Path to the file */
        private final Path path;

        private final String absolutePath;

        private InputFile(int id, boolean fileSystem, Path path) {
            this.id = id;
            this.fileSystem = fileSystem;
            this.path = path;
            absolutePath = fileSystem ? path.toAbsolutePath().normalize().toString() : "*" + path;
        }

        public int getId() {
            return id;
        }

        public boolean isEmpty() {
            return path.toString().isEmpty();
        }

        public boolean isFileSystem() {
            return fileSystem;
        }

        public Path getPath() {
            return path;
        }

        /**
         * @return a distinct path. If there's only one input file, it is an empty path, otherwise the relative file path.
         */
        public String getDistinctPath() {
            return fileSystem
                    ? standardFiles.size() <= 1 ? "" : path.toString()
                    : "*" + path.toString();
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public Path getRelativePath() {
            return fileSystem ? path.relativize(basePath) : path;
        }

        public String getCode() {
            return fileContents.get(id);
        }

        private InputFiles parent() {
            return InputFiles.this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass() || parent() != ((InputFile)o).parent()) return false;

            InputFile inputFile = (InputFile) o;
            return id == inputFile.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "InputFile{" +
                    "id=" + id +
                    ", fileSystem=" + fileSystem +
                    ", path=" + path +
                    '}';
        }
    }
}
