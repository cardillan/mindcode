# Command line tool

After building the project, an executable jar file is created at `compiler\target\mindcode.jar`. In the bin directory, there's a Linux script file `mindcode` and a Windows batch file `mindcode.bat`, which can be used to run the command line tool.

* Linux `mindcode` file requires Java 22 executable to be on the path.
* Windows `mindcode.bat` file requires the `JAVA_HOME` variable to point to Java 22 installation.

# Using the command line tool

The command line tool supports three different actions. The action is specified by the first command line argument, which must be one of the following:

* `cm` or `compile-mindcode`: compiles a Mindcode source to mlog.
* `dm` or `decompile-mlog`: partially decompiles an mlog code into Mindcode. The resulting code needs to be manually edited to create loops and conditions present in the original mlog.
* `cs` or `compile-schematic`: builds a schematic from a Schemacode source into a binary `.msch` file.
* `ds` or `decompile-schematic`: decompiles a binary `.msch` file to a Schemacode source.

Command-line arguments (e.g., `--remarks`) are case-sensitive. Values of command-line options (e.g., `none`) are generally case-insensitive. It is possible to use both `--remarks none` and `--remarks NONE`, although the lower-case specification is preferred. All multi-word command-ine options use _kebab-case_ convention. 

## Input/output files

All actions take the name of an input file as an argument. The output file needs to be specified using the `-o` option. When the given input or output is a text file, the argument is optional, and when not specified, the standard input/output is used. Use `-` to explicitly specify standard input or output for an input or output file.

### Input file excerpt

When performing the _Compile Mindcode_ action, Mindcode accepts the `--excerpt` command line option followed by a `line:column-line:column` specification of the portion of the input file to load for compiling (both line and column indexes starting at 1). For example, `--excerpt 8:5-15:17` selects text starting at line 8, column 5 and ending at line 15, column 17 from the input file. Some IDEs can be configured to produce these values corresponding to the selected text, giving the ability to compile just the selected text using Mindcode.

The `--excerpt` option is applied only to the main input file, not to the files added through the `--append` command line option or `require` directive.

### Additional input files

When performing the _Compile Mindcode_ action, it is possible to use the `-a` or `--append` command line parameter to specify additional source files to be compiled along with the input file. The source files are parsed separately, and error messages that may be generated during the compilation include the name of the file where the error occurred.

The `--append` command-line option has the same effect as the [`require` statement](SYNTAX.markdown#libraries-and-external-files).

## Log file

The `-l` argument can be used to specify a log file, a file which receives messages generated while running the tool. When such a file isn't specified, the standard output is used (if standard output is already used for the output file, messages are written to the standard error instead).

## Mlog Watcher integration

The command-line tool can send the compiled code directly into a processor in a running Mindustry game through the Mlog Watcher mod. See [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown) for details.

## Clipboard integration

When compiling Mindcode or building a schematic, the `-c` or `--clipboard` argument can be used. In this case, if the action was successful, the output is copied to the clipboard and can be pasted directly into Mindustry:

* _Compile Mindcode_ action: clipboard contains mlog instructions which can be pasted into a processor on the processor configuration screen, by using **Edit/Import from clipboard** command.
* _Compile Schematic_ action: clipboard contains a schematic encoded into Mindustry Schematic string, which can be pasted as a new schematic on the Schematics screen, by using **Import schematic.../Import from clipboard** command. 

## Running the compiled code

When performing the _Compile Mindcode_ action, it is possible to use the `--run` command line option to run the resulting mlog code on an emulated processor. The processor is much faster than Mindustry Logic processors but supports only very few operations that interact with the Mindustry World.

The behavior of the processor emulator can be further modified through the execution flags. For more details, refer to the [processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown) documentation.

## Compiler options

Compiler options, such as target Mindustry Logic version and compiler optimizations, can be specified for _Compile Mindcode_ and _Compile schema_ actions. See the command line help for more details.

# Command line help

It is possible to display the command line help by using a `-h` or `--help` argument. A general help is displayed when no action is specified, and an action-specific help is displayed when using `-h` together with action. For reference, the command line help is included here.

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
    dm (decompile-mlog,process-mlog)
                         Load and process mlog code from a file or an in-game processor.
    cs (compile-schema,compile-schematic)
                         Compile a schematic definition file into binary msch file.
    ds (decompile-schema,decompile-schematic)
                         Decompile a binary msch file into schematic definition file.
```

## Compile Mindcode action help

```
usage: mindcode cm [-h] [-c] [-w [{update,update-all,upgrade-all,force-update-all}]] [--watcher-version {v0,v1}]
                [--watcher-port {0..65535}] [--watcher-timeout {0..3600000}] [--excerpt [EXCERPT]] [-o [OUTPUT]]
                [-l [LOG]] [--output-directory OUTPUT-DIRECTORY] [--file-references {path,uri,windows-uri}]
                [-a FILE [FILE ...]] [-t {6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}] [-i {1..100000}]
                [--builtin-evaluation {none,compatible,full}] [--null-counter-is-noop {true,false}]
                [--symbolic-labels [{true,false}]] [--mlog-indent {0..8}] [--no-argument-padding [{true,false}]]
                [--function-prefix {short,long}] [--author author [author ...]] [--no-signature]
                [--processor-id processor_ID] [--program-name program_name] [--program-version program_version]
                [--encode-zero-characters [{true,false}]] [-y {strict,mixed,relaxed}] [--target-guard [{true,false}]]
                [--setrate {1..1000}] [--ipt {1..1000}] [--volatile-atomic {true,false}]
                [--boundary-checks {true,false}] [--emulate-strict-not-equal {true,false}]
                [--enforce-instruction-limit {true,false}] [--error-function {true,false}]
                [--error-reporting {none,assert,minimal,simple,described}] [-r {none,comments,passive,active}]
                [--auto-printflush {true,false}] [-g {size,neutral,speed}] [-e {1..1000}]
                [--unsafe-case-optimization [{true,false}]] [--case-optimization-strength {0..6}]
                [--mlog-block-optimization {true,false}] [--use-lookup-arrays {true,false}]
                [--use-short-arrays {true,false}] [--use-text-jump-tables {true,false}]
                [--use-text-translations {true,false}] [-O {0..4}] [--temp-variables-elimination LEVEL]
                [--case-expression-optimization LEVEL] [--dead-code-elimination LEVEL] [--jump-normalization LEVEL]
                [--condition-optimization LEVEL] [--single-step-elimination LEVEL] [--expression-optimization LEVEL]
                [--boolean-optimization LEVEL] [--if-expression-optimization LEVEL] [--data-flow-optimization LEVEL]
                [--instruction-reordering LEVEL] [--loop-hoisting LEVEL] [--loop-rotation LEVEL]
                [--loop-unrolling LEVEL] [--function-inlining LEVEL] [--array-optimization LEVEL]
                [--case-switching LEVEL] [--return-optimization LEVEL] [--jump-straightening LEVEL]
                [--jump-threading LEVEL] [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL]
                [--print-merging LEVEL]
                [--sort-variables [{linked,params,globals,main,locals,all,none} [{linked,params,globals,main,locals,all,none} ...]]]
                [-p {0..2}] [--debug [{true,false}]] [-d {0..3}] [--print-code-size {true,false}]
                [-u [{none,plain,flat-ast,deep-ast,source}]] [-s]
                [--emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]]
                [--emulator-fps {1.0..240.0}] [--run [{true,false}]] [--run-steps {0..1000000000}]
                [--output-profiling [{true,false}]] [--trace-execution {true,false}]
                [--dump-variables-on-stop {true,false}] [--stop-on-stop-instruction {true,false}]
                [--stop-on-end-instruction {true,false}] [--stop-on-program-end {true,false}]
                [--err-parse-error {true,false}] [--err-invalid-counter {true,false}]
                [--err-unsupported-opcode {true,false}] [--err-nonexistent-var {true,false}]
                [--err-assignment-to-fixed-var {true,false}] [--err-not-an-object {true,false}]
                [--err-not-a-number {true,false}] [--err-unknown-color {true,false}]
                [--err-invalid-character {true,false}] [--err-invalid-lookup {true,false}]
                [--err-invalid-link {true,false}] [--err-memory-access {true,false}] [--err-memory-object {true,false}]
                [--err-unsupported-block-operation {true,false}] [--err-text-buffer-overflow {true,false}]
                [--err-invalid-format {true,false}] [--err-graphics-buffer-overflow {true,false}]
                [--err-runtime-check-failed {true,false}] [input]

