<div markdown="1" align="center">
   <img width="100%" src="wide-logo.png">

# Mindcode: a high-level compiler for mlog

<br>

[![Static Badge](https://img.shields.io/badge/web%20app-blue?link=http%3A%2F%2Fmindcode.herokuapp.com%2F)](http://mindcode.herokuapp.com/)
[![Static Badge](https://img.shields.io/badge/doc-mincdcode-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](doc/syntax/SYNTAX.markdown)
[![Static Badge](https://img.shields.io/badge/doc-mlog-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](https://yrueii.github.io/Mlog%20Documentation/)
[![GitHub issue custom search in repo](https://img.shields.io/github/issues-search/cardillan/mindcode?query=is%3Aopen%20label%3Abug&label=open%20bugs&color=red&link=https%3A%2F%2Fgithub.com%2Fcardillan%2Fmindcode%2Fissues%3Fq%3Dis%253Aissue%2520state%253Aopen%2520label%253Abug)](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug)
[![Static Badge](https://img.shields.io/badge/chat-discord-blue?link=https%3A%2F%2Fdiscord.gg%2FA8vdVdvf)](https://discord.gg/q4eGwpu2mp)

</div>
<br>

**Mindcode** is a high-level programming language for [Mindustry Logic](https://github.com/Anuken/Mindustry). Many language features are provided, including variable declaration, arrays (including `@counter` arrays), conditional statements and loops, functions, modules, remote function calls, system library etc. Mindcode generates fairly well optimized mlog code, using available instruction space to make the resulting code faster. It comes with a [web app](http://mindcode.herokuapp.com/) and a [command-line compiler](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#setting-up-the-command-line-compiler), provides means for integration both with various IDEs and Mindustry itself.

**Schemacode**, an extension built over Mindcode, is a specialized definition language designed for creating a complete Mindustry schematic from a text file. [Schematics Builder](doc/syntax/SCHEMACODE.markdown) compiles these definition files directly into Mindustry schematics, either into binary `.msch` file, or into the text representation. Processors can be included in these schematics, complete with the code (specified either in Mindcode or mlog) and linked blocks.

## Supported Mindustry versions

Mindcode can generate code for several Mindustry versions:

* **6.0 Build 126.2**: use `#set target = 6;` to select it.
* **7.0 Build 146** (the default one): use `#set target = 7;` to explicitly select it.  
* **v8 Build 149 Beta**: use `#set target = 8.0;` to select it.
* **v8 Build 150 Beta** or later: use `#set target = 8;` (or `8.1`) to select it.

At this moment, target `8` is also fully compatible with the latest Mindustry BE version (build 26149).

[Here](/doc/syntax/MINDUSTRY-8.markdown#new-functionality-in-mindustry-8) is a summary of the new Logic content and corresponding Mindcode functionality in Mindustry 8.

## Mindcode Syntax

Please refer to the [documentation](doc/syntax/SYNTAX.markdown) for a complete description of Mindcode syntax. You can also use the code examples in the web application to get familiar with Mindcode.  

Additionally, the following repositories contain Mindcode projects, which may also serve as an example of how to use Mindcode: 

* [golem](https://github.com/cardillan/golem), a collection of more complex Mindcode and Schemacode scripts, maintained by the author.
* [mindustry-mindcode-projects](https://github.com/50275/mindustry-mindcode-projects), an independent collection of Mindcode programs.

## Latest development

See [issues](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug) for open bugs and possible workarounds. 

The most important recent changes to Mindcode include:

* Mindustry Logic 8
  * Complete support for [the latest Mindustry 8 pre-release](/doc/syntax/MINDUSTRY-8.markdown).
  * Full support for [remote functions and variables](doc/syntax/REMOTE-CALLS.markdown).
  * New string/character-based instructions and character literals.
  * Support for named color literals: `%[red]`.
* Language features
  * Support for `null` values in the `case` expression.
  * Improved optimization of `case` expressions, including `case` expression over block types, unit types, items or liquids.
  * Passing arguments (including arrays) to inline functions by reference.
  * Specific syntax for [mlog keywords](doc/syntax/SYNTAX.markdown#mlog-keywords).
  * Support for passing mlog keywords as arguments to inline functions.
  * External and internal arrays (`@counter` arrays), including basic array optimizations.
  * Expressions in string interpolation: `print($"Sum: ${a + b}.")`.
  * Support for color literals: `%00ffff80`.
  * Prefix/postfix increment/decrement operators: `i++`, `--j`.
* Other functionality
  * Support for [symbolic labels and indentation in generated mlog code](/doc/syntax/SYNTAX-5-OTHER.markdown#option-symbolic-labels).
  * Using Mindustry Logic metadata corresponding to the target selected for compilation.
  * Profiling information for code executed using the built-in processor emulator.

See [changelog](CHANGELOG.markdown) for a comprehensive list of changes.

## Using Mindcode 

### Online

Mindcode is available at http://mindcode.herokuapp.com/. Write some Mindcode in the _Mindcode Source Code_ text area, then press the **Compile** button. The _Mindustry Logic_ text area will contain the mlog version of your Mindcode. Copy the mlog code into the clipboard. Back in Mindustry, edit your processor, then use the **Edit** button in the Logic UI. Select **Import from Clipboard**. Mindustry is now ready to execute your code.

You can also use the **Compile and Run** button to execute the compiled code right away on an emulated processor. The output produced by `print` instructions in your code will be displayed. Very limited interaction with the Mindustry World is supported.

> [!TIP]
> Mindcode performs a variety of different optimizations. The mlog code it produces may bear little resemblance to the source code at a first glance.

### Offline

Alternatively, you can download the command-line compiler and use Mindcode [from within an IDE](doc/syntax/TOOLS-IDE-INTEGRATION.markdown).

### Support mods

Mindcode can interface with the [Mlog Watcher mod](/doc/syntax/TOOLS-MLOG-WATCHER.markdown) to inject the compiled code into the selected processor in Mindustry World directly, avoiding the use of the clipboard.

The [Mlog Assertions mod](https://github.com/cardillan/MlogAssertions), available for Mindustry 7/8, allows efficient [array-bounds checking](/doc/syntax/SYNTAX-5-OTHER.markdown#option-boundary-checks) for both internal and external arrays, making this kind of bugs easier to detect. 

## Mindustry Logic References

To learn more about Mindustry Logic, you can find more information about it here:

* Yruei's [Mlog Documentation](https://yrueii.github.io/MlogDocs/) (last updated May 2025)

You can also get help on these Discord servers:

* [Logic channel on the Mindustry server](https://discord.gg/YkMMPMYABE)
* [Mindustry Logic server](https://discord.gg/WkFD3E8bXv)

## Contributing

See [CONTRIBUTING](CONTRIBUTING.markdown).

# License

MIT. See [LICENSE](LICENSE) for the full text of the license.
