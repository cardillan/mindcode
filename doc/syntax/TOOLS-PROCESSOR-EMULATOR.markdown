# Processor emulator

Mindcode has a built-in processor emulator which allows to run the compiled code in a simulated environment. This emulator is available both to the web application and to the command-line tool. The emulated processor is much faster than Mindustry Logic processors; the speed of instruction executions is only limited by the speed of the computer.

## Linked blocks

The emulated processor is equipped with the following linked blocks:
* memory cells `cell1` to `cell9`,
* memory banks `bank1` to `bank9`,
* large logic displays `display1` to `display4`,
* logic displays `display5` to `display9`.
* message blocks `message1` to `message9`.

At the beginning of the executions all the memory cells and memory banks are empty.

## Supported instructions

All operations that do not interact with the Mindustry World are supported. Operations which do interact with the Mindustry World are handled as described here:

* Printing to the text buffer is fully supported and the text produced by the program is displayed when the execution ends.
  * The size of the text buffer is limited to 10,000 characters.
  * The `format` instruction is fully supported. Only text produced since the last `printflush` instructions is available for formatting.  
  * When the contents of the text buffer encountered at the `printflush` instruction is the same as the content encountered at the previous `printflush` instruction, the text isn't written to the text buffer again, just the number of the text block repetitions is printed. 
  * `draw print` instruction is handled like the `printflush` instructions: the text consumed by the `draw print` instruction isn't removed from the text buffer and is included in the program output.
* The `getlink` instruction can be used to access linked blocks specified above. The `@links` variable is set to the total number of linked blocks.
* The `read` and `write` instructions can be used with the linked memory cells and memory blocks obtained either through the linked variable (e.g. `cell1`), or through the `getlink` instruction.  
* The `sensor` instruction is supported for a very limited number of properties:
  * `type`: returns the type of the object (e.g. `@memory-cell`)
  * `id`: returns the id of the object if it exists (e.g. `1` for `@lead`)
  * Accessing other properties isn't supported. 
