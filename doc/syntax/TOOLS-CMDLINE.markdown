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

## Input/output files

All actions take the name of input file and the name of output file as an argument. When the given input or output is a text file, the argument is optional and when not specified, the standard input/output is used. Use `-` to explicitly specify standard input or output for input or output file.

### Input file excerpt

Mindcode accepts the `--excerpt` command line option followed by a `line:column-line:column` specification of the portion of the input file to load for compiling (both line and column indexes starting at 1). For example, `--excerpt 8:5-15:17` selects text starting at line 8, column 5 and ending at line 15, column 17 from the input file. Some IDEs can be configured to produce these values corresponding to the selected text, giving teh ability to compile just the selected text using Mindcode.

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
usage: mindcode [-h] [-v] ACTION ...

Mindcode/Schemacode command-line compiler.

named arguments:
  -h, --help             show this help message and exit
  -v, --version          show program's version number and exit

Actions:
  Specifies the type of processing to be performed

  ACTION                 Type of processing
    cm (compile-mindcode)
                         Compile a Mindcode source file into text mlog file.
    dm (decompile-mlog)  Decompile a text  mlog  file  into  Mindcode  source,  leaving  jumps  and unknown instructions
                         unresolved.
    cs (compile-schema,compile-schematic)
                         Compile a schematic definition file into binary msch file.
    ds (decompile-schema,decompile-schematic)
                         Decompile a binary msch file into schematic definition file.
```

## Compile Mindcode action help

```
usage: mindcode cm [-h] [-c] [-w] [--watcher-port {0..65535}] [--watcher-timeout {0..3600000}] [--excerpt [EXCERPT]]
                [-l [LOG]] [-a FILE [FILE ...]] [-y {STRICT,MIXED,RELAXED}]
                [-t {6,6.0,7,7.0,7.0w,7.1,7.1w,7w,8,8.0,8.0w,8w}] [-i {1..100000}] [-e {1..1000}] [-g {SIZE,SPEED,AUTO}]
                [-r {NONE,PASSIVE,ACTIVE}] [--function-prefix {short,long}] [--link-guards {true,false}]
                [--boundary-checks {NONE,ASSERT,MINIMAL,SIMPLE,DESCRIBED}] [--printflush {true,false}]
                [--sort-variables [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} ...]]]
                [--no-signature] [--run] [--run-steps {1..1000000000}] [--trace-execution {true,false}]
                [--dump-variables-on-stop {true,false}] [--stop-on-stop-instruction {true,false}]
                [--stop-on-end-instruction {true,false}] [--stop-on-program-end {true,false}]
                [--err-invalid-counter {true,false}] [--err-invalid-identifier {true,false}]
                [--err-unsupported-opcode {true,false}] [--err-uninitialized-var {true,false}]
                [--err-assignment-to-fixed-var {true,false}] [--err-not-an-object {true,false}]
                [--err-not-a-number {true,false}] [--err-invalid-content {true,false}] [--err-invalid-link {true,false}]
                [--err-memory-access {true,false}] [--err-unsupported-block-operation {true,false}]
                [--err-text-buffer-overflow {true,false}] [--err-invalid-format {true,false}]
                [--err-graphics-buffer-overflow {true,false}] [--err-runtime-check-failed {true,false}] [-o LEVEL]
                [--temp-variables-elimination LEVEL] [--case-expression-optimization LEVEL]
                [--dead-code-elimination LEVEL] [--jump-normalization LEVEL] [--jump-optimization LEVEL]
                [--single-step-elimination LEVEL] [--expression-optimization LEVEL] [--if-expression-optimization LEVEL]
                [--data-flow-optimization LEVEL] [--loop-hoisting LEVEL] [--loop-optimization LEVEL]
                [--loop-unrolling LEVEL] [--function-inlining LEVEL] [--case-switching LEVEL]
                [--return-optimization LEVEL] [--jump-straightening LEVEL] [--jump-threading LEVEL]
                [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL] [--print-merging LEVEL] [-p {0..2}]
                [-d {0..3}] [-u [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]] [-s] [input] [output]

