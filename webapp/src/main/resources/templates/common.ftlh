<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#setting locale="en_US">

<#macro header>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Mindcode: a Mindustry Logic high-level language</title>
    <link href="https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://kit.fontawesome.com/592e5a7a8a.js" crossorigin="anonymous"></script>
    <style>
        .textarea-container {
            position: relative;
            display: inline-block;
        }

        .copy-btn {
            position: absolute;
            top: 10px;
            right: 20px;
            width: 40px;
            height: 40px;
            background: gray;
            color: white;
            padding: 8px 12px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .copy-btn i {
            font-size: 1.2rem;
        }

        .mlog-btn {
            position: absolute;
            top: 10px;
            right: 70px;
            width: 40px;
            height: 40px;
            background: gray;
            color: white;
            padding: 8px 12px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.4rem;
        }

    </style>
</head>
<body class="m-4">
</#macro>

<#macro h1>
<h1 class="my-2 text-2xl font-bold">
    <#nested>
</h1>
</#macro>

<#macro navigation selection>
<h1 class="my-4 text-2xl font-bold">
    <nav class="my-2">
        <ul class="flex space-around">
            <li><a href="/"                 class="block text-center py-2 px-4 mx-2 rounded <#if selection == "mindcode"        >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Mindcode Compiler</a>
            <li><a href="/mlog-decompiler"  class="block text-center py-2 px-4 mx-2 rounded <#if selection == "mlog-decompiler" >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Mlog Decompiler</a>
            <li><a href="/schematics"       class="block text-center py-2 px-4 mx-2 rounded <#if selection == "schematics"      >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Builder</a>
            <li><a href="/decompiler"       class="block text-center py-2 px-4 mx-2 rounded <#if selection == "decompiler"      >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Decompiler</a>
        </ul>
    </nav>
</h1>
</#macro>

<#macro sample name>
<li><a href="?s=${name}" class="block text-center py-2 px-4 mx-2 underline rounded <#if model.sample == name>font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>"><#nested></a>
</#macro>

<#macro codeareas mlogWatcher sourceTitle targetTitle sourceLines targetLines>
    <label class="block font-bold" for="source">${sourceTitle}</label>
    <label class="block font-bold" for="target">${targetTitle}</label>
    <div>
        <textarea name="source" rows="24" id="source"   class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.source}</textarea>
        <#if sourceLines == "Y"><p class="text-center">${model.sourceLoc} lines of code</p></#if>
    </div>
    <div class="textarea-container">
        <textarea               rows="24" id="compiled" class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.compiled}</textarea>
        <#if targetLines == "Y"><p class="text-center">${model.compiledLoc} lines of code</p></#if>
        <button type="button" class="copy-btn bg-indigo-700" onclick="copyToClipboard()">
            <i id="copyIcon" class="fa-solid fa-copy"></i>
        </button>
        <#if mlogWatcher = "Y">
            <button type="button" class="mlog-btn bg-indigo-700">
                <i id="mlogIcon" class="fa-solid fa-microchip"></i>
            </button>
        </#if>
    </div>
</#macro>

<#macro link href title="">
<a title="${title}" href="${href}"><button type="button" class="px-4 bg-gray-600 text-white py-2 my-2 rounded"><#nested></button></a>
</#macro>

<#macro submit name="" value="" class="" title="">
<button type="submit" class="px-4 py-2 bg-gray-600 text-white my-4 ${class}" <#if title??>title="${title}"</#if> <#if name??>name="${name}"</#if> <#if value??>value="${value}"</#if>>
    <#nested>
</button>
</#macro>

<#macro optimizationSettings >
    <select aria-label="Optimization level" name="optimizationLevel" id="optimization" class="border-2 border-black p-2 my-2 rounded">
        <option value="OFF"          <#if model.optimizationLevel == "OFF"          >selected</#if>>No optimization</option>
        <option value="BASIC"        <#if model.optimizationLevel == "BASIC"        >selected</#if>>Basic optimization</option>
        <option value="ADVANCED"     <#if model.optimizationLevel == "ADVANCED"     >selected</#if>>Advanced optimization</option>
        <option value="EXPERIMENTAL" <#if model.optimizationLevel == "EXPERIMENTAL" >selected</#if>>Experimental optimization</option>
    </select>
</#macro>

