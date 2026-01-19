<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="mindcode"/>

<#if model.sample != "">
    <p class="my-2 text-lg">
        <strong>Mindcode</strong> is a high-level language that compiles down to Mindustry Logic - <strong>mlog</strong>.
        Mindcode includes higher-level constructs, such as expressions, user-defined functions and control statements.
        Here are some examples to give you an idea how to use Mindcode:
    <p>
    <nav class="my-4">
        <ul class="flex space-around">
            <@com.sample name="control-multiple-units"  >Controlling units</@com.sample>
            <@com.sample name="one-thorium"             >Controlling a block</@com.sample>
            <@com.sample name="many-thorium"            >Controlling more&nbsp;blocks</@com.sample>
            <@com.sample name="heal-damaged-building"   >Healing buildings</@com.sample>
            <@com.sample name="mining-drone"            >Mining drone</@com.sample>
            <@com.sample name="upgrade-conveyors"       >Building</@com.sample>
            <@com.sample name="sum-of-primes"           >Computing</@com.sample>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas mlogWatcher="Y" sourceTitle="Mindcode Source Code:" targetTitle="Mindustry Logic:" sourceLines="Y" targetLines="Y" />
            <div>
                <div class="flex-wrap items-center space-x-2">
                    <@com.targetSettings />
                    <input type="submit" value="Compile" class="px-4 py-2 bg-gray-600 text-white my-2 rounded"/>
                    <input type="submit" value="Compile and Run" class="px-4 py-2 bg-gray-600 text-white my-2 rounded" formaction="/compileandrun"/>
                    <@com.link href="/?s=clean">Start with a new script</@com.link>
                </div>
                <@com.footerLinks mindcode="Y" />
            </div>
            <div>
                <#if model.runOutput != "">
                    <label class="block font-bold" for="output">Program output (${model.runSteps} steps):</label>
                    <textarea rows="4" id="output" class="block font-mono text-sm bg-gray-100 dark:bg-gray-700 w-full p-1">${model.runOutput}</textarea>
                </#if>

                <@com.compilerMessages messageTitle="Compiler messages:" />
            </div>
        </div>
    </form>
</div>

<@com.mlogwatcher/>

<@com.footer/>
