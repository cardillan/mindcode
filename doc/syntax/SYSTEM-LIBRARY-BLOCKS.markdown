# Blocks library

Block-related functions (just the `findLinkedBlocks` function at this moment).

## Functions

### findLinkedBlocks

**Definition:** `inline void findLinkedBlocks(title, message, linkMap...)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Linking two blocks                       |                  31 |                 31 |
| Linking four blocks                      |                  50 |                 50 |
| Linking six blocks                       |                  69 |                 69 |

Searches blocks linked to the processor for blocks of requested types and assigns them to given variables if found.
The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types instead of using the predefined link name.
By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name,
such as `message1` or `switch1`. The function can handle situations when two blocks of the same type are needed
(e.g., two switches), or it can handle situations where a single variable can accept multiple block types
(e.g., either memory cell or memory bank), but not both situations at once.

Function outputs status information while it is running to the block passed in the `message` parameter.
If that block is found among the linked blocks, it is skipped.

**Inputs and outputs:**

- `title`: title to be used as part of the status information. Pass `null` to avoid printing the title.
- `message`: block to use to output the status information. For example, `message1`.
- `linkMap`: definition of the required blocks. Each block needs four variables:
  - `requested`: type of the requested block, e.g. `@switch`.
  - `name`: name of the block to use as part of the status information.
  - `out variable`: variable to receive the block
  - `required`: if `true`, the function will wait until a block of a given type is linked to the processor. If `false`, the function doesn't wait.

Example of a call to this function:

```
require blocks;

findLinkedBlocks("Example program.\nTrying to locate linked blocks", message1,
    @large-logic-display,   "Display",  out display,    true,
    @message,               "Message",  out message,    false,
    @switch,                "Switch",   out switch,     false,
    @memory-bank,           "Memory",   out memory,     true,
    @memory-cell,           "Memory",   out memory,     true
);
```

When the function call ends, the `display` and `memory` variables are set to a large display or memory cell/memory
bank respectively. `message` and `switch` are set if corresponding blocks are linked to the processor,
otherwise they're `null`.


### findLinkedBlocksMessage

**Definition:** `inline void findLinkedBlocksMessage(title, linkMap...)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Linking two blocks                       |                  29 |                 29 |
| Linking four blocks                      |                  48 |                 48 |
| Linking six blocks                       |                  67 |                 67 |

Searches blocks linked to the processor for blocks of requested types and assigns them to given variables if found.
The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types instead of using the predefined link name.
By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name,
such as `message1` or `switch1`. The function can handle situations when two blocks of the same type are needed
(e.g., two switches), or it can handle situations where a single variable can accept multiple block types
(e.g., either memory cell or memory bank), but not both situations at once.

Function outputs status information into the first block of type `@message` it finds in the linked block list.

**Inputs and outputs:**

- `title`: title to be used as part of the status information. Pass `null` to avoid printing the title.
- `linkMap`: definition of the required blocks. Each block needs four variables:
  - `requested`: type of the requested block, e.g. `@switch`.
  - `name`: name of the block to use as part of the status information.
  - `out variable`: variable to receive the block
  - `required`: if `true`, the function will wait until a block of a given type is linked to the processor. If `false`, the function doesn't wait.

Example of a call to this function:

```
require blocks;

findLinkedBlocksMessage("Example program.\nTrying to locate linked blocks",
    @large-logic-display,   "Display",  out display,    true,
    @message,               "Message",  out message,    false,
    @switch,                "Switch",   out switch,     false,
    @memory-bank,           "Memory",   out memory,     true,
    @memory-cell,           "Memory",   out memory,     true
);
```

When the function call ends, the `display` and `memory` variables are set to a large display or memory cell/memory
bank respectively. `message` and `switch` are set if corresponding blocks are linked to the processor,
otherwise they're `null`.


---

[&#xAB; Previous: Bigarray](SYSTEM-LIBRARY-BIGARRAY.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Compatibility &#xBB;](SYSTEM-LIBRARY-COMPATIBILITY.markdown)
