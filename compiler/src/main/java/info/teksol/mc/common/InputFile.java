package info.teksol.mc.common;

import info.teksol.mc.messages.SourcePositionTranslator;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public interface InputFile {
    int getId();

    /// @return true if this file is a standalone source (i.e., not loaded from an existing file or library - has no path)
    boolean isStandaloneSource();

    /// @return true if this file is a system library
    boolean isLibrary();

    /// @return source code of this file
    String getCode();

    /// @return number of lines in this file
    int getNumberOfLines();

    /// @return path to the file as given when loaded
    Path getPath();

    /// @return distinct path for displaying in log files
    String getDistinctPath();

    /// @return distinct title for displaying in log files
    String getDistinctTitle();

    /// An input file represents a single source code unit. The contents of the input file may have been assembled
    /// from several pieces - code snippets (either via the Schematic builder or via the excerpt command line option).
    /// This method returns a position translator which translates a position specified within the entire input file
    /// into the proper location of the original code snippets.
    ///
    /// @return source position translator valid for this file
    SourcePositionTranslator getPositionTranslator();

    InputFile EMPTY = new InputFile() {
        @Override
        public int getId() {
            return Integer.MIN_VALUE;
        }

        @Override
        public boolean isStandaloneSource() {
            return false;
        }

        @Override
        public boolean isLibrary() {
            return false;
        }

        @Override
        public String getCode() {
            return "";
        }

        @Override
        public int getNumberOfLines() {
            return 1;
        }

        @Override
        public Path getPath() {
            return Path.of("");
        }

        @Override
        public String getDistinctPath() {
            return "";
        }

        @Override
        public String getDistinctTitle() {
            return "";
        }

        @Override
        public String toString() {
            return "EMPTY_INPUT_FILE";
        }

        @Override
        public SourcePositionTranslator getPositionTranslator() {
            return p -> p;
        }
    };
}
