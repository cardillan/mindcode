package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public abstract class AbstractCommandLineTest {

    protected Namespace parseCommandLine(String args) throws ArgumentParserException {
        ArgumentParser parser = Main.createArgumentParser(Arguments.fileType(), 79);
        return parser.parseArgs(args.split("\\s+"));
    }
}