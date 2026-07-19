# Mindcode Features

This file contains a list of Mindcode features.

## Language

### Basic language features

* Procedural language
* Syntactic modes
  * Relaxed: for simple scripts, implicit variable creation
  * Strict: for larger project, enforces proper code structure
  * Mixed: facilitates the transition from the relaxed to the strict syntax mode
* Control flow structures
  * Loops
    * While loops
    * Do-while loops
    * Range iteration loops (ascending/descending)
    * List iteration loops (ascending/descending)
      * Parallel iteration of several distinct lists
      * List element modification
    * C-style for loops
    * Infinite loops
    * Break and continue statements
  * Conditionals
    * If-else statements
    * Case expressions
  * Code blocks
  * Mlog code blocks
* Expressions
  * All mlog-provided operators
  * Additional operators
    * Unary operators
    * Boolean/logical and/or operators
    * Range/list membership operator
  * Array/subarray assignments
* Variables
  * No data types
  * Variable scope (global/local)
  * Types of variables
    * Constants
    * Program parameters
    * Linked blocks
    * Guarded linked blocks 
    * Internal variables (stored in processor variables, support all values)
    * External variables (stored in memory banks/memory cells, only numerical values)
    * Remote variables (stored in a different processor)
      * Imported via remote module
      * Explicitly declared
    * Built-in mlog variables
  * Arrays
    * Internal arrays (stored in processor variables, support all values)
    * External arrays (stored in memory banks/memory cells, only numerical values)
    * Remote arrays (stored in a different processor)
    * Constant arrays
    * Arrays of (guarded) linked blocks
    * Subarrays
  * Volatile variables
  * Storage specification for declared variables (remote processors or memory banks/cells)
  * Automatic allocation of external arrays/variables in a specified memory block
