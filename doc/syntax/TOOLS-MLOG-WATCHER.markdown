# Mlog Watcher

Mlog Watcher is a Mindustry mod by [Sharlottes](https://github.com/Sharlottes) which can be found on [GitHub](https://github.com/Sharlottes/MlogWatcher). Thanks to Mlog Watcher it is possible to automatically inject a compiled mlog code into a processor in Mindustry, in a much easier and vastly superior way compared to transferring the code via clipboard.

Both the web app and the command-line tool can interact with the Mlog Watcher.

## Installing Mlog Watcher

You can install the mod directly from Mindustry using the mod browser:

- Click on **Mods** in Mindustry's main menu.
- Click on the **Mod Browser** button.
- Type **MlogWatcher** into the search box.
- Click on the **MlogWatcher** mod in the list.
- Click on the **Install** button at the bottom of the screen.

After installing the mod, the game will exit so that the newly installed mod can be loaded on the next start. Just start the game again. 

## Binding a processor

Mlog Watcher injects the received code into a processor which is bound to it. 

When you select a processor by tapping or clicking on it, it becomes bound to Mlog Watcher as well. Mlog Watcher indicates the selected status by drawing a diamond over/around the processor. Tapping the processor again makes it unselected, hiding the highlights over the blocks that are linked to the processor, but keeps the processor bound to Mlog Watcher. This is useful when your code produces a graphical output to a display, as the display is unobstructed by the link highlights.

Tapping or clicking away from the processor unbinds the processor from Mlog Watcher, which will no longer forward the received code to any processor.

## Mindcode web application

The panel with the compiled mlog code contains two buttons in the upper right corner. The left button sends the contents of the pane into a Mindustry game running on your computer. If there's the Mlog Watcher mod running within the game, it will accept the code and a checkmark will be displayed briefly. Mlog Watcher then injects the received code into the bound processor. If there's no processor bound to Mlog Watcher in Mindustry game, or a map isn't loaded but Mindustry is running, a disconnected plug will be displayed instead.

When Mindcode doesn't succeed connecting to Mlog Watcher, a cross is displayed briefly on the button to indicate the failure.

At this moment, the web app only supports Mlog Watcher using port `9992`. This is the default setting for Mlog Watcher.

## Command-line tool

### Updating processor code through local file

When Mlog Watcher is installed, you can go to Mlog Watcher settings in Mindustry and select a file on your filesystem. Whenever the contents of this file changes, Mlog Watcher loads it and injects it into the currently selected processor in opened Mindustry map. When no processor is selected, the update doesn't happen.

When compiling a file using the Mindcode compiler, make sure the mlog code gets written to the file selected in Mlog Watcher by specifying it on the command line. If the compilation is successful, the resulting mlog code written to the file will be processed by Mlog Watcher.

### Updating processor code through WebSocket

Mlog Watcher runs a WebSocket server on your machine on port `9992` (the port number can be changed in settings). Mindcode compiler can be configured, using the `-w` command-line option, to contact Mlog Watcher on this port and send the compiled code there. Mlog Watcher then injects the received code into the selected processor. This avoids the need to write the compiled code into a single specific file and bypasses the file system entirely.

If you need to run the communication on a different port, you need to change the port number in Mindustry Mlog Watcher settings, and provide the new port number to Mindcode compiler using the `--watcher-port` command-line option. Additionally, there's a 500 millisecond timeout interval when trying to connect to Mlog Watcher, which can be also changed through the `--watcher-timeout` command--line option.

When the communication with Mlog Watcher fails, Mindcode compiler writes out an error message, but produces all other outputs as usual.

---

[« Previous: Processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schematics Refresher »](TOOLS-REFRESHER.markdown)
