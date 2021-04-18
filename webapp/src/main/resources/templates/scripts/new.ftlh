<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.NewScriptData" -->
<#import "../common.ftlh" as com>
<@com.header/>

<@com.h1>New Script</@com.h1>

<form method="post" action="/scripts" class="w-full">
    <div class="grid grid-cols-2 gap-2">
        <div class="col-span-2">
            <label class="block font-bold" for="name">Script Name</label>
            <input type="text" name="script[name]" class="block border border-gray-700 bg-gray-100 w-full p-1" placeholder="Prevent one thorium reactor from exploding"/>
        </div>
        <label class="block font-bold" for="source">Mindcode Source Code:</label>
        <label class="block font-bold">Mindustry Logic:</label>
        <div>
            <textarea name="script[source]" rows="24" id="source"
                      class="block font-mono text-sm border border-gray-700 bg-gray-100 w-full p-1">${model.source}</textarea>
            <p class="text-center">${model.sourceLoc} lines of code</p>
        </div>
        <div>
            <textarea rows="24" id="compiled"
                      class="block font-mono text-sm border border-gray-700 bg-gray-100 w-full p-1">${model.compiled}</textarea>
            <p class="text-center">${model.compiledLoc} lines of code</p>
        </div>
        <div>
            <p>
                <button type="submit" class="px-4 py-2 bg-gray-600 text-white my-4">Compile</button>
                or
                <@com.link href="/scripts">cancel</@com.link>
            </p>
        </div>
        <div>
            <#list model.syntaxErrors>
                <label class="mt-4 block font-bold">Syntax Errors:</label>
                <ul class="list-disc list-inside">
                    <#items as error>
                        <li class="text-red-700">${error}</li>
                    </#items>
                </ul>
            </#list>
        </div>
    </div>
</form>

<@com.footer/>
