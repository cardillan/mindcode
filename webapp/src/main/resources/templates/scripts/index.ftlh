<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.ListScriptData" -->
<#import "../common.ftlh" as com>
<@com.header/>

<@com.h1>My Scripts</@com.h1>

<form method="get" action="/scripts" class="flex justify-end items-baseline">
    <label for="q" class="sr-only">Search for</label>
    <input type="search" id="q" name="q" class="p-2 bg-gray-200 border border-gray-700 flex-initial w-48" value="${model.query}"/>
    <@com.submit class="flex-grow-0">Search</@com.submit>
</form>
<table class="w-full">
    <thead>
        <tr>
            <th class="text-left border-b border-gray-500">Name</th>
            <th class="text-left border-b border-gray-500">Recorded At</th>
        </tr>
    </thead>
    <tbody>
        <#list model.scripts as script>
            <tr>
                <td class="text-left p-1">
                    <@com.link href="/scripts/${script.id}/edit">
                        ${script.name}
                    </@com.link>
                </td>
                <td class="text-left p-1">${script.recordedAt}</td>
            </tr>
        <#else>
            <tr>
                <td colspan="2">
                    <#if model.query == "">
                        You have no scripts.
                        <@com.link href="/scripts/new">Write a script</@com.link>
                    <#else>
                        No scripts found.
                    </#if>
                </td>
            </tr>
        </#list>
    </tbody>
</table>

<p class="mt-4">
    <@com.link href="/scripts/new">Write a new script</@com.link>
    <#if model.query != "">
        <@com.link href="/scripts">Show all scripts</@com.link>
    </#if>
</p>

<@com.footer/>
