# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project now adheres to [Semantic Versioning](https://semver.org/).

## 3.7.0 - Unreleased

### Added

* Added [mlog blocks](/doc/syntax/SYNTAX-EXTENSIONS.markdown#mlog-blocks) for embedding complex mlog logic into Mindcode sources. 

## 3.7.0 - 2025-06-24

### Fixed

* Mindcode incorrectly warned about possible loss of precision for integer literals larger than 2<sup>52</sup>-1. The correct maximum safe integer value is 2<sup>53</sup>-1.
* Added missing `setrule` instruction variants (`canGameOver`, `dragMultiplier`, `ban`, `unban` and `unitMineSpeed`) for target 8. 

### Added

* Added support for the new `setmarker outline` instruction.
* Added the new `@operations` property.
* Added the [`target-guard` compiler option](/doc/syntax/SYNTAX-5-OTHER.markdown#option-target-guard). When set, generates a guard code which verifies the code is run by a Mindustry version compatible with both the `target` and `builtin-evaluation` options.
* Added a new [`compatibility` system library](/doc/syntax/SYSTEM-LIBRARY.markdown#compatibility-library). The library provides a function that verifies Mindcode's compatibility with the Mindustry version in which it is run.   

### Changed

* **Breaking:** the `target-optimization` compiler option has been replaced with the [`builtin-evaluation` option](/doc/syntax/SYNTAX-5-OTHER.markdown#option-builtin-evaluation). Instead of `#set target-optimization = specific;` use `#set builtin-evaluation = full;`.
* Improvements to the [Case Switching optimization](/doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching):
  * The optimization now processes integer case expression with ranges too if ranges do not overlap other ranges or standalone values. 
  * The optimization now uses bisection search to locate the proper segment when performing [Jump Table Compression](/doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-table-compression).
  * The jump table can be padded not just towards zero, but also towards the maximum value, to remove the need for the range check. Requires `builtin-evaluation` to be set to `full`. 
  * The `case-optimization-strength` option now has a range from `0` to `6`. Each additional value significantly increases the number of segment arrangements considered, as well as optimization time. The value of `0` doesn't consider any other configuration except a full jump table, effectively turning Jump Table compression off.
  * When `case-optimization-strength` is greater than zero, the optimizer also generates segment configuration for a full bisection search. This may improve case expressions which are too small for a full jump table optimization.
  * The bodies of `when` branches are moved into correct places inside the case expression when possible, to avoid unnecessary jumps.
  * On the `experimental` optimization level, the bodies of `when` branches may be duplicated to avoid even more jumps at the cost of additional code size increase. This optimization usually only kicks in with jump table compression for small branch bodies, since for larger code increases, a better performing solution can be achieved by a different segment arrangement. 
  * The performance of the Case Switching optimization (in terms of compilation time) has been significantly improved.  
* Small improvements to the `printExactHex` and `printExactFast` functions in the `printing` library.

### Miscellaneous

* Added a `legacy` attribute to the Blocks metadata.
* Added new types of metadata corresponding to all opcode selectors.
* Updated the BE version metadata to the latest available BE build.
* Added automatic test for verifying that existing instruction opcodes correspond one-to-one to the known metadata. 

## 3.6.1 - 2025-06-06

### Fixed

* Fixed the compiler refusing compile-time expressions with unrepresentable values ([#262](https://github.com/cardillan/mindcode/issues/262)).
* Fixed the Data Flow optimization producing unrepresentable mlog values ([#263](https://github.com/cardillan/mindcode/issues/263)).
* Fixed the Print Merging optimization merging prints across function calls ([#264](https://github.com/cardillan/mindcode/issues/264)).
* Fixed wrong evaluation of compile-time expressions with character literals ([#265](https://github.com/cardillan/mindcode/issues/265)).
* Fixed erroneous loop unrolling optimization ([#266](https://github.com/cardillan/mindcode/issues/266)).
* Fixed internal error in Schemacode builder on missing link name ([#267](https://github.com/cardillan/mindcode/issues/267)).

### Added

* Added support for the [`>>>` (unsigned right shift) operator](/doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#shift-operators) to the compiler for targets preceding `8`. In these earlier targets, the operation is emulated by a sequence of up to seven instructions.   
* Several [compiler-defined constants](/doc/syntax/SYNTAX-1-VARIABLES.markdown#compiler-defined-constants) were added.
* The `printExactBinary` and `printExactHex` functions were added to the [`printing` system library](/doc/syntax/SYSTEM-LIBRARY.markdown#printing-library). The functions print all digits (64 or 16, respectively) of the input number, without a prefix. Negative numbers are printed without a minus sign, but with the sign bit set.
* Added missing support for new Mindustry 8 blocks to Schemacode (e.g., `@landing-pad` including support for configuration, and `@tile-logic-display`).

### Changed

* Inactive branches in `if` expressions with compile-time constant conditions are now omitted from compiling, effectively amounting to conditional compilation. Note: this feature was already implemented in past versions, but got meanwhile deactivated due to a bug.
* The `formatBinaryNumber`, `printBinaryNumber`, `formatHexNumber` and `printHexNumber` functions in the `printing` system library were updated to support formatting/printing negative numbers. A minus sign is printed in front of a negative number, including its prefix.

## 3.6.0 - 2025-06-04

### Fixed

* Fixed the `sync()` function not requiring a volatile variable as an argument ([#260](https://github.com/cardillan/mindcode/issues/260)).
* Fixed the Data Flow Optimization incorrectly removing a condition ([#261](https://github.com/cardillan/mindcode/issues/261)).

### Added

* **Breaking:** added new [`guarded` keyword](/doc/syntax/SYNTAX-1-VARIABLES.markdown#guard-code-for-linked-variables). The keyword is a variable declaration modifier that ensures the generation of the guard code for linked variables.
* Added support for the new `op logn` Mindustry BE instruction to the compiler and processor emulator.
* Added the `logn()` function to the `math` system library. This function corresponds to the Mindustry Logic 8 instruction `op logn`, and when target `8` is selected, the instruction is used instead of the library implementation.
* Added the [`%%` (positive modulo) operator](/doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#multiplicative-operators) to the compiler and processor emulator. When target `8` is selected, the native Mindustry Logic instruction is used; for lower targets, the operation is emulated by a sequence of three instructions.
* Added the [`>>>` (unsigned right shift) operator](/doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#shift-operators) to the compiler and processor emulator. Only available for target `8` or higher.
* Added the ability to declare new keywords, built-in variables, and linked block names through the [`#declare` directive](/doc/syntax/SYNTAX-EXTENSIONS.markdown).
* Added support for [constant arrays](/doc/syntax/SYNTAX-1-VARIABLES.markdown#internal-arrays-1). Elements of a constant array aren't stored in processor variables but are used directly in the generated mlog program.
* Added the [`scaleDisplay` function](/doc/syntax/SYSTEM-LIBRARY.markdown#scaledisplay) to the `graphics` system library. The function compensates for rounding errors in Mindustry Logic `draw scale` instruction. 
* Added the [`noControlWithin` function](/doc/syntax/SYSTEM-LIBRARY.markdown#nocontrolwithin) to the `units` system library. The function determines whether the current unit lies within a given radius without taking control of the unit.

### Changed

* Updated the `scaleSmallToLarge()` and `scaleLargeToSmall()` functions to compensate for rounding errors in Mindustry Logic `draw scale` instruction.
* Updated the `log2()` function to use `op logn` when possible.
* Updated all conditional operators to support string arguments (e.g., `name == "Phillip"` is now a valid expression).
* The `noinit` keyword has no longer any effect in `linked` variable declarations. Guard code is not generated unless the `guarded` modifier is used to explicitly request it.
* Changed the [Case Switching optimization](/doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) to generate many different ways to compress a jump table, selecting the most efficient one for the given optimization goal and instruction-space constraints.
* Removed the optimization goal `auto` and added `neutral`. Implemented new optimization selection according to the [optimization goal](/doc/syntax/SYNTAX-5-OTHER.markdown#option-goal). The Case Switching optimization is capable of generation optimizations for the `size` and `neutral` goals. 

### Miscellaneous

* Added an explanatory message to the web app when an internal error is encountered.
* Added a case switching optimization-specific test suite to the Extended Testing framework. The tests serve to verify the size and execution costs calculations for the Case Switching optimization are correct.

## 3.5.2 - 2025-05-24

### Fixed

* Fixed function calls with missing arguments causing compiler error ([#259](https://github.com/cardillan/mindcode/issues/259)).

## 3.5.1 - 2025-05-20

### Fixed

* Fixed an internal error compiling loops containing a function call ([#258](https://github.com/cardillan/mindcode/issues/258)).

## 3.5.0 - 2025-05-19

### Fixed

* Fixed wrong `unsafe-case-switching` optimization for small jump tables ([#253](https://github.com/cardillan/mindcode/issues/253)).
* Fixed wrong handling of side effects by the Case Switching optimization ([#254](https://github.com/cardillan/mindcode/issues/254)).
* Fixed incorrect IDs produced by the `sensor ... @id` instruction ([#255](https://github.com/cardillan/mindcode/issues/255)).
* Fixed unrecognized keywords for some instructions ([#256](https://github.com/cardillan/mindcode/issues/256)).
* Fixed incorrect block type obtained through the `lookup` instruction in compile-time evaluation and the processor emulator for zero logic ID.  

### Added

* Added support for the new `op sign`, `op round` and `unpackcolor` Mindustry BE instructions to the compiler and processor emulator.
* Added the `output-profiling` compiler option, which causes the profiling output (number of times each instruction was executed) to be written to the log file.

#### Experimental

* Added support for using `null` literals in case expression. When used in case expressions involving Mindustry content, `null` values in the `when` clauses are supported by the Case Switching optimization too.
 
### Changed

* **Breaking**: The system library was changed to accommodate the new logic instructions:
  * The `sign` function in the `math` library was renamed to `signInexact`.
  * The `signExact` function in the `math` library was renamed to `sign`. This function corresponds to the Mindustry 8 instruction `op sign`, and when target 8 is selected, the instruction is used instead of the library implementation. 
  * The order of parameters of the `unpackcolor` function in the `graphics` library was changed to match the `unpackcolor` instruction in Mindustry 8. When target 8 is selected, the instruction is used instead of the library implementation.
* When splitting jump tables into multiple segments during jump table compression, the instruction jumping to the next segment is always placed first to make the overall execution of the optimized case expression faster.
* Other improvements to the Case Switching optimization: better optimization, more precise cost and benefit calculation, support for `null` values.

### Miscellaneous

* Mindustry Logic functions take precedence over functions defined in the system library. This allows system libraries to contain functions that can be used when a corresponding Mindustry Logic function doesn't exist in the current target.
* A list of supported keywords and built-in variables for instruction parameters are now provided by mimex-generated metadata.  
* Added a list of accepted built-in variables to the description of functions in the function reference.
* The docker definition was updated to avoid unnecessary recompilations (courtesy of 3bd).
* Updated metadata corresponding to the BE version to the latest available BE build.
* Added new metadata types, fixed zero logic IDs problem.

## 3.4.0 - 2025-05-11

### Fixed

* Removed unnecessary condition duplication for empty loops ([#252](https://github.com/cardillan/mindcode/issues/252)).

### Added

* Added new functions to the [`graphics` library](doc/syntax/SYSTEM-LIBRARY.markdown#graphics-library):
  * Added `setAlpha()` function which takes a packed color as an argument (including e.g., named color literals) and returns a packed color with an updated alpha channel.
  * Added `packHsv()` function which creates a packed color value out of `hue`, `saturation`, `value` and `alpha` components.
* Expanded the [Case Switching optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching):
  * Range checking of the input values may be suppressed using [`unsafe-case-optimization`](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#range-check-elimination) compiler directive.
  * Case expressions based on Mindustry content (e.g., items, block types, and so on) [can be optimized](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#mindustry-content-conversion) by converting the values to logic IDs and building jump tables using these numerical values.
  * Large jump tables containing a lot of unused values may be [compressed](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-table-compression) to save space.

#### Experimental features

* Added [module signatures](doc/syntax/REMOTE-CALLS.markdown#module-signatures) to the remote call mechanism to prevent binding to an incompatible remote module.
* Added support for [using the same module in multiple remote processors](doc/syntax/REMOTE-CALLS.markdown#multiple-instantiations-of-a-remote-module).
* Added an optional output parameter to the [`finished()` function](doc/syntax/REMOTE-CALLS.markdown#asynchronous-remote-calls), which receives the resulting value of the remote function call if it is already finished.  

### Changed

* **Breaking**: changed the command-line arguments of the offline compiler:
  * The `-o` command-line option no longer sets the optimization level, but specifies the name of the output file. 
  * The output file needs to be specified using the `-o` or `--output` named argument.
  * There's a new `-O` option taking a numerical value (optimization level). Values `0` to `3` correspond to optimization levels `none`, `basic`, `advanced` and `experimental`.  
* **Breaking**: the remote call mechanism was redesigned. When recompiling code in a processor which uses remote calls, all related processors need to be recompiled too.
* The mlog decompiler replaces illegal characters in mlog variable names with underscores. If the names of some variables collide due to these conversions, a numeric index is appended to some of them until a unique name is found. 

### Miscellaneous

* Added an instruction index to the function reference. 
* Added a list of possible keywords to the description of functions in the function reference.
* Added references to the relevant portion of Yruei's documentation to function reference.

## 3.3.3 - 2025-04-26

### Fixed

* Fixed an internal error when compiling unused functions ([#247](https://github.com/cardillan/mindcode/issues/247)).
* Fixed a whitelabel error in the web application when an internal compilation error occurs. The error is reported as a standard error message instead.

## 3.3.2 - 2025-04-26

### Added

* Support for dark mode in the web application (courtesy of 3bd). Light/dark mode follows the browser preference.
* Support for named color literals (e.g., `%[red]`) in target 8.

### Changed

* Hexadecimal and binary literals can be specified as 64-bit unsigned numbers (e.g., `0xFFFFFFFFFFFFFFFF` represents `-1`). These literals are encoded to mlog as is when possible. If the literal cannot be properly parsed by the target Mindustry version, it is encoded as a signed decimal literal.
* Negative hexadecimal and binary literals are encoded to mlog as is (with possible conversion to lower case). If the literal cannot be properly parsed by the target Mindustry version, it is encoded as a signed decimal literal.

### Miscellaneous

* Fixed (hopefully) Discord links in the Readme.

## 3.3.1 - 2025-04-16

### Fixed

* Fixed the logic ID translation from blocks to IDs and back not handling all types of blocks ([#246](https://github.com/cardillan/mindcode/issues/246)).  
* Fixed a small bug in jump tables generation for unused arrays.

### Added

* A document offering some [tips on writing a better performing Mindcode](/doc/syntax/PERFORMANCE-TIPS.markdown). 

### Changed

* Improved list iteration loops generation for symbolic labels to perform the same as a code compiled without symbolic labels.
* Small improvements in chained random array access optimizations.

### Miscellaneous

* Updated metadata corresponding to the BE version to the latest available BE build.

## 3.3.0 - 2025-04-11

### Fixed

* Fixed some command-line options not having an effect in the command-line tool ([#231](https://github.com/cardillan/mindcode/issues/231)).
* Fixed wrong handling or hoisted set instruction setting up the return address in later loop unrolling ([#234](https://github.com/cardillan/mindcode/issues/234)).
* Fixed optimizations removing the `spawn` instruction when the output value was not used ([#236](https://github.com/cardillan/mindcode/issues/236)).
* Fixed Jump Optimization not performing the optimization in unrolled loops.
* Fixed an error in compile-time evaluation of an expression involving a character literal ([#240](https://github.com/cardillan/mindcode/issues/240)).
* Fixed incorrect compile-time evaluation of some logic IDs ([#242](https://github.com/cardillan/mindcode/issues/242)).
* Fixed possible incorrect handling of arguments passed to the `print()` and other output functions ([#243](https://github.com/cardillan/mindcode/issues/243)).

### Added

* **Breaking:** new `ref` keyword was added to the language. Code that uses this keyword as a function or variable name will not compile, and the variable or function will have to be renamed.
* Added the [`char()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-char-function). The function returns the ASCII value of a character at given index from a string value using the `read` instruction, as supported in the latest BE version.
* Added support for invoking properties and the `sensor` functions on string values to support latest BE Enhancement of sensing string lengths using `@size`. 
* Added the [`strlen()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-strlen-function) returning the length of a string determined at runtime.
* Added support for compile-time evaluation of the `length()` function, allowing to specify offsets relative to the end of the array in subarrays (e.g., `array[0 ... length(array) - 1]`).
* Added compile-time evaluation of either stable, or all built-in numerical variables (such as `@pi` or `@unitCount`).
* Added a new remarks mode, `comments`, to compile remarks as mlog comments (`# comment`).
* Added support for generating runtime boundary checks for explicitly declared external arrays.
* Added new `printLines()` function to the `printing` library. The function prints each of its arguments on a new line. 
* Loop Hoisting optimizer enhanced with an ability to optimize instructions setting up return addresses of function calls. 
* Added handling of numerical literal values unsupported by Mindustry Logic version 7 and earlier (namely, `-2147483648`).
  When a numerical literal or constant expression has this value, a compile-time error is generated.
* Added support for strings in `read` and `sensor` instructions to processor emulator. 
* Added a new [`--file-references` command-line option](/doc/syntax/TOOLS-IDE-INTEGRATION.markdown#file-references).
* Added a suggestion of the closest matching alternative when an unknown compiler directive or option value is found.
* Added support for mlog syntax highlighting into the provided [IntelliJ file type settings](/doc/syntax/TOOLS-IDE-INTEGRATION.markdown#intellij-idea).

#### Experimental features

* Added support for passing arguments to inline functions [by reference](/doc/syntax/SYNTAX-4-FUNCTIONS.markdown#function-parameters). It is possible to pass variables and arrays this way.
* Added new `target-optimization` compiler directive/command line option. The `specific` option generates code for the specific compilation target only, the `compatible` option generates code intended for the compilation target and future versions of Mindustry Logic.    
* Added [array-specific optimizations for speed](/doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#array-optimization) (available on `experimental` level).
* Added new [`arrays` system library](/doc/syntax/SYSTEM-LIBRARY.markdown#arrays-library) with some basic array functions. The size calculations for the library functions are possibly incorrect, as new means for determining code size of functions taking an array as a ref argument needs to be developed.    
* Added support for generating symbolic labels instead of instruction addresses in jump instructions, through the [`symbolic-labels` compiler directive/command line option](/doc/syntax/SYNTAX-5-OTHER.markdown#option-symbolic-labels).
* Added support for applying indenting to the generated mlog code based through the [`mlog-indent` compiler directive/command line option](/doc/syntax/SYNTAX-5-OTHER.markdown#option-mlog-indent).

### Changed

* **Breaking**: underscores in multi-word command-line options were changed to hyphens.
* The metadata used by Mindcode compiler and processor emulator now correspond to the compilation target. Schemacode still uses the latest version of the metadata for both building and decompiling schematics.
* Compile-time evaluation cache is cleared when exiting a function context. This forces primarily the `length()` function to be reevaluated in each call to an inline function, as its value depends on the actual arguments passed to the function. 
* Improved optimization of jumps by making multiple passes over jump-related optimizers, up to the optimization passes limit.
* Volatile built-in variables used an upper or lower bound in a ranged for-loop statement are used directly in the condition, without storing them in a temporary variable.
* Stripped unnecessary `.0` distinctions from local variable prefix.
* The `noinit` modifier is no longer disallowed for local variables.
* The processor emulator uses numerical values provided by metadata for built-in variables (such as `@pi` or `@unitCount`). The metadata version corresponds to the compilation target.  

## 3.2.1 - 2025-03-23

### Fixed

* Fixed errors when trying to invoke properties on internal array elements ([#228](https://github.com/cardillan/mindcode/issues/228)).
* Fixed a wrong optimization of list iteration loops ([#229](https://github.com/cardillan/mindcode/issues/229)).
* Fixed incorrect optimization of `if` expressions ([#230](https://github.com/cardillan/mindcode/issues/230)).
 
### Added

* Added a warning when a variable declared volatile is not used anywhere in a program, which would preclude remote access to such a variable.  

### Changed

* Enhanced Temporary Variables Elimination to remove superfluous temporary variables generated by the compiler for volatile variables.
* Updated Dead Code Elimination so that it does not remove dead writes to volatile variables.

## 3.2.0 - 2025-03-16

### Fixed

* Fixed the _Start with a new schematic_ button causing HTTP 404.
* Fixed the Schematic Decompiler incorrectly processing factories with no unit plan selected.
* Fixed wrong values of `@blockCount`, `@unitCount`, `@itemCount` and `@liquidCount` variables in processor emulator (currently assigned values correspond to Mindustry Logic 8 regardless of the compiler target). 

### Added

* Added [mlog keywords](doc/syntax/SYNTAX.markdown#mlog-keywords) as a preferred way to specify mlog keywords to Mindustry Logic functions ([#215](https://github.com/cardillan/mindcode/issues/215)).
* Added support for passing mlog keywords and formattable string literals as arguments to inline functions.
* Added support for creating constants out of mlog keywords.
* Added known mlog keywords to the file type definitions of the [provided IntelliJ IDEA IDE settings](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#intellij-idea). 
* Added new [`mlogText()` function](doc/syntax/SYNTAX-EXTENSIONS.markdown#defining-new-mlog-instructions) for direct encoding of mlog instructions.
* Added support for descending iteration order to [Range Iteration Loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) and [List Iteration Loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#descending-iteration-order) ([#174](https://github.com/cardillan/mindcode/issues/174)).
* The description of the _storage clause_ added in Mindcode 3.1 was added to the [documentation](doc/syntax/SYNTAX-1-VARIABLES.markdown#external-variables).
* Added a new [Item Transport sample](http://mindcode.herokuapp.com/schematics?s=item-transport). The sample demonstrates the use of a simple remote call scheme for acquiring units.

#### Experimental features

* Added support for [remote functions and variables](doc/syntax/REMOTE-CALLS.markdown) ([#218](https://github.com/cardillan/mindcode/issues/218)).

### Changed

* **Breaking:** new keywords were added to the language: `descending`, `module` and `remote`. Code that uses any of these keywords as a function or variable name will not compile, and the variable or function will have to be renamed.
* All reads from and writes to variables declared `volatile` are preserved now. Volatile variables can be accessed from a remote processor safely.
* Prefixes for local variables are created from the function name by default. Use [function-prefix](doc/syntax/SYNTAX-5-OTHER.markdown#option-function-prefix) option or command-line argument to generate shorter prefixes.
* Changed the `sort-variables` option to use `draw triangle` instructions to create variables in a defined order.

### Deprecated

* Deprecated the `loop` keyword in `do while` loop.
* Deprecated specifying mlog keywords without the `:` prefix in Mindustry Logic function calls. 

## 3.1.1 - 2025-03-15

### Fixed

* Fixed the web app page not loading ([#227](https://github.com/cardillan/mindcode/issues/227)).

## 3.1.0 - 2025-02-20

### Fixed

* Fixed some syntax errors reported at wrong places ([#214](https://github.com/cardillan/mindcode/issues/214)).
* Fixed wrong block/unit logic IDs ([#224](https://github.com/cardillan/mindcode/issues/224)).
* Fixed an error decompiling schematics with Air Factory ([#225](https://github.com/cardillan/mindcode/issues/225)).

### Added

* Added support for [internal and external arrays/subarrays](doc/syntax/SYNTAX-1-VARIABLES.markdown#arrays) (closes [#213](https://github.com/cardillan/mindcode/issues/213)).
* Added parallel iteration through arrays/lists in [list iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#list-iteration-loops).   
* Added support for the new `printchar` instruction in compiler, optimizer, and processor emulator.
* Added [character literals](doc/syntax/SYNTAX.markdown#character-literals) (e.g., `'A'`). The value of the literal is the ASCII value of the character in quotes.
* Added the [`ascii()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-ascii-function). The function returns the ASCII value of the first character of a string literal or constant.
* Added the possibility to specify storage different from the heap when declaring external variables.  

#### Experimental features

* Added the "backpropagation" optimization to Data Flow Optimization (closes [#151](https://github.com/cardillan/mindcode/issues/151)).
* Added support for compiling-in runtime checks for checking internal array elements are not accessed out of bounds. One of the methods uses the MlogAssertions mod, which is currently only available for Mindustry 7.

### Changed

* The default optimization level in both the web application and command line compiler is now `experimental`.

## 3.0.0 - 2025-01-26

### Fixed

* Fixed functions called using varargs potentially not being recognized as used ([#203](https://github.com/cardillan/mindcode/issues/203)).
* Fixed an incorrect implementation of the strict equality operator ([#206](https://github.com/cardillan/mindcode/issues/206)).
* Fixed a wrong reference counting for inline functions ([#207](https://github.com/cardillan/mindcode/issues/207)).
* Fixed compiler errors when placing enhanced comments into loops.
* Fixed wrong compile-time and emulator evaluation of `asin`, `acos` and `atan` functions ([#209](https://github.com/cardillan/mindcode/issues/209)).
* Fixed compile-time evaluation potentially processing functions not available in the current target. The only affected target was 6, where non-existent functions `asin`, `acos`, `atan` and `packcolor` could be compile-time evaluated instead of reported as non-existent.  

### Added

* Added support for encoding the results of compile-time expressions as color literals.
* Added functionality to automatically add a `printflush message1` instruction at the end of the main program body if missing from program.
* Added the `auto-printflush` compiler directive and `--auto-printflush` command-line option to control the addition of the `printflush message1` instruction.
* Added explanatory messages to the web app when compiling the source yields an empty program. 

### Changed

* More precise calculations of the optimization benefits in loop unrolling and call inlining.
* The `sensor` instruction is considered deterministic if a deterministic property of a non-volatile value, which is not a linked variable, is being queried.
* When clicking on an error or warning in the web application, the entire portion of the source code causing the error or warning is selected.
* Most advanced optimizations were moved to the `basic` level. Only optimizations that might be incorrect in some contexts (such as optimizations replacing computed IDs of Mindustry items with IDs known to the compiler) are left on the advanced level. 
* The default optimization level in the web application is now `advanced`.

### Miscellaneous

* Renamed virtual instructions to better match their actual use.
* For-each loop reworked to use MultiJump/MultiLabel instructions instead of (former) goto. Goto was renamed to Return, as it is now used solely for returning from stackless calls. Markers are no longer used with Return to bind it with possible return targets.

## 3.0.0-preview1 - 2025-01-17 

### Fixed

* Fixed errors when compiling empty bodies of functions and statements ([#99](https://github.com/cardillan/mindcode/issues/99)).
* Fixed errors when compiling unary plus and minus operators ([#175](https://github.com/cardillan/mindcode/issues/175)).
* Fixed execution errors not being displayed in the web application ([#185](https://github.com/cardillan/mindcode/issues/185)).
* `printflush()` and `drawflush()` didn't compile when called as methods on blocks ([#190](https://github.com/cardillan/mindcode/issues/190)).
* Fixed Single Step Elimination mistakenly removing some conditional jumps ([#199](https://github.com/cardillan/mindcode/issues/199)).
* Fixed inaccurate precision loss handling ([#202](https://github.com/cardillan/mindcode/issues/202)).
* Fixed Data Flow Optimization crashing on a return statement containing conditional expressions ([#205](https://github.com/cardillan/mindcode/issues/205)).
* Fixed the Web Application not working inside the Docker container.

### Added

* Added support for embedding expression in formattable String literals (e.g., `println($"Items: ${localItems + computeRemoteItems()}.")`).
* Added support for color literals in the form of `%rrggbb` or `%rrggbbaa`, e.g., `%ff0000` is bright red.
  * The `packcolor()` function taking constant arguments is evaluated at compile time or during optimization.
  * Processor emulator supports color literals.
* Added increment/decrement operators (`++` and `--`) in both prefix and postfix form (e.g., `i++`, `--total`).
* Added support for code blocks (delimited by `begin` and `end`).
* Added a mechanism for invoking properties on expressions ([#92](https://github.com/cardillan/mindcode/issues/92)).
* Added support for using external variables and properties wherever "normal" variables can be used (e.g., as output arguments to function calls).
* Added an ability to report errors and warnings in unused functions.
* Added several new library functions
  * [`graphics` library](doc/syntax/SYSTEM-LIBRARY.markdown#graphics-library):
    * Added `drawflush()` function which empties the draw buffer
    * Added `unpackcolor()` function which decomposes a packed color into individual `r`/`g`/`b`/`a` components.
  * [`printing` library](doc/syntax/SYSTEM-LIBRARY.markdown#printing-library):
    * Added `printflush()` function which empties the text buffer
    * Added functions for outputting numbers in binary and hexadecimal base: `formatBinaryNumber()`, `printBinaryNumber()`, `formatHexNumber()`, `printHexNumber()`.
  * [`math` library](doc/syntax/SYSTEM-LIBRARY.markdown#math-library):
    * Added `boolean()` function which converts a number to a boolean value (`0` if `null` or zero, `1` otherwise) 
    * Added `integer()` function which converts a number to an integer by truncating the fractional part
    * Added `log2()` function returning a base 2 logarithm of a number
    * Added `lerp()` function for linear interpolation between two values
* Added new `--excerpt` command-line option, allowing to specify only a part of the input file to be compiled.

#### Experimental features

* Added support for explicit variable declarations.
* Newly defined `relaxed`, `mixed` and `strict` syntax modes.

### Changed

* **Breaking:** new keywords were added to the language: `begin`, `cached`, `external`, `linked`, `noinit`, `var` and `volatile`. Code that uses any of these keywords as a function or variable name will not compile, and the variable or function will have to be renamed.
* **Breaking:** statements and declaration no longer provide any value, and using them where an expression providing a value is expected causes compilation errors. In the past even statements and declarations were considered expressions, albeit they always evaluated to `null`.
* Removed `sensor` from the list of keywords.
* The `loop` keyword in `do loop while` loops is optional. The keyword will be deprecated and eventually dropped.
* Special types of variables (external variables and properties) can be used in the same way as normal variables, e.g., as function output arguments.
* When enhanced comments are used on a line which contains a beginning of a new statement or expression, the output generated by the enhanced comment precedes the first such statement/expression ([#185](https://github.com/cardillan/mindcode/issues/185)).
* Better reporting of errors and warnings produced during compilation.
* Changed the mechanism of mlog variable names generation.
* Optimization of bitwise and boolean expressions which are incorrect for non-integers are only [performed on `advanced` level](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization).
* Any compile-time constant expression can be used as a value for a [program parameter](doc/syntax/SYNTAX-1-VARIABLES.markdown#program-parameters).

## 2.7.4 - 2025-01-04

### Fixed

* Fixed wrong definition of `control color` instruction ([#194](https://github.com/cardillan/mindcode/issues/194)).
* Fixed wrong definition of `fetch unit` and `fetch unitCount` instructions ([#197](https://github.com/cardillan/mindcode/issues/197)).
* Fixed wrong expression optimization of `op sub` instruction ([#198](https://github.com/cardillan/mindcode/issues/198)).

## 2.7.3 - 2024-12-28

### Fixed

* Fixed the If Expression optimization wrongly applying forward assignment to some instructions. This particular optimization was also moved to the `advanced` optimization level. ([#193](https://github.com/cardillan/mindcode/issues/193)).

## 2.7.2 - 2024-12-27

### Fixed

* Fixed a wrong compile-time evaluation of trigonometric functions (radians were assumed instead of degrees used by Mindustry - [#192](https://github.com/cardillan/mindcode/issues/192)).
* Fixed the documentation stating that `do` in loops is optional ([#191](https://github.com/cardillan/mindcode/issues/191)).
* Fixed decompiler output missing the `#set target` directive ([#188](https://github.com/cardillan/mindcode/issues/188)).

### Changed

* Changed Mindcode to recognize new properties supported in Mindustry Version 8: `@currentAmmoType`, `@armor`, `@velocityX`, `@velocityY`, `@cameraX`, `@cameraY`, `@cameraWidth`, `@cameraHeight` and `@solid` ([#187](https://github.com/cardillan/mindcode/issues/187)).
* The decompiler now produces a function call syntax instead of method call syntax for `printflush` and `drawflush` instructions.

## 2.7.1 - 2024-12-07

### Fixed

* Fixed Function Inlining optimization incorrectly processing functions with output parameters ([#183](https://github.com/cardillan/mindcode/issues/183)).

### Changed

* Changed the Print Merger optimization and processor emulator to correctly round numbers in print instructions in accordance with [Mindustry Logic update](https://github.com/Anuken/Mindustry-Suggestions/issues/5319).

## 2.7.0 - 2024-11-28

### Fixed

* Fixed a wrong Jump Threading optimization in out-of-line functions ([#177](https://github.com/cardillan/mindcode/issues/177)).
* Fixed bugs in the Data Flow Optimization:
  * The virtual `setaddr` instruction, unused due to the corresponding jump being unreachable, wouldn't be removed from code. This caused errors referencing a non-existent label.
  * Data Flow Optimization would sometimes corrupt the expected code structure when removing unneeded instructions, causing bugs or runtime errors during subsequent optimizations. 
  * The entry condition to a loop might be incorrectly evaluated, leading to wrong optimizations of some loops.
  * A variable read in an unreachable part of code might be reported as uninitialized. This might lead to a compilation error for compiler-generated uninitialized variables.
* Fixed the If Expression Optimization mistakenly propagating a jump into the bodies of the if/else statement, causing errors during subsequent optimizations.
* Fixed the Single Step Elimination occasionally crashing when removing a superfluous jump.
* Fixed the Loop Unrolling Optimization possibly unrolling an incorrect number of iterations on loops with no exit condition.
* Fixed a bug in the implementation of the `median(x...)` function in the `math` system library.
  
### Added

* Added a separate [Testing Framework](doc/syntax/TOOLS-TESTING-TOOL.markdown) command-line tool. The tool allows compiling and running Mindcode source files under various optimization settings, helping to detect bugs in Mindcode.  

#### Experimental features

* Added limited [Unit testing support](doc/syntax/TOOLS-TESTING-TOOL.markdown#unit-testing-support).

### Changed

* **Breaking:** changed the default language target from ML7 to ML7A. The following functions are affected:
  * `getBlock`: previously, the building at given coordinates was returned through an output parameter. In ML7A, the building is returned through function return value. See [`getBlock()`](doc/syntax/FUNCTIONS-71.markdown#instruction-unit-control).
  * `ulocate`: previously, the function returned a boolean value (`true` when the building was found, `false` otherwise). In ML7A, the function returns a building (if found), and the `found` flag is returned through an output parameter. See [`getBlock()`](doc/syntax/FUNCTIONS-71.markdown#instruction-unit-locate).
* Changed automatic generation of IntelliJ IDEA settings files to only modify the zip files when the content of the contained files changes.
* Changed Loop Unrolling Optimization to handle more cases of loop control variable modification run on the `advanced` level.

## 2.6.0 - 2024-11-15

**Breaking:** This release comes with a new keyword in Mindcode syntax (`require`), which break existing code where this keyword was used as a variable or function name.

### Fixed

* Fixed the `findLinkedBlocks` library function not to produce a warning about uninitialized variables.
* Fixed the mlog decompiler crashing on `jump` instructions targeting instructions outside valid range.
* Fixed an unhandled error when decompiling invalid code from mlog by the web application.
* Fixed `or` operator being evaluated the same as `||` instead of `|` by the processor emulator.

### Added

* Added warnings for unrecognized built-in variables. An unrecognized built-in variable might be invalid or mistyped.
* Added the built-in math constants `@pi`, `@e`, `@degToRad` and `@radToDeg` to the processor emulator.
* Added automatic generation of [IntelliJ IDEA settings files](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#intellij-idea) to keep them up to date with the language definition.

#### Experimental features

* Added a new `require` keyword for adding a system library or another external file (for command-line compilers) to the compiled code.
* Added information about the compiled code size of individual functions to the [system library documentation](doc/syntax/SYSTEM-LIBRARY.markdown#compiled-function-sizes).
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

* Fixed an incorrectly processed case expression passed into functions ([#107](https://github.com/cardillan/mindcode/issues/107)).
* Fixed arguments incorrectly passed to recursive calls ([#169](https://github.com/cardillan/mindcode/issues/169)).
* Fixed output function parameters incorrectly accepting expressions as arguments ([#170](https://github.com/cardillan/mindcode/issues/170)).
* Fixed an error when allocating a stack using the ML8A target ([#171](https://github.com/cardillan/mindcode/issues/171)).
* Fixed optimization not recognizing all interactions with Mindustry World ([#172](https://github.com/cardillan/mindcode/issues/172)).
* Fixed wrong optimization of List iteration loops with output iterator variables ([#173](https://github.com/cardillan/mindcode/issues/173)).

### Added

* Added a new `void` keyword for declaring functions not returning any value.
* Added a new `begin` and `var` keywords reserved for future use. 
* Added support for omitting optional arguments in the argument list.
* Added support for the special `@wait` argument of the [`message()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#the-message-function).

#### Experimental features

* Added [output and input/output parameters](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#function-parameters) of user-defined functions.
* Added [vararg (variable arity) functions](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#vararg-functions).
* Added [function overloading](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#function-overloading).
* Added the possibility to specify custom instructions not known to Mindcode through the [`mlog()` function](doc/syntax/SYNTAX-EXTENSIONS.markdown#defining-new-mlog-instructions).
* Added the [`findLinkedBlocks` function](doc/syntax/SYSTEM-LIBRARY.markdown#findlinkedblocks) to the system library.

### Changed

* Changed the [Loop Unrolling optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling) to replace output iterator variables with the variable assigned to them.
* Changed the [Single Step Eliminator](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to remove a jump is if there is an identical jump preceding it and there are no other jumps or labels between them. Active on `experimental`.
* Changed the expression evaluator to evaluate operations over known built-in values. The change enhances the Data Flow and Jump Normalization optimizations.
* Changed the Schemacode compiler to correctly output positions of error messages generated by both the Schemacode and Mindcode compilers, taking into account both the source file and/or position of the Mindcode program or program snippet within the source file.  

### Deprecated

* Deprecated the syntax of properties omitting the `@` prefix for Mindustry built-in variables (e.g., `vault1.coal` instead of `vault1.@coal`).
* Deprecated the syntax of Mindustry Logic function calls not requiring the `out` modifier when passing in an output argument.

### Miscellaneous

* Improved the command-line tool output of the Compile Schemacode action.

## 2.4.0 - 2024-10-23

### Fixed

* Fixed Copy to clipboard button not working ([#168](https://github.com/cardillan/mindcode/issues/168)).
* Fixed an unhandled error when decoding a malformed Schematic file string by the web application.
* Fixed Schemacode samples to not use deprecated syntax.

### Added

* Added support for [partially decompiling](doc/syntax/TOOLS-MLOG-DECOMPILER.markdown) existing mlog code into Mindcode.

## 2.3.0 - 2024-10-16

### Fixed

* Fixed a wrong compilation order of appended files ([#155](https://github.com/cardillan/mindcode/issues/155)).
* Fixed inadequate reporting of syntax error ([#156](https://github.com/cardillan/mindcode/issues/156)).
* Fixed wrong handling of comparison operators by Data Flow Optimization ([#158](https://github.com/cardillan/mindcode/issues/158)).
* Fixed wrong parsing of formattable string literals.
* Fixed inadequate handling of unsupported expressions embedded in formattable string literals.  

### Added

* **Breaking:** Added support for syntax variants (`strict` and `relaxed`) to Mindcode. The Strict syntax is the default now; to be able to compile existing Mindcode the Relaxed syntax needs to be activated using the `#relaxed;` directive.
* Added support for the [Mlog Watcher](doc/syntax/TOOLS-MLOG-WATCHER.markdown) Mindustry mod integration to both the web app and the command-line tool. This mod allows the compiled code to be automatically injected into a selected processor in a running Mindustry game.
* Added variable name validation: when inserting mlog into Mindustry processor, variables named `configure` are silently renamed to `config`. For this reason, using `configure` as a name for any variable in Mindcode causes an error. 
* Added navigable compiler error messages to the web app. Clicking on a message with a known position in the source code selects the corresponding position in the editor.
* Added support for outputting the error messages by the command line tool in a format which allows IDEs to parse the position and navigate to the error location in the source code.
* Added a variety of new optimizations to the [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization). 
  * `op` instruction: many new optimizations when one of the two operands is known.
  * `lookup` instruction: when possible, the instruction is replaced by a `set` instruction setting the item, liquid, building, or unit directly to the target variable, allowing further optimizations to take place. Effective on `aggresive` optimization level.
* Added warning messages when deprecated features are detected in the source code. 
* Added support for creating constants from formattable string literals.
* Added full support for the `sync()` function: a variable passed as an argument to this function becomes automatically volatile.

**Schemacode**

* Added support for block comments, delimited by `/*` and `*/`. These comments can span multiple lines.

#### Experimental features
 
* Added support for Mindustry Logic from upcoming version 8. The features supported correspond to the current implementation in Mindustry and might therefore still change. All new features are described in [separate documentation](doc/syntax/MINDUSTRY-8.markdown).
* Added a [system library](doc/syntax/SYSTEM-LIBRARY.markdown), automatically included when the language target is `8A` or later. 
* Added support to the [If Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#instruction-propagation) to propagate values in `if` expressions not just into the `set` instruction, but into any instruction taking an input parameter. Available on  the `experimental` optimization level.

### Changed

* **Breaking:** Changed the implementation of the `printf()` function under language target `ML8A`. Instead of compile-time formatting of passed parameters, the function uses `print` and `format` instructions for [run-time formatting](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#run-time-formatting).
* Changed the definition of the `&&` and `||` operators: they are guaranteed to [always evaluate to either `0` or `1`](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#operators).
* Changed the `min()` and `max()` functions to accept more than just two arguments.
* Changed the [Temporary Variables Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#temporary-variables-elimination) to replace unused output variables in instructions with `0`, to ensure no unnecessary variable will be created by the instruction, reducing clutter. Closes [#154](https://github.com/cardillan/mindcode/issues/154).
* Changed the [If Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#if-expression-optimization) to support value propagation for all instructions having one output parameter (based on instruction metadata), instead of just a subset of specifically handled instructions.
* Changed—yet again—the way the [Single Step Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) removes the last instruction, which is a jump to the beginning of the program, so that it doesn't leave behind any jump that might have targeted the removed instruction. Such a jump was harmless, but unnecessary and looked strange in the mlog.
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

* Added a secondary set of icons representing constants with dashes in identifiers replaced by underscores (e.g., `ITEM_COAL` instead of `ITEM-COAL`). Kebab-case identifiers (with dashes) are deprecated in Mindcode and will be desupported in a future release. (In Schemacode, there are currently no plans to remove support for kebab-case identifiers.)
* Added support for declaring [program parameters](doc/syntax/SYNTAX-1-VARIABLES.markdown#program-parameters) using a new `param` keyword. Using global variables for program parametrization is deprecated, program parameters should be used instead. Support for program parametrization through global variables will be removed in a future release.     
* Added a new `noinline` keyword, which will prevent a function from being inlined even when called just once, and by the [Function Inlining](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining) optimization. The keyword can only be used when declaring a function.
* Added support for block comments, delimited by `/*` and `*/`. These comments can span multiple lines. 
* Added a new schematic sample with quite a sophisticated code [on the Schematics page](http://mindcode.herokuapp.com/?s=overdrive-dome-supply). It consists of an overdrive dome supplied by units controlled by a microprocessor. The microprocessor searches for available units among a list of supported types, switches to the preferred unit type when it becomes available, and rebinds units (possibly switching the type again) if units in use are destroyed or taken over by the player or a rogue processor.  

#### Experimental features

Experimental features may contain bugs, break existing code or produce suboptimal code, and are subject to change.

* Added support for multiple loop variables in [list iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#list-iteration-loops). Each iteration processes as many elements from the list as there are loop variables.
* Added an `out` keyword to be used with loop control variables in list iteration loop, allowing [list elements to be modified](doc/syntax/SYNTAX-3-STATEMENTS.markdown#modifications-of-variables-in-the-list).
* Added a new GUI option to choose an optimization level in the web app when compiling Mindcode or building Schemacode.
* Added a capability to run the compiled code on an emulated processor, by using a `Compile and Run` button in the web app, or the [`--run` command line option](doc/syntax/TOOLS-CMDLINE.markdown#running-the-compiled-code). The output is shown in a separate control in the web app or written to the log when using the command line tool.
* Added a capability to the command line tool to compile several source files at once using the [`--append` command line argument](doc/syntax/TOOLS-CMDLINE.markdown#additional-input-files).
* Added new optimization level, `experimental`. On this setting, the [Data Flow optimizer](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) doesn't assume the assignments to global variables might be changed by editing the compiled code, allowing performing more optimizations on them. Program parameters must be used instead of global variables for program parametrization if this optimization level is used.  
* Added [formattable string literals](doc/syntax/SYNTAX.markdown#formattable-string-literals), which allow formatting outputs of the `print` and `println` functions the same way as `printf` does. 
* Added [enhanced comments](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#enhanced-comments), an alternative way to enter remarks.
* Added new [`sort-variables` compiler directive](doc/syntax/SYNTAX-5-OTHER.markdown#option-sort-variables) which ensures that user variables will be accessed in a well-defined order, to make inspecting the variables in the Mindustry processors **Vars** screen easier.
* Added generation of signature at the end of the compiled code. The signature is a `print` instruction (which is not executed) with the text `Compiled by Mindcode - github.com/cardillan/mindcode`. The signature is not added should the instruction limit be exceeded or when the program doesn't naturally end in an unconditional jump. Adding the signature can be disabled in the command line tool by the `--no-signature` command line argument.  

### Changed

* Changed the names of [optimization levels](doc/syntax/SYNTAX-5-OTHER.markdown#option-optimization) from `off` and `aggressive` to `none` and `advanced`. The former names are still supported in the `#set` compiler directive, but not in the command-line options. 
* Changed the [Loop Hoisting](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-hoisting) optimization to analyze functions modifying variables inside loop instead of a blanket assumption that all global variables may be changed by a function.
* Changes to the web app
    * A button was added to the web app to copy the output code to the clipboard with a single click.
    * It is now possible to select the optimization level directly in the web app user interface. The default optimization level is now `basic` for Mindcode compiler and `advanced` for Schemacode builder.
* Changed the syntax to allow an optional `do` keyword in all `for` and `while` loops, and optional `then` keyword in `if` and `elsif` statements.
* Changed the [Single Step Elimination optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to remove the `end` instruction at the very end of the program on `advanced` optimization level.
* Changed the command line tool to output final instruction numbers when printing out the unresolved code, for easier localization of errors reported when running the compiled code on an emulated processor.  

### Miscellaneous

* From this version (`2.2.0`) on, Mindcode will use semantic versioning (the major version will be increased for significant changes to the syntax). The command-line tool now reports the version when passed in `-v` or `--version` as a parameter.  
* Replaced pattern matching in switch cases (JDK17 preview feature) with `if` and other constructs to remove the dependency on the `--enable-preview` switch.
* Updated existing Mindcode and Schemacode samples further to adapt for upcoming changes in syntax (optional keywords were added). Some samples were reworked.

## 2024-09-15

### Fixed

* Fixed a slightly wrong (too low) cost estimation in the [Case Switching](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) optimization.

### Added

* New [`remark()` function](doc/syntax/SYNTAX-4-FUNCTIONS.markdown#remarks), as described [here](https://github.com/cardillan/mindcode/issues/140).

### Changed

* Slightly improved the [Case Switching](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#case-switching) optimization - 
  instead of expanding the jump table range by one on each side and limiting the input value using the `op min` and 
  `op max` instructions, the jump table covers only the existing `when` branches and values outside the supported 
  range are handled using conditional jumps. This change saves two instructions and potentially speeds up the 
  execution for input values lying outside the jump table.

### Miscellaneous

* Some of the Mindcode samples used during unit tests were updated to use semicolons in anticipation of planned Mindcode changes.  

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
  from various sources (string literal, string constant, or external file). 
* Added a new Mandelbrot Generator schematic example.

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

* Schemacode compiler and decompiler now use [mimex](https://github.com/cardillan/mimex)-generated metadata for 
  lists of items, liquids, and unit commands. All Mindcode object definitions are now loaded from extracted metadata 
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
* Added support for the [`effect` instruction](doc/syntax/FUNCTIONS-71.markdown#instruction-effect).
* Added more capabilities to the
  [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization): 
  * Replacing `@this.x` and `@this.y` expressions by the `@thisx`/`@thisy` built-in constants.
  * Replacing `@constant.id` expressions by an integer value of the ID assigned to the `@constant`, assuming it is 
    a known item, liquid, block type, or unit type. This optimization is only active on `aggresive` optimization level. 
    For example, `id = @graphite.id` gets compiled down to `set id 3`, allowing further optimizations on the 
    constant value (`3`) to take place. 
  * Replacing multiplication by literal zero by an instruction setting the target variable to a zero directly, and 
    replacing multiplication/division by one and addition/subtraction of zero by an instruction setting the target 
    variable to the other operand directly.  

## 2024-01-19

> [!NOTE]
> Mindustry 7.0 build 146 added—among other improvements - the `id` property that provides an id of given 
> block, unit, item or liquid (basically the inverse of the `lookup` operation). Since Mindcode has a generic support 
> for properties and built-in constants, this property can be used right away; no explicit support from Mindcode is 
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

* Mindcode compiler now uses metadata extracted by [mimex](https://github.com/cardillan/mimex) to obtain
  a list of possible block names.
* Function Inlining optimizer now replaces the function return variable with the receiving variable when inlining a 
  function call. The protection granted to function return variables is not necessary after inlining the call, and 
  this replacement allows the receiving variable to be handled by further optimization.

### Miscellaneous

* Small documentation improvements.
* No-op instructions are removed before the final optimization phase, avoiding the need to handle no-op by the final 
  phase optimizers.

## 2023-07-20

### Fixed

* Fixed a bug in the code duplication routine that sometimes prevented [Unreachable Code
  Elimination](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#unreachable-code-elimination) from removing unreachable portions 
  of optimized case statements and inlined functions. The bug sometimes caused runtime exceptions when optimizing 
  for speed, usually under tight instruction space restrictions only.
* Fixed [#108 Compilation errors are not properly reported](https://github.com/cardillan/mindcode/issues/108).

### Changed

* Changed weight computation of user-defined functions. Weight of a function is computed as a total weight of all 
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

* Fixed a wrong cost estimation in [Loop Unrolling](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling). The cost 
  estimates were too high, potentially preventing some eligible loops from being unrolled.
* Fixed the compiler not recognizing integer values in compiler option directives (`#set`).

### Changed

* Changed [Jump Threading](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-threading) to also replace jumps leading to `goto`
  instructions with the `goto` instruction itself on `aggressive` level. There's a possible speedup in some stackless 
  function calls and list iteration loops.
* Changed [Single Step Elimination](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#single-step-elimination) to also remove two 
  consecutive jumps that are identical. Such sequences were sometimes produced as a result of other optimizations.
* Changed [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) to use a single 
  instruction iterator instance instead of making an instruction list from each AstContext being processed. 
  Significantly speeds up processing.
* Additional optimizers converted to adding no-op instruction instead of removing them; updated other optimizers 
  to correctly handle such code.

## 2023-07-12

### Fixed

* Fixed a bug in [Function Inlining](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#function-inlining) that caused runtime 
  exception in some circumstances.
* Fixed a bug in [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization) that may have 
  removed instructions that were actually used by the program.

### Changed

* Changed Data Flow Optimization to perform fewer iterations in certain situations. The reduction is significant (up 
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

* Fixed incorrect weight computation of user-defined functions. Currently, weight is based on the number of times a 
  function is called in the program. This is still not quite correct, though—the weight should be computed by 
  tracking call hierarchy and weights of contexts containing the calls.
* Fixed incorrect weight computation of list-iteration loops. The weight was set to the actual number of loops  
  instead of the unified loop weight. 

### Changed

* Changed option name `conditional-jump-optimization` to `jump-optimization` to conform with 
  [documentation](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#jump-optimization).

### Miscellaneous

* Eliminated `STORED_RETVAL` variable type. Values returned from user-defined function calls are now assigned to
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
* Changed the printout of the unresolved instruction (activated by the `-u` command line option) to omit 
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
  variables](doc/syntax/SYNTAX-1-VARIABLES.markdown#variable-scope) even on `basic` level. Only final assignments to 
  main variables are preserved on `basic` level, other assignments can be optimized away. This change allows the Loop 
  Unrolling optimization to be functional even on `basic` optimization level.
* Changed condition duplication by Loop Optimization to use the general
  [optimization for speed](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#optimization-for-speed) mechanism.   
* Changed various optimizations for minor improvements:
  * The number of times the Data Flow Optimization has to process loops was reduced.
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

* Added support for the [`unitHealth` function](doc/syntax/FUNCTIONS-70.markdown#instruction-set-rule).

### Fixed

* Fixed Data Flow Optimization incorrectly handling `break`, `continue` and `return` statements 
  ([#98](https://github.com/cardillan/mindcode/issues/98)).

### Miscellaneous

* Statistics about executions of processor-based unit tests are now collected and committed to the repository, 
  allowing to track the evolution of the efficiency of generated code over time. 

## 2023-06-18

### Added

* Added support for the new [`angleDiff` operation](doc/syntax/FUNCTIONS-70.markdown#instruction-operation) in Mindustry v145.

## 2023-06-17

### Added

* Added elimination of useless `set` instructions (such as `set x x`) to
  [Expression Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#expression-optimization). Optimizes recursive 
  function calls passing unchanged value of function argument to the next function call. 
* Added elimination of variables never modified by a function from stack in
  [Stack Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization).
* Added specific optimization for recursive function calls to the
  [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization):
  * The optimizer is aware when a variable value is preserved on the stack and can reuse the variable state when 
    optimizing code following a recursive function call.
  * Streamlining expressions when passing value derived from the current value of the function argument to the 
    recursive call (e.g., calling `foo(n - 1)` in function `foo(n)`).

### Fixed

* Fixed Data Flow Optimization crashing when source code contained unused user-defined functions
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
  the quality of the generated code. 

## 2023-06-16

### Added

* Added [Constant folding](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#constant-folding) to Data Flow Optimization.
* Added [Common subexpression optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#common-subexpressions-optimization) 
  to Data Flow Optimization.

### Removed

* Removed _Function call optimization_. This optimization was completely superseded by the
  [Data Flow Optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization), which handles all 
  cases previously optimized by Function call optimization, and is able to identify more opportunities for 
  optimization. The old optimization was removed because it became incompatible (i.e., produced wrong results) 
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
  remove user defined variables from compiled code—see the documentation for details. 

### Fixed

* Fixed some syntax errors in source programs not being properly reported. Some syntax errors were ignored, others 
  might have caused unhandled exceptions in the web or command line application. When a syntax error occurs, 
  partially generated code is no longer displayed in the web application.      
* Fixed the compiler not producing any code when optimizations were switched off.

## 2023-06-08

### Fixed

* Fixed an incorrect creation of schematics containing bridges. Configuration for bridges and other blocks that connect 
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

* Fixed the [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) not having the upper 
  boundary fixed under some conditions. The feature announced in release [2023-05-03](#2023-05-03) wasn't fully 
  implemented until now.
* Fixed bugs in the [stack optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#stack-optimization). In some cases, 
  `push`/`pop` instructions were mistakenly removed, in other cases unnecessary `push`/`pop` instructions were left
  in the code. Current implementation uses AST structure metadata to identify and protect variables used in 
  loops.   
  
### Miscellaneous

* Generated mlog instructions are now linked to the AST nodes they were created from (via instances of AstContext),
  and through them to the parser tokens (for now in somewhat coarse granularity).
  * This allows optimizers to inspect source AST nodes of individual instructions, obtaining information about code 
    structure. Optimizations based on these metadata should be easier to write and understand, once necessary 
    support tooling is in place.
  * It is necessary to update the AST context structure after each optimization iteration. These updates aren't done 
    of the fly—optimizers must understand that the changes they make to the program in a single iteration aren't 
    reflected in the AST context structure.
  * Optimizers need to be careful to create newly generated instructions using the right (already existing) AST 
    context, otherwise the following AST context-based optimizers can misunderstand the code structure. There are means
    to duplicate existing code while deep-copying the context structure.
  * Instructions can be linked to source code. At this moment it can be only displayed using the `-f source` command 
    line argument, hopefully in the future a better error reporting will be built using these metadata.   

## 2023-05-23

### Added

* Added option for [code generation goal](doc/syntax/SYNTAX-5-OTHER.markdown#option-goal). Allows specifying whether 
  to aim for a smaller code or a faster code.
* Added basic [loop optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#loop-optimization).

## 2023-05-21

Note: the bug fixed in this release only affects the command line tool. The web app hasn't been redeployed.

### Fixed

* Fixed the command line tool application crashing when decompiling a schematic.  

## 2023-05-20

### Added

* Added language target `7A`, i.e., Mindcode Logic version 7, revision A. Uses the same instruction set as `7`, except  
  the `getBlock` and `ulocate` functions returning the located block/building.
  See [`getBlock`](doc/syntax/FUNCTIONS-71.markdown#instruction-unit-control),
  [`ulocate`](doc/syntax/FUNCTIONS-71.markdown#instruction-unit-locate). Target `7` is still default both in webapp 
  and in the command line tool, so the change doesn't break existing code.
* Added support for the reintroduced `ucontrol pathfind` instruction. When issued, navigates the current unit to a given 
  location using AI to avoid obstacles. See [`ucontrol`](doc/syntax/FUNCTIONS-71.markdown#instruction-unit-control). 
* Added command line options for [Schematic Decompiler](doc/syntax/TOOLS-CMDLINE.markdown#decompile-schematic-action-help)
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

* Fixed the handling of block label reuse in schemacode. Reusing the same label for multiple blocks now causes an error.
* Fixed [#93: Mindcode compiled wrong when using semicolons](https://github.com/cardillan/mindcode/issues/93).

## 2023-05-13

### Added

* Added Schematics Builder tool with a new [Schemacode language](doc/syntax/SCHEMACODE.markdown).
* Added a new [command-line interface](doc/syntax/TOOLS-CMDLINE.markdown) for the Mindcode Compiler, Schematic 
  Builder, and Schematic Decompiler.
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
  Schematics screen is used. The mod allows refreshing schematics after they were rebuilt by Schematics Builder. 

## 2023-05-03

### Added

* Added support for using [Mindustry built-in icons](doc/syntax/SYNTAX-1-VARIABLES.markdown#constants-representing-built-in-icons) in 
  `print` functions.
* Added support for compile-time [string concatenation](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#string-concatenation). 
  Allows—among other things—to embed icons into string constants at compile time, such as
  `const HEADER = "Using unit: " + UNIT-MEGA`    

### Changed

* **Breaking:** changed [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) to 
  evaluate the upper boundary only once, before entering the loop for the first time. Previous version evaluated 
  the upper bound at each iteration, and reflected possible changes in the upper bound. The documentation was 
  expanded to specify the evaluation of the upper bound. Use a while loop or a C-style loop if you want to fully 
  evaluate the loop condition at each iteration.
* Changed handling of non-constant string expressions: when detected, a compilation error occurs (see also 
  [string concatenation](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#string-concatenation)).

### Miscellaneous

* The `compiler` module has been renamed to `mindcode`. The `compiler` module name will be repurposed for a 
  new command-line interface to the `mindcode` module and an upcoming
  [`schemacode` module](https://github.com/cardillan/mindcode/issues/90).  

## 2023-04-30

### Changed

* Enhanced [print merging optimization](doc/syntax/SYNTAX-6-OPTIMIZATIONS.markdown#print-merging) to merge non-string 
  literals (e.g., numeric constants) on aggressive optimization level. 
* Changed handling of hexadecimal and boolean literals to include range checks and refuse literals outside the valid 
  range (signed 64-bit integer; note that [Mindustry Logic variables](doc/syntax/SYNTAX-1-VARIABLES.markdown) cannot 
  represent the full range of possible values without loss of precision).   

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

* Changed [`ubind` function](doc/syntax/FUNCTIONS-70.markdown#instruction-unit-bind) to return the freshly bound unit.
* Changed [encoding of numeric values to mlog](doc/syntax/SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic).

### Fixed

* Fixed constant expression evaluation crashing on binary numeric literals.
* Fixed constant expression evaluation producing values not representable in mlog source code.
* Fixed function call optimization not processing numeric literal arguments in some cases.

## 2023-04-14

### Added

* Added support for using [binary numeric literals](doc/syntax/SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic) 
  (e.g., `0b00101`) in a Mindcode source code.
* Added support for using
  [scientific notation in numeric literals](doc/syntax/SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic) 
  in a Mindcode source code. Literals compatible with mlog are kept unchanged, literals unrecognized by mlog (e.g., `1.5e-5`)
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
* Added [compiler directives](doc/syntax/SYNTAX-5-OTHER.markdown) (`#set`): optimization and target instruction set 
  can be now set from the web app compiler.
* Support for the new [`setprop` world processor instruction](doc/syntax/FUNCTIONS-70.markdown#instruction-set-prop)
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

The full changelog wasn't kept for earlier releases. What we know is documented separately here (click the date for fuller description): 

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

* User-defined functions
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