Compile a Mindcode source file into text mlog file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        copy compiled mlog code to clipboard
  -w, --watcher [{update,update-all,upgrade-all,force-update-all}]
                         invoke an specific Mlog Watcher operation on the compiled mlog code (default: update)
                             update           send it to the selected processor
                             update-all       send it to processors with the exact same version on the active map
                             upgrade-all      send it to processors with the same or lower version on the active map
                             force-update-all send it to processors matching program ID (regardless of the version)
  --watcher-version {v0,v1}
                         specifies the version of the Mlog Watcher mod
  --watcher-port {0..65535}
                         port number for communication with Mlog Watcher
  --watcher-timeout {0..3600000}
                         timeout in milliseconds when trying to establish a connection to Mlog Watcher

Input/output files:
  input                  Mindcode file to be compiled into an mlog file; uses stdin when not specified
  --excerpt [EXCERPT]    Allows to specify a portion  of  the  input  file  for  processing, parts outside the specified
                         excerpt are ignored. The excerpt needs  to be specified as 'line:column-line:column' (':column'
                         may be omitted if it is equal to 1),  giving two positions inside the main input file separated
                         by a dash. The start position must precede the end position.
  -o, --output [OUTPUT]  output file to receive compiled  mlog  code;  uses  input  file  with  .mlog extension when not
                         specified, or stdout when input is stdin. Use "-" to force stdout output.
  -l, --log [LOG]        output file to receive additional  processing  messages;  uses  input  file with .log extension
                         when no file is specified.
  --output-directory OUTPUT-DIRECTORY
                         specifies the directory where the output files will be placed.
  --file-references {path,uri,windows-uri}
                         specifies the format in which a reference to a  location  in a source file is output on console
                         and into the log
  -a, --append FILE [FILE ...]
                         Additional Mindcode source file to  be  compiled  along  with  the  input file. Such additional
                         files may contain common functions. More  than  one  file  may  be  added this way. The excerpt
                         argument isn't applied to additional files.

