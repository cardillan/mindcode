package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.BlockPosition;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.mindcode.compiler.generation.variables.StandardNameCreator;
import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherClient;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherCommand;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.List;
import java.util.Map;

@NullMarked
public class ProcessMlogAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(ToolAppAction.PROCESS_MLOG.getShortcut())
                .aliases("process-mlog", "dm", "decompile-mlog")
                .description("Load mlog code from a file or an in-game processor for further processing (partially decompiling " +
                        "into a Mindcode source, running on the internal emulator or sending to an in-game processor).")
                .help("Load and process mlog code from a file or an in-game processor.");

        ArgumentGroup files = subparser.addArgumentGroup("Input/output");

        files.addArgument("input")
                .help("Mlog text file to be decompiled into Mindcode source file. When -w extract is used, the input file must not be specified.")
                .nargs("?")
                .type(inputFileType);

        addOutputFileOption(files, true,
                "output file to receive the mlog file extracted from the processor selected in the game.",
                "--output-mlog");

        addOutputFileOption(files, true,
                "output file to receive the decompiled Mindcode.",
                "--output-decompiled");

        addOutputDirectoryOption(files);

        addMlogWatcherOptions(files, ToolAppAction.PROCESS_MLOG);

        addAllCompilerOptions(subparser, options, OptionCategory.EMULATOR);
        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile profile = createCompilerProfile(false, arguments);

        final PositionFormatter positionFormatter = sp -> sp.formatForIde(profile.getFileReferences());
        ConsoleMessageLogger messageLogger = ConsoleMessageLogger.create(positionFormatter, false, true);
        ToolMessageEmitter toolMessages = new ToolMessageEmitter(messageLogger);

        final MlogWatcherCommand command = getMlogWatcherCommand(arguments);
        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");

        if (inputFile == null && command != MlogWatcherCommand.EXTRACT) {
            toolMessages.error("No input file or Mlog Watcher source specified.");
            return;
        } else if (inputFile != null && command == MlogWatcherCommand.EXTRACT) {
            toolMessages.error("Both an input file and an Mlog Watcher source specified.");
            return;
        }

        MlogWatcherClient mlogWatcherClient = createMlogWatcherClient(arguments, toolMessages);
        try {
            String mlog;
            if (command == MlogWatcherCommand.EXTRACT) {
                // Couldn't connect, error already reported
                if (mlogWatcherClient == null) return;
                mlog = mlogWatcherClient.extractSelectedProcessorCode();

                // Couldn't load, error already reported
                if (mlog == null) return;
            } else {
                mlog = readInput(inputFile);
            }

            if (profile.isRun()) {
                run(profile, mlog, messageLogger);
            }

            final File outputMlog = arguments.get("output_mlog");
            if (outputMlog != null) {
                final File output = resolveOutputFile(inputFile, outputDirectory, outputMlog, ".mlog", "mlog-watcher");
                writeOutput(output, mlog);
            }

            final File outputDecompiled = arguments.get("output_decompiled");
            if (outputDecompiled != null) {
                final File output = resolveOutputFile(inputFile, outputDirectory, outputDecompiled, ".dmnd", "mlog-watcher");
                String decompiled = MlogDecompiler.decompile(mlog);
                writeOutput(output, decompiled);
            }

            if (mlogWatcherClient != null && command == MlogWatcherCommand.UPDATE) {
                mlogWatcherClient.updateSelectedProcessor(mlog);
            }
        } finally {
            if (mlogWatcherClient != null) {
                mlogWatcherClient.close();
            }
        }
    }

    private void run(CompilerProfile profile, String mlog, ConsoleMessageLogger messageLogger) {
        EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageLogger);

        InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageLogger,
                new StandardNameCreator(), profile);
        MindustryMetadata metadata = instructionProcessor.getMetadata();

        LogicBlock logicBlock = LogicBlock.createProcessor(metadata, profile.getEmulatorTarget().type(),
                BlockPosition.ZERO_POSITION, mlog);
        logicBlock.createDefaultBlocks(metadata);

        EmulatorSchematic schematic = new EmulatorSchematic(List.of(logicBlock));
        Emulator emulator = new BasicEmulator(messageLogger, profile, schematic);

        emulator.run(profile.getStepLimit());
        processEmulatorResults(emulatorMessages, emulator, profile.isOutputProfiling());
    }
}
