<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.EditScriptData" -->
<#import "../common.ftlh" as com>
<@com.header/>

<@com.h1>New Script</@com.h1>

<form method="post" action="/scripts/${model.id}" class="w-full">
    <div class="grid grid-cols-2 gap-2">
        <div class="col-span-2">
            <label class="block font-bold" for="name">Script Name</label>
            <input type="text" class="block border border-gray-700 bg-gray-100 w-full p-1"
                   placeholder="Prevent one thorium reactor from exploding"
                   name="script[name]" value="${model.name}"/>
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
                <@com.submit name="action" value="compile">Compile</@com.submit>
                <@com.submit name="action" value="commit">Save and commit</@com.submit>
                or
                <@com.link href="/scripts">back to list</@com.link>
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

<h4 class="block font-bold">Version History</h4>
<ol class="list-inside list-decimal">
    <#list model.versionHistory as version>
        <li>
            Rollback to
            <@com.link href="#">
                <time datetime="${version.committedAt}" title="${version.committedAt}">N minutes old</time>
            </@com.link>
            version
        </li>
    </#list>
</ol>

<@com.footer/>
