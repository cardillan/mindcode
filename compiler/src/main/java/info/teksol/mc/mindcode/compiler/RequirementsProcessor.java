package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequireFile;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequireLibrary;
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
public class RequirementsProcessor extends CompilerMessageEmitter {
    private static final Map<String, InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();

    private final CompilerProfile profile;
    private final InputFiles inputFiles;

    public RequirementsProcessor(MessageConsumer messageConsumer, CompilerProfile profile, InputFiles inputFiles) {
        super(messageConsumer);
        this.inputFiles = inputFiles;
        this.profile = profile;
    }

    public @Nullable InputFile loadFile(AstRequire requirement) {
        if (requirement.isLibrary()) {
            return loadLibrary((AstRequireLibrary) requirement);
        } else if (inputFiles.hasPackagedFile(requirement.getName())) {
            return inputFiles.registerFile(inputFiles.getBasePath().resolve(requirement.getName()),
                    inputFiles.getPackagedFile(requirement.getName()));
        } else if (profile.isWebApplication()) {
            error(requirement, ERR.REQUIRE_WEBAPP_EXT_UNSUPPORTED);
            return null;
        } else {
            return loadFile((AstRequireFile) requirement, inputFiles.getBasePath().resolve(requirement.getName()));
        }
    }

    private @Nullable InputFile loadFile(AstRequireFile requirement, Path path) {
        try {
            String code = Files.readString(path, StandardCharsets.UTF_8);
            return inputFiles.registerFile(path, code);
        } catch (IOException e) {
            error(requirement.getFile(), ERR.REQUIRE_ERROR_READING_FILE, path);
            return null;
        }
    }

    private @Nullable InputFile loadLibrary(AstRequireLibrary requirement) {
        return LIBRARY_SOURCES.computeIfAbsent(requirement.getName(), s -> loadLibraryFromResource(requirement));
    }

    private @Nullable InputFile loadLibraryFromResource(AstRequireLibrary requirement) {
        String libraryName = requirement.getName();
        try {
            InputFile library = loadSystemLibrary(libraryName);
            if (library == null) {
                error(requirement.getLibrary(), ERR.REQUIRE_UNKNOWN_SYSTEM_LIBRARY, libraryName);
            }
            return library;
        } catch (IOException e) {
            error(requirement, ERR.REQUIRE_ERROR_READING_SYSTEM_FILE, libraryName);
            throw new MindcodeInternalError(e, ERR.REQUIRE_ERROR_READING_SYSTEM_FILE, libraryName);
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
