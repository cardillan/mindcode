package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/// This class hold all input files used in a compilation.
@NullMarked
public class InputFiles {
    private static final Path EMPTY_PATH = Path.of("");

    /// Base path of the project. Path of the source file, or empty path when compiling a String.
    /// Always absolute.
    private final Path basePath;

    /// All registered input files
    private final List<InputFile> inputFiles = new ArrayList<>();

    /// Registered files residing on the file system.
    private final Map<Path, InputFile> standardFiles = new HashMap<>();

    /// Contents of files for later retrieval.
    private final List<String> fileContents = new ArrayList<>();

    /// Count of file names used.
    private final Map<String, AtomicInteger> fileNames = new HashMap<>();

    private InputFiles(Path basePath) {
        this.basePath = basePath;
    }

    public List<InputFile> getInputFiles() {
        return inputFiles;
    }

    public static InputFiles create() {
        return new InputFiles(EMPTY_PATH);
    }

    public static InputFiles create(Path basePath) {
        return new InputFiles(basePath);
    }

    public static InputFiles fromSource(String source) {
        InputFiles inputFiles = new InputFiles(EMPTY_PATH);
        inputFiles.registerFile(EMPTY_PATH, source);
        return inputFiles;
    }

    public InputFile registerFile(Path path, String code) {
        Path absolute = path.toAbsolutePath().normalize();
        if (standardFiles.containsKey(absolute)) {
            InputFile existing = standardFiles.get(absolute);
            if (existing.getCode().equals(code)) {
                return existing;
            }

            throw new IllegalArgumentException("File already registered with different content: " + absolute);
        }

        InputFileImpl inputFile = createFile(path, false, code);
        fileNames.computeIfAbsent(inputFile.getFileName(), k -> new AtomicInteger()).incrementAndGet();
        standardFiles.put(absolute, inputFile);
        return inputFile;
    }

    public InputFile registerLibraryFile(Path path, String code) {
        return createFile(path, true, code);
    }

    public InputFile registerSource(String code) {
        return createFile(EMPTY_PATH, false, code);
    }

    public Path getBasePath() {
        return basePath;
    }

    public InputFile getMainInputFile() {
        return inputFiles.getFirst();
    }

    public InputFile getInputFile(Path path) {
        return standardFiles.get(path.toAbsolutePath().normalize());
    }

    private InputFileImpl createFile(Path path, boolean library, String code) {
        InputFileImpl inputFile = new InputFileImpl(fileContents.size(), library, path);
        inputFiles.add(inputFile);
        fileContents.add(code);
        return inputFile;
    }

    /// Represents an input file
    public class InputFileImpl implements InputFile {
        /// ID of the file for storing in file position
        private final int id;

        /// Indicates whether this is a physical file residing on a file system
        private final boolean library;

        /// Path to the file
        private final Path path;

        private final String fileName;

        private final String absolutePath;

        private InputFileImpl(int id, boolean library, Path path) {
            this.id = id;
            this.library = library;
            this.path = path;
            fileName = path.getFileName().toString();
            absolutePath = library ? "*" + path : path.toAbsolutePath().normalize().toString();
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public boolean isStandaloneSource() {
            return path.toString().isEmpty();
        }

        @Override
        public boolean isLibrary() {
            return library;
        }

        @Override
        public Path getPath() {
            return path;
        }

        public String getFileName() {
            return fileName;
        }

        @Override
        public String getDistinctPath() {
            return isStandaloneSource() ? ""
                    : library ? "*" + fileName
                    : standardFiles.size() <= 1 ? ""
                    : fileNames.get(fileName).get() <= 1 ? fileName
                    : absolutePath;
        }

        @Override
        public String getDistinctTitle() {
            return isStandaloneSource() ? "Main file"
                    : library ? "System library " + fileName
                    : fileNames.get(fileName).get() <= 1 ? "File " + fileName
                    : "File " + absolutePath;
        }

        @Override
        public String getAbsolutePath() {
            return absolutePath;
        }

        @Override
        public Path getRelativePath() {
            return library ? path : path.relativize(basePath);
        }

        @Override
        public String getCode() {
            return fileContents.get(id);
        }

        private InputFiles parent() {
            return InputFiles.this;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass() ||
                parent() != ((InputFileImpl) o).parent() && !library) return false;

            InputFileImpl inputFile = (InputFileImpl) o;
            return (library == inputFile.library)
                   && (library ? Objects.equals(path, inputFile.path) : id == inputFile.id);
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "InputFile{" +
                    "id=" + id +
                    ", library=" + library +
                    ", path=" + path +
                    '}';
        }
    }
}
