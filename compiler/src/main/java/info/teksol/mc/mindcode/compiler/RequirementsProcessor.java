package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// The RequirementsProcessor class is responsible for processing and managing dependency
/// requirements in the context of a compilation. It handles loading input files and system
/// libraries, as well as reporting issues related to requirement handling.
///
/// This class extends AbstractMessageEmitter, leveraging its messaging utilities to report
/// warnings, errors, and other informational messages during the execution.
@NullMarked
public class RequirementsProcessor extends AbstractMessageEmitter {
    private static final Map<String, InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();

    private final CompilerProfile profile;
    private final InputFiles inputFiles;

    public RequirementsProcessor(MessageConsumer messageConsumer, CompilerProfile profile, InputFiles inputFiles) {
        super(messageConsumer);
        this.inputFiles = inputFiles;
        this.profile = profile;
    }

    public @Nullable InputFile processRequirement(AstRequire requirement) {
        if (requirement.isLibrary()) {
            return loadLibrary(requirement);
        } else if (profile.isWebApplication()) {
            error(requirement, "Loading code from external file not supported in web application.");
            return null;
        } else {
            return loadFile(requirement, inputFiles.getBasePath().resolve(requirement.getName()));
        }
    }

    private @Nullable InputFile loadFile(AstRequire requirement, Path path) {
        try {
            String code = Files.readString(path, StandardCharsets.UTF_8);
            return inputFiles.registerFile(path, code);
        } catch (IOException e) {
            error(requirement, "Error reading file '%s'.", path);
            return null;
        }
    }

    private @Nullable InputFile loadLibrary(AstRequire requirement) {
        return LIBRARY_SOURCES.computeIfAbsent(requirement.getName(), s -> loadLibraryFromResource(requirement));
    }

    private @Nullable InputFile loadLibraryFromResource(AstRequire requirement) {
        String libraryName = requirement.getName();
        try {
            InputFile library = loadSystemLibrary(libraryName);
            if (library == null) {
                error(requirement, "Unknown system library '%s'.", libraryName);
            }
            return library;
        } catch (IOException e) {
            error(requirement, "Error reading system library file '%s'.", libraryName);
            throw new MindcodeInternalError(e, "Error reading system library file '%s'.", libraryName);
        }
    }

    private @Nullable InputFile loadSystemLibrary(String libraryName) throws IOException {
        try (InputStream resource = MindcodeCompiler.class.getResourceAsStream("/library/" + libraryName + ".mnd")) {
            if (resource == null) {
                return null;
            }
            try (final InputStreamReader reader = new InputStreamReader(resource)) {
                final StringWriter out = new StringWriter();
                reader.transferTo(out);
                return inputFiles.registerLibraryFile(Path.of(libraryName), out.toString());
            }
        }
    }
}
