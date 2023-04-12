# Changelog

All notable changes to this project will be documented in this file.

## Upcoming

* Update to Java 17

## 2023-04-12

### Fixed

*  Fixed incorrectly merging instructions across `printflush()` calls ([#91](https://github.com/francois/mindcode/issues/91)).

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

*  Fixed parameters not being passed to inline functions ([#89](https://github.com/francois/mindcode/issues/89)).

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

 