Environment options:
  Options to specify the target environment for the  code  being  compiled. This includes the Mindustry version, as well
  as prescribing which specific processor features may or may not be used.

  -t, --target {6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  -i, --instruction-limit {1..100000}
                         sets the maximal number of instructions for the speed optimizations
  --builtin-evaluation {none,compatible,full}
                         sets the level of compile-time evaluation of numeric builtin constants
  --null-counter-is-noop {true,false}
                         when active, Mindcode assumes assigning a 'null' to  '@counter' is ignored by the processor and
                         may produce code depending on this behavior

Mlog formatting options:
  Options determining how the mlog code  is  generated  and  formatted,  including  instructions generated to carry over
  specific information into the compiled program (such as signature or processor ID).

  --symbolic-labels [{true,false}]
                         generate symbolic labels for jump instructions where possible
  --mlog-indent {0..8}   the amount of indenting applied to logical blocks in the compiled mlog code
  --no-argument-padding [{true,false}]
                         when set, mlog instructions emitted  by  the  compiler  are  not  padded with zeroes for unused
                         arguments
  --function-prefix {short,long}
                         specifies the how the function prefix of  local  variables  is generated (either a short common
                         prefix for all functions, or a potentially long prefix derived from function name)
  --author author [author ...]
                         adds an author to  the  list  of  authors  which  is  then  output  similarly to the compiler's
                         signature
  --no-signature         prevents appending a signature 'Compiled  by  Mindcode  - github.com/cardillan/mindcode' at the
                         end of the final code
  --processor-id processor_ID
                         sets the processor ID to be stored in the compiled code
  --program-name program_name
                         sets the program name to be stored in the compiled code
  --program-version program_version
                         sets the program version to be stored in the compiled code
  --encode-zero-characters [{true,false}]
                         allow encoding zero characters into mlog string  literals (WARNING: the resulting code can't be
                         edited as a teext or copied/pasted to/from the clipboard!)

Compiler options:
  Options which affect the way the source code is compiled.

  -y, --syntax {strict,mixed,relaxed}
                         specifies syntactic mode used to compile the source code
  --target-guard [{true,false}]
                         generates guard code at  the  beginning  of  the  program  ensuring  the  processor runs in the
                         Mindustry version compatible with the 'target' and 'builtin-evaluation' options
  --setrate {1..1000}    generates a 'setrate' instruction with  the  specified  value  in  the initialization code, and
                         sets the IPT value for the compiler to use
  --ipt {1..1000}        sets the IPT value for the compiler to use without generating the 'setrate' instruction
  --volatile-atomic {true,false}
                         when active, nonvolatile variables are not protected by atomic blocks
  --boundary-checks {true,false}
                         activates or deactivates runtime checks  to  catch  indexes  being  out of range when accessing
                         arrays
  --emulate-strict-not-equal {true,false}
                         use the 'select' instruction to emulate jump with the !== (strict not equal) condition
  --enforce-instruction-limit {true,false}
                         generate compilation error when the instruction limit is exceeded
  --error-function {true,false}
                         activates or deactivates the  error()  function  (when  set  to  'false', error() function does
                         nothing)
  --error-reporting {none,assert,minimal,simple,described}
                         specifies the mechanism used by the compiler when generating runtime checks
  -r, --remarks {none,comments,passive,active}
                         controls remarks propagation  to  the  compiled  code:  none  (remarks  are  removed), comments
                         (included as mlog comments), passive  (included  as  'print'  but  not not executed), or active
                         (included as 'print' and printed)
  --auto-printflush {true,false}
                         automatically add a 'printflush message1'  instruction  at  the  end  of  the program if one is
                         missing

Optimization options:
  Options guiding the  overall  optimization  of  the  compiled  code  or  activating/deactivating specific optimization
  actions.

  -g, --goal {size,neutral,speed}
                         sets code generation  goal:  minimize  code  size,  minimize  execution  speed,  or no specific
                         preference
  -e, --passes {1..1000}
                         sets the number of optimization passes to perform
  --unsafe-case-optimization [{true,false}]
                         omits range checking of case expressions without an else branch during optimization
  --case-optimization-strength {0..6}
                         sets the strength of case switching optimization:  higher number means more case configurations
                         are considered, potentially producing a more efficient  code, at the cost of longer compilation
                         time
  --mlog-block-optimization {true,false}
                         allows (limited) optimization of code inside mlog blocks
  --use-lookup-arrays {true,false}
                         allows using the lookup mechanism for implementing internal arrays
  --use-short-arrays {true,false}
                         allows using specialized implementation of short arrays (2-4 elements)
  --use-text-jump-tables {true,false}
                         when active, generates jump  tables  by  encoding  instruction  addresses  into a single String
                         value, and uses a single 'read' instruction to  directly  set the counter to the target address
                         (target 8 or higher required)
  --use-text-translations {true,false}
                         allows using 'read' instruction to implement simple case expressions

Optimization levels:
  Options specifying the global and individual  optimization  levels.  Individual  optimizers  use global level when not
  explicitly set. Available optimization levels are {none, basic, advanced, experimental}.

  -O {0..4}              sets global optimization level for all optimizers
  --temp-variables-elimination LEVEL
                         sets the optimization level of eliminating  temporary  variables created to extract values from
                         instructions
  --case-expression-optimization LEVEL
                         sets the  optimization  level  of  eliminating  temporary  variables  created  to  execute case
                         expressions
  --dead-code-elimination LEVEL
                         sets the optimization level of eliminating  writes  to compiler- or user-defined variables that
                         are not used
  --jump-normalization LEVEL
                         sets the optimization level  of  replacing  always  true  conditional  jumps with unconditional
                         ones, removing always false jumps
  --condition-optimization LEVEL
                         sets the optimization level of merging  an  op  instruction producing a boolean expression into
                         the following conditional jump
  --single-step-elimination LEVEL
                         sets the optimization level of eliminating jumps to the next instruction
  --expression-optimization LEVEL
                         sets the optimization level of optimizing some common mathematical expressions
  --boolean-optimization LEVEL
                         sets the optimization level of simplifying  boolean  expressions and/or implementing them using
                         the 'select' instruction
  --if-expression-optimization LEVEL
                         sets the optimization level of improving ternary/if expressions
  --data-flow-optimization LEVEL
                         sets the optimization level of improving  variable  assignments and expressions, analyzing data
                         flow for other optimizations
  --instruction-reordering LEVEL
                         sets the optimization level of reordering  instructions to allow additional optimizations being
                         made (not available yet)
  --loop-hoisting LEVEL  sets the optimization level of moving invariant code out of loops
  --loop-rotation LEVEL  sets the optimization level of rotating a front loop condition to the bottom of the loop
  --loop-unrolling LEVEL
                         sets the optimization level of unrolling loops with a fixed number of iterations
  --function-inlining LEVEL
                         sets the optimization level of inlining stackless function calls
  --array-optimization LEVEL
                         sets the optimization level of finding optimal array-access implementation for internal arrays
  --case-switching LEVEL
                         sets the optimization level of modifying suitable case  expressions to use jump tables or value
                         translations
  --return-optimization LEVEL
                         sets the optimization  level  of  speeding  up  return  statements  in  recursive and stackless
                         functions
  --jump-straightening LEVEL
                         sets the optimization level of simplifying sequences of intertwined jumps
  --jump-threading LEVEL
                         sets the optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         sets the optimization level of  eliminating  instructions  made unreachable by optimizations or
                         false conditions
  --stack-optimization LEVEL
                         sets the optimization level of optimizing variable storage on the stack
  --print-merging LEVEL  sets the optimization level of merging consecutive print statements outputting constant values

Debugging options:
  Options to activate debugging features or additional output from the compiler.

  --sort-variables [{linked,params,globals,main,locals,all,none} [{linked,params,globals,main,locals,all,none} ...]]
                         prepends the final  code  with  instructions  that  ensure  variables  are  created  inside the
                         processor  in a well-defined order. The variables  are  sorted according to their categories in
                         order, and then  alphabetically.   Category  ALL  represents  all  remaining, not-yet processed
                         variables. When --sort-variables  is  given  without  specifying  any  category,  LINKED PARAMS
                         GLOBALS MAIN LOCALS are used.
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  --debug [{true,false}]
                         activates or deactivates generation of debug code
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  --print-code-size {true,false}
                         outputs final code size broken down by function
  -u, --print-unresolved [{none,plain,flat-ast,deep-ast,source}]
                         activates output of the unresolved code (before  virtual instructions resolution) of given type
                         (instruction numbers are included in the output)
  -s, --stacktrace       outputs a stack trace onto stderr when an unhandled exception occurs

Emulator options:
  Options to specify whether and how to run the code  or schematic in an emulated environment. The emulated processor is
  much faster than Mindustry processors, but can't run  instructions  which obtain information from the Mindustry World.
  Memory cells/banks and other processors can be read from or written  to if part of the schematic or, when running just
  the code, the default processor configuration provided by Mindcode.

  --emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  --emulator-fps {1.0..240.0}
                         the fps used by the emulator
  --run [{true,false}]   run the compiled code on an emulated processor
  --run-steps {0..1000000000}
                         the maximum number of instruction executions  to  emulate,  the execution stops when this limit
                         is reached
  --output-profiling [{true,false}]
                         output the profiling data into the log file
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
  --err-parse-error {true,false}
                         stop execution when an error or invalid instruction is encountered during parsing
  --err-invalid-counter {true,false}
                         stop execution when an invalid value is written to '@counter'
  --err-unsupported-opcode {true,false}
                         stop execution when an instruction unsupported by the emulator is encountered
  --err-nonexistent-var {true,false}
                         stop execution when a nonexistent variable is being indirectly accessed
  --err-assignment-to-fixed-var {true,false}
                         stop execution on attempts to write a value to an unmodifiable built-in variable
  --err-not-an-object {true,false}
                         stop execution when a numeric value is used instead of an object
  --err-not-a-number {true,false}
                         stop execution when an object is used instead of a numeric value (nulls are always permitted)
  --err-unknown-color {true,false}
                         stop execution when an unknown color is used in a named color literal
  --err-invalid-character {true,false}
                         stop execution when an invalid numeric value is used in the 'printchar' instruction
  --err-invalid-lookup {true,false}
                         stop execution when an invalid index is used in the 'lookup' instruction
  --err-invalid-link {true,false}
                         stop execution when an invalid index is used in the 'getlink' instruction
  --err-memory-access {true,false}
                         stop execution when accessing invalid memory-cell or memory-bank index
  --err-memory-object {true,false}
                         stop execution when attempting to store an object in external memory
  --err-unsupported-block-operation {true,false}
                         stop execution when performing an unsupported operation on a block
  --err-text-buffer-overflow {true,false}
                         stop execution when the text buffer size (400 characters) is exceeded
  --err-invalid-format {true,false}
                         stop execution when no placeholder for the 'format' instruction exists in the buffer
  --err-graphics-buffer-overflow {true,false}
                         stop execution when the graphics buffer size (256 operations) is exceeded
  --err-runtime-check-failed {true,false}
                         stop execution when a compiler-generated runtime check fails.
```

## Decompile Mlog action help

```
usage: mindcode dm [-h] [--output-mlog [OUTPUT_MLOG]] [--output-decompiled [OUTPUT_DECOMPILED]]
                [--output-directory OUTPUT-DIRECTORY] [-w [{update,update-all,upgrade-all,force-update-all,extract}]]
                [--watcher-version {v0,v1}] [--watcher-port {0..65535}] [--watcher-timeout {0..3600000}]
                [--emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]]
                [--emulator-fps {1.0..240.0}] [--run [{true,false}]] [--run-steps {0..1000000000}]
                [--output-profiling [{true,false}]] [--trace-execution {true,false}]
                [--dump-variables-on-stop {true,false}] [--stop-on-stop-instruction {true,false}]
                [--stop-on-end-instruction {true,false}] [--stop-on-program-end {true,false}]
                [--err-parse-error {true,false}] [--err-invalid-counter {true,false}]
                [--err-unsupported-opcode {true,false}] [--err-nonexistent-var {true,false}]
                [--err-assignment-to-fixed-var {true,false}] [--err-not-an-object {true,false}]
                [--err-not-a-number {true,false}] [--err-unknown-color {true,false}]
                [--err-invalid-character {true,false}] [--err-invalid-lookup {true,false}]
                [--err-invalid-link {true,false}] [--err-memory-access {true,false}] [--err-memory-object {true,false}]
                [--err-unsupported-block-operation {true,false}] [--err-text-buffer-overflow {true,false}]
                [--err-invalid-format {true,false}] [--err-graphics-buffer-overflow {true,false}]
                [--err-runtime-check-failed {true,false}] [input]

