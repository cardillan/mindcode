<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="mindcode"/>

<p class="my-2 text-lg"><a href="https://mindustrygame.github.io/">Mindustry</a> is a tower-defense type of game, now with added Logic.
 Logic is a programming language, but it is very low-level. It is more akin to assembly than high-level language.</p>
<p class="my-2 text-lg"><strong>Mindcode</strong> is a high-level language that compiles down to Mindustry Logic.
 Mindcode includes higher-level constructs, such as expressions, user-defined functions, control statements, or "direct" memory access.<p>
<#if model.sample != "">
    <p class="my-2 text-lg">Mindcode now comes with <strong>significantly improved code generation</strong>, including <strong>data flow analysis</strong>, <strong>loop unrolling</strong> and  <strong>function inlining</strong>;
     for a list of recent changes, see <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/CHANGELOG.markdown">changelog</a>.<p>
    <p class="my-2 text-lg">Go ahead and try the following samples, then write your own!<p>
    <nav class="my-2">
        <ul class="flex space-around">
            <li><a href="?s=bind-single-unit"       class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "bind-single-unit"     >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Bind a single poly and move it around</a>
            <li><a href="?s=one-thorium"            class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "one-thorium"          >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Prevent Thorium reactor blowup when out of cryo</a>
            <li><a href="?s=heal-damaged-building"  class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "heal-damaged-building">font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Patrol around a building and heal damaged ones</a>
            <li><a href="?s=many-thorium"           class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "many-thorium"         >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Prevent MULTIPLE Thorium reactors from blowing up when any runs out of cryo</a>
            <li><a href="?s=mine-coord"             class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "mine-coord"           >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Mine a specific coordinate and drop into a container</a>
            <li><a href="?s=upgrade-conveyors"      class="block text-center py-2 px-4 mx-2 underline <#if model.sample == "upgrade-conveyors"    >font-bold bg-indigo-700 text-white<#else>bg-gray-200 </#if>">Upgrade copper conveyors to titanium, over a specific area</a>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <label class="block font-bold" for="source">Mindcode Source Code:</label>
            <label class="block font-bold">Mindustry Logic:</label>
            <div>
                <textarea name="source" rows="24" id="source"   class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.source}</textarea>
                <p class="text-center">${model.sourceLoc} lines of code</p>
            </div>
            <div>
                <textarea               rows="24" id="compiled" class="block font-mono text-sm border border-1 border-gray-700 bg-gray-100 w-full p-1">${model.compiled}</textarea>
                <p class="text-center">${model.compiledLoc} lines of code</p>
            </div>
            <div>
                <div>
                    <input type="submit" value="Compile" class="px-4 py-2 bg-gray-600 text-white my-4"/>
                    <@com.link href="/?s=clean">Start with a new script</@com.link>
                    <label>
                        <#if model.enableOptimization>
                            <input type="checkbox" name="enableOptimization" checked>
                        <#else>
                            <input type="checkbox" name="enableOptimization">
                        </#if>
                        Enable optimization
                    </label>
                </div>
                <div style="margin-top:1cm;text-align:center;">
                    <nav>
                        <a class="underline text-blue-500" href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYNTAX.markdown">Mindcode syntax</a>
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
                    <label class="mt-4 block font-bold">Compiler messages:</label>
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
