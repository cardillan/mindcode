# Changelog

All notable changes to this project will be documented in this file.

## 2.7.0 - 2024-11-28

### Fixed

* Fixed wrong Jump Threading optimization in out-of-line functions ([#177](https://github.com/cardillan/mindcode/issues/107)).
* Fixed bugs in the Data Flow Optimization:
  * The virtual `setaddr` instruction, unused due to the corresponding jump being unreachable, wouldn't be removed from code. This caused errors referencing a non-existent label.
  * Data Flow Optimization would sometimes corrupt the expected code structure when removing unneeded instructions, causing bugs or runtime errors during subsequent optimizations. 
  * The entry condition to a loop might be incorrectly evaluated, leading to wrong optimizations of some loops.
  * A variable read in an unreachable part of code might be reported as uninitialized. This might lead to compilation error for compiler-generated uninitialized variables.
* Fixed the If Expression Optimization mistakenly propagating a jump into the bodies of the if/else statement, causing errors during subsequent optimizations.
* Fixed the Single Step Elimination occasionally crashing when removing a superfluous jump.
* Fixed the Loop Unrolling Optimization possibly unrolling incorrect number of iterations on loops with no exit condition.
* Fixed a bug in the implementation of the `median(x...)` function in the `math` system library.
  
### Added

* Added separate [Testing Framework](doc/syntax/TOOLS-TESTING-TOOL.markdown) command-line tool. The tool allows compiling and running Mindcode source files under various optimization settings, helping to detect bugs in Mindcode.  

#### Experimental features

* Added limited [Unit testing support](doc/syntax/TOOLS-TESTING-TOOL.markdown#unit-testing-support).

### Changed

* **Breaking:** changed the default language target from ML7 to ML7A. The following functions are affected:
  * `getBlock`: previously, the building at given coordinates was returned through an output parameter. In ML7A, the building is returned through function return value. See [`getBlock()`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ucontrol).
  * `ulocate`: previously, the function returned a boolean value (`true` when the building was found, `false` otherwise). In ML7A, the function returns a building (if found), and the `found` flag is returned through an output parameter. See [`getBlock()`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ulocate).
* Changed automatic generation of Intellij IDEA settings files to only modify the zip files when the contents of the contained files changes.
* Changed Loop Unrolling Optimization to handle more cases of loop control variable modification on advanced level.

## 2.6.0 - 2024-11-15

**Breaking:** This release comes with a new keyword in Mindcode syntax (`require`), which break existing code where this keyword was used as a variable or function name.

### Fixed

* Fixed the `findLinkedBlocks` library function not to produce a warning about uninitialized variables.
* Fixed the mlog decompiler crashing on `jump` instructions targeting instructions outside valid range.
* Fixed unhandled error when decompiling invalid code from mlog by the web application.
* Fixed `or` operator being evaluated the same as `||` instead of `|` by the processor emulator.

### Added

* Added warnings for unrecognized built-in variables. An unrecognized built-in variable might be invalid or mistyped.
* Added the built-in math constants `@pi`, `@e`, `@degToRad` and `@radToDeg` to the processor emulator.
* Added automatic generation of [Intellij IDEA settings files](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#intellij-idea) to keep them up-to-date with the language definition.

#### Experimental features

* Added a new `require` keyword for adding a system library or another external file (for command-line compilers) to the compiled code.
* Added information about the compiled code size of individual functions to the  [system library documentation](doc/syntax/SYSTEM-LIBRARY.markdown#compiled-function-sizes).
* Added new functions to the [`printing` system library](doc/syntax/SYSTEM-LIBRARY.markdown#printing-library) (`printExactFast` and `printExactSlow`).
* Added new functions to the [`math` system library](doc/syntax/SYSTEM-LIBRARY.markdown#math-library) (`round`, `frac`, `sign`, `isZero`, `isEqual`, `nullToZero`, `sum`, `avg`, `median`).
* Added configurable [execution flags](doc/syntax/TOOLS-PROCESSOR-EMULATOR.markdown#execution-flags) governing the behavior of the processor emulator.

### Changed

* **Breaking:** changed the [system library](doc/syntax/SYSTEM-LIBRARY.markdown) to several separate files that can be included in the compiled code using the `require` keyword. The system libraries are no longer automatically loaded when compiling for `ML8A` target, and most of them can be used with earlier targets as well.
* Changed rules for function overloading: a vararg function doesn't conflict with a non-vararg function. When a function call matches both a vararg function and a non-vararg function, the non-vararg function will be called.
* Changed all variables within system libraries to use the `_` prefix, to avoid possible clashes with constants and program parameters declared in the main file.
* Changed existing examples to utilize functions from the system library where one is available.
* Changed processor emulator to [output all existing variables and their values](doc/syntax/TOOLS-PROCESSOR-EMULATOR.markdown#inspecting-program-state) when encountering the `stop` instruction.
* Changed the Jump Optimization to handle cases where the jump instruction contains a value produced by a function.

## 2.5.0 - 2024-11-03

**Breaking:** This release comes with new keywords in Mindcode syntax (`begin`, `var` and `void`), which break existing code where those new keywords were used as a variable or function name.

### Fixed

* Fixed incorrectly processed case expression passed into functions ([#107](https://github.com/cardillan/mindcode/issues/107)).
* Fixed arguments incorrectly passed to recursive calls ([#169](https://github.com/cardillan/mindcode/issues/169)).
* Fixed output function parameters incorrectly accepting expressions as arguments ([#170](https://github.com/cardillan/mindcode/issues/170)).
* Fixed error when allocating a stack using the ML8A target  ([#171](https://github.com/cardillan/mindcode/issues/171)).
* Fixed optimization not recognizing all interactions with Mindustry World  ([#172](https://github.com/cardillan/mindcode/issues/172)).
* Fixed wrong optimization of List iteration loops with output iterator variables ([#173](https://github.com/cardillan/mindcode/issues/173)).

### Added

* Added a new `void` keyword for declaring functions not returning any value.
* Added a new `begin` and `var` keywords reserved for future use. 
* Added support for omitting optional arguments in the argument list.
* Added support for the special `@wait` argument of the [`message()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-message-function).

#### Experimental features

* Added [output and input/output parameters](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#function-parameters) of user defined functions.
* Added [vararg (variable arity) functions](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#vararg-functions).
* Added [function overloading](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#function-overloading).
* Added the possibility to specify custom instructions not known to Mindcode through the [`mlog()` function](doc/syntax/SYNTAX-5-OTHER.markdown#creating-custom-mlog-instructions).
* Added the [`findLinkedBlocks` function](doc/syntax/SYSTEM-LIBRARY.markdown#findlinkedblocks) to the system library.

### Changed

* Changed the [Loop Unrolling optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling) to replace output iterator variables with the variable assigned to them.
* Changed the [Single Step Eliminator](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to remove a jump is if there is an identical jump preceding it and there are no other jumps or labels between them. Active on `experimental`.
* Changed the expression evaluator to evaluate operations over known built-in values. The change enhances the Data Flow and Jump Normalization optimizations.
* Changed the Schemacode compiler to correctly output positions of error messages generated by both the Schemacode and Mindcode compilers, taking into account both the source file and/or position of the Mindcode program or program snippet within the source file.  

### Deprecated

* Deprecated the syntax of properties omitting the `@` prefix for Mindustry built-in variables (e.g. `vault1.coal` instead of `vault1.@coal`).
* Deprecated the syntax of Mindustry Logic function calls not requiring the `out` modifier when passing in an output argument.

### Miscellaneous

* Improved the command-line tool output of the Compile Schemacode action.

## 2.4.0 - 2024-10-23

### Fixed

* Fixed Copy to clipboard button not working ([#168](https://github.com/cardillan/mindcode/issues/168)).
* Fixed unhandled error when decoding a malformed Schematic file string by the web application.
* Fixed Schemacode samples to not use deprecated syntax.

### Added

* Added support for [partially decompiling](doc/syntax/TOOLS-MLOG-DECOMPILER.markdown) existing mlog code into Mindcode.

## 2.3.0 - 2024-10-16

### Fixed

* Fixed wrong compilation order of appended files ([#155](https://github.com/cardillan/mindcode/issues/155)).
* Fixed inadequate reporting of syntax error ([#156](https://github.com/cardillan/mindcode/issues/156)).
* Fixed wrong handling of comparison operators by Data Flow Optimization ([#158](https://github.com/cardillan/mindcode/issues/158)).
* Fixed wrong parsing of formattable string literals.
* Fixed inadequate handling of unsupported expressions embedded in formattable string literals.  

### Added

* **Breaking:** Added support for syntax variants ([`strict` and `relaxed`](doc/syntax/SYNTAX-STRICT-RELAXED.markdown)) to Mindcode. The Strict syntax is the default now; to be able to compile existing Mindcode the Relaxed syntax needs to be activated using the `#relaxed;` directive.
* Added support for the [Mlog Watcher](doc/syntax/TOOLS-MLOG-WATCHER.markdown) Mindustry mod integration to both the web app and the command-line tool. This mod allows the compiled code to be automatically injected into a selected processor in a running Mindustry game.
* Added variable name validation: when inserting mlog into Mindustry processor, variables named `configure` are silently renamed to `config`. For this reason, using `configure` as a name for any variable in Mindcode causes an error. 
* Added navigable compiler error messages to the web app. Clicking on a message with known position in the source code selects the corresponding position in the editor.
* Added support for outputting the error messages by the command line tool in a format which allows IDEs to parse the position and navigate to the error location in the source code.
* Added a variety of new optimizations to the [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization). 
  * `op` instruction: many new optimizations when one of the two operands is known.
  * `lookup` instruction: when possible, the instruction is replaced by a `set` instruction setting the item, liquid, building or unit directly to the target variable, allowing further optimizations to take place. Effective on `aggresive` optimization level.
* Added warning messages when [deprecated features](doc/syntax/SYNTAX-STRICT-RELAXED.markdown#deprecated-features) are detected in the source code. 
* Added support for creating constants from formattable string literals.
* Added full support for the `sync()` function: a variable passed as an argument to this function becomes automatically volatile.

**Schemacode**

* Added support for block comments, delimited by `/*` and `*/`. These comments can span multiple lines.

#### Experimental features
 
* Added support for Mindustry Logic from upcoming version 8. The features supported correspond to the current implementation in Mindustry and might therefore still change. All new features are described in a [separate documentation](doc/syntax/MINDUSTRY-8.markdown).
* Added a [system library](doc/syntax/SYSTEM-LIBRARY.markdown), automatically included when the language target is `8A` or later. 
* Added support to the [If Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#instruction-propagation) to propagate values in `if` expressions not just into the `set` instruction, but into any instruction taking an input parameter. Available on  the `experimental` optimization level.

### Changed

* **Breaking:** Changed the implementation of the `printf()` function under language target `ML8A`. Instead of compile-time formatting of passed parameters, the function uses `print` and `format` instructions for [run-time formatting](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#run-time-formatting).
* Changed the definition of the `&&` and `||` operators: they are guaranteed to [always evaluate to either `0` or `1`](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#operators).
* Changed the `min()` and `max()` functions to accept more than just two arguments.
* Changed the [Temporary Variables Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#temporary-variables-elimination) to replace unused output variables in instructions with `0`, to ensure no unnecessary variable will be created by the instruction, reducing clutter. Closes [#154](https://github.com/cardillan/mindcode/issues/154).
* Changed the [If Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#if-expression-optimization) to support value propagation for all instructions having one output parameter (based on instruction metadata), instead of just a subset of specifically handled instructions.
* Changed - yet again - the way the [Single Step Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) removes the last instruction which is a jump to the beginning of the program, so that it doesn't leave behind any jump that might have targeted the removed instruction. Such a jump was harmless, but unnecessary and looked strange in the mlog.
* Changed the text buffer handling in the processor emulator to recognize identical outputs produced by consecutive `printflush` operations and avoid creating duplicate outputs.
* When a compiler-generated variable is evaluated as uninitialized (this situation indicates a bug in some of the optimizers), an internal error is thrown.

### Deprecated

* Deprecated the original Mindcode syntax.
* Deprecated the usage of parentheses around the list of values in list iteration loops.
* Deprecated escaped double quotes in string literals.
* Deprecated kebab-case identifiers (note that built-in mlog variables, such as `@blast-compound`, will continue to be supported).
* Deprecated the `printf()` function in language target `ML7A` and earlier.
* Deprecated the `configure` property. This property from Mindustry Logic 6 was replaced by `config` in Mindustry Logic 7.

### Miscellaneous

* Added parallel execution of unit tests.
* Renamed the command-line tool module from `compiler` to `toolapp`, and the Mindcode compiler module from `mindcode` to `compiler`.  
* Renamed the files in `bin` directory from `mcc`/`mcc.bat` to `mindcode`/`mindcode.bat`.

## 2.2.1 - 2024-09-30

### Fixed

* Fixed the layout of the web page not rendering well on smaller screens and mobile devices.

### Changed

* Changed the [Single Step Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to remove the last instruction if it jumps to the start of the program (not just `end`, but also unconditional jump) on `advanced` optimization level.

## 2.2.0 - 2024-09-29

**Breaking:** This release comes with three new keywords in Mindcode syntax (`noinline`, `out` and `param`), which break existing code where those new keywords were used as a variable or function name. 

### Fixed

* Fixed wrong size computation of code generated by the `remark()` function if the [`remarks` option](doc/syntax/SYNTAX-5-OTHER.markdown#option-remarks) was set to `none` or `active`.
* Fixed Data Flow optimization incorrectly handling equality and inequality comparisons ([#146](https://github.com/cardillan/mindcode/issues/146)).
* Fixed Data Flow optimization incorrectly processing execution paths through a case expression ([#147](https://github.com/cardillan/mindcode/issues/147)).
* Fixed [Function Inlining optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining) missing some opportunities to inline a function.
* Fixed the `webapp.bat` file not working on Windows due to classpath being too long.

### Added

* Added secondary set of icons representing constants with dashes in identifiers replaced by underscores (e.g. `ITEM_COAL` instead of `ITEM-COAL`). Kebab-case identifiers (with dashes) are deprecated in Mindcode and will be desupported in a future release. (In Schemacode, there are currently no plans to remove support for kebab-case identifiers.)
* Added support for declaring [program parameters](doc/syntax/SYNTAX-1-VARIABLES.markdown#program-parameters) using a new `param` keyword. Using global variables for program parametrization is deprecated, program parameters should be used instead. Support for program parametrization through global variables will be removed in a future release.     
* Added a new `noinline` keyword, which will prevent a function from being inlined even when called just once, and by the [Function Inlining](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining) optimization. The keyword can only be used when declaring a function.
* Added support for block comments, delimited by `/*` and `*/`. These comments can span multiple lines. 
* Added a new schematic sample with quite a sophisticated code [on the Schematics page](http://mindcode.herokuapp.com/?s=overdrive-dome-supply). It consists of an overdrive dome supplied by units controlled by a microprocessor. The microprocessor searches for available units among a list of supported types, switches to the preferred unit type when it becomes available, and rebinds units (possibly switching the type again) if units in use are destroyed or taken over by the player or a rogue processor.  

#### Experimental features

Experimental features may contain bugs, break existing code or produce suboptimal code, and are subject to change.

* Added support for multiple loop variables in [list iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#list-iteration-loops). Each iteration processes as many elements from the list as there are loop variables.
* Added an `out` keyword to be used with loop control variables in list iteration loop, allowing [list elements to be modified](doc/syntax/SYNTAX-3-STATEMENTS.markdown#modifications-of-values-in-the-list).
* Added a new GUI option to choose optimization level in the web app when compiling Mindcode or building Schemacode.
* Added a capability to run the compiled code on an emulated processor, by using a `Compile and Run` button in the web app, or the [`--run` command line option](doc/syntax/TOOLS-CMDLINE.markdown#running-the-compiled-code). The output is shown in a separate control in the web app, or written to the log when using the command line tool.
* Added a capability to the command line tool to compile several source files at once using the [`--append` command line argument](doc/syntax/TOOLS-CMDLINE.markdown#additional-input-files).
* Added new optimization level, `experimental`. On this setting, the [Data Flow optimizer](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) doesn't assume the assignments to global variables might be changed by editing the compiled code, allowing to perform more optimizations on them. Program parameters must be used instead of global variables for program parametrization if this optimization level is used.  
* Added [formattable string literals](doc/syntax/SYNTAX.markdown#formattable-string-literals), which allow formatting outputs of the `print` and `println` functions the same way as `printf` does. 
* Added [enhanced comments](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#enhanced-comments), an alternative way to enter remarks.
* Added new [`sort-variables` compiler directive](doc/syntax/SYNTAX-5-OTHER.markdown#option-sort-variables) which ensures that user variables will be accessed in a well-defined order, to make inspecting the variables in the Mindustry processors **Vars** screen easier.
* Added generation of signature at the end of the compiled code. The signature is a `print` instruction (which is not executed) with the text `Compiled by Mindcode - github.com/cardillan/mindcode`. The signature is not added should the instruction limit be exceeded, or when the program doesn't naturally end in an unconditional jump. Adding the signature can be disabled in the command line tool by the `--no-signature` command line argument.  

### Changed

* Changed the names of [optimization levels](doc/syntax/SYNTAX-5-OTHER.markdown#option-optimization) from `off` and `aggressive` to `none` and `advanced`. The former names are still supported in the `#set` compiler directive, but not in the command-line options. 
* Changed the [Loop Hoisting](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting) optimization to analyze functions modifying variables inside loop instead of blanket assumption that all global variables may be changed by a function.
* Changes to the web app
    * A button was added do the web app to copy the output code to the clipboard with a single click.
    * It is now possible to select the optimization level directly in the web app user interface. The default optimization level is now `basic` for Mindcode compiler and `advanced` for Schemacode builder.
* Changed the syntax to allow an optional `do` keyword in all `for` and `while` loops, and optional `then` keyword in `if` and `elsif` statements.
* Changed the [Single Step Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to remove the `end` instruction at the very end of the program on `advanced` optimization level.
* Changed the command line tool to output final instruction numbers when printing out the unresolved code, for easier localization of errors reported when running the compiled code on an emulated processor.  

### Miscellaneous

* From this version (`2.2.0`) on, Mindcode will use semantic versioning (major version will be increased for significant changes to the syntax). The command-line tool now reports the version when passed in `-v` or `--version` as a parameter.  
* Replaced pattern matching in switch cases (JDK17 preview feature) with `if` and other constructs to remove the dependency on the `--enable-preview` switch.
* Updated existing Mindcode and Schemacode samples further to adapt for upcoming changes in syntax (optional keywords were added). Some samples were reworked.

## 2024-09-15

### Fixed

* Fixed slightly wrong (too low) cost estimation in the
  [Case Switching](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) optimization.

### Added

* New [`remark()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#remarks), as described
  [here](https://github.com/cardillan/mindcode/issues/140).

### Changed

* Slightly improved the [Case Switching](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) optimization - 
  instead of expanding the jump table range by one on each side and limiting the input value using the `op min` and 
  `op max` instructions, the jump table covers only the existing `when` branches and values outside the supported 
  range are handled using conditional jumps. This change saves two instructions and potentially speeds up the 
  execution for input values lying outside the jump table.

### Miscellaneous

* Some of the Mindcode samples used during unit tests were updated to use semicolons in anticipation to planned 
  Mindcode changes.  

## 2024-09-10

### Fixed

* Fixed wrong expression handling in loops by Data Flow Optimizer
  ([issue #131](https://github.com/cardillan/mindcode/issues/131)).
* Fixed Data Flow Optimizer not removing some superfluous variables when using `case` expressions
  ([issue #133](https://github.com/cardillan/mindcode/issues/133)).
* Fixed Data Flow Optimizer incorrectly applying common subexpression optimization in some cases
  ([issue #133](https://github.com/cardillan/mindcode/issues/133)).

### Added

* Added support for specifying processor code (both Mindcode and mlog) as a concatenation of multiple code snippets
  from various sources (string literal, string constant or external file). 
* Added new Mandelbrot Generator schematic example.

### Miscellaneous

* Starting from this release, releases will be [published in GitHub](https://github.com/cardillan/mindcode/releases) 
  including the Mindcode compiler provided as a `.jar` (binary) file. 

## 2024-03-12

### Fixed

* Fixed incorrect handling of inlined function return values
 ([issue #130](https://github.com/cardillan/mindcode/issues/130)).

## 2024-03-08

### Fixed

* Fixed the Data Flow Optimizer incorrectly reporting variables initialized inside an infinite `while` loop as 
  uninitialized ([issue #127](https://github.com/cardillan/mindcode/issues/127)).
* Fixed the Loop Hoisting Optimizer incorrectly hoisting assignments from a function return variable
  ([issue #129](https://github.com/cardillan/mindcode/issues/129)).

## 2024-02-17

### Added

* Added support for [block type](doc/syntax/SCHEMACODE.markdown#block-type-configuration),
  [unit type for payload-source](doc/syntax/SCHEMACODE.markdown#unit-configuration)
  and [unit command](doc/syntax/SCHEMACODE.markdown#unit-command-configuration) configurations to Schemacode compiler 
  and decompiler. This definitely resolves [issue #122](https://github.com/cardillan/mindcode/issues/122).
* Added support for handling list iteration loops and loop invariant `if` expressions to the
  [Loop Hoisting](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting) optimization.

### Fixed

* Fixed comments on the last line of source code causing compilation errors
  ([#110](https://github.com/cardillan/mindcode/issues/110)).

### Miscellaneous

* Schemacode compiler and decompiler now uses [mimex](https://github.com/cardillan/mimex)-generated metadata for 
  lists of items, liquids and unit commands. All Mindcode object definitions are now loaded from extracted metadata 
  and not from separate definitions, in Mindcode as well as in Schemacode.
* The documentation of [Loop Hoisting](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting) optimization now uses 
  diff-style mlog code listings to demonstrate the effects of optimizations on emitted code. The goal is to 
  gradually replace all suitable optimization examples to this format.

## 2024-02-10

### Added
 
* Added [Loop Hoisting](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting) optimization. This optimization 
  moves invariant code out of loops.
* Added new reserved keywords `elseif` and `elif`. Using these keywords will result in a compilation error (see
  [#121](https://github.com/cardillan/mindcode/issues/121)).

### Changed

* Schemacode decompiler now ignores some block-specific configurations that aren't supported yet (see
  [#122](https://github.com/cardillan/mindcode/issues/122)). This is a temporary measure to prevent the decompiler 
  crashing on such schematics until these configurations are fully supported.  

## 2024-01-28

### Fixed

* Fixed wrong optimization of side effects in the `when` expressions
  ([#119](https://github.com/cardillan/mindcode/issues/119)).

### Miscellaneous

* Changed AstContext to store `CallGraph.Function` instances instead of a function prefix (`String`) to identify 
  functions. All other classes that handle functions now operate on functions themselves instead of the prefixes. 
  Avoids function lookups from local prefixes and increases type safety. 

## 2024-01-22

### Added

* Added limited support for the new `sync` instruction through the `sync()` function. The function requires a global
  variable as a parameter (see [the `sync()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-sync-function) for 
  the description of the function and the limitations imposed on it).
* Added support for the [`effect` instruction](doc/syntax/FUNCTIONS_V7A.markdown#instruction-effect).
* Added more capabilities to the
  [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization): 
  * Replacing `@this.x` and `@this.y` expressions by the `@thisx`/`@thisy` built-in constants.
  * Replacing `@constant.id` expressions by an integer value of the ID assigned to the `@constant`, assuming it is 
    a known item, liquid, block type or unit type. This optimization is only active on `aggresive` optimization level. 
    For example, `id = @graphite.id` gets compiled down to `set id 3`, allowing further optimizations on the 
    constant value (`3`) to take place. 
  * Replacing multiplication by literal zero by an instruction setting the target variable to a zero directly, and 
    replacing multiplication/division by one and addition/subtraction of zero by an instruction setting the target 
    variable to the other operand directly.  

## 2024-01-19

> [!NOTE]
> Mindustry 7.0 build 146 added - among other improvements - the `id` property that provides an id of given 
> block, unit, item or liquid (basically an inverse of the `lookup` operation). Since Mindcode has a generic support 
> for properties and built-in constants, this property can be used right away, no explicit support from Mindcode is 
> needed. For example: 
>
> ```
> item = sorter1.config
> numericId = item.id
> print(numericId)
> 
> property = @id
> value = item.sensor(property)
> print(value)
>
> id = @graphite.id
> print(id)
> ```
>
> Among the new logic instructions, `autoPathfind` is already supported. Support for the new world-processor 
> instructions is yet to be added.    
 
### Added

* Added support for the `ucontrol autoPathfind` instruction through the `autoPathfind()` function
  ([PR #113](https://github.com/cardillan/mindcode/pull/113)).

### Fixed

* Fixed Loop Unrolling failing on loops that do not have a loop control variable
  ([#109](https://github.com/cardillan/mindcode/issues/109)).
* Fixed Function Inlining optimization incorrectly inlining some functions containing a return statement
  ([#116](https://github.com/cardillan/mindcode/issues/116)).
* Fixed Function Inlining optimization incorrectly inlining functions used in expressions
  ([#118](https://github.com/cardillan/mindcode/issues/118)).
* Fixed `turret` not being recognized as a block name ([#117](https://github.com/cardillan/mindcode/issues/117)).

### Changed

* Mindcode compiler now utilizes metadata extracted by [mimex](https://github.com/cardillan/mimex) to obtain - and
  keep current - a list of possible block names.
* Function Inlining optimizer now replaces function return variable with the receiving variable when inlining a 
  function call. The protection granted to function return variables is not necessary after inlining the call, and 
  this replacement allows the receiving variable to be handled by further optimization.

### Miscellaneous

* Small documentation improvements.
* No-op instructions are removed before the final optimization phase, avoiding the need to handle no-op by the final 
  phase optimizers.

## 2023-07-20

### Fixed

* Fixed a bug in code duplication routine that sometimes prevented [Unreachable Code
  Elimination](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination) from removing unreachable portions 
  of optimized case statements and inlined functions. The bug sometimes caused runtime exceptions when optimizing 
  for speed, usually under tight instruction space restrictions only.
* Fixed [#108 Compilation errors are not properly reported](https://github.com/cardillan/mindcode/issues/108).

### Changed

* Changed weight computation of user defined functions. Weight of a function is computed as a total weight of all 
  its `CALL`, resp. `CALLREC` instructions.

## 2023-07-15

### Added

* Added [Case Switching](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) optimization. This optimization converts 
  suitable case expressions to use jump tables.
* Added [Return Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#return-optimization) improving some very specific 
  cases sometimes arising in recursive functions.
* Added [compiler option](doc/syntax/SYNTAX-5-OTHER.markdown#option-instruction-limit) to alter the instruction limit 
  for speed optimizations. 

### Fixed

* Fixed wrong cost estimation in [Loop Unrolling](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling). The cost 
  estimates were too high, potentially preventing some eligible loops from being unrolled.
* Fixed compiler not recognizing integer values in compiler option directives (`#set`).

### Changed

* Changed [Jump Threading](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-threading) to also replace jumps leading to `goto`
  instructions with the `goto` instruction itself on `aggressive` level. There's a possible speedup in some stackless 
  function calls and list iteration loops.
* Changed [Single Step Elimination](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to also remove two 
  consecutive jumps that are identical. Such sequences were sometimes produced as a result of other optimizations.
* Changed [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) to use single 
  instruction iterator instance instead of making an instruction list from each AstContext being processed. 
  Significantly speeds up processing.
* Additional optimizers converted to adding no-op instruction instead of removing them; updated other optimizers 
  to correctly handle such code.

## 2023-07-12

### Fixed

* Fixed bug in [Function Inlining](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining) that caused runtime 
  exception in some circumstances.
* Fixed bug in [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) that may have 
  removed instructions that were actually used by the program.

### Changed

* Changed Data Flow Optimization to perform fewer iterations in certain situations. The reduction is substantial (up 
  to fivefold in tested scenarios) and translates into faster compilation times in these scenarios. 
* Changed optimizers that need to be compatible with AST context structure to replace instructions with a no-op 
  instead of removing them, preserving the AST context structure. Prevents potential bugs in optimizers relying on 
  AST context structure. 

## 2023-07-10

### Added

* Added list iteration loop unrolling to [Loop Unrolling](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling) 
  optimization.
* Added [Function Inlining](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining). Stackless functions and even 
  individual function calls may be selected for inlining based on expected benefit and available instruction space.

### Fixed

* Fixed incorrect weight computation of user defined functions. Currently, weight is based on a number of times a 
  function is called in the program. This is still not quite correct, though - the weight should be computed by 
  tracking call hierarchy and weights of contexts containing the calls.
* Fixed incorrect weight computation of list-iteration loops. The weight was set to the actual number of loops, 
  instead of the unified loop weight. 

### Changed

* Changed option name `conditional-jump-optimization` to `jump-optimization` to conform with 
  [documentation](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-optimization).

### Miscellaneous

* Eliminated `STORED_RETVAL` variable type. Values returned from user defined function calls are now assigned to
  standard temporary variables, as their usage is correctly tracked by Data Flow Optimizer and no longer requires 
  special handling. 
  
## 2023-07-08

### Fixed

* Fixed main variables being mistakenly reported as uninitialized by Data Flow Optimization on `basic` optimization 
  level.

### Changed

* Changed [Unreachable Code Elimination](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination) to perform 
  actual control flow analysis and remove all unreachable regions of code, instead of relying on active labels to 
  detect reachable instructions. This change eliminates some unreachable code that was not recognized before, such 
  as loops inside unreachable regions of code.
* Changed Data Flow Optimization to protect assignment to uninitialized variables made before calling an out-of-line 
  or recursive function that might call the
  [`end()` function](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#handling-of-uninitialized-variables). Hopefully all 
  possible means of calling the `end()` function are finally covered.
* Changed the printout of final list of unresolved instruction (activated by the `-u` command line option) to omit 
  inactive labels.

## 2023-07-06

### Added

* Added general [optimization for speed](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#optimization-for-speed) mechanism. 
  Various opportunities for optimizations that improve execution speed at the price of code size increase are 
  identified and realized in the order of decreasing efficiency until the opportunities or the available instruction 
  space are exhausted.   
* Added [Loop Unrolling](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling) optimization.
* Added [compiler option](doc/syntax/SYNTAX-5-OTHER.markdown#option-passes) to limit the number of performed 
  optimization passes.

### Fixed

* Fixed If Expression optimization failing to preserve variable value
  ([#101](https://github.com/cardillan/mindcode/issues/101)).
* Fixed `mcc.bat` and `webapp.bat` files missing in the `bin` directory.

### Changed

* Changed [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) to optimize [main 
  variables](doc/syntax/SYNTAX-1-VARIABLES.markdown#main-variables) even on `basic` level. Only final assignments to 
  main variables are preserved on `basic` level, other assignments can be optimized away. This change allows the Loop 
  Unrolling optimization to be functional even on `basic` optimization level.
* Changed condition duplication by Loop Optimization to employ the general
  [optimization for speed](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#optimization-for-speed) mechanism.   
* Changed various optimizations for minor improvements:
  * Number of times the Data Flow Optimization has to process loops was reduced.
  * Jumps Normalization now recognizes always true/always false conditional jumps based on known variable 
    values.
  * Improved handling of complex conditions in Loop Optimization.
  * Print Merging can merge print instructions across inactive jump labels.

## 2023-07-01

### Added

* Added support for executing multiple optimization passes. This allows individual optimizers to incrementally 
  improve the code while benefiting from changes made by other optimizers.

### Fixed

* Fixed incorrect handling of chained assignments ([#100](https://github.com/cardillan/mindcode/issues/100)). Two
  separate problems were fixed:
  * Chain-assigning a result of ternary operator/if expression could be compiled incorrectly.
  * Chain-assigning a volatile value (such as `@tick` or `@time`) could result in different values being written to
    variables.
* Fixed a bug in the processor emulator that incorrectly assigned some values to variables. New unit tests uncovered
  the bug; processor emulator is not used in production code at the moment.

### Changed

* The [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) now properly handles 
  assignments to uninitialized variables made before calling the
  [`end()` function](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#handling-of-uninitialized-variables). 

### Removed

* Removed _Return value optimization_ and _Temporary inputs elimination_. These optimizations were completely 
  superseded by the [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization).
* Removed the old command-line compiler and the `mindcode` / `mindcode.bat` files in the `bin` directory.

## 2023-06-19

### Added

* Added support for the [`unitHealth` function](doc/syntax/FUNCTIONS_V7.markdown#instruction-setrule).

### Fixed

* Fixed Data Flow Optimization incorrectly handling `break`, `continue` and `return` statements 
  ([#98](https://github.com/cardillan/mindcode/issues/98)).

### Miscellaneous

* Statistics about executions of processor-based unit tests are now collected and committed to the repository, 
  allowing to track the evolution of the efficiency of generated code over time. 

## 2023-06-18

### Added

* Added support for the new [`angleDiff` operation](doc/syntax/FUNCTIONS_V7.markdown#instruction-op) in Mindustry v145.

## 2023-06-17

### Added

* Added elimination of useless `set` instructions (such as `set x x`) to
  [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization). Optimizes recursive 
  function calls passing unchanged value of function argument to the next function call. 
* Added elimination of variables never modified by a function from stack in
  [Stack Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization).
* Added specific optimization for recursive function calls to the
  [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization):
  * The optimizer is aware when a variable value is preserved on stack and can reuse the variable state when 
    optimizing code following a recursive function call.
  * Streamlining expressions when passing value derived from the current value of the function argument to the 
    recursive call (e.g. calling `foo(n - 1)` in function `foo(n)`).

### Fixed

* Fixed Data Flow Optimization crashing when source code contained unused user defined functions
  ([#97](https://github.com/cardillan/mindcode/issues/97)).
* Fixed misspelled name of the `case-expression-optimizer` command-line and compiler directive option (was 
  `ease-expression-optimizer` before).

### Changed

* Docker configuration updated to not expose the PostgreSQL port to the host machine. It is now possible to run 
  Mindcode in Docker even when PostgreSQL is also installed and running on the host machine.

### Miscellaneous

* Added processor unit tests based on Project Euler problem solutions.
* Added statistics generation for processor unit test (captures size of the compiled program, number of steps to 
  reach the solution, and code coverage in terms of mlog instructions). These should document improvements in 
  quality of the generated code. 

## 2023-06-16

### Added

* Added [Constant folding](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#constant-folding) to Data Flow Optimization.
* Added [Common subexpression optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#common-subexpressions-optimization) 
  to Data Flow Optimization.

### Removed

* Removed _Function call optimization_. This optimization was completely superseded by the
  [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization), which handles all 
  cases previously optimized by Function call optimization, and is able to identify more opportunities for 
  optimization. The old optimization was removed because it became incompatible (i.e. produced wrong results) 
  with the code produced by Data Flow Optimization.

### Deprecated

* Deprecated _Return value optimization_. This optimization was completely superseded by the
  [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization), which handles more cases 
  than the old optimization. The Return value optimization will be removed when it becomes incompatible with 
  further changes to code generation/optimization.  
* Deprecated _Temporary inputs elimination_, for the same reasons as above. 

## 2023-06-11

### Added

* Added [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization). This optimization could 
  remove user defined variables from compiled code - see the documentation for details. 

### Fixed

* Fixed some syntax errors in source programs not being properly reported. Some syntax errors were ignored, others 
  might have caused unhandled exceptions in the web or command line application. When a syntax error occurs, 
  partially generated code is no longer displayed in the web application.      
* Fixed compiler not producing any code when optimizations were switched off.

## 2023-06-08

### Fixed

* Fixed incorrect creation of schematics containing bridges. Configuration for bridges and other blocks that connect 
  to a single other block was mistakenly written as an array of connections into a `.msch` file, which are ignored 
  by Mindustry.   

## 2023-06-02

Note: the bug fixed in this release only affects the command line tool. The web app hasn't been redeployed.

### Fixed

* Fixed the command line tool application not properly recognizing actions.

## 2023-05-30

### Added

* Added [If Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#if-expression-optimization). Sometimes 
  it only decreases the number of instructions by rearranging them, in other cases it can decrease number of executed 
  instructions. Only ternary expressions and if statements containing both true and false branch are affected. 

### Fixed

* Fixed the [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) not having upper 
  boundary fixed under some conditions. The feature announced in release [2023-05-03](#2023-05-03) wasn't fully 
  implemented until now.
* Fixed bugs in the [stack optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization). In some cases, 
  `push`/`pop` instructions were mistakenly removed, in other cases unnecessary `push`/`pop` instructions were left
  in the code. Current implementation utilizes AST structure metadata to identify and protect variables used in 
  loops.   
  
### Miscellaneous

* Generated mlog instructions are now linked to the AST nodes they were created from (via instances of AstContext),
  and through them to the parser tokens (for now in somewhat coarse granularity).
  * This allows optimizers to inspect source AST nodes of individual instructions, obtaining information about code 
    structure. Optimizations based on these metadata should be easier to write and understand, once necessary 
    support tooling is in place.
  * It is necessary to update the AST context structure after each optimization iteration. These updates aren't done 
    of the fly -- optimizers must understand that the changes they make to the program in a single iteration aren't 
    reflected to the AST context structure.
  * Optimizers need to be careful to create newly generated instructions using the right (already existing) AST 
    context, otherwise subsequent AST context-based optimizers can misunderstand the code structure. There are means
    to duplicate existing code while deep-copying the context structure.
  * Instructions can be linked to source code. At this moment it can be only displayed using the `-f source` command 
    line argument, hopefully in the future a better error reporting will be built using these metadata.   

## 2023-05-23

### Added

* Added option for [code generation goal](doc/syntax/SYNTAX-5-OTHER.markdown#option-goal). Allows to specify whether 
  to aim for a smaller code or a faster code.
* Added basic [loop optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-optimization).

## 2023-05-21

Note: the bug fixed in this release only affects the command line tool. The web app hasn't been redeployed.

### Fixed

* Fixed the command line tool application crashing when decompiling a schematic.  

## 2023-05-20

### Added

* Added language target `7A`, i.e. Mindcode Logic version 7, revision A. Uses the same instruction set as `7`, except  
  the `getBlock` and `ulocate` functions returning the located block/building.
  See [`getBlock`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ucontrol),
  [`ulocate`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ulocate). Target `7` is still default both in webapp 
  and in the command line tool, so the change doesn't break existing code.
* Added support for the reintroduced `ucontrol pathfind` instruction. When issued, navigates current unit to given 
  location using AI to avoid obstacles. See [`ucontrol`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ucontrol). 
* Added command line options for [Schematics Decompiler](doc/syntax/TOOLS-CMDLINE.markdown#decompile-schematic-action-help)
  to specify order in which blocks are written to the schematic definition file and when to generate the `facing` 
  directive.
* Added support for [color configuration](doc/syntax/SCHEMACODE.markdown#color-configuration) and 
  [unit configuration](doc/syntax/SCHEMACODE.markdown#unit-configuration) in Schemacode. 
* Added [overlapping blocks detection](doc/syntax/SCHEMACODE.markdown#block-position). Overlapping blocks now cause 
  compilation errors.
* Added support for [non-zero schematic origin](doc/syntax/SCHEMACODE.markdown#origin-and-dimensions-calculation).
* Added validation of [bridge](doc/syntax/SCHEMACODE.markdown#connecting-bridges) and
  [mass driver](doc/syntax/SCHEMACODE.markdown#connecting-mass-drivers) connections. Invalid links now cause 
  compilation errors.
* Added [validation of power node connections](doc/syntax/SCHEMACODE.markdown#connecting-power-nodes). Invalid links 
  now cause compilation errors.
* Added support for unidirectional declaration of connections between power nodes: it is no longer necessary to 
  declare a connection between two power nodes in both directions to have it reliably created when building the 
  schematic.   

### Fixed

* Fixed handling of block label reuse in schemacode. Reusing the same label for multiple blocks now causes an error.
* Fixed [#93: Mindcode compiled wrong when using semicolons](https://github.com/cardillan/mindcode/issues/93).

## 2023-05-13

### Added

* Added Schematics Builder tool with a new [Schemacode language](doc/syntax/SCHEMACODE.markdown).
* Added new [command-line interface](doc/syntax/TOOLS-CMDLINE.markdown) for the Mindcode Compiler, Schematics 
  Builder and Schematics Decompiler.
* Added [Schematics Builder](http://mindcode.herokuapp.com/schematics) and
  [Schematics Decompiler](http://mindcode.herokuapp.com/decompiler) interface to the web application. 

### Changed

* **Breaking:** changed names of
  [individual optimization options](doc/syntax/SYNTAX-5-OTHER.markdown#individual-optimization-options) from
  `camelCase` to `kebab-case`. The same option names are now used with the new command line tool as in the `#set` 
  directive.

### Deprecated

* Deprecated the old command-line compiler, it was replaced by the new command line tool and will be removed in one of 
  the future releases.

### Miscellaneous

* Created [Mindustry Metadata Extractor](https://github.com/cardillan/mimex), a Mindustry mod. The mod, when loaded 
  into the game, extracts various Mindustry metadata and writes them into external files to be used by Mindcode. At 
  this moment, only block types for Schematics Builder are extracted, but more metadata will follow.
* Created [Schematics Refresher](https://github.com/cardillan/SchematicsRefresher), another Mindustry mod. The mod
  refreshes schematics stored in the `/schematics` subdirectory whenever the game loads or the Refresh button in the 
  Schematics screen is used. The mod allows to refresh schematics after they were rebuilt by Schematics Builder. 

## 2023-05-03

### Added

* Added support for using [Mindustry built-in icons](doc/syntax/SYNTAX-1-VARIABLES.markdown#built-in-icons) in 
  `print` functions.
* Added support for compile-time [string expressions](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#strings-in-expressions). 
  Allows - among other things - to embed icons into string constants at compile time, such as
  `const HEADER = "Using unit: " + UNIT-MEGA`    

### Changed

* **Breaking:** changed [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) to 
  evaluate upper boundary only once, before entering the loop for the first time. Previous version evaluated 
  the upper bound at each iteration, and reflected possible changes in the upper bound. The documentation was 
  expanded to specify the evaluation of the upper bound. Use a while loop or a C-style loop if you want to fully 
  evaluate the loop condition at each iteration.
* Changed handling of non-constant string expressions: when detected, a compilation error occurs (see also 
  [string expressions](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#strings-in-expressions)).

### Miscellaneous

* The `compiler` module has been renamed to `mindcode`. The `compiler` module name will be repurposed for a 
  new command-line interface to the `mindcode` module and an upcoming
  [`schemacode` module](https://github.com/cardillan/mindcode/issues/90).  

## 2023-04-30

### Changed

* Enhanced [print merging optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#print-merging) to merge non-string 
  literals (e.g. numeric constants) on aggressive optimization level. 
* Changed handling of hexadecimal and boolean literals to include range checks and refuse literals outside valid 
  range (signed 64-bit integer; note that [Mindustry Logic variables](doc/syntax/SYNTAX-1-VARIABLES.markdown) cannot 
  represent full range of possible values without loss of precision).   

### Miscellaneous

* Logic instructions arguments are represented by objects with proper attributes and type information instead of 
  plain strings.  

## 2023-04-18

### Added

* Added _Return value optimizer_. Improves processing return values from function calls. 
* Added [section about using units](doc/syntax/MINDUSTRY-TIPS-N-TRICKS.markdown#using-units) to Mindustry Tips 
  and Tricks.
* Added warnings when constant expression evaluation or Mindcode numeric literal cannot be compiled to mlog without 
  a loss of precision. 

### Changed

* Changed [`ubind` function](doc/syntax/FUNCTIONS_V7.markdown#instruction-ubind) to return the freshly bound unit.
* Changed [encoding of numerical values to mlog](doc/syntax/SYNTAX.markdown#numeric-literals-in-mindustry-logic).

### Fixed

* Fixed constant expression evaluation crashing on binary numeric literals.
* Fixed constant expression evaluation producing values not representable in mlog source code.
* Fixed function call optimization not processing numeric literal arguments in some cases.

## 2023-04-14

### Added

* Added support for using [binary numeric literals](doc/syntax/SYNTAX.markdown#numeric-literals-in-mindustry-logic) 
  (e.g. `0b00101`) in Mindcode source.
* Added support for using
  [scientific notation in numeric literals](doc/syntax/SYNTAX.markdown#numeric-literals-in-mindustry-logic) 
  in Mindcode source. Literals compatible with mlog are kept unchanged, literals unrecognized by mlog (e.g. `1.5e-5`)
  are converted to mlog-compatible representation (in this case, `15e-6`).
* Added [simple expression optimizer](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization).
  Current implementation replaces `mul` by a constant or an `idiv`/`div` followed by a `floor`
  with a single `idiv` instruction.
  
### Changed

* Updated project to use Java 17.

### Fixed

* Fixed constant expression evaluation creating numeric literals incompatible with mlog. (Mlog recognizes  
  decimal point and an exponent, but not both in the same literal; `1.5e-5` is not a valid mlog number.)

## 2023-04-12

### Fixed

*  Fixed optimizer incorrectly merging instructions across `printflush()` calls 
  ([#91](https://github.com/cardillan/mindcode/issues/91)).

## 2023-04-08

### Added

* Added limited support for [compile-time constant expression evaluation](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#constant-expressions).
* Added support for [constant declaration](doc/syntax/SYNTAX-1-VARIABLES.markdown#constants) (`const foo = 1 / 3`).
  The value assigned must be a compile-time constant expression.
* Added [compiler directives](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown) (`#set`): optimization and target instruction set 
  can be now set from the web app compiler.
* Support for the new [`setprop` world processor instruction](doc/syntax/FUNCTIONS_V7.markdown#instruction-setprop)
  available in Mindustry v143.  

### Changed

* Numeric literals are now placed directly into code without intermediate temp variable allocation.
  Produces better unoptimized code (optimization would remove the variables anyway).
* String constants, not just string literals, are accepted as the first printf() argument.
* Numeric constants, not just numeric literals, are accepted in stack and heap declarations.
* Minor optimization improvements.
* [Syntax documentation](doc/syntax/SYNTAX.markdown) overhauled.

### Fixed

* Fixed lots of typos both in produced outputs and supporting documentation. 

## 2023-04-01

### Changed

* Warning and information messages from the compiler/optimizer are now shown in the web app.

### Fixed

*  Fixed parameters not being passed to inline functions ([#89](https://github.com/cardillan/mindcode/issues/89)).

## Earlier releases

Full changelog wasn't kept for earlier releases. What we know is documented separately here (click the date for fuller description): 

### [2023-03-29](/doc/announcements/2023-03-29.markdown)

* Several breaking changes were made: new keywords, different handling of variables corresponding to linked blocks,
  significant changes to functions, stricter instruction argument validation.  
* Full support for Mindustry Logic 7.
* Overhaul of user-defined functions: inline, stackless and recursive, local scope for function variables
  and parameters, return statement. 
* Improved case expressions
* Do-while loops and list iteration loops
* Break/continue statements for loops
* printf() and println() functions
* All known bugs fixed
 
### [2021-03-27](/doc/announcements/2021-03-27.markdown)

* User defined functions
* More examples
* ...
 
### [2021-03-18](/doc/announcements/2021-03-18.markdown)

* Added [syntax documentation](doc/syntax/SYNTAX.markdown)
* Added full support for drawing, `uradar` and `ulocate`
* Added support for heaps, meaning auto-allocated global variables
* Added case / when expressions
* For loops
* Added support for the ternary operator, a more compact form of if/else expression: `self = @unit == null ? bind(@unit) : @unit`
* Added a new sample: [patrol around a building and heal any damaged ones](http://mindcode.herokuapp.com/?s=heal-damaged-building)

### [2021-03-16](/doc/announcements/2021-03-16.markdown)

The first release.
