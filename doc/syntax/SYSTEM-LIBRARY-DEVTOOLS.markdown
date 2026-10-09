# Devtools library

Provides functions for some of the instructions provided by the Mlog Dev Tools mod.

## Functions

### breakpoint

**Definition:** `inline void breakpoint()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates an unconditional breakpoint.

### breakpoint

**Definition:** `inline void breakpoint(condition)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a conditional breakpoint. The breakpoint only triggers when the condition evaluates as true (that is,
not `false`).

### startProfiling

**Definition:** `inline void startProfiling()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Activates profiling of the current processor. The profiling results are available on the processor's
**Profile** screen.

### startProfiling

**Definition:** `inline void startProfiling(processor)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Activates profiling of the specified processor. The profiling results are available on the processor's
**Profile** screen.

**Inputs and outputs:**

- `processor`: the processor to profile. Any processor accessible to the user can be profiled.

### stopProfiling

**Definition:** `inline void stopProfiling()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Deactivates profiling of the current processor. The profiling results are available on the processor's
**Profile** screen.

### stopProfiling

**Definition:** `inline void stopProfiling(processor)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Deactivates profiling of the specified processor. The profiling results are available on the processor's
**Profile** screen.

**Inputs and outputs:**

- `processor`: the processor to profile. Any processor accessible to the user can be profiled.

### clearProfiling

**Definition:** `inline void clearProfiling()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Clears the profiling data of the current processor. Clearing the profiling data does not activate or deactivate
profiling.

### clearProfiling

**Definition:** `inline void clearProfiling(processor)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Clears the profiling data of the specified processor. Clearing the profiling data does not activate or deactivate
profiling.

**Inputs and outputs:**

- `processor`: the processor to profile. Any processor accessible to the user can be profiled.

### restartProcessor

**Definition:** `inline void restartProcessor()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Restarts the current processor. If a profiling was active before, it will be reactivated after the restart, but the
profiling data will be cleared.

### restartProcessor

**Definition:** `inline void restartProcessor(processor)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Restarts the specified processor. If a profiling was active before, it will be reactivated after the restart, but the
profiling data will be cleared.

**Inputs and outputs:**

- `processor`: the processor to restart. Any processor accessible to the user can be restarted.

### isolatedSnapshot

**Definition:** `inline void isolatedSnapshot(name)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates an isolated snapshot of the current processor with the specified name. The snapshot records the state of the
processor at the time of the snapshot.

**Inputs and outputs:**

- `name`: the name of the snapshot.

### isolatedSnapshot

**Definition:** `inline void isolatedSnapshot(block, name)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates an isolated snapshot of the given block with the specified name. The snapshot records the state of the
block at the time of the snapshot.

**Inputs and outputs:**

- `block`: the block to take a snapshot of.
- `name`: the name of the snapshot.

### connectedSnapshot

**Definition:** `inline void connectedSnapshot(name)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a connected snapshot of the current processor with the specified name. The snapshot records the state of the
processor, and all entities related to the processor (linked blocks, objects stored in processor's variables,
controlled units) at the time of the snapshot.

**Inputs and outputs:**

- `name`: the name of the snapshot.

### connectedSnapshot

**Definition:** `inline void connectedSnapshot(block, name)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a connected snapshot of the given block with the specified name. The snapshot records the state of the
block, and all entities related to the block (linked blocks, objects stored in processor's variables,
controlled units in case of processors, and stored objects in cases of memory blocks) at the time of the snapshot.

**Inputs and outputs:**

- `block`: the block to take a snapshot of.
- `name`: the name of the snapshot.

### recordingSnapshot

**Definition:** `inline void recordingSnapshot(steps)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a series of recording snapshots in the current processor for the specified number of instructions.
A connected snapshot is created first, followed by a snapshot after the execution of each instruction.
The instruction snapshots include objects referenced by the instruction's variables.

The number of steps is limited by the mod's settings.

**Inputs and outputs:**

- `name`: the name of the snapshot.

### recordingSnapshot

**Definition:** `inline void recordingSnapshot(processor, steps)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a series of recording snapshots in the specified processor for the specified number of instructions.
A connected snapshot is created first, followed by a snapshot after the execution of each instruction.
The instruction snapshots include objects referenced by the instruction's variables.

The number of steps is limited by the mod's settings.

**Inputs and outputs:**

- `processor`: the processor to record the snapshots of. Any processor accessible to the user can be recorded.
- `steps`: number of instruction executions to record.

### globalSnapshot

**Definition:** `inline void globalSnapshot(name)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Creates a global snapshot. The snapshot records the state of each accessible processor and each accessible
memory block on the map.

**Inputs and outputs:**

- `name`: the name of the snapshot.

---

[&#xAB; Previous: Compatibility](SYSTEM-LIBRARY-COMPATIBILITY.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Graphics &#xBB;](SYSTEM-LIBRARY-GRAPHICS.markdown)
