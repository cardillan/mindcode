# Remote functions and variables

Mindcode supports accessing variables (including arrays) in linked processors and calling Mindcode functions in them. This functionality uses the Mindustry Logic 8 capability to read and write variables to/from other processors, including `@counter`, by name.

Calling functions stored in other processors is a feature which brings two main benefits to Mindcode:

1. Possibility to break a large program into several pieces stored in multiple processors, effectively surpassing the 1000 instruction limit per program.
2. Means to call remote functions asynchronously, resulting in parallel processing. 

## Architecture

Processor which accesses other processor's variables or functions is called _main processor_. The processor which contains these variables and functions is called _remote processor_. A main processor can use multiple remote processors, but each remote processor can be used by at most one main processor. Furthermore, a remote processor can act as a main processor to another remote processor. Processors can therefore be arranged into an oriented graph - a tree (in the sense of graph theory - the root is the main processor, which starts and controls the execution over the entire processor tree).

The execution of the entire distributed program starts with the topmost main processor (there's always just one), while the other remote processors wait until a remote call is initiated from their respective main processor. Both main processor and the remote processors wait until their respective linked processors are initialized.

To successfully run a distributed program in Mindustry World, all necessary processors with their code need to be built. The easiest way to do so is to create a schematics (using [Schemacode](SCHEMACODE.markdown)) containing all the processors, properly linked, and their code.

Code for each processor in a distributed system is compiled separately from a standalone source code. When using the command-line compiler, each source code would typically be stored in a separate file. It is also possible to include all processors and their code in a single Schematics file, which can be compiled by the web application.

A remote processor can act as a main processor to another remote processor. Processors can therefore be arranged into an oriented graph - a tree (in the sense of graph theory - the root is the main processor, which starts and controls the execution over the entire processor tree).

## Remote processors code

Code belonging to a remote processor must be placed in a module. A module is created by using the `module` keyword. Modules must be named, although at this moment there is no use for the name:

```
module library;
```

When compiling a module, the compiler generates initialization code. This code creates variables holding address of each remote function, named `:functionName*address`. Furthermore, `*mainProcessor` variable is initialized to `@this`. After executing the initialization, the code enters an endless loop, awaiting remote function call.

If the module contains any code outside functions (i.e. a main code block), this code is executed as part of the initialization of the remote processor. Only when this code completes the processor is ready to accept remote calls. Main processor's initialization waits for all remote processor initializations to complete, so it is crucial for the initialization code to be fast. If the initialization code contains an endless loop, the main processor execution will be blocked forever.

Note: there's no specific designation of remote modules. Any module may contain remote functions/variables. When such a module is imported locally through plain `require`, the remote variables are accessible locally, but the remote functions cannot be called.

### Remote variables

It is possible to declare a remote variable or array in a module using the `remote` keyword:

```
[noinit] [volatile] remote [var] <variable1> [= <initial value>];
[volatile] remote [var] <variable1>'['size']' [= (<initial values>)] [, <variable2> [= (<initial values>)] ... ];
```

Remote variables must be declared in global scope and are therefore always global. When an initial value is provided, it is assigned to the variable at the moment of the declaration. Any expression can be used as the initial value, including function calls.

When declaring remote variables these additional modifiers can be used:

* `noinit`: this modifier suppresses the "uninitialized variable" warning for the declared variable. This modifier cannot be used if the variable is assigned an initial value.
* `volatile`: remote variables are volatile by default, because they may be read or written by other processors. It is possible to specify the `volatile` modifier explicitly for better clarity. 

Modifiers can be specified in any order.

```
// Remote variables, a unitialized, b initialized
remote a, b = 10;         

// Remote arrays: initialized and uninitialized
remote array1[10];
remote array2[5] = (1, 2, 3, 4, 5);
```

Remote variables are available through remote access in code which imports the module. Remote variables/arrays may or may not be initialized. When some remote variables or array elements are unused within the module (and the corresponding mlog variables would therefore not be created when parsing the code by the logic processor), the missing variables are explicitly created using `draw triangle` instruction(s), so that they may be accessed remotely. These instructions are placed at the end of the generated code and are never executed.

### Remote functions

Function to be called remotely must be declared using the `remote` keyword again:

```
remote def foo(x, y, out z)
    z = x + y;
    return x * y;
end;
```

Function parameters may be input or output (as usual). A remote function is never inlined and can be declared neither `inline` nor `noinline`. Varags aren't supported. Remote function cannot be overloaded, although it is possible to overload a remote function with non-remote ones. Remote functions must not be called from the module which declares them, meaning they also can't be recursive. It is possible to call a recursive function from within a remote function, though.

All remote functions are active entry points for the compiler and thus are always included in the compiled code.

## Main processor code

All remote modules must be declared in the main processor code using the `require ... remote` statement. This declaration specifies which remote processor contains remote functions:

```
require "library.mnd" remote processor1;
```

The required file (in this example stored in `library.mnd` file) must be a module, and the compiled code of this module must be stored in processor `processor1`. The processor can be specified using a linked block name, a parameter, or a variable. The variable needs to be initialized, e.g. by a function. 

When a module is being requested for remote access using two different processors, or is being requested both locally end remotely, a compilation error occurs.

Modules imported remotely aren't compiled into the code, but remote variables and functions declared in them are available to the main processor.

When at least one remote function is called from a processor, guard code for the processor is generated. The guard code waits for the processor to be created and its `*mainProcessor` variable initialized. When this state is reached, the following happens:

* The `*mainProcessor` variable in the remote processor is set to `@this`
* `:functionName*address` is copied from the remote processor to the local processor for each function called remotely.

### Remote variables

All variables declared `remote` in remotely requested modules are accessible in the main processor. It's not required to declare the variable in the main processor again.

An array declared `remote` in remotely imported modules is accessible in the main processor. The array elements are stored in the remote processor. Access to individual elements happens through the `read` and `write` instructions, not through remote calls. For random access, jump tables are generated in the main processor code, meaning that random access to remote arrays has he same performance as random access to local arrays. Direct access to remote array elements gets resolved to remote variable access, which is as fast as external variable access (except it supports non-numerical values).

### Synchronous remote calls

The syntax to call functions remotely is the same as for local function call:

```
product = foo(10, 20, out sum);
```

Execution of the code is paused until the remote function completes and returns. The effect of calling a remote function is the same as calling local function: function return value and output variables are set up as usual.

The performance of the synchronous remote call depends on many factors. For infrequent calls to larger remote functions (i.e. less than one call per tick, while the call itself takes more than a tick), the performance is comparable to local calls, and might even be better due to the fact that after waiting for the remote call to complete, the rate of instruction execution (instructions per tick) may even be temporarily increased.

For frequent calls to short functions, the performance suffers, as instructions in both local and remote processors must be executed to complete the call, which always takes at least one tick. The faster the processor and/or its overdrive, the higher the fraction of time potentially lost waiting for the tick to complete.

### Asynchronous remote calls

When a remote function is called asynchronously, the main processor continues to execute code while the remote function computes. It is possible to determine whether the remote function has completed, and/or wait for its completion and receive return values. Multiple asynchronous remote calls may be active at the same time, provided each of them is executed on a different remote processor.

Asynchronous calls are started using the built-in `async()` function:

```
async(foo(10, 20));
```

The `out` modifiers are disallowed in asynchronous function calls, and output only arguments need to be completely omitted.

To check for completion, call the `finished()` function, passing the name of the remote function as an argument:

```
done = finished(foo);
```

The function returns true if the asynchronous call to the given remote function is finished.

Function return value may be obtained via the `await()` function (not to be confused with the `wait()` function mapped to the `wait` instruction):

```
product = await(foo);
sum = foo.z;
```

This call functions waits for `foo` completion, and then returns its return value. Other output parameters are available under their fully qualified names, e.g. `foo.z`. The output parameters may be queried as soon as `finished()` returned true. Calling `await()` is not necessary before starting another remote call on the same processor. 

## Mechanism of the remote calls

This chapter briefly describes how synchronous and asynchronous calls are implemented. 

### Local side - synchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the main processor.
* `@counter` in the remote processor is set to `:functionName*address`.
* Waiting for call completion:
  * instruction `wait 1e-15` is executed. This skips the rest of the tick, while transferring unspent instruction execution quota to the next tick(s). 
  * loop while `:functionName*finished` equals `false`
* At this point, it is known that the remote processor has written the output values (`:functionName*retval`, plus all output parameters) to the variables in this processor. Values of output parameters are copied to output arguments (this step is removed by optimization when possible).

### Local side - asynchronous call

* The function parameters are set up in the remote processor.
* `:functionName*finished` is set to `false` in the main processor.
* `@counter` in the remote processor is set to `:functionName*address`.
* The code execution continues in the main processor.
 
The mechanisms to monitor the call, wait for completion and retrieve output values are implemented by the programmer using the `await()` and/or `finished()` functions.

### Remote side

The call mechanism on the remote side is the same for both synchronous and asynchronous calls.

* Waiting for the remote call is implemented using the `wait 1e12` instruction. This wait is over 30 thousands years long, so it never completes. The wait also transfers unspent instruction execution quota to the next tick(s).
* Remote call is initiated by the main processor remotely setting `@counter` to the start of the function. The function starts executing immediately, as all parameters have been set by the caller.
* Upon completion, the function performs these operations:
    * All output values (`:functionName*retval`, plus all output parameters) are  set up in the main processor.
    * `:functionName*finished` in the main processor is set to `true`
    * The waiting phase is reentered.

## Concurrent function calls

If the same or different remote function gets called while a previous call is active, the previous call is terminated. The corresponding `:functionName*finished` flag won't be set.

There's no way to define a cleanup routine in case a remote call is terminated, and Mindcode doesn't guard against possible corruption of the remote processor state.

# Arbitrary remote variable access

To access arbitrary remote processor variables using the `read` and `write` instructions., `read(variable)`  and `write(value, variable)` methods must be used, to be called on processors.

# Schemacode support for modules 

All [string values](SCHEMACODE.markdown#string-value-definition) defined in a Schemacode source file are accessible through the `require` keyword as files. The name of the file must be the same as the identifier used to declare the string value, without any path or extension. This makes it possible to `require` source code stored in a string value as a remote module:

```
schematic
    name = "Parallel processing"

a-message1:
    @message            at  (0, 0)
    @micro-processor    at +(1, 0) processor links a-* end mindcode = Main end
a-processor1:
    @micro-processor    at +(1, 0) processor links b-* end mindcode = RemoteTest end
b-processor1:
    @micro-processor    at +(1, 0) processor mindcode = RemoteTest2 end
end

Main = """
    //#set optimization = none;
    #set target = 8;
    require "RemoteTest" remote processor1;

    greeting = name(out another);
    println($"Hello, $greeting $another!");
    printflush(message1);
    """

RemoteTest = """
    //#set optimization = none;
    #set target = 8;
    module RemoteTest;
    require "RemoteTest2" remote processor1;

    remote def name(out another)
        another = anotherName();
        return "Dolly";
    end;
    """

RemoteTest2 = """
    //#set optimization = none;
    #set target = 8;
    module RemoteTest2;

    remote def anotherName()
        return "and Polly";
    end;
    """
```

---

[« Previous: Mindustry Logic 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
