# System library

Mindcode comes equipped with a system library. System library is stored in several files. The `require` statement imports the library functions into your program:

```
require blocks;   // Use the 'blocks' system library
require units;    // and the 'units' library too
```

The order in which system libraries are imported doesn't matter.

System libraries contain functions and sometimes constants that can be used by a Mindcode program. The following library modules are provided:

* [Module `arrays`](SYSTEM-LIBRARY-ARRAYS.markdown). Functions for basic array manipulations.
* [Module `blocks`](SYSTEM-LIBRARY-BLOCKS.markdown). Block-related functions (just the `findLinkedBlocks` function at this moment).
* [Module `compatibility`](SYSTEM-LIBRARY-COMPATIBILITY.markdown). A special-purpose library for testing Mindcode's compatibility with a specific Mindustry version.
* [Module `graphics`](SYSTEM-LIBRARY-GRAPHICS.markdown). Additional graphics functions. Functions related to display transformations require the Mindustry Logic 8 instruction set.
* [Module `math`](SYSTEM-LIBRARY-MATH.markdown). A math library.
* [Module `printing`](SYSTEM-LIBRARY-PRINTING.markdown). Functions for printing and formatting numbers. Some functions require the Mindustry Logic 8 instruction set.
* [Module `units`](SYSTEM-LIBRARY-UNITS.markdown). Functions for searching and binding available units of a required type.

## Size of library functions

As Mindustry Logic limits the program size to 1,000 instructions, the compiled size of the library functions is an important consideration. The documentation of each function contains a table specifying the size of the resulting code under various conditions. The size is measured using these rules:

- all input arguments are variables,
- all output values provided by the function are used,
- the target is the latest supported target (might be different from a default target),
- the optimization level is set to `experimental`,
- for internal arrays, the array jump table is not included in the measurement. The size of the jump table corresponds to the number of elements in the array, and is not related to the function itself.

If a function just returns its input parameter as the return value and does nothing else, it doesn't produce any instruction—its size is zero. The system libraries sometimes use these functions for consistency, for example, as a special case for a vararg function.  

The following types of function calls are measured:

- **Inlined function**: the size of the function when called inline. Valid for functions that are declared inline or inlined later either because they're called just once, or by the Function Inlining optimization.
- **Function body**: the size of the function when not called inline. It is typically just one instruction larger than the inline version, because an instruction to return from the function to the caller needs to be added. Not shown for functions that are declared `inline`.
- **Function call**: the number of instructions used to call the function from another place of the program. Not shown for functions that are declared `inline`.
- Vararg functions can only be declared `inline`. For these functions, the size of the function depends heavily on the number of arguments passed in. Therefore, for vararg functions a few examples of function calls and their corresponding sizes are included with each function.

There are several factors that might cause the size of a function used in an actual program to differ from the measurement above:

- Optimizations might remove instructions setting up input parameters or receiving output values from the function.
- When constant values are used as input parameters in inlined functions, optimizations might make the resulting code significantly smaller, as constants in expressions can sometimes be optimized away. 
- If the output value of an output parameter isn't used in the entire program, the output parameter might get removed from the function body.

> [!NOTE]
> The function sizes are measured separately for the `speed` and `size` optimization goals. In some cases, optimizing for speed may produce smaller code than optimizing for size. The reason for this primarily is that optimization for speed may unroll some loops resulting in linear code, which is much better suited for further optimizations.

# Additional resources

The system library is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the library can be found [here](https://github.com/cardillan/mindcode/tree/main/compiler/src/main/resources/library).

---

[&#xAB; Previous: Logic functions](FUNCTIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry 8 &#xBB;](MINDUSTRY-8.markdown)
