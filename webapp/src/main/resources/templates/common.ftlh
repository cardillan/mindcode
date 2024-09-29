<#setting locale="en_US">

<#macro header>
<!DOCTYPE>
<html>
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
            <li><a href="/"            class="block text-center py-2 px-4 mx-2 rounded <#if selection == "mindcode"  >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Mindcode compiler</a>
            <li><a href="/schematics"  class="block text-center py-2 px-4 mx-2 rounded <#if selection == "schematics">font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Builder</a>
            <li><a href="/decompiler"  class="block text-center py-2 px-4 mx-2 rounded <#if selection == "decompiler">font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Decompiler</a>
        </ul>
    </nav>
</h1>
</#macro>

<#macro sample name>
<li><a href="?s=${name}" class="block text-center py-2 px-4 mx-2 underline rounded <#if model.sample == name>font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>"><#nested></a>
</#macro>

<#macro codeareas sourceTitle targetTitle sourceLines targetLines>
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
        <#list model.errors>
            <label class="mt-4 block font-bold">Syntax Errors:</label>
            <ul class="list-disc list-inside">
                <#items as message>
                    <li class="text-red-700">${message}</li>
                </#items>
            </ul>
        </#list>
    <#elseif model.hasMessages>
        <label class="mt-4 block font-bold">${messageTitle}</label>
        <#list model.warnings>
            <ul class="list-disc list-inside">
                <#items as message>
                    <li class="text-red-700">${message}</li>
                </#items>
            </ul>
        </#list>
        <#list model.messages>
            <ul class="list-disc list-inside">
                <#items as message>
                    <li>${message}</li>
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

<script>
    function copyToClipboard() {
        var textarea = document.getElementById("compiled");
        var text = textarea.value;

        navigator.clipboard.writeText(text).then(function() {
            // Change icon to checkmark
            var icon = document.getElementById("copyIcon");
            icon.classList.remove("fa-copy");
            icon.classList.add("fa-check");

            // Revert icon back to copy after 2 seconds
            setTimeout(function() {
                icon.classList.remove("fa-check");
                icon.classList.add("fa-copy");
            }, 2000);

        }).catch(function(err) {
            alert("Failed to copy text: " + err);
        });
    }
</script>

</body>
</html>
</#macro>
