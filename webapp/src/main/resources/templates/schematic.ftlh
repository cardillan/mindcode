<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="schematics"/>

<p class="my-2 text-lg">Here you can compile a schematics definition written in Schemacode, a schema definition language,
 into text representation to be pasted into Mindustry using the <strong>Import schematics...</strong> button.
 If your schematics contain processors, you can specify code for the processor using either Mindustry Logic or <a href="/">Mindcode</a>.</p>
<p class="my-2 text-lg">You can also decompile an existing schematics using the <a href="/decompiler">Decompiler</a> and modify the resulting code.<p>
<#if model.sample != "">
    <p class="my-2 text-lg">Go ahead and try the following samples, then write your own!<p>
    <nav class="my-2">
        <ul class="flex space-around">
            <li><a href="?s=detector"               class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "detector"             >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Property detector</a>
            <li><a href="?s=healing-center"         class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "healing-center"       >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Healing center</a>
            <li><a href="?s=on-off-switch"          class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "on-off-switch"        >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">On/off switch</a>
            <li><a href="?s=regulator"              class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "regulator"            >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Regulator</a>
            <li><a href="?s=worker-recall-station"  class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "worker-recall-station">font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Unit Recall Station</a>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/schematics/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <label class="block font-bold" for="source">Schemacode definition:</label>
            <label class="block font-bold">Encoded schematics:</label>
            <div>
                <textarea name="source" rows="24" id="source"   class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.source}</textarea>
                <p class="text-center">${model.sourceLoc} lines of code</p>
            </div>
            <div>
                <textarea               rows="24" id="compiled" class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.compiled}</textarea>
            </div>
            <div>
                <div>
                    <input type="submit" value="Build" class="px-4 py-2 bg-gray-600 text-white my-4"/>
                    <@com.link href="/schematics/?s=clean">Start with a new schematics</@com.link>
                </div>
                <div style="margin-top:1cm;text-align:center;">
                    <nav>
                        <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown">Schemacode syntax</a>
                        | <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/README.markdown">Readme</a>
                        | <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/CHANGELOG.markdown">Changelog</a>
                    </nav>
                    <p>Bug reports, suggestions and questions are welcome at the <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode">project page</a>.<p>
                </div>
            </div>
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
                    <label class="mt-4 block font-bold">Build messages:</label>
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
        </div>
    </form>
</div>

<@com.footer/>
