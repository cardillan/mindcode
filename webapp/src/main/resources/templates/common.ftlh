<#setting locale="en_US">

<#macro header>
<!DOCTYPE>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mindcode: a Mindustry Logic high-level language</title>
    <link href="https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="m-4">
</#macro>

<#macro h1>
<h1 class="my-2 text-2xl font-bold">
    <#nested>
</h1>
</#macro>

<#macro navigation selection>
<h1 class="my-2 text-2xl font-bold">
    <nav class="my-2">
        <ul class="flex space-around">
            <li><a href="/"           class="block text-center py-2 px-4 mx-2 <#if selection == "mindcode"  >font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Mindcode compiler</a>
            <li><a href="/schematics" class="block text-center py-2 px-4 mx-2 <#if selection == "schematics">font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Builder</a>
            <li><a href="/decompiler"  class="block text-center py-2 px-4 mx-2 <#if selection == "decompiler">font-bold bg-indigo-700 text-white<#else>bg-gray-200</#if>">Schematics Decompiler</a>
        </ul>
    </nav>
</h1>
</#macro>

<#macro link href title="">
<a class="underline text-blue-500" title="${title}" href="${href}"><#nested></a>
</#macro>

<#macro submit name="" value="" class="" title="">
<button type="submit" class="px-4 py-2 bg-gray-600 text-white my-4 ${class}" <#if title??>title="${title}"</#if> <#if name??>name="${name}"</#if> <#if value??>value="${value}"</#if>>
    <#nested>
</button>
</#macro>

<#macro footer>
<footer class="mt-8 text-xs text-center border border-t-2 border-l-0 border-r-0 border-b-0 border-gray-700">
    <div class="mt-4 flex justify-between">
        <div><strong>2023-05-23</strong></div>
        <div><strong>PRIVACY POLICY</strong>: This website does not track its users. The Mindcode and schematics you submit for compilation is kept for later analysis.
            No other information is kept about you or your actions on the site.</div>
        <div>Created by François (<a class="underline text-blue-500" href="https://github.com/francois">GitHub</a>,
                <a class="underline text-blue-500" href="https://twitter.com/fbeausoleil">Twitter</a>).</br>
                 Maintained by <a class="underline text-blue-500" href="https://github.com/cardillan">Cardillan</a>.</div>
    </div>
</footer>
</body>
</html>
</#macro>