Compile a Mindcode source file into text mlog file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        copy compiled mlog code to clipboard
  -w, --watcher          send compiled mlog code to the Mlog Watcher  mod  in  Mindustry (the code will be injected into
                         the selected processor)
  --watcher-port {0..65535}
                         port number for communication with Mlog Watcher
  --watcher-timeout {0..3600000}
                         timeout in milliseconds when trying to establish a connection to Mlog Watcher

input/output files:
  input                  Mindcode file to be compiled into an mlog file; uses stdin when not specified
  output                 Output file to receive compiled  mlog  code;  uses  input  file  with  .mlog extension when not
                         specified, or stdout when input is stdin. Use "-" to force stdout output.
  --excerpt [EXCERPT]    Allows to specify a portion of the  input  file  as  input, parts outside the specified excerpt
                         are ignored. The excerpt needs to  be  specified as 'line:column-line:column' (':column' may be
                         omitted if it is equal to 1), giving  two  positions  inside the main input file separated by a
                         dash. The start position must precede the end position.
  -l, --log [LOG]        Output file to receive compiler messages; uses input  file  with .log extension when no file is
                         specified.
  -a, --append FILE [FILE ...]
                         Additional Mindcode source file to  be  compiled  along  with  the  input file. Such additional
                         files may contain common functions. More  than  one  file  may  be  added this way. The excerpt
                         argument isn't applied to additional files.

compiler options:
  -y, --syntax {STRICT,MIXED,RELAXED}
                         specifies syntactic mode used to compile the source code
  -t, --target {6,6.0,7,7.0,7.0w,7.1,7.1w,7w,8,8.0,8.0w,8w}
                         selects target processor version and edition ('w' suffix specifies the world processor)
  -i, --instruction-limit {1..100000}
                         sets the maximal number of instructions for the speed optimizations
  -e, --passes {1..1000}
                         sets maximal number of optimization passes to be made
  -g, --goal {SIZE,SPEED,AUTO}
                         sets  code  generation  goal:  minimize  code   size,   minimize  execution  speed,  or  choose
                         automatically
  -r, --remarks {NONE,PASSIVE,ACTIVE}
                         controls remarks  propagation  to  the  compiled  code:  none  (remarks  are  removed), passive
                         (remarks are not executed), or active (remarks are printed)
  --function-prefix {short,long}
                         specifies the how the function prefix of  local  variables  is generated (either a short common
                         prefix for all functions, or a potentially long prefix derived from function name)
  --link-guards {true,false}
                         when set to true,  generates  code  to  ensure  each  declared  linked  block  is linked to the
                         processor before the program runs
  --boundary-checks {NONE,ASSERT,MINIMAL,SIMPLE,DESCRIBED}
                         governs the runtime checks generated by compiler to  catch indexes out of bounds when accessing
                         internal array elements
  --printflush {true,false}
                         when set to true, automatically  adds  a  'printflush  message'  instruction  at the end of the
                         program if one is missing
  --sort-variables [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} ...]]
                         prepends the final  code  with  instructions  which  ensure  variables  are  created inside the
                         processor in a defined order. The variables are  sorted according to their categories in order,
                         and then alphabetically.  Category ALL  represents  all remaining, not-yet processed variables.
                         When --sort-variables is given  without  specifying  any  category,  LINKED PARAMS GLOBALS MAIN
                         LOCALS are used.
  --no-signature         prevents appending a signature "Compiled  by  Mindcode  - github.com/cardillan/mindcode" at the
                         end of the final code

