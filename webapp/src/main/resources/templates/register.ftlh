<#-- @ftlvariable name="model" type="java.util.Map<String, String>" -->
<#import "common.ftlh" as com>
<@com.header/>
<@com.h1>
    Register
</@com.h1>

<form method="post" action="/register" class="">
    <label for="username" class="block font-bold">Username</label>
    <input type="text" id="username" name="username" value="${model.username}" class="border bg-gray-100 border-gray-700 p-1"/>

    <label for="password" class="block font-bold">Password</label>
    <input type="password" id="password" name="password" class="border bg-gray-100 border-gray-700 p-1"/>
    <p>10+ characters</p>

    <div>
        <@com.submit>Register</@com.submit>
    </div>
</form>

<@com.footer/>
