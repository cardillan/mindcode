<div markdown="1" align="center">
   <img width="100%" src="wide-logo.png">

# Mindcode: a high-level compiler for mlog

<br>

[![Static Badge](https://img.shields.io/badge/web%20app-blue?link=http%3A%2F%2Fmindcode.herokuapp.com%2F)](http://mindcode.herokuapp.com/)
[![Static Badge](https://img.shields.io/badge/doc-mincdcode-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](doc/syntax/SYNTAX.markdown)
[![Static Badge](https://img.shields.io/badge/doc-mlog-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](https://yrueii.github.io/Mlog%20Documentation/)
[![GitHub issue custom search in repo](https://img.shields.io/github/issues-search/cardillan/mindcode?query=is%3Aopen%20label%3Abug&label=open%20bugs&color=red&link=https%3A%2F%2Fgithub.com%2Fcardillan%2Fmindcode%2Fissues%3Fq%3Dis%253Aissue%2520state%253Aopen%2520label%253Abug)](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug)
[![Static Badge](https://img.shields.io/badge/chat-discord-blue?link=https%3A%2F%2Fdiscord.gg%2FA8vdVdvf)](https://discord.gg/A8vdVdvf)

</div>
<br>

**Mindcode** is a high-level programming language for [Mindustry Logic](https://github.com/Anuken/Mindustry). Many language features are provided, including variable declaration, arrays (including `@counter` arrays), conditional statements and loops, functions, modules, remote function calls, system library etc. Mindcode generates fairly well optimized mlog code, utilizing available instruction space to make the resulting code faster. It comes with a [web app](http://mindcode.herokuapp.com/) and a [command-line compiler](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#setting-up-the-command-line-compiler), provides means for integration both with various IDEs and Mindustry itself.

**Schemacode**, an extension built over Mindcode, is a specialized definition language designed for creating a complete Mindustry schematic from a text file. [Schematics builder](doc/syntax/SCHEMACODE.markdown) compiles these definition files directly into Mindustry schematics, either into binary `.msch` file, or into the text representation. Processors can be included in these schematics, complete with the code (specified either in Mindcode or mlog) and linked blocks.

## Mindcode Syntax

Please refer to the [documentation](doc/syntax/SYNTAX.markdown) for a complete description of Mindcode syntax. You can also use the code examples in the web application to get familiar with Mindcode.  

You can also visit [golem](https://github.com/cardillan/golem), a collection of more complex Mindcode and Schemacode scripts.

## Latest development

See [issues](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug) for open bugs and possible workarounds. 

> [!NOTE]
> Deprecated functionalities from Mindcode 2.x were removed. The system for specifying [language version targets](/doc/syntax/SYNTAX-5-OTHER.markdown#option-target) has been changed.

The most important recent changes to Mindcode include:

* Language features
  * Support for [symbolic labels in generated mlog code](/doc/syntax/SYNTAX-5-OTHER.markdown#option-symbolic-labels).
  * Specific syntax for [mlog keywords](doc/syntax/SYNTAX.markdown#mlog-keywords).
  * Support for passing mlog keywords as arguments to inline functions.
  * External and internal arrays (`@counter` arrays).
  * Expressions in string interpolation: `print($"Sum: ${a + b}.")`.
  * Support for color literals: `%00ffff80`.
  * Prefix/postfix increment/decrement operators: `i++`, `--j`.
  * Redefined [strict and relaxed syntax modes](/doc/syntax/SYNTAX.markdown#strict-syntax).
  * Variable declaration (optional in relaxed syntax, compulsory in strict syntax).
* Basic functionality
  * Significant improvement in reporting error messages generated during compilation.
  * A built-in [library of system functions](doc/syntax/SYSTEM-LIBRARY.markdown).
  * Ability to inject the mlog code to Mindustry processors via the [Mlog Watcher mod](doc/syntax/TOOLS-MLOG-WATCHER.markdown).
  * Support for running the compiled mlog code in an emulated processor.
  * Tool for [partial decompilation](doc/syntax/TOOLS-MLOG-DECOMPILER.markdown) of an existing mlog code into Mindcode.
* Mindustry Logic 8
  * Complete support for the Mindustry Logic in the latest [Mindustry BE version](/doc/syntax/MINDUSTRY-8.markdown).
  * Full support for [remote functions and variables](doc/syntax/REMOTE-CALLS.markdown).
  * New `printchar` instruction and character literals.

See [changelog](CHANGELOG.markdown) for a comprehensive list of changes.

## Using Mindcode 

### Online

Mindcode is available at http://mindcode.herokuapp.com/. Write some Mindcode in the _Mindcode Source Code_ text area, then press the **Compile** button. The _Mindustry Logic_ text area will contain the mlog version of your Mindcode. Copy the mlog code into the clipboard. Back in Mindustry, edit your processor, then use the **Edit** button in the Logic UI. Select **Import from Clipboard**. Mindustry is now ready to execute your code.

You can also use the **Compile and Run** button to execute the compiled code right away on an emulated processor. The output produced by `print` instructions in your code will be displayed. Very limited interaction with the Mindustry World is supported.

> [!TIP]
> Mindcode performs a variety of different optimizations. The mlog code it produces may bear little resemblance to the original source code at a first glance.

### Offline

Alternatively, you can download the command-line compiler and use Mindcode [from within an IDE](doc/syntax/TOOLS-IDE-INTEGRATION.markdown).

### Support mods

Mindcode can interface with the [Mlog Watcher mod](/doc/syntax/TOOLS-MLOG-WATCHER.markdown) to inject the compiled code into selected processor in Mindustry World directly, witthout having to use a clipboard.

The [Mlog Assertions mod](https://github.com/cardillan/MlogAssertions), available for Mindustry 7, allows efficient [array-bounds checking](/doc/syntax/SYNTAX-5-OTHER.markdown#option-boundary-checks) for both internal and external arrays, making this kind of bugs easier to detect. 

## Mindustry Logic References

If you don't know much about Mindustry Logic, you can read more information about it here:

* Yruei's [Mlog Documentation](https://yrueii.github.io/MlogDocs/) (last updated Feb 2025)

You can also get help on these Discord servers:

* [Logic channel on the Mindustry server](https://discord.gg/YkMMPMYABE)
* [Mindustry Logic server](https://discord.gg/TbkC728p)

## Roadmap

An overview of planned new functionalities is available [here](https://github.com/cardillan/mindcode/discussions/142).

Actual progress of development is published in the [`#development` channel on Discord](https://discord.gg/wnWYC2BjAU).

Comments and ideas are welcome at both places.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.markdown).

# License

MIT. See [LICENSE](LICENSE) for the full text of the license.
