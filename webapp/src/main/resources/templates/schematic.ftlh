<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="schematics"/>

<#if model.sample != "">
    <p class="my-2 text-lg">
        Here you can compile a schematics definition written in <a href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown" class="text-indigo-1000">Schemacode</a>, a schema definition language,
        into a text representation to be pasted into Mindustry using the <strong>Import schematics...</strong> button.
        If your schematics contain processors, you can specify code for the processor using either Mindustry Logic or Mindcode.
        You can also decompile an existing schematics using the <a href="/decompiler">Decompiler</a> and modify the resulting code.
    </p>
    <p class="my-2 text-lg">
        Here are some examples to give you an idea how to use Schemacode:
    <p>
    <nav class="my-4">
        <ul class="flex space-around">
            <@com.sample name="detector"                >Property Detector</@com.sample>
            <@com.sample name="healing-center"          >Healing Center</@com.sample>
            <@com.sample name="on-off-switch"           >On/off Switch</@com.sample>
            <@com.sample name="regulator"               >Regulator</@com.sample>
            <@com.sample name="overdrive-dome-supply"   >Supplying Overdrive Dome</@com.sample>
            <@com.sample name="worker-recall-station"   >Unit Recall Station</@com.sample>
            <@com.sample name="scrap-to-metaglass-2"    >Scrap to Metaglass</@com.sample>
            <@com.sample name="mandelbrot-generator"    >Mandelbrot Generator</@com.sample>
        </ul>
    </nav>
</#if>

<div class="w-full">
    <form action="/schematics/compile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas mlogWatcher="N" sourceTitle="Schemacode definition:" targetTitle="Encoded schematic:" sourceLines="Y" targetLines="N" />
            <div>
                <div class="flex-wrap items-center space-x-2">
                    <@com.optimizationSettings />
                    <input type="submit" value="Build" class="px-4 py-2 bg-gray-600 text-white my-2 rounded"/>
                    <@com.link href="/schematics/?s=clean">Start with a new schematic</@com.link>
                </div>
                <@com.footerLinks mindcode="N" />
            </div>
            <@com.compilerMessages messageTitle="Build messages:" />
        </div>
    </form>
</div>

<@com.footer/>
