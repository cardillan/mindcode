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
            <@com.sample name="control-two-units"       >Control two different units using variables</@com.sample>
            <@com.sample name="one-thorium"             >Prevent Thorium reactor blowup when out of cryo</@com.sample>
            <@com.sample name="heal-damaged-building"   >Patrol around a building and heal damaged ones</@com.sample>
            <@com.sample name="many-thorium"            >Prevent MULTIPLE Thorium reactors from blowing up when any runs out of cryo</@com.sample>
            <@com.sample name="mine-coord"              >Mine a specific coordinate and drop into a container</@com.sample>
            <@com.sample name="upgrade-conveyors"       >Upgrade copper conveyors to titanium, over a specific area</@com.sample>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas sourceTitle="Mindcode Source Code:" targetTitle="Mindustry Logic:" sourceLines="Y" targetLines="Y" />
            <div>
                <div class="flex items-center space-x-3">
                    <select aria-label="Optimization level" name="optimizationLevel" id="optimization" class="border-2 border-black p-2 rounded">
                        <option value="OFF"          <#if model.optimizationLevel == "OFF"       >selected</#if>>No optimization</option>
                        <option value="BASIC"        <#if model.optimizationLevel == "BASIC"     >selected</#if>>Basic optimization</option>
                        <option value="AGGRESSIVE"   <#if model.optimizationLevel == "AGGRESSIVE">selected</#if>>Advanced optimization</option>
                    </select>
                    <input type="submit" value="Compile" class="px-4 py-2 bg-gray-600 text-white my-4 rounded"/>
                    <@com.link href="/?s=clean">Start with a new script</@com.link>
                </div>
                <@com.footerLinks mindcode="Y" />
            </div>
            <@com.compilerMessages messageTitle="Compiler messages:" />
        </div>
    </form>
</div>

<@com.footer/>
