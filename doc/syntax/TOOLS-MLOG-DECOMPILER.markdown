# Mlog Decompiler

The Mlog Decompiler allows to **partially decompile** an existing mlog file into Mindcode. The resulting file cannot be compiled by Mindcode right away, but needs to be manually edited to create loops, conditional statements, functions and other high-level control structures that may be directly or indirectly present in the original mlog.

Variable names used in mlog are converted to Mindcode variables by removing leading `.` and `:` characters and replacing all remaining illegal characters with an underscore. If two mlog variables map to the same variable name after this conversion, a numeric index is used to distinguish them.

The decompiler is mainly useful to produce expressions and mlog function calls in the correct Mindcode syntax, saving some time and possibly helping to avoid some mistakes compared to a manual rewrite of the entire mlog code from scratch.

Mlog can be decompiled by both the [command line tool](TOOLS-CMDLINE.markdown) and the [web application](http://mindcode.herokuapp.com/mlog-decompiler).

---

[« Previous: Schematics Refresher](TOOLS-REFRESHER.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Testing framework »](TOOLS-TESTING-TOOL.markdown)
