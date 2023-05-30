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
usage: mindcode [-h] ACTION ...

Mindcode/Schemacode command-line compiler.

named arguments:
  -h, --help             show this help message and exit

Actions:
  Specifies the compilation type to be performed

  ACTION                 Type of compilation
    cm (compile-mindcode)
                         Compile a mindcode source file into text mlog file.
    cs (compile-schema)  Compile a schema definition file into binary msch file.
    ds (decompile-schema)
                         Decompile a binary msch file into schema definition file.
```

## Compile Mindcode action help

```
usage: mindcode cm [-h] [-c] [-l [LOG]] [-o LEVEL] [--jump-normalization LEVEL] [--dead-code-elimination LEVEL]
                [--single-step-elimination LEVEL] [--output-temp-elimination LEVEL] [--expression-optimization LEVEL]
                [--ease-expression-optimization LEVEL] [--conditionals-optimization LEVEL] [--jump-straightening LEVEL]
                [--loop-optimization LEVEL] [--if-expression-optimization LEVEL] [--jump-threading LEVEL]
                [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL] [--function-call-optimization LEVEL]
                [--return-value-optimization LEVEL] [--input-temp-elimination LEVEL] [--print-merging LEVEL]
                [-t {6,7s,7w,7as,7aw}] [-g {SIZE,SPEED,AUTO}] [-p {0..2}] [-d {0..3}]
                [-u [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]] [-s] [input] [output]

Compile a mindcode source file into text mlog file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        paste compiled mlog code into clipboard
  -t, --target {6,7s,7w,7as,7aw}
                         selects target processor version and edition (version  6,  version 7 with standard processor or
                         world processor, version 7 rev. A with standard processor or world processor)
  -g, --goal {SIZE,SPEED,AUTO}
                         sets  code  generation  goal:  minimize  code   size,   minimize  execution  speed,  or  choose
                         automatically

input/output files:
  input                  Mindcode file to be compiled into an mlog file; uses stdin when not specified.
  output                 Output file to receive compiled  mlog  code;  uses  input  file  with  .mlog extension when not
                         specified, or stdout when input is stdin. Use "-" to force stdout output.
  -l, --log [LOG]        Output file to receive compiler messages; uses input  file  with .log extension when no file is
                         specified.

optimization levels:
  Options to specify global  and  individual  optimization  levels.  Individual  optimizers  use  global  level when not
  explicitly set. Available optimization levels are {off,basic,aggressive}.

  -o, --optimization LEVEL
                         sets global optimization level for all optimizers
  --jump-normalization LEVEL
                         optimization level  of  replacing  always  true  conditional  jumps  with  unconditional  ones,
                         removing always false jumps
  --dead-code-elimination LEVEL
                         optimization level of eliminating writes to  compiler-  or  user-defined variables that are not
                         used
  --single-step-elimination LEVEL
                         optimization level of eliminating jumps to the next instruction
  --output-temp-elimination LEVEL
                         optimization  level  of  eliminating  temporary  variables   created  to  extract  values  from
                         instructions
  --expression-optimization LEVEL
                         optimization level of optimizing some common mathematical expressions
  --ease-expression-optimization LEVEL
                         optimization level of eliminating temporary variables created to execute case expressions
  --conditionals-optimization LEVEL
                         optimization level of  merging  an  op  instruction  producing  a  boolean  expression into the
                         following conditional jump
  --jump-straightening LEVEL
                         optimization level of simplifying sequences of intertwined jumps
  --loop-optimization LEVEL
                         optimization level of improving loops
  --if-expression-optimization LEVEL
                         optimization level of improving ternary/if expressions
  --jump-threading LEVEL
                         optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         optimization level of  eliminating  instructions  made  unreachable  by  optimizations or false
                         conditions
  --stack-optimization LEVEL
                         optimization level of optimizing variable storage on stack
  --function-call-optimization LEVEL
                         optimization level of optimizing passing arguments to functions
  --return-value-optimization LEVEL
                         optimization level of optimizing passing return values from functions
  --input-temp-elimination LEVEL
                         optimization level of eliminating temporary variables created to pass values into instructions
  --print-merging LEVEL  optimization level of merging consecutive print statements outputting text literals

debug output options:
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  -u, --print-unresolved [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]
                         activates output of the unresolved code (before virtual instructions resolution) of given type
  -s, --stacktrace       prints stack trace into stderr when an exception occurs
```

## Compile Schema action help

```
usage: mindcode cs [-h] [-c] [-l [LOG]] [-o LEVEL] [--jump-normalization LEVEL] [--dead-code-elimination LEVEL]
                [--single-step-elimination LEVEL] [--output-temp-elimination LEVEL] [--expression-optimization LEVEL]
                [--ease-expression-optimization LEVEL] [--conditionals-optimization LEVEL] [--jump-straightening LEVEL]
                [--loop-optimization LEVEL] [--if-expression-optimization LEVEL] [--jump-threading LEVEL]
                [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL] [--function-call-optimization LEVEL]
                [--return-value-optimization LEVEL] [--input-temp-elimination LEVEL] [--print-merging LEVEL]
                [-t {6,7s,7w,7as,7aw}] [-g {SIZE,SPEED,AUTO}] [-p {0..2}] [-d {0..3}]
                [-u [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]] [-s] [-a TAG [TAG ...]] [input] [output]

