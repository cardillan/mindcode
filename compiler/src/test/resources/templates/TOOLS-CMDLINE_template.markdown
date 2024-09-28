# Command line tool

After building the project, an executable jar file is created at `compiler\target\mindcode.jar`. In the bin 
directory, there's a Linux script file `mcc` and a Windows batch file `mcc.bat`, which can be used to run the 
command line tool.

* Linux `mcc` file requires Java 16 or Java 17 executable to be on the path.
* Windows `mcc.bat` file requires the `JAVA_HOME` variable to point to Java 16 or Java 17 installation.

# Using the command line tool

The command line tool supports three different actions. The action is specified by the first command line argument, 
which must be one of the following:

* `cm` or `compile-mindcode`: compiles a Mindcode source to mlog.
* `cs` or `compile-schematic`: builds a schematic from Schemacode source into a binary `.msch` file.
* `ds` or `decompile-schematic`: decompiles a binary `.msch` file to Schemacode source.

## Input/output files

All actions take the name of input file and the name of output file as an argument. When the given input or output is 
a text file, the argument is optional and when not specified, the standard input/output is used. Use `-` to explicitly 
specify standard input or output for input or output file.

### Additional input files

When performing the _Compile Mindcode_ action, it is possible to use the `-a` or `--append` command line parameter to specify additional source files to be compiled along with the input file. The source files are parsed separately and error messages that may be generated during the compilation include the name of the file where the error occurred.

This feature is experimental and will be ultimately replaced by [support for modules](https://github.com/cardillan/mindcode/issues/149).

## Log file

The `-l` argument can be used to specify log file, a file which receives messages generated while running the tool. 
When such file isn't specified, the standard output is used (if standard output is already used for the output file, 
messages are witten to the standard error instead).

## Clipboard integration

When compiling Mindcode or building a schematic, the `-c` or `--clipboard` argument can be used. In this case, if the 
action was successful, the output is copied to the clipboard and can be pasted directly into Mindustry:

* _Compile Mindcode_ action: clipboard contains mlog instructions which can be pasted into a processor on the 
  processor configuration screen, by using **Edit/Import from clipboard** command.
* _Compile schema_ action: clipboard contains schematic encoded into mindustry compatible string, which can be pasted 
  as new schematic on the Schematics screen, by using **Import schematic.../Import from clipboard** command. 

## Running the compiled code

When performing the _Compile Mindcode_ action, it is possible to use the `--run` command line option to run the resulting mlog code on an emulated processor. The processor is much faster than Mindustry Logic processors, but only supports those operations that do not interact with the Mindustry World (specifically, operations that do not input information from the Mindustry World). The exception to this rule is access to external memory cells and memory banks. The emulated processor is equipped with nine memory cells and nine memory banks accessible under the names of `cell1` to `cell9` and `bank1` to `bank9` (they cannot be accessed using the `getlink` instruction). At the beginning of the executions all these memory cells and banks are empty.

The execution of the code ends when one of the following conditions is encountered:
* end of the instruction list is reached,
* an `end` or `stop` instruction is executed,
* execution step limits is exceeded,
* an unsupported operation is attempted.

When the execution ends, the contents of the print buffer is written to the log file.

## Compiler options

Compiler options, such as target Mindustry Logic version and compiler optimizations, can be specified for _Compile 
Mindcode_ and _Compile schema_ actions. See the command line help for more details.

# Command line help

It is possible to obtain command line help by using a `-h` or `--help` argument. A general help is displayed when no 
action is specified, and an action-specific help is displayed when using `-h` together with action. For reference, 
the command line help is included here.

## General help

```
#generate:GENERAL
```

## Compile Mindcode action help

```
#generate:COMPILE_MINDCODE
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

[« Previous: Schemacode](SCHEMACODE.markdown) &nbsp; | &nbsp;
[Next: Schematics Refresher »](TOOLS-REFRESHER.markdown)