run options:
  Options to specify if and how to  run  the  compiled  code  on  an  emulated processor. The emulated processor is much
  faster than Mindustry processors, but can't run instructions  which  obtain information from the Mindustry World. Sole
  exceptions are memory cells (cell1 to cell9) and memory banks (bank1 to bank9), which can be read and written.

  --run                  run the compiled code on an emulated processor.
  --run-steps {1..1000000000}
                         the maximum number of instruction executions  to  emulate,  the execution stops when this limit
                         is reached.
  --trace-execution {true,false}
                         output instruction and variable states at each execution step
  --dump-variables-on-stop {true,false}
                         output variable values when the 'stop' instruction is encountered
  --stop-on-stop-instruction {true,false}
                         stop execution when the 'stop' instruction is encountered
  --stop-on-end-instruction {true,false}
                         stop execution when the 'end' instruction is encountered
  --stop-on-program-end {true,false}
                         stop execution when the end of instruction list is reached
  --err-invalid-counter {true,false}
                         stop execution when an invalid value is written to '@counter'
  --err-invalid-identifier {true,false}
                         stop execution when a malformed identifier or value is encountered
  --err-unsupported-opcode {true,false}
                         stop execution when unsupported instruction is encountered
  --err-uninitialized-var {true,false}
                         stop execution when an uninitialized variable is used in internal processing
  --err-assignment-to-fixed-var {true,false}
                         stop execution on attempts to write a value to an unmodifiable built-in variable
  --err-not-an-object {true,false}
                         stop execution when a numeric value is used instead of an object
  --err-not-a-number {true,false}
                         stop execution when an object is used instead of a numeric value
  --err-invalid-content {true,false}
                         stop execution when an invalid index is used in the 'lookup' instruction
  --err-invalid-link {true,false}
                         stop execution when an invalid index is used in the 'getlink' instruction
  --err-memory-access {true,false}
                         stop execution when accessing invalid memory-cell or memory-bank index 
  --err-unsupported-block-operation {true,false}
                         stop execution when attempting to perform an unsupported operation on a block
  --err-text-buffer-overflow {true,false}
                         stop execution when the text buffer size (400 characters) is exceeded
  --err-invalid-format {true,false}
                         stop execution when no placeholder for the 'format' instruction exists in the buffer
  --err-graphics-buffer-overflow {true,false}
                         stop execution when the graphics buffer size (256 operations) is exceeded
  --err-runtime-check-failed {true,false}
                         stop execution when a runtime check fails.

optimization levels:
  Options to specify global  and  individual  optimization  levels.  Individual  optimizers  use  global  level when not
  explicitly set. Available optimization levels are {none,basic,advanced}.

  -o, --optimization LEVEL
                         sets global optimization level for all optimizers
  --temp-variables-elimination LEVEL
                         optimization  level  of  eliminating  temporary  variables   created  to  extract  values  from
                         instructions
  --case-expression-optimization LEVEL
                         optimization level of eliminating temporary variables created to execute case expressions
  --dead-code-elimination LEVEL
                         optimization level of eliminating writes to  compiler-  or  user-defined variables that are not
                         used
  --jump-normalization LEVEL
                         optimization level  of  replacing  always  true  conditional  jumps  with  unconditional  ones,
                         removing always false jumps
  --jump-optimization LEVEL
                         optimization level of  merging  an  op  instruction  producing  a  boolean  expression into the
                         following conditional jump
  --single-step-elimination LEVEL
                         optimization level of eliminating jumps to the next instruction
  --expression-optimization LEVEL
                         optimization level of optimizing some common mathematical expressions
  --if-expression-optimization LEVEL
                         optimization level of improving ternary/if expressions
  --data-flow-optimization LEVEL
                         optimization level of improving variable assignments and and expressions
  --loop-hoisting LEVEL  optimization level of moving invariant code out of loops
  --loop-optimization LEVEL
                         optimization level of improving loops
  --loop-unrolling LEVEL
                         optimization level of unrolling  loops  with  constant  number  of iterations (optimization for
                         speed)
  --function-inlining LEVEL
                         optimization level of inlining stackless function calls (optimization for speed)
  --case-switching LEVEL
                         optimization level of modifying suitable case expressions to use jump tables
  --return-optimization LEVEL
                         optimization level of speeding up return statements in recursive and stackless functions
  --jump-straightening LEVEL
                         optimization level of simplifying sequences of intertwined jumps
  --jump-threading LEVEL
                         optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         optimization level of  eliminating  instructions  made  unreachable  by  optimizations or false
                         conditions
  --stack-optimization LEVEL
                         optimization level of optimizing variable storage on stack
  --print-merging LEVEL  optimization level of merging consecutive print statements outputting text literals

