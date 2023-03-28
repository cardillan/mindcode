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
    <div class="mt-8 flex justify-between">
        <p class="mr-4 flex-grow"><b class="font-bold">PRIVACY POLICY</b>: This website does not track its users. The Mindcode you submit for compilation is kept for later analysis. No other information is kept about you or your actions on the site.</p>
        <p class="<#if model.loggedIn>mx-4<#else>ml-4</#if>flex-grow">My name is François and I build web applications for a living. You can find me on <a class="underline text-blue-500" href="https://github.com/francois">GitHub</a> and on <a class="underline text-blue-500" href="https://twitter.com/fbeausoleil">Twitter</a>.</p>
        <#if model.loggedIn>
            <div class="ml-4 flex-shrink">
                <form method="post" action="/logout">
                    <button type="submit" class="px-4 py-2 bg-indigo-600 text-white my-4">Logout</button>
                </form>
            </div>
        </#if>
    </div>
</footer>
</body>
</html>
</#macro>
