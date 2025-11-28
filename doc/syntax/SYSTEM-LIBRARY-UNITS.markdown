# Units library

Functions for searching and binding available units of a required type.

## Functions

### noControlWithin

**Definition:** `def noControlWithin(x, y, radius)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   6 |                  6 |
| Function body                            |                   7 |                  7 |
| Function call                            |                   6 |                  6 |

Determines whether the current unit is within a given radius from a given point.
Unlike the built-in `within()` function, this function doesn't get the control of the unit to make the test
but is slower. If you already control the current unit, use `within()` instead.

### findFreeUnit

**Definition:** `def findFreeUnit(unit_type, initial_flag)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  15 |                 15 |
| Function body                            |                  16 |                 16 |
| Function call                            |                   5 |                  5 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
If no free unit of the given type can be found (either because none exists or because all existing units are occupied),
the function returns immediately.

**Inputs and outputs:**

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit, or `null` if no free unit of the given type exists.

The function doesn't use units that are controlled by a player or a different processor.

### findClosestUnit

**Definition:** `def findClosestUnit(x, y, unit_type, initial_flag)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  30 |                 29 |
| Function body                            |                  31 |                 30 |
| Function call                            |                   7 |                  7 |

Searches for and binds a free unit of the given type closest to the coordinates on the map given.
If no free unit of the given type can be found (either because none exists or because all existing units
are occupied), the function returns immediately.

**Inputs and outputs:**

- `x`, `y`: position of the map to compute unit distance relative to
- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit closest to given coordinates, or `null` if no free unit of the given type exists.

The function doesn't use units that are controlled by a player or a different processor.

### waitForFreeUnit

**Definition:** `def waitForFreeUnit(unit_type, initial_flag)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   5 |                  5 |
| Function body                            |                   6 |                  6 |
| Function call                            |                   5 |                  5 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
The function doesn't return until a free unit of the given type can be found.

**Inputs and outputs:**

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit.

The function doesn't use units that are controlled by a player or a different processor.

### waitForFreeUnit

**Definition:** `def waitForFreeUnit(message, preface, unit_type, initial_flag)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  15 |                 15 |
| Function body                            |                  16 |                 16 |
| Function call                            |                   7 |                  7 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
The function doesn't return until a free unit of the given type can be found. The function prints status
information about the search onto a message block—one of two messages:

- `No unit of type <unit type> found.` when no unit of the given type exists,
- `Looking for a free <unit type>...` when units of the given type exist, but none is free to use.

**Inputs and outputs:**

- `message`: message block to receive status information about the search.
- `preface`: additional text to output before the status message, e.g., description of the processor.
- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit.

The function doesn't use units that are controlled by a player or a different processor.

---

[« Previous: Printing](SYSTEM-LIBRARY-PRINTING.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown)
