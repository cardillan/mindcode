<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="decompiler"/>

<p class="my-2 text-lg">Here you can decompile schematics copied from Mindustry into Schemacode, a schema definition language,
    which can then be modified and compiled back into a schema again. Press the <strong>Export</strong> button on a Mindustry schematic,
    choose <strong>Copy to clipboard</strong> and paste the text into the left pane. Then press <strong>Decompile</strong>.
    You can compile Schematics definition back to Mindustry schematics on the <a href="/schematics">Schematics Builder</a> page.</p>
<p class="my-2 text-lg">If your schematics contains processor(s), the code is decompiled into mlog.</p>

<div class="w-full">
    <form action="/decompiler/decompile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <label class="block font-bold" for="source">Encoded schematics:</label>
            <label class="block font-bold">Decompiled schemacode:</label>
            <div>
                <textarea name="source" rows="24" id="source"   class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.source}</textarea>
            </div>
            <div>
                <textarea               rows="24" id="compiled" class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.compiled}</textarea>
                <p class="text-center">${model.compiledLoc} lines of code</p>
            </div>
            <div>
                <div>
                    <input type="submit" value="Decompile" class="px-4 py-2 bg-gray-600 text-white my-4"/>
                    <@com.link href="/decomnpiler/?s=clean">Erase schematics</@com.link>
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
                    <label class="mt-4 block font-bold">Decompiler messages:</label>
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