debug output options:
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  -u, --print-unresolved [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]
                         activates output of the unresolved code (before  virtual instructions resolution) of given type
                         (instruction numbers are included in the output)
  -s, --stacktrace       prints stack trace into stderr when an exception occurs
```

## Decompile Mlog action help

```
usage: mindcode dm [-h] input [output]

Partially decompile a text mlog file into Mindcode source file.

positional arguments:
  input                  Mlog text file to be decompiled into Mindcode source file.
  output                 Output file to receive decompiled Mindcode; uses  input  file  name with .dmnd extension if not
                         specified.

named arguments:
  -h, --help             show this help message and exit
```

## Compile Schematic action help

```
usage: mindcode cs [-h] [-c] [-l [LOG]] [-a TAG [TAG ...]] [-y {STRICT,MIXED,RELAXED}]
                [-t {6,6.0,7,7.0,7.0w,7.1,7.1w,7w,8,8.0,8.0w,8w}] [-i {1..100000}] [-e {1..1000}] [-g {SIZE,SPEED,AUTO}]
                [-r {NONE,PASSIVE,ACTIVE}] [--function-prefix {short,long}] [--link-guards {true,false}]
                [--boundary-checks {NONE,ASSERT,MINIMAL,SIMPLE,DESCRIBED}] [--printflush {true,false}]
                [--sort-variables [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} ...]]]
                [--no-signature] [-o LEVEL] [--temp-variables-elimination LEVEL] [--case-expression-optimization LEVEL]
                [--dead-code-elimination LEVEL] [--jump-normalization LEVEL] [--jump-optimization LEVEL]
                [--single-step-elimination LEVEL] [--expression-optimization LEVEL] [--if-expression-optimization LEVEL]
                [--data-flow-optimization LEVEL] [--loop-hoisting LEVEL] [--loop-optimization LEVEL]
                [--loop-unrolling LEVEL] [--function-inlining LEVEL] [--case-switching LEVEL]
                [--return-optimization LEVEL] [--jump-straightening LEVEL] [--jump-threading LEVEL]
                [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL] [--print-merging LEVEL] [-p {0..2}]
                [-d {0..3}] [-u [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]] [-s] [input] [output]

Compile a schematic definition file into binary msch file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        encode the created schematic into text representation and paste into clipboard

input/output files:
  input                  Schematic definition file to be compiled into a binary msch file.
  output                 Output file to receive the resulting binary Mindustry schematic file (.msch).
  -l, --log [LOG]        output file to receive compiler messages; uses stdout/stderr when not specified

schematic creation:
  -a, --add-tag TAG [TAG ...]
                         defines additional tag(s) to add  to  the  schematic,  plain  text  and symbolic icon names are
                         supported

compiler options:
  -y, --syntax {STRICT,MIXED,RELAXED}
                         specifies syntactic mode used to compile the source code
  -t, --target {6,6.0,7,7.0,7.0w,7.1,7.1w,7w,8,8.0,8.0w,8w}
                         selects target processor version and edition ('w' suffix specifies the world processor)
  -i, --instruction-limit {1..100000}
                         sets the maximal number of instructions for the speed optimizations
  -e, --passes {1..1000}
                         sets maximal number of optimization passes to be made
  -g, --goal {SIZE,SPEED,AUTO}
                         sets  code  generation  goal:  minimize  code   size,   minimize  execution  speed,  or  choose
                         automatically
  -r, --remarks {NONE,PASSIVE,ACTIVE}
                         controls remarks  propagation  to  the  compiled  code:  none  (remarks  are  removed), passive
                         (remarks are not executed), or active (remarks are printed)
  --function-prefix {short,long}
                         specifies the how the function prefix of  local  variables  is generated (either a short common
                         prefix for all functions, or a potentially long prefix derived from function name)
  --link-guards {true,false}
                         when set to true,  generates  code  to  ensure  each  declared  linked  block  is linked to the
                         processor before the program runs
  --boundary-checks {NONE,ASSERT,MINIMAL,SIMPLE,DESCRIBED}
                         governs the runtime checks generated by compiler to  catch indexes out of bounds when accessing
                         internal array elements
  --printflush {true,false}
                         when set to true, automatically  adds  a  'printflush  message'  instruction  at the end of the
                         program if one is missing
  --sort-variables [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} [{LINKED,PARAMS,GLOBALS,MAIN,LOCALS,ALL,NONE} ...]]
                         prepends the final  code  with  instructions  which  ensure  variables  are  created inside the
                         processor in a defined order. The variables are  sorted according to their categories in order,
                         and then alphabetically.  Category ALL  represents  all remaining, not-yet processed variables.
                         When --sort-variables is given  without  specifying  any  category,  LINKED PARAMS GLOBALS MAIN
                         LOCALS are used.
  --no-signature         prevents appending a signature "Compiled  by  Mindcode  - github.com/cardillan/mindcode" at the
                         end of the final code

