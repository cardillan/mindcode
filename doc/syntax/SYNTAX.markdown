# Mindcode

Mindcode is a high-level procedural programming language for [Mindustry Logic](https://github.com/Anuken/Mindustry). Many language features are provided, including variable declaration, arrays, conditional statements and loops, functions, modules, remote variables and function calls (synchronous and asynchronous), system library, user libraries, debugging support, and others. Mindcode generates fairly well-optimized mlog code, using available instruction space to make the resulting code faster. It comes with a [web app](http://mindcode.herokuapp.com/) and a [command-line compiler](TOOLS-IDE-INTEGRATION.markdown#setting-up-the-command-line-compiler) and provides means for integration both with various IDEs and Mindustry itself.

Mindcode provides programmatic access to full Mindustry Logic functionality—all mlog instructions are supported, and the language can be easily extended to support new instructions provided by mods or different clients.

This documentation covers the Mindcode syntax, libraries, supporting tools, best practices and more.

# Contents

* [Mindcode Basics](SYNTAX-0-BASICS.markdown)
* [Variables](SYNTAX-1-VARIABLES.markdown)
* [Expressions](SYNTAX-2-EXPRESSIONS.markdown)
* [Control Flow Statements](SYNTAX-3-STATEMENTS.markdown)
* [Functions](SYNTAX-4-FUNCTIONS.markdown)

Mindcode provides access to most mlog instructions via Logic functions:

* [Function reference for target 7.1](FUNCTIONS-71.markdown) (for the latest stable Mindustry version)
* [Function reference for target 8.1](FUNCTIONS-81.markdown) (for the latest Mindustry beta release)
* See [Logic functions](FUNCTIONS.markdown) for all supported versions.

A [System Library](SYSTEM-LIBRARY.markdown), consisting of several separate modules, is available:

* [Arrays](SYSTEM-LIBRARY-ARRAYS.markdown)
* [Blocks](SYSTEM-LIBRARY-BLOCKS.markdown)
* [Compatibility](SYSTEM-LIBRARY-COMPATIBILITY.markdown)
* [Graphics](SYSTEM-LIBRARY-GRAPHICS.markdown)
* [Math](SYSTEM-LIBRARY-MATH.markdown)
* [Printing](SYSTEM-LIBRARY-PRINTING.markdown)
* [Units](SYSTEM-LIBRARY-UNITS.markdown)

For Mindustry Logic 8 and later, many new functionalities are supported:

* [Mindustry Logic 8](MINDUSTRY-8.markdown)
* [Parallel processing](REMOTE-CALLS.markdown)

The more advanced Mindcode features and topics are discussed here:

* [Compiler Options](SYNTAX-5-OTHER.markdown)
* [Code Optimization](SYNTAX-6-OPTIMIZATIONS.markdown)
* [Extending Mindcode](SYNTAX-EXTENSIONS.markdown)
* [Best Practices](BEST-PRACTICES.markdown)

Schemacode, a schematic definition language, is covered here:

* [Schemacode](SCHEMACODE.markdown)

Supporting tools: 

* [Command line tool](TOOLS-CMDLINE.markdown)
* [IDE Integration](TOOLS-IDE-INTEGRATION.markdown)
* [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown)
* [Processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown)
* [Mlog Decompiler](TOOLS-MLOG-DECOMPILER.markdown)
* [Testing Framework](TOOLS-TESTING-TOOL.markdown)

Additional information on Mindcode and Mindustry Logic:

* [Troubleshooting Mindcode](TROUBLESHOOTING.markdown)
* [Mindustry Tips and Tricks](MINDUSTRY-TIPS-N-TRICKS.markdown)

---

[Next: Mindcode Basics &#xBB;](SYNTAX-0-BASICS.markdown)
