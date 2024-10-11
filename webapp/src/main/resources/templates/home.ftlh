<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="mindcode"/>

<#if model.sample != "">
    <p class="my-2 text-lg">
        <strong>Mindcode</strong> is a high-level language that compiles down to Mindustry Logic - <strong>mlog</strong>.
        Mindcode includes higher-level constructs, such as expressions, user-defined functions and control statements.
        Here are some examples to give you an idea how to use Mindcode:
    <p>
    <nav class="my-4">
        <ul class="flex space-around">
            <@com.sample name="control-two-units"       >Controlling units</@com.sample>
            <@com.sample name="one-thorium"             >Controlling a block</@com.sample>
            <@com.sample name="many-thorium"            >Controlling more&nbsp;blocks</@com.sample>
            <@com.sample name="heal-damaged-building"   >Healing buildings</@com.sample>
            <@com.sample name="mining-drone"            >Mining drone</@com.sample>
            <@com.sample name="upgrade-conveyors"       >Building</@com.sample>
            <@com.sample name="sum-of-primes"           >Computing</@com.sample>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas mlogWatcher="Y" sourceTitle="Mindcode Source Code:" targetTitle="Mindustry Logic:" sourceLines="Y" targetLines="Y" />
            <div>
                <div class="flex-wrap items-center space-x-2">
                    <@com.optimizationSettings />
                    <input type="submit" value="Compile" class="px-4 py-2 bg-gray-600 text-white my-2 rounded"/>
                    <input type="submit" value="Compile and Run" class="px-4 py-2 bg-gray-600 text-white my-2 rounded" formaction="/compileandrun"/>
                    <@com.link href="/?s=clean">Start with a new script</@com.link>
                </div>
                <@com.footerLinks mindcode="Y" />
            </div>
            <div>
                <#if model.runOutput != "">
                    <label class="block font-bold" for="output">Program output (${model.runSteps} steps):</label>
                    <textarea rows="4" id="output" class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.runOutput}</textarea>
                </#if>

                <@com.compilerMessages messageTitle="Compiler messages:" />
            </div>
        </div>
    </form>
</div>

<@com.footer/>

<script>
    let socket = null;
    let nextIcon = "fa-microchip";

    // Button to send the message
    const mlogBtn = document.getElementById('mlogIcon');
    const message = document.getElementById('compiled');

    function busy() {
        var icon = document.getElementById("mlogIcon");
        icon.classList.remove("fa-microchip", "fa-check", "fa-xmark", "fa-plug-circle-xmark");
        icon.classList.add("fa-cog", "fa-spin");
    }

    function success() {
        var icon = document.getElementById("mlogIcon");
        icon.classList.remove("fa-cog", "fa-spin", "fa-microchip", "fa-xmark", "fa-plug-circle-xmark");
        icon.classList.add("fa-check");

        // Revert icon back to copy after 2 seconds
        setTimeout(function() {
            icon.classList.remove("fa-cog", "fa-spin", "fa-plug-circle-xmark", "fa-check", "fa-xmark");
            icon.classList.add("fa-microchip");
        }, 2000);
    }

    function failure() {
        var icon = document.getElementById("mlogIcon");
        icon.classList.remove("fa-cog", "fa-spin", "fa-microchip", "fa-check", "fa-plug-circle-xmark");
        icon.classList.add("fa-xmark");

        // Revert icon back to copy after a second
        setTimeout(function() {
            icon.classList.remove("fa-cog", "fa-spin", "fa-plug-circle-xmark", "fa-check", "fa-xmark");
            icon.classList.add("fa-microchip");
        }, 2000);
    }

    function unplugged() {
        var icon = document.getElementById("mlogIcon");
        icon.classList.remove("fa-cog", "fa-spin", "fa-microchip", "fa-check", "fa-xmark");
        icon.classList.add("fa-plug-circle-xmark");

        // Revert icon back to copy after a second
        setTimeout(function() {
            icon.classList.remove("fa-cog", "fa-spin", "fa-plug-circle-xmark", "fa-check", "fa-xmark");
            icon.classList.add("fa-microchip");
        }, 2000);
    }

    // When the button is clicked, open the WebSocket connection and send the message
    mlogBtn.onclick = function() {
        if (message.textContent !== '') {
            // Open the WebSocket connection only if it's not already open
            if (!socket || socket.readyState === WebSocket.CLOSED) {
                busy();
                socket = new WebSocket('ws://localhost:9992/');

                // WebSocket connection opened
                socket.onopen = function (event) {
                    console.log("Connection opened:", event.data);
                    socket.send(message.textContent);
                };

                // Message received from the WebSocket server
                socket.onmessage = function (event) {
                    console.log("Message from server:", event.data);
                    if (event.data === "ok") {
                        success();
                    } else {
                        unplugged();
                    }
                };

                // Handle any errors that occur
                socket.onerror = function (event) {
                    console.error("WebSocket error:", event);
                    failure();
                };

                // WebSocket connection closed
                socket.onclose = function (event) {
                    console.log("Connection closed:", event.data);
                };
            } else {
                // Send the message if the WebSocket connection is already open
                busy();
                console.log("Sending new message.");
                socket.send(message.textContent);
            }
        }
    };
</script>
