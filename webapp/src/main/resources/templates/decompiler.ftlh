<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="decompiler"/>

<p class="my-2 text-lg">Here you can decompile schematics copied from Mindustry into Schemacode, a schema definition language,
    which can then be modified and compiled back into a schema again. Press the <strong>Export</strong> button on a Mindustry schematic,
    choose <strong>Copy to clipboard</strong> and paste the text into the left pane. Then press <strong>Decompile</strong>.
    You can compile the Schematic definition back to Mindustry schematics on the <a href="/schematics">Schematics Builder</a> page.</p>
<p class="my-2 text-lg">If your schematic contains processor(s), the code is decompiled into mlog.</p>

<div class="w-full">
    <form action="/decompiler/decompile" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas sourceTitle="Encoded schematic:" targetTitle="Decompiled schemacode:" sourceLines="N" targetLines="Y" />
            <div>
                <div class="flex items-center space-x-3">
                    <input type="submit" value="Decompile" class="px-4 py-2 bg-gray-600 text-white my-4 rounded "/>
                    <@com.link href="/decomnpiler/?s=clean">Erase schematic</@com.link>
                </div>
                <@com.footerLinks mindcode="N" />
            </div>
            <@com.compilerMessages messageTitle="Decompiler messages:" />
        </div>
    </form>
</div>

<@com.footer/>
