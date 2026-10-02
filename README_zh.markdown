<div markdown="1" align="center">
   <img width="100%" src="wide-logo.png">

# Mindcode：Mlog 的高级编译器

<br>

[![Static Badge](https://img.shields.io/badge/web%20app-blue?link=https%3A%2F%2Fmindcode.herokuapp.com%2F)](https://mindcode.herokuapp.com/)
[![Static Badge](https://img.shields.io/badge/doc-mincdcode-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](doc/syntax/SYNTAX.markdown)
[![Static Badge](https://img.shields.io/badge/doc-mlog-green?link=doc%2Fsyntax%2FSYNTAX.markdown)](https://yrueii.github.io/Mlog%20Documentation/)
[![GitHub issue custom search in repo](https://img.shields.io/github/issues-search/cardillan/mindcode?query=is%3Aopen%20label%3Abug&label=open%20bugs&color=red&link=https%3A%2F%2Fgithub.com%2Fcardillan%2Fmindcode%2Fissues%3Fq%3Dis%253Aissue%2520state%253Aopen%2520label%253Abug)](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug)
[![Static Badge](https://img.shields.io/badge/chat-discord-blue?link=https%3A%2F%2Fdiscord.gg%2FA8vdVdvf)](https://discord.gg/q4eGwpu2mp)

</div>
<br>

**Mindcode** 是一种用于 [Mindustry Logic](https://github.com/Anuken/Mindustry) 的高级过程式编程语言。它提供了许多语言功能，包括变量声明、内部和外部数组、条件语句和循环、函数、模块、远程变量和函数调用（同步和异步）、原子代码执行、系统库、用户库、调试支持等。Mindcode 生成相当优化的 mlog 代码，利用可用指令空间使生成的代码更快。它附带一个[网页应用](https://mindcode.herokuapp.com/)和一个[命令行编译器](doc/syntax/TOOLS-IDE-INTEGRATION.markdown#setting-up-the-command-line-compiler)，并提供了与各种 IDE 以及 Mindustry 本身集成的方式。

**Schemacode** 是构建在 Mindcode 之上的扩展，是一种专用定义语言，旨在从文本文件创建完整的 Mindustry 蓝图。[Schematics Builder](doc/syntax/SCHEMACODE.markdown) 将这些定义文件直接编译为 Mindustry 蓝图，可以是二进制 `.msch` 文件，也可以是文本表示。处理器可以包含在这些蓝图中，并带有代码（用 Mindcode 或 mlog 指定）和链接的方块。

有关更详细的功能列表，请参见 [Mindcode 功能](FEATURES.markdown)。

## 支持的 Mindustry 版本

Mindcode 可以为所有主要 Mindustry 版本和不同类型的处理器生成代码。可以在网页应用中使用组合框选择目标，或在源代码中包含 `#set target` 指令。查看[支持的目标及其对应的 Mindustry 版本列表](/doc/syntax/SYNTAX-5-OTHER.markdown#option-target)。

当前，默认目标是 `8.2m`，对应最新官方 Mindustry 8 版本（**v8 Build 160.1**）。

## Mindcode 语法

请参阅[文档](doc/syntax/SYNTAX.markdown)以获取 Mindcode 语法的完整描述。你也可以使用网页应用中的代码示例来熟悉 Mindcode。

此外，以下仓库包含 Mindcode 项目，也可作为如何使用 Mindcode 的示例：

* 由作者维护
  * [golem](https://github.com/cardillan/golem)，一个更复杂的 Mindcode 和 Schemacode 脚本集合。
* 由其他用户维护
  * [Mindustry Mindcode Projects](https://github.com/50275/mindustry-mindcode-projects)
  * [Mindustry logic scripts](https://github.com/dadymax/mindustrylogicscripts)

## 最新开发

查看 [issues](https://github.com/cardillan/mindcode/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug) 以了解未解决的 bug 和可能的解决方法。

Mindcode 最近最重要的变更包括：

* Mindustry 8 特定功能
  * 完整支持[最新的 Mindustry 8 版本（v8 Build 160.1）](/doc/syntax/MINDUSTRY-8.markdown)。
  * [原子代码段](doc/syntax/REMOTE-CALLS.markdown#atomic-code-execution)，保证以原子方式执行——不中断。
  * 完全支持[远程函数和变量](doc/syntax/REMOTE-CALLS.markdown)。
  * 使用 Mindustry 8 逻辑能力的[数组实现](/doc/syntax/optimizations/ARRAY-OPTIMIZATION.markdown)。
  * 新的基于字符串/字符的指令和字符字面量。
* 语言/编译器功能
  * 支持字符串字面量中的转义字符，包括 Unicode 转义序列。
  * 支持在局部作用域中声明数组。
  * 递归函数内联和尾调用优化。
  * 增强的 Schemacode 支持（方块区域/方块数组、符号处理器链接名称）。
  * 布尔表达式的短路求值。
  * 改进的[循环旋转](doc/syntax/optimizations/LOOP-ROTATION.markdown)和[循环提升](doc/syntax/optimizations/LOOP-HOISTING.markdown)优化。
  * 用于测试范围或列表成员资格的 [`in` 运算符](doc/syntax/SYNTAX-2-EXPRESSIONS.markdown#rangelist-membership-operator)。
* 其他功能
  * 通过 Mlog Watcher 模组将构建的蓝图直接从网页应用发送到 Mindustry。
  * 移动端友好的[网页应用](https://mindcode.herokuapp.com/)，带语法高亮。
  * 用于所有支持的 Mindcode 目标的[处理器模拟器](doc/syntax/TOOLS-PROCESSOR-EMULATOR.markdown)，包括蓝图模拟。

查看[更新日志](CHANGELOG.markdown)以获取完整变更列表。

## 使用 Mindcode

### 在线

Mindcode 可在 https://mindcode.herokuapp.com/ 获取。在 _Mindcode Source Code_ 文本区域编写一些 Mindcode，然后按 **Compile** 按钮。_Mlog Code_ 文本区域将包含你的 Mindcode 的 mlog 版本。将 mlog 代码复制到剪贴板。回到 Mindustry，编辑你的处理器，然后在 Logic UI 中使用 **Edit** 按钮。选择 **Import from Clipboard**。Mindustry 现在已准备好执行你的代码。

你也可以使用 **Compile and Run** 按钮立即在模拟处理器上执行编译后的代码。代码中 `print` 指令产生的输出将显示出来。支持与模拟 Mindustry 世界进行非常有限的交互。

> [!TIP]
> Mindcode 执行各种不同的优化。它生成的 mlog 代码乍看之下可能与源代码几乎没有相似之处。

### 离线

或者，你可以下载命令行编译器，并在 IDE 中[使用 Mindcode](doc/syntax/TOOLS-IDE-INTEGRATION.markdown)。

### 支持模组

Mindcode 可以与 [Mlog Watcher 模组](/doc/syntax/TOOLS-MLOG-WATCHER.markdown)交互，将编译后的代码直接注入 Mindustry 世界中选定的处理器，避免使用剪贴板。在线和离线版本的 Mindcode 都支持此功能。

适用于 Mindustry 7/8 的 [Mlog Assertions 模组](https://github.com/cardillan/MlogAssertions)，允许对内部和外部数组进行高效的[数组边界检查](/doc/syntax/SYNTAX-5-OTHER.markdown#option-error-reporting)，使这类 bug 更容易检测。

## Mindustry 逻辑参考资料

要了解有关 Mindustry 逻辑的更多信息，可以在此处找到更多相关信息：

* Yruei 的 [Mlog 文档](https://yrueii.github.io/MlogDocs/)（最后更新于 2026 年 9 月）

你也可以在这些 Discord 服务器上获得帮助：

* [Mindustry 服务器上的 Logic 频道](https://discord.gg/YkMMPMYABE)
* [Mindustry Logic 服务器](https://discord.gg/WkFD3E8bXv)

## 贡献

如果你有兴趣帮助这个项目，请参阅 [CONTRIBUTING](CONTRIBUTING.markdown)。

Mindcode 最初由 [François Beausoleil](https://github.com/francois) 创建，自 2023 年以来由 [cardillan](https://github.com/cardillan) 维护。新的前端由 [JeanJPNM](https://github.com/JeanJPNM) 开发。

其他贡献者请参见 [Contributors (GitHub)](https://github.com/cardillan/mindcode/graphs/contributors)。

# 许可证

MIT。完整许可证文本请参见 [LICENSE](LICENSE)。
