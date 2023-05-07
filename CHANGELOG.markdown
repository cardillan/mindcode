# Changelog

All notable changes to this project will be documented in this file.

## 2023-05-03

### Added

* Added support for using [Mindustry built-in icons](doc/syntax/SYNTAX-1-VARIABLES.markdown#built-in-icons) in 
  `print` functions.
* Added support for compile-time [string expressions](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#string-expressions). 
  Allows - among other things - to embed icons into string constants at compile time, such as
  `const HEADER = "Using unit: " + UNIT-MEGA`    

### Changed

* **Breaking**: changed [range iteration loops](doc/syntax/SYNTAX-3-STATEMENTS.markdown#range-iteration-loops) to 
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
* Added [section about using units](doc/syntax/SYNTAX-6-TIPS-N-TRICKS.markdown#using-units) to Mindustry Tips 
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

 