* The `wait` instruction isn't supported, specifically, it doesn't pause the execution of the program. 
* All other instructions that do not set an output variable are silently ignored. For example, the `ucontrol move` instruction executes, but does nothing (there's no unit to send the command to in the emulated world).
* Instructions which do set an output variable, such as `ulocate`, are not supported and, unless specifically configured, stop the execution of the program. Since the Mindustry World isn't simulated, it is not possible to set the values of output variables to some sensible value. 

## Irregular situation handling

Except the `stop` instruction, Mindustry processors never stop executing a program. Upon encountering an instruction which cannot be executed, the instruction simply isn't executed. For example, when the `read` instruction tries to read from memory which doesn't exist, it skips the execution without changing the variable which would receive the value read from the memory.

While mlog programs, and to a certain extent Mindcode programs, can sometimes put this mechanism to good use, in most cases such situations indicate a bug in the program. The processor emulator therefore, by default, stops when encountering an irregularity. It is, however, possible to set up the emulated processor to ignore the error and continue executing the program, just like the Mindustry processor would. This configuration is done through _execution flags_.       

## Execution flags

Execution flags can be set through compiler directives or command-line options. Each flag can be set to `true` or `false`. The function of most flags is to stop the emulator when a corresponding condition is encountered. Some flags change the emulator behavior instead.

All flags are described in the following table:

| Flag                            | Default | Meaning                                                                    |
|---------------------------------|:-------:|----------------------------------------------------------------------------|
| trace-execution                 |  false  | output instruction and variable states at each execution step              |
| stop-on-stop-instruction        |  true   | stop when the `stop` instruction is encountered                            |
| stop-on-end-instruction         |  true   | stop when the `end` instruction is encountered                             |
| stop-on-program-end             |  true   | stop when the end of instruction list is reached                           |
| err-invalid-counter             |  true   | stop when an invalid value is written to `@counter`                        |
| err-invalid-identifier          |  true   | stop when a malformed identifier or value is encountered                   |
| err-unsupported-opcode          |  true   | stop when unsupported instruction is encountered                           |
| err-uninitialized-var           |  false  | stop when an uninitialized variable is read                                |
| err-assignment-to-fixed-var     |  true   | stop on attempts to write a value to an unmodifiable built-in variable     |
| err-not-an-object               |  true   | stop when a numerical value is used instead of an object                   |
| err-not-a-number                |  true   | stop when an object is used instead of a numerical value                   |
| err-invalid-content             |  true   | stop when an invalid index is used in the `lookup` instruction             |
| err-invalid-link                |  true   | stop when an invalid index is used in the `getlink` instruction            |
| err-memory-access               |  true   | stop when accessing invalid memory-cell or memory-bank index               |
| err-unsupported-block-operation |  true   | stop when attempting to perform an unsupported operation on a block        |
| err-text-buffer-overflow        |  false  | stop when the text buffer size (400 characters) is exceeded                |
| err-graphics-buffer-overflow    |  true   | stop when the graphics buffer size (256 operations) is exceeded            |
| err-invalid-format              |  true   | stop when no placeholder for the `format` instruction exists in the buffer |

The `err-uninitialized-var` and `err-text-buffer-overflow` flags are `false` by default. It is expected that these events can happen even in an otherwise sound program. Setting them to `true` enforces even stricter standards in your programs.   

## Inspecting program state

The processor emulator provides two ways to inspect the internal state of the program.

Firstly, the contents of all variables is written to the message log when the `stop` instruction is encountered. Therefore, it is possible to use the `stopProcessor()` function to output the values of all variables at some point in the program. To let the program continue after that, and possibly output the variable values using the `stopProcessor()` function repeatedly, the `stop-on-stop-instruction` flag must be set to false.

Example of the variable dump:

```
stop instruction encountered, dumping variable values:
@counter: 76.0
__fn13__base: 60000.09930373835
__fn13__cmp: 8.976000009930374E-8
__fn13__exp: 0.0
__fn13__n: 1.00000008976
__fn13__t: 60000.0
__tmp1: 1.00000008976
__tmp10: 1.0
__tmp12: 1.0
__tmp13: 1.50000008976
__tmp15: 8.976000009930374E-8
__tmp21: 1.0
__tmp22: 0.6000009930373835
__tmp7: 3.898227098923865E-8
__tmp9: -0.0
number: 1.00000008976
```

Secondly, when the `trace-execution` flag is set, the instruction to be executed, followed by the values of all input variables, is printed to the message log at each step. The value of output variables is also printed after the instruction is executed. The size of the output produced by this mechanism can be quite sizeable, but it can be used to inspect the program flow in detail.

Example of the execution trace:

```
Program execution trace:
Step 1, instruction #0: set number 1.00000008976
   out number: 1.00000008976
Step 2, instruction #1: set __fn13__n number
   in  number: 1.00000008976
   out __fn13__n: 1.00000008976
Step 3, instruction #2: op abs __tmp1 number
   in  number: 1.00000008976
   out __tmp1: 1.00000008976
Step 4, instruction #3: jump 6 greaterThan __tmp1 0
   in  __tmp1: 1.00000008976
Step 5, instruction #6: jump 9 greaterThanEq number 0
   in  number: 1.00000008976
Step 6, instruction #9: op log10 __tmp7 __fn13__n
   in  __fn13__n: 1.00000008976
   out __tmp7: 3.898227098923865E-8
Step 7, instruction #10: op floor __fn13__exp __tmp7
   in  __tmp7: 3.898227098923865E-8
   out __fn13__exp: 0.0
Step 8, instruction #11: op mul __tmp9 -1 __fn13__exp
   in  __fn13__exp: 0.0
   out __tmp9: -0.0
Step 9, instruction #12: op pow __tmp10 10 __tmp9
   in  __tmp9: -0.0
   out __tmp10: 1.0
Step 10, instruction #13: op mul __fn13__base __fn13__n __tmp10
   in  __fn13__n: 1.00000008976
   in  __tmp10: 1.0
   out __fn13__base: 1.00000008976
```

In the web app, the number of messages output by either of these two mechanisms is limited to 1000; in the command-line application, the limit is 10,000. 

---

[« Previous: Command line tool](TOOLS-CMDLINE.markdown)  &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mlog Watcher »](TOOLS-MLOG-WATCHER.markdown)