* Literals
  * All mlog literal types supported
  * Standard numeric literals (transcribed into mlog-compatible representation when needed)
  * Character literals (converted to numbers)
  * Literals without mlog representation (can't be stored in processor variables):
    * Formattable string literals (for string interpolation)
    * Mlog keyword literals
* Functions
  * Inline functions
  * Stackless function (non-inlined and non-recursive)
  * Recursive functions (limited support)
  * Input, output and input/output function parameters
  * Optional output-only function parameters
  * Additional inline function features
    * Passing arguments by reference, including mlog keywords and formattable string literals
    * Vararg parameters
  * Function overloading
  * Functions with and without a return value (procedures)
  * Logic functions (represent most existing mlog instructions)
    * Complete function reference for each supported target
  * Remote functions
* String interpolation (in printing functions only)
* Block, line and enhanced comments 

### Modules

* Support for modules stored in separate source files
* Modules can be used by a program or another module
* Remote modules
  * Allow partitioning code among different processors
    * Avoiding code-size limits
    * Allowing for parallel code execution
  * Static and dynamic binding of remote processors
  * Multiple instantiations of a remote module
  * Synchronous remote function calls
  * Asynchronous remote function calls
  * Remote variables
  * Background process (executed between remote function calls)
  
### Inter-processor communication

* Arbitrary access to other processors' variables
* Atomic code sections

## Compiler

### Targets

* Target versioning
  * Fully supports Mindustry 7 and 8 versions
  * Limited support for Mindustry 6
  * Existing target framework makes supporting future bleeding edge Mindustry versions easy
  * Selecting the target Mindustry version and processor type
  * All mlog instructions from the selected target and processor type are supported and can be used
* Target guard (refuses execution of the code on a wrong version of Mindustry processor)
* Uses mimex for loading information about the current target 

### Code generation
 
* Powerful compile-time evaluation, including:
  * String expressions
  * Simple user-defined functions
* Output code formatting
  * Absolute addressing (uses instruction numbers in `jump` instructions)
  * Symbolic labels and indentation
  * Comments in output code
  * Optional argument padding
* Verifies/enforces code size limits
* Processor ID
* Code signature
* Text-based jump tables
* Several different implementations of internal arrays
  * `@counter` arrays (the only available for Mindustry 6/7)
  * Folded `@counter` arrays
  * Compact/lookup arrays
  * Separate implementation for small arrays
  * Out-of-bounds array access runtime checks (optional)

### Compiler options

* Wide array of compiler options
* Settable from the command line or via compiler directives
* Local compiler options for finer control

### Extensibility

* Support for non-standard mlog features (mods adding logic capabilities or new Mindustry versions) 
  * User-defined mlog keywords
  * User-defined built-in variables
  * User-defined named colors
  * User-defined linked block names
  * User-defined instructions
* Support for embedding blocks of mlog code
  * Seamless integration with surrounding code
  * Symbolic labels and jumps
  * Supports all Mindcode literals (transcribed into mlog-compatible representation when needed)
  * Mlog comments (propagated to the compiled code)
  * Line comments (not propagated to the compiled code)

## Optimizer

* Optimizations respect the selected compilation target  
* Optimization goals
  * Speed (faster but larger code, trades code size for speed up to the code size limit)
  * Neutral
  * Size
* Individually selectable optimizations
  * Array Optimization
  * Boolean Optimization
  * Case Expression Optimization
  * Condition Optimization
  * Data Flow Optimization
  * Dead Code Elimination
  * Expression Optimization
  * Function Inlining
  * If Expression Optimization
  * Jump Threading
  * Loop Hoisting
  * Loop Rotation
  * Loop Unrolling
  * Print Merging
  * Return Optimization
  * Stack Optimization
  * Temp Variables Elimination
  * Unreachable Code Elimination

## Libraries

* System libraries
  * `arrays`: functions for basic array manipulations.
  * `blocks`: block-related functions (just the `findLinkedBlocks` function at this moment).
  * `compatibility`: a special-purpose library for testing Mindcode's compatibility with a specific Mindustry version.
  * `graphics`: additional graphics functions. Functions related to display transformations require the Mindustry Logic 8 instruction set.
  * `math`: a math library.
  * `printing`: functions for printing and formatting numbers. Some functions require the Mindustry Logic 8 instruction set.
  * `units`: functions for searching and binding available units of a required type.
* User-defined libraries stored in separate files

## Schematic generation

* Schemacode: a DSL language for schematic definition
  * Support for all existing block types
  * Support for all existing configuration types
    * Literal link specification for processors, including virtual links
    * Symbolic link specification shared by Schemacode and Mindcode
    * Supports both Mindcode and mlog code in processor configuration
  * Support for reusable block patterns (regions)
  * Support for filling rectangular or circular areas within schematics with blocks or regions
* Error detection in schematic definition
  * Overlapping blocks
  * Unused/udefined block links
  * Gaps in link numbering schemes
  * Unknown block types
  * Blocks linked to bridges, power nodes or processors out of the block's range
  * Exceeding maximum schematic dimensions or processor configuration sizes
* Mindcode and mlog code storage
  * Embedded processor code (within the schematic definition file)
  * Externally stored processor code
  * Combining code snippets even from different sources
  
## Development tools

* Web application
  * Syntax and error highlighting
* Command-line tool
* Limited IDE support
  * Syntax highlighting for IntelliJ IDEA
  * The command-line compiler can be integrated as an external tool into most IDEs 
* MlogWatcher integration (web app/command-line tool)
  * Injecting the compiled code into the selected processor
  * Storing built schematics into the in-game schematic database
* MlogWatcher integration (command-line tool only)
  * Updating code in all existing processors on the map
  * Loading code from a selected processor
  * Loading the selected schematic from the schematic database

## Support tools

### Emulator

* Ticks-based emulation of entire schematics
* Instruction emulation
  * Emulates all instructions not interacting with the map
  * Emulates all read/write operations within the schematics (from/to processors and memory blocks)
  * Emulates print operations
* Detects and reports error states (even those ignored by the in-game processor)
* Runs code and schematics built by Mindcode directly
* Runs code and schematics loaded from external sources 

### Decompiler

* Schematic decompiler
  * Decompiles an existing schematic into a Schemacode file
  * Supports all schematic features
  * Produced file can be compiled back into the same schematic again
* Mlog decompiler
  * Partially decompiles mlog code into Mindcode source
  * Decompiles complex expressions
  * No support for control flow structures

### Mods

* MlogAssertions mod
  * Additional logic instructions:
    * Efficient implementation of runtime checks generated by Mindcode
    * Error-reporting and logging
  * Visual indication of invalid processor states
  * Visual indication of waiting processors
* MlogWatcher mod (maintained by [Sharlottes](https://github.com/Sharlottes))
  * Allows sending compiled code into the selected processor
  * Displays processor ID in-game
