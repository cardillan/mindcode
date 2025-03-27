# Command line tool

After building the project, an executable jar file is created at `compiler\target\mindcode.jar`. In the bin directory, there's a Linux script file `mindcode` and a Windows batch file `mindcode.bat`, which can be used to run the command line tool.

* Linux `mindcode` file requires Java 22 executable to be on the path.
* Windows `mindcode.bat` file requires the `JAVA_HOME` variable to point to Java 22 installation.

# Using the command line tool

The command line tool supports three different actions. The action is specified by the first command line argument, which must be one of the following:

* `cm` or `compile-mindcode`: compiles a Mindcode source to mlog.
* `dm` or `decompile-mlog`: partially decompiles an mlog code into Mindcode. The resulting code needs to be manually edited to create loops and conditions present in the original mlog.
* `cs` or `compile-schematic`: builds a schematic from Schemacode source into a binary `.msch` file.
* `ds` or `decompile-schematic`: decompiles a binary `.msch` file to Schemacode source.

Command-line arguments (e.g. `--remarks`) are case-sensitive. Values of command-line options (e.g. `none`) are generally case-insensitive. It is possible to use both `--remarks none` and `--remarks NONE`, although the lower-case specification is preferred. All multi-word command-ine options use _kebab-case_ convention. 

## Input/output files

All actions take the name of input file and the name of output file as an argument. When the given input or output is a text file, the argument is optional and when not specified, the standard input/output is used. Use `-` to explicitly specify standard input or output for input or output file.

### Input file excerpt

When performing the _Compile Mindcode_ action, Mindcode accepts the `--excerpt` command line option followed by a `line:column-line:column` specification of the portion of the input file to load for compiling (both line and column indexes starting at 1). For example, `--excerpt 8:5-15:17` selects text starting at line 8, column 5 and ending at line 15, column 17 from the input file. Some IDEs can be configured to produce these values corresponding to the selected text, giving teh ability to compile just the selected text using Mindcode.

The `--excerpt` option is applied only to the main input file, not to the files added through the `--append` command line option or `require` directive.

### Additional input files

When performing the _Compile Mindcode_ action, it is possible to use the `-a` or `--append` command line parameter to specify additional source files to be compiled along with the input file. The source files are parsed separately and error messages that may be generated during the compilation include the name of the file where the error occurred.

The `--append` command-line option has the same effect as the [`require` statement](SYNTAX.markdown#libraries-and-external-files).

## Log file

The `-l` argument can be used to specify log file, a file which receives messages generated while running the tool. When such file isn't specified, the standard output is used (if standard output is already used for the output file, messages are witten to the standard error instead).

## MLog Watcher integration

The command-line tool can send the compiled code directly into a processor in a running Mindustry game through the Mlog Watcher mod. See [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown) for details.

## Clipboard integration

When compiling Mindcode or building a schematic, the `-c` or `--clipboard` argument can be used. In this case, if the action was successful, the output is copied to the clipboard and can be pasted directly into Mindustry:

* _Compile Mindcode_ action: clipboard contains mlog instructions which can be pasted into a processor on the processor configuration screen, by using **Edit/Import from clipboard** command.
* _Compile Schematic_ action: clipboard contains schematic encoded into Mindustry Schematic string, which can be pasted as new schematic on the Schematics screen, by using **Import schematic.../Import from clipboard** command. 

## Running the compiled code

When performing the _Compile Mindcode_ action, it is possible to use the `--run` command line option to run the resulting mlog code on an emulated processor. The processor is much faster than Mindustry Logic processors, but supports only very few operations that interact with the Mindustry World.

The behavior of the processor emulator can be further modified through the execution flags. For more details, refer to the [processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown) documentation.

## Compiler options

Compiler options, such as target Mindustry Logic version and compiler optimizations, can be specified for _Compile Mindcode_ and _Compile schema_ actions. See the command line help for more details.

# Command line help

It is possible to obtain command line help by using a `-h` or `--help` argument. A general help is displayed when no action is specified, and an action-specific help is displayed when using `-h` together with action. For reference, the command line help is included here.

## General help

```
#generate:GENERAL
```

## Compile Mindcode action help

```
#generate:COMPILE_MINDCODE
```

## Decompile Mlog action help

```
#generate:DECOMPILE_MLOG
```

## Compile Schematic action help

```
#generate:COMPILE_SCHEMA
```

## Decompile Schematic action help

```
#generate:DECOMPILE_SCHEMA
```

---

[« Previous: IDE Integration](TOOLS-IDE-INTEGRATION.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Processor emulator »](TOOLS-PROCESSOR-EMULATOR.markdown)
