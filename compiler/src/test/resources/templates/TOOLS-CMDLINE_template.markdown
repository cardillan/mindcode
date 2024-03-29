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
* `cs` or `compile-schema`: builds a schematic from Schemacode source into a binary `.msch` file.
* `ds` or `decompile-schema`: decompiles a binary `.msch` file to Schemacode source.

## Input/output files

All actions take the name of input file and the name of output file as an argument. When the given input or output is 
a text file, the argument is optional and when not specified, the standard input/output is used. Use `-` to explicitly 
specify standard input or output for input or output file.  

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
  as new schematics on the Schematics screen, by using **Import schematics/Import from clipboard** command. 

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

## Compile Schema action help

```
#generate:COMPILE_SCHEMA
```

## Decompile Schema action help

```
#generate:DECOMPILE_SCHEMA
```

---

[« Previous: Schemacode](SCHEMACODE.markdown) &nbsp; | &nbsp;
[Next: Schematics Refresher »](TOOLS-REFRESHER.markdown)
