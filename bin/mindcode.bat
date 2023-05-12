@echo off
echo This tool is deprecated. Use mcc.bat instead. 1>&2
setlocal enableDelayedExpansion
pushd %~dp0
set __CLPATH=.
for /R ..\mindcode\target %%i in (*.jar) do set __CLPATH=!__CLPATH!;%%i
popd
"%JAVA_HOME%\bin\java.exe" --enable-preview -classpath !__CLPATH! info.teksol.mindcode.compiler.CompileMain %*
endlocal