Load mlog code from a file  or  an  in-game  processor  for  further  processing  (partially decompiling into a Mindcode
source, running on the internal emulator or sending to an in-game processor).

named arguments:
  -h, --help             show this help message and exit

Input/output:
  input                  Mlog text file to be decompiled into Mindcode  source  file. When -w extract is used, the input
                         file must not be specified.
  --output-mlog [OUTPUT_MLOG]
                         output file to receive the mlog file extracted from the processor selected in the game.
  --output-decompiled [OUTPUT_DECOMPILED]
                         output file to receive the decompiled Mindcode.
  --output-directory OUTPUT-DIRECTORY
                         specifies the directory where the output files will be placed.
  -w, --watcher [{update,update-all,upgrade-all,force-update-all,extract}]
                         use Mlog Watcher to obtain or send the mlog code from/to the game (default: update).
                             extract     load code from the selected processor in the game
                             update      send code loaded from a file to the selected processor
  --watcher-version {v0,v1}
                         specifies the version of the Mlog Watcher mod
  --watcher-port {0..65535}
                         port number for communication with Mlog Watcher
  --watcher-timeout {0..3600000}
                         timeout in milliseconds when trying to establish a connection to Mlog Watcher

Emulator options:
  Options to specify whether and how to run the code  or schematic in an emulated environment. The emulated processor is
  much faster than Mindustry processors, but can't run  instructions  which obtain information from the Mindustry World.
  Memory cells/banks and other processors can be read from or written  to if part of the schematic or, when running just
  the code, the default processor configuration provided by Mindcode.

  --emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  --emulator-fps {1.0..240.0}
                         the fps used by the emulator
  --run [{true,false}]   run the compiled code on an emulated processor
  --run-steps {0..1000000000}
                         the maximum number of instruction executions  to  emulate,  the execution stops when this limit
                         is reached
  --output-profiling [{true,false}]
                         output the profiling data into the log file
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
  --err-parse-error {true,false}
                         stop execution when an error or invalid instruction is encountered during parsing
  --err-invalid-counter {true,false}
                         stop execution when an invalid value is written to '@counter'
  --err-unsupported-opcode {true,false}
                         stop execution when an instruction unsupported by the emulator is encountered
  --err-nonexistent-var {true,false}
                         stop execution when a nonexistent variable is being indirectly accessed
  --err-assignment-to-fixed-var {true,false}
                         stop execution on attempts to write a value to an unmodifiable built-in variable
  --err-not-an-object {true,false}
                         stop execution when a numeric value is used instead of an object
  --err-not-a-number {true,false}
                         stop execution when an object is used instead of a numeric value (nulls are always permitted)
  --err-unknown-color {true,false}
                         stop execution when an unknown color is used in a named color literal
  --err-invalid-character {true,false}
                         stop execution when an invalid numeric value is used in the 'printchar' instruction
  --err-invalid-lookup {true,false}
                         stop execution when an invalid index is used in the 'lookup' instruction
  --err-invalid-link {true,false}
                         stop execution when an invalid index is used in the 'getlink' instruction
  --err-memory-access {true,false}
                         stop execution when accessing invalid memory-cell or memory-bank index
  --err-memory-object {true,false}
                         stop execution when attempting to store an object in external memory
  --err-unsupported-block-operation {true,false}
                         stop execution when performing an unsupported operation on a block
  --err-text-buffer-overflow {true,false}
                         stop execution when the text buffer size (400 characters) is exceeded
  --err-invalid-format {true,false}
                         stop execution when no placeholder for the 'format' instruction exists in the buffer
  --err-graphics-buffer-overflow {true,false}
                         stop execution when the graphics buffer size (256 operations) is exceeded
  --err-runtime-check-failed {true,false}
                         stop execution when a compiler-generated runtime check fails.
