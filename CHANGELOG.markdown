# Changelog

All notable changes to this project will be documented in this file.

## 2023-06-02

Note: the bug fixed in this release only affects the command line tool. The web app hasn't been redeployed.

### Fixed

* Fixed the command line tool application not properly recognizing actions.

## 2023-05-30

### Added

* Added [If expression optimization](doc/syntax/SYNTAX-5-OTHER.markdown#if-expression-optimization). In some cases 
  only decreases the number of instructions by rearranging them, in other cases can decrease number of executed 
  instructions. Only if/ternary expressions containing both true and false branch are affected. 

### Fixed

* Fixed the [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) not having upper 
  boundary fixed under some conditions. The feature announced in release [2023-05-03](#2023-05-03) wasn't fully 
  implemented until now.
* Fixed bugs in the [stack optimization](doc/syntax/SYNTAX-5-OTHER.markdown#stack-optimization). In some cases, 
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
    of the fly -- optimizers must understand the changes they make to the program in a single iteration aren't 
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
* Added basic [loop optimization](doc/syntax/SYNTAX-5-OTHER.markdown#loop-optimization).

## 2023-05-21

Note: the bug fixed in this release only affects the command line tool. The web app hasn't been redeployed.

### Fixed

* Fixed the command line tool application crashing when decompiling a schematics.  

## 2023-05-20

### Added

* Added language target `7A`, i.e. Mindcode Logic version 7, revision A. Uses the same instruction set as `7`, except  
  the `getBlock` and `ulocate` functions returning the located block/building.
  See [`getBlock`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ucontrol),
  [`ulocate`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ulocate). Target `7` is still default both in webapp 
  and in the command line tool, so the change doesn't break existing code.
* Added support for the reintroduced `ucontrol pathfind` instruction. When issued, navigates current unit to given 
  location using AI to avoid obstacles. See [`ucontrol`](doc/syntax/FUNCTIONS_V7A.markdown#instruction-ucontrol). 
* Added command line options for [Schematics Decompiler](doc/syntax/TOOLS-CMDLINE.markdown#decompile-schema-action-help)
  to specify order in which blocks are written to the schema definition file and when to generate the `facing` 
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
* Added new [command-line interface](/doc/syntax/TOOLS-CMDLINE.markdown) for the Mindcode Compiler, Schematics 
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
* Added support for compile-time [string expressions](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#string-expressions). 
  Allows - among other things - to embed icons into string constants at compile time, such as
  `const HEADER = "Using unit: " + UNIT-MEGA`    

### Changed

* **Breaking:** changed [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) to 
  evaluate upper boundary only once, before entering the loop for the first time. Previous version evaluated 
  the upper bound at each iteration, and reflected possible changes in the upper bound. The documentation was 
  expanded to specify the evaluation of the upper bound. Use a while loop or a C-style loop if you want to fully 
  evaluate the loop condition at each iteration.
* Changed handling of non-constant string expressions: when detected, a compilation error occurs (see also 
  [string expressions](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#string-expressions)).

### Miscellaneous

* The `compiler` module has been renamed to `mindcode`. The `compiler` module name will be repurposed for a 
  new command-line interface to the `mindcode` module and an upcoming
  [`schemacode` module](https://github.com/cardillan/mindcode/issues/90).  

## 2023-04-30

### Changed

* Enhanced [print merging optimization](doc/syntax/SYNTAX-5-OTHER.markdown#print-merging) to merge non-string 
  literals (eg. numeric constants) on aggressive optimization level. 
* Changed handling of hexadecimal and boolean literals to include range checks and refuse literals outside valid 
  range (signed 64-bit integer; note that [Mindustry Logic variables](doc/syntax/SYNTAX-1-VARIABLES.markdown) cannot 
  represent full range of possible values without loss of precision).   

### Miscellaneous

* Logic instructions arguments are represented by objects with proper attributes and type information instead of 
  plain strings.  

## 2023-04-18

### Added

* Added [return value optimizer](doc/syntax/SYNTAX-5-OTHER.markdown#return-value-optimization). Improves processing 
  return values from function calls. 
* Added [section about using units](doc/syntax/MINDUSTRY-TIPS-N-TRICKS.markdown#using-units) to Mindustry Tips 
  and Tricks.
* Added warnings when constant expression evaluation or Mindcode numeric literal cannot be compiled to mlog without 
  a loss of precision. 

### Changed

* Changed [`ubind` function](/doc/syntax/FUNCTIONS_V7.markdown#instruction-ubind) to return the freshly bound unit.
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
* Added [simple expression optimizer](doc/syntax/SYNTAX-5-OTHER.markdown#expression-optimization).
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

 