Compile a schema definition file into binary msch file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        encode schematics into text representation and paste into clipboard
  -t, --target {6,7s,7w,7as,7aw}
                         selects target processor version and edition (version  6,  version 7 with standard processor or
                         world processor, version 7 rev. A with standard processor or world processor)
  -g, --goal {SIZE,SPEED,AUTO}
                         sets  code  generation  goal:  minimize  code   size,   minimize  execution  speed,  or  choose
                         automatically

input/output files:
  input                  Schema definition file to be compiled into a binary msch file.
  output                 Output file to receive binary Mindustry schema (msch).
  -l, --log [LOG]        output file to receive compiler messages; uses stdout/stderr when not specified
  -a, --add-tag TAG [TAG ...]
                         defines additional tag(s) to add to  the  schematics,  plain  text  and symbolic icon names are
                         supported

optimization levels:
  Options to specify global  and  individual  optimization  levels.  Individual  optimizers  use  global  level when not
  explicitly set. Available optimization levels are {off,basic,aggressive}.

  -o, --optimization LEVEL
                         sets global optimization level for all optimizers
  --jump-normalization LEVEL
                         optimization level  of  replacing  always  true  conditional  jumps  with  unconditional  ones,
                         removing always false jumps
  --dead-code-elimination LEVEL
                         optimization level of eliminating writes to  compiler-  or  user-defined variables that are not
                         used
  --single-step-elimination LEVEL
                         optimization level of eliminating jumps to the next instruction
  --output-temp-elimination LEVEL
                         optimization  level  of  eliminating  temporary  variables   created  to  extract  values  from
                         instructions
  --expression-optimization LEVEL
                         optimization level of optimizing some common mathematical expressions
  --ease-expression-optimization LEVEL
                         optimization level of eliminating temporary variables created to execute case expressions
  --conditionals-optimization LEVEL
                         optimization level of  merging  an  op  instruction  producing  a  boolean  expression into the
                         following conditional jump
  --jump-straightening LEVEL
                         optimization level of simplifying sequences of intertwined jumps
  --loop-optimization LEVEL
                         optimization level of improving loops
  --if-expression-optimization LEVEL
                         optimization level of improving ternary/if expressions
  --jump-threading LEVEL
                         optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         optimization level of  eliminating  instructions  made  unreachable  by  optimizations or false
                         conditions
  --stack-optimization LEVEL
                         optimization level of optimizing variable storage on stack
  --function-call-optimization LEVEL
                         optimization level of optimizing passing arguments to functions
  --return-value-optimization LEVEL
                         optimization level of optimizing passing return values from functions
  --input-temp-elimination LEVEL
                         optimization level of eliminating temporary variables created to pass values into instructions
  --print-merging LEVEL  optimization level of merging consecutive print statements outputting text literals

debug output options:
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  -u, --print-unresolved [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]
                         activates output of the unresolved code (before virtual instructions resolution) of given type
  -s, --stacktrace       prints stack trace into stderr when an exception occurs
```

## Decompile Schema action help

```
usage: mindcode ds [-h] [-p] [-P] [-c] [-C] [-l] [-L] [-s {ORIGINAL,HORIZONTAL,VERTICAL}]
                [-d {ROTATABLE,NON_DEFAULT,ALL}] input [output]

Decompile a binary msch file into schema definition file.

positional arguments:
  input                  Mindustry schema file to be decompiled into Schema Definition File.
  output                 Output file to receive compiled mlog  code;  uses  input  file  name with .sdf extension if not
                         specified.

named arguments:
  -h, --help             show this help message and exit
  -p, --relative-positions
                         use relative coordinates for block positions where possible
  -P, --absolute-positions
                         use absolute coordinates for block positions
  -c, --relative-connections
                         use relative coordinates for connections
  -C, --absolute-connections
                         use absolute coordinates for connections
  -l, --relative-links   use relative coordinates for processor links
  -L, --absolute-links   use absolute coordinates for processor links
  -s, --sort-order {ORIGINAL,HORIZONTAL,VERTICAL}
                         specifies how to order blocks in the decompiled schema definition file
  -d, --direction {ROTATABLE,NON_DEFAULT,ALL}
                         specifies when to include  direction  clause  in  decompiled  schema  definition file: only for
                         blocks affected by rotation, only for block with non-default direction, or for all blocks
```

---

[« Previous: Schemacode](SCHEMACODE.markdown) &nbsp; | &nbsp;
[Next: Schematics Refresher »](TOOLS-REFRESHER.markdown)
