# Bigarray library

Used internally by the Mindcode compiler/Schemacode builder to create processors for large storage.

## Constants

### chars

**Definition:** `const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz?$&()<=>[]^_";`

## Functions

### initializeBigArray

**Definition:** `inline void initializeBigArray(index)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Basic usage                              |                 259 |                259 |

Creates the code for a BigArray storage processor at the given index. When the index is greater than 15,
no name setup code is generated, as such processors only contain data and not a variable name index.


---

[&#xAB; Previous: Arrays](SYSTEM-LIBRARY-ARRAYS.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Blocks &#xBB;](SYSTEM-LIBRARY-BLOCKS.markdown)
