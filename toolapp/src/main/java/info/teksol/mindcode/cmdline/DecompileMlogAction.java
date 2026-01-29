package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.*;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.mindcode.compiler.generation.variables.StandardNameCreator;
import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public class DecompileMlogAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(ToolAppAction.DECOMPILE_MLOG.getShortcut())
                .aliases("decompile-mlog")
                .description("Partially decompile a text mlog file into Mindcode source file.")
                .help("Decompile a text mlog file into Mindcode source, leaving jumps and unknown instructions unresolved.");

        ArgumentGroup files = subparser.addArgumentGroup("Input/output");

        files.addArgument("input")
                .help("Mlog text file to be decompiled into Mindcode source file.")
                .type(inputFileType);

        files.addArgument("-o", "--output")
                .help("Output file to receive decompiled Mindcode (doesn't produce an output when not specified).")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        files.addArgument("--output-directory")
                .dest("output-directory")
                .help("specifies the directory where the output files will be placed")
                .type(Arguments.fileType().verifyIsDirectory());

        addMlogWatcherOptions(files, ToolAppAction.DECOMPILE_MLOG);

        addAllCompilerOptions(subparser, options, OptionCategory.EMULATOR);
        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile profile = createCompilerProfile(false, arguments);
        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");
        String mlog = readInput(inputFile);

        if (profile.isRun()) {
            run(profile, mlog);
        }

        final File outputFile = arguments.get("output");
        if (outputFile != null) {
            final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".dmnd");
            String decompiled = MlogDecompiler.decompile(mlog);
            writeOutput(output, decompiled);
        }
    }

    private void run(CompilerProfile profile, String mlog) {
        PositionFormatter positionFormatter = sp -> sp.formatForIde(profile.getFileReferences());
        ConsoleMessageLogger messageConsumer = ConsoleMessageLogger.create(positionFormatter, false, true);
        EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageConsumer);

        InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer,
                new StandardNameCreator(), profile);
        MindustryMetadata metadata = instructionProcessor.getMetadata();

        LogicBlock logicBlock = LogicBlock.createProcessor(metadata, profile.getEmulatorTarget().type(),
                BlockPosition.ZERO_POSITION, mlog);
        initializeLogicBlock(metadata, logicBlock);
        EmulatorSchematic schematic = new EmulatorSchematic(List.of(logicBlock));
        Emulator emulator = new BasicEmulator(messageConsumer, profile, schematic);

        emulator.run(profile.getStepLimit());
        processEmulatorResults(emulatorMessages, emulator, profile.isOutputProfiling());
    }

    private void initializeLogicBlock(MindustryMetadata metadata, LogicBlock logicBlock) {
        // All flags are already set as we want them to be
        addBlocks(logicBlock, "cell", _ -> MemoryBlock.createMemoryCell(metadata, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "bank", _ -> MemoryBlock.createMemoryBank(metadata, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "display", i -> LogicDisplay.createLogicDisplay(metadata, i < 5, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "message", _ -> MessageBlock.createMessage(metadata, BlockPosition.ZERO_POSITION));
    }

    private void addBlocks(LogicBlock logicBlock, String name, Function<Integer, MindustryBuilding> creator) {
        for (int i = 1; i < 10; i++) {
            logicBlock.addBlock(name + i, creator.apply(i));
        }
    }
}