```

## Compile Schematic action help

```
usage: mindcode cs [-h] [-c] [-w [{update,add}]] [--watcher-version {v0,v1}] [--watcher-port {0..65535}]
                [--watcher-timeout {0..3600000}] [-o [OUTPUT]] [-l [LOG]] [--output-directory OUTPUT-DIRECTORY]
                [--file-references {path,uri,windows-uri}] [-a [TAG [TAG ...]]]
                [-t {6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}] [-i {1..100000}]
                [--builtin-evaluation {none,compatible,full}] [--null-counter-is-noop {true,false}]
                [--symbolic-labels [{true,false}]] [--mlog-indent {0..8}] [--no-argument-padding [{true,false}]]
                [--function-prefix {short,long}] [--author author [author ...]] [--no-signature]
                [--processor-id processor_ID] [--program-name program_name] [--program-version program_version]
                [--encode-zero-characters [{true,false}]] [-y {strict,mixed,relaxed}] [--target-guard [{true,false}]]
                [--setrate {1..1000}] [--ipt {1..1000}] [--volatile-atomic {true,false}]
                [--boundary-checks {true,false}] [--emulate-strict-not-equal {true,false}]
                [--enforce-instruction-limit {true,false}] [--error-function {true,false}]
                [--error-reporting {none,assert,minimal,simple,described}] [-r {none,comments,passive,active}]
                [--auto-printflush {true,false}] [-g {size,neutral,speed}] [-e {1..1000}]
                [--unsafe-case-optimization [{true,false}]] [--case-optimization-strength {0..6}]
                [--mlog-block-optimization {true,false}] [--use-lookup-arrays {true,false}]
                [--use-short-arrays {true,false}] [--use-text-jump-tables {true,false}]
                [--use-text-translations {true,false}] [-O {0..4}] [--temp-variables-elimination LEVEL]
                [--case-expression-optimization LEVEL] [--dead-code-elimination LEVEL] [--jump-normalization LEVEL]
                [--condition-optimization LEVEL] [--single-step-elimination LEVEL] [--expression-optimization LEVEL]
                [--boolean-optimization LEVEL] [--if-expression-optimization LEVEL] [--data-flow-optimization LEVEL]
                [--instruction-reordering LEVEL] [--loop-hoisting LEVEL] [--loop-rotation LEVEL]
                [--loop-unrolling LEVEL] [--function-inlining LEVEL] [--array-optimization LEVEL]
                [--case-switching LEVEL] [--return-optimization LEVEL] [--jump-straightening LEVEL]
                [--jump-threading LEVEL] [--unreachable-code-elimination LEVEL] [--stack-optimization LEVEL]
                [--print-merging LEVEL]
                [--sort-variables [{linked,params,globals,main,locals,all,none} [{linked,params,globals,main,locals,all,none} ...]]]
                [-p {0..2}] [--debug [{true,false}]] [-d {0..3}] [--print-code-size {true,false}]
                [-u [{none,plain,flat-ast,deep-ast,source}]] [-s]
                [--emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]]
                [--emulator-fps {1.0..240.0}] [--run [{true,false}]] [--run-steps {0..1000000000}]
                [--output-profiling [{true,false}]] [--trace-execution {true,false}]
                [--dump-variables-on-stop {true,false}] [--stop-on-stop-instruction {true,false}]
                [--stop-on-end-instruction {true,false}] [--stop-on-program-end {true,false}]
                [--err-parse-error {true,false}] [--err-invalid-counter {true,false}]
                [--err-unsupported-opcode {true,false}] [--err-nonexistent-var {true,false}]
                [--err-assignment-to-fixed-var {true,false}] [--err-not-an-object {true,false}]
                [--err-not-a-number {true,false}] [--err-unknown-color {true,false}]
                [--err-invalid-character {true,false}] [--err-invalid-lookup {true,false}]
                [--err-invalid-link {true,false}] [--err-memory-access {true,false}] [--err-memory-object {true,false}]
                [--err-unsupported-block-operation {true,false}] [--err-text-buffer-overflow {true,false}]
                [--err-invalid-format {true,false}] [--err-graphics-buffer-overflow {true,false}]
                [--err-runtime-check-failed {true,false}] [input]

Compile a schematic definition file into binary msch file.