optimization levels:
  Options to specify global  and  individual  optimization  levels.  Individual  optimizers  use  global  level when not
  explicitly set. Available optimization levels are {none,basic,advanced}.

  -o, --optimization LEVEL
                         sets global optimization level for all optimizers
  --temp-variables-elimination LEVEL
                         optimization  level  of  eliminating  temporary  variables   created  to  extract  values  from
                         instructions
  --case-expression-optimization LEVEL
                         optimization level of eliminating temporary variables created to execute case expressions
  --dead-code-elimination LEVEL
                         optimization level of eliminating writes to  compiler-  or  user-defined variables that are not
                         used
  --jump-normalization LEVEL
                         optimization level  of  replacing  always  true  conditional  jumps  with  unconditional  ones,
                         removing always false jumps
  --jump-optimization LEVEL
                         optimization level of  merging  an  op  instruction  producing  a  boolean  expression into the
                         following conditional jump
  --single-step-elimination LEVEL
                         optimization level of eliminating jumps to the next instruction
  --expression-optimization LEVEL
                         optimization level of optimizing some common mathematical expressions
  --if-expression-optimization LEVEL
                         optimization level of improving ternary/if expressions
  --data-flow-optimization LEVEL
                         optimization level of improving variable assignments and and expressions
  --loop-hoisting LEVEL  optimization level of moving invariant code out of loops
  --loop-optimization LEVEL
                         optimization level of improving loops
  --loop-unrolling LEVEL
                         optimization level of unrolling  loops  with  constant  number  of iterations (optimization for
                         speed)
  --function-inlining LEVEL
                         optimization level of inlining stackless function calls (optimization for speed)
  --case-switching LEVEL
                         optimization level of modifying suitable case expressions to use jump tables
  --return-optimization LEVEL
                         optimization level of speeding up return statements in recursive and stackless functions
  --jump-straightening LEVEL
                         optimization level of simplifying sequences of intertwined jumps
  --jump-threading LEVEL
                         optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         optimization level of  eliminating  instructions  made  unreachable  by  optimizations or false
                         conditions
  --stack-optimization LEVEL
                         optimization level of optimizing variable storage on stack
  --print-merging LEVEL  optimization level of merging consecutive print statements outputting text literals

debug output options:
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  -u, --print-unresolved [{PLAIN,FLAT_AST,DEEP_AST,SOURCE}]
                         activates output of the unresolved code (before  virtual instructions resolution) of given type
                         (instruction numbers are included in the output)
  -s, --stacktrace       prints stack trace into stderr when an exception occurs
```

## Decompile Schematic action help

```
usage: mindcode ds [-h] [-p] [-P] [-c] [-C] [-l] [-L] [-s {ORIGINAL,HORIZONTAL,VERTICAL}]
                [-d {ROTATABLE,NON_DEFAULT,ALL}] input [output]

Decompile a binary msch file into schematic definition file.

positional arguments:
  input                  Mindustry schematic file to be decompiled into Schematic Definition File.
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
                         specifies how to order blocks in the decompiled schematic definition file
  -d, --direction {ROTATABLE,NON_DEFAULT,ALL}
                         specifies when to include direction clause  in  decompiled  schematic definition file: only for
                         blocks affected by rotation, only for block with non-default direction, or for all blocks
```

---

[« Previous: IDE Integration](TOOLS-IDE-INTEGRATION.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Processor emulator »](TOOLS-PROCESSOR-EMULATOR.markdown)
