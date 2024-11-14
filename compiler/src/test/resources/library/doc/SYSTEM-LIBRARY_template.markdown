# System library

Mindcode comes equipped with a system library. System library is stored in several files. To use a particular file from the system library, the `require` statement needs to be used:

```
require blocks;   // Use the 'blocks' system library
```

System libraries contain functions and sometimes constants that can be used by a Mindcode program. The following system libraries are provided:

* `graphics`: additional graphics functions. Requires the Mindustry Logic 8 instruction set.
* `printing`: functions for outputting formatted numbers. Some functions require the Mindustry Logic 8 instruction set.
* `blocks`: block-related functions (just the `findLinkedBlocks` function at this moment).
* `units`: functions for searching and binding available units of required type.
* `math`: mathematical functions.

The system library is an experimental feature. The functions provided by the library and the mechanism for its inclusion in your program may change in future releases.

> [!IMPORTANT]
> Using a program parameter or constant with a name matching a name of one of the library variables causes a compilation error. To avoid this problem, all function parameters in system libraries start with an underscore. Do not declare program parameters or constants starting with an underscore.
>
> This limitation of the system library will be removed in one of the future releases.
#generate
# Additional resources

The system library is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the library can be found [here](https://github.com/cardillan/mindcode/tree/main/compiler/src/main/resources/library).

---

[« Previous: Mindustry 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
