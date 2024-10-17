<#-- @ftlvariable name="model" type="info.teksol.mindcode.webapp.HomePageData" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.navigation selection="mlog-decompiler"/>

<p class="my-2 text-lg">Here you can partially decompile an mlog code into Mindcode. <strong>The resulting code cannot
        be directly compiled by Mindcode.</strong> Jump instructions in the original mlog are transcribed as <strong>if</strong>
        and <strong>goto</strong> statements which aren't supported by Mindcode and must be rewritten using conditional
        statements, loops and other high-level constructs. The decompiler is mainly useful to produce expressions and
        function calls in the correct Mindcode syntax, saving some time and possibly helping to avoid some mistakes
        compared to a manual rewrite of the entire mlog code from scratch.
</p>

<div class="w-full">
    <form action="/mlog-decompiler/decompile-mlog" method="post" class="mx-2" autocomplete="off">
        <#if model.id??>
            <input type="hidden" name="id" value="${model.id}">
        </#if>
        <div class="grid grid-cols-2 gap-2">
            <@com.codeareas mlogWatcher="N" sourceTitle="Mlog code:" targetTitle="Decompiled Mindcode:" sourceLines="N" targetLines="Y" />
            <div>
                <div class="flex-wrap items-center space-x-2">
                    <input type="submit" value="Decompile" class="px-4 py-2 bg-gray-600 text-white my-2 rounded "/>
                    <@com.link href="/mlog-decompiler">Erase mlog</@com.link>
                </div>
                <@com.footerLinks mindcode="Y" />
            </div>
            <@com.compilerMessages messageTitle="Decompiler messages:" />
        </div>
    </form>
</div>

<@com.footer/>
