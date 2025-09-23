# Blocks library

To use the Blocks library, use the `require blocks;` statement.

## Functions

### findLinkedBlocks

**Definition:** `inline void findLinkedBlocks(title, message, linkMap...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Linking two blocks             |                  25 |                 27 |
| Linking four blocks            |                  34 |                 40 |
| Linking six blocks             |                  43 |                 53 |

Searches blocks linked to the processor for blocks of requested types and assigns them to given variables if found.
The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types instead of using the predefined link name.
By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name,
such as `message1` or `switch1`. The function cannot handle situations when two blocks of the same type are needed
(e.g., two switches), but can handle situations where a single variable can accept multiple block types
(e.g., either memory cell or memory bank).

Function outputs status information while it is running to the block passed in the `message` parameter.
When a `@message` block is among the required types and is found, it is used instead of the `message` parameter.

**Inputs and outputs:**

- `title`: title to be used as part of the status information.
- `message`: initial block to use to output status information. Typically `message1`.
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

