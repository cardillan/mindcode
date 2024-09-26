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
            <@com.sample name="detector"                >Property Detector</@com.sample>
            <@com.sample name="healing-center"          >Healing Center</@com.sample>
            <@com.sample name="on-off-switch"           >On/off Switch</@com.sample>
            <@com.sample name="regulator"               >Regulator</@com.sample>
            <@com.sample name="overdrive-dome-supply"   >Supplying Overdrive Dome with Units</@com.sample>
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
            <@com.codeareas sourceTitle="Schemacode definition:" targetTitle="Encoded schematics:" sourceLines="Y" targetLines="N" />
            <div>
                <div class="flex items-center space-x-3">
                    <@com.optimizationSettings />
                    <input type="submit" value="Build" class="px-4 py-2 bg-gray-600 text-white my-4 rounded"/>
                    <@com.link href="/schematics/?s=clean">Start with a new schematics</@com.link>
                </div>
                <@com.footerLinks mindcode="N" />
            </div>
            <@com.compilerMessages messageTitle="Build messages:" />
        </div>
    </form>
</div>

<@com.footer/>
