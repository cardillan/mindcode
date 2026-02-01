# Mlog Watcher

Mlog Watcher is a Mindustry mod by [Sharlottes](https://github.com/Sharlottes) which can be found on [GitHub](https://github.com/Sharlottes/MlogWatcher). Thanks to Mlog Watcher, it is possible to automatically send mlog code or shcematic directly to the running game, and even extract mlog code or schematics from the game for processing. This is a much easier and vastly superior way compared to transferring the code via clipboard.

Both the web app and the command-line tool can interact with the Mlog Watcher.

# Installing Mlog Watcher

You can install the mod directly from Mindustry using the mod browser:

- Click on **Mods** in Mindustry's main menu.
- Click on the **Mod Browser** button.
- Type **MlogWatcher** into the search box.
- Click on the **MlogWatcher** mod in the list.
- Click on the **Install** button at the bottom of the screen.

After installing the mod, the game will exit so that the newly installed mod can be loaded on the next start. Just start the game again. 

# Selecting a processor

Mlog Watcher injects the received code into a processor which is selected. 

A processor becomes selected by tapping or clicking on it. Mlog Watcher indicates the selected status by drawing a diamond over/around the processor. Tapping the processor again hides the highlights over the blocks that are linked to the processor but keeps the processor selected to Mlog Watcher. This is useful when your code produces a graphical output to a display, as the display is unobstructed by the link highlights.

Tapping or clicking away from the processor unselects the processor from Mlog Watcher.

# Mindcode web application

The panel with the compiled mlog code contains two buttons in the upper right corner. The left button sends the contents of the pane into a Mindustry game running on your computer. If there's the Mlog Watcher mod running within the game, it will accept the code and a checkmark will be displayed briefly. Mlog Watcher then injects the received code into the bound processor. If there's no processor bound to Mlog Watcher in the Mindustry game, or a map isn't loaded but Mindustry is running, a disconnected plug will be displayed instead.

When Mindcode doesn't succeed connecting to Mlog Watcher, a cross is displayed briefly on the button to indicate the failure.

At this moment, the web app only supports Mlog Watcher using port `9992`. This is the default setting for Mlog Watcher.

# Command-line tool

## Integration through the local file system

The File Watcher monitors a specific directory for changes to files containing mlog code or schematic definitions. This directory needs to be set up on the mod's settings first.

### Mlog code

Mlog code must be stored in files having the `.mlog` extension. When such a file is created or updated in the watched directory, its content is loaded and is injected to the last selected processor in the currently running game; the update is indicated visually on the processor. If no such processor exists, nothing happens.

Use command-lie arguments, such as `--output-directory`, to let the command-line toor create mlog files in the watched directory.

### Schematics

Schematics need to be stored in binary format in files having the `.msch` extension. When such a file is created or updated in the watched directory and contains a valid schematic, the schematic is loaded, and `MlogWatcher` tag is added to the schematic, and the schematic is stored in the library. If a schematic with an identical name containing the `MlogWatcher` tag already exists in the library, it is replaced by the imported schematic. Otherwise, the imported schematic is added to the library; if a schematic with the same name already exists, the new one is placed alongside the existing one.

Schematics are updated regardless of the state of the game. When the game is running, only a message is briefly shown in the game. When the game is paused or not active at all, the imported schematic is shown on screen.

Use command-lie arguments, such as `--output-directory`, to let the command-line toor create schematic files in the watched directory.

## Integration through the WebSocket API

Mlog Watcher runs a WebSocket server on your machine on port `9992` (the port number can be changed in settings). Mindcode compiler can perform various operations through this interface via the `-w` command-line option (for exmaple, `-w update-all`). Possible operations are:

* `update`: send the created or processed mlog code or schematic to the Mlog Watcher mod. For mlog code, the code is injected into the selected processor; when no processor is selected, an error message is displayed. For schematics, the schematic is stored in the library; storing a schematic in the library should never fail and happens regardless of the state of the game (menu, paused, running). If a schematic with the same name that was also added via Mlog Watcher already exists, it will be overwritten by the new schematic, otherwise it is added to the library.
* `update-all`: sends the compiled mlog code into Mlog Watcher, which then injects the received code into all processors on the map with a matching [program ID](#program-id). Only processors having the exact same version number in the program ID are processed.  
* `upgrade-all`: sends the compiled mlog code into Mlog Watcher, which then injects the received code into all processors on the map with a matching [program ID](#program-id). Only processors having equal or lower version number in the program ID are processed. 
* `force-update-all`: sends the compiled mlog code into Mlog Watcher, which then injects the received code into all processors on the map with a matching [program ID](#program-id), regardless of the actual verison number. This operation can be used to downgrade the program stored in the processors on the map.
* `add`: adds a schematic to the library, even if a Mlog Watcher managed schematic with the same name already exists in the library.
* `extract`: extracts the mlog code from the selected processor (when used with the `pm` action) or schematic currently displayed on screen (when used with the `ps` action). Note that extracting schematics from the library may fail when the Schematics screen was entered from the main menu; enter the Schematics screen from a running game to successfully extract a schematic from the library.
 
Note that `update-all`, `upgrade-all` and `force-update-all` operations can't be used with the `pm` (process mlog) action, as the program ID is only created when compiling programs from Mindcode. Also, when no map is loaded, no processors are updated and an error message is displayed.

### Program ID

### Interface parameters

If you need to run the communication on a different port, you need to change the port number in Mindustry Mlog Watcher settings, and provide the new port number to Mindcode compiler using the `--watcher-port` command-line option. Additionally, there's a 500-millisecond timeout interval when trying to connect to Mlog Watcher, which can be also changed through the `--watcher-timeout` command--line option.

When the communication with Mlog Watcher fails, Mindcode compiler writes out an error message but produces all other outputs as usual.

---

[&#xAB; Previous: IDE Integration](TOOLS-IDE-INTEGRATION.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Processor emulator &#xBB;](TOOLS-PROCESSOR-EMULATOR.markdown)
