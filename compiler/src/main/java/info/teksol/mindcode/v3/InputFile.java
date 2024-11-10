package info.teksol.mindcode.v3;

import java.nio.file.Path;

public interface InputFile {
    int getId();

    /** @return true if this file is a standalone source (i.e. not loaded from existing file or library */
    boolean isStandaloneSource();

    /** @return true if this file is a system library */
    boolean isLibrary();

    /** @return source code of this file */
    String getCode();

    /** @return path to the file as given when loaded */
    Path getPath();

    /** @return relative path to the root of all input files (if applicable) */
    Path getRelativePath();

    /** @return absolute, normalized path of the file */
    String getAbsolutePath();

    /** @return distinct path for displaying in log files */
    String getDistinctPath();

    /** @return distinct title for displaying in log files  */
    String getDistinctTitle();
}