named arguments:
  -h, --help             show this help message and exit
  -c, --clipboard        encode the created schematic into text representation and paste into clipboard
  -w, --watcher [{update,add}]
                         invoke an specific Mlog Watcher operation on the created schematic (default: update)
                             update      update the schematic with the same name in the schematics library
                             add         add a new copy of the schematic to the schematics library
  --watcher-version {v0,v1}
                         specifies the version of the Mlog Watcher mod
  --watcher-port {0..65535}
                         port number for communication with Mlog Watcher
  --watcher-timeout {0..3600000}
                         timeout in milliseconds when trying to establish a connection to Mlog Watcher

Input/output files:
  input                  Schematic definition file  to  be  compiled  into  a  binary  msch  file;  uses  stdin when not
                         specified.
  -o, --output [OUTPUT]  output file to receive the resulting binary Mindustry schematic file (.msch).
  -l, --log [LOG]        output file to receive compiler messages; uses input  file  with .log extension when no file is
                         specified.
  --output-directory OUTPUT-DIRECTORY
                         specifies the directory where the output files will be placed.
  --file-references {path,uri,windows-uri}
                         specifies the format in which a reference to a  location  in a source file is output on console
                         and into the log

Schematic creation:
  -a, --add-tag [TAG [TAG ...]]
                         defines additional tag(s) to add  to  the  schematic,  plain  text  and symbolic icon names are
                         supported

