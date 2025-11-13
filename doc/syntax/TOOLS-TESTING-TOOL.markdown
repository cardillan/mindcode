# Testing tool

## Extended testing

Mindcode comes with a set of optimizers and other settings that can be independently set, leading to more than half a billion of possible configurations that can be used to compile the code. Only a handful of typical configurations are tested each time Mindcode is built, leaving all other possible configurations untested.

While typically just a handful of possible configurations, which get tested during each build, are used, sometimes a particular optimization is not available, causing a relatively rare code structure being fed into subsequent optimizations. This may uncover additional bugs.

It therefore makes a lot of sense to test non-typical configurations. Furthermore, the _intention_ is for every possible compiler/optimizer configuration to be correctly handled by Mindcode. To help achieve this goal, a separate testing tool is provided.

> [!TIP]
> It is not expected that regular Mindcode users will use the testing tool. The tool is intended for Mindcode language developers and possibly for interested users that might want to extensively test their own code for added safety.

### Setting up the testing tool

The testing tool can be set up in the following way:

1. Download and install [Eclipse Temurin version 22](https://adoptium.net/temurin/releases/?version=22).
2. Download `mindcode-exttest.jar` from the [Releases page](https://github.com/cardillan/mindcode/releases) and place it in a directory on your computer.
3. To run the testing tool, use `java.exe -jar mindcode-exttest.jar`. Provide full paths to the `java.exe` file from the Eclipse Temurin installation created in the first step, and to the `mindcode.jar` file downloaded in the second step. The testing tool takes no arguments:

```
java -jar mindcode-exttest.jar
```

### Testing tool configuration

Configuration for the testing tool must be stored in a `settings.yaml` file in the current directory. An example configuration file is here:

```yaml
# TESTING TOOL SETTINGS

# Number of tests to run in parallel
threads: 24

# A name of the section containing the list of tests to execute
# Several sections can be present in the config file, the active one is selected here
#test-suite: single-source
test-suite: all-sources

# Where to put all the output files
# When not set, test-specific output files will be generated in the directory containing the source file
# Global output files (result.txt) will be generated in the current directory.
output-path: .

# 'true' to run all possible configurations for all tests, 'false' to run the given number of samples
# (full tests would typically take many hours to complete)
full-tests: false

# This number will multiply the number of samples configured to run per test
# Allows to create a basic configuration that runs reasonably fast
# and just increase the multiplier for longer, more comprehensive tests
sample-multiplier: 1


# DEFAULT TEST SETTINGS

# All values specified here may be overridden for specific tests
defaults:
  # Number of test cases to execute
  samples: 500

  # When the number of failures encountered while executing a test exceeds the given value, the test is stopped
  # Prevents creation of indiscriminately large output files when too may tests fail
  failure-limit: 500

  # 'true' to compile, optimize and run the code, 'false' to just compile and optimize.
  run: true

  # List of goal settings to use
  goal: [ speed, size ]

  # List of optimization levels for each given optimization
  case-expression-optimization: [ none ]
  case-switching: [ none ]
  data-flow-optimization: [ none, basic, advanced, experimental ]
  dead-code-elimination: [ none, basic, advanced ]
  expression-optimization: [ none, basic, advanced ]
  function-inlining: [ none, basic, advanced ]
  if-expression-optimization: [ none, basic, experimental ]
  jump-normalization: [ none, basic ]
  jump-optimization: [ none, basic ]
  jump-straightening: [ none, basic ]
  jump-threading: [ none, basic, advanced ]
  loop-hoisting: [ none, basic, advanced ]
  loop-optimization: [ none, basic ]
  loop-unrolling: [ none, basic, advanced ]
  print-merging: [ none ]
  return-optimization: [ none, basic ]
  single-step-elimination: [ none, basic, advanced, experimental ]
  stack-optimization: [ none ]
  temp-variables-elimination: [ none, basic ]
  unreachable-code-elimination: [ none, basic, advanced ]


# LIST OF SOURCES

# A single file to test.
# Unspecified settings are inherited from default.
single-source:
  - source: sum-of-primes.mnd
    samples: 20000


# List of all files to test
all-sources:
  - source: math.mnd
    samples: 200

  - source: printing.mnd
    samples: 400
    print-merging: [ none, basic, advanced, experimental ]

  - source: quick-sort.mnd
    samples: 5000
    stack-optimization: [ none, basic ]

  - source: bubble-sort.mnd
    samples: 2000

  - source: sum-of-primes.mnd
    samples: 2000

  - source: impact-reactor-logic.mnd
    samples: 125
    run: false
```

A slightly more complex configuration is available [in the repository](https://github.com/cardillan/mindcode/tree/main/exttest/data/settings.yaml).

### Preparing a code for testing

A code to be tested by the testing tool must be stored in a standalone file. The test may consist of compiling and optimizing the code with different settings, or of compiling, optimizing and running it. In both cases, a test will fail when the code doesn't compile or when the compiler or optimizer produces any warnings; therefore, it is crucial that no warnings are present when the code is compiled with typical settings (e.g., advanced optimization level for all optimizers).

When the code is compiled but not run, only bugs that result in additional warnings or compilation crashes will be detected. While a lot of bugs in the past did result in an unexpected warning or compilation failure, obviously there might be bugs that result in faulty code with no additional errors or warnings present. These bugs can be caught by running the code and comparing the produced output to the expected value(s). These checks are achieved through the use of `assertEquals` and `assertPrints` functions (see [unit testing](#unit-testing-support)). On top of that, it is also expected that the execution won't fail in any of the [irregular situations](TOOLS-PROCESSOR-EMULATOR.markdown#irregular-situation-handling). A test can be configured to ignore a particular irregular execution by clearing corresponding [execution flag(s)](TOOLS-PROCESSOR-EMULATOR.markdown#execution-flags) through the `#set` directives. 

The code to be tested should conform to these requirements:

* Compile without errors or warnings in typical settings (see above).
* Avoid explicitly setting any optimization levels or code generation goal through the `#set` directives (these settings would override configurations set up by the testing tool).
  * If a specific optimization setting is required for some reason, it should be set up in the testing tool configuration.
* If the code is meant to be run by the test tool:  
  * Include at least one assertion (`assertEquals` or `assertPrints`).
  * Use the `stopProcessor()` function to stop the execution of the code after all assertions have been run (some optimization configurations might remove the `end` instructions or replace them with jumps to the front of the code, preventing the processor emulator to stop the execution after running the code just once).
  * Use processor emulator flags if necessary to avoid errors when running the code.

### Testing failures

When a test produces a failure, it is recorded in the results' file for current test (the name of the results' file is `result-<source-file-name>.mnd`). When encountering a test failure, please report the bug [by creating an issue](https://github.com/cardillan/mindcode/issues/new). Include the exact source code and the `settings.yaml` file that produced the error.

## Unit testing support

Mindcode currently provides limited support for [unit testing](https://en.wikipedia.org/wiki/Unit_testing), in the language itself and in the processor emulator. Unit testing code is not meant to be run in Mindustry Logic processors.

The Mindcode compiler supports two functions which generate _assertions_. Assertions are evaluated at runtime by the processor emulator, and the result of the evaluation is reported. Failed assertions are reported as errors.

* `void assertEquals(expected, actual, message)`: the function creates an assertion which compares the `expected` and `actual` values. The two values can be any expressions, and the assertion fails if the two values do not match exactly (using strict equality comparison, i.e., `===`.)

* `void assertPrints(expected, textGenerator, message)`: the function creates an assertion which compares the `expected` value to the contents of the text buffer. The assertion fails when the `expected` value isn't exactly identical to the contents of the print buffer generated by the `textGenerator` expression or function call. The return value of the `textGenerator` expression is ignored and may even be `void`.

Unit tests testing the `math` and `printing` libraries look like this:

```Mindcode
#set target = 8;
require math;
require printing;

assertEquals(5, distance(1,1,4,5), "distance(1,1,4,5)");
assertEquals(1, sum(1), "sum(1)");
assertEquals(5, median(2,6,4,8), "median(2,6,4,8)");

assertPrints("1,234,567", printNumber(1234567), "printNumber(1234567)");
assertPrints("0", printExactFast(0), "printExactFast(0)");
assertPrints("1.012345678900000", printExactSlow(1.0123456789), "printExactSlow(1.0123456789)");
```

The special instructions generated by the `assertEquals` and `assertPrints` functions are present in the generated mlog code. The assertions are automatically evaluated when running the code on the emulated processor. It's not recommended to use a program containing these special instructions in an actual Mindustry Logic processor, as these instructions will be invalid there.

To create a basic unit test, a separate source file containing just the assertions should be created. This file would use the `require` directive to include the file being tested. The tested files should contain only functions, as any code present in the tested file would be executed before the unit tests, which isn't desirable.

---

[« Previous: Mlog Decompiler](TOOLS-MLOG-DECOMPILER.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Troubleshooting Mindcode »](TROUBLESHOOTING.markdown)
