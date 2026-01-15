# Compatibility library

A special-purpose library for testing Mindcode's compatibility with a specific Mindustry version.

## Functions

### runCompatibilityTest

**Definition:** `inline void runCompatibilityTest()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                 976 |                976 |

This function runs the compatibility test on a Mindustry Logic processor. The compatibility test verifies that the
compiler's metadata corresponding to the current target are identical to the actual data in the Mindustry processor.
The test needs to be run on a logic processor (a microprocessor can also be used, but it takes a few seconds to finish
the test) with a message block linked as `message1`. The result of the test is output on the message block.

If the test fails, the message will suggest compiler options to use to avoid compatibility issues. By using the
suggested compiler options, Mindcode will generate code that should run correctly on the logic processor on which
the test was performed, avoiding the compatibility issues.

Additionally, the procedure also tests whether assigning `null` to the `@counter` variable is ignored by the processor,
and outputs a message accordingly. Again, a message suggesting a compiler option to use is displayed if the test fails.

The function never returns: when the test finishes, the processor loops indefinitely. This test isn't meant to be
incorporated into a larger program. Typically, a program for running the test will look like this:

```
// Set the target appropriately
#set target = 8;

require compatibility;

runCompatibilityTest();
```

---

[&#xAB; Previous: Blocks](SYSTEM-LIBRARY-BLOCKS.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Graphics &#xBB;](SYSTEM-LIBRARY-GRAPHICS.markdown)