Environment options:
  Options to specify the target environment for the  code  being  compiled. This includes the Mindustry version, as well
  as prescribing which specific processor features may or may not be used.

  -t, --target {6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  -i, --instruction-limit {1..100000}
                         sets the maximal number of instructions for the speed optimizations
  --builtin-evaluation {none,compatible,full}
                         sets the level of compile-time evaluation of numeric builtin constants
  --null-counter-is-noop {true,false}
                         when active, Mindcode assumes assigning a 'null' to  '@counter' is ignored by the processor and
                         may produce code depending on this behavior

Mlog formatting options:
  Options determining how the mlog code  is  generated  and  formatted,  including  instructions generated to carry over
  specific information into the compiled program (such as signature or processor ID).

  --symbolic-labels [{true,false}]
                         generate symbolic labels for jump instructions where possible
  --mlog-indent {0..8}   the amount of indenting applied to logical blocks in the compiled mlog code
  --no-argument-padding [{true,false}]
                         when set, mlog instructions emitted  by  the  compiler  are  not  padded with zeroes for unused
                         arguments
  --function-prefix {short,long}
                         specifies the how the function prefix of  local  variables  is generated (either a short common
                         prefix for all functions, or a potentially long prefix derived from function name)
  --author author [author ...]
                         adds an author to  the  list  of  authors  which  is  then  output  similarly to the compiler's
                         signature
  --no-signature         prevents appending a signature 'Compiled  by  Mindcode  - github.com/cardillan/mindcode' at the
                         end of the final code
  --processor-id processor_ID
                         sets the processor ID to be stored in the compiled code
  --program-name program_name
                         sets the program name to be stored in the compiled code
  --program-version program_version
                         sets the program version to be stored in the compiled code
  --encode-zero-characters [{true,false}]
                         allow encoding zero characters into mlog string  literals (WARNING: the resulting code can't be
                         edited as a teext or copied/pasted to/from the clipboard!)

Compiler options:
  Options which affect the way the source code is compiled.

  -y, --syntax {strict,mixed,relaxed}
                         specifies syntactic mode used to compile the source code
  --target-guard [{true,false}]
                         generates guard code at  the  beginning  of  the  program  ensuring  the  processor runs in the
                         Mindustry version compatible with the 'target' and 'builtin-evaluation' options
  --setrate {1..1000}    generates a 'setrate' instruction with  the  specified  value  in  the initialization code, and
                         sets the IPT value for the compiler to use
  --ipt {1..1000}        sets the IPT value for the compiler to use without generating the 'setrate' instruction
  --volatile-atomic {true,false}
                         when active, nonvolatile variables are not protected by atomic blocks
  --boundary-checks {true,false}
                         activates or deactivates runtime checks  to  catch  indexes  being  out of range when accessing
                         arrays
  --emulate-strict-not-equal {true,false}
                         use the 'select' instruction to emulate jump with the !== (strict not equal) condition
  --enforce-instruction-limit {true,false}
                         generate compilation error when the instruction limit is exceeded
  --error-function {true,false}
                         activates or deactivates the  error()  function  (when  set  to  'false', error() function does
                         nothing)
  --error-reporting {none,assert,minimal,simple,described}
                         specifies the mechanism used by the compiler when generating runtime checks
  -r, --remarks {none,comments,passive,active}
                         controls remarks propagation  to  the  compiled  code:  none  (remarks  are  removed), comments
                         (included as mlog comments), passive  (included  as  'print'  but  not not executed), or active
                         (included as 'print' and printed)
  --auto-printflush {true,false}
                         automatically add a 'printflush message1'  instruction  at  the  end  of  the program if one is
                         missing

Optimization options:
  Options guiding the  overall  optimization  of  the  compiled  code  or  activating/deactivating specific optimization
  actions.

  -g, --goal {size,neutral,speed}
                         sets code generation  goal:  minimize  code  size,  minimize  execution  speed,  or no specific
                         preference
  -e, --passes {1..1000}
                         sets the number of optimization passes to perform
  --unsafe-case-optimization [{true,false}]
                         omits range checking of case expressions without an else branch during optimization
  --case-optimization-strength {0..6}
                         sets the strength of case switching optimization:  higher number means more case configurations
                         are considered, potentially producing a more efficient  code, at the cost of longer compilation
                         time
  --mlog-block-optimization {true,false}
                         allows (limited) optimization of code inside mlog blocks
  --use-lookup-arrays {true,false}
                         allows using the lookup mechanism for implementing internal arrays
  --use-short-arrays {true,false}
                         allows using specialized implementation of short arrays (2-4 elements)
  --use-text-jump-tables {true,false}
                         when active, generates jump  tables  by  encoding  instruction  addresses  into a single String
                         value, and uses a single 'read' instruction to  directly  set the counter to the target address
                         (target 8 or higher required)
  --use-text-translations {true,false}
                         allows using 'read' instruction to implement simple case expressions

Optimization levels:
  Options specifying the global and individual  optimization  levels.  Individual  optimizers  use global level when not
  explicitly set. Available optimization levels are {none, basic, advanced, experimental}.

  -O {0..4}              sets global optimization level for all optimizers
  --temp-variables-elimination LEVEL
                         sets the optimization level of eliminating  temporary  variables created to extract values from
                         instructions
  --case-expression-optimization LEVEL
                         sets the  optimization  level  of  eliminating  temporary  variables  created  to  execute case
                         expressions
  --dead-code-elimination LEVEL
                         sets the optimization level of eliminating  writes  to compiler- or user-defined variables that
                         are not used
  --jump-normalization LEVEL
                         sets the optimization level  of  replacing  always  true  conditional  jumps with unconditional
                         ones, removing always false jumps
  --condition-optimization LEVEL
                         sets the optimization level of merging  an  op  instruction producing a boolean expression into
                         the following conditional jump
  --single-step-elimination LEVEL
                         sets the optimization level of eliminating jumps to the next instruction
  --expression-optimization LEVEL
                         sets the optimization level of optimizing some common mathematical expressions
  --boolean-optimization LEVEL
                         sets the optimization level of simplifying  boolean  expressions and/or implementing them using
                         the 'select' instruction
  --if-expression-optimization LEVEL
                         sets the optimization level of improving ternary/if expressions
  --data-flow-optimization LEVEL
                         sets the optimization level of improving  variable  assignments and expressions, analyzing data
                         flow for other optimizations
  --instruction-reordering LEVEL
                         sets the optimization level of reordering  instructions to allow additional optimizations being
                         made (not available yet)
  --loop-hoisting LEVEL  sets the optimization level of moving invariant code out of loops
  --loop-rotation LEVEL  sets the optimization level of rotating a front loop condition to the bottom of the loop
  --loop-unrolling LEVEL
                         sets the optimization level of unrolling loops with a fixed number of iterations
  --function-inlining LEVEL
                         sets the optimization level of inlining stackless function calls
  --array-optimization LEVEL
                         sets the optimization level of finding optimal array-access implementation for internal arrays
  --case-switching LEVEL
                         sets the optimization level of modifying suitable case  expressions to use jump tables or value
                         translations
  --return-optimization LEVEL
                         sets the optimization  level  of  speeding  up  return  statements  in  recursive and stackless
                         functions
  --jump-straightening LEVEL
                         sets the optimization level of simplifying sequences of intertwined jumps
  --jump-threading LEVEL
                         sets the optimization level of eliminating chained jumps
  --unreachable-code-elimination LEVEL
                         sets the optimization level of  eliminating  instructions  made unreachable by optimizations or
                         false conditions
  --stack-optimization LEVEL
                         sets the optimization level of optimizing variable storage on the stack
  --print-merging LEVEL  sets the optimization level of merging consecutive print statements outputting constant values

Debugging options:
  Options to activate debugging features or additional output from the compiler.

  --sort-variables [{linked,params,globals,main,locals,all,none} [{linked,params,globals,main,locals,all,none} ...]]
                         prepends the final  code  with  instructions  that  ensure  variables  are  created  inside the
                         processor  in a well-defined order. The variables  are  sorted according to their categories in
                         order, and then  alphabetically.   Category  ALL  represents  all  remaining, not-yet processed
                         variables. When --sort-variables  is  given  without  specifying  any  category,  LINKED PARAMS
                         GLOBALS MAIN LOCALS are used.
  -p, --parse-tree {0..2}
                         sets the detail level of parse tree output into the log file, 0 = off
  --debug [{true,false}]
                         activates or deactivates generation of debug code
  -d, --debug-messages {0..3}
                         sets the detail level of debug messages, 0 = off
  --print-code-size {true,false}
                         outputs final code size broken down by function
  -u, --print-unresolved [{none,plain,flat-ast,deep-ast,source}]
                         activates output of the unresolved code (before  virtual instructions resolution) of given type
                         (instruction numbers are included in the output)
  -s, --stacktrace       outputs a stack trace onto stderr when an unhandled exception occurs

Emulator options:
  Options to specify whether and how to run the code  or schematic in an emulated environment. The emulated processor is
  much faster than Mindustry processors, but can't run  instructions  which obtain information from the Mindustry World.
  Memory cells/banks and other processors can be read from or written  to if part of the schematic or, when running just
  the code, the default processor configuration provided by Mindcode.

  --emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  --emulator-fps {1.0..240.0}
                         the fps used by the emulator
  --run [{true,false}]   run the compiled code on an emulated processor
  --run-steps {0..1000000000}
                         the maximum number of instruction executions  to  emulate,  the execution stops when this limit
                         is reached
  --output-profiling [{true,false}]
                         output the profiling data into the log file
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
  --err-parse-error {true,false}
                         stop execution when an error or invalid instruction is encountered during parsing
  --err-invalid-counter {true,false}
                         stop execution when an invalid value is written to '@counter'
  --err-unsupported-opcode {true,false}
                         stop execution when an instruction unsupported by the emulator is encountered
  --err-nonexistent-var {true,false}
                         stop execution when a nonexistent variable is being indirectly accessed
  --err-assignment-to-fixed-var {true,false}
                         stop execution on attempts to write a value to an unmodifiable built-in variable
  --err-not-an-object {true,false}
                         stop execution when a numeric value is used instead of an object
  --err-not-a-number {true,false}
                         stop execution when an object is used instead of a numeric value (nulls are always permitted)
  --err-unknown-color {true,false}
                         stop execution when an unknown color is used in a named color literal
  --err-invalid-character {true,false}
                         stop execution when an invalid numeric value is used in the 'printchar' instruction
  --err-invalid-lookup {true,false}
                         stop execution when an invalid index is used in the 'lookup' instruction
  --err-invalid-link {true,false}
                         stop execution when an invalid index is used in the 'getlink' instruction
  --err-memory-access {true,false}
                         stop execution when accessing invalid memory-cell or memory-bank index
  --err-memory-object {true,false}
                         stop execution when attempting to store an object in external memory
  --err-unsupported-block-operation {true,false}
                         stop execution when performing an unsupported operation on a block
  --err-text-buffer-overflow {true,false}
                         stop execution when the text buffer size (400 characters) is exceeded
  --err-invalid-format {true,false}
                         stop execution when no placeholder for the 'format' instruction exists in the buffer
  --err-graphics-buffer-overflow {true,false}
                         stop execution when the graphics buffer size (256 operations) is exceeded
  --err-runtime-check-failed {true,false}
                         stop execution when a compiler-generated runtime check fails.
```

## Decompile Schematic action help

```
usage: mindcode ds [-h] [--output-msch [OUTPUT_MSCH]] [--output-decompiled [OUTPUT_DECOMPILED]]
                [--output-directory OUTPUT-DIRECTORY] [-w [{update,extract,add}]] [--watcher-version {v0,v1}]
                [--watcher-port {0..65535}] [--watcher-timeout {0..3600000}] [-p] [-P] [-c] [-C] [-l] [-L]
                [-s {original,horizontal,vertical}] [-d {rotatable,non-default,all}]
                [--emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]]
                [--emulator-fps {1.0..240.0}] [--run [{true,false}]] [--run-steps {0..1000000000}]
                [--output-profiling [{true,false}]] [--trace-execution {true,false}]
                [--dump-variables-on-stop {true,false}] [--stop-on-stop-instruction {true,false}]
                [--stop-on-end-instruction {true,false}] [--stop-on-program-end {true,false}]
                [--err-parse-error {true,false}] [--err-invalid-counter {true,false}]
                [--err-unsupported-opcode {true,false}] [--err-nonexistent-var {true,false}]
                [--err-assignment-to-fixed-var {true,false}] [--err-not-an-object {true,false}]
                [--err-not-a-number {true,false}] [--err-unknown-color {true,false}]
                [--err-invalid-character {true,false}] [--err-invalid-lookup {true,false}]
                [--err-invalid-link {true,false}] [--err-memory-access {true,false}] [--err-memory-object {true,false}]
                [--err-unsupported-block-operation {true,false}] [--err-text-buffer-overflow {true,false}]
                [--err-invalid-format {true,false}] [--err-graphics-buffer-overflow {true,false}]
                [--err-runtime-check-failed {true,false}] [input]