<#macro compilerMessages messageTitle>
<div>
    <#if model.hasErrors>
        <label class="mt-4 block font-bold">Syntax Errors:</label>
        <ul class="list-disc list-inside">
            <#list model.errors>
                <#items as msg>
                    <li class="text-red-700">
                        <#if msg.hasPosition()>
                            <a class="underline" href="#" onclick="jumpToPosition(${msg.line}, ${msg.charPositionInLine}); return false;">${msg.position}</a>:
                        </#if>
                        ${msg.message}
                    </li>
                </#items>
            </#list>
        </ul>
        <div class="my-2">Errors were encountered. See
            <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/TROUBLESHOOTING.markdown">Mindcode Troubleshooting</a>
            for some tips, or <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/discussions/157">ask for help</a>.
        </div>
    <#elseif model.hasMessages>
        <label class="mt-4 block font-bold">${messageTitle}</label>
        <#list model.warnings>
            <ul class="list-disc list-inside">
                <#items as msg>
                    <li class="text-red-700">
                        <#if msg.hasPosition()>
                            <a class="underline" href="#" onclick="jumpToPosition(${msg.line}, ${msg.charPositionInLine}); return false;">${msg.position}</a>
                        </#if>
                        ${msg.message}
                    </li>
                </#items>
            </ul>
        </#list>
        <#list model.messages>
            <ul class="list-disc list-inside">
                <#items as msg>
                    <li>${msg.message}</li>
                </#items>
            </ul>
        </#list>
    </#if>
</div>
</#macro>

<#macro footerLinks mindcode>
<div style="margin-top:1cm;text-align:center;">
    <nav>
        <#if mindcode == "Y">
            <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYNTAX.markdown">Mindcode syntax</a>
        <#else>
            <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown">Schemacode syntax</a>
        </#if>
        | <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/README.markdown">Readme</a>
        | <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/CHANGELOG.markdown">Changelog</a>
    </nav>
    <p>Bug reports, suggestions and questions are welcome at the <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode">project page</a>.<p>
</div>
</#macro>

<#macro footer>
<script>
    function copyToClipboard() {
        try {
            var textarea = document.getElementById("compiled");
            textarea.focus();
            textarea.select();
            document.execCommand('copy');
            textarea.setSelectionRange(0, 0);

            // Change icon to checkmark
            var icon = document.getElementById("copyIcon");
            icon.classList.remove("fa-copy");
            icon.classList.add("fa-check");

            // Revert icon back to copy after 2 seconds
            setTimeout(function() {
                icon.classList.remove("fa-check");
                icon.classList.add("fa-copy");
            }, 2000);
        } catch(err) {
            alert("Failed to copy text: " + err);
        }
    }
</script>

<script>
    function jumpToPosition(line, char) {
        const textarea = document.getElementById('source');
        const lines = textarea.value.split('\n');

        // Ensure the specified line exists
        if (line > lines.length || line < 1) {
            alert('Line number out of range');
            return;
        }

        // Calculate the position by summing up the lengths of all preceding lines + chars in the target line
        let position = 0;
        for (let i = 0; i < line - 1; i++) {
            position += lines[i].length + 1; // +1 for the newline character
        }

        // Move the cursor to the calculated position
        textarea.setSelectionRange(0, 0);
        textarea.setSelectionRange(position, position + lines[line - 1].length);

        position += char - 1; // Char position within the line

        // Set focus on the textarea
        textarea.focus();


        // Now scroll the textarea to bring the cursor into view
        // Scroll to ensure the cursor is vertically centered
        const lineHeight = parseInt(window.getComputedStyle(textarea).lineHeight);
        const cursorY = (line / lines.length) * textarea.scrollHeight;
        const textareaHeight = textarea.clientHeight;

        // Scroll so that the cursor is in the middle
        textarea.scrollTop = cursorY - textareaHeight / 2 + lineHeight / 2;

        setTimeout(() => {
            // Deselect text after highlight (optional)
            textarea.setSelectionRange(position, position);
        }, 250);
    }
</script>

<footer class="mt-8 text-xs text-center border border-t-2 border-l-0 border-r-0 border-b-0 border-gray-700">
    <div class="mt-4 flex justify-between">
        <div><strong>${model.version}</strong></div>
        <div><strong>PRIVACY POLICY</strong>: This website does not track its users. The Mindcode and schematics you submit for compilation/decompilation is kept for later analysis.
            No other information is kept about you or your actions on the site.</div>
        <div>Created by François (<a class="underline text-blue-500" href="https://github.com/francois">GitHub</a>,
                <a class="underline text-blue-500" href="https://twitter.com/fbeausoleil">Twitter</a>).<br />
                 Maintained by <a class="underline text-blue-500" href="https://github.com/cardillan">Cardillan</a>.</div>
    </div>
</footer>

</body>
</html>
</#macro>
