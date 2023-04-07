@echo off
setlocal enableDelayedExpansion
pushd %~dp0
set __CLPATH=.
for /R ..\compiler\target %%i in (*.jar) do set __CLPATH=!__CLPATH!;%%i
popd
"%JAVA_HOME%\bin\java.exe" -classpath !__CLPATH! info.teksol.mindcode.compiler.CompileMain %*
endlocal