Decompile a binary msch file into schematic definition file.

named arguments:
  -h, --help             show this help message and exit

Input/output:
  input                  Mindustry schematic file to be decompiled  into  Schematic  Definition File. When -w extract is
                         used, the input file must not be specified.
  --output-msch [OUTPUT_MSCH]
                         output file to receive the schematics loaded or extracted from the game in binary format.
  --output-decompiled [OUTPUT_DECOMPILED]
                         output file to receive a decompiled schematic definition file.
  --output-directory OUTPUT-DIRECTORY
                         specifies the directory where the output files will be placed.
  -w, --watcher [{update,extract,add}]
                         use Mlog Watcher to obtain or send the schematic from/to the game (default: update).
                             extract     load schematic from the schematic shown on the info screen in-game
                             update      update the schematic with the same name in the schematics library
                             add         add a new copy of the schematic to the schematics library
  --watcher-version {v0,v1}
                         specifies the version of the Mlog Watcher mod
  --watcher-port {0..65535}
                         port number for communication with Mlog Watcher
  --watcher-timeout {0..3600000}
                         timeout in milliseconds when trying to establish a connection to Mlog Watcher

Decompilation options:
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
  -s, --sort-order {original,horizontal,vertical}
                         specifies how to order blocks in the decompiled schematic definition file
  -d, --direction {rotatable,non-default,all}
                         specifies when to include direction clause  in  decompiled  schematic definition file: only for
                         blocks affected by rotation, only for block with non-default direction, or for all blocks

Emulator options:
  Options to specify whether and how to run the code  or schematic in an emulated environment. The emulated processor is
  much faster than Mindustry processors, but can't run  instructions  which obtain information from the Mindustry World.
  Memory cells/banks and other processors can be read from or written  to if part of the schematic or, when running just
  the code, the default processor configuration provided by Mindcode.

  --emulator-target [{6,6.0,7,7w,7.0,7.0w,7.1,7.1w,8,8w,8.0,8.0w,8.1,8.1w}]
                         selects target processor version and type (a 'm', 'l', 'h' or 'w' suffix specifies the type)
  --emulator-fps {1.0..240.0}
                         the fps used by the emulator
  --run [{true,false}]   run the compiled code on an emulated processor
  --run-steps {0..1000000000}
                         the maximum number of instruction executions  to  emulate,  the execution stops when this limit
                         is reached
  --output-profiling [{true,false}]
                         output the profiling data into the log file
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
  --err-parse-error {true,false}
                         stop execution when an error or invalid instruction is encountered during parsing
  --err-invalid-counter {true,false}
                         stop execution when an invalid value is written to '@counter'
  --err-unsupported-opcode {true,false}
                         stop execution when an instruction unsupported by the emulator is encountered
  --err-nonexistent-var {true,false}
                         stop execution when a nonexistent variable is being indirectly accessed
  --err-assignment-to-fixed-var {true,false}
                         stop execution on attempts to write a value to an unmodifiable built-in variable
  --err-not-an-object {true,false}
                         stop execution when a numeric value is used instead of an object
  --err-not-a-number {true,false}
                         stop execution when an object is used instead of a numeric value (nulls are always permitted)
  --err-unknown-color {true,false}
                         stop execution when an unknown color is used in a named color literal
  --err-invalid-character {true,false}
                         stop execution when an invalid numeric value is used in the 'printchar' instruction
  --err-invalid-lookup {true,false}
                         stop execution when an invalid index is used in the 'lookup' instruction
  --err-invalid-link {true,false}
                         stop execution when an invalid index is used in the 'getlink' instruction
  --err-memory-access {true,false}
                         stop execution when accessing invalid memory-cell or memory-bank index
  --err-memory-object {true,false}
                         stop execution when attempting to store an object in external memory
  --err-unsupported-block-operation {true,false}
                         stop execution when performing an unsupported operation on a block
  --err-text-buffer-overflow {true,false}
                         stop execution when the text buffer size (400 characters) is exceeded
  --err-invalid-format {true,false}
                         stop execution when no placeholder for the 'format' instruction exists in the buffer
  --err-graphics-buffer-overflow {true,false}
                         stop execution when the graphics buffer size (256 operations) is exceeded
  --err-runtime-check-failed {true,false}
                         stop execution when a compiler-generated runtime check fails.
```

---

[&#xAB; Previous: Schemacode](SCHEMACODE.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: IDE Integration &#xBB;](TOOLS-IDE-INTEGRATION.markdown)
